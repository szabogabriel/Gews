package gews.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class FileService {
	
	private MimeTypeCache mimeCache = new MimeTypeCache();

	public String getMimeType(File file) {
		if (file == null || !file.isFile()) {
			mimeCache.remove(file);
			return null;
		}
		
		String ret = mimeCache.get(file);
		
		if (ret == null) {
			try {
				ret = Files.probeContentType(file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
			mimeCache.store(file, ret);
		}
		
		return ret;
	}
	
	public File combine(File root, String path) {
		String absolutePath = root.getAbsolutePath() + ((path.startsWith("/") ? "" : "/")) + path;
		File tmp = new File(absolutePath);
		return tmp;
	}
	
	public long getFileSize(File file) {
		return file.length();
	}
	
	public void sendFileToStream(File file, OutputStream out) {
		if (file == null || !file.isFile() || out == null) {
			return;
		}
		
		byte[] buffer = new byte[4096];
		int len;
		try (FileInputStream in = new FileInputStream(file)) {
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String listAndFormatFilesAndDirs(File root, File dir) {
		StringBuilder ret = new StringBuilder();
		for (File it : dir.listFiles()) {
			ret.append(mapFileToHtml(root, it));
			ret.append("<br/>");
		}
		return ret.toString();
	}
	
	private String mapFileToHtml(File root, File tmp) {
		String filepath = tmp.getAbsolutePath().substring(root.getAbsolutePath().length());
		return "<a href='" + filepath + "'> " + (tmp.isFile() ? "[F] " : "[D] ") + tmp.getName() + "</a>";
	}

}
