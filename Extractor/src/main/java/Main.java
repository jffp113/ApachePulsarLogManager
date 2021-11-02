import extractor.Extractor;

public class Main {
    public static void main(String[] args) throws Exception {
        String filePath = System.getenv("FILENAME");
        String lineRegex = System.getenv("LINE_REGEX");
        //String appName = System.getenv("APP_NAME");
        String urlService = System.getenv("URL_SERVICE");

        String env = System.getenv("ENVIRONMENT");
        String instance = System.getenv("INSTANCE");
        String technology = System.getenv("TECHNOLOGY");
        int timeBetweenLineReading = Integer.parseInt(System.getenv("TIME_PER_LOG"));
        //int nodeNumber = Integer.parseInt(System.getenv("NODE_NUMBER"));

        String username = System.getenv("USERNAME");
        String password = System.getenv("PASSWORD");

        Extractor c =  Extractor.builder()
                            .setFilepath(filePath)
                            .setLineRegex(lineRegex)
                            .setTimeBetweenLineReading(timeBetweenLineReading)
                            .setEnvironment(env)
                            .setInstance(instance)
                            .setTechnology(technology)
                            .setServiceURL(urlService)
                            .setUsername(username)
                            .setPassword(password)
                            .build();
        c.start();

        Runtime.getRuntime().addShutdownHook(new Thread(c::stop));
    }

}
