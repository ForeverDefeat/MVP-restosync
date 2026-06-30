package com.restosync.comandas;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.DockerClientFactory;

public class RequiresDockerCondition implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        try {
            if (DockerClientFactory.instance().isDockerAvailable()) {
                return ConditionEvaluationResult.enabled("Docker is available");
            }
        } catch (RuntimeException ignored) {
            // Testcontainers logs the detailed provider checks; the condition keeps local test runs usable.
        }

        return ConditionEvaluationResult.disabled("Docker is required for Testcontainers MySQL tests");
    }
}
