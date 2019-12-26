package com.github.marschall.mmap;

final class HostSystem {

  private HostSystem() {
    throw new AssertionError("not instantiable");
  }

  /**
   * MS-DOS and OS/2 (FAT / VFAT / FAT32 file systems)
   */
  static final int DOS = 0;

  /**
   * Amiga
   */
  static final int AMIGA = 1;

  /**
   * OpenVMS
   */
  static final int OPEN_VMS = 2;

  /**
   * UNIX
   */
  static final int UNIX = 3;

  /**
   * VM/CMS
   */
  static final int VM_CMS = 4;

  /**
   * Atari ST
   */
  static final int ATARI = 5;

  /**
   * OS/2 H.P.F.S.
   */
  static final int OS_2 = 6;

  /**
   * Macintosh
   */
  static final int MACINTOSH = 7;

  /**
   * Z-System
   */
  static final int Z_SYSTEM = 8;

  /**
   * CP/M
   */
  static final int CP_M = 9;

  /**
   * Windows NTFS
   */
  static final int WINDOWS_NTFS = 10;

  /**
   * MVS (OS/390 - Z/OS)
   */
  static final int MVS = 11;

  /**
   * VSE
   */
  static final int VSE = 12;

  /**
   * Acorn Risc
   */
  static final int ACORN = 13;

  /**
   * VFAT
   */
  static final int VFAT = 14;

  /**
   * alternate MVS
   */
  static final int ALTERNATE_MVS = 15;

  /**
   * BeOS
   */
  static final int BE_OS = 16;

  /**
   * Tandem
   */
  static final int TANDEM = 17;

  /**
   * OS/400
   */
  static final int OS_400 = 18;

  /**
   * OS X (Darwin)
   */
  static final int OS_X = 19;

}
