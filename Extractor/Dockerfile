FROM security_dependency as tmp
#This image is imporant to get the m2 dependency
FROM maven:3-openjdk-11 As compiler
WORKDIR /project
COPY --from=tmp /root/.m2/repository/pt /root/.m2/repository/pt/
COPY ./pom.xml /project
RUN mvn dependency:go-offline
COPY src /project/src
COPY start.sh /project/
RUN chmod +x start.sh
RUN mvn clean package -DskipTests
CMD ["./start.sh"]