package recruitment.dev.workflowservice;

import org.flowable.engine.delegate.DelegateExecution;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public final class TestDelegateExecutionFactory {

    private TestDelegateExecutionFactory() {
    }

    public static DelegateExecution create(Map<String, Object> variables) {
        return create(variables, null);
    }

    public static DelegateExecution create(Map<String, Object> variables, String activityName) {
        Map<String, Object> mutableVariables = new HashMap<>(variables);

        return (DelegateExecution) Proxy.newProxyInstance(
                DelegateExecution.class.getClassLoader(),
                new Class[]{DelegateExecution.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getVariable" -> mutableVariables.get(args[0]);
                    case "setVariable" -> {
                        mutableVariables.put((String) args[0], args[1]);
                        yield null;
                    }
                    case "hasVariable" -> mutableVariables.containsKey(args[0]);
                    case "getVariables" -> new HashMap<>(mutableVariables);
                    case "getCurrentActivityName" -> activityName;
                    case "toString" -> "DelegateExecutionTestStub";
                    default -> defaultValue(method.getReturnType());
                }
        );
    }

    private static Object defaultValue(Class<?> returnType) {
        if (!returnType.isPrimitive()) {
            return null;
        }
        if (returnType == boolean.class) {
            return false;
        }
        if (returnType == char.class) {
            return '\0';
        }
        if (returnType == byte.class) {
            return (byte) 0;
        }
        if (returnType == short.class) {
            return (short) 0;
        }
        if (returnType == int.class) {
            return 0;
        }
        if (returnType == long.class) {
            return 0L;
        }
        if (returnType == float.class) {
            return 0F;
        }
        if (returnType == double.class) {
            return 0D;
        }
        return null;
    }
}
