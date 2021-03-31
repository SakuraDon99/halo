FROM gradle:jdk11 as builder
WORKDIR /builder
COPY . /builder
RUN gradle bootJar -x test

FROM adoptopenjdk:11-jre-hotspot
WORKDIR /app
COPY --from=builder /builder/build/libs/*.jar /app/halo.jar

ENV JVM_XMS="256m" \
    JVM_XMX="256m" \
    JVM_OPTS="" \
    TZ=Asia/Shanghai

RUN ln -sf /usr/share/zoneinfo/$TZ /etc/localtime \
    && echo $TZ > /etc/timezone

ENTRYPOINT java -Xms${JVM_XMS} -Xmx${JVM_XMX} ${JVM_OPTS} -Djava.security.egd=file:/dev/./urandom -server -jar halo.jar