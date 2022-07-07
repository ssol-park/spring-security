package com.ssolpark.security.service.impl;

import com.ssolpark.security.common.ResponseType;
import com.ssolpark.security.common.DataApiResponse;
import com.ssolpark.security.constant.GrantType;
import com.ssolpark.security.dto.RegMemberDto;
import com.ssolpark.security.dto.SnsUserInfoDto;
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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String AUTHORIZATION_TYPE = "Bearer ";

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
    public Member registration(RegMemberDto regMemberDto) {

        Member member = findByEmail(regMemberDto.getEmail());

        if(member != null) {
            throw new BusinessException(ResponseType.REGISTERED_MEMBER);
        }

        String password = regMemberDto.getKakaoId() != null ? null : passwordEncoder.encode(regMemberDto.getPassword());

        Member saveMember = Member.builder()
                .email(regMemberDto.getEmail())
                .password(password)
                .name(regMemberDto.getName())
                .kakaoId(regMemberDto.getKakaoId())
                .build();

        memberRepo.save(saveMember);

        return saveMember;
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

    @Transactional
    public JwtResponse processJwt(Member member) {

        final String email = member.getEmail();

        JwtResponse jwtResponse = jwtProvider.generateToken(email);

        MemberRefreshToken refToken = memberRefreshTokenRepo.findById(member.getMemberId()).orElse(null);

        if(refToken != null) {

            if(refToken.getExpiredOn().after(new Date())) {
                log.info("::: Found a valid Refresh Token, Email : {} :::", email);
                jwtResponse.setRefreshToken(refToken.getRefreshToken());

            }else {

                jwtResponse.setRefreshToken(UUID.randomUUID().toString().replace("-","").toLowerCase());

                Date expiredDate = new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALID_TIME * 1000);

                refToken.updateRefreshTokenAndExpiredOn(jwtResponse.getRefreshToken(), expiredDate);

                memberRefreshTokenRepo.save(refToken);

                log.info("::: Updated a Refresh Token, ExpiredDate : {}, Email : {} :::", expiredDate, email);
            }

        } else {

            jwtResponse.setRefreshToken(UUID.randomUUID().toString().replace("-","").toLowerCase());

            Date expiredDate = new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALID_TIME * 1000);

            refToken = MemberRefreshToken.builder()
                    .member(member)
                    .refreshToken(jwtResponse.getRefreshToken())
                    .expiredOn(expiredDate)
                    .build();

            refToken.updateRefreshTokenAndExpiredOn(jwtResponse.getRefreshToken(), expiredDate);

            memberRefreshTokenRepo.save(refToken);

            log.info("::: Created a Refresh Token, ExpiredDate : {}, Email : {} :::", expiredDate, email);
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

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_CLIENT_ID);
        params.add("redirect_uri", KAKAO_REDIRECT_URI);
        params.add("code", code);

        RestTemplate template = new RestTemplate();

        HttpEntity<MultiValueMap<String, String>> accessTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = template.exchange(
                KAKAO_OAUTH_TOKEN,
                HttpMethod.POST,
                accessTokenRequest,
                String.class
        );

        if(response.getStatusCodeValue() == HttpStatus.OK.value()) {

            String tokenJson = response.getBody();
            String accessToken = null;

            try {

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(tokenJson);

                accessToken = jsonObject.get("access_token").toString();

            } catch (ParseException e) {
                log.error(" ### Parse Error : {} ###", e.getMessage());
            }

            SnsUserInfoDto userInfo = getKakaoUserInfoByToken(accessToken);

            Member member = findByEmailAndKakaoId(userInfo.getEmail(), userInfo.getId());

            // registration
            if(member == null) {

                RegMemberDto regMember = RegMemberDto.builder()
                        .kakaoId(userInfo.getId())
                        .email(userInfo.getEmail())
                        .build();

                Member oAuthMember = registration(regMember);

                JwtResponse jwtResponse = processJwt(oAuthMember);

                return new DataApiResponse(jwtResponse);
            }

            // login
            JwtResponse jwtResponse = processJwt(member);

            return new DataApiResponse(jwtResponse);
        }

        throw new BusinessException(ResponseType.KAKAO_LOGIN_FAILED);
    }

    private SnsUserInfoDto getKakaoUserInfoByToken(String accessToken) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION_HEADER, String.format("%s%s", AUTHORIZATION_TYPE, accessToken));
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        RestTemplate template = new RestTemplate();

        HttpEntity<MultiValueMap<String, String>> userInfoRequest = new HttpEntity<>(headers);

        ResponseEntity<String> response = template.exchange(
        "https://kapi.kakao.com/v2/user/me",
              HttpMethod.POST,
              userInfoRequest,
              String.class
        );

        if(response.getStatusCodeValue() == HttpStatus.OK.value()) {

            try {

                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(response.getBody());
                JSONObject kakaoAccount = (JSONObject) jsonObject.get("kakao_account");

                return SnsUserInfoDto.builder()
                        .id(Long.parseLong(jsonObject.get("id").toString()))
                        .email(kakaoAccount.get("email").toString())
                        .build();

            } catch (ParseException e) {
                log.error(" ### Parse Error : {} ###", e.getMessage());
            }

        }

        // todo 타입 보완
        throw new BusinessException(ResponseType.KAKAO_LOGIN_FAILED);
    }

    @Transactional
    public EmailAndRefreshTokenDto validateRefreshToken(ReissueTokenRequest tokenRequest) {

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

    @Transactional
    public Member findByEmail(String email) {
        return memberRepo.findByEmail(email).orElse(null);
    }

    @Transactional
    public Member findByEmailAndKakaoId(String email, long id) {
        return memberRepo.findByEmailAndKakaoId(email, id).orElse(null);
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
