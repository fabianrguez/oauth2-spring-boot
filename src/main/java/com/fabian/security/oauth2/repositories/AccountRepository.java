package com.fabian.security.oauth2.repositories;

import com.fabian.security.oauth2.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	Optional<Account> findByUsername(String username);
	void deleteAccountById(Long id);
}
