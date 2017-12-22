# Welcome to the QuizZz project!

This application is being developed as a Spring Boot learning project, in which we'll be diving into several components of the Spring family. 

The idea of the application is simple, it's a quiz application in which users can create and publish quizzes. Other users can then play those quizzes. To make the application a bit more interesting, it also includes a user management module with functionality such as email sign up, login or forgot my password. It then uses Spring Security and Spring AOP for authentication and authorization purposes. Users are required to open an account in order to create and edit their own quizzes, but they can play without the need for an account. In addition, users can save their quizzes as drafts and publish them later on, provided the quiz is well formed at the time of publishing.

The application includes a simple but functional web frontend using AngularJS and Bootstrap, but this is not the main focus of the project.

This is intended to be a learning project so please, feel free to fork this repository and play around with the code!

## Getting Started

All the code required to run this project is available in this Git repository. You can either download it as a zip file from Github or run:

```bash
$ git clone https://github.com/oojorgeoo89/QuizZz.git
```

### Environment

In order to run QuizZz you will need:

* [Java 8](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html) - Main Backend Language
* [Maven](https://maven.apache.org/) - Dependency Management
* [MySQL](https://www.mysql.com) - The default RDBMS.

In addition, there are a few optional yet recommended installations:

* [Git](https://git-scm.com)
* Your favorte IDE. I am using [Spring STS](https://spring.io/tools/sts/all), which is based on Eclipse, but feel free to use whichever you find more convenient.
* An SMTP client such as [FakeSMTP](http://nilhcem.com/FakeSMTP/). In order to test and verify user registrations, it is easier to use a stubbed SMTP server than a real email provider.

### Prerequisites

#### Database Setup

Before starting the application, you will need to set up the database. By default, QuizZz attempts to connect to a database called QuizZz in localhost:3306, or one called QuizZzTest in the same location for the E2E automatic tests. You can change the default location and name of the databases in application.properties and application-test.properties respectively.

If you don't wish to set up a MySQL database, QuizZz also comes with H2 support out of the box. In order to switch to an h2 database, you need to activate the h2 profile. If you are unsure of how to accomplish this, please refer to [this document](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html).

#### Start SMTP Server

In order to start FakeSMTP, download the jar file from [the official website](http://nilhcem.com/FakeSMTP/), run the following command as root and click on "Start server":

```bash
$ sudo java -jar fakeSMTP-2.0.jar
```

The reason to run as root is that SMTP's well known port is port 25, which is typically protected by the OS. However, if you are not comfortable giving FakeSMTP root access, you will want to start FakeSMTP on any port higher than 1024. You can use the -p option to start it on any port you want. Just remember to edit "mail.port" inside application.properties in order to point QuizZz to the right address.

```bash
$ java -jar fakeSMTP-2.0.jar -p <port_number>
```

### Time to run the application!

You can run the application either using maven on the command line or using your IDE to import and run the code. We'll be covering Maven and Spring STS in this section.

#### Using Maven

If you don't want to go through the process of using an IDE and just want to get the project running to explore it, navigate to the directory where you downloaded the source code and run:

```bash
$ mvn spring-boot:run
```

If everything went well, you should be able to access the web app here: http://localhost:8080

#### Using Spring STS

Spring STS makes it simple to import and run Spring Boot projects. In order to do so:

##### Import the project:
1. Open Spring STS.
2. Navigate to "File - Import".
3. In the Wizard, look for "Existing Maven Projects" and click Next.
4. Click Browse and select the root directory of the source code.
5. Click on Finish.

##### Run the project:

Before hitting run, lets make sure all the Maven dependencies are in place:

1. Open the Package Explorer.
2. Right click on the QuizZz project.
3. Navigate to "Maven - Update Project".

Once the dependencies are downloaded and installed, we have two options to run:

1. Run via "Package Explorer":
   1. In the Package Explorer, right click on the QuizZz project.
   2. Navigate to "Run As - Spring Boot App".
2. Run via "Boot Dashboard":
   1. Open the "Boot Dashboard".
   2. Expand "local".
   3. Select QuizZz.
   4. Hit the Play button.

If everything went well, you should be able to access the web app here: http://localhost:8080

#### Generating WAR file

To generate the WAR file and start up the server, run:

```bash
$ mvn clean package
$ cd target
$ java -jar QuizZz-0.0.1-SNAPSHOT.war
```

## Running the tests

At the moment, there are Unit Tests using JUnit and Mockito as well as end to end tests using MockMvc for the REST API.

### Unit Tests

Unit Tests are written in JUnit/Mockito and are located under the package "jorge.rv.QuizZz.unitTests". They cover the Service layer of the application, which is where most of the code resides.

### REST API Tests

In order to test the REST API, there are E2E tests under "jorge.rv.QuizZz.integration". At the moment, there is a single Life Cycle test. The idea of this test is to bring up an app from scratch, with an empty DB and cover a range of scenarios a user is likely to go through. In order to make it more realistic, the test is only allowed to interact with the database via the REST API and shoudln't load any mock data into the database directly. The test has grown quite long and needs refactoring. Other smaller tests would also be desirable.

### Webpage Tests

At the moment there are no automatic tests available for the actual webpages. Automated tests using Selenium is in the to do list.

### Test Coverage

Test Coverage is measured using EclEmma. The minimum coverage for delivery is 90% between the Unit Tests and the E2E tests. Having said that, the actual number is not as important as writing tests that actually cover real test cases.

### Static Analysis

On top of a clean set of tests, FindBugs shall not produce errors with the reporting category set at level 15.

## Tehcnologies Used

### Backend

#### Java

* [Java 8](http://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html) - Main Backend Language
* [Maven](https://maven.apache.org/) - Dependency Management

#### The Spring Family

* [Spring Boot](https://projects.spring.io/spring-boot/) - The Framework of Frameworks
* [Spring MVC](https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html) - The Web framework
* [Spring Security](https://projects.spring.io/spring-security/) - Security, Authentication, Authorization
* [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/) - Abstraction Layer on top of JPA

#### Databases

* [MySQL](https://www.mysql.com) - The default RDBMS.
* [H2](http://www.h2database.com/html/main.html) - Simple RDBMS to use for testing purposes.
* [Hibernate](http://findbugs.sourceforge.net) - ORM

#### Testing

* [JUnit](http://junit.org/junit5/) - The main Testing Framework.
* [Mockito](http://site.mockito.org) - Mocking framework for Unit Tests.
* [Hamcrest](http://hamcrest.org) - Matcher framework for automated tests.
* [MockMvc](https://docs.spring.io/spring-security/site/docs/current/reference/html/test-mockmvc.html) - Test entry point for Spring MVC controllers.
* [GreenMail](http://www.icegreen.com/greenmail/) - Stub SMTP Server to validate email functionality.

#### Others

* [Thymeleaf](http://www.thymeleaf.org) - Templating language used to render the final web page.
* [EclEmma](http://www.eclemma.org) - Test Coverage tool for Java.
* [FindBugs](http://findbugs.sourceforge.net) - Static Analysis tool for Java.

### Frontend

* [HTML/CSS/JS](https://en.wikipedia.org/wiki/HTML) - Just HTML/CSS/JS. 
* [Bootstrap](http://getbootstrap.com/2.3.2/) - Design Framework.
* [AngularJS](https://angularjs.org) - Frontend framework.

## Authors

* **Jorge Rodriguez** - *Main Developer* - [oojorgeoo89](https://github.com/oojorgeoo89)

## What next?

* Refactoring and improvement of the REST API automated tests.
* Automatic web page testing with Selenium.
* UI polishing. There are a few UI elements out of place and some dummy data still displayed.
* QuizZz's Wiki. This readme file contains some high level information of the project. However, with this being a learning project, it needs a Wiki page with more technical details about how the different features have been designed and coded.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
