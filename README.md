# JavaUltimateTools v1.5.1
[![Build Status](https://travis-ci.org/JGCompTech/JavaUltimateTools.svg?branch=master)](https://travis-ci.org/JGCompTech/JavaUltimateTools) [![CircleCI](https://circleci.com/gh/JGCompTech/JavaUltimateTools.svg?style=svg)](https://circleci.com/gh/JGCompTech/JavaUltimateTools) [![Language grade: Java](https://img.shields.io/lgtm/grade/java/g/JGCompTech/JavaUltimateTools.svg?logo=lgtm&logoWidth=18)](https://lgtm.com/projects/g/JGCompTech/JavaUltimateTools/context:java) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jgcomptech.tools/java-ultimate-tools/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.jgcomptech.tools/java-ultimate-tools/) [![Javadocs](http://www.javadoc.io/badge/com.jgcomptech.tools/java-ultimate-tools.svg?style=flat-square)](http://www.javadoc.io/doc/com.jgcomptech.tools/java-ultimate-tools)

Java Ultimate Tools is a large repository of scripts for use in any Java program. It contains the following:
- OSInfo - Contains many classes that return information about the current OS installation. This includes Architecture, Edition, Name, Product Key, Service Pack, User Info and Version.
- HWInfo - Contains many classes that return information about the current computer hardware. This includes BIOS, Network, OEM, Processor, RAM and Storage.
- SecurityTools - Contains methods surrounding hashing and encryption. Includes methods using MD5, SHA1, SHA256, SHA384 and SHA512. Also includes encryption/decryption with RSA.
- CommandInfo - Allows you to run any console command and will return the result to a string to use within your program. You can also run the command elevated and it will open in a new cmd window and show the results. Note: If elevated, result cannot be returned as a string.
- MessageBox and Login dialogs - Dialogs to use in JavaFX applications
- FXML Dialog Wrapper - Class to wrap a FXML dialog object, reducing the amount of required code
- DatabaseTools - Allows communication with H2, HyperSQL and SQLite databases
- SQL Statement Builders - Allows for using methods instead of native SQL code to generate Prepared Statements
- User Account Management Tools - Includes User Roles, Sessions and Permissions
- Custom Event Manager System - Creates Events similar to the JavaFX Events but not using any JavaFX classes
- Utility Classes - Includes classes for managing collections, numbers and strings
- And Much More!

**NOTE: This Project Has Now Been Updated To Use Java 10!!!**

# Development
Want to contribute? Great!
Any help with development is greatly appreciated. If you want to add something or fix any issues please submit a pull request and if it is helpful it may be merged. Please check out our [Code of Conduct for Contributors](https://github.com/JGCompTech/JavaUltimateTools/blob/master/code-of-conduct.md).

# Documentation
The documentation for JUT is currently a work in progress and new changes will be occurring soon.
To access the documentation site go to: [https://jut-docs.jgcomptech.com](https://jut-docs.jgcomptech.com).
If you would like to view the JavaDoc info, it is hosted at [github.io(Current GitHub Branch)](https://jgcomptech.github.io/JavaUltimateTools/) and at [javadoc.io(Current Maven Release)](http://www.javadoc.io/doc/com.jgcomptech.tools/java-ultimate-tools). The github.io version is what is stored in the doc folder in the project.

# Download
**[Download v1.5.1](https://github.com/JGCompTech/JavaUltimateTools/releases/tag/v1.5.1)**

The changelog can be found [here](https://jut-docs.jgcomptech.com/changelog/)

# Using with Maven
If you are familiar with [Maven](http://maven.apache.org), add the following XML
fragments into your pom.xml file. With those settings, your Maven will automatically download our library into your local Maven repository, since our libraries are synchronized with the [Maven central repository](http://repo1.maven.org/maven2/com/jgcomptech/tools/java-ultimate-tools/).

    <dependencies>
       <dependency>
          <groupId>com.jgcomptech.tools</groupId>
         <artifactId>java-ultimate-tools</artifactId>
         <version>1.5.1</version>
       </dependency>
    </dependencies>

# License
[![Creative Commons License](https://i.creativecommons.org/l/by/4.0/88x31.png)](http://creativecommons.org/licenses/by/4.0/)

JavaUltimateTools by J&G CompTech is licensed under a [Creative Commons Attribution 4.0 International License](http://creativecommons.org/licenses/by/4.0/).

&copy;2018 J&amp;G CompTech
