package extractor;

public class Conf {

    //Path to the log file
    private String filepath;

    //Regex that indicates the beginning of
    //a new log line
    private String lineRegex;

    //Time between new line reading
    private int timeBetweenLineReading;

    private String environment;
    private String instance;
    private String technology;

    private String serviceURL;

    private String username;
    private String password;

    protected Conf(){
        filepath = "server.log";
        lineRegex = "*.";
        timeBetweenLineReading = 0;
        environment = "test";
        technology = "testApp";
        instance = "1";
        serviceURL = "pulsar://localhost:6650";
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getLineRegex() {
        return lineRegex;
    }

    public void setLineRegex(String lineRegex) {
        this.lineRegex = lineRegex;
    }

    public int getTimeBetweenLineReading() {
        return timeBetweenLineReading;
    }

    public void setTimeBetweenLineReading(int timeBetweenLineReading) {
        this.timeBetweenLineReading = timeBetweenLineReading;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public String getServiceURL() {
        return serviceURL;
    }

    public void setServiceURL(String serviceURL) {
        this.serviceURL = serviceURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
