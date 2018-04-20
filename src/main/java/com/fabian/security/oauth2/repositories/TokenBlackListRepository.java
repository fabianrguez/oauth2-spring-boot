package com.fabian.security.oauth2.repositories;

import com.fabian.security.oauth2.models.TokenBlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenBlackListRepository extends JpaRepository<TokenBlackList, String> {
	Optional<TokenBlackList> findByJti(String jti);
	List<TokenBlackList> findAllByUserId(Long userId);
	List<TokenBlackList> deleteAllByUserIdAndExpiresBefore(Long userId, Long date);
}
