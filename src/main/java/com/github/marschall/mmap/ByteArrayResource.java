package com.github.marschall.mmap;

final class ByteArrayResource {

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

}
