package com.myapp.userservice.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TenantDto {

    private String email;

    private String orgName;

    private String firstName;

    private String lastName;

    private String instanceName;

}