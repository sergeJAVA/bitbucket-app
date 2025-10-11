package com.sergej.app;

import java.util.List;

/**
 * Класс, который предоставляет команды для работы с консолью.
 */
public class Commands {

    public static final String DEFAULT_MESSAGE =
            """
            
            --------------------------------------------
            Попробуйте команду help, чтобы ознакомиться с существующими командами.
            --------------------------------------------
            """;

    public static final String NO_WORKSPACES =
            """
            
            --------------------------------------------
            You have no any workspaces! :(
            --------------------------------------------
            """;

    public static final String NO_CURRENT_WORKSPACE =
            """
            
            --------------------------------------------
            Вы еще не выбрали workspace! Попробуйте команду workspaces, чтобы узнать доступные вам workspace'ы.
            Для того чтобы выбрать workspace, воспользуйтесь командой set-workspace <index/slug of workspace> .
            --------------------------------------------
            """;

    public static final String NO_WORKSPACE_SLUG =
            """
            
            --------------------------------------------
            У вас нет такого workspace'а! Попробуйте команду workspaces, чтобы узнать доступные вам workspace'ы.
            --------------------------------------------
            """;

    public static final String NO_WORKSPACE_SLUG_OR_WRONG_INDEX =
            """
            
            --------------------------------------------
            Произошла ошибка при выборе workspace: Неправильный index/slug или у вас нет workspace'ов.
            Попробуйте команду workspaces, чтобы узнать доступные вам workspace'ы.
            --------------------------------------------
            """;

    public static final String NO_PARTICULAR_WORKSPACE_SLUG_OR_WRONG_INDEX =
            """
            
            --------------------------------------------
            Произошла ошибка при выборе workspace: Неправильный index/slug или у вас нет такого workspace'а.
            Попробуйте команду workspaces, чтобы узнать доступные вам workspace'ы.
            --------------------------------------------
            """;

    public static final String HELP_MESSAGE =
            """
           
            --------------------------------------------
            stop - Завершить программу.
            workspaces - Вывод всех workspace'ов.
            set-workspace <index/slug> - Установить current workspace.
            workspace - Вывод значения current workspace.
            repos - Вывод списка репозиториев из current workspace.
            workspace-users - Вывод всех пользователей из current workspace.
            workspace-users <slug> - Вывод всех пользователей из определённого workspace'а.
            def-reviewers <repo_slug> - Вывод списка default-reviewers из определённого репозитория в current workspace.
            rm-def-reviewer <repo_slug> <uuid> - Удаление пользователя из списка default reviewers у
              определённого репозитория в current workspace. UUID передавать с фигурными скобками!
            rm-def-reviewer all <uuid> - Удаление пользователя из списка default reviewers у всех репозиториев.
              UUID передавать с фигурными скобками!
            add-def-reviewer <repo_slug> <uuid> - Добавление пользователя в список default reviewers у
              определённого репозитория в current workspace. UUID передавать с фигурными скобками!
            add-def-reviewer all <uuid> - Добавление пользователя в список default reviewers у всех репозиториев.
              UUID передавать с фигурными скобками!
            --------------------------------------------
           """;

    /**
     * Вывод дефолтного сообщения в консоль.
     */
    public static void defaultPrint() {
        System.out.println(DEFAULT_MESSAGE);
    }

    /**
     * Обновление списка и вывод всех элементов из {@code List<String>} workspaceSlugs класса {@link Config}.
     */
    public static void workspacesPrint(BitBucketClient client) {
        client.getWorkspaces();
        Config config = client.getConfig();
        if (config.getWorkspaceSlugs().isEmpty()) {
            System.out.println(NO_WORKSPACES);
            return;
        }

        for (int i = 0; i < config.getWorkspaceSlugs().size(); i++) {
            System.out.println(i + " - " + config.getWorkspaceSlugs().get(i));
        }
    }

    /**
     * Команда для изменения текущего workspace'а в конфигурации.
     * @param workspace тот, на который хотим изменить.
     * @param config конфигурация приложения.
     */
    public static void setCurrentWorkspace(String workspace, Config config) {
        if (config.getWorkspaceSlugs().isEmpty()) {
            System.out.println(NO_WORKSPACES);
            return;
        }

        try {
            int index = Integer.parseInt(workspace);
            changeCurrentWorkspaceByIndex(index, config);
        } catch (NumberFormatException ex) {
            changeCurrentWorkspace(workspace, config);
        }
    }

