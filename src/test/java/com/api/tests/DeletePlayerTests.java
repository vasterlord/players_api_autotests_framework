package com.api.tests;

import com.api.tests.clients.PlayersApiClient;
import com.api.tests.clients.ResponseWrapper;
import com.api.tests.dto.CreateUpdatePlayerRequestDto;
import com.api.tests.dto.PlayerDataResponseDto;
import com.api.tests.dto.Role;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.testng.Tag;
import io.qameta.allure.testng.Tags;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.concurrent.CopyOnWriteArrayList;

import static org.apache.hc.core5.http.HttpStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

@Feature("/player/delete/{editor}")
public class DeletePlayerTests extends BasePlayersApiTest {

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
    @Description("Verify that admin editor can delete player")
    public void verifyAdminDeletesPlayerSuccessfully() {
        final PlayerDataResponseDto adminPlayerResponseDto = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR,
                        CreateUpdatePlayerRequestDto.buildValidRandomCreatePlayerRequestDto(Role.ADMIN.getRoleName()))
                .expectingStatusCode(SC_SUCCESS)
                .readEntity();
        createdPlayersList.add(adminPlayerResponseDto.getId());
        final PlayerDataResponseDto userPlayerResponseDto = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR,
                        CreateUpdatePlayerRequestDto.buildValidRandomCreatePlayerRequestDto(Role.USER.getRoleName()))
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
    @Tags({
            @Tag("Negative"),
            @Tag("Defect: user editor can delete admin user, whereas user should have delete opportunities")
    })
    @Description("Verify that user editor cannot delete admin player")
    public void verifyUserCannotDeleteAdmin() {
        final PlayerDataResponseDto userPlayerResponseDto = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR,
                        CreateUpdatePlayerRequestDto.buildValidRandomCreatePlayerRequestDto(Role.USER.getRoleName()))
                .expectingStatusCode(SC_SUCCESS)
                .readEntity();
        createdPlayersList.add(userPlayerResponseDto.getId());
        final PlayerDataResponseDto adminPlayerResponseDto = playersApiClient
                .runCreatePlayerRequest(DEFAULT_SUPERVISOR_EDITOR,
                        CreateUpdatePlayerRequestDto.buildValidRandomCreatePlayerRequestDto(Role.ADMIN.getRoleName()))
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
