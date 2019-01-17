package ru.otus.hibernate.cache;

public class MyElement<K, V> {
    private final K key;
    private final Object value;
    private final long creationTime;
    private long lastAccessTime;

    @Override
    public String toString() {
        return value.toString();
    }

    public MyElement(K key, V value) {
        this.key = key;
        this.value = value;
        this.creationTime = getCurrentTime();
        this.lastAccessTime = getCurrentTime();
    }

    protected long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public K getKey() {
        return key;
    }

    public String getValue() {
        return value.toString();
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setAccessed() {
        lastAccessTime = getCurrentTime();
    }
}