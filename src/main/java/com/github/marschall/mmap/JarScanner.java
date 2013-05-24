package com.github.marschall.mmap;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class JarScanner {

  // 0x06054b50
  private static final byte[] END_OF_CENTRAL_DIR_SIGNATURE = new byte[]{0x50, 0x4b, 0x05, 0x06};
  
  // 0x02014b50
  private static final byte[] CENTRAL_DIR_SIGNATURE = new byte[]{0x50, 0x4b, 0x01, 0x02};

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
        
        if (isCentralDirSignature(centralDirectory)) {
          int versionNeededToExtract = readInt2(centralDirectory, 6);
          if (versionNeededToExtract != 10) {
            throw new UnsupportedFeatureException("unsupported verison " + (versionNeededToExtract / 10) + '.' + (versionNeededToExtract % 10) + " only 1.0 is supported");
          }
          int generalPurposeBitFlag = readInt2(centralDirectory, 8);
          int compressionMethod = readInt2(centralDirectory, 10);
          
        } else {
          throw new CorruptFileException("invalid central directory header at: " + centralDirectoryOffset);
        }
        
      } else {
        // TODO scan slowly
      }
    }
  }

  private int readInt4(byte[] b, int offset) {
    // TODO long
    return Byte.toUnsignedInt(b[offset])
        | Byte.toUnsignedInt(b[offset + 1]) << 8
        | Byte.toUnsignedInt(b[offset + 2]) << 16
        | Byte.toUnsignedInt(b[offset + 3]) << 24;
  }

  private int readInt2(byte[] b, int offset) {
    return Byte.toUnsignedInt(b[offset]) | Byte.toUnsignedInt(b[offset + 1]) << 8;
  }

  private boolean isEndOfCentralDirSignature(byte[] b) {
    return startsWit4h(b, END_OF_CENTRAL_DIR_SIGNATURE);
  }
  
  private boolean isCentralDirSignature(byte[] b) {
    return startsWit4h(b, CENTRAL_DIR_SIGNATURE);
  }
  
  private boolean startsWit4h(byte[] b, byte[] prefix) {
    return b[0] == prefix[0]
        && b[1] == prefix[1]
        && b[2] == prefix[2]
        && b[3] == prefix[3];
  }

  public static void main(String[] args) throws IOException {
    JarScanner scanner = new JarScanner();
    Path path = Paths.get("target", "mmap-zip-classloader-0.1.0-SNAPSHOT.jar");
    scanner.scan(path);
  }

}
