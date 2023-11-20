package com.api.tests;

import com.api.tests.clients.ResponseWrapper;
import com.api.tests.dto.CreateUpdatePlayerRequestDto;
import com.api.tests.dto.PlayerDataResponseDto;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.testng.Tag;
import io.qameta.allure.testng.Tags;
import org.testng.annotations.Test;

import static org.apache.hc.core5.http.HttpStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

@Feature("/player/get")
public class GetPlayerTests extends BasePlayersApiTest {


    @Test
    @Description("Verify that player is returned successfully for GET request")
    public void verifyPlayerIsReturnedSuccessfully() {
        final CreateUpdatePlayerRequestDto createPlayerRequestDto = CreateUpdatePlayerRequestDto
                .buildValidRandomCreatePlayerRequestDto();
        final PlayerDataResponseDto createPlayerResponseDto = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR, createPlayerRequestDto)
                .expectingStatusCode(SC_SUCCESS)
                .readEntity();
        createdPlayersList.add(createPlayerResponseDto.getId());


        final PlayerDataResponseDto actualGetPlayerResponseDto = playersApiClient
                .runGetPlayerByIdRequest(createPlayerResponseDto.getId())
                .expectingStatusCode(SC_SUCCESS)
                .readEntity();
        final PlayerDataResponseDto expectedGetPlayerResponseDto = PlayerDataResponseDto
                .buildExpectedPlayerDataResponseDto(createPlayerResponseDto.getId(), createPlayerRequestDto);

        assertThat(actualGetPlayerResponseDto)
                .as("Get player request should have field with valid values")
                .usingRecursiveComparison()
                .isEqualTo(expectedGetPlayerResponseDto);
    }

    @Test
    @Tags({
            @Tag("Negative"),
            @Tag("Defect - the result of getting player by not existing playerId is 200 OK, expected 404 NOT FOUND")
    })
    @Description("Verify that not found status code returned while getting not existing user")
    public void verifyErrorCodeIsReturnedWhenGettingInvalidUser() {
        final CreateUpdatePlayerRequestDto createPlayerRequestDto = CreateUpdatePlayerRequestDto
                .buildValidRandomCreatePlayerRequestDto();
        final PlayerDataResponseDto createPlayerResponseDto = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR, createPlayerRequestDto)
                .expectingStatusCode(SC_SUCCESS)
                .readEntity();
        playersApiClient.runDeletePlayerRequest(DEFAULT_SUPERVISOR_EDITOR, createPlayerResponseDto.getId())
                .expectingStatusCode(SC_NO_CONTENT);

        final ResponseWrapper<PlayerDataResponseDto> actualGetPlayerResponseDto = playersApiClient
                .runGetPlayerByIdRequest(createPlayerResponseDto.getId());

        assertThat(actualGetPlayerResponseDto.getStatusCode())
                .as("Get player request should have field with valid values")
                .isEqualTo(SC_NOT_FOUND);
    }

}
