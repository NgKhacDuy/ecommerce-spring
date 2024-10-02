package com.app.controllers;

import java.util.Collections;
import java.util.Map;

import com.app.config.ApiResponse;
import com.app.payloads.RefreshTokenDto;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.exceptions.UserNotFoundException;
import com.app.payloads.LoginCredentials;
import com.app.payloads.UserDTO;
import com.app.security.JWTUtil;
import com.app.services.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "E-Commerce Application")
public class AuthController {

	@Autowired
	private UserService userService;

	@Autowired
	private JWTUtil jwtUtil;

	@Autowired
	private ApiResponse apiResponse;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostMapping("/register")
	public ResponseEntity<Map<String, Object>> registerHandler(@Valid @RequestBody UserDTO user) throws UserNotFoundException {
		String encodedPass = passwordEncoder.encode(user.getPassword());

		user.setPassword(encodedPass);

		UserDTO userDTO = userService.registerUser(user);

		String token = jwtUtil.generateToken(userDTO.getEmail());

		return new ResponseEntity<Map<String, Object>>(Collections.singletonMap("jwt-token", token),
				HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<Object> loginHandler(@Valid @RequestBody LoginCredentials credentials) {

		UsernamePasswordAuthenticationToken authCredentials = new UsernamePasswordAuthenticationToken(
				credentials.getEmail(), credentials.getPassword());

		authenticationManager.authenticate(authCredentials);

		String token = jwtUtil.generateToken(credentials.getEmail());
		String refreshToken = jwtUtil.generateRefreshToken(credentials.getEmail());
		return new ResponseEntity<>(Map.of("access_token", token, "refresh-token", refreshToken), HttpStatus.OK);
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<Object> refreshTokenHandler(@RequestBody RefreshTokenDto refreshTokenDto) {
		String refreshToken = refreshTokenDto.getRefreshToken();
		try {
			String email = jwtUtil.validateTokenAndRetrieveSubject(refreshToken);
			String newAccessToken = jwtUtil.generateToken(email);
			return new ResponseEntity<>(Map.of("access_token", newAccessToken), HttpStatus.OK);
		} catch (JWTVerificationException e) {
			e.printStackTrace();
			apiResponse.setMessage("Invalid refresh token");
			return new ResponseEntity<>(apiResponse, HttpStatus.UNAUTHORIZED);
		} catch (Exception e) {
			e.printStackTrace();
			apiResponse.setMessage("Internal server error");
			return new ResponseEntity<>(apiResponse, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}