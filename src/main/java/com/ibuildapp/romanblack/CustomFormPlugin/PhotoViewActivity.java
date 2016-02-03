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
package com.ibuildapp.romanblack.CustomFormPlugin;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;

import com.appbuilder.sdk.android.AppBuilderModuleMain;
import com.ibuildapp.romanblack.CustomFormPlugin.utils.ImageUtils;

public class PhotoViewActivity extends AppBuilderModuleMain {
    private Dialog progress;
    private ImageView imageDetails;
    private String bitmapSource;
    private int displayWidth;
    private int displayHeight;

    @Override
    public void create() {
        content();
        UI();

    }

    private void UI() {
        setContentView(R.layout.custom_form_photo_view_layout);
        imageDetails = (ImageView) findViewById(R.id.custom_form_photo_view_detail);

        setTopBarLeftButtonText(getResources().getString(R.string.common_back_upper), true, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        setTopBarTitle(" ");
        progressShow();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = ImageUtils.decodeSampledBitmapFromFile(bitmapSource, displayWidth/2, displayHeight/2);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        imageDetails.setImageBitmap(bitmap);
                        progressHide();
                    }
                });

            }
        }).start();
    }

    private void content() {
        Intent intent = getIntent();
        bitmapSource = intent.getStringExtra(ImageUtils.BITMAP_EXTRA);

        DisplayMetrics displaymetrics = new DisplayMetrics();
       getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        displayWidth = displaymetrics.widthPixels;
        displayHeight = displaymetrics.heightPixels;
    }

    @Override
    public void destroy() {
        progressHide();
        progress = null;
    }

    protected void progressShow() {
        progress = ProgressDialog.show(this, null, "", true);
        progress.setCancelable(true);
        progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                onBackPressed();
            }
        });
        if(progress.isShowing())
            return;

        progress.show();
    }

    protected void progressHide() {
        if(progress != null)
            progress.dismiss();
    }
}
