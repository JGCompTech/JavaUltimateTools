# JavaUltimateTools v1.3.0 [![Build Status](https://travis-ci.org/JGCompTech/JavaUltimateTools.svg?branch=master)](https://travis-ci.org/JGCompTech/JavaUltimateTools) [![Dependency Status](https://www.versioneye.com/user/projects/58e2fbb124ef3e003b526de5/badge.svg?style=flat-square)](https://www.versioneye.com/user/projects/58e2fbb124ef3e003b526de5) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.jgcomptech.tools/java-ultimate-tools/badge.svg?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/com.jgcomptech.tools/java-ultimate-tools/) [![Javadocs](http://www.javadoc.io/badge/com.jgcomptech.tools/java-ultimate-tools.svg?style=flat-square)](http://www.javadoc.io/doc/com.jgcomptech.tools/java-ultimate-tools)

JavaUltimateTools is a large repository of scripts for use in any Java program. It contains the following:
- OSInfo - Contains many classes that return information about the current Windows installation. This includes Architecture, Edition, Name, Product Key, Service Pack, User Info and Version.
- HWInfo - Contains many classes that return information about the current computer hardware. This includes BIOS, Network, OEM, Processor, RAM and Storage.
- SecurityTools - Contains methods surrounding hashing and encryption. Includes methods using MD5, SHA1, SHA256, SHA384 and SHA512. Also includes encryption/decryption with RSA.
- CommandInfo - Allows you to run any console command and will return the result to a string to use within your program. You can also run the command elevated and it will open in a new cmd window and show the results. Note: If elevated, result cannot be returned as a string.
- MessageBox and Login dialogs - Dialogs to use in JavaFX applications
- DatabaseTools - Allows communication with SQLite and H2 databases
- And Much More!

If you would like a more details check out the [CLASSDESCRIPTION.md](https://github.com/JGCompTech/JavaUltimateTools/blob/master/CLASSDESCRIPTION.md) file. I will be updating this with more documentation soon.

If you would like to view the JavaDoc info it is hosted at [github.io](https://jgcomptech.github.io/JavaUltimateTools/) and at [javadoc.io](http://www.javadoc.io/doc/com.jgcomptech.tools/java-ultimate-tools). This is what is stored in the doc folder in the project.

# Development
Want to contribute? Great!
Any help with development is greatly appreciated. If you want to add something or fix any typos please submit a pull request and if it is helpful it may be merged. Please check out our [Code of Conduct for Contributors](https://github.com/JGCompTech/JavaUltimateTools/blob/master/code-of-conduct.md).

# Download
**[Download v1.3.0](https://github.com/JGCompTech/JavaUltimateTools/releases/tag/v1.3)**

The changelog can be found [here](https://github.com/JGCompTech/JavaUltimateTools/blob/master/Changelog.txt)

# Using with Maven
If you are familiar with [Maven](http://maven.apache.org), add the following XML
fragments into your pom.xml file. With those settings, your Maven will automatically download our library into your local Maven repository, since our libraries are synchronized with the [Maven central repository](http://repo1.maven.org/maven2/com/jgcomptech/tools/java-ultimate-tools/).

    <dependencies>
       <dependency>
          <groupId>com.jgcomptech.tools</groupId>
         <artifactId>java-ultimate-tools</artifactId>
         <version>1.3.0</version>
       </dependency>
    </dependencies>

# License
[![Creative Commons License](https://i.creativecommons.org/l/by/4.0/88x31.png)](http://creativecommons.org/licenses/by/4.0/)

JavaUltimateTools by J&G CompTech is licensed under a [Creative Commons Attribution 4.0 International License](http://creativecommons.org/licenses/by/4.0/).
