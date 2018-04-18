package com.fabian.security.oauth2.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

@Configuration
@EnableResourceServer
public class ResourceConfig extends ResourceServerConfigurerAdapter {

	@Value("${security.oauth2.resource.id}")
	private static String RESOURCE_ID;

	@Autowired
	private DefaultTokenServices tokenServices;

	@Autowired
	private TokenStore tokenStore;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources
				.resourceId(RESOURCE_ID)
				.tokenServices(tokenServices)
				.tokenStore(tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
			.requestMatcher(new OAuthRequestedMatcher())
			.csrf().disable()
			.anonymous().disable()
			.authorizeRequests()
			.antMatchers(HttpMethod.OPTIONS).permitAll()
			.antMatchers("/api/hello").access("hasAnyRole('USER')")
			.antMatchers("/api/admin").hasRole("ADMIN")
			.antMatchers("/api/**").authenticated();
	}

	private static class OAuthRequestedMatcher implements RequestMatcher {
		public boolean matches(HttpServletRequest request) {
			// Determine if the resource called is "/api/**"
			String path = request.getServletPath();
			if ( path.length() >= 5 ) {
				path = path.substring(0, 5);
				return path.equals("/api/");
			} else return false;
		}
	}
}
