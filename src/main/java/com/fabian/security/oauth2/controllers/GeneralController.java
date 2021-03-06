package com.fabian.security.oauth2.controllers;

import com.fabian.security.oauth2.models.Account;
import com.fabian.security.oauth2.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
public class GeneralController {

	@Autowired
	private AccountService accountService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/getUser")
	public void user() {
		List<Account> accounts = accountService.findAll();
		System.out.println(accounts.get(0));
	}

	@GetMapping("/")
	public RestMessage hello() {
		return new RestMessage("Hello World!");
	}

	@GetMapping("/api/hello")
	public RestMessage helloUser() {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return new RestMessage("Hello " + username);
	}

	@GetMapping("/api/test")
	public RestMessage apiTest() {
		return new RestMessage("Hello ApiTest!");
	}

	@GetMapping("/api/admin")
	public RestMessage helloAdmin(Principal principal) {
		return new RestMessage("Welcome " + principal.getName());
	}

	public static class RestMessage {
		private String message;

		public RestMessage(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}
}
