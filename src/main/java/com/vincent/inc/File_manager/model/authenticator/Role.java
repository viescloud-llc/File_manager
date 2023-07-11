package com.vincent.inc.File_manager.model.authenticator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {

    private int id;

    private String name;

    private int level;
}
