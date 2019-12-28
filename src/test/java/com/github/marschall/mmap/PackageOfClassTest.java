package com.github.marschall.mmap;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PackageOfClassTest {

  @Test
  void samePackage() {
    PackageOfClass class1 = new PackageOfClass("com.acme.Class");
    PackageOfClass class2 = new PackageOfClass("com.acme.Class2");

    assertEquals(class1, class2);
    assertEquals(class2, class1);
    assertEquals(class1, class1);

    assertEquals(class1.hashCode(), class2.hashCode());
  }

  @Test
  void notSamePackage() {
    PackageOfClass class1 = new PackageOfClass("com.acme.Class");
    PackageOfClass class2 = new PackageOfClass("org.acme.Class");

    assertNotEquals(class1, class2);
    assertNotEquals(class2, class1);
    assertEquals(class1, class1);

    assertNotEquals(class1.hashCode(), class2.hashCode());
  }
  
  @Test
  void defaultPackge() {
    PackageOfClass class1 = new PackageOfClass("Class");
    PackageOfClass class2 = new PackageOfClass("Class1");

    assertEquals(class1, class2);
    assertEquals(class2, class1);
    assertEquals(class1, class1);

    assertEquals(class1.hashCode(), class2.hashCode());
  }

}
