package com.ibuildapp.romanblack.CustomFormPlugin;

/**
 * Entity class that represents checkbox item of group.
 */
public class GroupItemCheckBox extends GroupItem {

    private boolean checked = false;
    
    /**
     * Sets checked or unchecked value to checkbox
     * @param value 
     */
    @Override
    public void setValue(String value) {
        if (value.equalsIgnoreCase("checked")) {
            checked = true;
        }
        super.setValue(value);
    }

    /**
     * Returns the state of checkbox.
     * @return true if checkbox is checked, false otherwise
     */
    public boolean isChecked() {
        return checked;
    }

    /**
     * Sets the checked state of checkbox.
     * @param checked the checked state to set
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
