#BUILD image
#FROM gradle:8.5.0-jdk17 as builder
##Work directory
#WORKDIR /build
#COPY build.gradle.kts .
#COPY settings.gradle.kts .
#COPY gradlew .
#COPY src /build/src
#COPY .gradle .
#
##Build application
#RUN ./gradlew clean build

# 8-alpine3.17-jre
FROM openjdk:17-jdk-alpine as runtime
EXPOSE 8080
#Set app home folder
ENV APP_HOME /app
#Possibility to set JVM options (https://www.oracle.com/technetwork/java/javase/tech/vmoptions-jsp-140102.html)
ENV JAVA_OPTS=""
#Create base app folder
RUN mkdir $APP_HOME
#Create folder to save configuration files
RUN mkdir $APP_HOME/config
#Create folder with application logs
RUN mkdir $APP_HOME/logs

VOLUME $APP_HOME/logs
VOLUME $APP_HOME/config

WORKDIR $APP_HOME
#Copy executable jar file from the builder image
#COPY --from=builder /build/build/libs/entryeventures-mercury-v1.jar app.jar
COPY build/libs/entryventures-mercury-v1.jar app.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/urandom -jar app.jar" ]
#Second option using shell form:
#ENTRYPOINT exec java $JAVA_OPTS -jar app.jar $0 $@

#
#FROM openjdk:17-jdk-alpine
#
#LABEL maintainer="slade@gmail.com"
#
#WORKDIR /slade
#
#COPY target/mortgage-0.0.1-mercury.jar /slade/mortgage-0.0.1-mercury.jar
#
#ENTRYPOINT ["java", "-jar", "mortgage-0.0.1-mercury.jar"]

# docker run -v /path/on/host/logs:/app/logs -v /path/on/host/config:/app/config -p 6002:6002 your-image-name