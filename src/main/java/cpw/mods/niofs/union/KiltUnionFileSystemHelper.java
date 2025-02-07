package cpw.mods.niofs.union;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Collections;
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

    public static <T> void modifyStaticField(Class<?> destClass, String name, T value) throws Throwable {
        Unsafe unsafe = getUnsafe();
        unsafe.putObject(destClass, unsafe.staticFieldOffset(destClass.getDeclaredField(name)), value);
    }

    public static void directlyLoadIntoClassLoader(ClassLoader classLoader) throws Throwable {
        synchronized (lock) {
            Class<?> handler = Class.forName("cpw.mods.niofs.union.UnionFileSystemProvider", true, classLoader);
            List<FileSystemProvider> providers = new ArrayList<>(reflectStaticField(FileSystemProvider.class, "installedProviders"));
            providers.add((FileSystemProvider) handler.getDeclaredConstructor().newInstance());

            modifyStaticField(FileSystemProvider.class, "installedProviders", Collections.unmodifiableList(providers));
        }
    }
}
