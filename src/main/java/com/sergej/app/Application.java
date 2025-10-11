package com.sergej.app;

import java.util.Scanner;

/**
 * Класс, который запускает консольную программу.
 * @param client объект, который будет выполнять запросы на BitBucket API.
 */
public record Application(BitBucketClient client) {

    private static boolean isRunning = true;

    /**
     * Метод для запуска обработчика команд.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println();
        while (isRunning) {
            System.out.print("> ");
            String[] commandParts = scanner.nextLine().trim().split("\\s+");

            if (commandParts.length > 0 && commandParts.length < 2) {
                command(commandParts[0], client);
                continue;
            }

            commands(commandParts, client);

        }
        System.out.println("Программа завершена...");
    }

    /**
     * Для одиночной команды без аргументов
     */
    private void command(String command, BitBucketClient client) {
        switch (command) {
            case "stop" -> setIsRunning(false);
            case "workspaces" -> Commands.workspacesPrint(client);
            case "workspace" -> Commands.currentWorkspacePrint(client.getConfig());
            case "repos" -> Commands.getReposFromCurrentWorkspace(client);
            case "workspace-users" -> Commands.printUsersFromCurrentWorkspace(client);
            case "help" -> Commands.printHelpMessage();
            default -> Commands.defaultPrint();
        }
    }

    /**
     * Для составной команды.
     */
    private void commands(String[] commands, BitBucketClient client) {
        switch (commands[0]) {
            case "set-workspace" -> {
                if (commands.length != 2) {
                    Commands.defaultPrint();
                    break;
                }
                Commands.setCurrentWorkspace(commands[1], client.getConfig());
            }
            case "workspace-users" -> {
                if (commands.length != 2) {
                    Commands.defaultPrint();
                    break;
                }
                Commands.printWorkspaceUsers(commands[1], client);
            }
            case "def-reviewers" -> {
                if (commands.length != 2) {
                    Commands.defaultPrint();
                    break;
                }
                Commands.printDefaultReviewersFromCurrentWorkspace(client, commands[1]);
            }
            case "rm-def-reviewer" -> {
                if (commands.length != 3) {
                    Commands.defaultPrint();
                    break;
                }
                if (commands[1].equals("all")) {
                    Commands.deleteDefaultReviewerFromAllRepos(client, commands[2]);
                    break;
                }
                Commands.deleteDefaultReviewer(client, commands[1], commands[2]);
            }
            case "add-def-reviewer" -> {
                if (commands.length != 3) {
                    Commands.defaultPrint();
                    break;
                }
                if (commands[1].equals("all")) {
                    Commands.addDefaultReviewerToAllRepos(client, commands[2]);
                    break;
                }
                Commands.addDefaultReviewer(client, commands[1], commands[2]);
            }
            default -> Commands.defaultPrint();
        }
    }

    public static boolean isIsRunning() {
        return isRunning;
    }

    public static void setIsRunning(boolean isRunning) {
        Application.isRunning = isRunning;
    }

}
