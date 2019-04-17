package com.nstudio.shaadimatches.utils;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.nstudio.shaadimatches.R;


public class ImageLoader {

    public static void loadImage(final ImageView view, final String imageUrl, String thumb, String gender) {

        final int placeHolder;
        if (gender.equals("male")){
            placeHolder = R.drawable.ic_user_male;
        }else {
            placeHolder = R.drawable.ic_user_female;
        }

        Glide.with(view.getContext())
                .load(imageUrl)
                .placeholder(placeHolder)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(view);

    }

}
