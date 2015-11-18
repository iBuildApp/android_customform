package com.ibuildapp.romanblack.CustomFormPlugin;

import android.graphics.Color;
import android.util.Log;
import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Sax handler that handle configuration xml tags and prepare module data structure.
 */
public class FormHandler extends DefaultHandler {

    private boolean inEntryField = false;
    private boolean inTextArea = false;
    private boolean inCheckBox = false;
    private boolean inDatePicker = false;
    private boolean inDropDown = false;
    private boolean inRadioButton = false;
    private boolean inGroup = false;
    private boolean inLabel = false;
    private boolean inValue = false;
    private boolean inTitle = false;
    private boolean inEmail = false;
    private boolean inAddress = false;
    private boolean inSubject = false;
    private boolean inButton = false;
    private int color1 = Color.parseColor("#4d4948");
    private int color2 = Color.parseColor("#fff58d");
    private int color3 = Color.parseColor("#fff7a2");
    private int color4 = Color.parseColor("#ffffff");
    private int color5 = Color.parseColor("#bbbbbb");
    private StringBuilder sb = null;
    private FormButton button = null;
    private GroupItemDropDown dropDown = null;
    private GroupItemTextArea textArea = null;
    private GroupItemEntryField entryField = null;
    private GroupItemCalendar datePicker = null;
    private GroupItemCheckBox checkBox = null;
    private GroupItemRadioButton radioButton = null;
    private Group group = null;
    private Form form = null;
    private ArrayList<Form> forms = null;

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();

        sb = new StringBuilder();

        forms = new ArrayList<Form>();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        if (localName.equalsIgnoreCase("form")) {
            form = new Form();
            if (!(attributes.getValue("type") == null)) {
                form.setType(attributes.getValue("type"));
            }
        } else if (localName.equalsIgnoreCase("group")) {
            group = new Group();
            inGroup = true;
        } else if (localName.equalsIgnoreCase("radiobutton")) {
            radioButton = new GroupItemRadioButton();
            inRadioButton = true;
        } else if (localName.equalsIgnoreCase("checkbox")) {
            checkBox = new GroupItemCheckBox();
            inCheckBox = true;
        } else if (localName.equalsIgnoreCase("entryfield")) {
            entryField = new GroupItemEntryField();
            String str = attributes.getValue("format");
            if (str != null) {
                entryField.setType(str);
            }
            inEntryField = true;
        } else if (localName.equalsIgnoreCase("textarea")) {
            textArea = new GroupItemTextArea();
            inTextArea = true;
        } else if (localName.equalsIgnoreCase("dropdown")) {
            dropDown = new GroupItemDropDown();
            inDropDown = true;
        } else if (localName.equalsIgnoreCase("item")) {
            GroupItemDropDownItem item = new GroupItemDropDownItem();
        } else if (localName.equalsIgnoreCase("datepicker")) {
            datePicker = new GroupItemCalendar();
            inDatePicker = true;
        } else if (localName.equalsIgnoreCase("email")) {
            inEmail = true;
        } else if (localName.equalsIgnoreCase("address")) {
            inAddress = true;
        } else if (localName.equalsIgnoreCase("subject")) {
            inSubject = true;
        } else if (localName.equalsIgnoreCase("label")) {
            inLabel = true;
        } else if (localName.equalsIgnoreCase("value")) {
            inValue = true;
        } else if (localName.equalsIgnoreCase("title")) {
            inTitle = true;
        } else if (localName.equalsIgnoreCase("button")) {
            button = new FormButton();
            inButton = true;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);

