package com.myapp.userservice.service;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.myapp.userservice.dto.Dsource;
import com.myapp.userservice.dto.TenantDto;
import com.myapp.userservice.multitenancy.MultiTenancyJpaConfiguration;
import com.myapp.userservice.multitenancy.TenantContextHolder;
import com.zaxxer.hikari.HikariDataSource;

@Service
public class TenantServiceImpl implements TenantService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private Map<String, DataSource> dataSourcesMtApp;

	@Value("${tenant.service.api.get}")
	private String tenantGetApi;

	@Value("${tenant.service.api.create}")
	private String tenantPostApi;

	@Value("${com.database.url}")
	private String databaseUrl;

	@Value("${com.database.user}")
	private String databaseUser;

	@Value("${com.database.password}")
	private String databasePassword;

	@Value("${com.database.driver}")
	private String databaseDriver;

	@Override
	public String register(TenantDto tenantDto) {
		boolean flag = false;
		HttpHeaders headers = new HttpHeaders();
		ResponseEntity<Dsource[]> response = restTemplate.exchange(tenantGetApi, HttpMethod.GET,
				new HttpEntity<Object>(headers), Dsource[].class);

		if (response.getBody() != null) {
			Dsource[] dsList = response.getBody();
			if (dsList != null && dsList.length > 0) {
				for (Dsource d : dsList) {
					if (d.getTenantId().equalsIgnoreCase(tenantDto.getInstanceName())) {
						flag = true;
					}
				}
			}
		}

		if (flag) {
			return "tenant already exists!";
		}
		try {
			restTemplate.postForEntity(tenantPostApi, tenantDto, String.class);
		} catch (Exception e) {

		}

		DataSourceBuilder<?> factory = DataSourceBuilder.create(MultiTenancyJpaConfiguration.class.getClassLoader())
				.url(databaseUrl + tenantDto.getInstanceName())
				.username(databaseUser)
				.password(databasePassword).driverClassName(databaseDriver);
		HikariDataSource ds = (HikariDataSource) factory.build();
		ds.setKeepaliveTime(40000);
		ds.setMinimumIdle(1);
		ds.setMaxLifetime(45000);
		ds.setIdleTimeout(35000);
		dataSourcesMtApp.put(tenantDto.getInstanceName(), ds);
		TenantContextHolder.setTenantId(tenantDto.getInstanceName());
		return "tenant registered successfully!";
	}

}
