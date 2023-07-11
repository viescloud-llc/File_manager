package com.vincent.inc.File_manager.model;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileBrowserFetchResponse implements Serializable {
    private List<FileBrowserItem> items;
    private long numDirs;
    private long numFiles;
    private FileBrowserSorting sorting;
    private String path;
    private String name;
    private long size;
    private String extension;
    private String modified;
    private long mode;
    private boolean isDir;
    private boolean isSymlink;
    private String type;
}
