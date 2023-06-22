package de.olivergeisel.materialgenerator.finalization;


import de.olivergeisel.materialgenerator.core.courseplan.CoursePlan;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/finalization")
public class FinalizationController {


	private final DownloadManager downloadManager;

	public FinalizationController(DownloadManager downloadManager) {
		this.downloadManager = downloadManager;
	}

	@GetMapping("download")
	public void generateAndDownloadTemplate(HttpServletRequest request, HttpServletResponse response) {
		downloadManager.createSingle("test", request, response);
	}

	@GetMapping("/download-all/{courseId}")
	public void generateAndDownloadTemplates(@PathVariable("courseId") CoursePlan plan, HttpServletRequest request, HttpServletResponse response) {
		var zipName = plan.getMetadata().getName().orElse("course");
		var structure = plan.getStructure();

		downloadManager.createZip(zipName, "blank", structure, request, response);
	}
}
