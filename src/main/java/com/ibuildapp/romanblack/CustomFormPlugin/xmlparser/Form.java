package com.ibuildapp.romanblack.CustomFormPlugin.xmlparser;

import com.ibuildapp.romanblack.CustomFormPlugin.groups.Group;

import java.util.ArrayList;

/**
 * Entity class that represents module input form.
 */
public class Form {

    private String type = "";
    private String address = "";
    private String subject = "";
    private ArrayList<Group> groups = new ArrayList<Group>();
    private ArrayList<FormButton> buttons = new ArrayList<FormButton>();

    /**
     * Adds the group of elements to this input form.
     * @param group the group of elements
     */
    public void addGroup(Group group) {
        getGroups().add(group);
    }

    /**
     * Adds the submit button to this input form.
     * @param button the submit button
     */
    public void addButton(FormButton button) {
        getButtons().add(button);
    }

    /**
     * Returns the type of this input form.
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type of this input form.
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the input form groups.
     * @return the input form groups
     */
    public ArrayList<Group> getGroups() {
        return groups;
    }

    /**
     * Returns the submit email address.
     * @return the email address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the submit email address.
     * @param address the email address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns the submit email subject.
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Sets the submit email subject.
     * @param subject the email subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Returns the submit email buttons array.
     * @return the submit email buttons array
     */
    public ArrayList<FormButton> getButtons() {
        return buttons;
    }
}
