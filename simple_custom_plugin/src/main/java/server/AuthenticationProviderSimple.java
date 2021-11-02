package server;

import java.io.IOException;
import java.net.SocketAddress;

import javax.naming.AuthenticationException;

import data.AuthenticationSimpleData;
import org.apache.pulsar.broker.ServiceConfiguration;
import org.apache.pulsar.broker.authentication.AuthenticationDataSource;
import org.apache.pulsar.broker.authentication.AuthenticationProvider;
import org.apache.pulsar.broker.authentication.metrics.AuthenticationMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.SecurityProvider;

public class AuthenticationProviderSimple implements AuthenticationProvider {

    private static final String AUTH_METHOD_NAME = "simple_text";

    private static final String MASTER_USERNAME = "username";
    private static final String MASTER_PASSWORD = "password";

    @Override
    public void initialize(ServiceConfiguration config) throws IOException {
        log.info("Simple Text Auth initialized");
    }

    @Override
    public String getAuthMethodName() {
        return AUTH_METHOD_NAME;
    }

    @Override
    public String authenticate(AuthenticationDataSource authData) throws AuthenticationException {
        SocketAddress clientAddress;
        String username = null;
        String password =  null;

        try {
            if (authData.hasDataFromPeer()) {
                clientAddress = authData.getPeerAddress();
            } else {
                throw new AuthenticationException("Authentication data source does not have a client address");
            }

            log.info("HasDataFromHttp " + authData.hasDataFromHttp());
            log.info("authData.hasDataFromCommand " + authData.hasDataFromCommand());

            if (authData.hasDataFromCommand()) {
                String userNamePassword = authData.getCommandData();
                String[] splited = userNamePassword.split(":");
                if(splited.length == 2){
                    username = splited[0];
                    password = splited[1];
                }
            } else
           if (authData.hasDataFromHttp()) {
               log.info("Data from HTTP " + authData.getHttpHeader(AuthenticationSimpleData.AUTHORIZATION_HEADER));
               String token = authData.getHttpHeader(AuthenticationSimpleData.AUTHORIZATION_HEADER);
               if(token == null){
                   throw new AuthenticationException("Authentication token is null");
               }

               String[] splited = SecurityProvider.parseFromBasicAuthToken(token);
               if(splited != null){
                   username = splited[0];
                   password = splited[1];
               }
               //username = authData.getHttpHeader(AuthenticationSimple.USERNAME);
                //password = authData.getHttpHeader(AuthenticationSimple.PASSWORD);
            } else {
                throw new AuthenticationException("Authentication data source does not have a role token");
            }

            if (username == null) {
                throw new AuthenticationException("Username is null, can't authenticate");
            }
            if (password == null) {
                throw new AuthenticationException("Password is null, Server is Using Athenz Authentication");
            }

            if (username.equals("")) {
                throw new AuthenticationException("Username is empty, can't authenticate");
            }
            if (password.equals("")) {
                throw new AuthenticationException("Password is empty, can't authenticate");
            }

            if (log.isDebugEnabled()) {
                log.debug("Username: {} Password: {} received from Client: {}", username, password, clientAddress);
            }

             if(password.equals(MASTER_PASSWORD)
                    && username.equals(MASTER_USERNAME)){
                return username;
            } else {
                throw new AuthenticationException(
                        String.format("Wrong username or password. Not Authenticated from Client: %s", clientAddress));
            }

        } catch (AuthenticationException exception) {
            AuthenticationMetrics.authenticateFailure(getClass().getSimpleName(), getAuthMethodName(), exception.getMessage());
            throw exception;
        }
    }

    @Override
    public void close() throws IOException {
        //Nothing to close
    }

    private static final Logger log = LoggerFactory.getLogger(AuthenticationProviderSimple.class);
}