package com.github.marschall.mmap;

final class HeaderIds {

  private HeaderIds() {
    throw new AssertionError("not instantiable");
  }

  /**
   * Zip64 extended information extra field
   */
  static final int ZIP_64 = 0x0001;
  
  /**
   * AV Info
   */
  static final int AV_INFO = 0x0007;
  
  /**
   * Reserved for extended language encoding data (PFS)
   */
  static final int EXTENDED_LANGUAGE_ENCODING_DATA = 0x0008;
  
  /**
   * OS/2
   */
  static final int OS_2 = 0x0009;
  
  /**
   * NTFS
   */
  static final int NTFS = 0x000a;
  
  /** OpenVMS */
  static final int Open_VMS = 0x000c;
  
  /** UNIX */
  static final int UNIX = 0x000d;
  
  /**
   * Reserved for file stream and fork descriptors
   */
  static final int FILE_STREAM_AND_FORK_DESCRIPTOR = 0x000e;
  
  /**
   * Patch Descriptor
   */
  static final int PATCH_DESCRIPTOR = 0x000f;
  
  /**
   * PKCS#7 Store for X.509 Certificates
   */
  static final int PKCS7_STORE = 0x0014;
  
  /**
   * X.509 Certificate ID and Signature for individual file
   */
  static final int X509_CERTIFICATE_FOR_FILE = 0x0015;
  
  /**
   * X.509 Certificate ID for Central Directory
   */
  static final int X509_CERTIFICATE_FOR_CENTRAL_DIRECTORY = 0x0016;
  
  /**
   * Strong Encryption Header
   */
  static final int STRONG_ENCRYPTION_HEADER = 0x0017;
  
  /**
   * Record Management Controls
   */
  static final int RECORD_MANAGEMENT_CONTROL = 0x0018;
  
  /**
   * PKCS#7 Encryption Recipient Certificate List
   */
  static final int PKCS7_ENCRYPTION_RECIPIENT_CERTIFICATE_LIST = 0x0019;
  
  /**
   * Reserved for Timestamp record
   */
  static final int TIMESTAMP_RECORD = 0x0020;
  
  /**
   * Policy Decryption Key Record
   */
  static final int POLICY_DECRYPTION_KEY_RECORD = 0x0021;
  
  /**
   * Smartcrypt Key Provider Record
   */
  static final int SMARTCRYPT_KEY_PROVIDER_RECORD = 0x0022;
  
  /**
   * Smartcrypt Policy Key Data Record
   */
  static final int SMARTCRYPT_POLICY_KEY_DATA_RECORD = 0x0023;
  
  /**
   * IBM S/390 (Z390), AS/400 (I400) attributes - uncompressed
   */
  static final int IBM_UNCOMPRESSED = 0x0065;
  
  /**
   * Reserved for IBM S/390 (Z390), AS/400 (I400) attributes - compressed
   */
  static final int IBM_COMPRESSED = 0x0066;
  
  /**
   * POSZIP 4690 (reserved)
   */
  static final int POSZIP_4690 = 0x4690;

}
