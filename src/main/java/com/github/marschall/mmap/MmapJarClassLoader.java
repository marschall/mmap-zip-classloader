package com.github.marschall.mmap;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class MmapJarClassLoader extends ClassLoader implements Closeable {

  static {
    registerAsParallelCapable();
  }

  // TODO support directories as well

  // TODO check opening folders as resources

  // TODO CharSequence and custom map
  
  // expert
  // - byte[] pool
  // - ByteBuffer to String

  private final Map<String, Object> resourceLoaders;

  public MmapJarClassLoader(String name, ClassLoader parent, File[] jarFiles) {
    super(name, parent);
    this.resourceLoaders = buildResourceLoaderMap(jarFiles);
  }

  private static Map<String, Object> buildResourceLoaderMap(File[] jarFiles) {
    return null;
  }

  @Override
  protected Class<?> findClass(String moduleName, String name) {
    Objects.requireNonNull(name, "name");
    if (moduleName == null) {
      ByteArrayResource resource = this.findByteArrayResource(name);
      if (resource != null) {
        try {
          return this.defineClass(name, resource);
        } finally {
          resource.release();
        }
      }
    }
    return null;
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    Objects.requireNonNull(name, "name");
    ByteArrayResource resource = this.findByteArrayResource(name);
    if (resource != null) {
      try {
        return this.defineClass(name, resource);
      } finally {
        resource.release();
      }
    } else {
      throw new ClassNotFoundException(name);
    }
  }

  private Class<?> defineClass(String className, ByteArrayResource resource) {
    return this.defineClass(className, resource.getByteArray(), resource.getOffset(), resource.getLength());
  }

  private ByteArrayResource findByteArrayResource(String className) {
    String path = getResourceName(className);
    Object loaders = this.resourceLoaders.get(path);
    if (loaders instanceof ResourceLoader) {
      ResourceLoader loader = (ResourceLoader) loaders;
      return loader.findByteArrayResource(path);
    } else if (loaders instanceof List) {
      for (Object each : (List<?>) loaders) {
        ResourceLoader loader = (ResourceLoader) each;
        ByteArrayResource resource = loader.findByteArrayResource(path);
        if (resource != null) {
          return resource;
        }
      }
    }
    return null;
  }

  @Override
  public URL getResource(String name) {
    Objects.requireNonNull(name, "name");
    Object loaders = this.resourceLoaders.get(name);
    if (loaders instanceof ResourceLoader) {
      ResourceLoader loader = (ResourceLoader) loaders;
      return loader.findResource(name);
    } else if (loaders instanceof List) {
      for (Object each : (List<?>) loaders) {
        ResourceLoader loader = (ResourceLoader) each;
        URL resource = loader.findResource(name);
        if (resource != null) {
          return resource;
        }
      }
    }
    return null;
  }

  @Override
  public Enumeration<URL> getResources(String name) throws IOException {
    Objects.requireNonNull(name, "name");
    List<URL> resources = new ArrayList<>();
    Object loaders = this.resourceLoaders.get(name);
    if (loaders instanceof ResourceLoader) {
      ResourceLoader loader = (ResourceLoader) loaders;
      URL resource = loader.findResource(name);
      if (resource != null) {
        resources.add(resource);
      }
    } else if (loaders instanceof List) {
      for (Object each : (List<?>) loaders) {
        ResourceLoader loader = (ResourceLoader) each;
        URL resource = loader.findResource(name);
        if (resource != null) {
          resources.add(resource);
        }
      }
    }
    return Collections.enumeration(resources);
  }

  @Override
  public InputStream getResourceAsStream(String name) {
    Objects.requireNonNull(name, "name");
    Object loaders = this.resourceLoaders.get(name);
    if (loaders instanceof ResourceLoader) {
      ResourceLoader loader = (ResourceLoader) loaders;
      return loader.findResourceAsStream(name);
    } else if (loaders instanceof List) {
      for (Object each : (List<?>) loaders) {
        ResourceLoader loader = (ResourceLoader) each;
        InputStream resource = loader.findResourceAsStream(name);
        if (resource != null) {
          return resource;
        }
      }
    }
    return null;
  }

  static String getResourceName(String className) {
    return className.replace('.', '/') + ".class";
  }

  @Override
  public void close() throws IOException {
    // TODO nop on second execution
    Set<ResourceLoader> toClose = new HashSet<>();
    for (Object loaders : this.resourceLoaders.values()) {
      if (loaders instanceof ResourceLoader) {
        toClose.add((ResourceLoader) loaders);
      } else if (loaders instanceof List) {
        for (Object each : (List<?>) loaders) {
          toClose.add((ResourceLoader) each);
        }
      }
    }
    List<IOException> caughtExceptions = new ArrayList<>();
    for (ResourceLoader resourceLoader : toClose) {
      // even if one unmap() fails make sure we try to unmap() all
      try {
        resourceLoader.close();
      } catch (IOException e) {
        caughtExceptions.add(e);
      }
    }
    if (!caughtExceptions.isEmpty()) {
      IOException exception = new IOException("failed to close class loader");
      for (IOException caught : caughtExceptions) {
        exception.addSuppressed(caught);
      }
      throw exception;
    }
  }

}
