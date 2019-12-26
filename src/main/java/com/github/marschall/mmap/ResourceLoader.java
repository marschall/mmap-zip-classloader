package com.github.marschall.mmap;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;

final class ResourceLoader {

  private final MappedByteBuffer buffer;
  private final Path path;
  private final Map<String, CentralDirectoryHeader> headerMap;

  ResourceLoader(Path path, MappedByteBuffer buffer, List<CentralDirectoryHeader> headers) {
    this.path = path;
    this.buffer = buffer;
    this.headerMap = buildHeaderMap(headers);
  }

  private static Map<String, CentralDirectoryHeader> buildHeaderMap(List<CentralDirectoryHeader> headers) {
    return null;
  }

  ByteArrayResource findByteArrayResource(String path) {
    return null;
  }

  InputStream findResourceAsStream(String path) {
    return null;
  }

  URL findResource(String path) {
    return null;
  }

  void close() throws IOException {
    Unmapper.unmap(buffer, this.path);
  }

}