        if (inEntryField && inValue && (entryField != null)) {
            String str = new String(ch, start, length);
            if (!str.equals("\n") && !str.equals(" ")) {
                entryField.setValue(new String(ch, start, length));
            }
        } else if (inEntryField && inLabel && (entryField != null)) {
            String str = new String(ch, start, length);
            if (!str.equals("\n") && !str.equals(" ")) {
                entryField.setLabel(new String(ch, start, length));
            }

        } else if (inTextArea && inValue && (textArea != null)) {
            String str = new String(ch, start, length);
            if (!str.equals("\n") && !str.equals(" ")) {
                textArea.setValue(new String(ch, start, length));
            }
        } else if (inTextArea && inLabel && (textArea != null)) {
            String str = new String(ch, start, length);
            if (!str.equals("\n") && !str.equals(" ")) {
                textArea.setLabel(new String(ch, start, length));
            }

        } else if (inDatePicker && inValue && (datePicker != null)) {
            String str = new String(ch, start, length);
            if (!str.equals("\n") && !str.equals(" ")) {
                datePicker.setValue(new String(ch, start, length));
            }
        } else if (inDatePicker && inLabel && (datePicker != null)) {
            String str = new String(ch, start, length);
            if (!str.equals("\n") && !str.equals(" ")) {
                datePicker.setLabel(new String(ch, start, length));
            }

        } else if (inRadioButton && inValue && (radioButton != null)) {
            String str = new String(ch, start, length);
            if (!str.equals("\n") && !str.equals(" ")) {
                radioButton.setValue(new String(ch, start, length));
            }
        } else if (inRadioButton && inLabel && (radioButton != null)) {
            String str = new String(ch, start, length);
            if (!str.equals("\n") && !str.equals(" ")) {
                radioButton.setLabel(new String(ch, start, length));
            }

        } else if (inCheckBox && inValue && (checkBox != null)) {
            String str = new String(ch, start, length);
            if (!str.equals("\n") && !str.equals(" ")) {
                checkBox.setValue(new String(ch, start, length));
            }
        } else if (inCheckBox && inLabel && (checkBox != null)) {
            String str = new String(ch, start, length);
            if (!str.equals("\n") && !str.equals(" ")) {
                checkBox.setLabel(new String(ch, start, length));
            }

        } else if (inDropDown && inValue && (dropDown != null)) {
            String str = new String(ch, start, length);
            if (!str.equals("\n") && !str.equals(" ")) {
                dropDown.setValue(new String(ch, start, length));

                GroupItemDropDownItem item = new GroupItemDropDownItem();
                item.setValue(new String(ch, start, length).trim());
                dropDown.addItem(item);
            }
        } else if (inDropDown && inLabel && (dropDown != null)) {
            String str = new String(ch, start, length);
            if (!str.equals("\n") && !str.equals(" ")) {
                dropDown.setLabel(new String(ch, start, length));
            }

        } else if (inEmail && inSubject) {
            String subject = new String(ch, start, length);
            if (!subject.equals("\n") && form.getSubject().equals("")) {
                form.setSubject(subject);
            }
        } else if (inEmail && inAddress) {
            String subject = new String(ch, start, length);
            if (!subject.equals("\n") && form.getAddress().equals("")) {
                form.setAddress(subject);
            }
        } else if (inTitle && inGroup) {
            String subject = new String(ch, start, length);
            Log.w(subject, subject);
            if (!subject.equals("\n")) {
                group.setTitle(subject);
            }
        } else if (inEmail && inButton && inLabel && (button != null)) {
            String subject = new String(ch, start, length);
            if (!subject.equals("\n")) {
                button.setLabel(subject);
            }
        }

        sb.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        super.endElement(uri, localName, qName);

        if (localName.equalsIgnoreCase("entryfield") && (entryField != null)) {
            group.addItem(entryField);
            entryField = null;
            inEntryField = false;
        } else if (localName.equalsIgnoreCase("textarea") && (textArea != null)) {
            group.addItem(textArea);
            textArea = null;
            inTextArea = false;
        } else if (localName.equalsIgnoreCase("dropdown") && (dropDown != null)) {
            group.addItem(dropDown);
            dropDown = null;
            inDropDown = false;
        } else if (localName.equalsIgnoreCase("datepicker")
                && (datePicker != null)) {
            group.addItem(datePicker);
            datePicker = null;
            inDatePicker = false;
        } else if (localName.equalsIgnoreCase("radiobutton")
                && (radioButton != null)) {
            group.addItem(radioButton);
            radioButton = null;
            inRadioButton = false;
        } else if (localName.equalsIgnoreCase("checkbox") && (checkBox != null)) {
            group.addItem(checkBox);
            checkBox = null;
            inCheckBox = false;
        }

        if (localName.equalsIgnoreCase("group")) {
            form.addGroup(group);
            group = null;
        } else if (localName.equalsIgnoreCase("form")) {
            if (form.getButtons().isEmpty()) {
                FormButton btn = new FormButton();
                btn.setLabel("Send");
                form.addButton(btn);
            }
            forms.add(form);
            form = null;
        } else if (localName.equalsIgnoreCase("email")) {
            inEmail = false;
        } else if (localName.equalsIgnoreCase("address")) {
            inAddress = false;
        } else if (localName.equalsIgnoreCase("subject")) {
            inSubject = false;
        } else if (localName.equalsIgnoreCase("label")) {
            inLabel = false;
        } else if (localName.equalsIgnoreCase("value")) {
            inValue = false;
        } else if (localName.equalsIgnoreCase("title")) {
            inTitle = false;
        } else if (localName.equalsIgnoreCase("button")) {
            form.addButton(button);
            button = null;
            inButton = false;
        } else if (localName.equalsIgnoreCase("color1")) {
            color1 = Color.parseColor(sb.toString().trim());
        } else if (localName.equalsIgnoreCase("color2")) {
            color2 = Color.parseColor(sb.toString().trim());
        } else if (localName.equalsIgnoreCase("color3")) {
            color3 = Color.parseColor(sb.toString().trim());
        } else if (localName.equalsIgnoreCase("color4")) {
            color4 = Color.parseColor(sb.toString().trim());
        } else if (localName.equalsIgnoreCase("color5")) {
            color5 = Color.parseColor(sb.toString().trim());
        }

        sb.setLength(0);
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    /**
     * Returns the parsed module forms.
     * @return the parsed module forms
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
