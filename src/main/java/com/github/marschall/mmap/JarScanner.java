package com.github.marschall.mmap;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

final class JarScanner {

  public void scan(Path path) throws IOException {
    ZipReader reader = ZipReader.on(path);
    try {
      int endOfCentralDirectoryRecordOffset = reader.findEndOfCentralDirectoryRecord();

      EndOfCentralDirectoryRecord endOfCentralDirectoryRecord = reader.readEndOfCentralDirectoryRecord(endOfCentralDirectoryRecordOffset);
      if (!endOfCentralDirectoryRecord.isEmpty()) {
        List<CentralDirectoryHeader> headers = reader.readCentralDirectoryRecord(endOfCentralDirectoryRecord);
      }
    } finally {
      reader.close();
    }
  }

  public static void main(String[] args) throws IOException {
    scanMac1();
  }

  private static void scanLinux1() throws IOException {
    JarScanner scanner = new JarScanner();


    String home = System.getProperty("user.home");
    List<String> paths = Arrays.asList(
        "target/mmap-zip-classloader-0.1.0-SNAPSHOT.jar",
        home + "/bin/java/zulu7.31.0.5-ca-jdk7.0.232-linux_x64/jre/lib/rt.jar",
        home + "/bin/java/zulu6.18.0.3-jdk6.0.99-linux_x64/jre/lib/rt.jar",
        home + "/bin/java/zulu8.40.0.25-ca-jdk8.0.222-linux_x64/jre/lib/rt.jar",
        home + "/bin/java/graalvm-ce-19.2.0.1/jre/lib/rt.jar",
        home + "/bin/java/Alibaba_Dragonwell_8.1.1-GA_Linux_x64/jre/lib/rt.jar",
        home + "/bin/java/graalvm-ee-19.2.0.1/jre/lib/rt.jar",
        home + "/.m2/repository/org/jboss/jboss-vfs/3.2.14.Final/jboss-vfs-3.2.14.Final.jar",
        home + "/.m2/repository/org/jboss/logging/jboss-logging/3.4.0.Final/jboss-logging-3.4.0.Final.jar"
        );
    for (String each : paths) {
      scanPath(scanner, each);
    }

  }

  private static void scanMac1() throws IOException {
    JarScanner scanner = new JarScanner();


    String home = System.getProperty("user.home");
    List<String> paths = Arrays.asList(
        "target/mmap-zip-classloader-0.1.0-SNAPSHOT.jar",
        "/Library/Java/JavaVirtualMachines/jdk1.8.0_192.jdk/Contents/Home/jre/lib/rt.jar",
        "/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/jre/lib/rt.jar",
        "/Library/Java/JavaVirtualMachines/zulu-8.jdk/Contents/Home/jre/lib/rt.jar",
        "/Library/Java/JavaVirtualMachines/1.7.0.jdk/Contents/Home/jre/lib/rt.jar",
        //  "/opt/jdk1.8.0/jre/lib/rt.jar",
        home + "/.m2/repository/org/jboss/logging/jboss-logging/3.4.0.Final/jboss-logging-3.4.0.Final.jar",
        home + "/.m2/repository/org/jboss/jboss-vfs/3.2.9.Final/jboss-vfs-3.2.9.Final.jar"
        );
    for (String each : paths) {
      scanPath(scanner, each);
    }

  }

  static void scanPath(JarScanner scanner, String path) throws IOException {
    long start = System.currentTimeMillis();
    scanner.scan(Paths.get(path));
    long end = System.currentTimeMillis();
    System.out.printf("completed %s in %d ms%n", path, end - start);

  }

}
