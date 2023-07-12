package com.vincent.inc.File_manager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vincent.inc.File_manager.model.FileBrowserEndpoint;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class FileManagerApplication {

	@Value("${spring.profiles.active}")
	private String env = "?";

	public static void main(String[] args) {
		SpringApplication.run(FileManagerApplication.class, args);
	}

	// @GetMapping("/")
	// public ModelAndView redirectSwagger(ModelMap model) {
	// 	return new ModelAndView("redirect:/swagger-ui/index.html", model);
	// }

	@GetMapping("/file_browser")
	public FileBrowserEndpoint endPoint() {
		return new FileBrowserEndpoint();
	}

	@GetMapping("/_status/healthz")
	public String healthCheck() {
		return String.format("File Manager %s is up and running", env);
	}
}
