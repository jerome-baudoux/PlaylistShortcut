package com.jbaudoux.smp;

public class PlayList {
    private final int id;
    private final int count;
    private final String name;

    public PlayList(int id, int count, String name) {
        this.id = id;
        this.count = count;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }
}
