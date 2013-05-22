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
  private static final byte[] END_OF_CENTRAL_DIR_SIGNATURE = new byte[]{0x50, 0x4b, 0x05, 0x06 };

  public void scan(Path p) throws IOException {
    try (FileChannel channel = FileChannel.open(p, StandardOpenOption.READ)) {
      long size = channel.size();
      if (size > Integer.MAX_VALUE) {
        throw new IllegalStateException("unsupported size: " + size);
      }
      MappedByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0L, size);
      // ByteBuffer read = ByteBuffer.allocate(22);
      int emptyEndLenght = 22;
      byte[] read = new byte[emptyEndLenght];
      int position = (int) size - emptyEndLenght;
      buffer.position(position);
      buffer.get(read, 0, emptyEndLenght);
      if (isEndOfCentralDirSignature(read)) {
        int numberOfEntries = readInt2(read, 8); 
        int centralDirectorySize = readInt4(read, 12); 
        int centralDirectoryOffset = readInt4(read, 16); 
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
    return b[0] == END_OF_CENTRAL_DIR_SIGNATURE[0]
            && b[1] == END_OF_CENTRAL_DIR_SIGNATURE[1]
            && b[2] == END_OF_CENTRAL_DIR_SIGNATURE[2]
            && b[3] == END_OF_CENTRAL_DIR_SIGNATURE[3];
  }

  public static void main(String[] args) throws IOException {
    JarScanner scanner = new JarScanner();
    Path path = Paths.get("target", "mmap-zip-classloader-0.1.0-SNAPSHOT.jar");
    scanner.scan(path);
  }

}
