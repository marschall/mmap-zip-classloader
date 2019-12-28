package com.github.marschall.mmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

public class ReverseJar {

  public static void main(String[] args) throws IOException {
    
    String home = System.getProperty("user.home");
    File file = new File(home + "/.m2/repository/org/jboss/logging/jboss-logging/3.4.0.Final/jboss-logging-3.4.0.Final.jar");
    try (URLClassLoader classLoader = new URLClassLoader(new URL[] {file.toURL()})) {
//      URL resource = classLoader.getResource("META-INF/");
//      System.out.println(resource);
//      System.out.println(resource.openStream());
//      try (InputStream is = classLoader.getResourceAsStream("META-INF/")) {
//      try (InputStream is = classLoader.getResourceAsStream("org/")) {
      try (InputStream is = classLoader.getResourceAsStream("org/jboss/logging")) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        is.transferTo(bos);
        System.out.println(Arrays.toString(bos.toByteArray()));
        System.out.println(new String(bos.toByteArray()));
      }
    }

  }

}
