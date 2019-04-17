package com.nstudio.shaadimatches.ui;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.nstudio.shaadimatches.R;
import com.nstudio.shaadimatches.libraries.CircleImageView;
import com.nstudio.shaadimatches.models.ProfileModel;
import com.nstudio.shaadimatches.utils.ImageLoader;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.MyViewHolder>{

    private ArrayList<ProfileModel> list;
    private DateFormat dateFormat, timeFormat;
    private DateFormat format;
    private String dateToday;
    private MainActivity.OnItemRemoveListener itemRemoveListener;

    ProfileAdapter(ArrayList<ProfileModel> list, MainActivity.OnItemRemoveListener itemRemoveListener) {
        this.list = list;
        this.itemRemoveListener = itemRemoveListener;
        dateFormat = new SimpleDateFormat("dd MMM yy", Locale.ENGLISH);
        timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
        Date dateObj = new Date();
        dateToday = dateFormat.format(dateObj);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shaadi_matches_card,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //i'm hiding view on item removed because view is becoming visible after animation end
        //hence if item is restored back by clicking undo then itemView must be visible again, hence check visibility and restore
        if (holder.itemView.getVisibility()!=View.VISIBLE){
            holder.itemView.setVisibility(View.VISIBLE);
        }

        ProfileModel profile = list.get(position);

        ImageLoader.loadImage(holder.imgProfile,profile.getPicture().getLarge(), profile.getPicture().getThumbnail(),profile.getGender());

        holder.tvName.setText(profile.getName());

        holder.tvDescription.setText(ProfileModel.getDescription(profile.getDob(),profile.getLocation()));

        Date date = null;
        try {
            date = format.parse(profile.getRegisteredDate());
        } catch (ParseException e) {
            Log.e("ProfileAdapter","date parse error for > "+profile.getRegisteredDate());
           e.printStackTrace();
        }
        String text;
        if (date != null) {
            text = dateFormat.format(date);
            if (text.equals(dateToday)) {
                text = "Today";
            }
            String dateTime = text + " " + timeFormat.format(date);
            holder.tvTimestamp.setText(dateTime);
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public CardView parentCardView;
        private CircleImageView imgProfile;
        private TextView tvName,tvTimestamp,tvDescription;
        private TextView tvDecline,tvConnect;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            tvName = itemView.findViewById(R.id.tvName);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvConnect = itemView.findViewById(R.id.tvConnect);
            tvDecline = itemView.findViewById(R.id.tvDecline);
            parentCardView = itemView.findViewById(R.id.parentView);

            //dismiss card animating left
            tvDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemRemoveListener.onItemRemove(MyViewHolder.this, getAdapterPosition());
                }
            });

            //dismiss card animating right
            tvConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemRemoveListener.onItemRemove(MyViewHolder.this, getAdapterPosition());
                }
            });
        }

    }

    void removeItem(final int position, final RecyclerView.ViewHolder viewHolder) {
        Animation anim = AnimationUtils.loadAnimation(viewHolder.itemView.getContext(),
                android.R.anim.slide_out_right);
        anim.setDuration(300);
        viewHolder.itemView.startAnimation(anim);

        new Handler().postDelayed(new Runnable() {
            public void run() {

                //after end of animation, item is displaying back to it's original position for a second, hence setting item invisible

                viewHolder.itemView.setVisibility(View.GONE);

                // notify the item removed by position
                // to perform recycler view delete animations

                list.remove(position);
                notifyItemRemoved(position);

            }

        }, 300);


    }

    void restoreItem(ProfileModel item, int position) {
        list.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }


}
