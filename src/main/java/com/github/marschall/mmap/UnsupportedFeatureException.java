package com.github.marschall.mmap;

public class UnsupportedFeatureException extends RuntimeException {

  public UnsupportedFeatureException() {
    super();
  }

  public UnsupportedFeatureException(String message) {
    super(message);
  }

  public UnsupportedFeatureException(Throwable cause) {
    super(cause);
  }

  public UnsupportedFeatureException(String message, Throwable cause) {
    super(message, cause);
  }

}
