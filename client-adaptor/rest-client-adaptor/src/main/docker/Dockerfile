FROM openjdk:8-jdk-alpine
MAINTAINER ShiZhengye <szhengye@gmail.com>
VOLUME /tmp
ADD rest-client-adaptor.jar rest-client-adaptor.jar
RUN sh -c 'touch /rest-client-adaptor.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /rest-client-adaptor.jar" ]
