package com.api.tests.clients;


import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Allure;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.parsing.Parser;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.with;
import static io.restassured.config.LogConfig.logConfig;
import static io.restassured.config.ObjectMapperConfig.objectMapperConfig;
import static io.restassured.config.RestAssuredConfig.config;

@Slf4j
public class BaseApiClient {

    private static final double LONG_TO_DOUBLE_DIVIDER = 1000.0;

    private final RequestSpecification baseRequestSpec;

    protected BaseApiClient(String logDetailLevel, BaseApiClientParameters baseApiClientParameters) {
        final LogDetail logDetail = getLogDetail(logDetailLevel);
        config().logConfig(logConfig().enableLoggingOfRequestAndResponseIfValidationFails())
                .objectMapperConfig(objectMapperConfig()
                        .jackson2ObjectMapperFactory((cls, charset) -> new ObjectMapper()));
        RestAssured.defaultParser = Parser.JSON;
        baseRequestSpec = setupBaseRequestSpec(logDetail, baseApiClientParameters);
    }

    private static LogDetail getLogDetail(final String logDetailLevel) {
        final Optional<LogDetail> logDetailOptional = Arrays.stream(LogDetail.values())
                .filter(logDetailValue -> logDetailValue.name()
                .equals(logDetailLevel.toUpperCase())).findFirst();
        final LogDetail logDetail;
        if (logDetailOptional.isPresent()) {
            logDetail = logDetailOptional.get();
        } else {
            logDetail = LogDetail.ALL;
            log.warn("Provided api requests log detail level '{}' is not valid. Please check .README.md",
                    logDetailLevel);
            log.info("API requests log detail level was set to default value: {}", LogDetail.ALL.name());
        }
        return logDetail;
    }

    private RequestSpecification setupBaseRequestSpec(final LogDetail logDetail,
                                                      final BaseApiClientParameters baseApiClientParameters) {
        return new RequestSpecBuilder()
                .addFilters(List.of(new AllureRestAssured(), new ResponseLoggingFilter()))
                .log(logDetail)
                .setBaseUri(baseApiClientParameters.getBaseUri())
                .setBasePath(baseApiClientParameters.getBasePath())
                .setContentType(baseApiClientParameters.getContentType())
                .build();
    }

    /**
     * Get copy of base request spec and decorate that with specific parameters to run request. You
     * can decorate with .headers(), .body() and other available methods.
     *
     * @return {@link RequestSpecification} get copy of base request spec.
     */
    protected RequestSpecification getBaseRequestSpec() {
        return new RequestSpecBuilder().addRequestSpecification(baseRequestSpec).build();
    }

    protected <F> ResponseWrapper<F> get(
            final RequestSpecification configuredSpec,
            final String path,
            final Class<F> responseClass
    ) {
        final Response response = with().spec(configuredSpec).get(path);
        logAllureResponseTime(response);
        logFailedResponse(response);

        return new ResponseWrapper<>(response, responseClass);
    }

    protected <F> ResponseWrapper<F> post(
            final RequestSpecification configuredSpec,
            final String path,
            final Class<F> responseClass
    ) {
        final Response response = with().spec(configuredSpec).post(path);
        logAllureResponseTime(response);
        logFailedResponse(response);

        return new ResponseWrapper<>(response, responseClass);
    }

    protected <F> ResponseWrapper<F> put(
            final RequestSpecification configuredSpec,
            final String path,
            final Class<F> responseClass
    ) {
        final Response response = with().spec(configuredSpec).put(path);
        logAllureResponseTime(response);
        logFailedResponse(response);

        return new ResponseWrapper<>(response, responseClass);
    }

    protected <F> ResponseWrapper<F> patch(
            final RequestSpecification configuredSpec,
            final String path,
            final Class<F> responseClass
    ) {
        final Response response = with().spec(configuredSpec).patch(path);
        logAllureResponseTime(response);
        logFailedResponse(response);

        return new ResponseWrapper<>(response, responseClass);
    }

    protected <F> ResponseWrapper<F> delete(
            final RequestSpecification configuredSpec,
            final String path,
            final Class<F> responseClass
    ) {
        final Response response = with().spec(configuredSpec).delete(path);
        logAllureResponseTime(response);
        logFailedResponse(response);

        return new ResponseWrapper<>(response, responseClass);
    }

    private void logFailedResponse(final Response response) {
        response.then().log().ifError().log().ifValidationFails();
    }

    private Response logAllureResponseTime(final Response rawResponse) {
        Allure.addAttachment("Response time",
                Double.toString(rawResponse.getTimeIn(TimeUnit.MILLISECONDS) / LONG_TO_DOUBLE_DIVIDER)
        );

        return rawResponse;
    }

    @SneakyThrows
    public static String objectToJsonString(final Object object) {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

}
