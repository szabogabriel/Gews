package gews.http.scgi;

import java.io.IOException;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import jscgi.SCGIMessage;

public class ScgiHandlerMessageBased extends ScgiHandler {
	
	@Override
	public void handle(SCGIMessage message, HttpExchange exchange) throws IOException {
		SCGIMessage response = CLIENT.sendAndReceiveAsScgiMessage(message);
		exchange.sendResponseHeaders(200, response.getBodySize());
		fillResponseHeaders(exchange.getResponseHeaders(), response.getHeaders());
		exchange.getResponseBody().write(response.getBody());		
	}
	
	private void fillResponseHeaders(Headers headers, Map<String, String> data) {
		for (String it : data.keySet()) {
			headers.add(it, data.get(it));
		}
	}

}
