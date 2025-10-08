package com.sergej.app;

public record User(String nickname, String accountId, String type, String uuid) {
    @Override
    public String toString() {
        return "nickname='" + nickname + '\'' +
                ", accountId='" + accountId + '\'' +
                ", type='" + type + '\'' +
                ", uuid='" + uuid + '\'' ;
    }
}
