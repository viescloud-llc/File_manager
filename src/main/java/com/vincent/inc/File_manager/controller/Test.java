package com.vincent.inc.File_manager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vincent.inc.File_manager.model.FileBrowserToken;
import com.vincent.inc.File_manager.service.FileBrowserService;

@RestController
@RequestMapping("/test")
public class Test {
    @Autowired
    private FileBrowserService fileBrowserService;

    @GetMapping("1")
    public FileBrowserToken login() {
        return fileBrowserService.getToken();
    }

}
