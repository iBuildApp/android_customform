package com.ibuildapp.romanblack.CustomFormPlugin.groups;

/**
 * Entity class that represents entry field item of group.
 */
public class GroupItemEntryField extends GroupItem {

    private String type = "";

    /**
     * Returns the entry field type.
     * This value can be "number" or "text"
     * @return the type
     */ 
    public String getType() {
        return type;
    }

    /**
     * Sets the entry field type.
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
}
