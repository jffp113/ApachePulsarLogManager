package extractor;

public class ExtractorBuilder {

    private final Conf conf;

    protected ExtractorBuilder(){
        conf = new Conf();
    }

    public ExtractorBuilder setFilepath(String filepath) {
        conf.setFilepath(filepath);
        return this;
    }

    public ExtractorBuilder setLineRegex(String lineRegex) {
        conf.setLineRegex(lineRegex);
        return this;
    }

    public ExtractorBuilder setTimeBetweenLineReading(int timeBetweenLineReading) {
        conf.setTimeBetweenLineReading(timeBetweenLineReading);
        return this;
    }

    public ExtractorBuilder setEnvironment(String environment) {
        conf.setEnvironment(environment);
        return this;
    }

    public ExtractorBuilder setInstance(String instance) {
        conf.setInstance(instance);
        return this;
    }

    public ExtractorBuilder setTechnology(String technology) {
       conf.setTechnology(technology);
       return this;
    }

    public ExtractorBuilder setServiceURL(String serviceURL) {
        conf.setServiceURL(serviceURL);
        return this;
    }

    public Extractor build(){
        return new Extractor(conf);
    }

}
