(Head)LineApp :: Import
=======================

A Java application for importing existing headlines form e-mails to the database of a lineapp instance.


Requirements
------------
* Java SDK 1.5+
* Maven 2.x or 3.x ([http://maven.apache.org](http://maven.apache.org))


Build
-----

1. Make sure you installed Maven 2.x or higher

2. Create a `JAVA_HOME` environment variable that points to the location of your JDK

    `set JAVA_HOME=C:\Programme\Java\jdk1.6.0_16`

3. Build it with Maven

    `mvn package`

4. Distribute the product `target/lineapp-import-x.x.x.zip`


Run
---

1. Unzip the product `lineapp-import-x.x.x.zip`

2. Change to the unzipped directory

	`cd lineapp-import-x.x.x`

3. Edit `dbconfig.properties`, enter the properties for your lineapp database

4. Run it

	`Importer.bat path/to/dir`
