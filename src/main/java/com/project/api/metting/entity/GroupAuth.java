package com.project.api.metting.entity;

public enum GroupAuth {
    MEMBER("MEMBER"), HOST("HOST");


    private final String displayName;

    GroupAuth(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }


    @Override
    public String toString() {
        return displayName;
    }
}
