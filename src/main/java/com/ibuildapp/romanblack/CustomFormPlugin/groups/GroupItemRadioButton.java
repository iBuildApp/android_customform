package com.ibuildapp.romanblack.CustomFormPlugin.groups;

import com.ibuildapp.romanblack.CustomFormPlugin.groups.GroupItem;

/**
 * Entity class that represents radio button item of group.
 */
public class GroupItemRadioButton extends GroupItem {

    private boolean selected = false;

    /**
     * Sets checked or unchecked value to radio button
     * @param value 
     */
    @Override
    public void setValue(String value) {
        if (value.equalsIgnoreCase("checked")) {
            setSelected(true);
        }
        super.setValue(value);
    }

    /**
     * Returns the state of radio button.
     * @return true if radio button is selected, false otherwise
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets the selected state of radio button.
     * @param checked the selected state to set
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
