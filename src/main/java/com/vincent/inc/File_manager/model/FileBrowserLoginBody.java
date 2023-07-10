package com.vincent.inc.File_manager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileBrowserLoginBody {
    private String username;
    private String password;
    private String recaptcha = "";
}
