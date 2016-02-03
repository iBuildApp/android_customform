package com.ibuildapp.romanblack.CustomFormPlugin.groups;

import java.util.Date;

/**
 * Entity class that represents calendar item of group.
 */
public class GroupItemCalendar extends GroupItem {
    private boolean isSet = false;
    private Date date = new Date();

    /**
     * Returns the default calendar value.
     * @return the default value
     */
    @Override
    public void setValue(String value) {
        setSet(true);
        setDate(value);
        super.setValue(value);
    }

    /**
     * Sets the default date if it can be parsed from given string.
     * Supported date format: MM.dd.yyyy.
     * @param date the date string with given format
     */
    public void setDate(String date) {
        setSet(true);
        try {
            int year = Integer.parseInt(date.substring(6, 10));
            int month = Integer.parseInt(date.substring(0, 2));
            int day = Integer.parseInt(date.substring(3, 5));
            this.date = new Date(year - 1900, month - 1, day);
        } catch (Exception ex) {
        }
    }

    /**
     * Returns the current calendar date.
     * @return the current date
     */
    public Date getDate() {

        return date;
    }

    /**
     * Sets the date to calendar item.
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Sets the date to calendar item.
     * @param year the date year
     * @param month the date month
     * @param dayOfMonth the date day of month
     */
    public void setDate(int year, int month, int dayOfMonth) {
        setSet(true);
        this.date.setYear(year - 1900);
        this.date.setMonth(month);
        this.date.setDate(dayOfMonth);
    }

    public boolean isSet() {
        return isSet;
    }

    public void setSet(boolean isSet) {
        this.isSet = isSet;
    }
}
