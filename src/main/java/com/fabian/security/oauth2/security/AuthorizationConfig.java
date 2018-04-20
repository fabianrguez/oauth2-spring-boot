package com.fabian.security.oauth2.security;

import com.fabian.security.oauth2.models.Role;
import com.fabian.security.oauth2.services.AccountService;
import com.fabian.security.oauth2.services.TokenBlackListService;
import com.fabian.security.oauth2.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

@Configuration
@EnableAuthorizationServer
public class AuthorizationConfig extends AuthorizationServerConfigurerAdapter {

	@Value("${security.oauth2.client.access-token-validity-seconds}")
	private int ACCESS_TOKEN_VALIDITY_SECONDS;

	@Value("${security.oauth2.client.refresh-token-validity-seconds}")
	private int REFRESH_TOKEN_VALIDITY_SECONDS;

	@Value("${security.oauth2.resource.id}")
	private String RESOURCE_ID;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private TokenBlackListService tokenBlackListService;

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) {
		security
				.tokenKeyAccess("hasAuthority('ROLE_TRUSTED_CLIENT')")
				.checkTokenAccess("hasAuthority('ROLE_TRUSTED_CLIENT')");
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		System.out.println(ACCESS_TOKEN_VALIDITY_SECONDS);
		clients
				.inMemory()
					.withClient("trusted-app")
						.authorizedGrantTypes("client_credentials", "password", "refresh_token")
						.authorities(Role.ROLE_TRUSTED_CLIENT.toString())
						.scopes("read", "write")
						.resourceIds(RESOURCE_ID)
						.accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS)
						.refreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY_SECONDS)
						.secret("{noop}secret")
				.and()
					.withClient("register-app")
						.authorizedGrantTypes("client_credentials")
						.authorities(Role.ROLE_REGISTER.toString())
						.scopes("register")
						.accessTokenValiditySeconds(10)
						.refreshTokenValiditySeconds(10)
						.resourceIds(RESOURCE_ID)
						.secret("secret");
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		endpoints
				.authenticationManager(this.authenticationManager)
				.tokenServices(tokenServices())
				.tokenStore(tokenStore())
				.accessTokenConverter(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(
				new ClassPathResource("mykeys.jks"), "mypass".toCharArray());
		converter.setKeyPair(keyStoreKeyFactory.getKeyPair("mykeys"));
		return converter;
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	@Primary
	public DefaultTokenServices tokenServices() {
		TokenService tokenService = new TokenService(tokenBlackListService);
		tokenService.setTokenStore(tokenStore());
		tokenService.setSupportRefreshToken(true);
		tokenService.setTokenEnhancer(accessTokenConverter());
		return tokenService;
	}

	@Bean
	public UserDetailsService userDetailsService() {
		return new AccountService();
	}

}
