package com.project.api.metting.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Membership {
    GENERAL, PREMIUM;

    @JsonCreator
    public static Membership fromString(String value) {
        return valueOf(value.toUpperCase());
    }
}
