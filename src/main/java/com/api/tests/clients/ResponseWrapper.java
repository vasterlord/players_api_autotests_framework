package com.api.tests.clients;

import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@RequiredArgsConstructor
public final class ResponseWrapper<T> {

    private final Response rawResponse;

    private final Class<T> responseClass;

    public Response getRawResponse() {
        return rawResponse;
    }

    public T readEntity() {
        return rawResponse.as(responseClass);
    }

    public List<T> readEntities() {
        return readEntities(EMPTY);
    }

    public List<T> readEntities(final String jsonPath) {
        return rawResponse.body().jsonPath().getList(jsonPath, responseClass);
    }

    public int getStatusCode() {
        return rawResponse.getStatusCode();
    }

    public ResponseWrapper<T> expectingStatusCode(final int statusCode) {
        getRawResponse().then().assertThat().statusCode(statusCode).log().ifValidationFails();

        return this;
    }

}
