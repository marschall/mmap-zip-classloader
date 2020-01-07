package com.github.marschall.mmap;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NibbleArrayTest {

  @Test
  void get() {
    byte[] array = new byte[(0b1111 / 2) + 1];
    for (int i = 0; i < 0b10000; i += 2) {
      array[i / 2] = (byte) ((i << 4) | (i + 1));
    }
    NibbleArray nibbleArray = new NibbleArray(array);
    for (int i = 0; i < 0b10000; i++) {
      assertEquals(i, nibbleArray.get(i));
    }
  }

  @Test
  void set() {
    NibbleArray nibbleArray = new NibbleArray(0b1000);
    for (int i = 0; i < 0b111; i++) {
      nibbleArray.set(i, i);
    }
    for (int i = 0; i < 0b111; i++) {
      assertEquals(i, nibbleArray.get(i));
    }
  }
  
  @Test
  void overwrite() {
    NibbleArray nibbleArray = new NibbleArray(0b1000);
    for (int i = 0; i < 0b111; i++) {
      nibbleArray.set(i, 0b111);
    }
    for (int i = 0; i < 0b111; i++) {
      nibbleArray.set(i, i);
    }
    for (int i = 0; i < 0b111; i++) {
      assertEquals(i, nibbleArray.get(i));
    }
  }

}
