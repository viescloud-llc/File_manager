package com.vincent.inc.File_manager.model;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileBrowserItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column
    private String path;
    
    @Column
    private String name;
    
    @Column
    private long size;
    
    @Column
    private String extension;
    
    @Column
    private String modified;
    
    @Column
    private long mode;
    
    @Column
    private boolean isDir;
    
    @Column
    private boolean isSymlink;
    
    @Column
    private String type;
    
    @Column
    private boolean isPublic;
    
    @Column
    private List<Integer> sharedUsers;
}
