package gews;

import java.io.IOException;

import gews.config.Config;
import gews.http.StandaloneWebServer;

public class Main {
	
	public static void main(String[] args) throws IOException {
		initConfig(args);
		
		new StandaloneWebServer();
	}
	
	private static void initConfig(String[] args) {
		if (Config.DEFAULT_CONFIG_FILE.exists()) {
			Config.loadProperties(Config.DEFAULT_CONFIG_FILE);
		}

		Config.parseProperties(args);
		
		if (Config.GENERATE_PROPERTIES.getBooleanValue()) {
			Config.generatePropertiesFile(Config.DEFAULT_CONFIG_FILE);
		}
	}

}
