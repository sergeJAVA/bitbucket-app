package com.sergej.app;

public class Commands {
    public static final String DEFAULT_MESSAGE =
        """
        
        --------------------------------------------
        Попробуйте команду help, чтобы ознакомиться с существующими командами.
        --------------------------------------------
        """;
    public static final String NO_WORKSPACES = "You have no any workspaces! :(";

    public static void defaultPrint() {
        System.out.println(DEFAULT_MESSAGE);
    }

    public static void workspacesPrint(Config config) {
        if (config.getWorkspaceSlugs().isEmpty()) {
            System.out.println(NO_WORKSPACES);
            return;
        }

        for (int i = 0; i < config.getWorkspaceSlugs().size(); i++) {
            System.out.println(i + " - " + config.getWorkspaceSlugs().get(i));
        }
    }

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

    public static void currentWorkspacePrint(Config config) {
        if (config.getCurrentWorkspace() == null || config.getCurrentWorkspace().isEmpty()) {
            System.out.println("Вы еще не выбрали workspace! Попробуйте команду workspaces," +
                     " чтобы узнать доступные вам workspace'ы." + '\n' +
                    "Для того чтобы выбрать workspace, воспользуйтесь командой set-workspace <index/slug of workspace> .");
            return;
        }

        System.out.println("Current workspace: " + config.getCurrentWorkspace());
    }

    public static void getReposFromCurrentWorkspace(Config config, BitBucketClient client) {
        if (config.getCurrentWorkspace() == null || config.getCurrentWorkspace().isEmpty()) {
            System.out.println("Вы еще не выбрали workspace! Попробуйте команду workspaces," +
                    " чтобы узнать доступные вам workspace'ы." + '\n' +
                    "Для того чтобы выбрать workspace, воспользуйтесь командой set-workspace <index/slug of workspace> .");
            return;
        }

        client.getRepositoriesByWorkspace(config.getCurrentWorkspace());
        printReposFromCurrentWorkspace(config);
    }

    private static void changeCurrentWorkspaceByIndex(int index, Config config) {
        if (index >= config.getWorkspaceSlugs().size() || index < 0) {
            System.out.println("Произошла ошибка при выборе workspace: Неправильный index или у вас нет workspace'ов."
                            + '\n' + "Попробуйте команду workspaces, чтобы узнать доступные вам workspace'ы.");
            return;
        }

        String workspace = config.getWorkspaceSlugs().get(index);
        config.setCurrentWorkspace(workspace);
        System.out.println("Current workspace установлен на: " + workspace);
    }

    private static void changeCurrentWorkspace(String workspace, Config config) {
        if (!containsWorkspace(workspace, config)) {
            System.out.println("У вас нет такого workspace'а! Попробуйте команду workspaces," +
                    " чтобы узнать доступные вам workspace'ы.");
            return;
        }
        config.setCurrentWorkspace(workspace);
        System.out.println("Current workspace установлен на: " + workspace);
    }

    private static boolean containsWorkspace(String workspace, Config config) {
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
