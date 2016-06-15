package org.jboss.resteasy.test.nextgen.wadl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by weli on 6/14/16.
 */
public class WADLTestExistenceVerifier {
    private Map<String, Boolean> data = new HashMap<>();

    public void createVerifier(String... keys) {
        for (String key : keys) {
            data.put(key, false);
        }
    }

    public void verify(List targets, Class targetClass, String fetchKeyMethod) throws InvocationTargetException, IllegalAccessException {
        assertNotNull(targets);
        assertTrue(targets.size() > 0);

        Method invocation = null;

        for (Object target : targets) {
            for (Method method : target.getClass().getMethods()) {
                if (target.getClass().equals(targetClass) && method.getName().equals(fetchKeyMethod)) {
                    invocation = method;
                    break;
                }
            }
        }

        if (invocation == null) throw new NoSuchMethodError(fetchKeyMethod);

        for (Object target : targets) {
            for (String key : data.keySet()) {
                if (target.getClass().equals(targetClass) && key.equals(invocation.invoke(target))) {
                    data.put(key, true);
                }

            }
        }

        assertTrue(data.toString(), allTrue());
    }

    public boolean allTrue() {
        boolean flag = true;
        for (Boolean value : data.values()) {
            if (value.booleanValue() == false) {
                flag = false;
                break;
            }
        }
        return flag;
    }
}
