/*
 License:

 blueprint-sdk is licensed under the terms of Eclipse Public License(EPL) v1.0
 (http://www.eclipse.org/legal/epl-v10.html)


 Distribution:

 Maven Central - https://search.maven.org/artifact/io.github.lempel/blueprint-sdk
 MVN Repository - https://mvnrepository.com/artifact/io.github.lempel/blueprint-sdk
 */

package blueprint.sdk.util.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ReflectPermission;

/**
 * Provides reflective access to every field/method.
 *
 * @author lempel@gmail.com
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

    /**
     * See if 'test' is an instance of 'control'
     *
     * @param test    test class
     * @param control control class
     * @return true: 'test' is an instance of 'control'
     */
    public static boolean isInstance(Class test, Class control) {
        boolean result;

        if (test.equals(control)) {
            result = true;
        } else {
            Class[] infs = test.getInterfaces();
            result = false;

            for (Class inf : infs) {
                if (isInstance(inf, control)) {
                    result = true;
                    break;
                }
            }

            if (!result) {
                Class superClass = test.getSuperclass();
                if (superClass != null) {
                    result = isInstance(superClass, control);
                }
            }
        }

        return result;
    }
}
