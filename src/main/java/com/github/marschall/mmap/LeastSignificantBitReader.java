package com.github.marschall.mmap;

import java.nio.MappedByteBuffer;

/**
 * Reads bits from a {@link MappedByteBuffer} using least-significant bit order.
 */
final class LeastSignificantBitReader {

  private final MappedByteBuffer buffer;
  private int byteOffset;
  private int bitOffset;
  private int currentByte;

  LeastSignificantBitReader(MappedByteBuffer buffer, int offset) {
    this.buffer = buffer;
    this.byteOffset = 0;
    this.bitOffset = -1;
  }

  void position(int byteOffset, int bitsRead) {
    if (byteOffset < 0) {
      throw new IllegalArgumentException();
    }
    if (bitsRead < 0 || bitsRead >= 8) {
      throw new IllegalArgumentException();
    }
    this.byteOffset = byteOffset;
    this.currentByte = Byte.toUnsignedInt(this.buffer.get(this.byteOffset));
    this.bitOffset = 7 - bitsRead;
  }

  int readBits(int len) {
    // TODO optimize
    if (len <= 0 || len > 31) {
      throw new IllegalArgumentException();
    }
    int bits = 0;

    for (int i = 0; i < len; i++) {
      if (this.bitOffset == -1) {
        this.bitOffset = 7;
        this.currentByte = Byte.toUnsignedInt(this.buffer.get(this.byteOffset));
        this.byteOffset += 1;
      }

      int bit = (this.currentByte & (1 << this.bitOffset)) >> this.bitOffset;
      // bit order is least-significant bit
      int shift = i;
      bits |= bit << shift;

      this.bitOffset -= 1;
    }

    return bits;
  }

}
