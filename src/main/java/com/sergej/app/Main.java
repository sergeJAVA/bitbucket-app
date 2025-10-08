package com.sergej.app;

import java.util.Scanner;

public class Main {

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

        Application application = new Application(bitBucketClient);
        application.run();
    }

}
