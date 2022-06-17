package gews.http;

import java.io.File;
import java.io.IOException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import gews.config.Config;
import gews.service.FileService;

public class HttpHandlerImpl implements HttpHandler {
	
	private final FileService FILE_UTILS = new FileService();
	
	private final File ROOT;
	
	public HttpHandlerImpl(File root) {
		this.ROOT = root;
	}

	public void handle(HttpExchange exchange) throws IOException {
		String requestPath = getRequestPath(exchange);
		
		if (!isValidRequestPath(requestPath)) {
			sendBadRequestErrorMessage(exchange);
		}
	
		File tmp = FILE_UTILS.combine(ROOT, requestPath);
		if (Config.DEFAULT_CONFIG_FILE.equals(tmp)) {
			sendBadRequestErrorMessage(exchange);
		}
		
		if (tmp.isFile()) {
			sendFile(exchange, tmp);
		} else {
			listDirectory(exchange, tmp, requestPath);
		}
	}
	
	private void listDirectory(HttpExchange exchange, File dir, String path) throws IOException {
		String template = 
				"<html><head><title>File listing</title></head><body>"
				+ "<h1>File listing: " + path + "</h1>"
				+ FILE_UTILS.listAndFormatFilesAndDirs(ROOT, dir) 
				+ "</body></html>";
		exchange.sendResponseHeaders(200, template.length());
		exchange.getResponseHeaders().set("Content-type", "text/html");
		exchange.getResponseBody().write(template.getBytes());
		exchange.close();
	}
	
	private void sendFile(HttpExchange exchange, File file) throws IOException {
		exchange.sendResponseHeaders(200, FILE_UTILS.getFileSize(file));
		exchange.getResponseHeaders().set("Content-type", FILE_UTILS.getMimeType(file));
		FILE_UTILS.sendFileToStream(file, exchange.getResponseBody());
		exchange.close();
	}
	
	private void sendBadRequestErrorMessage(HttpExchange exchange) throws IOException {
		String message = "Sorry, bad request.";
		exchange.sendResponseHeaders(400, message.length());
		exchange.getResponseBody().write(message.getBytes());
	}
	
	private String getRequestPath(HttpExchange exchange) {
		String path = exchange.getRequestURI().toString();
		return path.substring(Config.CONTEXT_ROOT.getValue().length());
	}
	
	private boolean isValidRequestPath(String path) {
		return !path.contains("..");
	}
	
}
