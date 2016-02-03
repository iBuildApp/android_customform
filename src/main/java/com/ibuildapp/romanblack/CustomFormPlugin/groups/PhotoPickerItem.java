/****************************************************************************
 * *
 * Copyright (C) 2014-2015 iBuildApp, Inc. ( http://ibuildapp.com )         *
 * *
 * This file is part of iBuildApp.                                          *
 * *
 * This Source Code Form is subject to the terms of the iBuildApp License.  *
 * You can obtain one at http://ibuildapp.com/license/                      *
 * *
 ****************************************************************************/
package com.ibuildapp.romanblack.CustomFormPlugin.groups;

import android.graphics.Bitmap;

public class PhotoPickerItem {
    private Bitmap source;
    private Bitmap thumbnail;
    private String imageSource;

    public PhotoPickerItem() {
    }

    public PhotoPickerItem(Bitmap source, Bitmap thumbnail) {
        this.source = source;
        this.thumbnail = thumbnail;
    }

    public Bitmap getSource() {
        return source;
    }

    public void setSource(Bitmap source) {
        this.source = source;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getImageSource() {
        return imageSource;
    }

    public void setImageSource(String imageSource) {
        this.imageSource = imageSource;
    }
}
