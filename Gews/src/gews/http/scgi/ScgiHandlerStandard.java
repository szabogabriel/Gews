package gews.http.scgi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import jscgi.SCGIMessage;

public class ScgiHandlerStandard extends ScgiHandler {
	
	private static final String HTTP_LINE = "http_line";

	@Override
	public void handle(SCGIMessage message, HttpExchange exchange) throws IOException {
		byte[] data = CLIENT.sendAndReceiveAsByteArray(message);
		
		Map<String, String> headers = new HashMap<>();
		int position = parseHeaders(data, headers);
		byte[] body = new byte[data.length - position];
		System.arraycopy(data, position, body, 0, data.length - position);
		
		exchange.sendResponseHeaders(Integer.parseInt(headers.get(HTTP_LINE).split(" ")[1]), body.length);
		fillResponseHeaders(exchange.getResponseHeaders(), headers);
		exchange.getResponseBody().write(body);	
	}
	
	private int parseHeaders(byte[] data, Map<String, String> ret) {
		int tmp = 0;
		String line = null;
		while ((line = readLine(data, tmp)).length() > 0) {
			String [] keyvalue = line.split(":");
			if (keyvalue.length == 2) {
				ret.put(keyvalue[0].trim(), keyvalue[1].trim());
			} else {
				ret.put(HTTP_LINE, line.trim());
			}
			tmp += line.getBytes().length + 1;
		}
		return tmp;
	}
	
	private String readLine(byte[] data, int start) {
		int end = start;
		//13 == \r, 10 == \n
		while (data[end] != 10)
			end++;
		return new String(data, start, end - start);
	}
	
	private void fillResponseHeaders(Headers headers, Map<String, String> data) {
		for (String it : data.keySet()) {
			headers.add(it, data.get(it));
		}
	}
	
}
