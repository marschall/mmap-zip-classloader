package com.github.marschall.mmap;

import java.util.Objects;

/**
 * Lookup key class for a package of a class.
 * <p>
 * Exists to avoid having to allocate a substring from going from
 * {@code "com.github.marschall.mmap.PackageOfClass"} to
 * {@code "com/github/marschall/mmap"}.
 */
final class PackageOfClass implements CharSequence {
  
  // TODO Comparable to HashMap collisions?

  private final String className;
  private final int length;

  PackageOfClass(String className) {
    Objects.requireNonNull(className, "className");
    this.className = className;
    this.length = className.lastIndexOf('/');
  }

  @Override
  public int length() {
    return this.length;
  }

  @Override
  public char charAt(int index) {
    if (index < 0 || index >= this.length) {
      throw new IndexOutOfBoundsException(index);
    }
    return this.className.charAt(index);
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    for (int i = 0; i < this.length; i++) {
      hash = 31 * hash + this.className.charAt(i);
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
    
    if (obj instanceof CharSequence) {
      CharSequence other = (CharSequence) obj;
      if (this.length != other.length()) {
        return false;
      }
      for (int i = 0; i < this.length; i++) {
        if (this.className.charAt(i) != other.charAt(i)) {
          return false;
        }
      }
      return true;
    }
    
    return false;
  }

}
