package com.vincent.inc.File_manager.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.vincent.inc.File_manager.model.FileBrowserLoginBody;
import com.vincent.inc.File_manager.model.FileBrowserToken;
import com.vincent.inc.File_manager.util.DatabaseUtils;

@Service
public class FileBrowserService {
    public static final String HASH_KEY = "com.vincent.inc.File_manager.service.FileBrowserService";
    public static final String LOGIN_PATH = "/api/login";

    @Value("${file_browser.uri}")
    private String FileBrowserUri;

    @Value("${file_browser.port}")
    private String FileBrowserPort;

    @Value("${file_browser.username}")
    private String FileBrowserUsername;

    @Value("${file_browser.password}")
    private String FileBrowserPassword;

    private DatabaseUtils<FileBrowserToken, Integer> databaseUtils;

    @Autowired
    private RestTemplate restTemplate;

    public FileBrowserService(DatabaseUtils<FileBrowserToken, Integer> databaseUtils) {
        this.databaseUtils = databaseUtils.init(null, HASH_KEY);
    }

    public FileBrowserToken getToken() {
        FileBrowserToken token = this.databaseUtils.get(0);

        if(ObjectUtils.isEmpty(token)) {
            token = this.login();
            this.databaseUtils.saveAndExpire(0, token);
        }
        
        return token;
    }

    public FileBrowserToken refreshToken() {
        FileBrowserToken token = login();

        this.databaseUtils.saveAndExpire(0, token);
        
        return token;
    }

    public FileBrowserToken login() {
        String url = String.format("%s:%s%s", this.FileBrowserUri, this.FileBrowserPort, LOGIN_PATH);
        UriComponents uri = UriComponentsBuilder.fromHttpUrl(url).encode().build();
        HttpEntity<FileBrowserLoginBody> request = new HttpEntity<>(new FileBrowserLoginBody(this.FileBrowserUsername, this.FileBrowserPassword, ""));
        var response = this.restTemplate.postForObject(uri.toUri(), request, String.class);

        if(!ObjectUtils.isEmpty(response)) 
            return new FileBrowserToken(String.format("%s%s", "auth=", response), response);

        return null;
    }
}
