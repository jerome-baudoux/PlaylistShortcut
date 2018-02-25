package com.jbaudoux.smp;

public class PlayList {
    private final int id;
    private final String name;

    public PlayList(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
