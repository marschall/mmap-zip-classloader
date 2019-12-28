package com.github.marschall.mmap;

import java.util.Objects;

abstract class PackageSubstring {

  // TODO Comparable to HashMap collisions?

  static boolean packageEquals(String className, String path, int length) {
    for (int i = 0; i < length; i++) {
      char c1 = className.charAt(i);
      char c2 = path.charAt(i);
      if (c1 != c2 && c1 != '.' && c2 != '/') {
        return false;
      }
    }
    return true;
  }

  /**
   * Lookup key class for a package of a class.
   * <p>
   * Exists to avoid having to allocate a substring from going from
   * {@code "com.github.marschall.mmap.PackageOfClass"} to
   * {@code "com/github/marschall/mmap"}.
   */
  static final class PackageOfClass extends PackageSubstring {

    final String className;
    final int length;

    PackageOfClass(String className) {
      Objects.requireNonNull(className, "className");
      this.className = className;
      this.length = className.lastIndexOf('.');
    }

    @Override
    public int hashCode() {
      int hash = 0;
      for (int i = 0; i < this.length; i++) {
        char c = this.className.charAt(i);
        if (c == '.') {
          c = '/';
        }
        hash = 31 * hash + c;
      }
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj instanceof PackageOfClass) {
        PackageOfClass other = (PackageOfClass) obj;
        if (this.length != other.length) {
          return false;
        }
        return this.className.regionMatches(0, other.className, 0, this.length);
      }
      if (obj instanceof PackageOfFile) {
        PackageOfFile other = (PackageOfFile) obj;
        if (this.length != other.length) {
          return false;
        }
        return PackageSubstring.packageEquals(this.className, other.path, this.length);
      }

      // no check for CharSequence as it can not be made reflexive

      return false;
    }

    @Override
    public String toString() {
      StringBuilder buffer = new StringBuilder(this.length);
      for (int i = 0; i < this.length; i++) {
        char c = this.className.charAt(i);
        if (c == '.') {
          c = '/';
        }
        buffer.append(c);
      }
      return buffer.toString();
    }

  }
  /**
   * Lookup key class for a package of a file.
   * <p>
   * Exists to avoid having to allocate a substring from going from
   * {@code "com/github/marschall/mmap/PackageOfClass.class"} to
   * {@code "com/github/marschall/mmap"}.
   */
  static final class PackageOfFile extends PackageSubstring {

    final String path;
    final int length;

    PackageOfFile(String path) {
      Objects.requireNonNull(path, "path");
      this.path = path;
      this.length = path.lastIndexOf('/');
    }



    @Override
    public int hashCode() {
      int hash = 0;
      for (int i = 0; i < this.length; i++) {
        hash = 31 * hash + this.path.charAt(i);
      }
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == this) {
        return true;
      }
      if (obj instanceof PackageOfFile) {
        PackageOfFile other = (PackageOfFile) obj;
        if (this.length != other.length) {
          return false;
        }
        return this.path.regionMatches(0, other.path, 0, this.length);
      }
      if (obj instanceof PackageOfClass) {
        PackageOfClass other = (PackageOfClass) obj;
        if (this.length != other.length) {
          return false;
        }
        return PackageSubstring.packageEquals(other.className, this.path, this.length);
      }

      // no check for CharSequence as it can not be made reflexive

      return false;
    }

    @Override
    public String toString() {
      if (this.length == -1) {
        // default package
        return "";
      }
      return this.path.substring(0, this.length);
    }

  }

}
