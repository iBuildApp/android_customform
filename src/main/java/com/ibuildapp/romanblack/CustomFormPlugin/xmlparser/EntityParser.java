package com.ibuildapp.romanblack.CustomFormPlugin.xmlparser;

import android.graphics.Color;
import android.util.Log;
import android.util.Xml;

import java.util.ArrayList;

/**
 * This class using for module xml data parsing.
 */
public class EntityParser {

    /**
     * Constructs new EntityParser instance.
     * @param xml - module xml data to parse
     */
    public EntityParser(String xmlData) {
        this.xmlData = xmlData.trim();
    }
    
    private int color1 = Color.TRANSPARENT;
    private int color2 = Color.TRANSPARENT;
    private int color3 = Color.TRANSPARENT;
    private int color4 = Color.TRANSPARENT;
    private int color5 = Color.TRANSPARENT;
    private String xmlData = null;
    private ArrayList<Form> forms = new ArrayList<Form>();

    /**
     * Parses module XML data that was set in constructor.
     */
    public void parse() {
        FormHandler handler = new FormHandler();

        try {
            Xml.parse(xmlData, handler);
        } catch (Exception e) {
            Log.w(e.toString(), e.getMessage());
        }

        this.forms = handler.getForms();
        this.color1 = handler.getColor1();
        this.color2 = handler.getColor2();
        this.color3 = handler.getColor3();
        this.color4 = handler.getColor4();
        this.color5 = handler.getColor5();
    }

    /**
     * Returns the configured module input forms.
     * @return the configured module input forms
     */
    public ArrayList<Form> getForms() {
        return forms;
    }

    /**
     * Returns the color scheme color 1.
     * @return the color 1
     */
    public int getColor1() {
        return color1;
    }

    /**
     * Returns the color scheme color 2.
     * @return the color 2
     */
    public int getColor2() {
        return color2;
    }

    /**
     * Returns the color scheme color 3.
     * @return the color 3
     */
    public int getColor3() {
        return color3;
    }

    /**
     * Returns the color scheme color 4.
     * @return the color 4
     */
    public int getColor4() {
        return color4;
    }

    /**
     * Returns the color scheme color 5.
     * @return the color 5
     */
    public int getColor5() {
        return color5;
    }
}
