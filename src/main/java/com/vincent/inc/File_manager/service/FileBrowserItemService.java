package com.vincent.inc.File_manager.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import org.springframework.data.domain.Example;
import com.vincent.inc.File_manager.dao.FileBrowserItemDao;
import com.vincent.inc.File_manager.model.FileBrowserItem;
import com.vincent.inc.File_manager.util.DatabaseUtils;
import com.vincent.inc.File_manager.util.ReflectionUtils;
import com.vincent.inc.File_manager.util.Http.HttpResponseThrowers;

@Service
public class FileBrowserItemService {
    public static final String HASH_KEY = "com.vincent.inc.File_manager.service.FileBrowserItemService";

    private DatabaseUtils<FileBrowserItem, Integer> databaseUtils;

    private FileBrowserItemDao fileBrowserItemDao;

    public FileBrowserItemService(DatabaseUtils<FileBrowserItem, Integer> databaseUtils,
            FileBrowserItemDao fileBrowserItemDao) {
        this.databaseUtils = databaseUtils.init(fileBrowserItemDao, HASH_KEY);
        this.fileBrowserItemDao = fileBrowserItemDao;
    }

    public List<FileBrowserItem> getAll() {
        return this.fileBrowserItemDao.findAll();
    }

    public FileBrowserItem getById(int id) {
        FileBrowserItem fileBrowserItem = this.databaseUtils.getAndExpire(id);

        if (ObjectUtils.isEmpty(fileBrowserItem))
            HttpResponseThrowers.throwBadRequest("FileBrowserItem Id not found");

        return fileBrowserItem;
    }

    public FileBrowserItem tryGetById(int id) {
        FileBrowserItem fileBrowserItem = this.databaseUtils.getAndExpire(id);
        return fileBrowserItem;
    }

    public List<FileBrowserItem> getAllByMatchAll(FileBrowserItem fileBrowserItem) {
        Example<FileBrowserItem> example = ReflectionUtils.getMatchAllMatcher(fileBrowserItem);
        return this.fileBrowserItemDao.findAll(example);
    }

    public List<FileBrowserItem> getAllByMatchAny(FileBrowserItem fileBrowserItem) {
        Example<FileBrowserItem> example = ReflectionUtils.getMatchAnyMatcher(fileBrowserItem);
        return this.fileBrowserItemDao.findAll(example);
    }

    public List<FileBrowserItem> getAllByMatchAll(FileBrowserItem fileBrowserItem, String matchCase) {
        Example<FileBrowserItem> example = ReflectionUtils.getMatchAllMatcher(fileBrowserItem, matchCase);
        return this.fileBrowserItemDao.findAll(example);
    }

    public List<FileBrowserItem> getAllByMatchAny(FileBrowserItem fileBrowserItem, String matchCase) {
        Example<FileBrowserItem> example = ReflectionUtils.getMatchAnyMatcher(fileBrowserItem, matchCase);
        return this.fileBrowserItemDao.findAll(example);
    }

    public FileBrowserItem createFileBrowserItem(FileBrowserItem fileBrowserItem) {
        this.databaseUtils.saveAndExpire(fileBrowserItem);
        return fileBrowserItem;
    }

    public FileBrowserItem modifyFileBrowserItem(int id, FileBrowserItem fileBrowserItem) {
        FileBrowserItem oldFileBrowserItem = this.getById(id);

        ReflectionUtils.replaceValue(oldFileBrowserItem, fileBrowserItem);

        oldFileBrowserItem = this.databaseUtils.saveAndExpire(oldFileBrowserItem);

        return oldFileBrowserItem;
    }

    public FileBrowserItem patchFileBrowserItem(int id, FileBrowserItem fileBrowserItem) {
        FileBrowserItem oldFileBrowserItem = this.getById(id);

        ReflectionUtils.patchValue(oldFileBrowserItem, fileBrowserItem);

        oldFileBrowserItem = this.databaseUtils.saveAndExpire(oldFileBrowserItem);

        return oldFileBrowserItem;
    }

    public void deleteFileBrowserItem(int id) {
        this.databaseUtils.deleteById(id);
    }
}