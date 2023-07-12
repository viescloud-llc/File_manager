package com.vincent.inc.File_manager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vincent.inc.File_manager.model.FileBrowserItem;
import com.vincent.inc.File_manager.service.FileBrowserService;
import com.vincent.inc.File_manager.util.Http.HttpResponseThrowers;

import jakarta.ws.rs.QueryParam;

@RestController
@RequestMapping("items")
public class FileManagerController {
    public static final String LOGIN_PATH = "/api/login";
    public static final String RESOURCES_PATH = "/api/resources";
    public static final String DOWNLOAD_PATH = "/api/raw";

    @Autowired
    private FileBrowserService fileBrowserService;

    /**
     * this function should only be use by admin only
     * 
     * @param userId
     * @return
     */
    @GetMapping("/all/{id}")
    public List<FileBrowserItem> getAllFile(@PathVariable("id") int userId) {
        return this.fileBrowserService.getAllItem(userId);
    }

    @GetMapping("/all")
    public List<FileBrowserItem> getAllFileByUser(@RequestHeader("user_id") int userId) {
        return this.fileBrowserService.getAllItem(userId);
    }

    @GetMapping("/info/{id}")
    public FileBrowserItem getItemInfoById(@RequestHeader("user_id") int userId, @PathVariable("id") int itemId) {
        var item = this.fileBrowserService.getFileBrowserItemService().getById(itemId);
        validateOwner(item, userId);
        return item;
    }

    @GetMapping("/info")
    public FileBrowserItem getItemInfoByPath(@RequestHeader("user_id") int userId, @QueryParam("path") String path) {
        var item = this.fileBrowserService.getItem(path);
        validateOwner(item, userId);
        return item;
    }

    @PutMapping("/public/{itemId}")
    public FileBrowserItem togglePublic(@PathVariable("itemId") int itemId, @RequestHeader("user_id") int userId) {
        var item = this.fileBrowserService.getFileBrowserItemService().getById(itemId);
        validateOwner(item, userId);
        item.setPublic(true);
        return this.fileBrowserService.getFileBrowserItemService().patchFileBrowserItem(itemId, item);
    }

    @PutMapping("/private/{itemId}")
    public FileBrowserItem togglePrivate(@PathVariable("itemId") int itemId, @RequestHeader("user_id") int userId) {
        var item = this.fileBrowserService.getFileBrowserItemService().getById(itemId);
        validateOwner(item, userId);
        item.setPublic(false);
        return this.fileBrowserService.getFileBrowserItemService().patchFileBrowserItem(itemId, item);
    }

    public void validateOwner(FileBrowserItem item, int userId) {
        if (ObjectUtils.isEmpty(item))
            HttpResponseThrowers.throwBadRequest("Item does not exist");

        if (!item.getSharedUsers().contains(userId))
            HttpResponseThrowers.throwForbidden("User not allow to access this item");
    }
}
