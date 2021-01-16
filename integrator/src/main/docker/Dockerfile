FROM openjdk:8-jdk-alpine
MAINTAINER ShiZhengye <szhengye@gmail.com>
VOLUME /tmp
ADD integrator.jar integrator.jar
RUN sh -c 'touch /integrator.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /integrator.jar" ]
