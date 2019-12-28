package com.github.marschall.mmap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import com.github.marschall.mmap.PackageSubstring.PackageOfFile;

class PackageOfFileTest {
  
  @Test
  void testToString() {
    PackageOfFile pkg = new PackageOfFile("com/acme/Class.class");
    assertEquals("com/acme", pkg.toString());
  }

  @Test
  void samePackage() {
    PackageOfFile pkg1 = new PackageOfFile("com/acme/Class.class");
    PackageOfFile pkg2 = new PackageOfFile("com/acme/Class2.class");

    assertEquals(pkg1, pkg2);
    assertEquals(pkg2, pkg1);
    assertEquals(pkg1, pkg1);

    assertEquals(pkg1.hashCode(), pkg2.hashCode());
  }

  @Test
  void notSamePackage() {
    PackageOfFile pkg1 = new PackageOfFile("com/acme/Class.class");
    PackageOfFile pkg2 = new PackageOfFile("org/acme/Class.class");

    assertNotEquals(pkg1, pkg2);
    assertNotEquals(pkg2, pkg1);
    assertEquals(pkg1, pkg1);

    assertNotEquals(pkg1.hashCode(), pkg2.hashCode());
  }

  @Test
  void defaultPackge() {
    PackageOfFile pkg1 = new PackageOfFile("Class.class");
    PackageOfFile pkg2 = new PackageOfFile("Class1.class");

    assertEquals(pkg1, pkg2);
    assertEquals(pkg2, pkg1);
    assertEquals(pkg1, pkg1);

    assertEquals(pkg1.hashCode(), pkg2.hashCode());
  }

}
