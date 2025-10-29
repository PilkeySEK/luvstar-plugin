package me.pilkeysek.data;

public class ChestLockData {
    public String owner;
    public boolean locked;
    public ChestLockData(String owner, boolean locked) {
        this.owner = owner;
        this.locked = locked;
    }
}
