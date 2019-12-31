package com.github.marschall.mmap;

import java.util.Objects;

final class NibbleArray {

  private final byte[] array;

  NibbleArray(int size) {
    if (size < 0) {
      throw new IllegalArgumentException();
    }
    this.array = new byte[size >> 1];
  }

  NibbleArray(byte[] array) {
    Objects.requireNonNull(array, "array");
    this.array = array;
  }

  int get(int index) {
    int value = Byte.toUnsignedInt(this.array[index >> 1]);
    int shift = (1 - index & 0b1) << 2;
    int mask = 0b1111 << shift;
    int nibble = value & mask;
    return nibble >>> shift;
  }

  void set(int index, int value) {
    if (value > 0b111) {
      throw new IllegalArgumentException();
    }
    int arrayIndex = index >> 1;
    int current = Byte.toUnsignedInt(this.array[arrayIndex]);
    int shift = (1 - index & 0b1) << 2;
    int mask = 0b1111 << shift;

    // mask out the current value
    current = current & ~mask;

    int newValue = current | (value << shift);
    this.array[arrayIndex] = (byte) newValue;
  }

}
