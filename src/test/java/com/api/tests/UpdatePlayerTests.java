package com.api.tests;

import com.api.tests.clients.ResponseWrapper;
import com.api.tests.dto.CreateUpdatePlayerRequestDto;
import com.api.tests.dto.PlayerDataResponseDto;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.testng.Tag;
import io.qameta.allure.testng.Tags;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Test;

import static org.apache.hc.core5.http.HttpStatus.SC_CONFLICT;
import static org.apache.hc.core5.http.HttpStatus.SC_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;

@Feature("/player/update/{editor}/{id}")
public class UpdatePlayerTests extends BasePlayersApiTest {

    @Test
    @Tag("Possible defect, because regarding Swagger update request contains password data, whereas response is not")
    @Description("Verify that player can be updated")
    public void verifyPlayerDataCanBeUpdated() {
        final PlayerDataResponseDto createPlayerResponseDto = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR,
                        CreateUpdatePlayerRequestDto.buildValidRandomCreatePlayerRequestDto())
                .expectingStatusCode(SC_SUCCESS)
                .readEntity();
        createdPlayersList.add(createPlayerResponseDto.getId());

        final CreateUpdatePlayerRequestDto updatePlayerRequestDto = CreateUpdatePlayerRequestDto
                .buildValidRandomCreatePlayerRequestDto();
        final PlayerDataResponseDto actualUpdatePlayerDataResponseDto = playersApiClient
                .runUpdatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR, createPlayerResponseDto.getId(),
                        updatePlayerRequestDto)
                .expectingStatusCode(SC_SUCCESS)
                .readEntity();
        final PlayerDataResponseDto expectedUpdatePlayerDataResponseDto = PlayerDataResponseDto
                .buildExpectedPlayerDataResponseDto(createPlayerResponseDto.getId(), updatePlayerRequestDto);
        assertThat(actualUpdatePlayerDataResponseDto)
                .as("Update player should have field with valid values")
                .usingRecursiveComparison()
                .isEqualTo(expectedUpdatePlayerDataResponseDto);
    }

    @Test
    @Tags({
            @Tag("Negative"),
            @Tag("Defect: when we re-use existing, active user 'screenName' for updating another one 404 not returned")
    })
    @Description("Verify that player cannot be updated with the the other active user login and screenName values")
    public void verifyPlayerCannotHaveOtherUserLoginScreenName() {
        final PlayerDataResponseDto firstPlayerDto = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR,
                        CreateUpdatePlayerRequestDto.buildValidRandomCreatePlayerRequestDto())
                .expectingStatusCode(SC_SUCCESS)
                .readEntity();
        final PlayerDataResponseDto playerDtoToUpdate = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR,
                        CreateUpdatePlayerRequestDto.buildValidRandomCreatePlayerRequestDto())
                .expectingStatusCode(SC_SUCCESS)
                .readEntity();
        createdPlayersList.add(firstPlayerDto.getId());

        final CreateUpdatePlayerRequestDto updatePlayerLoginRequestDto = CreateUpdatePlayerRequestDto
                .buildValidRandomCreatePlayerRequestDto().setLogin(firstPlayerDto.getLogin());
        final ResponseWrapper<PlayerDataResponseDto> updatedPlayerLoginResponse = playersApiClient
                .runUpdatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR, playerDtoToUpdate.getId(),
                        updatePlayerLoginRequestDto);

        final CreateUpdatePlayerRequestDto updatePlayerScreenNameRequestDto = CreateUpdatePlayerRequestDto
                .buildValidRandomCreatePlayerRequestDto().setScreenName(firstPlayerDto.getScreenName());
        final ResponseWrapper<PlayerDataResponseDto> updatedPlayerScreenNameResponse = playersApiClient
                .runUpdatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR, playerDtoToUpdate.getId(),
                        updatePlayerScreenNameRequestDto);

        final SoftAssertions softAssertions = new SoftAssertions();

        softAssertions.assertThat(updatedPlayerLoginResponse.getStatusCode())
                .as("One user can't be updated with another user 'login' value")
                .isEqualTo(SC_CONFLICT);
        softAssertions.assertThat(updatedPlayerScreenNameResponse.getStatusCode())
                .as("One user can't be updated with another user 'screenName' value")
                .isEqualTo(SC_CONFLICT);

        softAssertions.assertAll();
    }

}
