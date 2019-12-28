package com.github.marschall.mmap;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class JarReaderTest {

  @Test
  void roundUpToNextPowerOfTwo() {
    assertEquals(8, JarReader.roundUpToNextPowerOfTwo(5));
    assertEquals(8, JarReader.roundUpToNextPowerOfTwo(6));
    assertEquals(8, JarReader.roundUpToNextPowerOfTwo(7));
    assertEquals(8, JarReader.roundUpToNextPowerOfTwo(8));
  }
  
  @Test
  void ensureBufferSize() {
    byte[] buffer = new byte[8];
    
    byte[] ensured = JarReader.ensureBufferSize(buffer, 7);
    assertSame(buffer, ensured);
    
    ensured = JarReader.ensureBufferSize(buffer, 8);
    assertSame(buffer, ensured);
    
    ensured = JarReader.ensureBufferSize(buffer, 9);
    assertNotSame(buffer, ensured);
    assertEquals(16, ensured.length);
  }

}
