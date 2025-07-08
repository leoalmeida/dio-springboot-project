FROM maven:3.9.9
# FROM maven:3.8.2-jdk-8 # for Java 8

ADD . /usr/src/backend
WORKDIR /usr/src/backend
ENTRYPOINT ["mvn", "clean", "package", "spring-boot:run"]
