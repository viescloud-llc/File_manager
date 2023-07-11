package com.vincent.inc.File_manager.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.vincent.inc.File_manager.service.FileBrowserService;

@RestController
@RequestMapping("file_manager")
public class FileManagerController {
    public static final String LOGIN_PATH = "/api/login";
    public static final String RESOURCES_PATH = "/api/resources";
    public static final String DOWNLOAD_PATH = "/api/raw";

    @Autowired
    private FileBrowserService fileBrowserService;

    @GetMapping("/all")
    public List<String> getAllFile() {
        return new ArrayList<String>();
    }
}
