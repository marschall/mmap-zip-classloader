package com.github.marschall.mmap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import com.github.marschall.mmap.PackageSubstring.PackageOfClass;

class PackageOfClassTest {
  
  @Test
  void testToString() {
    PackageOfClass pkg = new PackageOfClass("com.acme.Class");
    assertEquals("com/acme", pkg.toString());
  }

  @Test
  void samePackage() {
    PackageOfClass pkg1 = new PackageOfClass("com.acme.Class");
    PackageOfClass pkg2 = new PackageOfClass("com.acme.Class2");

    assertEquals(pkg1, pkg2);
    assertEquals(pkg2, pkg1);
    assertEquals(pkg1, pkg1);

    assertEquals(pkg1.hashCode(), pkg2.hashCode());
  }

  @Test
  void notSamePackage() {
    PackageOfClass pkg1 = new PackageOfClass("com.acme.Class");
    PackageOfClass pkg2 = new PackageOfClass("org.acme.Class");

    assertNotEquals(pkg1, pkg2);
    assertNotEquals(pkg2, pkg1);
    assertEquals(pkg1, pkg1);

    assertNotEquals(pkg1.hashCode(), pkg2.hashCode());
  }
  
  @Test
  void defaultPackge() {
    PackageOfClass pkg1 = new PackageOfClass("Class");
    PackageOfClass pkg2 = new PackageOfClass("Class1");

    assertEquals(pkg1, pkg2);
    assertEquals(pkg2, pkg1);
    assertEquals(pkg1, pkg1);

    assertEquals(pkg1.hashCode(), pkg2.hashCode());
  }

}
