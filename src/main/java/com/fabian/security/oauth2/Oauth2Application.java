package com.fabian.security.oauth2;

import com.fabian.security.oauth2.models.Account;
import com.fabian.security.oauth2.models.Role;
import com.fabian.security.oauth2.services.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class Oauth2Application {

	public static void main(String[] args) {
		SpringApplication.run(Oauth2Application.class, args);
	}

//	@SuppressWarnings("deprecation")
//	@Bean
//	public static NoOpPasswordEncoder passwordEncoder() {
//		return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
//	}

	@Bean
	public CommandLineRunner init(AccountService accountService) {
		System.out.println("Insertando en DB");
		return (evt) -> Arrays.asList(
				"user,admin,john,robert,ana".split(",")).forEach(
				username -> {
					Account acct = new Account();
					acct.setUsername(username);
					acct.setPassword("password");
					acct.setFirstName(username);
					acct.setLastName("LastName");
					acct.setEmail(username + "@mail.com");
					acct.grantAuthority(Role.ROLE_USER);
					if (username.equals("admin"))
						acct.grantAuthority(Role.ROLE_ADMIN);
					accountService.registerUser(acct);
				}
		);
	}

}
