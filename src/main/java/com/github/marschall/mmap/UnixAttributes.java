package com.github.marschall.mmap;

final class UnixAttributes {

  private UnixAttributes() {
    throw new AssertionError("not instantiable");
  }

  /**
   * named pipe (fifo)
   */
  static final int S_IFIFO = 0010000;

  /**
   * character special
   */
  static final int S_IFCHR = 0020000;

  /**
   * directory
   */
  static final int S_IFDIR = 0040000;

  /**
   * block special
   */
  static final int S_IFBLK = 0060000;

  /**
   * regular
   */
  static final int S_IFREG = 0100000;

  /**
   * symbolic link
   */
  static final int S_IFLNK = 0120000;

  /**
   * socket
   */
  static final int S_IFSOCK = 0140000;

}
