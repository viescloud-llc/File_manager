package com.vincent.inc.File_manager.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileBrowserToken implements Serializable {

    private String Cookie;
    private String XAuth;
}
