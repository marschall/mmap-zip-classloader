package com.github.marschall.mmap;

final class MinimumFeatureVersion {

  private MinimumFeatureVersion() {
    throw new AssertionError("not instantiable");
  }

  /**
   * Default value
   */
  static final int DEFAULT_VALUE = 10;

  /**
   * File is a volume label
   */
  static final int VOLUME_LABEL = 11;

  /**
   * File is a folder (directory)
   */
  static final int FOLDER = 20;

  /**
   * File is compressed using Deflate compression
   */
  static final int COMPRESSED_DEFLATE = 20;

  /**
   * File is encrypted using traditional PKWARE encryption
   */
  static final int ENCRYPTED_PKWARE_TRADITIONAL = 20;

  /**
   * File is compressed using Deflate64(tm)
   */
  static final int COMPRESSED_DEFLATE64 = 21;

  /**
   * File is compressed using PKWARE DCL Implode
   */
  static final int COMPRESSED_PKWARE_IMPLODE = 25;

  /**
   * File is a patch data set
   */
  static final int PATCH_DATA_SET = 27;

  /**
   * File uses ZIP64 format extensions
   */
  static final int ZIP64 = 45;

  /**
   * File is compressed using BZIP2 compression
   */
  static final int COMPRESSED_BZIP2 = 46;

  /**
   * File is encrypted using DES
   */
  static final int ENCRYPTED_DES = 50;

  /**
   * File is encrypted using 3DES
   */
  static final int ENCRYPTED_3DES = 50;

  /**
   * File is encrypted using original RC2 encryption
   */
  static final int ENCRYPTED_RC2_ORIGINAL = 50;

  /**
   * File is encrypted using RC4 encryption
   */
  static final int ENCRYPTED_RC4 = 50;

  /**
   * File is encrypted using AES encryption
   */
  static final int ENCRYPTED_AES = 51;

  /**
   * File is encrypted using corrected RC2 encryption
   */
  static final int ENCRYPTED_RC2_CORRECTED = 51;

  /**
   * File is encrypted using corrected RC2-64 encryption
   */
  static final int ENCRYPTED_RC2_64_CORRECTED = 52;

  /**
   * File is encrypted using non-OAEP key wrapping
   */
  static final int ENCRYPTED_NON_OAEP = 61;

  /**
   * Central directory encryption
   */
  static final int CENTRAL_DIRECTORY_ENCRYPTION = 62;

  /**
   * File is compressed using LZMA
   */
  static final int COMPRESSED_LZMA = 63;

  /**
   * File is compressed using PPMd+
   */
  static final int COMPRESSED_PPMD = 63;

  /**
   * File is encrypted using Blowfish
   */
  static final int ENCRYPTED_BLOWFISH = 63;

  /**
   * File is encrypted using Twofish
   */
  static final int ENCRYPTED_TWOFISH = 63;

}
