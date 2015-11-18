package com.ibuildapp.romanblack.CustomFormPlugin;

import java.util.ArrayList;

/**
 * Entity class that represents dropdown list item of group.
 */
public class GroupItemDropDown extends GroupItem {

    private int selectedIndex = 0;
    private ArrayList<GroupItemDropDownItem> items =
            new ArrayList<GroupItemDropDownItem>();

    /**
     * Adds a new dropdown item.
     * @param item dropdown item to add
     */
    public void addItem(GroupItemDropDownItem item) {
        items.add(item);
    }

    /**
     * Returns the selected dropdown item index.
     * @return the selected dropdown item index
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Sets the index of selected dropdown item.
     * @param selectedIndex the selected index to set
     */
    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    /**
     * Returns the dropdown items array.
     * @return the dropdown items array
     */
    public ArrayList<GroupItemDropDownItem> getItems() {
        return items;
    }
}
