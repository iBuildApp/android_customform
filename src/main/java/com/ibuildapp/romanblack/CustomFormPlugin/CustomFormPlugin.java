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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.*;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.appbuilder.sdk.android.AppBuilderModuleMain;
import com.appbuilder.sdk.android.DialogSharing;
import com.appbuilder.sdk.android.StartUpActivity;
import com.appbuilder.sdk.android.Utils;
import com.appbuilder.sdk.android.Widget;
import com.ibuildapp.romanblack.CustomFormPlugin.creators.GroupItemPhotoPickerCreator;
import com.ibuildapp.romanblack.CustomFormPlugin.groups.Group;
import com.ibuildapp.romanblack.CustomFormPlugin.groups.GroupItem;
import com.ibuildapp.romanblack.CustomFormPlugin.groups.GroupItemCalendar;
import com.ibuildapp.romanblack.CustomFormPlugin.groups.GroupItemCheckBox;
import com.ibuildapp.romanblack.CustomFormPlugin.groups.GroupItemDropDown;
import com.ibuildapp.romanblack.CustomFormPlugin.groups.GroupItemDropDownItem;
import com.ibuildapp.romanblack.CustomFormPlugin.groups.GroupItemEntryField;
import com.ibuildapp.romanblack.CustomFormPlugin.groups.GroupItemPhotoPicker;
import com.ibuildapp.romanblack.CustomFormPlugin.groups.GroupItemRadioButton;
import com.ibuildapp.romanblack.CustomFormPlugin.groups.GroupItemTextArea;
import com.ibuildapp.romanblack.CustomFormPlugin.groups.PhotoPickerItem;
import com.ibuildapp.romanblack.CustomFormPlugin.utils.ImageUtils;
import com.ibuildapp.romanblack.CustomFormPlugin.utils.Statics;
import com.ibuildapp.romanblack.CustomFormPlugin.views.PhotoPickerLayout;
import com.ibuildapp.romanblack.CustomFormPlugin.xmlparser.EntityParser;
import com.ibuildapp.romanblack.CustomFormPlugin.xmlparser.Form;
import com.ibuildapp.romanblack.CustomFormPlugin.xmlparser.FormButton;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Main module class. Module entry point.
 * Represents custom form widget.
 */
@StartUpActivity(moduleName = "CustomForm")
public class CustomFormPlugin extends AppBuilderModuleMain {
    protected static final int CAMERA_REQUEST = 0;
    protected static final int GALLERY_PICTURE = 1;

