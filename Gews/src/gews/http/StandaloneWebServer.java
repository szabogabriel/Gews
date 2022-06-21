package gews.http;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import gews.config.Config;

public class StandaloneWebServer {
	
	private HttpServer server;
	
	public StandaloneWebServer() throws IOException {
		server = HttpServer.create(new InetSocketAddress(Config.PORT.getIntValue()), 0);
		
		if (Config.SCGI_ENABLED.getBooleanValue()) {
			server.createContext(Config.SCGI_PATH.getValue(), new ScgiHttpHandlerImpl());
		}
		server.createContext(Config.CONTEXT_ROOT.getValue(), new HttpHandlerImpl(Config.ROOT_DIR.getFileValue()));
		
		server.start();
	}
	
	
}
