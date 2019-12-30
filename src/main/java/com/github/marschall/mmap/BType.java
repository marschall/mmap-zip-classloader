package com.github.marschall.mmap;

final class BType {

  private BType() {
    throw new AssertionError("not instantiable");
  }
  
  /**
   * no compression
   */
  static final int NO_COMPRESSION = 0b00;
  
  /**
   * compressed with fixed Huffman codes
   */
  static final int COMPRESSED_FIXED = 0b01;
  
  /**
   * compressed with dynamic Huffman codes
   */
  static final int COMPRESSED_DYNAMIC = 0b10;
  /**
   * reserved (error)
   */
  static final int RESERVED = 0b11;

}
