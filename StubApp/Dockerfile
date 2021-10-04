FROM alpine
COPY ./start.sh . 
RUN chmod +x start.sh
RUN mkdir logs
COPY ./server.log .
CMD ["./start.sh"]
