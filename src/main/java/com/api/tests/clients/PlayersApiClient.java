package com.api.tests.clients;

import com.api.tests.dto.CreateUpdatePlayerRequestDto;
import com.api.tests.dto.PlayerDataResponseDto;
import io.qameta.allure.Step;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import org.json.JSONObject;

public final class PlayersApiClient extends BaseApiClient {

    public PlayersApiClient() {
        super(LogDetail.ALL, BaseApiClientParameters.builder()
                .baseUri("http://3.68.165.45")
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
