package com.ibuildapp.romanblack.CustomFormPlugin.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import com.ibuildapp.romanblack.CustomFormPlugin.PhotoViewActivity;
import com.ibuildapp.romanblack.CustomFormPlugin.R;
import com.ibuildapp.romanblack.CustomFormPlugin.groups.GroupItemPhotoPicker;
import com.ibuildapp.romanblack.CustomFormPlugin.utils.ImageUtils;
import com.ibuildapp.romanblack.CustomFormPlugin.utils.Statics;


public class PhotoPickerLayout extends LinearLayout{
    public  interface OnPreparedListener{
        void onPrepared();
    }
    private static float DENSITY = 0;
    private static int displayWidth;
    private static int itemSize;
    public static int COLUMN_COUNT = 0;
    private static int counter;
    private static int newId(){
        return counter++;
    }

    private int uniqueId;
    private static final int SPACE = 1;
    private GroupItemPhotoPicker item;
    private TextView label;
    private RecyclerView photosView;
    private Button button;
    private PhotoPickerAdapter adapter;

    public PhotoPickerLayout(Context context) {
        super(context);
        init();
    }

    public PhotoPickerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        if (displayWidth == 0) {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            displayWidth = displaymetrics.widthPixels;
            DENSITY = getResources().getDisplayMetrics().density;
            itemSize = (int) (80*DENSITY);
            COLUMN_COUNT = displayWidth/itemSize;
        }

        uniqueId = newId();

        LayoutInflater.from(getContext()).inflate(R.layout.custom_form_photo_picker_layout, this, true);
        setBackgroundColor(Statics.color1);
        setOrientation(VERTICAL);

        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);

        label = (TextView) findViewById(R.id.custom_form_photo_picker_label);
        label.setTextColor(Statics.color3);

        button = (Button) findViewById(R.id.custom_form_photo_picker_button);
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            button.setAllCaps(false);

        float borderSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
        drawable.setStroke((int) borderSize, Statics.color5);
        drawable.setColor(Statics.color5);
        button.setBackgroundDrawable(drawable);
        button.setTextColor(Statics.color1);

        photosView = (RecyclerView) findViewById(R.id.custom_form_photo_picker_photos_grid);
        photosView.setLayoutManager(new GridLayoutManager(getContext(), COLUMN_COUNT, GridLayoutManager.VERTICAL, false));

        adapter = new PhotoPickerAdapter();
        photosView.setAdapter(adapter);

        photosView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = (int) (5*SPACE * DENSITY);
                outRect.top = (int) (SPACE * DENSITY);
                outRect.right = (int) (SPACE * DENSITY);
                outRect.left = (int) (SPACE * DENSITY);
            }
        });
        DefaultItemAnimator animator = new DefaultItemAnimator();
        photosView.setItemAnimator(animator);
    }


    public void setData(GroupItemPhotoPicker item) {
        this.item = item;
        if (item.getLabel() == null || "".equals(item.getLabel()))
            label.setVisibility(GONE);
        else
        label.setVisibility(VISIBLE);
        this.label.setText(item.getLabel());
        this.button.setText(item.getValue());
    }

    public int getUniqueId() {
        return uniqueId;
    }

    public boolean canAddPhoto() {
        return item.getPhotos().size()< item.getLimit();
    }

    public void prepareInsert(final OnPreparedListener listener) {
        int size = item.getPhotos().size();
        if ((size+1)%COLUMN_COUNT == 1){
            ResizeAnimation anim = new ResizeAnimation(photosView, photosView.getWidth(), photosView.getHeight(), photosView.getWidth(), photosView.getHeight()+itemSize);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    listener.onPrepared();

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            photosView.startAnimation(anim);
        }
        else
            listener.onPrepared();
    }

    public class PhotoPickerHolder extends RecyclerView.ViewHolder{
        private SquareImageView mainView;
        private SquareLinearLayout deletePic;
        public PhotoPickerHolder(View itemView) {
            super(itemView);
            mainView = (SquareImageView) itemView.findViewById(R.id.custom_form_photo_picker_main_image);
            deletePic = (SquareLinearLayout) itemView.findViewById(R.id.custom_form_photo_picker_delete);
        }
    }
    public class PhotoPickerAdapter extends RecyclerView.Adapter<PhotoPickerHolder>{

        @Override
        public PhotoPickerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_form_photo_picker_item, parent, false);
            PhotoPickerHolder holder = new PhotoPickerHolder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(final PhotoPickerHolder holder, final int position) {
            holder.mainView.setImageBitmap(item.getPhotos().get(position).getThumbnail());

            holder.mainView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), PhotoViewActivity.class);
                    intent.putExtra(ImageUtils.BITMAP_EXTRA, item.getPhotos().get(position).getImageSource() );
                    getContext().startActivity(intent);
                }
            });

            final int pos = position;
            holder.deletePic.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (item.getPhotos().size() == 1) {
                        item.getPhotos().clear();
                    } else
                        item.getPhotos().remove(pos);

                    adapter.notifyItemRemoved(pos);
                    adapter.notifyItemRangeChanged(0, item.getPhotos().size());
                    int size = item.getPhotos().size();
                    if (size % COLUMN_COUNT == 0) {
                        ResizeAnimation anim = new ResizeAnimation(photosView, photosView.getWidth(), photosView.getHeight(), photosView.getWidth(), photosView.getHeight() - itemSize);
                        anim.setStartOffset(300);
                        photosView.startAnimation(anim);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return item.getPhotos().size();
        }
    }

    public Button getButton(){
        return button;
    }
    public GroupItemPhotoPicker getItem(){
        return item;
    }

    public PhotoPickerAdapter getAdapter(){
        return adapter;
    }

    public class ResizeAnimation extends Animation {
        private View mView;
        private float mToHeight;
        private float mFromHeight;

        private float mToWidth;
        private float mFromWidth;

        public ResizeAnimation(View v, float fromWidth, float fromHeight, float toWidth, float toHeight) {
            mToHeight = toHeight;
            mToWidth = toWidth;
            mFromHeight = fromHeight;
            mFromWidth = fromWidth;
            mView = v;
            setDuration(300);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            float height =
                    (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
            float width = (mToWidth - mFromWidth) * interpolatedTime + mFromWidth;
            LayoutParams p = (LayoutParams) mView.getLayoutParams();
            p.height = (int) height;
            p.width = (int) width;
            mView.requestLayout();
        }
    }
}
