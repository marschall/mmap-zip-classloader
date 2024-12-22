package com.github.marschall.mmap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

class SpringBootLoggingTests {

  @Test
  void loadClass() throws IOException {
    String home = System.getProperty("user.home");
    Path jarPath = Paths.get(home + "/git/spring-petclinic/target/spring-petclinic-3.4.0-SNAPSHOT.jar");
    ZipReader reader = ZipReader.on(jarPath);
    try {
      int endOfCentralDirectoryRecordOffset = reader.findEndOfCentralDirectoryRecord();

      EndOfCentralDirectoryRecord endOfCentralDirectoryRecord = reader.readEndOfCentralDirectoryRecord(endOfCentralDirectoryRecordOffset);
      if (!endOfCentralDirectoryRecord.isEmpty()) {
        List<CentralDirectoryHeader> headers = reader.readCentralDirectoryRecord(endOfCentralDirectoryRecord);
        CentralDirectoryHeader[] jars = headers.stream()
          .filter(header -> header.getFileName().endsWith(".jar"))
          .toArray(CentralDirectoryHeader[]::new);
        for (CentralDirectoryHeader jar : jars) {
          assertEquals(CompressionMethods.STORE, jar.getCompressionMethod(), "expect JARs to be uncompressed");
        }
      }
    } finally {
      reader.close();
    }
  }

}
