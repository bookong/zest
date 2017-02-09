http://central.sonatype.org/pages/apache-maven.html

If your version is a release version (does not end in -SNAPSHOT) and with this setup in place, you can run a deployment to OSSRH and an automated release to the Central Repository with the usual:
```
mvn clean deploy
```