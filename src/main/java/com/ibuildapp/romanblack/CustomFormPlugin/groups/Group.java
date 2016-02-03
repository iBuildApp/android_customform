package com.ibuildapp.romanblack.CustomFormPlugin.groups;

import java.util.ArrayList;

/**
 * Entity class that represents module form group.
 */
public class Group {

    private String title = "";
    private ArrayList<GroupItem> items = new ArrayList<GroupItem>();

    public void addItem(GroupItem gi) {
        items.add(gi);
    }

    /**
     * Returns the group elements array.
     * @return the group elements array
     */
    public ArrayList<GroupItem> getItems() {
        return items;
    }

    /**
     * Returns the group title
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the group title.
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
}
