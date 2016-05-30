package blueprint.sdk.util.debug;

/**
 * Loads Class from byte[]
 *
 * @author lempel@gmail.com
 * @since 2016-05-30
 */
public class ClazzLoader extends ClassLoader {
    public Class findClass(String name, byte classByte[]) {
        return defineClass(name, classByte, 0, classByte.length);
    }
}
