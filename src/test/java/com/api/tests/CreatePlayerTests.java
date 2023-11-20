package com.api.tests;

import com.api.tests.clients.PlayersApiClient;
import com.api.tests.clients.ResponseWrapper;
import com.api.tests.dto.CreateUpdatePlayerRequestDto;
import com.api.tests.dto.PlayerDataResponseDto;
import com.api.tests.dto.Role;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.testng.Tag;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.CopyOnWriteArrayList;

import static org.apache.hc.core5.http.HttpStatus.SC_SUCCESS;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.assertj.core.api.Assertions.assertThat;

@Feature("/player/create/{editor}")
public class CreatePlayerTests extends BasePlayersApiTest {

    private PlayersApiClient playersApiClient;

    private CopyOnWriteArrayList<Integer> createdPlayersList;

    @BeforeClass
    public void setUp() {
        playersApiClient = new PlayersApiClient();
        createdPlayersList = new CopyOnWriteArrayList<>();
    }

    @AfterClass
    public void tearDown() {
        cleanUpCreatedPlayers(createdPlayersList);
    }

    @Test
    @Description("Verify that player is created successfully")
    public void verifySuccessfulPlayerCreating() {
        final PlayerDataResponseDto createPlayerResponseDto = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR,
                        CreateUpdatePlayerRequestDto.buildValidRandomCreatePlayerRequestDto())
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
    @Tag("Defect: at the response we have nulls for age, gender, password, role, screenName values")
    @Description("Verify that created players has all valid fields with from request data")
    public void verifyPlayerIsCreatedWithValidFieldsDataResponse() {
        final CreateUpdatePlayerRequestDto createPlayerRequestDto = CreateUpdatePlayerRequestDto
                .buildValidRandomCreatePlayerRequestDto();
        final PlayerDataResponseDto actualCreatePlayerResponseDto = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR,
                        createPlayerRequestDto)
                .expectingStatusCode(SC_SUCCESS)
                .readEntity();
        createdPlayersList.add(actualCreatePlayerResponseDto.getId());

        final PlayerDataResponseDto expectedCreatePlayerResponseDto = PlayerDataResponseDto
                .buildExpectedPlayerDataResponseDto(actualCreatePlayerResponseDto.getId(), createPlayerRequestDto);
        assertThat(actualCreatePlayerResponseDto)
                .as("Created player should have field with valid values")
                .usingRecursiveComparison()
                .isEqualTo(expectedCreatePlayerResponseDto);
    }

    @Tag("Defect: the player can be created with invalid gender value")
    @Test(dataProvider = "creating_player_with_gander_range_data_provider",
            dataProviderClass = CreatePlayerDataProvider.class)
    @Description("Verify that player can be created within the specified genders range")
    public void verifyPlayerCreationWithinGendersRange(String gender, int expectedStatusCode) {
        final CreateUpdatePlayerRequestDto createPlayerRequestDto = CreateUpdatePlayerRequestDto
                .buildValidRandomCreatePlayerRequestDto().setGender(gender);
        final ResponseWrapper<PlayerDataResponseDto> actualCreatePlayerResponse = playersApiClient
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

    @Tag("Defect: the player can be created with disallowed passwords range")
    @Test(dataProvider = "creating_player_with_passwords_range_data_provider",
            dataProviderClass = CreatePlayerDataProvider.class)
    @Description("Verify that player can be created within the specified genders range")
    public void verifyPlayerCreationWithinPasswordsRange(String password, int expectedStatusCode) {
        final CreateUpdatePlayerRequestDto createPlayerRequestDto = CreateUpdatePlayerRequestDto
                .buildValidRandomCreatePlayerRequestDto().setPassword(password);
        final ResponseWrapper<PlayerDataResponseDto> actualCreatePlayerResponse = playersApiClient
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
        final PlayerDataResponseDto createdUserPlayer = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR,
                        CreateUpdatePlayerRequestDto.buildValidRandomCreatePlayerRequestDto()
                                .setRole(Role.USER.getRoleName()))
                .expectingStatusCode(SC_SUCCESS)
                .readEntity();
        createdPlayersList.add(createdUserPlayer.getId());

        final ResponseWrapper<PlayerDataResponseDto> creatingPlayerByUserResponse = playersApiClient
                .runCreatePlayerRequest(createdUserPlayer.getLogin(), CreateUpdatePlayerRequestDto
                        .buildValidRandomCreatePlayerRequestDto());
        if (creatingPlayerByUserResponse.getStatusCode() == SC_SUCCESS) {
            createdPlayersList.add(creatingPlayerByUserResponse.readEntity().getId());
        }
        assertThat(creatingPlayerByUserResponse.getStatusCode())
                .as("Player can't be created by user role editor")
                .isEqualTo(SC_FORBIDDEN);
    }

}
