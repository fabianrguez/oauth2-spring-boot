package com.fabian.security.oauth2.models;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TokenBlackList {

	@Id
	private String jti;

	private Long userId;

	private Long expires;

	private boolean isBlackListed;

	public TokenBlackList() {
	}

	public TokenBlackList(String jti, Long userId, Long expires) {
		this.jti = jti;
		this.userId = userId;
		this.expires = expires;
	}

	public String getJti() {
		return jti;
	}

	public void setJti(String jti) {
		this.jti = jti;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getExpires() {
		return expires;
	}

	public void setExpires(Long expires) {
		this.expires = expires;
	}

	public boolean isBlackListed() {
		return isBlackListed;
	}

	public void setBlackListed(boolean isBlackListed) {
		this.isBlackListed = isBlackListed;
	}

	@Override
	public String toString() {
		return "TokenBlackList{" +
				"jti='" + jti + '\'' +
				", userId=" + userId +
				", expires=" + expires +
				", isBlackListed=" + isBlackListed +
				'}';
	}
}
