package com.sergej.app;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class BitBucketClient {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    public static final String AUTH_HEADER = "Authorization";
    public static final String ACCEPT_HEADER = "Accept";
    private Config config;

    public BitBucketClient(Config config) {
        this.config = config;
        config.setAccessToken(getAccessToken(config));
        getWorkspaces();
        System.out.println("Авторизация прошла успешно!");
    }

    public JsonNode getWorkspaces() {
        if (!hasAccessToken(config)) {
            throw new RuntimeException("No access token to send request!");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Config.BASE_URL + "/workspaces"))
                .header(AUTH_HEADER, "Bearer " + config.getAccessToken())
                .header(ACCEPT_HEADER, "application/json")
                .GET()
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            checkResponseStatusCode200(response, "Error fetching workspaces: ");
            JsonNode root = objectMapper.readTree(response.body());

            addWorkspaceSlugs(root);

            return root;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public JsonNode getRepositoriesByWorkspace(String workspaceSlug) {
        if (!hasAccessToken(config)) {
            throw new RuntimeException("No access token to send request!");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Config.BASE_URL + "/repositories/" + workspaceSlug))
                .header(AUTH_HEADER, "Bearer " + config.getAccessToken())
                .header(ACCEPT_HEADER, "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            checkResponseStatusCode200(response, "Error fetching repositories: ");

            JsonNode root = objectMapper.readTree(response.body());
            addRepositorySlugs(root);
            return root;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private void checkResponseStatusCode200(HttpResponse<String> response, String message) {
        if (response.statusCode() != 200) {
            throw new RuntimeException(message + response.body());
        }
    }

    private void addWorkspaceSlugs(JsonNode root) {
        JsonNode values = root.get("values");
        for (JsonNode item : values) {
            String slug = item.get("slug").asText();
            config.getWorkspaceSlugs().add(slug);
        }
    }

    private void addRepositorySlugs(JsonNode root) {
        List<String> repoSlugs = new ArrayList<>();
        JsonNode values = root.get("values");
        for (JsonNode item : values) {
            String slug = item.get("slug").asText();
            repoSlugs.add(slug);
        }
        config.setRepoSlugs(repoSlugs);
    }

    private String getAccessToken(Config config) {
        String credentials = config.getKey() + ":" + config.getSecret();
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        String accessToken = null;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://bitbucket.org/site/oauth2/access_token"))
                .header("Authorization", "Basic " + encodedCredentials)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            checkResponseStatusCode200(response, "Failed to get access token: ");
            accessToken = objectMapper.readTree(response.body()).get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return accessToken;
    }

    private boolean hasAccessToken(Config config) {
        return config.getAccessToken() != null && config.getAccessToken().length() > 0;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

}
