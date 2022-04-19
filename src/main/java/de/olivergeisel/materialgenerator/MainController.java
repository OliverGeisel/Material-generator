package de.olivergeisel.materialgenerator;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {


	@GetMapping("")
	String landing(){
		return "index";
	}

}
