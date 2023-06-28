package de.olivergeisel.materialgenerator.finalization;

import de.olivergeisel.materialgenerator.finalization.parts.*;
import de.olivergeisel.materialgenerator.generation.generator.Material;
import org.slf4j.Logger;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class DownloadManager {

	private final Logger logger = org.slf4j.LoggerFactory.getLogger(DownloadManager.class);
	private final ServletContext servletContext;

	public DownloadManager(ServletContext servletContext) {
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
		var templateEngine = new TemplateEngine();
		String processedHtml = templateEngine.process("myTemp", context);
		response.setContentType("text/html");
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		response.setHeader("Content-Disposition", String.format("attachment; filename=%s.html", name));
		try {
			response.getWriter().write(processedHtml);
		} catch (IOException e) {
			logger.warn(e.toString());
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

	private void saveIncludes(File outputDir, String templateSet) {
		var classloader = this.getClass().getClassLoader();
		URL folder = classloader.getResource("");
		var outPath = outputDir.toPath();
		File sourceCopy = new File(folder.getFile(), "templateSets/" + templateSet + "/include");
		if (!sourceCopy.exists()) {
			return;
		}
		var copyPath = sourceCopy.toPath();
		var out = outPath.resolve(copyPath.relativize(copyPath));
		try (var files = Files.walk(copyPath)) {
			files.forEach(source -> {
				try {
					Path destination = out.resolve(copyPath.relativize(source));
					if (Files.isDirectory(source)) {
						Files.createDirectories(destination);
					} else {
						Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
					}
				} catch (IOException e) {
					logger.warn(e.toString());
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void exportCourse(RawCourse plan, String templateSet, File outputDir, HttpServletRequest request, HttpServletResponse response) throws IOException {
		var templateEngine = createTemplateEngine(templateSet);
		WebContext context = new WebContext(request, response, servletContext);
		CourseNavigation.MaterialLevel level = new CourseNavigation.MaterialLevel();
		var navigation = new CourseNavigation(level);
		var chapters = plan.getMaterialOrder().getChapterOrder();
		for (int i = 0; i < chapters.size(); i++) {
			var chapter = plan.getMaterialOrder().getChapterOrder().get(i);
			String chapterName = chapter.getName() == null || chapter.getName().isBlank() ? "Kapitel " + (chapters.indexOf(chapter) + 1) : chapter.getName();
			var nextChapter = i + 1 < chapters.size() ? chapters.get(i + 1) : null;
			var chapterNavigation = navigation.nextChapter(nextChapter);
			var subDir = Files.createDirectory(new File(outputDir, chapterName).toPath());
			var newLevel = new CourseNavigation.MaterialLevel(chapter.getName(), "", "", "");
			exportChapter(chapter, newLevel, chapterNavigation, context, subDir.toFile(), templateEngine);
		}
		context.setVariable("course", plan);
		var course = templateEngine.process("COURSE", context);
		saveTemplateToFile(course, outputDir, "Course.html");
		saveIncludes(outputDir, templateSet);
	}

	private void exportChapter(ChapterOrder chapter, CourseNavigation.MaterialLevel level, CourseNavigation navigation, WebContext context, File outputDir, TemplateEngine templateEngine) throws IOException {
		context.setVariable("chapter", chapter);
		var chapterOverview = templateEngine.process("CHAPTER", context);
		saveTemplateToFile(chapterOverview, outputDir, "overview.html");
		var groups = chapter.getGroupOrder();
		GroupOrder previousGroup = null;
		GroupOrder nextGroup = null;
		CourseNavigation previousNavigation = navigation;
		for (int i = 0; i < groups.size(); i++) {
			var group = groups.get(i);
			var newLevel = new CourseNavigation.MaterialLevel(level.getChapter(), group.getName());
			nextGroup = (i == groups.size() - 2) ? groups.get(i + 1) : null;
			MaterialHierarchy next;
			if (nextGroup == null) {
				next = new MaterialHierarchy(navigation.getNextChapter(), null, null, null, navigation.getCount() + 1, navigation.getSize());
			} else {
				next = new MaterialHierarchy(level.getChapter(), level.getGroup(), null, null, i + 1, groups.size());
			}
			CourseNavigation newCourseNavigation = new CourseNavigation(newLevel, navigation.getCurrentMaterialHierarchy(), next, i, groups.size());

			if (group instanceof GroupOrder group1) {
				String groupName = group1.getName() == null || group.getName().isBlank() ? "Gruppe " + (chapter.getGroupOrder().indexOf(group) + 1) : group.getName();
				var subDir = new File(outputDir, groupName);
				Files.createDirectory(subDir.toPath());
				exportGroup(group1, newLevel, navigation, context, subDir, templateEngine);
			}/* else if (subChapter instanceof TaskOrder task) {
				var subDir = new File(outputDir, task.getName());
				exportTask(task, context, subDir);
			} */ else {
				throw new IllegalArgumentException("Unknown substructure component! Must be either group or task inside a chapter.");
			}
			previousNavigation = newCourseNavigation;
		}
	}

	private void exportGroup(GroupOrder group, CourseNavigation.MaterialLevel level, CourseNavigation navigation, WebContext context, File outputDir, TemplateEngine templateEngine) throws IOException {
		context.setVariable("group", group);
		var chapterOverview = templateEngine.process("GROUP", context);
		saveTemplateToFile(chapterOverview, outputDir, "overview.html");
		var tasks = group.getTaskOrder();
		TaskOrder previousTask = null;
		TaskOrder nextTask = null;
		CourseNavigation previousNavigation = navigation;
		for (int i = 0; i < tasks.size(); i++) {
			var task = tasks.get(i);
			var newTaskLevel = new CourseNavigation.MaterialLevel(level.getChapter(), level.getGroup(), task.getName());
			nextTask = (i == tasks.size() - 2) ? tasks.get(i + 1) : null;
			MaterialHierarchy next;
			if (nextTask == null) {
				next = new MaterialHierarchy(level.getChapter(), navigation.getNextGroup(), null, null, navigation.getCount() + 1, navigation.getSize());
			} else {
				next = new MaterialHierarchy(level.getChapter(), level.getGroup(), nextTask.getName(), null, i + 1, tasks.size());
			}
			CourseNavigation newCourseNavigation = new CourseNavigation(newTaskLevel, navigation.getCurrentMaterialHierarchy(), next, i, tasks.size());
			/*if (subChapter instanceof GroupOrder subGroup) {
				var subDir = new File(outputDir, subGroup.getName());
				Files.createDirectory(subDir.toPath());
				exportGroup(subGroup, context, subDir);
			} else */
			if (task instanceof TaskOrder task1) {
				String taskName = task1.getName() == null || task1.getName().isBlank() ? "Task " + (group.getTaskOrder().indexOf(task1) + 1) : task1.getName();
				var subDir = new File(outputDir, taskName);
				Files.createDirectory(subDir.toPath());
				exportTask(task1, newTaskLevel, newCourseNavigation, context, subDir, templateEngine);
			} else {
				throw new IllegalArgumentException("Unknown substructure component! Must be either group or task inside a group.");
			}
			previousTask = task;
			previousNavigation = newCourseNavigation;
		}
	}

	private void exportTask(TaskOrder task, CourseNavigation.MaterialLevel level, CourseNavigation navigation, WebContext context, File outputDir, TemplateEngine templateEngine) {
		final int taskSize = task.getMaterialOrder().size();
		var materials = task.getMaterialOrder();
		Material previousMaterial = null;
		Material nextMaterial;
		CourseNavigation previousNavigation = navigation;
		for (int i = 0; i < materials.size(); i++) {
			context.clearVariables();
			var material = materials.get(i);
			nextMaterial = (i == materials.size() - 2) ? materials.get(i + 1) : null;
			MaterialHierarchy next;
			if (nextMaterial == null) {
				next = new MaterialHierarchy(level.getChapter(), level.getGroup(), navigation.getNextTask(), null, navigation.getCount() + 1, navigation.getSize());
			} else {
				next = new MaterialHierarchy(level.getChapter(), level.getGroup(), level.getTask(), "MATERIAL_" + (i + 1), i + 1, materials.size());
			}
			CourseNavigation.MaterialLevel materialLevel = new CourseNavigation.MaterialLevel(level.getChapter(), level.getGroup(), task.getName(), "MATERIAL_" + i);
			CourseNavigation newNavigation = new CourseNavigation(materialLevel, previousNavigation.getCurrentMaterialHierarchy(), next, i, taskSize);
			exportMaterial(context, outputDir, templateEngine, i, material, materialLevel, newNavigation);
			previousMaterial = material;
			previousNavigation = newNavigation;
		}
	}

	private void exportMaterial(WebContext context, File outputDir, TemplateEngine templateEngine, int materialNumber, Material material, CourseNavigation.MaterialLevel materialLevel, CourseNavigation newNavigation) {
		context.setVariable("material", material);
		context.setVariable("navigation", newNavigation);
		context.setVariable("rootPath", materialLevel.getPathToRoot());
		context.setVariable("title", material.getName());
		String processedHtml = templateEngine.process("MATERIAL", context);
		saveTemplateToFile(processedHtml, outputDir, String.format("Material_%s.html", materialNumber));
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