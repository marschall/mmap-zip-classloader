package com.github.marschall.mmap;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MmapJarClassLoaderTest {

  @Test
  void getResourceName() {
    assertEquals("com/github/marschall/mmap/MmapJarClassLoader.class", MmapJarClassLoader.getResourceName("com.github.marschall.mmap.MmapJarClassLoader"));
  }

}
