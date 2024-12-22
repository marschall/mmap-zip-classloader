package com.github.marschall.mmap;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class ModuleLayerTests {

  @Test
  void installedModules() {
    Set<String> jdkPackages = ModuleLayer.boot().modules().stream()
            .flatMap(module -> module.getPackages().stream().filter(packageName -> module.isExported(packageName)))
            .collect(Collectors.toSet());
    assertNotNull(jdkPackages);
  }

}