    protected static int STARTED_LAYOUT_ID = 1;
    private static String TEMP_FILE_NAME = "";
    private final int INITIALIZATION_FAILED = 0;
    private final int NEED_INTERNET_CONNECTION = 1;
    private final int LOADING_ABORTED = 2;
    private final int SEND_FAILED = 3;
    private final int SHOW_FORM = 4;
    private boolean useCache = false;
    private boolean isOnline = false;
    private String cacheMD5 = null;
    private String cachePath = null;
    private String widgetMD5 = null;
    private int lrPadding = 17;
    private String title = "";
    private Widget widget = null;
    private ProgressDialog progressDialog = null;
    private ArrayList<Form> forms = new ArrayList<Form>();
    private List<PhotoPickerLayout> layouts = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INITIALIZATION_FAILED: {
                    Toast.makeText(CustomFormPlugin.this,
                            R.string.alert_cannot_init,
                            Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            finish();
                        }
                    }, 5000);
                }
                break;
                case NEED_INTERNET_CONNECTION: {
                    Toast.makeText(CustomFormPlugin.this, R.string.alert_no_internet,
                            Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            finish();
                        }
                    }, 5000);
                }
                break;
                case LOADING_ABORTED: {
                    CustomFormPlugin.this.closeActivity();
                }
                break;
                case SEND_FAILED: {
                    Toast.makeText(CustomFormPlugin.this, R.string.alert_no_internet,
                            Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                        }
                    }, 5000);
                }
                break;
                case SHOW_FORM: {
                    buildForm();
                }
                break;
            }
        }
    };

    @SuppressLint("ValidFragment")
    public class DialogDatePicker  implements DatePickerDialog.OnDateSetListener{

        private int year;
        private int month;
        private int day;
        private Button edit;
        private GroupItemCalendar ef;

        public DialogDatePicker(int startDay, int startMonth, int startYear, Button button, GroupItemCalendar ef)
        {
            year = startYear;
            day = startDay;
            month = startMonth;
            edit = button;
            this.ef = ef;
        }
        public Date getDate()
        {
            return new Date();
        }
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            // Do something with the date chosen by the user
            try {
                this.year = year;
                month = monthOfYear;
                day = dayOfMonth;
                edit.setTextColor(Color.parseColor("#E6000000"));
                Date date = new Date(year-1900, monthOfYear, day);
                String datePattern = getResources().getString(R.string.data_picker_pattern);
                DateFormat DATE_FORMAT = new SimpleDateFormat(datePattern);//DateFormat.getDateInstance(, Locale.getDefault());
                String s = DATE_FORMAT.format(date);
                ef.setSet(true);
                ef.setDate(date);
                edit.setText(s);
                edit.setTextSize(18);
            }
            catch(Throwable e)
            {
                e.printStackTrace();
            }


        }
    }


    @Override
    public void create() {
        try { //ErrorLogging

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.romanblack_custom_form_main);
            setTitle(R.string.roamnblack_custom_form);

            Intent currentIntent = getIntent();
            Bundle store = currentIntent.getExtras();
            widget = (Widget) store.getSerializable("Widget");
            if (widget == null) {
                handler.sendEmptyMessageDelayed(INITIALIZATION_FAILED, 100);
                return;
            }

            //dnevolin changes start

            String pluginXmlDataFilePath = store.getString("WidgetFile");


            if ( !TextUtils.isEmpty(pluginXmlDataFilePath) ) {
                StringBuffer fileData = null;
                try {
                    fileData = new StringBuffer();
                    BufferedReader reader = new BufferedReader(new FileReader(pluginXmlDataFilePath));
                    char[] buf = new char[1024];
                    int numRead;
                    while((numRead=reader.read(buf)) != -1){
                        String readData = String.valueOf(buf, 0, numRead);
                        fileData.append(readData);
                    }
                    reader.close();
                } catch(IOException ioe) {
                    handler.sendEmptyMessageDelayed(INITIALIZATION_FAILED, 100);
                    return;
                }

                widget.setNormalPluginXmlData(fileData.toString());
            }

            //dnevolin changes end

            widgetMD5 = Utils.md5(widget.getPluginXmlData());

            if (widget.getTitle() != null && widget.getTitle().length() != 0) {
                setTopBarTitle(widget.getTitle());
            } else {
                setTopBarTitle(getResources().getString(R.string.roamnblack_custom_form));
            }

            if (widget.getPluginXmlData().length() == 0) {
                handler.sendEmptyMessageDelayed(INITIALIZATION_FAILED, 100);
                return;
            }
            // topbar initialization
            setTopBarLeftButtonText(getResources().getString(R.string.common_home_upper), true, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            cachePath = widget.getCachePath() + "/customform-" + widget.getOrder();
            File cache = new File(this.cachePath);
            if (!cache.exists()) {
                cache.mkdirs();
            }

            File cacheData = new File(cachePath + "/cache.data");

            if (cacheData.exists() && cacheData.length() > 0) {
                cacheMD5 = readFileToString(cachePath + "/cache.md5")
                        .replace("\n", "");
                if (cacheMD5.equals(widgetMD5)) {
                    useCache = true;
                } else {
                    File[] files = cache.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        files[i].delete();
                    }
                    try {
                        BufferedWriter bw = new BufferedWriter(
                                new FileWriter(new File(cachePath + "/cache.md5")));
                        bw.write(widgetMD5);
                        bw.close();
                        Log.d("IMAGES PLUGIN CACHE MD5", "SUCCESS");
                    } catch (Exception e) {
                        Log.w("IMAGES PLUGIN CACHE MD5", e);
                    }
                }
            }

            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni != null && ni.isConnectedOrConnecting()) {
                isOnline = true;
            }

            if (!isOnline && !useCache) {
                handler.sendEmptyMessage(NEED_INTERNET_CONNECTION);
                return;
            }

            progressDialog = ProgressDialog.show(this, null, getString(R.string.common_loading_upper), true);
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(
                    new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface arg0) {
                            handler.sendEmptyMessage(LOADING_ABORTED);
                        }
                    });

            new Thread() {
                @Override
                public void run() {
                    try {//ErrorLogging

                        EntityParser parser = new EntityParser(widget.getPluginXmlData());
                        parser.parse();

                        title = widget.getTitle();
                        forms = parser.getForms();

                        Statics.color1 = parser.getColor1();
                        Statics.color2 = parser.getColor2();
                        Statics.color3 = parser.getColor3();
                        Statics.color4 = parser.getColor4();
                        Statics.color5 = parser.getColor5();

                        handler.sendEmptyMessage(SHOW_FORM);

                    } catch (Exception e) {
                    }
                }
            }.start();


        } catch (Exception e) {
        }
    }

    /**
     * Builds custom form depending on module configuration.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void buildForm() {
        try {//ErrorLogging

            View root = findViewById(R.id.romanblack_custom_form_main_root);
          /*  if (isChemeDark(Statics.color1))
                root.setBackgroundColor(Color.BLACK);
            else*/
            root.setBackgroundColor(Statics.color1); //Color.WHITE);

            if (!forms.isEmpty()) {
                LinearLayout container =
                        (LinearLayout) findViewById(R.id.romanblack_custom_form_container);
                container.setBackgroundColor(Statics.color1);
                for (int j = 0; j < forms.get(0).getGroups().size(); j++) {
                    Group group = forms.get(0).getGroups().get(j);
                    final LinearLayout groupLL = new LinearLayout(this);
                    groupLL.setOrientation(LinearLayout.VERTICAL);
                    groupLL.setBackgroundColor(Statics.color1);
                    DisplayMetrics metrix = getResources().getDisplayMetrics();

                    if (!group.getTitle().equals("")) {
                        TextView textView = new TextView(this);

                        SpannableStringBuilder ssb =
                                new SpannableStringBuilder(group.getTitle());
                        StyleSpan ss = new StyleSpan(Typeface.NORMAL);

                        ssb.setSpan(ss, 0, ssb.length(),
                                Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

                        textView.setText(ssb);
                        textView.setTextColor(Statics.color5);
                        textView.setTextSize(20);

                        if (forms.get(0).getGroups().indexOf(group) == 0) {
                            textView.setPadding(0, (int)(15*metrix.density), 0, (int) (15*metrix.density));
                        } else {
                            textView.setPadding(0, (int)(15*metrix.density), 0, (int)(15*metrix.density));
                        }

                        textView.setPadding((int) (17 * getResources().getDisplayMetrics().density), (int) (15 * metrix.density), (int) (17 * getResources().getDisplayMetrics().density), (int)(15*metrix.density));

                        container.addView(textView);
                    }

                    for (int i = 0; i < group.getItems().size(); i++) {
                        Class itemClass = group.getItems().get(i).getClass();
                        if (itemClass.equals(GroupItemCalendar.class)) {
                            final GroupItemCalendar ef =
                                    (GroupItemCalendar) group.getItems().get(i);

                            LinearLayout ll = new LinearLayout(this);
                            ll.setOrientation(LinearLayout.VERTICAL);

                            TextView label = new TextView(this);
//                            label.setTextColor(Statics.color5);
                            label.setText(ef.getLabel());
                            label.setTextSize(16);
                            label.setPadding(0, 0, 0, (int) (10 * metrix.density));

                            label.setLayoutParams(new LayoutParams(
                                    LayoutParams.WRAP_CONTENT,
                                    LayoutParams.WRAP_CONTENT));

                            label.setTextColor(Statics.color3);

                            final Button value = new Button(this);
                            value.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                                    Float.valueOf(55 * metrix.density).intValue()));
                            value.setTextSize(18);
                            value.setGravity(Gravity.LEFT| Gravity.CENTER_VERTICAL);
                            value.setTextColor(Color.parseColor("#E6000000"));//Statics.color1);
                            value.setBackgroundResource(R.drawable.edittext_back);
                            //value.setBackgroundColor(Statics.color5);
                            Drawable img = getResources().getDrawable( R.drawable.arrow2x);// arrowbest );
                            //Drawable draw = prefetchDrawable(img);
                            if (metrix.densityDpi == DisplayMetrics.DENSITY_XHIGH) {
                                Bitmap bitmap = ((BitmapDrawable) img).getBitmap();

                                bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.5), (int) (bitmap.getHeight() * 1.5), false);
                                img = new BitmapDrawable(bitmap);
                            }
                            value.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                                    Float.valueOf(55 * metrix.density).intValue());
                            params.setMargins(0,0,0,(int)(18 * metrix.density));

                            value.setLayoutParams(params);

                            //value.getBackground().setColorFilter(Statics.color5, Mode.DST_OVER);
                            Calendar c = Calendar.getInstance();
                            DialogDatePicker picker = new DialogDatePicker(c.get(Calendar.YEAR), c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH), value, ef);
                            final DatePickerDialog dialog = new DatePickerDialog(CustomFormPlugin.this, picker,  c.get(Calendar.YEAR), c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));

                            Date date = ef.getDate();
                            if (ef.isSet()) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(date);

                                String datePattern = getResources().getString(R.string.data_picker_pattern);
                                DateFormat DATE_FORMAT = new SimpleDateFormat(datePattern);
                                String s = DATE_FORMAT.format(date);
                                value.setText(s);
                                dialog.updateDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                            }
                            else
                            {
                                String hint =getResources().getString(R.string.data_picker_hint);
                                value.setTextColor(Color.parseColor("#8c8c8c"));//"#99000000"));//Color.argb(125, Color.red(Statics.color1), Color.green(Statics.color1), Color.blue(Statics.color1)));
                                value.setText(hint);
                            }


                            value.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View view) {

                                    dialog.show();
                                }
                            });

                            ll.addView(label);
                            ll.addView(value);
                            ll.setBackgroundColor(Statics.color1);

                            groupLL.addView(ll);
                        } else if (itemClass.equals(GroupItemCheckBox.class)) {
                            final GroupItemCheckBox ef =
                                    (GroupItemCheckBox) group.getItems().get(i);

                            LinearLayout ll = new LinearLayout(this);
                            ll.setPadding(0,0,0,0);

                            TextView label = new TextView(this);
                            label.setTextColor(Color.BLACK);
                            label.setText(ef.getLabel());
                            label.setTextSize(16);
                            label.setPadding((int) (10 * metrix.density), 0, 0, (int) (10 * metrix.density));
                            label.setMaxLines(3);
                            label.setTextColor(Statics.color4);
                            label.setGravity(Gravity.LEFT);
                            LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                                    LayoutParams.WRAP_CONTENT,
                                    LayoutParams.WRAP_CONTENT);
                            labelParams.setMargins(0,0,0, (int)(18 * metrix.density));
                            label.setLayoutParams(labelParams);

                            final CheckBox value = createCheckBox(this, ef);
                            label.setOnClickListener(new OnClickListener() {
                                public void onClick(View arg0) {
                                    value.performClick();
                                }
                            });


                            ll.addView(value);
                            ll.addView(label);
                            ll.setBackgroundColor(Statics.color1);
                            groupLL.addView(ll);
                        }else if (itemClass.equals(GroupItemPhotoPicker.class)){
                            final GroupItemPhotoPicker ef =
                                    (GroupItemPhotoPicker) group.getItems().get(i);
                            final PhotoPickerLayout photoPickerLayout = GroupItemPhotoPickerCreator.create(ef, this);
                            layouts.add(photoPickerLayout);
                            photoPickerLayout.getButton().setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick( final View v) {
                                    if (!photoPickerLayout.canAddPhoto()){
                                        Toast.makeText(CustomFormPlugin.this,
                                                R.string.custom_form_cant_add_photo,
                                                Toast.LENGTH_LONG).show();
                                                return;
                                    }
                                    final DialogSharing.Configuration.Builder sharingDialogBuilder = new DialogSharing.Configuration.Builder();
                                    sharingDialogBuilder.addCustomListener(R.string.custom_form_camera, R.drawable.customform_camera, true, new DialogSharing.Item.OnClickListener() {
                                        @Override
                                        public void onClick() {
                                            STARTED_LAYOUT_ID = photoPickerLayout.getUniqueId();

                                            Intent intent;
                                            if (Build.VERSION.SDK_INT >= 21)
                                               intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                            else
                                                 intent = new Intent(CustomFormPlugin.this, CustomFormCameraActivity.class);

                                            TEMP_FILE_NAME = "temp" + Long.valueOf(System.currentTimeMillis()) + ".jpg";
                                            File f = new File(android.os.Environment
                                                    .getExternalStorageDirectory(), TEMP_FILE_NAME);
                                            intent.putExtra("output",
                                                    Uri.fromFile(f));

                                            startActivityForResult(intent,
                                                    CAMERA_REQUEST);
                                        }
                                    });

                                    sharingDialogBuilder.addCustomListener(R.string.custom_form_gallery, R.drawable.customform_gallery, true, new DialogSharing.Item.OnClickListener() {
                                        @Override
                                        public void onClick() {
                                            STARTED_LAYOUT_ID = photoPickerLayout.getUniqueId();

                                            Intent pictureActionIntent = new Intent(
                                                    Intent.ACTION_PICK,
                                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                                    //pictureActionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                            startActivityForResult(
                                                    pictureActionIntent,
                                                    GALLERY_PICTURE);
                                        }
                                    });
                                    showDialogSharing(sharingDialogBuilder.build());
                                }
                            });
                            groupLL.addView(photoPickerLayout);

                        } else if (itemClass.equals(GroupItemDropDown.class)) {
                            final GroupItemDropDown ef =
                                    (GroupItemDropDown) group.getItems().get(i);

                            LinearLayout ll = new LinearLayout(this);
                            ll.setOrientation(LinearLayout.VERTICAL);

                            TextView label = new TextView(this);
                            label.setText(ef.getLabel());
                            label.setTextSize(16);
                            label.setPadding(0, 0, 0, (int) (10 * metrix.density));
                            label.setTextColor(Statics.color3);

                            label.setLayoutParams(new LayoutParams(
                                    LayoutParams.WRAP_CONTENT,
                                    LayoutParams.WRAP_CONTENT));


                            final Button value = new Button (this);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, Float.valueOf(55 * metrix.density).intValue());
                            params.setMargins(0, 0, 0, (int) (18 * metrix.density));
                            value.setLayoutParams(params);
                            value.setTextSize(22);
                            value.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                            value.setTextColor(Color.parseColor("#E6000000"));//Statics.color1);
                            value.setHeight((int) (44 * metrix.density));
                            value.setEllipsize(TextUtils.TruncateAt.END);
                            value.setSingleLine();
                            value.setBackgroundResource(R.drawable.edittext_back);//Color(Statics.color5);

                            Drawable img = getResources().getDrawable(R.drawable.arrow2x);
                            // Drawable draw = prefetchDrawable(img);
                            if (metrix.densityDpi == DisplayMetrics.DENSITY_XHIGH ) {
                                Bitmap bitmap = ((BitmapDrawable) img).getBitmap();

                                bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth()*1.5), (int)(bitmap.getHeight()*1.5), false);
                                img = new BitmapDrawable(bitmap);
                            }
                            value.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null);
                            final ArrayList<String> dropItems = new ArrayList<String>();

                            for (Iterator<GroupItemDropDownItem> it1 = ef.getItems().iterator(); it1.hasNext();) {
                                dropItems.add(it1.next().getValue());
                            }
                            value.setText(dropItems.get(0));
                            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                                    android.R.layout.simple_spinner_item,
                                    dropItems);

                            value.setOnClickListener(new OnClickListener() {
                                @SuppressLint("NewApi")
                                @Override
                                public void onClick(View view) {
                                    try {
                                        CharSequence[] list = dropItems.toArray(new CharSequence[dropItems.size()]);

                                        AlertDialog.Builder builder = new AlertDialog.Builder(CustomFormPlugin.this);
                                        builder.setItems(list, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int item) {
                                                value.setText(dropItems.get(item));
                                                ef.setSelectedIndex(item);
                                            }
                                        });
                                        AlertDialog alert = builder.create();
                                        alert.show();


                                    }catch(Throwable e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            value.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                            ll.addView(label);
                            ll.addView(value);
                            ll.setPadding(0, 0, 0, 28);
                            ll.setBackgroundColor(Statics.color1);
                            groupLL.addView(ll);
                        } else if (itemClass.equals(GroupItemEntryField.class)) { //едиттекст с подписью
                            final GroupItemEntryField ef =
                                    (GroupItemEntryField) group.getItems().get(i);

                            LinearLayout ll = new LinearLayout(this);
                            ll.setOrientation(LinearLayout.VERTICAL);

                            TextView label = new TextView(this);
                            label.setTextColor(Statics.color3);
                            label.setText(ef.getLabel());
                            label.setTextSize(16);
                            label.setPadding(0, 0, 0, (int) (10 * metrix.density));

                            label.setLayoutParams(new LayoutParams(
                                    LayoutParams.WRAP_CONTENT,
                                    LayoutParams.WRAP_CONTENT));

                            EditText value = new EditText(this);
                            value.setHint(ef.getValue());
                            ef.setValue("");
                            value.setHintTextColor(Color.parseColor("#8c8c8c"));//value.setHintTextColor(Color.argb(125, Color.red(Statics.color1), Color.green(Statics.color1), Color.blue(Statics.color1)));

                            value.setTextColor(Color.parseColor("#E6000000"));//Statics.color1); //надо
                            value.setMaxLines(3);

                            value.setVerticalScrollBarEnabled(true);
                            value.setTextSize(18);
                            //value.setBackgroundColor(Statics.color5);
                            if (ef.getType().equalsIgnoreCase("number")) {
                                value.setInputType(InputType.TYPE_CLASS_NUMBER
                                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
                                        | InputType.TYPE_NUMBER_FLAG_SIGNED);
                            }
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                                    Float.valueOf(55 * metrix.density).intValue());
                            params.setMargins(0,0,0,(int)(10* metrix.density));

                            value.setLayoutParams(params);
                            value.addTextChangedListener(new TextWatcher() {
                                public void beforeTextChanged(CharSequence arg0,
                                                              int arg1, int arg2, int arg3) {
                                }

                                public void onTextChanged(CharSequence arg0,
                                                          int arg1, int arg2, int arg3) {
                                }

                                public void afterTextChanged(Editable arg0) {
                                    ef.setValue(arg0.toString());
                                }
                            });
                            value.setBackgroundResource(R.drawable.edittext_back);

                            ll.addView(label);
                            ll.addView(value);
                            // ll.setBackgroundColor(Statics.color1);
                            groupLL.addView(ll);
                        } else if (itemClass.equals(GroupItemRadioButton.class)) {
                            final GroupItemRadioButton ef =
                                    (GroupItemRadioButton) group.getItems().get(i);

                            LinearLayout ll = new LinearLayout(this);

                            TextView label = new TextView(this);
                            label.setText(ef.getLabel());
                            label.setTextSize(16);
                            label.setPadding((int) (10 * metrix.density), 0, 0, (int) (10 * metrix.density));
                            label.setTextColor(Statics.color4);
                            label.setMaxLines(3);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LayoutParams.WRAP_CONTENT,
                                    LayoutParams.WRAP_CONTENT);
                            params.setMargins(0,0,0, (int) (18* metrix.density));
                            label.setLayoutParams(params);

                            final RadioButton value = createRadioButton(this, ef, groupLL);

                            label.setOnClickListener(new OnClickListener() {
                                public void onClick(View arg0) {
                                    value.performClick();
                                }
                            });

                            ll.addView(value);
                            ll.addView(label);
                            ll.setBackgroundColor(Statics.color1);
                            groupLL.addView(ll);
                            value.setChecked(ef.isSelected());
                        } else if (itemClass.equals(GroupItemTextArea.class)) { //список с заголовком
                            final GroupItemTextArea ef =
                                    (GroupItemTextArea) group.getItems().get(i);

                            LinearLayout ll = new LinearLayout(this);
                            ll.setOrientation(LinearLayout.VERTICAL);

                            TextView label = new TextView(this);
                            label.setText(ef.getLabel());
                            label.setTextSize(17);
                            label.setPadding(0, 0, 0, (int) (10 * metrix.density));
                            label.setTextColor(Statics.color3);
                            label.setBackgroundColor(Color.TRANSPARENT);

                            label.setLayoutParams(new LayoutParams(
                                    LayoutParams.WRAP_CONTENT,
                                    LayoutParams.WRAP_CONTENT));

                            final ScrollView parentView = (ScrollView) findViewById(R.id.scroll);
                            parentView.setOnTouchListener(new View.OnTouchListener() {

                                public boolean onTouch(View v, MotionEvent event) {
                                    v.onTouchEvent(event);
                                    return false;
                                }
                            });

                            final EditText value = new EditText(this);
                            value.setGravity(Gravity.LEFT | Gravity.TOP);
                            value.setLines(4);
                            value.setHint(ef.getValue());
                            ef.setValue("");
                            value.setHintTextColor(Color.parseColor("#8c8c8c"));//value.setHintTextColor(Color.argb(125, Color.red(Statics.color1), Color.green(Statics.color1), Color.blue(Statics.color1)));
                            value.setTextColor(Color.parseColor("#E6000000"));//Statics.color1);
                            value.setTextSize(18);
                            value.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    ViewParent parent = value.getParent();
                                    parent.requestDisallowInterceptTouchEvent(true);
                                    return false;
                                }
                            });
                            //value.setBackgroundColor(Statics.color5);
                            value.setBackgroundResource(R.drawable.edittext_back);
                            LinearLayout.LayoutParams para = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                                    Float.valueOf(55 * metrix.density).intValue());
                            value.setLayoutParams(para);
                            value.addTextChangedListener(new TextWatcher() {
                                public void beforeTextChanged(CharSequence arg0,
                                                              int arg1, int arg2, int arg3) {
                                }

                                public void onTextChanged(CharSequence arg0,
                                                          int arg1, int arg2, int arg3) {
                                }

                                public void afterTextChanged(Editable arg0) {
                                    String res = arg0.toString().replace("\n", " ");
                                    ef.setValue(res);
                                }
                            });
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,
                                    LayoutParams.WRAP_CONTENT);
                            params.setMargins(0, 0, 0, (int) (18 * metrix.density));
                            value.setLayoutParams(params);
                            ll.addView(label);
                            ll.addView(value);
                            ll.setBackgroundColor(Statics.color1);
                            groupLL.addView(ll);
                        }
                    }
                    //сюда
                    View view = new View(this);
                    view.setBackgroundColor(Color.parseColor("#33000000"));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                            LayoutParams.WRAP_CONTENT);
                    //params.height = 10000;
                    params.width = 10000;
                    params.setMargins(0, 0, 0, 0);
                    view.setMinimumHeight((int)( 1*getResources().getDisplayMetrics().density));
                    view.setLayoutParams(params);
                    view.setPadding(0,0,0,28);
                    view.setMinimumWidth(10000);
