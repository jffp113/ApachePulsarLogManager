FROM alpine
ENV DOCKERVERSION=20.10.8

RUN apk add curl openssh-client
RUN curl -fsSLO https://download.docker.com/linux/static/stable/x86_64/docker-${DOCKERVERSION}.tgz \
  && tar xzvf docker-${DOCKERVERSION}.tgz --strip 1 \
                 -C /usr/local/bin docker/docker \
  && rm docker-${DOCKERVERSION}.tgz

#COPY /home/${USER}/.ssh/id_rsa /root/.ssh/id_rsa.pub
COPY ./start.sh . 
RUN chmod +x start.sh
RUN mkdir logs
COPY ./server.log .
CMD ["./start.sh"]
