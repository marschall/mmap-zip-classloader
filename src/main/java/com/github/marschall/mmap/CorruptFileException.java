package com.github.marschall.mmap;

public class CorruptFileException extends RuntimeException {

  public CorruptFileException() {
    super();
  }

  public CorruptFileException(String message) {
    super(message);
  }

  public CorruptFileException(Throwable cause) {
    super(cause);
  }

  public CorruptFileException(String message, Throwable cause) {
    super(message, cause);
  }

}
