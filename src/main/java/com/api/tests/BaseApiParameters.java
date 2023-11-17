package com.api.tests;

import io.restassured.http.ContentType;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public final class BaseApiParameters {

    private final String baseUri;

    private final ContentType contentType;

}
