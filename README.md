![zest](http://bookong.github.io/zest/images/logo.png)

[![Build Status](https://travis-ci.org/bookong/zest.svg?branch=master)](https://travis-ci.org/bookong/zest)
[![Coverage Status](https://coveralls.io/repos/github/bookong/zest/badge.svg?branch=master)](https://coveralls.io/github/bookong/zest?branch=master)
[![GitHub release](https://img.shields.io/github/release/bookong/zest.svg)](https://github.com/bookong/zest/releases)
[![Maven central](https://maven-badges.herokuapp.com/maven-central/com.github.bookong/zest/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.bookong/zest)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/https/oss.sonatype.org/com.github.bookong/zest.svg)](https://oss.sonatype.org/content/repositories/snapshots/com/github/bookong/zest/)
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

Zest is an easy-to-use unit testing tool based on Spring. It separates test data and test code, and automatically verifies the results of RMDB (SQL) and MongoDB.

# Notice

* 在 2021.2.4 之后版本的 IntelliJ IDEA 编辑器。 如果继续使用 [JUnit4 的方式](https://github.com/bookong/zest-example/tree/master/spring-mvc) ，将无法在 IDE 环境中执行测试 ( 报错 `No tests found matching Method` )
  * 需要改用 maven 命令执行 ( `mvn test -Dtest=testpackage.TestClass#testMethod` )
  * 原因是从 2021.3 开始的版本 IntelliJ IDEA 编辑器强化了对 "parameterized test" 的验证，而 JUnt4 中 Zest 实现方式接近 `魔改`。但使用 [JUnit5 的方式](https://github.com/bookong/zest-example/tree/master/spring-boot) 没有问题
* IntelliJ IDEA editor in versions after 2021.2.4. If you continue to [use junit4](https://github.com/bookong/zest-example/tree/master/spring-mvc) .The test cannot be executed in the IDE environment (error `No tests found matching Method`) 
  * You need to use the maven command instead ( `mvn test -Dtest=testpackage.TestClass#testMethod` )
  * The reason is that the IntelliJ IDEA editor since 2021.3 has strengthened the verification of "parameterized test", while the implementation of Zest in JUnt4 is close to the `magic change` . But there is no problem [using JUnit5](https://github.com/bookong/zest-example/tree/master/spring-boot)

# Documentation

- [Documentation Home](https://github.com/bookong/zest/wiki)
- [文档首页](https://github.com/bookong/zest/wiki/Home_zh_CN)

# Demo

- [With Spring Boot 2.X](https://github.com/bookong/zest-example/tree/master/spring-boot)
- [With Spring MVC](https://github.com/bookong/zest-example/tree/master/spring-mvc)

# Download

- [maven][1]
- [the latest JAR][2]

[1]: https://repo1.maven.org/maven2/com/github/bookong/zest/
[2]: https://search.maven.org/remote_content?g=com.github.bookong&a=zest&v=LATEST

# License

Zest is released under the [Apache 2.0 license](LICENSE).
