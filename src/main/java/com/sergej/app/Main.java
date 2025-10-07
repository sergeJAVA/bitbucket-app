package com.sergej.app;

import java.util.Scanner;

public class Main {

    private static boolean isRunning = true;

    public static void main(String[] args) {
        String OAuthConsumerKey = null;
        String OAuthConsumerSecret = null;


        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите key: ");
        OAuthConsumerKey = scanner.nextLine().trim();
        System.out.print("Введите secret: ");
        OAuthConsumerSecret = scanner.nextLine().trim();

        Config config = Config.createConfig(OAuthConsumerKey, OAuthConsumerSecret);
        BitBucketClient bitBucketClient = new BitBucketClient(config);

        run(scanner, bitBucketClient);
    }

    public static void run(Scanner scanner, BitBucketClient client) {
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

    private static void command(String command, BitBucketClient client) {
        switch (command) {
            case "stop" -> {
                setIsRunning(false);
            }
            case "workspaces" -> {
                Commands.workspacesPrint(client.getConfig());
            }
            case "workspace" -> {
                Commands.currentWorkspacePrint(client.getConfig());
            }
            case "repos" -> {
                Commands.getReposFromCurrentWorkspace(client.getConfig(), client);
            }
            default -> {
                Commands.defaultPrint();
            }
        }
    }

    private static void commands(String[] commands, BitBucketClient client) {
        switch (commands[0]) {
            case "set-workspace" -> {
                if (commands.length == 2) {
                    Commands.setCurrentWorkspace(commands[1], client.getConfig());
                } else {
                    Commands.defaultPrint();
                }
            }
            default -> {
                Commands.defaultPrint();
            }
        }
    }

    public static boolean isIsRunning() {
        return isRunning;
    }

    public static void setIsRunning(boolean isRunning) {
        Main.isRunning = isRunning;
    }
}
