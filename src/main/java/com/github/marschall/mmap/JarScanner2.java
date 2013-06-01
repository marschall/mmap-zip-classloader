package com.github.marschall.mmap;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JarScanner2 {
  
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
      byte[] front = new byte[8192];
      int position = (int) max(0, size - 8192);
      buffer.position(position);
      buffer.get(front, 0, (int) min(size, 8192));
      if (isEndOfCentralDirSignature(front, (int) min(size, front.length))) {
        DoubleBuffer doubleBuffer = new DoubleBuffer(front);
        doubleBuffer.position(8192 - 22);
        
        int numberOfEntries = doubleBuffer.readInt2(8);
        int centralDirectorySize = doubleBuffer.readInt4(12);
        int centralDirectoryOffset = doubleBuffer.readInt4(16);
        
        
        if (centralDirectoryOffset >= size - 8192) {
          // we just got lucky and the central directory is in the chunk we read
          // TODO optimize for case were central directory is in second last chunk
          doubleBuffer.position(8192- (int) (size - centralDirectoryOffset));
        } else {
          buffer.position(centralDirectoryOffset);
          buffer.get(front, 0, 8192);
          
          doubleBuffer.position(0);
        }
        
        Map<String, Integer> offsetMap = new HashMap<>(centralDirectoryOffset);
        for (int i = 0; i < numberOfEntries; ++i) {
          if (doubleBuffer.isCentralDirSignature()) {
            doubleBuffer.ensureAvailable(46, buffer);
            
            int versionNeededToExtract = doubleBuffer.readInt2(6);
            if (versionNeededToExtract != 10) {
              throw new UnsupportedFeatureException("unsupported verison " + (versionNeededToExtract / 10) + '.' + (versionNeededToExtract % 10) + " only 1.0 is supported");
            }
            int generalPurposeBitFlag = doubleBuffer.readInt2(8);
            int compressionMethod = doubleBuffer.readInt2(10);
            int fileNameLength = doubleBuffer.readInt2(28);
            int extraFieldLength = doubleBuffer.readInt2(30);
            int fileCommentLength = doubleBuffer.readInt2(32);
            
            int relativeOffsetOfLocalHeader = doubleBuffer.readInt4(42);
            doubleBuffer.ensureAvailable(46 + fileNameLength + fileCommentLength, buffer);
            
            String fileName = doubleBuffer.readString(fileNameLength, 46);
//            boolean c = fileName.contains("=");
//            if (fileName.length() != fileNameLength) {
//              throw new RuntimeException();
//            }
            offsetMap.put(fileName, relativeOffsetOfLocalHeader);
            
//            if (i == 93) {
//              for (String each : offsetMap.keySet()) {
//                System.out.println(each);
//              }
//            }
            
            doubleBuffer.incrementPostion(46 + fileNameLength + extraFieldLength + fileCommentLength);
            
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
  
    private boolean isEndOfCentralDirSignature(byte[] b, int bufferLength) {
    return startsWit4h(b, bufferLength - 22, END_OF_CENTRAL_DIR_SIGNATURE);
  }
  
  private boolean startsWit4h(byte[] bytes, int offset, byte[] prefix) {
    return bytes[offset] == prefix[0]
        && bytes[offset + 1] == prefix[1]
        && bytes[offset + 2] == prefix[2]
        && bytes[offset + 3] == prefix[3];
  }
  

  /**
   * This class allows us to always read in xk chunks without having to copy
   * what we have already read.
   */
  static final class DoubleBuffer {
    
    private static final Charset CHARSET = StandardCharsets.ISO_8859_1;
    private byte[] front;
    private byte[] back;
    private int position;
    private boolean backFilled;
    
    
    DoubleBuffer(byte[] front) {
      this.front = front;
      // this is a stupid trick in order to be able to lazily allocate the back
      // buffer and not having to do null checks
      // #fillBack will have to check for this and allocate
      this.back = front;
      this.backFilled = false;
      this.position = 0;
    }
    
    void position(int newPosition) {
      this.position = newPosition;
    }

    void incrementPostion(int increment) {
      int newPosition = this.position + increment;
      if (newPosition < this.front.length) {
        this.position = newPosition;
      } else {
        this.position = newPosition - this.front.length;
        this.flipBuffers();
      }
    }
    
    void ensureAvailable(int capacity, ByteBuffer buffer) {
      if (this.position + capacity < this.front.length) {
        // fits into front buffer
        return;
      }
      if (this.backFilled && this.position + capacity < this.front.length + this.back.length) {
        // fits into front and back buffer
        return;
      }
      
      // we have to load data
      if (this.backFilled) {
        // TODO enlarge back buffer
        throw new UnsupportedFeatureException("back buffer too small");
      }
      int loaded = this.fillBack(buffer);
      if (loaded + this.front.length - position < capacity) {
        throw new CorruptFileException("end of file reached");
      }
    }

    private int fillBack(ByteBuffer buffer) {
      if (this.back == this.front) {
        this.back = new byte[this.front.length];
      }
      
//      int newPosition = min(buffer.position() + front.length, buffer.limit());
//      buffer.position(newPosition);
      int length = min(back.length, buffer.limit() - buffer.position());
     
      buffer.get(back, 0, length);
      this.backFilled = true;
      
      return length;
    }
    
    private void flipBuffers() {
      byte[] temp = this.front;
      this.front = this.back;
      this.back = temp;
      this.backFilled = false;
    }
    
    
    String readString(int length, int offset) {
      if (this.position + offset + length < this.front.length) {
        // all if 1st buffer
        return new String(this.front, this.position + offset, length, CHARSET);
      } else if (this.position + offset >= this.front.length) {
        // all in 2nd buffer
        return new String(this.back, this.position + offset - this.front.length, length, CHARSET);
      } else {
        // have to copy
        // TODO high off-by-one chance
        byte[] buffer = new byte[length];
        System.arraycopy(this.front, this.position + offset, buffer, 0, this.front.length - offset - this.position);
        System.arraycopy(this.back, 0, buffer, this.front.length - offset - this.position, this.position + offset + length - this.front.length);
        return new String(buffer, 0, length, CHARSET);
      }
    }
    
    boolean isCentralDirSignature() {
      return this.readByte(0) == CENTRAL_DIR_SIGNATURE[0]
          && this.readByte(1) == CENTRAL_DIR_SIGNATURE[1]
          && this.readByte(2) == CENTRAL_DIR_SIGNATURE[2]
          && this.readByte(3) == CENTRAL_DIR_SIGNATURE[3];
    }
    
    int readInt4(int offset) {
      // TODO long
      return readByte(offset)
          | readByte(offset + 1) << 8
          | readByte(offset + 2) << 16
          | readByte(offset + 3) << 24;
    }
    
    int readByte(int offset) {
      int readPosition = this.position + offset;
      if (readPosition < this.front.length) {
        return Byte.toUnsignedInt(this.front[readPosition]);
      } else {
        return Byte.toUnsignedInt(this.back[readPosition - this.front.length]);
      }
    }

    int readInt2(int offset) {
      return readByte(offset) | readByte(offset + 1) << 8;
    }
    
    
  }

  public static void main(String[] args) throws IOException {
    JarScanner2 scanner = new JarScanner2();
    
    String home = System.getProperty("user.home");
    List<String> paths = Arrays.asList(
//        "/Library/Java/JavaVirtualMachines/jdk1.8.0.jdk/Contents/Home/jre/lib/rt.jar",
        "target/mmap-zip-classloader-0.1.0-SNAPSHOT.jar",
        home + "/.m2/repository/org/jboss/logging/jboss-logging/3.1.3.GA/jboss-logging-3.1.3.GA.jar",
        home + "/.m2/repository/org/jboss/jboss-vfs/3.2.0.Beta1/jboss-vfs-3.2.0.Beta1.jar"
        );
    for (String each : paths) {
      scanPath(scanner, each);
    }
    
  }
  
  static void scanPath(JarScanner2 scanner, String path) throws IOException {
    long start = System.currentTimeMillis();
    scanner.scan(Paths.get(path));
    long end = System.currentTimeMillis();
    System.out.printf("completed %s in %d ms%n", path, end - start);
    
  }

}
