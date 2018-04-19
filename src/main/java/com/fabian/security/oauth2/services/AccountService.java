package com.fabian.security.oauth2.services;

import com.fabian.security.oauth2.models.Account;
import com.fabian.security.oauth2.models.Role;
import com.fabian.security.oauth2.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService implements UserDetailsService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Account> account = accountRepository.findByUsername(username);
		if ( account.isPresent() ) {
			return account.get();
		} else {
			throw new UsernameNotFoundException(String.format("Username[%s] not found", username));
		}
	}

	public Account findAccountByUsername(String username) throws UsernameNotFoundException {
		Optional<Account> account = accountRepository.findByUsername(username);
		if ( account.isPresent() ) {
			return account.get();
		} else {
			throw new UsernameNotFoundException(String.format("Username[%s] not found", username));
		}
	}

	public List<Account> findAll() {
		return accountRepository.findAll();
	}

	public Account registerUser(Account account) {
		account.setPassword(passwordEncoder.encode(account.getPassword()));
		account.grantAuthority(Role.ROLE_USER);
		return accountRepository.save( account );
	}

	public void removeAuthenticatedAccount() throws UsernameNotFoundException {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Account acct = findAccountByUsername(username);
		accountRepository.deleteAccountById(acct.getId());

	}
}
