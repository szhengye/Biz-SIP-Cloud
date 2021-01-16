FROM openjdk:8-jdk-alpine
MAINTAINER ShiZhengye <szhengye@gmail.com>
VOLUME /tmp
ADD dynamic-config.jar dynamic-config.jar
RUN sh -c 'touch /dynamic-config.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /dynamic-config.jar" ]
