package client;

import data.AuthenticationSimpleData;
import org.apache.pulsar.client.api.Authentication;
import org.apache.pulsar.client.api.AuthenticationDataProvider;
import org.apache.pulsar.client.api.EncodedAuthenticationParameterSupport;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.impl.AuthenticationUtil;
import util.Helpers;

import java.io.IOException;
import java.util.Map;

public class AuthenticationSimple implements Authentication, EncodedAuthenticationParameterSupport {

    private static final long serialVersionUID = 1L;


    private static final String AUTH_METHOD_NAME = "simple_text";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    //User credentials
    private String username;
    private String password;

    public AuthenticationSimple() {
        username = null;
        password = null;
    }

    public AuthenticationSimple(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String getAuthMethodName() {
        return AUTH_METHOD_NAME;
    }

    @Override
    public AuthenticationDataProvider getAuthData() throws PulsarClientException {
        return new AuthenticationSimpleData(username,password);
    }

    @Override
    public void configure(Map<String, String> authParams) {
        setAuthParams(authParams);
    }

    @Override
    public void configure(String encodedAuthParamString) {
       if (Helpers.isBlank(encodedAuthParamString)) {
            throw new IllegalArgumentException("authParams must not be empty " + encodedAuthParamString);
        }
        try {
            setAuthParams(Helpers.configureFromJsonBASE64String(encodedAuthParamString));
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to parse authParams", e);
        }
    }

    private void setAuthParams(Map<String, String> authParams){
       this.username = authParams.get(USERNAME);
       this.password = authParams.get(PASSWORD);

        //this.password = "password";
        //this.username = "username";

        if(isNullOrBlank(username) || isNullOrBlank(password)){
            throw new IllegalArgumentException("password or username should not be null or blank" + authParams.size());
        }
    }

    @Override
    public void start() throws PulsarClientException {
        //Do nothing, there is nothing to be inited
    }

    @Override
    public void close() throws IOException {
        //Nothing to close
    }

    private boolean isNullOrBlank(String value){
        return value == null || value.equals("");
    }
}