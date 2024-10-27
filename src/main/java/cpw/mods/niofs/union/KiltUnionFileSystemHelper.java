package cpw.mods.niofs.union;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.file.spi.FileSystemProvider;
import java.util.List;

public class KiltUnionFileSystemHelper {
    private static final Object lock = new Object();

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
        synchronized (lock) {
            Class<?> handler = Class.forName("cpw.mods.niofs.union.UnionFileSystemProvider", true, classLoader);
            List<FileSystemProvider> providers = reflectStaticField(FileSystemProvider.class, "installedProviders");

            providers.add((FileSystemProvider) handler.getDeclaredConstructor().newInstance());
        }
    }
}
