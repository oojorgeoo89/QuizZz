FROM java:8

EXPOSE 8080

#install Spring Boot artifact
VOLUME /tmp
ADD /maven/QuizZz-0.0.1-SNAPSHOT.jar QuizZz.jar
RUN sh -c 'touch /QuizZz.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/QuizZz.jar"]