package com.fabian.security.oauth2.services;

import com.fabian.security.oauth2.exceptions.TokenNotFoundException;
import com.fabian.security.oauth2.models.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

public class TokenService extends DefaultTokenServices {

	private static final Logger log = LoggerFactory.getLogger(TokenService.class);

	private TokenBlackListService tokenBlackListService;

	public TokenService(TokenBlackListService tokenBlackListService) {
		this.tokenBlackListService = tokenBlackListService;
	}

	@Override
	public OAuth2AccessToken readAccessToken(String accessToken) {
		return super.readAccessToken(accessToken);
	}

	@Override
	public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
		OAuth2AccessToken token = super.createAccessToken(authentication);
		Account account = (Account) authentication.getPrincipal();
		String jti = (String) token.getAdditionalInformation().get("jti");

		tokenBlackListService.addToEnabledList(account.getId(), jti, token.getExpiration().getTime());
		return token;
	}

	@Override
	public OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest) throws AuthenticationException {
		log.info("refresh token:", refreshTokenValue);
		String jti = tokenRequest.getRequestParameters().get("jti");
		try {
			if (jti != null) {
				if(tokenBlackListService.isBlackListed(jti)) {
					return null;
				}
				OAuth2AccessToken token = super.refreshAccessToken(refreshTokenValue, tokenRequest);
				tokenBlackListService.addToBlackList(jti);
				return token;
			}
		} catch (TokenNotFoundException e) {
			log.error("TokenNotFound", e);
			return null;
		}
		return super.refreshAccessToken(refreshTokenValue, tokenRequest);
	}
}