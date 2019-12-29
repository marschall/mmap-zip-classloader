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
import com.github.marschall.mmap.PackageSubstring.PackageOfClass;
import com.github.marschall.mmap.PackageSubstring.PackageOfFile;

public final class MmapJarClassLoader extends ClassLoader implements Closeable {

  static {
    registerAsParallelCapable();
  }

  // TODO support directories as well

  // TODO check opening folders as resources

  // TODO custom map

  // expert
  // - byte[] pool

  private final Map<PackageSubstring, Object> resourceLoaders;

  public MmapJarClassLoader(String name, ClassLoader parent, File[] jarFiles) {
    super(name, parent);
    this.resourceLoaders = buildResourceLoaderMap(jarFiles);
  }

  private static Map<PackageSubstring, Object> buildResourceLoaderMap(File[] jarFiles) {
    return null;
  }

  static Set<String> getPackages(Set<String> packages, List<CentralDirectoryHeader> headers) {
    for (CentralDirectoryHeader header : headers) {
      if (header.isClass()) {
        packages.add(header.getPackageName());
      }
    }
    return packages;
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
    Object loaders = this.resourceLoaders.get(new PackageOfClass(className));
    if (loaders instanceof ZipResourceLoader) {
      ZipResourceLoader loader = (ZipResourceLoader) loaders;
      return loader.findByteArrayResource(path);
    } else if (loaders instanceof List) {
      for (Object each : (List<?>) loaders) {
        ZipResourceLoader loader = (ZipResourceLoader) each;
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
    Object loaders = this.resourceLoaders.get(new PackageOfFile(name));
    if (loaders instanceof ZipResourceLoader) {
      ZipResourceLoader loader = (ZipResourceLoader) loaders;
      return loader.findResource(name);
    } else if (loaders instanceof List) {
      for (Object each : (List<?>) loaders) {
        ZipResourceLoader loader = (ZipResourceLoader) each;
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
    Object loaders = this.resourceLoaders.get(new PackageOfFile(name));
    if (loaders instanceof ZipResourceLoader) {
      ZipResourceLoader loader = (ZipResourceLoader) loaders;
      URL resource = loader.findResource(name);
      if (resource != null) {
        return new SingletonEnumeration<URL>(resource);
      } else {
        return Collections.emptyEnumeration();
      }
    } else if (loaders instanceof List) {
      List<?> loaderList = (List<?>) loaders;
      List<URL> resources = new ArrayList<>(loaderList.size());
      for (Object each : loaderList) {
        ZipResourceLoader loader = (ZipResourceLoader) each;
        URL resource = loader.findResource(name);
        if (resource != null) {
          resources.add(resource);
        }
      }
      return Collections.enumeration(resources);
    } else {
      throw new IllegalStateException("corrupted object state");
    }
  }

  @Override
  public InputStream getResourceAsStream(String name) {
    Objects.requireNonNull(name, "name");
    Object loaders = this.resourceLoaders.get(new PackageOfFile(name));
    if (loaders instanceof ZipResourceLoader) {
      ZipResourceLoader loader = (ZipResourceLoader) loaders;
      return loader.findResourceAsStream(name);
    } else if (loaders instanceof List) {
      for (Object each : (List<?>) loaders) {
        ZipResourceLoader loader = (ZipResourceLoader) each;
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
    Set<ZipResourceLoader> toClose = new HashSet<>();
    for (Object loaders : this.resourceLoaders.values()) {
      if (loaders instanceof ZipResourceLoader) {
        toClose.add((ZipResourceLoader) loaders);
      } else if (loaders instanceof List) {
        for (Object each : (List<?>) loaders) {
          toClose.add((ZipResourceLoader) each);
        }
      }
    }
    List<IOException> caughtExceptions = new ArrayList<>();
    for (ZipResourceLoader resourceLoader : toClose) {
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
