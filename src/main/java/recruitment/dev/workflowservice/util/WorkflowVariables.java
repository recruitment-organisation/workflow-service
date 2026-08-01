package recruitment.dev.workflowservice.util;

import org.flowable.engine.delegate.DelegateExecution;

public final class WorkflowVariables {

    private WorkflowVariables() {
    }

    public static Long requiredLong(DelegateExecution execution, String variableName) {
        Object value = execution.getVariable(variableName);
        if (value == null) {
            throw new IllegalArgumentException("Workflow variable is required: " + variableName);
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String text && !text.isBlank()) {
            try {
                return Long.parseLong(text.trim());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Workflow variable must be a number: " + variableName, exception);
            }
        }
        throw new IllegalArgumentException("Workflow variable must be a number: " + variableName);
    }

    public static Long optionalLong(DelegateExecution execution, String variableName) {
        Object value = execution.getVariable(variableName);
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String text && !text.isBlank()) {
            try {
                return Long.parseLong(text.trim());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Workflow variable must be a number: " + variableName, exception);
            }
        }
        return null;
    }

    public static String requiredString(DelegateExecution execution, String variableName) {
        String value = optionalString(execution, variableName);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Workflow variable is required: " + variableName);
        }
        return value;
    }

    public static String optionalString(DelegateExecution execution, String variableName) {
        Object value = execution.getVariable(variableName);
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value).trim();
        return text.isEmpty() ? null : text;
    }

    public static Double optionalDouble(DelegateExecution execution, String variableName) {
        Object value = execution.getVariable(variableName);
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        if (value instanceof String text && !text.isBlank()) {
            try {
                return Double.parseDouble(text.trim());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Workflow variable must be a decimal number: " + variableName, exception);
            }
        }
        throw new IllegalArgumentException("Workflow variable must be a decimal number: " + variableName);
    }

    public static boolean booleanValue(DelegateExecution execution, String variableName, boolean defaultValue) {
        Object value = execution.getVariable(variableName);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean booleanValue) {
            return booleanValue;
        }
        if (value instanceof String text && !text.isBlank()) {
            return Boolean.parseBoolean(text.trim());
        }
        return defaultValue;
    }
}
