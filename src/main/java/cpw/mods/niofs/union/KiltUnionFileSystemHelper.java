package cpw.mods.niofs.union;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.Hashtable;

public class KiltUnionFileSystemHelper {
    private static MethodHandles.Lookup lookup = MethodHandles.lookup();

    public static Unsafe getUnsafe() throws NoSuchFieldException, IllegalAccessException {
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        return (Unsafe) theUnsafe.get(null);
    }

    public static <T> T reflectStaticField(Class<?> destClass, String name) throws Throwable {
        Unsafe unsafe = getUnsafe();
        return (T) unsafe.getObject(destClass, unsafe.staticFieldOffset(destClass.getDeclaredField(name)));
    }

    public static void directlyLoadIntoClassLoader(ClassLoader classLoader) throws Throwable {
        Class<?> handler = Class.forName("cpw.mods.niofs.union.UnionFileSystemProvider", true, classLoader);
        Hashtable<String, URLStreamHandler> handlers = reflectStaticField(URL.class, "handlers");

        handlers.putIfAbsent("union", (URLStreamHandler) handler.getDeclaredConstructor().newInstance());
    }
}
