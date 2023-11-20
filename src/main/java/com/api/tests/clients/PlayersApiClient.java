package com.api.tests.clients;

import com.api.tests.dto.CreatePlayerRequestDto;
import com.api.tests.dto.CreatePlayerResponseDto;
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
    public ResponseWrapper<CreatePlayerResponseDto> runCreatePlayerRequest(
            final String editor,
            final CreatePlayerRequestDto createPlayerRequestDto) {
        return get(getBaseRequestSpec().queryParams(createPlayerRequestDto.buildPlayerRequestParamsMap()),
                "/create/" + editor, CreatePlayerResponseDto.class);
    }

    @Step
    public ResponseWrapper<CreatePlayerResponseDto> runGetPlayerByIdRequest(final int playerId) {
        return post(getBaseRequestSpec().body(buildPlayerIdJsonRequestBody(playerId)), "/get",
                CreatePlayerResponseDto.class);
    }

    @Step
    public ResponseWrapper<CreatePlayerResponseDto> runUpdatePlayerRequest(
            final String editor,
            final String playerId,
            final CreatePlayerRequestDto createPlayerRequestDto) {
        return patch(getBaseRequestSpec().body(createPlayerRequestDto),
                String.format("/update/%s/%s", editor, playerId), CreatePlayerResponseDto.class);
    }

    @Step
    public ResponseWrapper<String> runDeletePlayerRequest(
            final String editor,
            final int playerId) {
        return delete(getBaseRequestSpec().body(buildPlayerIdJsonRequestBody(playerId)),
                "/delete/" + editor, String.class);
    }

}
