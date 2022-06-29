package com.ssolpark.security.service.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ssolpark.security.common.ApiResponse;
import com.ssolpark.security.common.ResponseType;
import com.ssolpark.security.common.DataApiResponse;
import com.ssolpark.security.constant.GrantType;
import com.ssolpark.security.dto.RegMemberDto;
import com.ssolpark.security.dto.auth.ReissueTokenRequest;
import com.ssolpark.security.dto.auth.JwtRequest;
import com.ssolpark.security.dto.auth.JwtResponse;
import com.ssolpark.security.exception.BusinessException;
import com.ssolpark.security.model.Member;
import com.ssolpark.security.model.MemberRefreshToken;
import com.ssolpark.security.repository.MemberRefreshTokenRepository;
import com.ssolpark.security.repository.MemberRepository;
import com.ssolpark.security.security.AuthenticationFilter;
import com.ssolpark.security.security.JwtProvider;
import com.ssolpark.security.service.AuthenticationService;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${kakao.oauth.redirect-url}")
    private String KAKAO_OAUTH_TOKEN;

    @Value("${kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    @Value("${kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${kakao.grant-type}")
    private String KAKAO_GRANT_TYPE;

    @Value("${refreshToken.duration}")
    private long REFRESH_TOKEN_VALID_TIME;

    private final MemberRepository memberRepo;

    private final MemberRefreshTokenRepository memberRefreshTokenRepo;

    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;

    public AuthenticationServiceImpl(MemberRepository memberRepo, MemberRefreshTokenRepository memberRefreshTokenRepo, JwtProvider jwtProvider, PasswordEncoder passwordEncoder) {
        this.memberRepo = memberRepo;
        this.memberRefreshTokenRepo = memberRefreshTokenRepo;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public ApiResponse registration(RegMemberDto regMemberDto) {

        Member member = findByEmail(regMemberDto.getEmail());

        if(member != null) {
            return new ApiResponse(ResponseType.REGISTERED_MEMBER.getCode(), ResponseType.REGISTERED_MEMBER.getMessage());
        }

        Member saveMember = Member.builder()
                .email(regMemberDto.getEmail())
                .password(passwordEncoder.encode(regMemberDto.getPassword()))
                .name(regMemberDto.getName())
                .build();

        memberRepo.save(saveMember);

        return new ApiResponse(ResponseType.SUCCESS.getCode(), ResponseType.SUCCESS.getMessage());
    }

    @Override
    public DataApiResponse authenticateForJwt(JwtRequest jwtRequest) {

        Member member;

        if(jwtRequest.getGrantType().equals(GrantType.CLIENT_CREDENTIALS)) {
            member = authEmailAndPassword(jwtRequest).orElseThrow(() -> new BusinessException(ResponseType.WRONG_EMAIL_OR_PASSWORD));

        }else {
            throw new BusinessException(ResponseType.GRANT_TYPE_NOT_FOUND);
        }

        JwtResponse jwtResponse = processJwt(member);

        return new DataApiResponse(jwtResponse);
    }

    private JwtResponse processJwt(Member member) {

        final String email = member.getEmail();

        JwtResponse jwtResponse = jwtProvider.generateToken(email);

        MemberRefreshToken refToken = memberRefreshTokenRepo.findById(member.getMemberId()).orElse(null);

        if(refToken != null && refToken.getExpiredOn().after(new Date())) {

            log.info("::: Found a valid Refresh Token, Email : {} :::", email);
            jwtResponse.setRefreshToken(refToken.getRefreshToken());

        } else {

            log.info("::: Start creating a New Refresh Token, Email : {} :::", email);

            jwtResponse.setRefreshToken(UUID.randomUUID().toString().replace("-","").toLowerCase());

            Date expiredDate = new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALID_TIME * 1000);

            refToken.updateRefreshTokenAndExpiredOn(jwtResponse.getRefreshToken(), expiredDate);

            memberRefreshTokenRepo.save(refToken);

            log.info("::: End creating a New Refresh Token, ExpiredDate : {}, Email : {} :::", expiredDate, email);

        }

        return jwtResponse;
    }

    @Override
    public DataApiResponse reIssueAccessToken(ReissueTokenRequest tokenRequest) {

        EmailAndRefreshTokenDto emailRfrTokenDto = validateRefreshToken(tokenRequest);

        String expiredAccessToken = tokenRequest.getAccessToken();

        if(StringUtils.hasText(expiredAccessToken) && expiredAccessToken.startsWith(AuthenticationFilter.AUTHORIZATION_TYPE)) {

            String expiredJwtToken = expiredAccessToken.substring(7);

            if(!jwtProvider.isExpiredToken(expiredJwtToken)) {
                throw new BusinessException(ResponseType.TOKEN_CANNOT_BE_ISSUED);
            }

            JwtResponse jwtResponse = jwtProvider.generateToken(emailRfrTokenDto.getEmail());
            jwtResponse.setRefreshToken(emailRfrTokenDto.getRefreshToken());

            return new DataApiResponse(jwtResponse);
        }

        log.error("### Failed to issue new token ###");

        throw new BusinessException(ResponseType.BAD_REQUEST);
    }

    @Override
    public DataApiResponse getKakaoAccessToken(String code) {

        String accessToken = "", refreshToken = "";

        try {
            URL url = new URL(KAKAO_OAUTH_TOKEN);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            /*
            * setDoOutput() : HttpURLConnection의 출력 스트림을 사용할지의 여부 설정
            * POST 방식은 스트림 기반의 데이터 전송 방식이기 setDoOutput(true) 설정
            * */
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuffer sb = new StringBuffer();

            sb.append(KAKAO_GRANT_TYPE);
            sb.append(String.format("&client_id=%s",KAKAO_CLIENT_ID));
            sb.append(String.format("&redirect_urdi=%s",KAKAO_REDIRECT_URI));
            sb.append(String.format("&code=%s",code));

            bw.write(sb.toString());
            bw.flush();

            int responseCode = conn.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK) {

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line = "", result = "";

                while ((line = br.readLine()) != null) {
                    result += line;
                }

                JsonElement element = JsonParser.parseString(result);

                accessToken = element.getAsJsonObject().get("access_token").getAsString();
                refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();

                log.info("::: Kakao login success, AccessToken : {}, RefreshToken : {} :::", accessToken, refreshToken);

                br.close();
                bw.close();

            } else {
                // todo 실패시
                log.info("::: Kakao login response code {} :::", responseCode);
            }

        } catch (MalformedURLException e) {

            e.printStackTrace();
        } catch (IOException e) {

            e.printStackTrace();
        }

        return new DataApiResponse(accessToken);
    }

    private EmailAndRefreshTokenDto validateRefreshToken(ReissueTokenRequest tokenRequest) {

        if(!StringUtils.hasText(tokenRequest.getRefreshToken())) {
            log.error(" ### Refresh Token is empty ###");

            throw new BusinessException(ResponseType.BAD_REQUEST);
        }

        Member member = memberRepo.findByEmail(tokenRequest.getEmail()).orElseThrow(() -> new BusinessException(ResponseType.MEMBER_NOT_FOUND));

        MemberRefreshToken memberRefreshToken = memberRefreshTokenRepo.findById(member.getMemberId()).orElseThrow(() -> new BusinessException(ResponseType.REFRESH_TOKEN_EXPIRED));

        if(!memberRefreshToken.getRefreshToken().equals(tokenRequest.getRefreshToken())) {
            log.error(" ### Refresh Token does not match ###");

            throw new BusinessException(ResponseType.BAD_REQUEST);
        }

        if(memberRefreshToken.getExpiredOn().before(new Date())) {
            throw new BusinessException(ResponseType.REFRESH_TOKEN_EXPIRED);
        }

        return EmailAndRefreshTokenDto.builder().email(member.getEmail()).refreshToken(memberRefreshToken.getRefreshToken()).build();
    }

    private Optional<Member> authEmailAndPassword(JwtRequest jwtRequest) {

        Member member = findByEmail(jwtRequest.getEmail());

        if(member != null) {
            return passwordEncoder.matches(jwtRequest.getPassword(), member.getPassword()) ? Optional.of(member) : Optional.empty();
        }

        return Optional.empty();
    }

    private Member findByEmail(String email) {
        return memberRepo.findByEmail(email).orElse(null);
    }

    @Getter
    public static class EmailAndRefreshTokenDto {

        private String email;

        private String refreshToken;

        @Builder
        public EmailAndRefreshTokenDto(String email, String refreshToken) {
            this.email = email;
            this.refreshToken = refreshToken;
        }
    }

}
