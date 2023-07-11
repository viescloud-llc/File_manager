package com.vincent.inc.File_manager.model;

import com.vincent.inc.File_manager.service.FileBrowserService;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileBrowserEndpoint {
    private String downloadFile = FileBrowserService.DOWNLOAD_PATH;
    private String fetchFile = FileBrowserService.DOWNLOAD_PATH;
    private String uploadFile = FileBrowserService.RESOURCES_PATH;
}
