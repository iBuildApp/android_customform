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


import java.util.ArrayList;
import java.util.List;

public class GroupItemPhotoPicker extends GroupItem{
    private List<PhotoPickerItem> photos = new ArrayList<>();
    private Integer limit;

    public GroupItemPhotoPicker(){

    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public List<PhotoPickerItem> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoPickerItem> photos) {
        this.photos = photos;
    }
}
