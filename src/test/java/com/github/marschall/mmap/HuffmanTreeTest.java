package com.github.marschall.mmap;

import org.junit.jupiter.api.Test;

class HuffmanTreeTest {

  @Test
  void test() {
    byte[] lengths = new byte[] {3, 3, 3, 3, 3, 2, 4, 4};
    HuffmanTree tree = HuffmanTree.on(lengths);
  }

}
