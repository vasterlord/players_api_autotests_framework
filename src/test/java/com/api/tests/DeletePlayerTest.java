package com.api.tests;

import com.api.tests.clients.ResponseWrapper;
import com.api.tests.dto.CreatePlayerRequestDto;
import com.api.tests.dto.CreatePlayerResponseDto;
import com.api.tests.dto.Role;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.testng.Tag;
import io.qameta.allure.testng.Tags;
import org.testng.annotations.Test;

import static org.apache.hc.core5.http.HttpStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

@Feature("/player/delete/{editor}")
public class DeletePlayerTest extends BasePlayersApiTest {

    @Test
    @Description("Verify that admin editor can delete player")
    public void verifyAdminDeletesPlayerSuccessfully() {
        final CreatePlayerResponseDto adminPlayerResponseDto = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR,
                        CreatePlayerRequestDto.buildValidRandomCreatePlayerRequestDto(Role.ADMIN.getRoleName()))
                .expectingStatusCode(SC_SUCCESS)
                .readEntity();
        createdPlayersList.add(adminPlayerResponseDto.getId());
        final CreatePlayerResponseDto userPlayerResponseDto = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR,
                        CreatePlayerRequestDto.buildValidRandomCreatePlayerRequestDto(Role.USER.getRoleName()))
                .expectingStatusCode(SC_SUCCESS)
                .readEntity();

        final ResponseWrapper<String> deleteUserResponse = playersApiClient
                .runDeletePlayerRequest(adminPlayerResponseDto.getLogin(), userPlayerResponseDto.getId());
        if (deleteUserResponse.getStatusCode() != SC_NO_CONTENT) {
            createdPlayersList.add(userPlayerResponseDto.getId());
        }
        assertThat(deleteUserResponse.getStatusCode())
                .as("Admin editor should be able to delete user successfully")
                .isEqualTo(SC_NO_CONTENT);
    }

    @Test
    @Tags({@Tag("Negative"), @Tag("Defect")})
    @Description("Verify that user editor cannot delete admin player")
    public void verifyUserCannotDeleteAdmin() {
        final CreatePlayerResponseDto userPlayerResponseDto = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR,
                        CreatePlayerRequestDto.buildValidRandomCreatePlayerRequestDto(Role.USER.getRoleName()))
                .expectingStatusCode(SC_SUCCESS)
                .readEntity();
        createdPlayersList.add(userPlayerResponseDto.getId());
        final CreatePlayerResponseDto adminPlayerResponseDto = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR,
                        CreatePlayerRequestDto.buildValidRandomCreatePlayerRequestDto(Role.ADMIN.getRoleName()))
                .expectingStatusCode(SC_SUCCESS)
                .readEntity();

        final ResponseWrapper<String> deleteUserResponse = playersApiClient
                .runDeletePlayerRequest(userPlayerResponseDto.getLogin(), adminPlayerResponseDto.getId());
        if (deleteUserResponse.getStatusCode() != SC_NO_CONTENT) {
            createdPlayersList.add(adminPlayerResponseDto.getId());
        }
        assertThat(deleteUserResponse.getStatusCode())
                .as("User editor should not be able to delete admin")
                .isEqualTo(SC_FORBIDDEN);
    }

}
