package com.fabian.security.oauth2.exceptions;

public class TokenNotFoundException extends Exception {

	private String message;

	public TokenNotFoundException(String jti) {
		super();
		message = String.format("Token with jti[%s] not found.", jti);
	}
}
