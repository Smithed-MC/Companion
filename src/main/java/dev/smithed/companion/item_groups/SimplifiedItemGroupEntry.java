package dev.smithed.companion.item_groups;

public class SimplifiedItemGroupEntry {
    private String type;
    private String id;
    private String nbt;

    public String getType() {
        return type;
    }

    public void setType(String value) {
        this.type = value;
    }

    public String getID() {
        return id;
    }

    public void setID(String value) {
        this.id = value;
    }

    public String getNbt() {
        return nbt;
    }

    public void setNbt(String value) {
        this.nbt = value;
    }

    @Override
    public String toString() {
        return "SimplifiedItemGroupEntry{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", nbt='" + nbt + '\'' +
                '}';
    }
}
