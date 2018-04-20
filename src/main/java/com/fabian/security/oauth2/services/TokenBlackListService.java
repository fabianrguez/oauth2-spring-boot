package com.fabian.security.oauth2.services;

import com.fabian.security.oauth2.exceptions.TokenNotFoundException;
import com.fabian.security.oauth2.models.TokenBlackList;
import com.fabian.security.oauth2.repositories.TokenBlackListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TokenBlackListService {

	@Autowired
	private TokenBlackListRepository tokenBlackListRepository;

	public boolean isBlackListed(String jti) throws TokenNotFoundException {
		Optional<TokenBlackList> token = tokenBlackListRepository.findByJti(jti);
		if (token.isPresent()) {
			return token.get().isBlackListed();
		} else {
			throw new TokenNotFoundException(jti);
		}
	}

	@Async
	public void addToEnabledList(Long userId, String jti, Long expired) {
		List<TokenBlackList> tokenList = tokenBlackListRepository.findAllByUserId(userId);
		if (tokenList != null && tokenList.size() > 0) {
			tokenList.forEach(token -> {
				token.setBlackListed(true);
				tokenBlackListRepository.save(token);
			});
		}
		TokenBlackList tokenBlackList = new TokenBlackList(jti, userId, expired);
		tokenBlackList.setBlackListed(false);
		tokenBlackListRepository.save(tokenBlackList);
		tokenBlackListRepository.deleteAllByUserIdAndExpiresBefore(userId, new Date().getTime());
	}

	@Async
	public void addToBlackList(String jti ) throws TokenNotFoundException {
		Optional<TokenBlackList> tokenBlackList = tokenBlackListRepository.findByJti(jti);
		if ( tokenBlackList.isPresent() ) {
			tokenBlackList.get().setBlackListed(true);
			tokenBlackListRepository.save(tokenBlackList.get());
		} else throw new TokenNotFoundException(jti);
	}
}
