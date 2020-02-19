package com.example.springboot;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
public class CryptoController {

	@RequestMapping("/")
	public String index() {
		return "Server Up";
	}

	@RequestMapping("/hash")
	public String hash() {
		return "Hash Called";
	}

	@RequestMapping("/encrypt")
	public String encrypt() {
		return "Encryption Called";
	}

	@RequestMapping("/login")
	public String login() {
		return "Login Called";
	}

	@RequestMapping("/register")
	public String register() {
		return "Register Called";
	}

	@RequestMapping("/genKeys")
	public String generateKeys() {
		return "GenerateKeys Called";
	}

	@RequestMapping("/getKeys")
	public String getKeys() {
		return "GetKeys Called";
	}

	@RequestMapping("/decrypt")
	public String decrypt() {
		return "Decrypt Called";
	}

	@RequestMapping("/sign")
	public String sign() {
		return "Sign Called";
	}

}