    /**
     * Вывести в консоль текущий workspace.
     * @param config конфигурация приложения.
     */
    public static void currentWorkspacePrint(Config config) {
        if (config.getCurrentWorkspace() == null || config.getCurrentWorkspace().isEmpty()) {
            System.out.println(NO_CURRENT_WORKSPACE);
            return;
        }

        System.out.println("Current workspace: " + config.getCurrentWorkspace());
    }

    /**
     * Получение всех имён репозиториев из текущего workspace'а и вывод их в консоль.
     * @param client отправляет запросы на BitBucket API.
     */
    public static void getReposFromCurrentWorkspace(BitBucketClient client) {
        Config config = client.getConfig();
        if (config.getCurrentWorkspace() == null || config.getCurrentWorkspace().isEmpty()) {
            System.out.println(NO_CURRENT_WORKSPACE);
            return;
        }

        client.getRepositoriesByWorkspace(config.getCurrentWorkspace());
        printReposFromCurrentWorkspace(config);
    }

    /**
     * Команда для вывода всех пользователей из определённого workspace'а в консоль.
     * @param workspace index/slug, который принадлежит workspace'у.
     * @param client отправляет запрос на BitBucket API.
     */
    public static void printWorkspaceUsers(String workspace, BitBucketClient client) {
        Config config = client.getConfig();

        if (workspace == null || workspace.isEmpty()) {
            System.out.println("Передан неправильный workspace!");
            return;
        }

        workspace = getWorkspaceByString(workspace, config);
        if (workspace == null) {
            return;
        }

        if (!containsWorkspace(workspace, config)) {
            System.out.println(NO_PARTICULAR_WORKSPACE_SLUG_OR_WRONG_INDEX);
            return;
        }

        List<User> users = client.getListUsersFromWorkspace(workspace);

        if (users == null) {
            return;
        }

        if (users.isEmpty()) {
            System.out.println("В workspace ("+ config.getCurrentWorkspace()+") нет пользователей.");
            return;
        }

        for (int i = 0; i < config.getWorkspaceUsers().size(); i++) {
            System.out.println(i + " - " + config.getWorkspaceUsers().get(i));
        }
    }

    /**
     * Команда для вывода всех пользователей из текущего workspace'а в консоль.
     * @param client отправляет запрос на BitBucket API.
     */
    public static void printUsersFromCurrentWorkspace(BitBucketClient client) {
        Config config = client.getConfig();

        if (config.getCurrentWorkspace() == null || config.getCurrentWorkspace().isEmpty()) {
            System.out.println(NO_CURRENT_WORKSPACE);
            return;
        }

        List<User> users = client.getListUsersFromWorkspace(config.getCurrentWorkspace());

        if (users == null) {
            return;
        }

        if (users.isEmpty()) {
            System.out.println("В workspace ("+ config.getCurrentWorkspace()+") нет пользователей.");
            return;
        }

        for (int i = 0; i < config.getWorkspaceUsers().size(); i++) {
            System.out.println(i + " - " + config.getWorkspaceUsers().get(i));
        }
    }

    /**
     * Команда для вывода всех default reviewers из определённого репозитория (из текущего workspace'а) в консоль.
     */
    public static void printDefaultReviewersFromCurrentWorkspace(BitBucketClient client, String repoSlug) {
        String workspace = client.getConfig().getCurrentWorkspace();
        if (workspace == null || workspace.isEmpty()) {
            System.out.println(NO_CURRENT_WORKSPACE);
            return;
        }

        List<User> defaultReviewers = client.getListDefaultReviewers(workspace, repoSlug);

        if (defaultReviewers == null) {
            return;
        }

        if (defaultReviewers.isEmpty()) {
            System.out.println("У репозитория (" + repoSlug + ") нет default reviewers.");
            return;
        }

        for (int i = 0; i < defaultReviewers.size(); i++) {
            System.out.println(i + " - " + defaultReviewers.get(i));
        }
    }

    /**
     * Команда для добавления пользователя в default reviewers в определённый репозиторий (из текущего workspace'а).
     */
    public static void addDefaultReviewer(BitBucketClient client, String repoSlug, String uuid) {
        Config config = client.getConfig();
        String workspace = config.getCurrentWorkspace();
        if (workspace == null || workspace.isEmpty()) {
            System.out.println(NO_CURRENT_WORKSPACE);
            return;
        }

        client.addDefaultReviewer(workspace, repoSlug, uuid);
    }

