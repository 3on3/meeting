package com.project.api.metting.entity;

public enum Place {
    SEOUL_GYEONGGI("서울/경기"),
    CHUNGCHEONG_DAEJEON("충청/대전"),
    GYEONGBUK_DAEGU("경북/대구"),
    GYEONGNAM_BUSAN("경남/부산"),
    GANGWONDO("강원도"),
    JEONLABUKDO("전라북도"),
    JEONNAM_GWANGJU("전남/광주"),
    JEJUDO("제주도");

    private final String displayName;

    Place(String displayName) {
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