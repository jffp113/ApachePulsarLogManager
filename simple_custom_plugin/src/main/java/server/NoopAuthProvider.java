package server;

import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import org.apache.pulsar.broker.authentication.AuthenticationDataSource;
import org.apache.pulsar.functions.auth.FunctionAuthData;
import org.apache.pulsar.functions.auth.KubernetesFunctionAuthProvider;
import org.apache.pulsar.functions.instance.AuthenticationConfig;
import org.apache.pulsar.functions.proto.Function;
import java.util.Optional;

public class NoopAuthProvider implements KubernetesFunctionAuthProvider {

    public NoopAuthProvider() {
    }

    public void initialize(CoreV1Api coreClient) {
        //No-op
    }

    public void setCaBytes(byte[] caBytes) {
        //No-op
    }

    public void setNamespaceProviderFunc(java.util.function.Function<Function.FunctionDetails, String> getNamespaceFromDetails) {
        //No-op
    }

    public void configureAuthDataStatefulSet(V1StatefulSet statefulSet, Optional<FunctionAuthData> functionAuthData) {
        //No-op
    }

    public void configureAuthenticationConfig(AuthenticationConfig authConfig, Optional<FunctionAuthData> functionAuthData) {
        //No-op
    }

    public Optional<FunctionAuthData> cacheAuthData(Function.FunctionDetails funcDetails, AuthenticationDataSource authenticationDataSource) {
        //No-op
        return Optional.of(FunctionAuthData.builder().data("no-op data".getBytes()).build());
    }

    public void cleanUpAuthData(Function.FunctionDetails funcDetails, Optional<FunctionAuthData> functionAuthData) throws Exception {
        //No-op
    }

    public Optional<FunctionAuthData> updateAuthData(Function.FunctionDetails funcDetails, Optional<FunctionAuthData> existingFunctionAuthData, AuthenticationDataSource authenticationDataSource) throws Exception {
        //No-op
        return Optional.of(FunctionAuthData.builder().data("no-op data".getBytes()).build());
    }
}