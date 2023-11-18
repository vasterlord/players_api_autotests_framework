package com.api.tests.reporting;

import io.qameta.allure.Allure.StepContext;
import io.qameta.allure.Allure.ThrowableContextRunnable;
import io.qameta.allure.Allure.ThrowableRunnable;
import io.qameta.allure.listener.StepLifecycleListener;
import io.qameta.allure.model.Parameter;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import io.qameta.allure.util.ExceptionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static io.qameta.allure.Allure.addAttachment;
import static io.qameta.allure.Allure.getLifecycle;
import static io.qameta.allure.util.ResultsUtils.*;
import static java.util.Locale.ENGLISH;
import static org.apache.commons.lang3.StringEscapeUtils.escapeJava;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
public final class AllureStepsLogging implements StepLifecycleListener {

    private static final Pattern CAMEL_CASE_PATTERN = Pattern.compile("[a-z]+([A-Z][a-z0-9]+)+");

    public static final int STEP_LOG_CHARS_LIMIT = 300;

    public static <T> T allureStep(
            final String name,
            final ThrowableRunnable<T> runnable
    ) {
        return allureStep(name, step -> {
            step.name(name);
            return runnable.run();
        });
    }

    public static <T> T allureStep(
            final String stepName,
            final ThrowableContextRunnable<T, StepContext> runnable
    ) {
        final String uuid = UUID.randomUUID().toString();
        getLifecycle().startStep(uuid, new StepResult().setName(stepName));
        try {
            final T result = runnable.run(new DefaultStepContext(uuid));
            getLifecycle().updateStep(uuid, step -> step.setStatus(Status.PASSED));
            return result;
        } catch (final Throwable throwable) {
            getLifecycle().updateStep(s -> s.setStatus(getStatus(throwable).orElse(Status.BROKEN))
                    .setStatusDetails(getStatusDetails(throwable).orElse(null)));
            ExceptionUtils.sneakyThrow(throwable);
            return null;
        } finally {
            getLifecycle().stopStep(uuid);
        }
    }

    public static void addAllureStepTextAttachment(
            final String attachmentName,
            final String attachmentContent
    ) {
        addAttachment(attachmentName, "text/plain", attachmentContent, ".txt");
    }


    @Override
    public void beforeStepStart(final StepResult stepResult) {
        if (isCamelCaseString(stepResult.getName())) {
            final String allureStep = convertCamelCaseToSentenceCase(stepResult.getName());
            final String stepParameters = stepResult.getParameters().stream()
                    .map(Parameter::getValue)
                    .filter(param -> escapeJava(param).chars().count() <= STEP_LOG_CHARS_LIMIT)
                    .collect(Collectors.joining(", "));

            final String resultAllureStepLog =
                    isNotBlank(stepParameters) ? String.join(": ", allureStep, stepParameters)
                            : allureStep;

            log.info(resultAllureStepLog);
            stepResult.setName(resultAllureStepLog);
        } else {
            log.info(stepResult.getName());
            stepResult.setName(stepResult.getName());
        }
    }

    private static boolean isCamelCaseString(final String text) {
        return isNotBlank(text) && CAMEL_CASE_PATTERN.matcher(text).matches();
    }

    private static String convertCamelCaseToSentenceCase(final String text) {
        final String result = StringUtils.join(
                StringUtils.splitByCharacterTypeCamelCase(text),
                SPACE
        );
        return result.substring(0, 1).toUpperCase(ENGLISH) + result.substring(1)
                .toLowerCase(ENGLISH);
    }

    @RequiredArgsConstructor
    @SuppressWarnings("java:S2972")
    private static final class DefaultStepContext implements StepContext {

        private final String uuid;

        public void name(final String name) {
            getLifecycle().updateStep(uuid, stepResult -> stepResult.setName(name));
        }

        public <T> T parameter(
                final String name,
                final T value
        ) {
            return parameter(name, value, null, null);
        }

        @Override
        public <T> T parameter(
                final String name,
                final T value,
                final Boolean excluded
        ) {
            return parameter(name, value, excluded, null);
        }

        @Override
        public <T> T parameter(
                final String name,
                final T value,
                final Parameter.Mode mode
        ) {
            return parameter(name, value, null, mode);
        }

        @Override
        public <T> T parameter(
                final String name,
                final T value,
                final Boolean excluded,
                final Parameter.Mode mode
        ) {
            final Parameter param = createParameter(name, value, excluded, mode);
            getLifecycle().updateStep(uuid, stepResult -> stepResult.getParameters().add(param));
            return value;
        }
    }
}