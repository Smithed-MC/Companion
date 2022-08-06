package dev.smithed.companion.item_groups;

import com.google.gson.annotations.Expose;

import java.util.Arrays;

public class SimplifiedItemGroup {

    private String id;
    private SimplifiedItemGroupEntry[] entries;
    private String name;
    private SimplifiedItemGroupEntry icon;
    private String texture;

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
    public String getID() {
        return id;
    }
    public void setID(String value) {
        this.id = value;
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
                ", id='" + getID() + '\'' +
                ", name='" + name + '\'' +
                ", icon=" + icon +
                ", texture='" + texture + '\'' +
                ", entries=" + Arrays.toString(getEntries()) +
                '}';
    }
}