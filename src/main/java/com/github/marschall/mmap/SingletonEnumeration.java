package com.github.marschall.mmap;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Objects;

final class SingletonEnumeration<E> implements Enumeration<E> {
  
  private E element;
  
  SingletonEnumeration(E element) {
    Objects.requireNonNull(element, "element");
    this.element = element;
  }

  @Override
  public boolean hasMoreElements() {
    return this.element != null;
  }

  @Override
  public E nextElement() {
    if (this.element == null) {
      throw new NoSuchElementException();
    }
    E returnValue = this.element;
    this.element = null;
    return returnValue;
  }

}
