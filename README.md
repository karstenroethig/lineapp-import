(Head)LineApp
=============

A Java application for importing existing headlines form e-mails to the database of a lineapp instance.


Requirements
------------
* Java SDK 1.5+ (for Grails 1.2 or greater)
* Maven 2.x or 3.x (http://http://maven.apache.org)


Run
---

1. Create a `JAVA_HOME` environment variable that points to the location of your JDK

    `set JAVA_HOME=C:\Programme\Java\jdk1.6.0_16`

2. Build it with Maven

    `mvn package`

3. Run it

	`java -cp target/lineapp-import-x.x.x.jar karstenroethig.lineapp.Importer`

4. Celebrate
