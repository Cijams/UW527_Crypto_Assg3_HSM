package com.example.springboot;

import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RestController
public class CryptoController {

	@RequestMapping("/")
	public String index() {
		return "Server Up";
	}

	@CrossOrigin
	@GetMapping("/hash")
	public ResponseEntity<String> hash() {

		return new ResponseEntity<>("You Called Hash", HttpStatus.OK);
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
