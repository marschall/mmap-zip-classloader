package com.github.marschall.mmap;

final class GeneralPurposeBitFlag {

  private GeneralPurposeBitFlag() {
    throw new AssertionError("not instantiable");
  }

  /**
   * Bit 0
   * 
   * If set, indicates that the file is encrypted.
   */
  static boolean isEncrypted(int flag) {
    return (flag & 0b1) == 1;
  }

  /**
   * Bit 3
   * 
   * If this bit is set, the fields crc-32, compressed 
   * size and uncompressed size are set to zero in the 
   * local header.  The correct values are put in the 
   * data descriptor immediately following the compressed
   * data.
   */
  static boolean areFieldsZeroInLocalHeader(int flag) {
    return (flag & 0b1000) == 1;
  }

  /**
   * Bit 6
   * 
   * Strong encryption. If this bit is set, you MUST
   * set the version needed to extract value to at least
   * 50 and you MUST also set bit 0.  If AES encryption
   * is used, the version needed to extract value MUST 
   * be at least 51. See the section describing the Strong
   * Encryption Specification for details.
   */
  static boolean isStrongEncryption(int flag) {
    return (flag & 0b10000000) == 1;
  }

  /**
   * Bit 11
   * 
   * Language encoding flag (EFS).  If this bit is set,
   * the filename and comment fields for this file
   * MUST be encoded using UTF-8.
   */
  static boolean isUtf8(int flag) {
    return (flag & 0b100000000000) == 1;
  }

}
