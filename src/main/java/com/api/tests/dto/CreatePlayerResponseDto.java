package com.api.tests.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import static com.api.tests.clients.BaseApiClient.objectToJsonString;

@Getter
@Builder
@Jacksonized
@EqualsAndHashCode
public final class CreatePlayerResponseDto {

    private final int id;

    private final String login;

    private final String password;

    private final String screenName;

    private final String gender;

    private final Integer age;

    private final String role;

    public static CreatePlayerResponseDto buildExpectedCreatePlayerResponseDto(
            final int id,
            final CreatePlayerRequestDto createPlayerRequestDto) {
        return CreatePlayerResponseDto.builder()
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
