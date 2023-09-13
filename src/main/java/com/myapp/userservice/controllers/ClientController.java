package com.myapp.userservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.myapp.userservice.dto.TenantDto;
import com.myapp.userservice.payload.response.MessageResponse;
import com.myapp.userservice.service.TenantService;

@RestController
@RequestMapping("/api/tenant")
public class ClientController {

	@Autowired
	private TenantService tenantService;

	@PostMapping("/register")
	public ResponseEntity<MessageResponse> register(@RequestBody TenantDto tenantDto) {
		return ResponseEntity.ok(new MessageResponse(tenantService.register(tenantDto)));
	}
}
