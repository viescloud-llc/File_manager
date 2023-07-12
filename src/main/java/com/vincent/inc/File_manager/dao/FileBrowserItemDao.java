package com.vincent.inc.File_manager.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vincent.inc.File_manager.model.FileBrowserItem;

public interface FileBrowserItemDao extends JpaRepository<FileBrowserItem, Integer> {
public FileBrowserItem findByPath(String path);
	public List<FileBrowserItem> findAllByPath(String path);

	public FileBrowserItem findByName(String name);
	public List<FileBrowserItem> findAllByName(String name);

	public FileBrowserItem findByExtension(String extension);
	public List<FileBrowserItem> findAllByExtension(String extension);

	public FileBrowserItem findByModified(String modified);
	public List<FileBrowserItem> findAllByModified(String modified);

	public FileBrowserItem findByIsDir(boolean isDir);
	public List<FileBrowserItem> findAllByIsDir(boolean isDir);

	public FileBrowserItem findByIsSymlink(boolean isSymlink);
	public List<FileBrowserItem> findAllByIsSymlink(boolean isSymlink);

	public FileBrowserItem findByType(String type);
	public List<FileBrowserItem> findAllByType(String type);

	public FileBrowserItem findByIsPublic(boolean isPublic);
	public List<FileBrowserItem> findAllByIsPublic(boolean isPublic);

	@Query(value = "select * from FileBrowserItem as fileBrowserItem where fileBrowserItem.path = :path and fileBrowserItem.name = :name and fileBrowserItem.extension = :extension and fileBrowserItem.modified = :modified and fileBrowserItem.isDir = :isDir and fileBrowserItem.isSymlink = :isSymlink and fileBrowserItem.type = :type and fileBrowserItem.isPublic = :isPublic", nativeQuery = true)
	public List<FileBrowserItem> getAllByMatchAll(@Param("path") String path, @Param("name") String name, @Param("extension") String extension, @Param("modified") String modified, @Param("isDir") boolean isDir, @Param("isSymlink") boolean isSymlink, @Param("type") String type, @Param("isPublic") boolean isPublic);

	@Query(value = "select * from FileBrowserItem as fileBrowserItem where fileBrowserItem.path = :path or fileBrowserItem.name = :name or fileBrowserItem.extension = :extension or fileBrowserItem.modified = :modified or fileBrowserItem.isDir = :isDir or fileBrowserItem.isSymlink = :isSymlink or fileBrowserItem.type = :type or fileBrowserItem.isPublic = :isPublic", nativeQuery = true)
	public List<FileBrowserItem> getAllByMatchAny(@Param("path") String path, @Param("name") String name, @Param("extension") String extension, @Param("modified") String modified, @Param("isDir") boolean isDir, @Param("isSymlink") boolean isSymlink, @Param("type") String type, @Param("isPublic") boolean isPublic);
}