    /**
     * Команда для удаления пользователя из default reviewers в определённом репозитории (из текущего workspace'а).
     */
    public static void deleteDefaultReviewer(BitBucketClient client, String repoSlug, String uuid) {
        Config config = client.getConfig();
        String workspace = config.getCurrentWorkspace();
        if (workspace == null || workspace.isEmpty()) {
            System.out.println(NO_CURRENT_WORKSPACE);
            return;
        }

        client.deleteDefaultReviewer(workspace, repoSlug, uuid);
    }

    /**
     * Команда для удаления пользователя из default reviewers во всех репозиториях (из текущего workspace'а).
     */
    public static void deleteDefaultReviewerFromAllRepos(BitBucketClient client, String uuid) {
        Config config = client.getConfig();
        if (config.getCurrentWorkspace() == null || config.getCurrentWorkspace().isEmpty()) {
            System.out.println(NO_CURRENT_WORKSPACE);
            return;
        }

        client.getRepositoriesByWorkspace(config.getCurrentWorkspace());
        deleteDefaultReviewerFromEachRepo(config.getRepoSlugs(), client, uuid);
    }

    /**
     * Команда для добавления пользователя в default reviewers во всех репозиториях (из текущего workspace'а).
     */
    public static void addDefaultReviewerToAllRepos(BitBucketClient client, String uuid) {
        Config config = client.getConfig();
        if (config.getCurrentWorkspace() == null || config.getCurrentWorkspace().isEmpty()) {
            System.out.println(NO_CURRENT_WORKSPACE);
            return;
        }

        client.getRepositoriesByWorkspace(config.getCurrentWorkspace());
        addDefaultReviewerToEachRepo(config.getRepoSlugs(), client, uuid);
    }

    /**
     * Вывод списка команд.
     */
    public static void printHelpMessage() {
        System.out.println(HELP_MESSAGE);
    }

    private static void deleteDefaultReviewerFromEachRepo(List<String> repos, BitBucketClient client, String uuid) {
        if (repos.isEmpty()) {
            System.out.println("В current workspace ("+ client.getConfig().getCurrentWorkspace() +") нет репозиториев.");
            return;
        }

        for (String repo : repos) {
            client.deleteDefaultReviewer(client.getConfig().getCurrentWorkspace(), repo, uuid);
        }
    }

    private static void addDefaultReviewerToEachRepo(List<String> repos, BitBucketClient client, String uuid) {
        if (repos.isEmpty()) {
            System.out.println("В current workspace ("+ client.getConfig().getCurrentWorkspace() +") нет репозиториев.");
            return;
        }

        for (String repo : repos) {
            client.addDefaultReviewer(client.getConfig().getCurrentWorkspace(), repo, uuid);
        }
    }

    private static void changeCurrentWorkspaceByIndex(int index, Config config) {
        if (index >= config.getWorkspaceSlugs().size() || index < 0) {
            System.out.println(NO_WORKSPACE_SLUG_OR_WRONG_INDEX);
            return;
        }

        String workspace = config.getWorkspaceSlugs().get(index);
        config.setCurrentWorkspace(workspace);
        System.out.println("Current workspace установлен на: " + workspace);
    }

    private static String getWorkspaceByString(String string, Config config) {
        try {
            int index = Integer.parseInt(string);
            if (index >= config.getWorkspaceSlugs().size() || index < 0) {
                System.out.println(NO_PARTICULAR_WORKSPACE_SLUG_OR_WRONG_INDEX);
                return null;
            }
            return config.getWorkspaceSlugs().get(index);
        }catch (NumberFormatException ex) {
            return string;
        }
    }

    private static void changeCurrentWorkspace(String workspace, Config config) {
        if (!containsWorkspace(workspace, config)) {
            System.out.println(NO_WORKSPACE_SLUG);
            return;
        }
        config.setCurrentWorkspace(workspace);
        System.out.println("Current workspace установлен на: " + workspace);
    }

    private static boolean containsWorkspace(String workspace, Config config) {
        if (workspace == null) {
            return false;
        }
        return config.getWorkspaceSlugs().contains(workspace);
    }

    private static void printReposFromCurrentWorkspace(Config config) {
        if (config.getRepoSlugs().isEmpty()) {
            System.out.println("В current workspace ("+ config.getCurrentWorkspace() +") нет репозиториев!");
            return;
        }

        for (int i = 0; i < config.getRepoSlugs().size(); i++) {
            System.out.println(i + " - " + config.getRepoSlugs().get(i));
        }
    }

}
