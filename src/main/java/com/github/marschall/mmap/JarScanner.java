package com.github.marschall.mmap;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// http://www.pkware.com/appnote
public class JarScanner {
  
  // 0x02014b50
  private static final byte[] CENTRAL_DIR_SIGNATURE = new byte[]{0x50, 0x4b, 0x01, 0x02};
  
  // 0x04034b50
  private static final byte[] LOCAL_FILE_HEADER_SIGNATURE = new byte[]{0x50, 0x4b, 0x03, 0x04};
  
  // 0x06054b50
  private static final byte[] END_OF_CENTRAL_DIR_SIGNATURE = new byte[]{0x50, 0x4b, 0x05, 0x06};
  
  // 0x08074b50
  private static final byte[] DATA_DESCRIPTOR_SIGNATURE = new byte[]{0x50, 0x4b, 0x07, 0x08};

  public void scan(Path p) throws IOException {
    try (FileChannel channel = FileChannel.open(p, StandardOpenOption.READ)) {
      long size = channel.size();
      if (size > Integer.MAX_VALUE) {
        throw new IllegalStateException("unsupported size: " + size);
      }
      MappedByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0L, size);
      // ByteBuffer read = ByteBuffer.allocate(22);
      int emptyEndLenght = 22;
      byte[] endRecord = new byte[emptyEndLenght];
      int position = (int) size - emptyEndLenght;
      buffer.position(position);
      buffer.get(endRecord, 0, emptyEndLenght);
      if (isEndOfCentralDirSignature(endRecord)) {
        int numberOfEntries = readInt2(endRecord, 8);
        int centralDirectorySize = readInt4(endRecord, 12);
        int centralDirectoryOffset = readInt4(endRecord, 16);
        
        byte[] centralDirectory = new byte[centralDirectorySize];
        buffer.position(centralDirectoryOffset);
        buffer.get(centralDirectory, 0, centralDirectorySize);
        
        Map<String, Integer> offsetMap = new HashMap<>(centralDirectoryOffset);
        int recodOffset = 0;
        for (int i = 0; i < numberOfEntries; ++i) {
          if (isCentralDirSignature(centralDirectory, recodOffset)) {
            int versionNeededToExtract = readInt2(centralDirectory, 6 + recodOffset);
            if (versionNeededToExtract != 10) {
              throw new UnsupportedFeatureException("unsupported verison " + (versionNeededToExtract / 10) + '.' + (versionNeededToExtract % 10) + " only 1.0 is supported");
            }
            int generalPurposeBitFlag = readInt2(centralDirectory, 8 + recodOffset);
            int compressionMethod = readInt2(centralDirectory, 10 + recodOffset);
            int fileNameLength = readInt2(centralDirectory, 28 + recodOffset);
            int extraFieldLength = readInt2(centralDirectory, 30 + recodOffset);
            int fileCommentLength = readInt2(centralDirectory, 32 + recodOffset);
            
            int relativeOffsetOfLocalHeader = readInt4(centralDirectory, 42 + recodOffset);
            
            String fileName = readString(centralDirectory, fileNameLength, 46 + recodOffset);
            offsetMap.put(fileName, relativeOffsetOfLocalHeader);
            
//            System.out.println(fileName);
            
            recodOffset += 46 + fileNameLength + extraFieldLength + fileCommentLength;
            
          } else {
            throw new CorruptFileException("invalid central directory header at: " + centralDirectoryOffset);
          }
        }
        
        
      } else {
        // TODO scan slowly
        throw new UnsupportedFeatureException("expected end of central dir at end of file");
      }
    }
  }
  
  private String readString(byte[] bytes, int length, int offset) {
    return new String(bytes, offset, length, StandardCharsets.UTF_8);
  }

  private int readInt4(byte[] bytes, int offset) {
    // TODO long
    return Byte.toUnsignedInt(bytes[offset])
        | Byte.toUnsignedInt(bytes[offset + 1]) << 8
        | Byte.toUnsignedInt(bytes[offset + 2]) << 16
        | Byte.toUnsignedInt(bytes[offset + 3]) << 24;
  }

  private int readInt2(byte[] bytes, int offset) {
    return Byte.toUnsignedInt(bytes[offset]) | Byte.toUnsignedInt(bytes[offset + 1]) << 8;
  }

  private boolean isEndOfCentralDirSignature(byte[] b) {
    return startsWit4h(b, END_OF_CENTRAL_DIR_SIGNATURE);
  }
  
  private boolean isCentralDirSignature(byte[] bytes, int offset) {
    return startsWit4h(bytes, offset, CENTRAL_DIR_SIGNATURE);
  }
  
  private boolean startsWit4h(byte[] bytes, int offset, byte[] prefix) {
    return bytes[offset] == prefix[0]
        && bytes[offset + 1] == prefix[1]
        && bytes[offset + 2] == prefix[2]
        && bytes[offset + 3] == prefix[3];
  }
  
  private boolean startsWit4h(byte[] bytes, byte[] prefix) {
    return bytes[0] == prefix[0]
        && bytes[1] == prefix[1]
        && bytes[2] == prefix[2]
        && bytes[3] == prefix[3];
  }
  

  public static void main(String[] args) throws IOException {
    JarScanner scanner = new JarScanner();
    
    String home = System.getProperty("user.home");
    List<String> paths = Arrays.asList(
//        "/Library/Java/JavaVirtualMachines/jdk1.8.0.jdk/Contents/Home/jre/lib/rt.jar",
        "/opt/jdk1.8.0/jre/lib/rt.jar"
//        "target/mmap-zip-classloader-0.1.0-SNAPSHOT.jar",
//        home + "/.m2/repository/org/jboss/logging/jboss-logging/3.1.3.GA/jboss-logging-3.1.3.GA.jar",
//        home + "/.m2/repository/org/jboss/jboss-vfs/3.2.0.Beta1/jboss-vfs-3.2.0.Beta1.jar",
//        home + "/.m2/repository/org/jboss/logging/jboss-logging/3.1.2.GA/jboss-logging-3.1.2.GA.jar",
//        home + "/.m2/repository/org/jboss/jboss-vfs/3.1.0.Final/jboss-vfs-3.1.0.Final.jar"
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
