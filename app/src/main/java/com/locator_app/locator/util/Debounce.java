package com.locator_app.locator.util;

public class Debounce {
    int minCallDelay;
    private long lastCall;

    public Debounce(int minCallDelay) {
        this.minCallDelay = minCallDelay;
    }

    public boolean calledRecently() {
        long delay = System.currentTimeMillis() - lastCall;
        lastCall = System.currentTimeMillis();
        return delay < minCallDelay;
    }
}
