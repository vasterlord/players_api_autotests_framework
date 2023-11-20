package com.api.tests.dto;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.api.tests.utils.CollectionUtils.getRandomEnum;
import static com.api.tests.utils.CollectionUtils.getRandomListElement;

@Data
@Builder
@EqualsAndHashCode
@Accessors(chain = true)
public final class CreatePlayerRequestDto {

    private Integer age;

    private String gender;

    private String login;

    private String password;

    private String role;

    private String screenName;

    public Map<String, Object> buildPlayerRequestParamsMap() {
        final Map<String, Object> createPlayerRequestParamsMap = new HashMap<>();

        Optional.ofNullable(this.age).ifPresent(age -> createPlayerRequestParamsMap.put("age", age));
        Optional.ofNullable(this.gender).ifPresent(gender -> createPlayerRequestParamsMap.put("gender", gender));
        Optional.ofNullable(this.login).ifPresent(login -> createPlayerRequestParamsMap.put("login", login));
        Optional.ofNullable(this.password).ifPresent(password -> createPlayerRequestParamsMap.put("password",
                password));
        Optional.ofNullable(this.role).ifPresent(role -> createPlayerRequestParamsMap.put("role", role));
        Optional.ofNullable(this.screenName).ifPresent(screenName -> createPlayerRequestParamsMap.put("screenName",
                screenName));

        return createPlayerRequestParamsMap;
    }

    public static CreatePlayerRequestDto buildValidRandomCreatePlayerRequestDto() {
        final var faker = Faker.instance();
        final var playerRole = getRandomListElement(List.of(Role.USER, Role.ADMIN)).getRoleName();
        return CreatePlayerRequestDto.builder()
                .age(faker.number().numberBetween(16, 60))
                .gender(getRandomEnum(Gender.class).getGenderName())
                .role(getRandomListElement(List.of(Role.USER, Role.ADMIN)).getRoleName())
                .login(faker.name().firstName() + playerRole.toUpperCase())
                .password(faker.internet().password(7, 15, true))
                .screenName(faker.name().lastName() + "_" + playerRole.toUpperCase())
                .build();
    }

    @Override
    public String toString() {
        return buildPlayerRequestParamsMap().toString();
    }

}
