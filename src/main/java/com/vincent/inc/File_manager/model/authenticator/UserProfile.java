package com.vincent.inc.File_manager.model.authenticator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {

    private int id;

    private String firstName;

    private String lastName;

    private String alias;

    private String email;
}
