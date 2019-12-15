package com.github.marschall.mmap;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

final class JarScanner3 {

  // 0x06054b50
  private static final byte[] END_OF_CENTRAL_DIR_SIGNATURE = new byte[]{0x50, 0x4b, 0x05, 0x06};

  public void scan(Path p) throws IOException {
    try (FileChannel channel = FileChannel.open(p, StandardOpenOption.READ)) {
      long size = channel.size();
      if (size > Integer.MAX_VALUE) {
        throw new IllegalStateException("unsupported size: " + size);
      }
      // TODO free
      MappedByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0L, size);
      System.out.println(this.findEndOfCentralDirectoryRecord(buffer, (int) size));
    }
  }

  public static void main(String[] args) throws IOException {
    JarScanner3 scanner = new JarScanner3();


    String home = System.getProperty("user.home");
    List<String> paths = Arrays.asList(
            "target/mmap-zip-classloader-0.1.0-SNAPSHOT.jar"
//        "/Library/Java/JavaVirtualMachines/jdk1.8.0.jdk/Contents/Home/jre/lib/rt.jar",
//          "/opt/jdk1.8.0/jre/lib/rt.jar",
//        home + "/.m2/repository/org/jboss/logging/jboss-logging/3.1.3.GA/jboss-logging-3.1.3.GA.jar",
//        home + "/.m2/repository/org/jboss/jboss-vfs/3.2.0.Beta1/jboss-vfs-3.2.0.Beta1.jar"
//        home + "/.m2/repository/org/jboss/logging/jboss-logging/3.1.2.GA/jboss-logging-3.1.2.GA.jar",
//        home + "/.m2/repository/org/jboss/jboss-vfs/3.1.0.Final/jboss-vfs-3.1.0.Final.jar"
        );
    for (String each : paths) {
      scanPath(scanner, each);
    }
  }

  private int findEndOfCentralDirectoryRecord(MappedByteBuffer buffer, int size) {
    // TODO four bytes at a time
    for (int i = size - 1; i > 3; i--) {
      if ((buffer.get(i - 3) == END_OF_CENTRAL_DIR_SIGNATURE[0])
              & (buffer.get(i - 2) == END_OF_CENTRAL_DIR_SIGNATURE[1])
              & (buffer.get(i - 1) == END_OF_CENTRAL_DIR_SIGNATURE[2])
              & (buffer.get(i) == END_OF_CENTRAL_DIR_SIGNATURE[3])) {
        return i - 3;
      }
    }
    return -1;

  }

  static void scanPath(JarScanner3 scanner, String path) throws IOException {
    long start = System.currentTimeMillis();
    scanner.scan(Paths.get(path));
    long end = System.currentTimeMillis();
    System.out.printf("completed %s in %d ms%n", path, end - start);

  }

}
