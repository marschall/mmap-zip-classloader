package com.github.marschall.mmap;

final class CentralDirectoryHeader {

  private final int crc32;
  private final String fileName;
  private final int compressionMethod; // TODO short?
  private final int compressedSize;
  private final int uncompressedSize;
  private final int localHeaderOffset;

  CentralDirectoryHeader(String fileName, int compressionMethod, int crc32, int compressedSize, int uncompressedSize, int localHeaderOffset) {
    this.fileName = fileName;
    this.compressionMethod = compressionMethod;
    this.crc32 = crc32;
    this.compressedSize = compressedSize;
    this.uncompressedSize = uncompressedSize;
    this.localHeaderOffset = localHeaderOffset;
  }

  int getCrc32() {
    return this.crc32;
  }

  String getFileName() {
    return this.fileName;
  }

  int getCompressionMethod() {
    return this.compressionMethod;
  }

  int getCompressedSize() {
    return this.compressedSize;
  }

  int getUncompressedSize() {
    return this.uncompressedSize;
  }

  int getLocalHeaderOffset() {
    return this.localHeaderOffset;
  }

  boolean isClass() {
    return fileName.endsWith(".class")
        // filename is not just .class or path/.class
        && (fileName.length() > 6 && fileName.charAt(fileName.length() - 7) != '/');
  }

  String getPackageName() {
    // TODO subsequence to reduce allocation
    return fileName.substring(0, fileName.length() - 7);
  }
  
  @Override
  public String toString() {
    return this.fileName.toString();
  }

}
