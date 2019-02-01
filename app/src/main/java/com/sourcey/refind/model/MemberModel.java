package com.sourcey.refind.model;

public class MemberModel {
    private String name;
    private String color;

    public MemberModel(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public MemberModel() {
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "MemberData{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
