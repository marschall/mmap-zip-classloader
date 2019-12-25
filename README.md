mmap() Zip ClassLoader
======================

This is an experiment to build a ClassLoader for zip files that is not based on `java.net.URLClassLoader` / `java.util.jar.JarFile`.

Mandatory
---------
* parallel capable
* multi release JAR

Low Prio / No Idea
------------------
* ZIP64
* `INDEX.LIST` support (seems only useful for [applets](https://docs.oracle.com/en/java/javase/11/docs/specs/jar/jar.html#jar-index))

TODO
----
* multi release JARs
* signed JARs
* permission checks, AccessControlContext

Recommendations
---------------

* Avoid split packages if possible as they increase lookup time and memory footprint.

