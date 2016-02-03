package com.ibuildapp.romanblack.CustomFormPlugin.groups;

/**
 * Entity class that represents basic element of group.
 */
public class GroupItem {

    /**
     * Constructs new GroupItem.
     */
    public GroupItem() {
    }

    /**
     * Constructs new GroupItem with given label and default value.
     * @param label the GroupItem label
     * @param value the GroupItem default value
     */
    public GroupItem(String label, String value) {
        this.label = label;
        this.value = value;
    }
    
    private String label = "";
    private String value = "";

    /**
     * Returns the GroupItem label.
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the GroupItem label.
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the default GroupItem value.
     * @return the default value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the default GroupItem value.
     * @param value the default value to set
     */
    public void setValue(String value) {
        this.value = value;
    }
}
