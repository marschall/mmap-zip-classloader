package com.github.marschall.mmap;

final class ByteArrayResource {
  
  // java.lang.ClassLoader.defineClass(String, ByteBuffer, ProtectionDomain)
  // requires a heap ByteBuffer so there is no point in using a ByteBuffer

  private final byte[] array;
  private final int offset;
  private final int length;

  ByteArrayResource(byte[] array, int offset, int length) {
    this.array = array;
    this.offset = offset;
    this.length = length;
  }

  byte[] getByteArray() {
    return this.array;
  }

  int getOffset() {
    return this.offset;
  }

  int getLength() {
    return this.length;
  }
  
  void release() {
    // TODO return to pool
  }

}
