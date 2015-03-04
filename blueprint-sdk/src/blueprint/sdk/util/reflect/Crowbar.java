/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Repository - https://github.com/lempel/blueprint-sdk.git
 Blog - http://lempel.egloos.com
 */

package blueprint.sdk.util.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;

/**
 * Provides reflective access to every field/method.
 *
 * @author Sangmin Lee
 * @since 2013. 9. 5.
 */
@SuppressWarnings("WeakerAccess")
public class Crowbar {
    public static boolean checkSecurity() {
        boolean result = true;

        try {
            System.getSecurityManager().checkPermission(new ReflectPermission("suppressAccessChecks"));
        } catch (SecurityException e) {
            result = false;
        }

        return result;
    }

    public static Field getField(Class<?> target, String name) throws SecurityException, NoSuchFieldException {
        Field result = target.getDeclaredField(name);
        result.setAccessible(true);

        return result;
    }

    public static Method getMethod(Class<?> target, String name, Class<?>[] paramTypes) throws SecurityException,
            NoSuchMethodException {
        Method result = target.getDeclaredMethod(name, paramTypes);
        result.setAccessible(true);
        return result;
    }

    public static Object getValue(Object target, String name) throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field field = getField(target.getClass(), name);
        return field.get(target);
    }

    public static Object callMethod(Object target, String name, Object[] params) throws SecurityException,
            IllegalArgumentException, IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        Class<?>[] paramTypes = new Class<?>[params.length];
        for (int i = 0; i < params.length; i++) {
            paramTypes[i] = params[i].getClass();
        }

        Method method = getMethod(target.getClass(), name, paramTypes);
        return method.invoke(target, params);
    }
}
