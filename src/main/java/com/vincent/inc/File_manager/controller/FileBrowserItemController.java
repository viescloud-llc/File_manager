package com.vincent.inc.File_manager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.ws.rs.QueryParam;

import com.vincent.inc.File_manager.model.FileBrowserItem;
import com.vincent.inc.File_manager.service.FileBrowserItemService;

/**
 * This controller should only be used by admin only
 */
@RestController
@RequestMapping("/admin/items")
class FileBrowserItemController {
    @Autowired
    FileBrowserItemService fileBrowserItemService;

    @GetMapping
    public ResponseEntity<List<FileBrowserItem>> getAll() {
        List<FileBrowserItem> fileBrowserItems = fileBrowserItemService.getAll();

        if (fileBrowserItems.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(fileBrowserItems, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<FileBrowserItem> getById(@PathVariable("id") int id) {
        FileBrowserItem fileBrowserItem = fileBrowserItemService.getById(id);

        return new ResponseEntity<>(fileBrowserItem, HttpStatus.OK);
    }

    @GetMapping("match_all")
    public ResponseEntity<List<FileBrowserItem>> matchAll(
            @QueryParam("fileBrowserItem") FileBrowserItem fileBrowserItem) {
        List<FileBrowserItem> fileBrowserItems = this.fileBrowserItemService.getAllByMatchAll(fileBrowserItem);

        if (fileBrowserItems.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(fileBrowserItems, HttpStatus.OK);
    }

    @GetMapping("match_any")
    public ResponseEntity<List<FileBrowserItem>> matchAny(
            @QueryParam("fileBrowserItem") FileBrowserItem fileBrowserItem) {
        List<FileBrowserItem> fileBrowserItems = this.fileBrowserItemService.getAllByMatchAny(fileBrowserItem);

        if (fileBrowserItems.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(fileBrowserItems, HttpStatus.OK);
    }

    @GetMapping("match_all/{matchCase}")
    public ResponseEntity<List<FileBrowserItem>> matchAll(
            @QueryParam("fileBrowserItem") FileBrowserItem fileBrowserItem,
            @PathVariable("matchCase") String matchCase) {
        List<FileBrowserItem> fileBrowserItems = this.fileBrowserItemService.getAllByMatchAll(fileBrowserItem,
                matchCase);

        if (fileBrowserItems.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(fileBrowserItems, HttpStatus.OK);
    }

    @GetMapping("match_any/{matchCase}")
    public ResponseEntity<List<FileBrowserItem>> matchAny(
            @QueryParam("fileBrowserItem") FileBrowserItem fileBrowserItem,
            @PathVariable("matchCase") String matchCase) {
        List<FileBrowserItem> fileBrowserItems = this.fileBrowserItemService.getAllByMatchAny(fileBrowserItem,
                matchCase);

        if (fileBrowserItems.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        return new ResponseEntity<>(fileBrowserItems, HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<FileBrowserItem> create(@RequestBody FileBrowserItem fileBrowserItem) {
        FileBrowserItem savedFileBrowserItem = fileBrowserItemService.createFileBrowserItem(fileBrowserItem);
        return new ResponseEntity<>(savedFileBrowserItem, HttpStatus.CREATED);

    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FileBrowserItem> update(@PathVariable("id") int id,
            @RequestBody FileBrowserItem fileBrowserItem) {
        fileBrowserItem = this.fileBrowserItemService.modifyFileBrowserItem(id, fileBrowserItem);
        return new ResponseEntity<>(fileBrowserItem, HttpStatus.OK);
    }

    @PatchMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<FileBrowserItem> patch(@PathVariable("id") int id,
            @RequestBody FileBrowserItem fileBrowserItem) {
        fileBrowserItem = this.fileBrowserItemService.patchFileBrowserItem(id, fileBrowserItem);
        return new ResponseEntity<>(fileBrowserItem, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {
        fileBrowserItemService.deleteFileBrowserItem(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}