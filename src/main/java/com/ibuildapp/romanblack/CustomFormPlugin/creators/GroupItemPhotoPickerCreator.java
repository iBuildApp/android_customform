package com.ibuildapp.romanblack.CustomFormPlugin.creators;

import android.content.Context;
import android.view.View;

import com.ibuildapp.romanblack.CustomFormPlugin.groups.GroupItemPhotoPicker;
import com.ibuildapp.romanblack.CustomFormPlugin.views.PhotoPickerLayout;


public class GroupItemPhotoPickerCreator {
    public static PhotoPickerLayout create(GroupItemPhotoPicker item, Context context){
        PhotoPickerLayout layout = new PhotoPickerLayout(context);
        layout.setData(item);
        return layout;
    }
}
