package data;

import org.apache.pulsar.client.api.AuthenticationDataProvider;
import util.SecurityProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.*;
import java.util.Set;

public class AuthenticationSimpleData implements AuthenticationDataProvider {
    private static final long serialVersionUID = 1L;

    public static final String AUTHORIZATION_HEADER = "Authorization";

    protected String username;
    protected String password;

    public AuthenticationSimpleData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean hasDataForHttp() {
        return true;
    }

    @Override
    public Set<Entry<String, String>> getHttpHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put(AUTHORIZATION_HEADER, SecurityProvider.createBasicAuthToken(username,password));
        return headers.entrySet();
    }

    @Override
    public boolean hasDataFromCommand() {
        return true;
    }

    @Override
    public String getCommandData() {
        return username + ":" + password;
    }
}