package com.example.a02;

import android.app.Activity;
import android.media.Rating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class Comment_Adapter  extends RecyclerView.Adapter<Comment_Adapter.CustomViewHolder> {

    private ArrayList<commentData> mList = null;
    private Activity context = null;


    public Comment_Adapter(Activity context, ArrayList<commentData> list) {
        this.context = context;
        this.mList = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView id,comment;
        protected RatingBar rating;


        public CustomViewHolder(View view) {
            super(view);
            this.id = (TextView) view.findViewById(R.id.community_comment_id);
            this.rating = (RatingBar) view.findViewById(R.id.community_comment_rating);
            this.comment = (TextView) view.findViewById(R.id.community_comment_comment);
        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.com_comment_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        viewholder.id.setText(mList.get(position).getUser_ID());
        viewholder.rating.setRating(mList.get(position).getRating());
        viewholder.comment.setText(mList.get(position).getComment());

    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

}
