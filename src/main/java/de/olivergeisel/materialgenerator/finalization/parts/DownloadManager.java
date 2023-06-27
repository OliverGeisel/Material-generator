package de.olivergeisel.materialgenerator.finalization.parts;

import de.olivergeisel.materialgenerator.finalization.GoalRepository;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DownloadManager {
	private final ServletContext servletContext;
	private final GoalRepository goalRepository;

	public DownloadManager(ServletContext servletContext, GoalRepository goalRepository) {
		this.servletContext = servletContext;
		this.goalRepository = goalRepository;
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
		var templateEngine = new TemplateEngine();
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

	public void createZip(String name, String template, RawCourse plan, HttpServletRequest request, HttpServletResponse response) {
		File tempDir = null;
		File zipFile = null;
		try {
			tempDir = createTempDirectory();
			exportCourse(plan, template, tempDir, request, response);
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

	private TemplateEngine createTemplateEngine(String templateSet) {
		var templateResolver = new ClassLoaderTemplateResolver();
		templateResolver.setPrefix("templateSets/" + templateSet + "/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode("HTML");
		templateResolver.setCharacterEncoding("UTF-8");
		templateResolver.setCacheable(false);
		templateResolver.setCheckExistence(true);

		var templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(templateResolver);
		return templateEngine;
	}

	private void exportCourse(RawCourse plan, String templateSet, File outputDir, HttpServletRequest request, HttpServletResponse response) throws IOException {
		var templateEngine = createTemplateEngine(templateSet);
		WebContext context = new WebContext(request, response, servletContext);
		CourseNavigation.MaterialLevel level = new CourseNavigation.MaterialLevel();
		var navigation = new CourseNavigation(level);
		var chapters = plan.getMaterialOrder().getChapterOrder();
		for (int i = 0; i < chapters.size(); i++) {
			var chapter = plan.getMaterialOrder().getChapterOrder().get(i);
			var nextChapter = i + 1 < chapters.size() ? chapters.get(i + 1) : null;
			var chapterNavigation = navigation.nextChapter(nextChapter);
			var subDir = Files.createDirectory(new File(outputDir, chapter.getName()).toPath());
			var newLevel = new CourseNavigation.MaterialLevel(chapter.getName(), "", "", "");
			exportChapter(chapter, newLevel, chapterNavigation, context, subDir.toFile(), templateEngine);
		}
		context.setVariable("course", plan);
		var course = templateEngine.process("COURSE", context);
		saveTemplateToFile(course, outputDir, "Course.html");
		saveIncludes(outputDir, templateSet);
	}

	private void saveIncludes(File outputDir, String templateSet) {
		URL folder = this.getClass().getClassLoader().getResource("templateSets/" + templateSet + "/include");
		var outPath = outputDir.toPath();
		File fileO = new File(folder.getFile());
		if (!fileO.exists()) {
			return;
		}
		var file = fileO.toPath();
		try {
			Files.walkFileTree(file, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					Path targetDir = outPath.resolve(file.relativize(dir));
					Files.createDirectories(targetDir);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.copy(file, outPath.resolve(file.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void exportChapter(ChapterOrder chapter, CourseNavigation.MaterialLevel level, CourseNavigation navigation, WebContext context, File outputDir, TemplateEngine templateEngine) throws IOException {
		context.setVariable("chapter", chapter);
		var chapterOverview = templateEngine.process("CHAPTER", context);
		saveTemplateToFile(chapterOverview, outputDir, "overview.html");
		for (var subChapter : chapter.getGroupOrder()) {
			var newLevel = new CourseNavigation.MaterialLevel(level.getChapter(), subChapter.getName());
			if (subChapter instanceof GroupOrder group) {
				var subDir = new File(outputDir, group.getName());
				Files.createDirectory(subDir.toPath());
				exportGroup(group, newLevel, navigation, context, subDir, templateEngine);
			}/* else if (subChapter instanceof TaskOrder task) {
				var subDir = new File(outputDir, task.getName());
				exportTask(task, context, subDir);
			} */ else {
				throw new IllegalArgumentException("Unknown substructure component! Must be either group or task inside a chapter.");
			}
		}
	}

	private void exportGroup(GroupOrder group, CourseNavigation.MaterialLevel level, CourseNavigation navigation, WebContext context, File outputDir, TemplateEngine templateEngine) throws IOException {
		context.setVariable("group", group);
		var chapterOverview = templateEngine.process("GROUP", context);
		saveTemplateToFile(chapterOverview, outputDir, "overview.html");
		for (var subChapter : group.getTaskOrder()) {
			var newlevel = new CourseNavigation.MaterialLevel(level.getChapter(), level.getGroup(), subChapter.getName());
			/*if (subChapter instanceof GroupOrder subGroup) {
				var subDir = new File(outputDir, subGroup.getName());
				Files.createDirectory(subDir.toPath());
				exportGroup(subGroup, context, subDir);
			} else */
			if (subChapter instanceof TaskOrder task) {
				var subDir = new File(outputDir, task.getName());
				exportTask(task, newlevel, navigation, context, subDir, templateEngine);
			} else {
				throw new IllegalArgumentException("Unknown substructure component! Must be either group or task inside a group.");
			}
		}
	}

	private void exportTask(TaskOrder task, CourseNavigation.MaterialLevel level, CourseNavigation navigation, WebContext context, File outputDir, TemplateEngine templateEngine) {
		int number = 0;
		final int taskSize = task.getMaterialOrder().size();
		for (var material : task.getMaterialOrder()) {
			CourseNavigation.MaterialLevel newLevel = new CourseNavigation.MaterialLevel(level.getChapter(), level.getGroup(), task.getName(), Integer.toString(number));
			context.setVariable("material", material);
			context.setVariable("navigation", navigation);
			var templateInfo = material.getTemplate();
			String processedHtml = templateEngine.process(templateInfo.getTemplateType().type(), context);
			saveTemplateToFile(processedHtml, outputDir, String.format("Material_%s.html", number++));
		}
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
		try (OutputStream out = response.getOutputStream(); FileInputStream fis = new FileInputStream(zipFile)) {
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
