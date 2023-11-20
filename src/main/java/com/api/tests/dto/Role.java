package com.api.tests.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    SUPERVISOR("supervisor"),
    ADMIN("admin"),
    USER("user");


    private final String roleName;
}
