package com.github.marschall.mmap;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

class JBossLoggingTest {
  
  @Test
  void loadClass() throws IOException {
    String home = System.getProperty("user.home");
    Path jarPath = Paths.get(home + "/.m2/repository/org/jboss/logging/jboss-logging/3.4.0.Final/jboss-logging-3.4.0.Final.jar");
    String zipPath = "org/jboss/logging/Logger.class";
    ZipReader reader = ZipReader.on(jarPath);
    try {
      int endOfCentralDirectoryRecordOffset = reader.findEndOfCentralDirectoryRecord();

      EndOfCentralDirectoryRecord endOfCentralDirectoryRecord = reader.readEndOfCentralDirectoryRecord(endOfCentralDirectoryRecordOffset);
      if (!endOfCentralDirectoryRecord.isEmpty()) {
        List<CentralDirectoryHeader> headers = reader.readCentralDirectoryRecord(endOfCentralDirectoryRecord);
        for (CentralDirectoryHeader header : headers) {
          if (header.getFileName().equals(zipPath)) {
            int uncompressedSize = header.getUncompressedSize();
            byte[] buffer = new byte[uncompressedSize];
            reader.readFile(header, buffer);
            System.out.println(buffer);
          }
        }
      }
    } finally {
      reader.close();
    }
  }

}
