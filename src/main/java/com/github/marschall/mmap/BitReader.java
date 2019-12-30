package com.github.marschall.mmap;

import java.nio.MappedByteBuffer;

final class BitReader {

  private final MappedByteBuffer buffer;
  private int byteOffset;
  private int bitOffset;

  BitReader(MappedByteBuffer buffer, int offset) {
    this.buffer = buffer;
    this.byteOffset = offset;
    this.bitOffset = 0;
  }

  int readBits(int len) {
    int bits = 0;

    return bits;
  }

}
