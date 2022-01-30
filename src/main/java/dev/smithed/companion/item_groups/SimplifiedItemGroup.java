package dev.smithed.companion.item_groups;

import java.util.Arrays;

public class SimplifiedItemGroup {
    private String operation;
    private String id;
    private String name;
    private SimplifiedItemGroupEntry icon;
    private String texture;
    private SimplifiedItemGroupEntry[] entries;

    public String getOperation() {
        return operation;
    }

    public void setOperation(String value) {
        this.operation = value;
    }

    public String getID() {
        return id;
    }

    public void setID(String value) {
        this.id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public SimplifiedItemGroupEntry getIcon() {
        return icon;
    }

    public void setIcon(SimplifiedItemGroupEntry value) {
        this.icon = value;
    }

    public String getTexture() {
        return texture;
    }

    public void setTexture(String value) {
        this.texture = value;
    }

    public SimplifiedItemGroupEntry[] getEntries() {
        return entries;
    }

    public void setEntries(SimplifiedItemGroupEntry[] value) {
        this.entries = value;
    }

    @Override
    public String toString() {
        return "SimplifiedItemGroup{" +
                "operation='" + operation + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", icon=" + icon +
                ", texture='" + texture + '\'' +
                ", entries=" + Arrays.toString(entries) +
                '}';
    }
}
