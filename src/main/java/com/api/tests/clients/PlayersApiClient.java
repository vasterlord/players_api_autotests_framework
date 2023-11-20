package com.api.tests.clients;

import com.api.tests.dto.CreateUpdatePlayerRequestDto;
import com.api.tests.dto.PlayerDataResponseDto;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import org.json.JSONObject;

public final class PlayersApiClient extends BaseApiClient {

    private static final String DEFAULT_BASE_PLAYERS_API_URL = System.getProperty("base.api.url");

    private static final String DEFAULT_API_REQUESTS_LOG_LEVEL = System.getProperty("requests.api.log.level");

    public PlayersApiClient() {
        super(DEFAULT_API_REQUESTS_LOG_LEVEL, BaseApiClientParameters.builder()
                .baseUri(DEFAULT_BASE_PLAYERS_API_URL)
                .basePath("/player")
                .contentType(ContentType.JSON)
                .build());
    }

    private static String buildPlayerIdJsonRequestBody(final int playerId) {
        return new JSONObject().put("playerId", playerId).toString();
    }

    @Step
    public ResponseWrapper<PlayerDataResponseDto> runCreatePlayerRequest(
            final String editor,
            final CreateUpdatePlayerRequestDto createPlayerRequestDto) {
        return get(getBaseRequestSpec().queryParams(createPlayerRequestDto.buildPlayerRequestParamsMap()),
                "/create/" + editor, PlayerDataResponseDto.class);
    }

    @Step
    public ResponseWrapper<PlayerDataResponseDto> runGetPlayerByIdRequest(final int playerId) {
        return post(getBaseRequestSpec().body(buildPlayerIdJsonRequestBody(playerId)), "/get",
                PlayerDataResponseDto.class);
    }

    @Step
    public ResponseWrapper<PlayerDataResponseDto> runUpdatePlayerRequest(
            final String editor,
            final int playerId,
            final CreateUpdatePlayerRequestDto createPlayerRequestDto) {
        return patch(getBaseRequestSpec().body(createPlayerRequestDto),
                String.format("/update/%s/%s", editor, playerId), PlayerDataResponseDto.class);
    }

    @Step
    public ResponseWrapper<String> runDeletePlayerRequest(
            final String editor,
            final int playerId) {
        return delete(getBaseRequestSpec().body(buildPlayerIdJsonRequestBody(playerId)),
                "/delete/" + editor, String.class);
    }

}
