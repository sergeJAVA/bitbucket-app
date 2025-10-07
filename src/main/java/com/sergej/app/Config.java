package com.sergej.app;

import java.util.ArrayList;
import java.util.List;

public final class Config {

    public static final String BASE_URL = "https://api.bitbucket.org/2.0";
    private String currentWorkspace;
    private List<String> workspaceSlugs;
    private List<String> repoSlugs;
    private final String key;
    private final String secret;
    private String accessToken;

    public Config(String key, String secret) {
        this.key = key;
        this.secret = secret;
        this.workspaceSlugs = new ArrayList<>();
        this.repoSlugs = new ArrayList<>();
    }

    public static Config createConfig(String email, String apiToken) {
        return new Config(email, apiToken);
    }

    public String getKey() {
        return key;
    }

    public String getSecret() {
        return secret;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public List<String> getWorkspaceSlugs() {
        return workspaceSlugs;
    }

    public List<String> getRepoSlugs() {
        return repoSlugs;
    }

    public String getCurrentWorkspace() {
        return currentWorkspace;
    }

    public void setCurrentWorkspace(String currentWorkspace) {
        this.currentWorkspace = currentWorkspace;
    }

    public void setWorkspaceSlugs(List<String> workspaceSlugs) {
        this.workspaceSlugs = workspaceSlugs;
    }

    public void setRepoSlugs(List<String> repoSlugs) {
        this.repoSlugs = repoSlugs;
    }

    @Override
    public String toString() {
        return "Config{" +
                "currentWorkspace='" + currentWorkspace + '\'' +
                ", workspaceSlugs=" + workspaceSlugs +
                ", repoSlugs=" + repoSlugs +
                ", key='" + key + '\'' +
                ", secret='" + secret + '\'' +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }

}
