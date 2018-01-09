FROM java
ADD /develog.jar //
ENTRYPOINT ["java", "-jar", "/develog.jar"]
RUN apt-get update && \
    apt-get install -y  graphviz
