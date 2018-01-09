FROM java
ADD /develog.jar //
RUN apt-get update && apt-get install -y  graphviz
CMD java -Djava.security.egd=file:/dev/./urandom -jar /develog.jar