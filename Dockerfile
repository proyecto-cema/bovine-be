FROM java:8
# Add the service itself
ARG JAR_ARTIFACT
ADD target/${JAR_ARTIFACT} /bovine-be.jar
#Execute
CMD java -Djava.security.egd=file:/dev/./urandom $JAVA_OPTS -jar bovine-be.jar
