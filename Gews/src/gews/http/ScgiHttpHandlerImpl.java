package gews.http;

import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import gews.http.scgi.ScgiHandler;

public class ScgiHttpHandlerImpl implements HttpHandler {
	
	private final ScgiHandler handler;
	
	public ScgiHttpHandlerImpl() {
		handler = ScgiHandler.getInstance();
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		handler.handle(exchange);
	}
	
}
