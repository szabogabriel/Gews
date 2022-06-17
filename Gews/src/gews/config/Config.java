package gews.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public enum Config {
	
	PORT("port", "65000", "--port"),

	ROOT_DIR("root.dir", ".", "--root-dir"),
	
	GENERATE_PROPERTIES("generate.properties", "false", "--generate-properties"),
	
	CONTEXT_ROOT("context.root", "/", "--context-root"),
	
	CACHE_MIME_TYPE_MAX_SIZE("cache.mime-type.max.size", "1000", "--cache-mime-type-max-size"),
	;
	
	private static final Properties PROPS = new Properties();
	public static final File DEFAULT_CONFIG_FILE = new File("./.gews.properties");
	
	private final String KEY;
	private final String DEFAULT_VALUE;
	private final String SWITCH;
	
	private Config(String key, String defaultValue, String cliSwitch) {
		this.KEY = key;
		this.DEFAULT_VALUE = defaultValue;
		this.SWITCH = cliSwitch;
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
	
	public static void loadProperties(File file) {
		PROPS.clear();
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
		
		PROPS.clear();
		
		for (int i = 0; i < args.length; i++) {
			for (Config it : values()) {
				if (it.SWITCH.equals(args[i]) && args.length > i + 1) {
					PROPS.put(it.KEY, args[++i]);
				}
			}
		}
	}
	
	public static void generatePropertiesFile(File targetFile) {
		StringBuilder sb = new StringBuilder();
		for (Config it : values()) {
			sb.append(it.KEY).append(" = ").append(it.getValue());
		}
		try (FileOutputStream out = new FileOutputStream(targetFile)) {
			out.write(sb.toString().getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
