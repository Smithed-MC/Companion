package dev.smithed.companion;

public class SmithedConfig {
    private CachedHash[] cachedHashs;

    public CachedHash[] getCachedHashs() { return cachedHashs; }
    public void setCachedHashs(CachedHash[] value) { this.cachedHashs = value; }

    public static class CachedHash {
        private String id;
        private long hash;

        public String getID() { return id; }
        public void setID(String value) { this.id = value; }

        public long getHash() { return hash; }
        public void setHash(long value) { this.hash = value; }
    }
}
