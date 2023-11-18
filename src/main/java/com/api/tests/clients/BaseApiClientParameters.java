package com.api.tests.clients;

import io.restassured.http.ContentType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public final class BaseApiClientParameters {

    private final String baseUri;

    private final String basePath;

    private final ContentType contentType;

}
