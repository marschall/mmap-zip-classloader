package com.github.marschall.mmap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class ZipReaderTest {

  @Test
  void roundUpToNextPowerOfTwo() {
    assertEquals(8, ZipReader.roundUpToNextPowerOfTwo(5));
    assertEquals(8, ZipReader.roundUpToNextPowerOfTwo(6));
    assertEquals(8, ZipReader.roundUpToNextPowerOfTwo(7));
    assertEquals(8, ZipReader.roundUpToNextPowerOfTwo(8));
  }
  
  @Test
  void ensureBufferSize() {
    byte[] buffer = new byte[8];
    
    byte[] ensured = ZipReader.ensureBufferSize(buffer, 7);
    assertSame(buffer, ensured);
    
    ensured = ZipReader.ensureBufferSize(buffer, 8);
    assertSame(buffer, ensured);
    
    ensured = ZipReader.ensureBufferSize(buffer, 9);
    assertNotSame(buffer, ensured);
    assertEquals(16, ensured.length);
  }

}
