mmap() Zip ClassLoader
======================

This is an experiment to build a ClassLoader for zip files that is not based on `java.net.URLClassLoader` / `java.util.jar.JarFile`.

Mandatory
---------
* parallel capable

Optional
--------
* concurrent file access (requires `java.nio.channels.FileChannel`)
* aggressive closing of files

Low Prio / No Idea
------------------
* pack200
* ZIP64
* `INDEX.LIST` support (seems pointless, information is available from central directory)

