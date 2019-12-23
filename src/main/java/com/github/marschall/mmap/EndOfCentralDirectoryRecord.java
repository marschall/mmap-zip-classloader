package com.github.marschall.mmap;

final class EndOfCentralDirectoryRecord {

  private final int offset;
  private final int numberOfRecords;
  private final int centralDirectoryRecordSize;

  EndOfCentralDirectoryRecord(int offset, int numberOfRecords, int centralDirectoryRecordSize) {
    this.offset = offset;
    this.numberOfRecords = numberOfRecords;
    this.centralDirectoryRecordSize = centralDirectoryRecordSize;
  }

  int getOffset() {
    return this.offset;
  }

  int getNumberOfRecords() {
    return this.numberOfRecords;
  }

  int getCentralDirectoryRecordSize() {
    return this.centralDirectoryRecordSize;
  }

  boolean isEmpty() {
    return this.numberOfRecords == 0;
  }

}