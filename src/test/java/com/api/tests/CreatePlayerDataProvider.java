package com.api.tests;

import com.api.tests.dto.Gender;
import com.github.javafaker.Faker;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.testng.annotations.DataProvider;

import static org.apache.hc.core5.http.HttpStatus.SC_SUCCESS;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreatePlayerDataProvider {

    @DataProvider(name = "creating_player_with_gander_range_data_provider")
    public static Object[][] getPlayerRequestGendersDataProvider() {
        return new Object[][]{
                {Gender.MALE.getGenderName(), SC_SUCCESS},
                {Gender.FEMALE.getGenderName(), SC_SUCCESS},
                {"invalidGander", SC_BAD_REQUEST}
        };
    }

    @DataProvider(name = "creating_player_with_passwords_range_data_provider")
    public static Object[][] getPlayerRequestPasswordsDataProvider() {
        final var faker = Faker.instance();
        return new Object[][]{
                {faker.internet().password(1, 6), SC_BAD_REQUEST},
                {faker.internet().password(7, 15), SC_SUCCESS},
                {faker.internet().password(16, 50), SC_BAD_REQUEST}
        };
    }

}
