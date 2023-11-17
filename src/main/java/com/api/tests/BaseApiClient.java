package com.api.tests;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.api.tests.reporting.AllureStepsLogging.addAllureStepTextAttachment;
import static io.restassured.RestAssured.with;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;

public class BaseApiClient {

    private static final double LONG_TO_DOUBLE_DIVIDER = 1000.0;

    private final RequestSpecification baseRequestSpec;

    protected BaseApiClient(BaseApiParameters baseApiParameters) {
        config().objectMapperConfig(objectMapperConfig()
                .jackson2ObjectMapperFactory((cls, charset) -> new ObjectMapper()));
        baseRequestSpec = setupBaseRequestSpec(baseApiParameters);
    }

    private RequestSpecification setupBaseRequestSpec(final BaseApiParameters baseApiParameters) {
        return new RequestSpecBuilder().addFilters(List.of(new AllureRestAssured()))
                .setBaseUri(baseApiParameters.getBaseUri())
                .setContentType(baseApiParameters.getContentType())
                .build();
    }

    /**
     * Get copy of base request spec and decorate that with specific parameters to run request. You
     * can decorate with .headers(), .body() and other available methods.
     *
     * @return {@link RequestSpecification} get copy of base request spec.
     */
    public RequestSpecification getBaseRequestSpec() {
        return new RequestSpecBuilder().addRequestSpecification(baseRequestSpec).build();
    }

    public <F> ResponseWrapper<F> get(
            final RequestSpecification configuredSpec,
            final String path,
            final Class<F> responseClass
    ) {
        final Response response = with().spec(configuredSpec).get(path);
        logAllureResponseTime(response);
        logFailedResponse(response);

        return new ResponseWrapper<>(response, responseClass);
    }

    public <F> ResponseWrapper<F> post(
            final RequestSpecification configuredSpec,
            final String path,
            final Class<F> responseClass
    ) {
        final Response response = with().spec(configuredSpec).post(path);
        logAllureResponseTime(response);
        logFailedResponse(response);

        return new ResponseWrapper<>(response, responseClass);
    }

    public <F> ResponseWrapper<F> put(
            final RequestSpecification configuredSpec,
            final String path,
            final Class<F> responseClass
    ) {
        final Response response = with().spec(configuredSpec).put(path);
        logAllureResponseTime(response);
        logFailedResponse(response);

        return new ResponseWrapper<>(response, responseClass);
    }

    public <F> ResponseWrapper<F> delete(
            final RequestSpecification configuredSpec,
            final String path,
            final Class<F> responseClass
    ) {
        final Response response = with().spec(configuredSpec).delete(path);
        logAllureResponseTime(response);
        logFailedResponse(response);

        return new ResponseWrapper<>(response, responseClass);
    }

    /**
     * Log response if status code higher than 300 or validation failed.
     *
     * @param response - {@link Response} response.
     */
    private void logFailedResponse(final Response response) {
        response.then().log().ifError().log().ifValidationFails();
    }

    private Response logAllureResponseTime(final Response rawResponse) {
        addAllureStepTextAttachment("Response time",
                Double.toString(rawResponse.getTimeIn(TimeUnit.MILLISECONDS) / LONG_TO_DOUBLE_DIVIDER)
        );

        return rawResponse;
    }

}
