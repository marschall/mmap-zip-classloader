package com.github.marschall.mmap;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.file.Path;
import java.util.List;

final class ResourceLoader {

  private final MappedByteBuffer buffer;
  private final Path path;

  ResourceLoader(Path path, MappedByteBuffer buffer, List<CentralDirectoryHeader> headers) {
    this.path = path;
    this.buffer = buffer;
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
