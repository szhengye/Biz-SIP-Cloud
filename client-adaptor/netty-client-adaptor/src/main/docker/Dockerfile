FROM openjdk:8-jdk-alpine
MAINTAINER ShiZhengye <szhengye@gmail.com>
VOLUME /tmp
ADD netty-client-adaptor.jar netty-client-adaptor.jar
RUN sh -c 'touch /netty-client-adaptor.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /netty-client-adaptor.jar" ]
