FROM maven:3-openjdk-11 As compiler
WORKDIR /project
COPY ./pom.xml /project
#RUN mvn dependency:go-offline
COPY src /project/src
COPY start.sh /project/
RUN chmod +x start.sh
RUN mvn clean package -DskipTests
CMD ["./start.sh"]

#TODO review optimized version
#FROM maven:3-openjdk-11 As compiler
#WORKDIR /project
#COPY ./pom.xml /project
#RUN mvn dependency:go-offline
#COPY src /project/src
#RUN mvn clean package -DskipTests

#FROM alpine
#RUN apk add openjdk11-jre
#WORKDIR /project
#COPY --from=compiler /project/target/*.jar ./
#CMD ["java","-jar","./Sidecar-1.0-SNAPSHOT-jar-with-dependencies.jar"]
