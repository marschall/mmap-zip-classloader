package com.github.marschall.mmap;

final class HuffmanTree {

  private final byte[] codes;
  private final byte[] symbols;

  HuffmanTree(byte[] codes, byte[] symbols) {
    this.codes = codes;
    this.symbols = symbols;
  }
  
  static HuffmanTree on(byte[] lengths) {
    return new HuffmanTree(new byte[0], new byte[0]);
  }

  void decode(LeastSignificantBitReader bitReader) {
    // TODO
  }

}
