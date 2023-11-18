package com.api.tests.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import static com.api.tests.clients.BaseApiClient.objectToJsonString;

@Getter
@Builder
@Jacksonized
public final class CreatePlayerResponseDto {

    private final String id;

    private final String login;

    private final String password;

    private final String screenName;

    private final String gender;

    private final int age;

    private final String role;

    @Override
    public String toString() {
        return objectToJsonString(this);
    }
}
