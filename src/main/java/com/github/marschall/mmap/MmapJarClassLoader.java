package com.github.marschall.mmap;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
        return this.defineClass(name, resource);
      }
    }
    return null;
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    Objects.requireNonNull(name, "name");
    ByteArrayResource resource = this.findByteArrayResource(name);
    if (resource != null) {
      return this.defineClass(name, resource);
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
      return loader.findStringResource(name);
    } else if (loaders instanceof List) {
      for (Object each : (List<?>) loaders) {
        ResourceLoader loader = (ResourceLoader) each;
        URL resource = loader.findStringResource(name);
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
    // TODO Auto-generated method stub
    return super.getResources(name);
  }

  @Override
  public InputStream getResourceAsStream(String name) {
    Objects.requireNonNull(name, "name");
    Object loaders = this.resourceLoaders.get(name);
    if (loaders instanceof ResourceLoader) {
      ResourceLoader loader = (ResourceLoader) loaders;
      return loader.findStringResourceAsStream(name);
    } else if (loaders instanceof List) {
      for (Object each : (List<?>) loaders) {
        ResourceLoader loader = (ResourceLoader) each;
        InputStream resource = loader.findStringResourceAsStream(name);
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
    for (ResourceLoader resourceLoader : toClose) {
      resourceLoader.close();
    }

  }

}
