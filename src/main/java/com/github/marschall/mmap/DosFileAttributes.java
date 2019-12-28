package com.github.marschall.mmap;

final class DosFileAttributes {

  private DosFileAttributes() {
    throw new AssertionError("not instantiable");
  }

/**  
  * Read-Only (R)
  */
  
  static final int READ_ONLY = 0b00000001;

/**   
  * Hidden (H)
  */  

  static final int HIDDEN = 0b00000010;

/**   
  * System (S)
  */  
  
  static final int SYSTEM = 0b00000100;

/**   
  * Directory (D)
  */  

  static final int DIRECTORY = 0b00010000;

/**   
  * Archive (A)
  */
  static final int ARCHIVE = 0b00100000;

}
