package com.api.tests;

import com.api.tests.clients.ResponseWrapper;
import com.api.tests.dto.CreatePlayerRequestDto;
import com.api.tests.dto.CreatePlayerResponseDto;
import com.api.tests.dto.Role;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.testng.Tag;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Test;

import static org.apache.hc.core5.http.HttpStatus.SC_SUCCESS;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.assertj.core.api.Assertions.assertThat;

@Feature("/player/create/{editor}")
public class CreatePlayerTest extends BasePlayersApiTest {

    @Test
    @Description("Verify that player is created successfully")
    public void verifySuccessfulPlayerCreating() {
        final CreatePlayerResponseDto createPlayerResponseDto = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR,
                        CreatePlayerRequestDto.buildValidRandomCreatePlayerRequestDto())
                .expectingStatusCode(SC_SUCCESS)
                .readEntity();
        assertThat(createPlayerResponseDto.getId())
                .as("Create player response should have non-null 'id' field: "
                        + createPlayerResponseDto)
                .isNotNull()
                .isNotNegative();
        createdPlayersList.add(createPlayerResponseDto.getId());
    }

    @Test
    @Tag("Defect")
    @Description("Verify that created players has all valid fields with from request data")
    public void verifyPlayerIsCreatedWithValidFieldsDataResponse() {
        final CreatePlayerRequestDto createPlayerRequestDto = CreatePlayerRequestDto
                .buildValidRandomCreatePlayerRequestDto();
        final CreatePlayerResponseDto actualCreatePlayerResponseDto = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR,
                        createPlayerRequestDto)
                .expectingStatusCode(SC_SUCCESS)
                .readEntity();
        createdPlayersList.add(actualCreatePlayerResponseDto.getId());

        final CreatePlayerResponseDto expectedCreatePlayerResponseDto = CreatePlayerResponseDto
                .buildExpectedCreatePlayerResponseDto(actualCreatePlayerResponseDto.getId(), createPlayerRequestDto);
        assertThat(actualCreatePlayerResponseDto)
                .as("Created player should have field with valid values")
                .usingRecursiveComparison()
                .isEqualTo(expectedCreatePlayerResponseDto);
    }

    @Tag("Defect - player can be created with invalid gender")
    @Test(dataProvider = "creating_player_with_gander_range_data_provider",
            dataProviderClass = CreatePlayerDataProvider.class)
    @Description("Verify that player can be created within the specified genders range")
    public void verifyPlayerCreationWithinGendersRange(String gender, int expectedStatusCode) {
        final CreatePlayerRequestDto createPlayerRequestDto = CreatePlayerRequestDto
                .buildValidRandomCreatePlayerRequestDto().setGender(gender);
        final ResponseWrapper<CreatePlayerResponseDto> actualCreatePlayerResponse = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR, createPlayerRequestDto);
        final int actualStatusCode = actualCreatePlayerResponse.getStatusCode();
        final SoftAssertions softAssertions = new SoftAssertions();

        softAssertions.assertThat(actualStatusCode)
                .as("Player creation response status code with gander '%s' should equal %s",
                        createPlayerRequestDto.getGender(), expectedStatusCode)
                .isEqualTo(expectedStatusCode);
        if (actualStatusCode == SC_SUCCESS) {
            createdPlayersList.add(actualCreatePlayerResponse.readEntity().getId());
        }
        softAssertions.assertAll();
    }

    @Tag("Defect - player can be created with disallowed passwords range")
    @Test(dataProvider = "creating_player_with_passwords_range_data_provider",
            dataProviderClass = CreatePlayerDataProvider.class)
    @Description("Verify that player can be created within the specified genders range")
    public void verifyPlayerCreationWithinPasswordsRange(String password, int expectedStatusCode) {
        final CreatePlayerRequestDto createPlayerRequestDto = CreatePlayerRequestDto
                .buildValidRandomCreatePlayerRequestDto().setPassword(password);
        final ResponseWrapper<CreatePlayerResponseDto> actualCreatePlayerResponse = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR, createPlayerRequestDto);
        final int actualStatusCode = actualCreatePlayerResponse.getStatusCode();
        final SoftAssertions softAssertions = new SoftAssertions();

        softAssertions.assertThat(actualStatusCode)
                .as("Player creation response status code with password '%s' should equal %s",
                        createPlayerRequestDto.getPassword(), expectedStatusCode)
                .isEqualTo(expectedStatusCode);
        if (actualStatusCode == SC_SUCCESS) {
            createdPlayersList.add(actualCreatePlayerResponse.readEntity().getId());
        }
        softAssertions.assertAll();
    }


    @Test
    @Tag("Negative")
    @Description("Verify that player cannot be created by user editor")
    public void verifyPlayerCreationByUserEditor() {
        final CreatePlayerResponseDto createdUserPlayer = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR,
                        CreatePlayerRequestDto.buildValidRandomCreatePlayerRequestDto()
                                .setRole(Role.USER.getRoleName()))
                .expectingStatusCode(SC_SUCCESS)
                .readEntity();
        createdPlayersList.add(createdUserPlayer.getId());

        final ResponseWrapper<CreatePlayerResponseDto> creatingPlayerByUserResponse = playersApiClient
                .runCreatePlayerRequest(createdUserPlayer.getLogin(), CreatePlayerRequestDto
                        .buildValidRandomCreatePlayerRequestDto());
        if (creatingPlayerByUserResponse.getStatusCode() == SC_SUCCESS) {
            createdPlayersList.add(creatingPlayerByUserResponse.readEntity().getId());
        }
        assertThat(creatingPlayerByUserResponse.getStatusCode())
                .as("Player can't be created by user role editor")
                .isEqualTo(SC_FORBIDDEN);
    }

}
