FROM openjdk:8-jdk-alpine
MAINTAINER ShiZhengye <szhengye@gmail.com>
VOLUME /tmp
ADD api-gateway.jar api-gateway.jar
RUN sh -c 'touch /api-gateway.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /api-gateway.jar" ]
