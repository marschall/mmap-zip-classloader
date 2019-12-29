package com.github.marschall.mmap;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipException;

final class ZipResourceLoader {

  // TODO better key
  private final Map<String, CentralDirectoryHeader> headerMap;
  private ZipReader zipReader;

  ZipResourceLoader(ZipReader zipReader) throws ZipException {
    this.zipReader = zipReader;
    this.headerMap = buildHeaderMap(zipReader);
  }

  private static Map<String, CentralDirectoryHeader> buildHeaderMap(ZipReader zipReader) throws ZipException {
    int endOfCentralDirectoryRecordOffset = zipReader.findEndOfCentralDirectoryRecord();
    EndOfCentralDirectoryRecord endOfCentralDirectoryRecord = zipReader.readEndOfCentralDirectoryRecord(endOfCentralDirectoryRecordOffset);
    
    if (endOfCentralDirectoryRecord.isEmpty()) {
      return Map.of();
    }
    List<CentralDirectoryHeader> headers = zipReader.readCentralDirectoryRecord(endOfCentralDirectoryRecord);
    Map<String, CentralDirectoryHeader> headerMap = new HashMap<>();
    for (CentralDirectoryHeader header : headers) {
      headerMap.put(header.getFileName(), header);
    }
    return headerMap;
  }

  ByteArrayResource findByteArrayResource(String path) {
    CentralDirectoryHeader header = this.headerMap.get(path);
    if (header == null) {
      return null;
    }
    byte[] file = this.zipReader.readFile(header);
    return new ByteArrayResource(file, 0, file.length);
  }

  InputStream findResourceAsStream(String path) {
    return null;
  }

  URL findResource(String path) {
    return null;
  }

  void close() throws IOException {
    this.zipReader.close();
  }

}
