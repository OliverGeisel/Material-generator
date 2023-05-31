package de.olivergeisel.materialgenerator.generation.generator;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DowloadManager {
	private final TemplateEngine templateEngine;
	private final ServletContext servletContext;

	public DowloadManager(TemplateEngine templateEngine, ServletContext servletContext) {
		this.templateEngine = templateEngine;
		this.servletContext = servletContext;
	}

	private void cleanupTemporaryFiles(File tempDir, File zipFile) throws IOException {
		if (zipFile != null && zipFile.exists()) {
			Files.delete(zipFile.toPath());
		}
		deleteDirectory(tempDir);
	}

	public void createSingle(String name, HttpServletRequest request, HttpServletResponse response) {

		WebContext context = new WebContext(request, response, servletContext);
		context.setVariable("wert", "My Value");
		String processedHtml = templateEngine.process("myTemp", context);

		response.setContentType("text/html");
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setHeader("Content-Disposition", "attachment; filename=myTemplate.html");
		try {
			response.getWriter().write(processedHtml);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File createTempDirectory() throws IOException {
		return File.createTempFile("temp", Long.toString(System.nanoTime()));
	}

	public void createZip(String name, HttpServletRequest request, HttpServletResponse response) {
		File tempDir = null;
		File zipFile = null;
		try {
			tempDir = createTempDirectory();
			generateTemplates(name, tempDir, request, response);
			zipFile = createZipArchive(tempDir);
			writeZipFileToResponse(zipFile, response);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				cleanupTemporaryFiles(tempDir, zipFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private File createZipArchive(File directory) throws IOException {
		File zipFile = File.createTempFile("templates", ".zip");
		try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
			zipDirectory(directory, "", zipOut);
		}
		return zipFile;
	}

	private void deleteDirectory(File directory) throws IOException {
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					Files.delete(file.toPath());
				}
			}
		}
		Files.delete(directory.toPath());
	}

	private void generateTemplates(String name, File outputDir, HttpServletRequest request,
								   HttpServletResponse response) {
		// Hier kannst du deine Template-Generierung logik einfügen und die Dateien im outputDir speichern
		// Du kannst die TemplateEngine und den WebContext verwenden, um die Dateien zu generieren
		// Zum Beispiel:
		FileTemplateResolver templateResolver = new FileTemplateResolver();
		templateResolver.setPrefix("/WEB-INF/templates/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode("HTML5");
		templateEngine.setTemplateResolver(templateResolver);
		WebContext context = new WebContext(request, response, servletContext);
		context.setVariable("name", name);

		String template1 = templateEngine.process("template1", context);
		saveTemplateToFile(template1, outputDir, "template1.html");

		String template2 = templateEngine.process("template2", context);
		saveTemplateToFile(template2, outputDir, "template2.html");

	}

	private void saveTemplateToFile(String templateContent, File outputDir, String fileName) {
		try (Writer writer = new BufferedWriter(new FileWriter(new File(outputDir, fileName)))) {
			writer.write(templateContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeZipFileToResponse(File zipFile, HttpServletResponse response) throws IOException {
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", "attachment; filename=templates.zip");
		try (OutputStream out = response.getOutputStream();
			 FileInputStream fis = new FileInputStream(zipFile)) {
			byte[] buffer = new byte[1024];
			int length;
			while ((length = fis.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
		}
	}

	private void zipDirectory(File directory, String parentDir, ZipOutputStream zipOut) throws IOException {
		File[] files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					zipDirectory(file, parentDir + file.getName() + "/", zipOut);
				} else {
					try (FileInputStream stream = new FileInputStream(file)) {
						zipOut.putNextEntry(new ZipEntry(parentDir + file.getName()));
						int length;
						byte[] buffer = new byte[1024];
						while ((length = stream.read(buffer)) > 0) {
							zipOut.write(buffer, 0, length);
						}
					}
				}
			}
		}
	}
}
