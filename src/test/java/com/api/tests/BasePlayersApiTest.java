package com.api.tests;

import com.api.tests.clients.PlayersApiClient;
import io.qameta.allure.Step;

import java.util.concurrent.CopyOnWriteArrayList;

import static org.apache.http.HttpStatus.SC_NO_CONTENT;

public class BasePlayersApiTest {

    protected static final String DEFAULT_SUPERVISOR_EDITOR = "supervisor";

    @Step
    protected void cleanUpCreatedPlayers(final CopyOnWriteArrayList<Integer> createdPlayersList) {
        createdPlayersList.forEach(playerId -> new PlayersApiClient().runDeletePlayerRequest(DEFAULT_SUPERVISOR_EDITOR,
                playerId).expectingStatusCode(SC_NO_CONTENT));
    }

}
