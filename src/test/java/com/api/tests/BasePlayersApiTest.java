package com.api.tests;

import com.api.tests.clients.PlayersApiClient;
import io.qameta.allure.Step;
import org.testng.annotations.AfterClass;

import java.util.concurrent.CopyOnWriteArrayList;

import static org.apache.http.HttpStatus.SC_NO_CONTENT;

public class BasePlayersApiTest {

    protected static final String DEFAULT_SUPERVISOR_EDITOR = "supervisor";

    protected final PlayersApiClient playersApiClient = new PlayersApiClient();

    protected final CopyOnWriteArrayList<Integer> createdPlayersList = new CopyOnWriteArrayList<>();

    @Step
    @AfterClass
    public void cleanUpCreatedPlayers() {
        createdPlayersList.forEach(playerId -> playersApiClient.runDeletePlayerRequest(DEFAULT_SUPERVISOR_EDITOR, playerId)
                .expectingStatusCode(SC_NO_CONTENT));
    }

}
