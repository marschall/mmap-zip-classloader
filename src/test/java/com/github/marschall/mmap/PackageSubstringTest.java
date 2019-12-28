package com.github.marschall.mmap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import com.github.marschall.mmap.PackageSubstring.PackageOfClass;
import com.github.marschall.mmap.PackageSubstring.PackageOfFile;

class PackageSubstringTest {

  @Test
  void samePackage() {
    PackageSubstring pkg1 = new PackageOfClass("com.acme.Class");
    PackageSubstring pkg2 = new PackageOfFile("com/acme/Class2.class");

    assertEquals(pkg1, pkg2);
    assertEquals(pkg2, pkg1);
    assertEquals(pkg1, pkg1);

    assertEquals(pkg1.hashCode(), pkg2.hashCode());
  }

  @Test
  void notSamePackage() {
    PackageSubstring pkg1 = new PackageOfClass("com.acme.Class.class");
    PackageSubstring pkg2 = new PackageOfFile("org/acme/Class.class");

    assertNotEquals(pkg1, pkg2);
    assertNotEquals(pkg2, pkg1);
    assertEquals(pkg1, pkg1);

    assertNotEquals(pkg1.hashCode(), pkg2.hashCode());
  }

  @Test
  void defaultPackge() {
    PackageSubstring pkg1 = new PackageOfClass("Class");
    PackageSubstring pkg2 = new PackageOfFile("Class1.class");

    assertEquals(pkg1, pkg2);
    assertEquals(pkg2, pkg1);
    assertEquals(pkg1, pkg1);

    assertEquals(pkg1.hashCode(), pkg2.hashCode());
  }

}
