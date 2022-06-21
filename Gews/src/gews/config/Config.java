package gews.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public enum Config {
	
	HELP("help", "false", "--help", false, "\t\t\tPrint this help."),
	
	PORT("port", "65000", "--port", true, "\t\t\tSet the port for the server to listen to."),

	ROOT_DIR("root.dir", ".", "--root-dir", true, "\t\tSets the root directory to be listed."),
	
	GENERATE_PROPERTIES("generate.properties", "false", "--generate-properties", false, "\tGenerates an example properties file."),
	
	CONTEXT_ROOT("context.root", "/", "--context-root", true, "\t\tSets the web context root to listen on."),
	
	MIME_CACHE_MAX_SIZE("mime.cache.type.max.size", "1000", "--mime-cache-max-size", true, "\tSets the maximal amount of entries in the mime cache."),

	SCGI_ENABLED("scgi.enabled", "false", "--scgi-enabled", false, "\t\tEnables SCGI client for forwarding requests to the SCGI server. No additional attribute necessary."),
	SCGI_PATH("scgi.path", "/scgi", "--scgi-path", true, "\t\tPath to be forwarded to the SCGI server."),
	SCGI_SERVER("scgi.server", "localhost", "--scgi-server", true, "\t\tHost of the SCGI server."),
	SCGI_PORT("scgi.port", "3000", "--scgi-port", true, "\t\tPort of the SCGI server."),
	SCGI_TYPE("scgi.type", "STANDARD", "--scgi-type", true, "\t\tSCGI type as known in JSCGI. Possible values are STANDARD or SCGI_MESSAGE_BASED."),
	;
	
	private static final Properties PROPS = new Properties();
	public static final File DEFAULT_CONFIG_FILE = new File("./.gews.properties");
	
	private final String KEY;
	private final String DEFAULT_VALUE;
	private final String SWITCH;
	private final Boolean NEEDS_ARGUMENT;
	private final String DOCUMENTATION;
	
	private Config(String key, String defaultValue, String cliSwitch, boolean needsArgument, String documentation) {
		this.KEY = key;
		this.DEFAULT_VALUE = defaultValue;
		this.SWITCH = cliSwitch;
		this.NEEDS_ARGUMENT = needsArgument;
		this.DOCUMENTATION = documentation;
	}
		
	public String getValue() {
		return PROPS.getProperty(KEY, DEFAULT_VALUE);
	}
	
	public boolean getBooleanValue() {
		return Boolean.parseBoolean(getValue());
	}
	
	public int getIntValue() {
		return Integer.parseInt(getValue());
	}
	
	public File getFileValue() {
		return new File(getValue());
	}
	
	public static void clearProperties() {
		PROPS.clear();
	}
	
	public static void loadProperties(File file) {
			try (InputStream in = new FileInputStream(file)) {
			PROPS.load(in);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	public static void parseProperties(String[] args) {
		if (args == null) {
			return;
		}
		
		for (int i = 0; i < args.length; i++) {
			for (Config it : values()) {
				if (it.SWITCH.equals(args[i])) {
					if (it.NEEDS_ARGUMENT && args.length > i + 1) {
						PROPS.put(it.KEY, args[++i]);
					} else {
						PROPS.put(it.KEY, "true");
					}
				}
			}
		}
	}
	
	public static void generatePropertiesFile(File targetFile) {
		StringBuilder sb = new StringBuilder();
		for (Config it : values()) {
			sb.append(it.KEY).append(" = ").append(it.getValue()).append("\n");
		}
		try (FileOutputStream out = new FileOutputStream(targetFile)) {
			out.write(sb.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String generateHelpFile() {
		StringBuilder sb = new StringBuilder();
		sb.append("Gews (Good Enough Web Server) is a simple webserver for directory listings and SCGI forwardings. It\n"
				+ "offers only a few configuration attributes and aims to be a simple server to be used.\n\n");
		for (Config it : values()) {
			sb.append("\t");
			sb.append(it.SWITCH);
			sb.append(it.DOCUMENTATION);
			sb.append("\n");
		}
		return sb.toString();
	}

}
