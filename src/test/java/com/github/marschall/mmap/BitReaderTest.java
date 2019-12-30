package com.github.marschall.mmap;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Random;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BitReaderTest {

  private Path tempFile;

  @BeforeEach
  void setUp() throws IOException {
    this.tempFile = Files.createTempFile(null, null);
  }

  @AfterEach
  void tearDown() throws IOException {
    Files.delete(this.tempFile);
  }

  @Test
  void test() throws IOException {
    BitReader reader = this.createBitReader(new byte[] {(byte) 0b10110011, (byte) 0b100001111, (byte) 0b00001111});
  }

  private BitReader createBitReader(byte[] data) throws IOException {
    Random random = new Random();
    int padLenght = random.nextInt(15);
    byte[] padded = new byte[padLenght + data.length];
    for (int i = 0; i < padLenght; i++) {
      padded[i] = (byte) random.nextInt(255);
    }
    System.arraycopy(data, 0, padded, padLenght, data.length);
    Files.write(this.tempFile, padded);

    FileChannel channel = FileChannel.open(this.tempFile, StandardOpenOption.READ);
    MappedByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0L, padded.length);
    return new BitReader(buffer, padLenght);
  }

}
