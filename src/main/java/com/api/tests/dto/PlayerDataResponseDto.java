package com.api.tests.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import static com.api.tests.clients.BaseApiClient.objectToJsonString;

/*
    PlayerDataResponseDto.class can be used for create, get by id, update player endpoints.
 */
@Getter
@Builder
@Jacksonized
@EqualsAndHashCode
public final class PlayerDataResponseDto {

    private final int id;

    private final String login;

    private final String password;

    private final String screenName;

    private final String gender;

    private final Integer age;

    private final String role;

    public static PlayerDataResponseDto buildExpectedPlayerDataResponseDto(
            final int id,
            final CreateUpdatePlayerRequestDto createPlayerRequestDto) {
        return PlayerDataResponseDto.builder()
                .id(id)
                .login(createPlayerRequestDto.getLogin())
                .password(createPlayerRequestDto.getPassword())
                .screenName(createPlayerRequestDto.getScreenName())
                .gender(createPlayerRequestDto.getGender())
                .age(createPlayerRequestDto.getAge())
                .role(createPlayerRequestDto.getRole())
                .build();
    }

    @Override
    public String toString() {
        return objectToJsonString(this);
    }
}
