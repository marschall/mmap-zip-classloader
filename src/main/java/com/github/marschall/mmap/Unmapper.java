package com.github.marschall.mmap;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.file.Path;

/**
 * Utility class to unmap a {@link MappedByteBuffer}.
 */
final class Unmapper {

  private static final MethodHandle UNSAFE_INVOKE_CLEANER;

  static {
    Lookup lookup = MethodHandles.publicLookup();
    // Unsafe.theUnsafe.invokeCleaner(byteBuffer)
    try {
      Class<?> unsafeClass = getUnsafeClass();
      Object unsafe = getTheUnsafe(unsafeClass);

      Method invokeCleaner = unsafeClass.getDeclaredMethod("invokeCleaner", ByteBuffer.class);
      UNSAFE_INVOKE_CLEANER = lookup.unreflect(invokeCleaner).bindTo(unsafe);
    } catch (ReflectiveOperationException e) {
      throw new IllegalStateException("could not get invokeCleaner method handle", e);
    }
  }

  private static Class<?> getUnsafeClass() throws ReflectiveOperationException {
    return Class.forName("sun.misc.Unsafe");
  }

  private static Object getTheUnsafe(Class<?> unsafeClass) throws ReflectiveOperationException {
    Field singleoneInstanceField = unsafeClass.getDeclaredField("theUnsafe");
    if (!singleoneInstanceField.isAccessible()) {
      singleoneInstanceField.setAccessible(true);
    }
    return singleoneInstanceField.get(null);
  }

  static Object getTheUnsafe() throws ReflectiveOperationException {
    return getTheUnsafe(getUnsafeClass());
  }

  static void unmap(MappedByteBuffer buffer, Path path) throws IOException {
    // Java 9
    try {
      UNSAFE_INVOKE_CLEANER.invokeExact((ByteBuffer) buffer);
    } catch (RuntimeException e) {
      throw e;
    } catch (Error e) {
      throw e;
    } catch (Throwable e) {
      throw new UnmapFailedException(path.toString(), "could not unmap", e);
    }
  }
}
