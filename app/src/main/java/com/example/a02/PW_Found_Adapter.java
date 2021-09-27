package com.example.a02;
import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class PW_Found_Adapter extends RecyclerView.Adapter<PW_Found_Adapter.CustomViewHolder> {

    private ArrayList<UserData> mList = null;
    private Activity context = null;


    public PW_Found_Adapter(Activity context, ArrayList<UserData> list) {
        this.context = context;
        this.mList = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView pw;


        public CustomViewHolder(View view) {
            super(view);
            this.pw = (TextView) view.findViewById(R.id.find_pw_result);
        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pw_find_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        viewholder.pw.setText(mList.get(position).getUserPW());
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

}