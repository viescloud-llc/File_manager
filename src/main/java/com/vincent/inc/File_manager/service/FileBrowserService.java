package com.vincent.inc.File_manager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.vincent.inc.File_manager.model.FileBrowserItem;
import com.vincent.inc.File_manager.model.FileBrowserLoginBody;
import com.vincent.inc.File_manager.model.FileBrowserToken;
import com.vincent.inc.File_manager.util.DatabaseUtils;
import com.vincent.inc.File_manager.util.ReflectionUtils;
import com.vincent.inc.File_manager.util.Time;
import com.vincent.inc.File_manager.util.Http.HttpResponseThrowers;

@Service
public class FileBrowserService {
    public static final String TOKEN_HASH_KEY = "com.vincent.inc.File_manager.service.FileBrowserService";
    public static final String LOGIN_PATH = "/api/login";
    public static final String RESOURCES_PATH = "/api/resources";
    public static final String UPLOAD_PATH = "/api/resources";
    public static final String DOWNLOAD_PATH = "/api/raw";

    @Value("${file_browser.uri}")
    private String FileBrowserUri;

    @Value("${file_browser.port}")
    private String FileBrowserPort;

    @Value("${file_browser.username}")
    private String FileBrowserUsername;

    @Value("${file_browser.password}")
    private String FileBrowserPassword;

    private DatabaseUtils<FileBrowserToken, Integer> tokenDatabaseUtils;

    @Autowired
    private FileBrowserItemService fileBrowserItemService;

    @Autowired
    private RestTemplate restTemplate;

    public FileBrowserService(DatabaseUtils<FileBrowserToken, Integer> tokenDatabaseUtils) {
        this.tokenDatabaseUtils = tokenDatabaseUtils.init(null, TOKEN_HASH_KEY);
        this.tokenDatabaseUtils.setTTL(new Time(0, 0, 0, 0, 5, 0)); // 5 mins
    }
    
    public List<FileBrowserItem> getAllItem() {
        return fileBrowserItemService.getAll();
    }

    public boolean isItemExist(FileBrowserItem item) {
        if(ObjectUtils.isEmpty(item.getName()) || ObjectUtils.isEmpty(item.getPath()))
            return false;
            
        var anyMatchItemList = this.fileBrowserItemService.getAllByMatchAny(item, ReflectionUtils.CASE_SENSITIVE);
        boolean databaseMatch = anyMatchItemList.parallelStream().anyMatch(e -> e.getPath().equals(item.getPath()) && e.getName().equals(item.getName()));
        
        if(databaseMatch)
            return true;

        var fetchItem = this.getItem(item.getPath());
        if(!ObjectUtils.isEmpty(fetchItem)) {
            this.fileBrowserItemService.createFileBrowserItem(fetchItem);
            return true;
        }

        return false;
    }

    // Native API call

    public FileBrowserItem getItem(String path) {
        String url = String.format("%s%s/%s", this.getFileBrowserUrl(), RESOURCES_PATH, path);
        UriComponents uri = UriComponentsBuilder.fromHttpUrl(url)
                                                .queryParam("auth", this.getToken().getXAuth())
                                                .encode()
                                                .build();
        
        var response = this.restTemplate.getForObject(uri.toUri(), FileBrowserItem.class);
        return response;
    }

    public FileBrowserToken getToken() {
        FileBrowserToken token = this.tokenDatabaseUtils.get(0);

        if(ObjectUtils.isEmpty(token)) {
            token = this.login();
            this.tokenDatabaseUtils.saveAndExpire(0, token);
        }
        
        return token;
    }

    public FileBrowserToken refreshToken() {
        FileBrowserToken token = login();

        this.tokenDatabaseUtils.saveAndExpire(0, token);
        
        return token;
    }

    public FileBrowserToken login() {
        String url = String.format("%s:%s%s", this.FileBrowserUri, this.FileBrowserPort, LOGIN_PATH);
        UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).encode().build();
        HttpEntity<FileBrowserLoginBody> request = new HttpEntity<>(new FileBrowserLoginBody(this.FileBrowserUsername, this.FileBrowserPassword, ""));
        var response = this.restTemplate.postForObject(uri.toUri(), request, String.class);

        if(!ObjectUtils.isEmpty(response)) 
            return new FileBrowserToken(String.format("%s%s", "auth=", response), response);

        return (FileBrowserToken) HttpResponseThrowers.throwServerError("Server file storage is having technical difficulty");
    }

    public String getFileBrowserUrl() {
        return String.format("%s:%s", this.FileBrowserUri, this.FileBrowserPort);
    }
}
