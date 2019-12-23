package com.github.marschall.mmap;

final class CompressionMethods {

  private CompressionMethods() {
    throw new AssertionError("not instantiable");
  }

  /**
   * The file is stored (no compression).
   */
  static final int STORE = 0;

  /**
   * The file is Shrunk
   */
  @Deprecated
  static final int SHRUNK = 1;

  /**
   * The file is Reduced with compression factor 1.
   */
  @Deprecated
  static final int  COMPRESSION_FACTOR_1 = 2;

  /**
   * The file is Reduced with compression factor 2.
   */
  @Deprecated
  static final int COMPRESSION_FACTOR_2 = 3;

  /**
   * The file is Reduced with compression factor 3.
   */
  @Deprecated
  static final int  COMPRESSION_FACTOR_3 = 4;

  /**
   * The file is Reduced with compression factor 4
   */
  @Deprecated
  static final int COMPRESSION_FACTOR_4 = 5;

  /**
   * The file is Imploded
   */
  @Deprecated
  static final int IMPLODE = 6;

  /**
   * Reserved for Tokenizing compression algorithm
   */
  static final int TOKENIZING = 7;

  /**
   * The file is Deflated
   */
  static final int DEFLATE = 8;

  /**
   * Enhanced Deflating using Deflate64(tm)
   */
  static final int DEFLATE_64 = 9;

  /**
   * PKWARE Data Compression Library Imploding (old IBM TERSE)
   */
  static final int PKWARE = 10;

  /**
   * Reserved by PKWARE
   */
  static final int RESERVED_1 = 11;

  /**
   * File is compressed using BZIP2 algorithm
   */
  static final int BZIP2 = 12;

  /**
   * Reserved by PKWARE.
   */
  static final int RESERVED_2 = 13;

  /**
   * LZMA
   */
  static final int LZMA = 14;

  /**
   * Reserved by PKWARE
   */
  static final int RESERVED_3 = 15;

  /**
   * IBM z/OS CMPSC Compression
   */
  static final int CMPSC = 16;

  /**
   * Reserved by PKWARE
   */
  static final int RESERVED_4 = 17;

  /**
   * File is compressed using IBM TERSE (new)
   */
  static final int TERSE = 18;

  /**
   * IBM LZ77 z Architecture (PFS)
   */
  static final int LZ77 = 19;

  /**JPEG variant
   *
   */
  static final int JPEG = 96;

  /**
   * WavPack compressed data
   */
  static final int WAV_PACK = 97;

  /**
   * PPMd version I, Rev 1
   */
  static final int PPMd = 98;

  /**
   * AE-x encryption marker (see APPENDIX E)
   */
  static final int AEX = 99;

}
