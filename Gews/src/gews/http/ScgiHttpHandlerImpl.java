package gews.http;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import gews.config.Config;
import jscgi.Mode;
import jscgi.SCGIMessage;
import jscgi.client.SCGIClient;

public class ScgiHttpHandlerImpl implements HttpHandler {
	
	private final SCGIClient CLIENT;
	
	public ScgiHttpHandlerImpl() {
		CLIENT = new SCGIClient(getScgiServer(), getScgiPort(), getScgiMode());
	}
	
	private String getScgiServer() {
		return Config.SCGI_SERVER.getValue();
	}
	
	private int getScgiPort() {
		return Config.SCGI_PORT.getIntValue();
	}
	
	private Mode getScgiMode() {
		Mode mode = null;
		if (Config.SCGI_TYPE.getValue().equals("STANDARD")) {
			mode = Mode.STANDARD;
		} else
		if (Config.SCGI_TYPE.getValue().equals("SCGI_MESSAGE_BASED")) {
			mode = Mode.SCGI_MESSAGE_BASED;
		} else {
			throw new IllegalArgumentException("SCGI type is incorrect: " + Config.SCGI_TYPE.getValue());
		}
		return mode;
	}
	
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		Map<String, String> requestHeaders = mapHeaders(exchange.getRequestHeaders());
		String requestBody = readInputStream(exchange.getRequestBody());
		SCGIMessage request = new SCGIMessage(requestHeaders, requestBody.getBytes());
		SCGIMessage response = CLIENT.sendAndReceiveAsScgiMessage(request);
		exchange.sendResponseHeaders(200, response.getBodySize());
		fillResponseHeaders(exchange.getResponseHeaders(), response.getHeaders());
		exchange.getResponseBody().write(response.getBody());
	}
	
	private void fillResponseHeaders(Headers headers, Map<String, String> data) {
		for (String it : data.keySet()) {
			headers.add(it, data.get(it));
		}
	}
	
	private Map<String, String> mapHeaders(Headers headers) {
		Map<String, String> ret = new HashMap<>();
		
		headers.keySet().forEach(k -> {
			List<String> values = headers.get(k);
			String value = "";
			for (String it : values) {
				if (value.length() > 0) {
					value += ";";
				}
				value += it;
			}
		});
		
		return ret;
	}
	
	private String readInputStream(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		byte[] buffer = new byte[4096];
		int read = 0;
		while ((read = in.read(buffer)) > 0) {
			sb.append(new String(buffer, 0, read));
		}
		
		return sb.toString();
	}

}
