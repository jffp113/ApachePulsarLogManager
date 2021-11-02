FROM openjdk:11 as builder

RUN apt-get update
RUN apt-get install maven -y

WORKDIR project
COPY ./pom.xml /project
RUN mvn dependency:go-offline
COPY src /project/src
RUN mvn clean package -DskipTests

FROM apachepulsar/pulsar:2.8.1

ENV authParams=eyJ1c2VybmFtZSI6InVzZXJuYW1lIiwicGFzc3dvcmQiOiJwYXNzd29yZCJ9
ENV authPlugin=client.AuthenticationSimple
RUN ./bin/apply-config-from-env.py ./conf/client.conf

RUN mkdir /pulsar/custom_plugins
COPY --from=builder /project/target/simple_custom_plugin-1.0-SNAPSHOT.jar /pulsar/lib/
COPY ./jars/ /pulsar/lib/
#ENV CLASSPATH=/pulsar/custom_plugins/simple_custom_plugin-1.0-SNAPSHOT.jar

#Plugin client


#ENV PF_clientAuthenticationPlugin=client.AuthenticationSimple
#ENV PF_clientAuthenticationParameters=eyJ1c2VybmFtZSI6InVzZXJuYW1lIiwicGFzc3dvcmQiOiJwYXNzd29yZCJ9
#RUN bin/gen-yml-from-env.py conf/functions_worker.yml


#Plugin specific configurations Broker
#ENV authenticationEnabled=true
#ENV authenticationProviders=server.AuthenticationProviderSimple


#Plugin functions
#ENV brokerClientAuthenticationEnabled=true
#ENV brokerClientAuthenticationPlugin=client.AuthenticationSimple
#ENV brokerClientAuthenticationParameters=eyJ1c2VybmFtZSI6InVzZXJuYW1lIiwicGFzc3dvcmQiOiJwYXNzd29yZCJ9
#{"username":"username","password":"password"}

#Proxy
#ENV brokerClientAuthenticationPlugin=client.AuthenticationSimple
#ENV brokerClientAuthenticationParameters=eyJ1c2VybmFtZSI6InVzZXJuYW1lIiwicGFzc3dvcmQiOiJwYXNzd29yZCJ9

#Broker
#ENV brokerClientAuthenticationPlugin=client.AuthenticationSimple
#ENV brokerClientAuthenticationParameters=eyJ1c2VybmFtZSI6InVzZXJuYW1lIiwicGFzc3dvcmQiOiJwYXNzd29yZCJ9



#RUN ./bin/apply-config-from-env.py ./conf/broker.conf
#RUN ./bin/apply-config-from-env.py ./conf/standalone.conf
#RUN ./bin/apply-config-from-env.py ./conf/client.conf

#RUN echo "brokerClientAuthenticationEnabled: true" >> ./conf/functions_worker.yml
#RUN echo "authenticationEnabled: true" >> ./conf/functions_worker.yml

#RUN echo "PF_clientAuthenticationPlugin: client.AuthenticationSimple" >> ./conf/functions_worker.yml
#RUN echo "clientAuthenticationParameters: eyJ1c2VybmFtZSI6InVzZXJuYW1lIiwicGFzc3dvcmQiOiJwYXNzd29yZCJ9" >> ./conf/functions_worker.yml

#RUN echo "brokerClientAuthenticationEnabled: false" >> ./conf/functions_worker.yml
#RUN echo "brokerClientAuthenticationPlugin: client.AuthenticationSimple" >> ./conf/functions_worker.yml
#RUN echo "brokerClientAuthenticationParameters: eyJ1c2VybmFtZSI6InVzZXJuYW1lIiwicGFzc3dvcmQiOiJwYXNzd29yZCJ9" >> ./conf/functions_worker.yml


