package de.olivergeisel.materialgenerator.finalization;

import de.olivergeisel.materialgenerator.core.courseplan.structure.CourseStructure;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureChapter;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureGroup;
import de.olivergeisel.materialgenerator.core.courseplan.structure.StructureTask;
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
public class DownloadManager {
	private final ServletContext servletContext;
	private TemplateEngine templateEngine;

	public DownloadManager(TemplateEngine templateEngine, ServletContext servletContext) {
		this.servletContext = servletContext;
		this.templateEngine = templateEngine;
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
		response.setHeader("Content-Disposition", String.format("attachment; filename=%s.html", name));
		try {
			response.getWriter().write(processedHtml);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File createTempDirectory() throws IOException {
		return Files.createTempDirectory("materialgenerator").toFile();
	}

	public void createZip(String name, String template, CourseStructure structure, HttpServletRequest request, HttpServletResponse response) {
		File tempDir = null;
		File zipFile = null;
		try {
			tempDir = createTempDirectory();
			generateTemplates(structure, template, tempDir, request, response);
			zipFile = createZipArchive(name, tempDir);
			writeZipFileToResponse(name, zipFile, response);
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

	private File createZipArchive(String zipName, File directory) throws IOException {
		File zipFile = File.createTempFile(zipName, ".zip");
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

	private void generateTemplates(CourseStructure structure, String templateSet, File outputDir, HttpServletRequest request,
								   HttpServletResponse response) {
		FileTemplateResolver templateResolver = new FileTemplateResolver();
		templateResolver.setPrefix("target/classes/templateSets/" + templateSet + "/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode("HTML");
		if (!templateEngine.isInitialized())
			templateEngine.setTemplateResolver(templateResolver);
		WebContext context = new WebContext(request, response, servletContext);
		//for( var chapter: structure.getOrder()){
		//	generateChapter(chapter, context, new File(outputDir, chapter.getName()));
		//}
		String template1 = templateEngine.process("DEFINITION", context);
		saveTemplateToFile(template1, outputDir, "DEFINITION.html");

	}

	private void generateChapter(StructureChapter chapter, WebContext context, File outputDir) {
		for (var subChapter : chapter.getParts()) {
			if (subChapter instanceof StructureGroup group) {
				var subDir = new File(outputDir, group.getName());
				generateGroup(group, context, subDir);
			} else if (subChapter instanceof StructureTask task) {
				var subDir = new File(outputDir, task.getName());
				generateTask(task, context, subDir);
			} else {
				throw new IllegalArgumentException("Unknown substructure component! Must be either group or task inside a chapter.");
			}
		}
	}

	private void generateTask(StructureTask task, WebContext context, File outputDir) {

	}

	private void generateGroup(StructureGroup group, WebContext context, File outputDir) {

	}

	private void saveTemplateToFile(String templateContent, File outputDir, String fileName) {
		try (Writer writer = new BufferedWriter(new FileWriter(new File(outputDir, fileName)))) {
			writer.write(templateContent);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeZipFileToResponse(String name, File zipFile, HttpServletResponse response) throws IOException {
		response.setContentType("application/zip");
		response.setHeader("Content-Disposition", String.format("attachment; filename=%s.zip", name));
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
		if (files == null) {
			return;
		}
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