//                    groupLL.addView(view);

                    groupLL.setPadding((int)(15*getResources().getDisplayMetrics().density), 0, (int)(15*getResources().getDisplayMetrics().density), 0);

                    container.addView(groupLL);
                    container.addView(view);
                }

                LinearLayout buttonsLayout = new LinearLayout(this);
                buttonsLayout.setGravity(Gravity.CENTER);

                for (Iterator<FormButton> it2 =
                     forms.get(0).getButtons().iterator(); it2.hasNext();) {
                    final FormButton fBtn = it2.next();

                    SpannableStringBuilder ssb = new SpannableStringBuilder();
                    ssb.append(fBtn.getLabel());
                    ssb.setSpan(new StyleSpan(Typeface.NORMAL), 0, ssb.length(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    Button sendBtn = new Button(this);
                    if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        sendBtn.setAllCaps(false);
                    sendBtn.setText(fBtn.getLabel());
                    float borderSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
                    sendBtn.setTextSize(22);
//                    sendBtn.setTextColor(Statics.color5);
                    sendBtn.setTextColor(Statics.color1);

                    sendBtn.setMinimumWidth((int)(160* getResources().getDisplayMetrics().density));
                    GradientDrawable drawable = new GradientDrawable();
                    drawable.setShape(GradientDrawable.RECTANGLE);
                    drawable.setStroke((int) borderSize, Statics.color5);
//                    drawable.setColor(Color.TRANSPARENT);
                    drawable.setColor(Statics.color5);

                    sendBtn.setBackgroundDrawable(drawable);
//                    LinearLayout.LayoutParams para =  new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//                    para.setMargins(0,(int)(20*getResources().getDisplayMetrics().density),0,(int)(15*getResources().getDisplayMetrics().density));
                    LinearLayout.LayoutParams para =  new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, Float.valueOf(55 * getResources().getDisplayMetrics().density).intValue());
                    para.setMargins(0, (int) (20 * getResources().getDisplayMetrics().density), 0, (int) (15 * getResources().getDisplayMetrics().density));
                    sendBtn.setLayoutParams(para);

                    sendBtn.setOnClickListener(new OnClickListener() {
                        public void onClick(View arg0) {
                            Intent emailIntent = chooseEmailClient();
                            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                                    Html.fromHtml(prepareText(forms.get(0))));
                            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                                    forms.get(0).getSubject());
                            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                                    new String[]{forms.get(0).getAddress()});

                            Form form = forms.get(0);
                            ArrayList<Uri> uris = new ArrayList<Uri>();

                            for (Iterator<Group> it = form.getGroups().iterator(); it.hasNext();) {
                                Group group = it.next();
                                for (Iterator<GroupItem> it1 = group.getItems().iterator();
                                     it1.hasNext();) {

                                    GroupItem item = it1.next();

                                     if (item instanceof GroupItemPhotoPicker){
                                         GroupItemPhotoPicker pick = (GroupItemPhotoPicker) item;
                                         for (PhotoPickerItem ppIt:pick.getPhotos())
                                             uris.add( Uri.fromFile(new File(ppIt.getImageSource())));
                                     }
                                }
                            }

                            emailIntent.putExtra(Intent.EXTRA_STREAM, uris);
                            startActivity(emailIntent);
                        }
                    });
                    sendBtn.setTypeface(null, Typeface.NORMAL);

                    buttonsLayout.addView(sendBtn);
                    buttonsLayout.setPadding((int) (17 * getResources().getDisplayMetrics().density), 0, (int) (17 * getResources().getDisplayMetrics().density), 0);
                }

                container.addView(buttonsLayout);

            }

            if (progressDialog != null) {
                progressDialog.dismiss();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Drawable prefetchDrawable(Drawable img) {
        Bitmap bitmap = ((BitmapDrawable) img).getBitmap();
        Bitmap.Config config = bitmap.getConfig();
        bitmap = bitmap.copy(config==null?Bitmap.Config.ARGB_8888:config, true);

        try {
            for (int height = 0; height < bitmap.getHeight(); height++)
                for (int width = 0; width < bitmap.getWidth(); width++) {
                    int currentColor = bitmap.getPixel(width ,height);
                    if (currentColor == Color.WHITE || currentColor == Color.TRANSPARENT)
                        continue;
                    else
                        bitmap.setPixel(width, height,  Statics.color5);
                }
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
        return new BitmapDrawable(bitmap);
    }

    private RadioButton createRadioButton(Context customFormPlugin, final GroupItemRadioButton ef, final LinearLayout groupLL) {
        final int height = 34;
        final int width = 34;

        final RadioButton value = new RadioButton(customFormPlugin);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        params.setMargins(0,0,0,(int)(15*getResources().getDisplayMetrics().density));

        value.setLayoutParams(params);

        final GradientDrawable buttonDrawable = new GradientDrawable();
        buttonDrawable.setColor(Color.TRANSPARENT);
        value.setButtonDrawable(buttonDrawable);

        Drawable uncheckedDraw = getResources().getDrawable(R.drawable.rb_off);
        Bitmap uncheckedBitmap =((BitmapDrawable)uncheckedDraw).getBitmap();
        //начало

        //Bitmap finalUncheckedBitmap =  Bitmap.createScaledBitmap(uncheckedBitmap, width, height, false);
        uncheckedBitmap = applyingColorFilter ( uncheckedBitmap);
        //finalUncheckedBitmap = addExternalSize(finalUncheckedBitmap);

        // finalUncheckedBitmap = getBitmapClippedCircle(finalUncheckedBitmap);
        //конец
        Drawable checkedDraw = getResources().getDrawable(R.drawable.rb_on);
        Bitmap checkedBitmap =((BitmapDrawable)checkedDraw).getBitmap();
        checkedBitmap = applyingColorFilter ( checkedBitmap);

       /* Bitmap finalCheckedBitmap = Bitmap.createScaledBitmap(checkedBitmap, width, height, false);
        finalCheckedBitmap = addExternalSize(finalCheckedBitmap);
        finalCheckedBitmap = getBitmapClippedCircle(finalCheckedBitmap);*/

        //final BitmapDrawable finalChecked = new BitmapDrawable(checkedBitmap);

        //final BitmapDrawable finalUnChecked = new BitmapDrawable(uncheckedBitmap);

        if (value.isChecked())
            value.setBackgroundResource(R.drawable.radiobtn_on2x);
        else
            value.setBackgroundResource(R.drawable.radiobtn_off);
        //value.setBackgroundDrawable(finalUnChecked);

        value.setWidth((int) (width * getResources().getDisplayMetrics().density));
        value.setHeight((int)(height*getResources().getDisplayMetrics().density ));
        try {
            final Drawable checked = getResources().getDrawable(R.drawable.radiobtn_on2x);
        }catch (Throwable ex)
        {
            ex.printStackTrace();
        }
        value.setOnCheckedChangeListener(
                new OnCheckedChangeListener() {
                    public void onCheckedChanged(
                            CompoundButton arg0,
                            boolean arg1) {
                        ef.setSelected(arg1);
                        if (arg1) {
                            value.setBackgroundResource(R.drawable.radiobtn_on2x);
                            //value.setBackgroundDrawable(getResources().getDrawable(R.drawable.radiobtn_on));
                            //  value.setButtonDrawable(checked);
                            value.setWidth((int) (width * getResources().getDisplayMetrics().density));
                            value.setHeight((int)(height*getResources().getDisplayMetrics().density ));
                            for (int viewNumb = 0; viewNumb < groupLL.getChildCount(); viewNumb++) {
                                try {
                                    LinearLayout childLL = (LinearLayout) groupLL.getChildAt(viewNumb);
                                    RadioButton changingCheckBox = (RadioButton) childLL.getChildAt(0);
                                    if (changingCheckBox.isChecked()) {
                                        if (value != changingCheckBox) {
                                            changingCheckBox.setChecked(false);
                                            changingCheckBox.setBackgroundResource(R.drawable.radiobtn_off);

                                            value.setWidth((int) (width * getResources().getDisplayMetrics().density));
                                            value.setHeight((int)(height*getResources().getDisplayMetrics().density ));
                                        }
                                    }
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                });
        value.setChecked(false);
        return value;
    }

    private Bitmap applyingColorFilter(Bitmap uncheckedBitmap) {
        Bitmap mutableBitmap = null;
        try {
            Bitmap.Config config = uncheckedBitmap.getConfig();
            mutableBitmap = uncheckedBitmap.copy(config==null?Bitmap.Config.ARGB_8888:config, true);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        int defaultColor = Color.rgb(60,150,2);
        int blackColor = Color.rgb(48,120,2);
        int lightColor = Statics.color5;

        int a_diff = Color.alpha(lightColor)+ (Color.alpha(blackColor) - Color.alpha(defaultColor));
        int r_diff = Color.red(lightColor)+ (Color.red(blackColor) - Color.red(defaultColor));
        int g_diff = Color.green(lightColor)+ (Color.green(blackColor) - Color.green(defaultColor));
        int b_diff = Color.blue(lightColor)+ (Color.blue(blackColor) - Color.blue(defaultColor));

        int darkColor = Color.argb(a_diff, r_diff, g_diff, b_diff);

        for (int h = 0; h < mutableBitmap.getHeight(); h++)
            for (int w = 0; w < mutableBitmap.getWidth(); w++)
            {
                int color = mutableBitmap.getPixel(h,w);
                if (h>mutableBitmap.getHeight() /3  && h < 2*mutableBitmap.getHeight()/3 && w > mutableBitmap.getWidth()/3 && w < 2*mutableBitmap.getWidth()/3 )
                    continue;
                if (color == Color.WHITE || color == Color.TRANSPARENT || color == Color.BLACK || (Color.green(color)< 119&& Color.red(color)<40))
                    continue;
                if (Color.green(color)  > 130)
                    mutableBitmap.setPixel(h,w, lightColor);
                else mutableBitmap.setPixel(h,w, darkColor);
            }
        return mutableBitmap;
    }


    private CheckBox createCheckBox(Context customFormPlugin, final GroupItemCheckBox ef) {
        final CheckBox checkBox = new CheckBox(customFormPlugin);
        final GradientDrawable buttonDrawable = new GradientDrawable();
        final int height = 34;
        final int width = 34;
        buttonDrawable.setColor(Color.TRANSPARENT);
        checkBox.setWidth((int) (width * getResources().getDisplayMetrics().density));
        checkBox.setHeight((int)(height*getResources().getDisplayMetrics().density ));
        checkBox.setButtonDrawable(buttonDrawable);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        params.setMargins(0,0,0,(int)(15*getResources().getDisplayMetrics().density));
        checkBox.setLayoutParams(params);
        //checkBox.setMA(0,(int)(10* getResources().getDisplayMetrics().density),0, (int)(10* getResources().getDisplayMetrics().density));

        // final GradientDrawable uncheckedDrawable = new GradientDrawable();
        //uncheckedDrawable.setShape(GradientDrawable.RECTANGLE);
        //uncheckedDrawable.setStroke((int) (1 * getResources().getDisplayMetrics().density), Statics.color5);
        //uncheckedDrawable.setColor(Color.TRANSPARENT);
        //uncheckedDrawable.setSize((int) (height * getResources().getDisplayMetrics().density), (int) (width * getResources().getDisplayMetrics().density));

        // Drawable myIcon = getResources().getDrawable( R.drawable.checkbox_on );
        // final Bitmap bitmap = ((BitmapDrawable) myIcon).getBitmap();
        // final Drawable resultDrawable = new BitmapDrawable(Bitmap.createScaledBitmap(bitmap, (int) (height * getResources().getDisplayMetrics().scaledDensity),  (int) (width * getResources().getDisplayMetrics().density), false));
        checkBox.setBackgroundResource(R.drawable.checkbox_off);

        checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                {
                    checkBox.setBackgroundResource(R.drawable.checkbox_on);
                    //checkBox.setBackgroundDrawable(resultDrawable);
                    // checkBox.getBackground().setColorFilter(Statics.color5, Mode.DST_ATOP);
                    checkBox.setWidth((int) (width * getResources().getDisplayMetrics().density));
                    checkBox.setHeight((int)(height*getResources().getDisplayMetrics().density ));
                }
                else {
                    checkBox.setBackgroundResource(R.drawable.checkbox_off);
                    //checkBox.setBackgroundDrawable(uncheckedDrawable);
                    checkBox.setWidth((int) (width * getResources().getDisplayMetrics().density));
                    checkBox.setHeight((int) (height * getResources().getDisplayMetrics().density));

                }
                ef.setChecked(b);
            }
        });
        checkBox.setChecked(ef.isChecked());
        return checkBox;
    }

    private Intent chooseEmailClient() {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
        intent.setType("text/plain");
        final PackageManager pm = getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
        ResolveInfo best = null;

        // trying to find gmail client
        for (final ResolveInfo info : matches) {
            if (info.activityInfo.packageName.endsWith(".gm")
                    || info.activityInfo.name.toLowerCase().contains("gmail")) {
                best = info;
            }
        }

        if (best == null) {
            // if there is no gmail client trying to fing internal email client
            for (final ResolveInfo info : matches) {
                if (info.activityInfo.name.toLowerCase().contains("mail")) {
                    best = info;
                }
            }
        }
        if (best != null) {
            intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        }

        return intent;
    }

    /**
     * Prepares email text to send.
     * @param form configured user form
     * @return email text
     */
    private String prepareText(Form form) {
        try {//ErrorLogging

            StringBuilder sb = new StringBuilder();

            for (Iterator<Group> it = form.getGroups().iterator(); it.hasNext();) {
                Group group = it.next();

                sb.append("<b>");
                sb.append(group.getTitle());
                sb.append("</b><br/>");

                for (Iterator<GroupItem> it1 = group.getItems().iterator();
                     it1.hasNext();) {

                    GroupItem item = it1.next();

                    Class itemClass = item.getClass();
                    if (itemClass.equals(GroupItemCalendar.class)) {
                        GroupItemCalendar ef =
                                (GroupItemCalendar) item;

                        sb.append(ef.getLabel());
                        sb.append(": ");
                        if (ef.isSet()) {
                            String datePattern = getResources().getString(R.string.data_picker_pattern);
                            DateFormat DATE_FORMAT = new SimpleDateFormat(datePattern);
                            String s = DATE_FORMAT.format(ef.getDate());
                            sb.append(s);
                        }
                        sb.append("<br/>");
                    } else if (itemClass.equals(GroupItemCheckBox.class)) {
                        GroupItemCheckBox ef =
                                (GroupItemCheckBox) item;

                        sb.append(ef.getLabel());
                        sb.append(": ");
                        if (ef.isChecked()) {
                            sb.append("yes");
                        } else {
                            sb.append("no");
                        }
                        sb.append("<br/>");
                    } else if (itemClass.equals(GroupItemDropDown.class)) {
                        GroupItemDropDown ef =
                                (GroupItemDropDown) item;

                        sb.append(ef.getLabel());
                        sb.append(": ");
                        try {
                            sb.append(ef.getItems()
                                    .get(ef.getSelectedIndex()).getValue());
                        } catch (Exception e) {
                        }
                        sb.append("<br/>");
                    } else if (itemClass.equals(GroupItemEntryField.class)) {
                        GroupItemEntryField ef =
                                (GroupItemEntryField) item;

                        sb.append(ef.getLabel());
                        sb.append(": ");
                        sb.append(ef.getValue());
                        sb.append("<br/>");
                    } else if (itemClass.equals(GroupItemRadioButton.class)) {
                        GroupItemRadioButton ef =
                                (GroupItemRadioButton) item;

                        sb.append(ef.getLabel());
                        sb.append(": ");
                        if (ef.isSelected()) {
                            sb.append("yes");
                        } else {
                            sb.append("no");
                        }
                        sb.append("<br/>");
                    } else if (itemClass.equals(GroupItemTextArea.class)) {
                        GroupItemTextArea ef =
                                (GroupItemTextArea) item;

                        sb.append(ef.getLabel());
                        sb.append(": ");
                        sb.append(addSpaces(ef.getValue(),
                                ef.getLabel().length() + 2));
                        sb.append("<br>");
                    }
                }
            }

            if (widget.isHaveAdvertisement()) {
                sb.append("<br>\n (sent from <a href=\"http://ibuildapp.com\">iBuildApp</a>)");
            }

            return sb.toString();//text;

        } catch (Exception e) {
            Log.e("ROMAN_C", "lalala", e);
            return "";
        }
    }

    /**
     * Adds spaces to prepared form text.
     * @param str prepared text
     * @param colSpaces spaces count
     * @return string with added spaces
     */
    private String addSpaces(String str, int colSpaces) {
        StringBuilder res = new StringBuilder();

        String[] substrings = str.split("\n");
        for (int i = 0; i < substrings.length; i++) {
            res.append(substrings[i]);
            if (i == (substrings.length - 1)) {
                res.append("\n");
                for (int j = 0; j < colSpaces; j++) {
                    res.append(" ");
                }
            }
        }

        return res.toString();
    }

    /**
     * Checks if module color cheme is dark.
     * @param backColor module background color
     * @return true if background color is dark, false otherwise
     */
    private boolean isChemeDark(int backColor) {
        int r = (backColor >> 16) & 0xFF;
        int g = (backColor >> 8) & 0xFF;
        int b = (backColor >> 0) & 0xFF;

        double Y = (0.299 * r + 0.587 * g + 0.114 * b);
        if (Y > 127) {
            return true;
        } else {
            return false;
        }
    }


    private String readFileToString(String pathToFile) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(pathToFile)));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            br.close();
        } catch (Exception e) {
        }
        return sb.toString();
    }

    private void closeActivity() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Bitmap bitmap = null;
                    File f = new File(Environment.getExternalStorageDirectory()
                            .toString());
                    for (File temp : f.listFiles()) {
                        if (temp.getName().equals(TEMP_FILE_NAME)) {
                            f = temp;
                            break;
                        }
                    }

                    if (!f.exists())
                        return;

                        bitmap = ImageUtils.decodeSampledBitmapFromFile(f.getAbsolutePath(), 150, 150);

                        Bitmap thumbnail = Bitmap.createScaledBitmap(bitmap, 75, 75, true);
                        final PhotoPickerItem item = new PhotoPickerItem();
                        item.setSource(bitmap);
                        item.setThumbnail(bitmap);
                        item.setImageSource(f.getAbsolutePath());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PhotoPickerLayout layout = null;
                                for (PhotoPickerLayout indexLayout : layouts) {
                                    if (indexLayout.getUniqueId() == STARTED_LAYOUT_ID) {
                                        layout = indexLayout;
                                        break;
                                    }
                                }
                                if (layout != null) {
                                    final PhotoPickerLayout finalLayout = layout;
                                    layout.prepareInsert(new PhotoPickerLayout.OnPreparedListener() {
                                        @Override
                                        public void onPrepared() {
                                            finalLayout.getItem().getPhotos().add(item);
                                            finalLayout.getAdapter().notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        });
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }).start();


        } else if (resultCode == RESULT_OK && requestCode == GALLERY_PICTURE) {
            if (data != null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap;
                        String selectedImagePath ;

                        Uri selectedImage = data.getData();
                        String[] filePath = { MediaStore.Images.Media.DATA };
                        Cursor c = getContentResolver().query(selectedImage, filePath,
                                null, null, null);
                        c.moveToFirst();
                        int columnIndex = c.getColumnIndex(filePath[0]);
                        selectedImagePath = c.getString(columnIndex);
                        c.close();

                        if (selectedImagePath == null)
                            return;

                        bitmap = ImageUtils.decodeSampledBitmapFromFile(selectedImagePath, 150, 150);
                        Bitmap thumbnail = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/3, bitmap.getHeight()/3, true);
                        final PhotoPickerItem item = new PhotoPickerItem();
                        item.setSource(bitmap);
                        item.setThumbnail(thumbnail);
                        item.setImageSource(selectedImagePath);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                PhotoPickerLayout layout = null;
                                for (PhotoPickerLayout indexLayout : layouts) {
                                    if (indexLayout.getUniqueId() == STARTED_LAYOUT_ID) {
                                        layout = indexLayout;
                                        break;
                                    }
                                }
                                if (layout != null) {
                                    final PhotoPickerLayout finalLayout = layout;
                                    layout.prepareInsert(new PhotoPickerLayout.OnPreparedListener() {
                                        @Override
                                        public void onPrepared() {
                                            finalLayout.getItem().getPhotos().add(item);
                                            finalLayout.getAdapter().notifyDataSetChanged();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }).start();

            }
        }
    }
}
