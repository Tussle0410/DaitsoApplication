package com.example.a02;
import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class ID_Found_Adapter extends RecyclerView.Adapter<ID_Found_Adapter.CustomViewHolder> {

    private ArrayList<UserData> mList = null;
    private Activity context = null;


    public ID_Found_Adapter(Activity context, ArrayList<UserData> list) {
        this.context = context;
        this.mList = list;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView id;


        public CustomViewHolder(View view) {
            super(view);
            this.id = (TextView) view.findViewById(R.id.find_id_result);
        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.id_find_list, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        viewholder.id.setText(mList.get(position).getUserID());
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

}