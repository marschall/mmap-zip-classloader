package com.github.marschall.mmap;

import java.util.Objects;

/**
 * Lookup key class for a package of a file.
 * <p>
 * Exists to avoid having to allocate a substring from going from
 * {@code "com/github/marschall/mmap/PackageOfClass.class"} to
 * {@code "com/github/marschall/mmap"}.
 */
final class PackageOfFile extends PackageSubstring {

  // TODO Comparable to HashMap collisions?

  private final String path;
  private final int length;

  PackageOfFile(String path) {
    Objects.requireNonNull(path, "path");
    this.path = path;
    this.length = path.lastIndexOf('/');
  }
  
  

}
