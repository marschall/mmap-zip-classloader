package com.github.marschall.mmap;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipException;

final class JarScanner3 {

  private static final int END_OF_CENTRAL_DIR_SIGNATURE = 0x06054b50;

  private static final int END_OF_CENTRAL_DIR_SIZE = 22;

  public void scan(Path path) throws IOException {
    try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
      long size = channel.size();
      if (size > Integer.MAX_VALUE) {
        throw new ZipException("size too large: " + size);
      }
      if (size < END_OF_CENTRAL_DIR_SIZE) {
        throw new ZipException("size too small: " + size);
      }
      MappedByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0L, size);
      try {
        int endOfCentralDirectoryRecordOffset = findEndOfCentralDirectoryRecord(buffer, (int) size);
        if ((size - endOfCentralDirectoryRecordOffset) != END_OF_CENTRAL_DIR_SIZE) {
          System.out.println(size - endOfCentralDirectoryRecordOffset);
        }
        if ((endOfCentralDirectoryRecordOffset < 0) || (endOfCentralDirectoryRecordOffset > size)) {
          throw new ZipException("corrupted end of central directory record");
        }

        EndOfCentralDirectoryRecord endOfCentralDirectoryRecord = readEndOfCentralDirectoryRecord(buffer, endOfCentralDirectoryRecordOffset);
        if (!endOfCentralDirectoryRecord.isEmpty()) {
          List<CentralDirectoryHeader> headers = readCentralDirectoryRecord(buffer, endOfCentralDirectoryRecord);
        }

      } finally {
        Unmapper.unmap(buffer, path);
      }
    }
  }

  private static List<CentralDirectoryHeader> readCentralDirectoryRecord(
          MappedByteBuffer buffer,
          EndOfCentralDirectoryRecord endOfCentralDirectoryRecord) {


    int numberOfRecords = endOfCentralDirectoryRecord.getNumberOfRecords();
    List<CentralDirectoryHeader> headers = new ArrayList<>(numberOfRecords);
    int offset = endOfCentralDirectoryRecord.getOffset();
    for (int i = 0; i < numberOfRecords; i++) {
//      CentralDirectoryHeader header = ;
//      headers.add(header);
//      offset += ;
    }
    return headers;
  }

  public static void main(String[] args) throws IOException {
    scanLinux1();
  }

  private static void scanLinux1() throws IOException {
    JarScanner3 scanner = new JarScanner3();


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
    JarScanner3 scanner = new JarScanner3();


    String home = System.getProperty("user.home");
    List<String> paths = Arrays.asList(
            "target/mmap-zip-classloader-0.1.0-SNAPSHOT.jar",
            "/Library/Java/JavaVirtualMachines/jdk1.8.0.jdk/Contents/Home/jre/lib/rt.jar"
            //  "/opt/jdk1.8.0/jre/lib/rt.jar",
            //home + "/.m2/repository/org/jboss/logging/jboss-logging/3.1.3.GA/jboss-logging-3.1.3.GA.jar",
            //home + "/.m2/repository/org/jboss/jboss-vfs/3.2.0.Beta1/jboss-vfs-3.2.0.Beta1.jar"
            //home + "/.m2/repository/org/jboss/logging/jboss-logging/3.1.2.GA/jboss-logging-3.1.2.GA.jar",
            //home + "/.m2/repository/org/jboss/jboss-vfs/3.1.0.Final/jboss-vfs-3.1.0.Final.jar"
            );
    for (String each : paths) {
      scanPath(scanner, each);
    }

  }

  private static int findEndOfCentralDirectoryRecord(MappedByteBuffer buffer, int size) throws ZipException {
    // TODO four bytes at a time
    int signgureLength = 4;
    for (int i = ((size - END_OF_CENTRAL_DIR_SIZE) + signgureLength) - 1; i > 3; i--) {

      int word = Byte.toUnsignedInt(buffer.get(i - 3))
                 | (Byte.toUnsignedInt(buffer.get(i - 2)) << 8)
                 | (Byte.toUnsignedInt(buffer.get(i - 1)) << 16)
                 | (Byte.toUnsignedInt(buffer.get(i)) << 24);
      if (word == END_OF_CENTRAL_DIR_SIGNATURE) {
        return i - 3;
      }
    }
    throw new ZipException("end of central directory record not found");
  }

  private static EndOfCentralDirectoryRecord readEndOfCentralDirectoryRecord(MappedByteBuffer buffer, int offset) throws ZipException {
    int localOffset = offset;

    int signagure = readInt4(buffer, localOffset);
    localOffset += 4;

    int numberOfDisks = readInt2(buffer, localOffset);
    if (numberOfDisks != 0) {
      throw new ZipException("multiple disks not supported");
    }
    localOffset += 2;

    int centralDirectoryRecordDisk = readInt2(buffer, localOffset);
    if (centralDirectoryRecordDisk != 0) {
      throw new ZipException("multiple disks not supported");
    }
    localOffset += 2;

    int numberOfCentralDirectoryRecordsOnThisDisk = readInt2(buffer, localOffset);
    localOffset += 2;

    int totalNumberOfCentralDirectoryRecords = readInt2(buffer, localOffset);
    localOffset += 2;
    if (totalNumberOfCentralDirectoryRecords != numberOfCentralDirectoryRecordsOnThisDisk) {
      throw new ZipException("number of central directory records don't match");
    }
    if (totalNumberOfCentralDirectoryRecords < 0) {
      throw new ZipException("too many central directory entries");
    }

    int centralDirectoryRecordSize = readInt4(buffer, localOffset);
    localOffset += 4;

    int centralDirectoryRecordOffset = readInt4(buffer, localOffset);
    localOffset += 4;

    int commentLength = readInt2(buffer, localOffset);
    if (commentLength != 0) {
      System.out.println("comment length: " + commentLength);
    }

    return new EndOfCentralDirectoryRecord(centralDirectoryRecordOffset, totalNumberOfCentralDirectoryRecords, centralDirectoryRecordSize);
  }

  private static int readInt2(MappedByteBuffer buffer, int offset) {
    return Byte.toUnsignedInt(buffer.get(offset))
            | (Byte.toUnsignedInt(buffer.get(offset + 1)) << 8);
  }

  private static int readInt4(MappedByteBuffer buffer, int offset) {
    int value = Byte.toUnsignedInt(buffer.get(offset))
        | (Byte.toUnsignedInt(buffer.get(offset + 1)) << 8)
        | (Byte.toUnsignedInt(buffer.get(offset + 2)) << 16)
        | (Byte.toUnsignedInt(buffer.get(offset + 3)) << 24);
    if (value < 0) {
      throw illegalStateExceptionValueTooLarge();
    }
    return value;
  }

  private static IllegalStateException illegalStateExceptionValueTooLarge() {
    return new IllegalStateException("value too large");
  }

  static void scanPath(JarScanner3 scanner, String path) throws IOException {
    long start = System.currentTimeMillis();
    scanner.scan(Paths.get(path));
    long end = System.currentTimeMillis();
    System.out.printf("completed %s in %d ms%n", path, end - start);

  }

}
