package sidecar;

public class SidecarBuilder {

    private final Conf conf;

    protected SidecarBuilder(){
        conf = new Conf();
    }

    public SidecarBuilder setFilepath(String filepath) {
        conf.setFilepath(filepath);
        return this;
    }

    public SidecarBuilder setLineRegex(String lineRegex) {
        conf.setLineRegex(lineRegex);
        return this;
    }

    public SidecarBuilder setTimeBetweenLineReading(int timeBetweenLineReading) {
        conf.setTimeBetweenLineReading(timeBetweenLineReading);
        return this;
    }

    public SidecarBuilder setEnvironment(String environment) {
        conf.setEnvironment(environment);
        return this;
    }

    public SidecarBuilder setInstance(String instance) {
        conf.setInstance(instance);
        return this;
    }

    public SidecarBuilder setTechnology(String technology) {
       conf.setTechnology(technology);
       return this;
    }

    public SidecarBuilder setServiceURL(String serviceURL) {
        conf.setServiceURL(serviceURL);
        return this;
    }

    public Sidecar build(){
        return new Sidecar(conf);
    }

}
