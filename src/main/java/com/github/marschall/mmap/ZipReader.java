package com.github.marschall.mmap;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.CRC32C;
import java.util.zip.ZipException;

final class ZipReader {

  private static final int CENTRAL_FILE_HEADER_SIGNATURE = 0x02014b50;

  private static final int END_OF_CENTRAL_DIR_SIGNATURE = 0x06054b50;
  
  private static final int END_OF_CENTRAL_DIR_SIZE = 22;
  
  private static final int LOCAL_FILE_HEADER_SIGNATURE  = 0x04034b50;
  
  private static final int LOCAL_FILE_HEADER_SIZE  = 30;

  private final MappedByteBuffer buffer;

  private final Path path;

  private final FileChannel channel;

  private final int size;

  private ZipReader(MappedByteBuffer buffer, Path path, FileChannel channel, int size) {
    this.buffer = buffer;
    this.path = path;
    this.channel = channel;
    this.size = size;
  }

  void close() throws IOException {
    Unmapper.unmap(this.buffer, this.path);
    this.channel.close();
  }
  
  static ZipReader on(Path path) throws IOException {
    if (!Files.isRegularFile(path)) {
      throw new ZipException("not a regular file: " + path);
    }
    FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);
    long size = channel.size();
    if (size > Integer.MAX_VALUE) {
      throw new ZipException("size too large: " + size);
    }
    if (size < END_OF_CENTRAL_DIR_SIZE) {
      throw new ZipException("size too small: " + size);
    }
    MappedByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0L, size);
    return new ZipReader(buffer, path, channel, (int) size);
  }

  List<CentralDirectoryHeader> readCentralDirectoryRecord(EndOfCentralDirectoryRecord endOfCentralDirectoryRecord) throws ZipException {

    int numberOfRecords = endOfCentralDirectoryRecord.getNumberOfRecords();
    List<CentralDirectoryHeader> headers = new ArrayList<>(numberOfRecords);
    int offset = endOfCentralDirectoryRecord.getOffset();
    byte[] fileNameBuffer = new byte[256];

    Set<Integer> compressionMethods = new HashSet<>();
    Set<Integer> versionsMadeBy = new HashSet<>();
    Set<Integer> hostSystems = new HashSet<>();
    int maxVersionNeededToExtract = 0;
    int maxFileNameLength = 0;

    for (int i = 0; i < numberOfRecords; i++) {
      int signature = readInt4(offset);
      if (signature != CENTRAL_FILE_HEADER_SIGNATURE) {
        throw new ZipException("corrupt file header");
      }
      int versionMadeBy = readInt2(offset + 4);
      int hostSystem = (versionMadeBy & 0xFF00) >>> 8;

      int versionNeededToExtract = readInt2(offset + 6);
      int generalPurposeBitFlag = readInt2(offset + 8);

      //      if (GeneralPurposeBitFlag.isEncrypted(generalPurposeBitFlag)) {
      //        System.out.println("encrypted");
      //      }
      //
      //      if (GeneralPurposeBitFlag.areFieldsZeroInLocalHeader(generalPurposeBitFlag)) {
      //        System.out.println("local fields are zero");
      //      }
      //      
      //      if (!GeneralPurposeBitFlag.isUtf8(generalPurposeBitFlag)) {
      //        System.out.println("not utf-8");
      //      }

      int compressionMethod  = readInt2(offset + 10);
      int fileLastModificationTime = readInt2(offset + 12);
      int fileLastModificationDate = readInt2(offset + 14);
      int crc32 = readInt4(offset + 16);
      int compressedSize = readInt4(offset + 20);
      int uncompressedSize = readInt4(offset + 24);

      int fileNameLength = readInt2(offset + 28);
      int extraFieldLength = readInt2(offset + 30);
      int fileCommentLength = readInt2(offset + 32);

      int startDiskNumber = readInt2(offset + 34);
      if (startDiskNumber != 0) {
        throw new ZipException("multiple disks not supported");
      }
      
      int internalFileAttributes = readInt2(offset + 36);
      int externalFileAttributes = readInt4(offset + 38);
      int localHeaderOffset = readInt4(offset + 42);

      fileNameBuffer = ensureBufferSize(fileNameBuffer, fileNameLength);
      String fileName = readString(fileNameBuffer, offset + 46, fileNameLength);
      //      if ((fileName.endsWith("/") || fileName.endsWith("\\")) && hostSystem == HostSystem.DOS) {
      //        boolean isDirectory = (externalFileAttributes & DosFileAttributes.DIRECTORY) != 0;
      //        if (!isDirectory) {
      //          
      //        }
      //      }
      if (fileName.endsWith("\\")) {
        System.out.println("x");
      }
      //      if ((fileName.endsWith("/") || fileName.endsWith("\\")) && hostSystem == HostSystem.UNIX) {
      //        boolean isDirectory = (externalFileAttributes & DosFileAttributes.DIRECTORY) != 0;
      //        // https://unix.stackexchange.com/questions/14705/the-zip-formats-external-file-attribute
      //        boolean isDirectoryU = ((externalFileAttributes >>> 16) & UnixAttributes.S_IFDIR) != 0;
      //        if (!isDirectory || !isDirectoryU) {
      //          System.out.println("x");
      //        }
      //        if (fileName.equals("META-INF/")) {
      //          System.out.println("x");
      //        }
      //      }

      CentralDirectoryHeader header = new CentralDirectoryHeader(fileName, compressionMethod, crc32, compressedSize, uncompressedSize, localHeaderOffset);
      headers.add(header);

      compressionMethods.add(compressionMethod);
      versionsMadeBy.add(versionMadeBy & 0xFF);
      hostSystems.add(hostSystem);
      maxVersionNeededToExtract = Math.max(maxVersionNeededToExtract, versionNeededToExtract);
      maxFileNameLength = Math.max(maxFileNameLength, fileNameLength);

      offset += 46 + fileNameLength + extraFieldLength + fileCommentLength;
    }
    System.out.println("compression methods: " + compressionMethods);
    System.out.println("versions made by: " + versionsMadeBy);
    System.out.println("host systems: " + hostSystems);
    System.out.println("version needed to extract: " + maxVersionNeededToExtract);
    System.out.println("max file name length: " + maxFileNameLength);
    return headers;
  }

  EndOfCentralDirectoryRecord readEndOfCentralDirectoryRecord(int offset) throws ZipException {

    int signagure = readInt4(offset);

    int numberOfDisks = readInt2(offset + 4);
    if (numberOfDisks != 0) {
      throw new ZipException("multiple disks not supported");
    }

    int centralDirectoryRecordDisk = readInt2(offset + 6);
    if (centralDirectoryRecordDisk != 0) {
      throw new ZipException("multiple disks not supported");
    }

    int numberOfCentralDirectoryRecordsOnThisDisk = readInt2(offset + 8);

    int totalNumberOfCentralDirectoryRecords = readInt2(offset + 10);
    if (totalNumberOfCentralDirectoryRecords != numberOfCentralDirectoryRecordsOnThisDisk) {
      throw new ZipException("number of central directory records don't match");
    }
    if (totalNumberOfCentralDirectoryRecords < 0) {
      throw new ZipException("too many central directory entries");
    }

    int centralDirectoryRecordSize = readInt4(offset + 12);

    int centralDirectoryRecordOffset = readInt4(offset + 16);

    int commentLength = readInt2(offset + 20);
    if (commentLength != 0) {
      System.out.println("comment length: " + commentLength);
    }

    return new EndOfCentralDirectoryRecord(centralDirectoryRecordOffset, totalNumberOfCentralDirectoryRecords, centralDirectoryRecordSize);
  }

  void readFile(CentralDirectoryHeader header, byte[] target) throws ZipException {
    int offset = header.getLocalHeaderOffset();
    
    int signature = readInt4(offset);
    if (signature != LOCAL_FILE_HEADER_SIGNATURE) {
      throw new ZipException("corrupt file header");
    }

    int versionNeededToExtract = readInt2(offset + 4);
    int generalPurposeBitFlag = readInt2(offset + 6);

    int compressionMethod  = readInt2(offset + 8);
    int fileLastModificationTime = readInt2(offset + 10);
    int fileLastModificationDate = readInt2(offset + 12);
    int crc32 = readInt4(offset + 14);
    int compressedSize = readInt4(offset + 18);
    int uncompressedSize = readInt4(offset + 22);

    int fileNameLength = readInt2(offset + 26);
    int extraFieldLength = readInt2(offset + 28);
    
    int dataStart = offset + LOCAL_FILE_HEADER_SIZE + fileNameLength + extraFieldLength;
    switch (header.getCompressionMethod()) {
      case CompressionMethods.DEFLATE:
        readDeflate(target, dataStart, uncompressedSize);
        break;
      case CompressionMethods.STORE:
        readStore(target, dataStart, uncompressedSize);
        break;
      default:
        throw new ZipException("unsupported compression method: " + header.getCompressionMethod());
    }

    if (crc32 != computeCrc32(target, 0, uncompressedSize)) {
      throw new ZipException("corrupted zip entry");
    }
  }

  private void readDeflate(byte[] target, int offset, int size) throws ZipException {
    int blockOffset = offset;
    boolean lastBlock = false;
    while (!lastBlock) {
      int header = Byte.toUnsignedInt(this.buffer.get(blockOffset));
      lastBlock = (header & 0b10000000) != 0;
      int btype = (header & 0b01100000) >> 5;
      switch (btype) {
        case BType.NO_COMPRESSION:
          int len = this.readInt2(blockOffset + 1);
          // one's complement of LEN
          int nlen = this.readInt2(blockOffset + 3);
          if ((len ^ nlen) != 0xFFFF) {
            throw new ZipException("corrupted zip entry");
          }
          this.readBytes(target, blockOffset + 5, len);
          break;
        case BType.COMPRESSED_DYNAMIC:
          break;
        case BType.COMPRESSED_FIXED:
          break;
        case BType.RESERVED:
          throw new ZipException("reserved BTYPE");
      }
    }
  }
  
  private void readStore(byte[] target, int offset, int size) {
    readBytes(target, offset, size);
  }

  private static int computeCrc32(byte[] buffer, int offset, int length) {
    CRC32 crc32 = new CRC32();
    crc32.update(buffer, offset, length);
    return (int) crc32.getValue();
  }

  static byte[] ensureBufferSize(byte[] buffer, int length) {
    if (buffer.length >= length) {
      return buffer;
    } else {
      return new byte[roundUpToNextPowerOfTwo(length)];
    }
  }

  static int roundUpToNextPowerOfTwo(int i) {
    int highestOneBit = Integer.highestOneBit(i);
    if (i == highestOneBit) {
      return i;
    }
    return highestOneBit << 1;
  }

  int findEndOfCentralDirectoryRecord() throws ZipException {
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

  int readInt2(int offset) {
    return Byte.toUnsignedInt(buffer.get(offset))
        | (Byte.toUnsignedInt(buffer.get(offset + 1)) << 8);
  }

  int readInt4(int offset) {
    int value = Byte.toUnsignedInt(buffer.get(offset))
        | (Byte.toUnsignedInt(buffer.get(offset + 1)) << 8)
        | (Byte.toUnsignedInt(buffer.get(offset + 2)) << 16)
        | (Byte.toUnsignedInt(buffer.get(offset + 3)) << 24);
    // TODO signed vs unsigend
    //    if (value < 0) {
    //      throw illegalStateExceptionValueTooLarge();
    //    }
    return value;
  }

  String readString(byte[] target, int offset, int length) {
    readBytes(target, offset, length);
    return new String(target, 0, length, StandardCharsets.UTF_8);
  }
  
  private void readBytes(byte[] target, int offset, int length) {
    // TODO fixed in 13
    for (int i = 0; i < length; i++) {
      target[i] = buffer.get(offset + i);
    }
  }

  private static IllegalStateException illegalStateExceptionValueTooLarge() {
    return new IllegalStateException("value too large");
  }

}
