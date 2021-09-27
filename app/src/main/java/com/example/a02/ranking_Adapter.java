package com.example.a02;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ranking_Adapter extends RecyclerView.Adapter<ranking_Adapter.CustomViewHolder> {
    private ArrayList<UserData> list=null;
    private Activity context=null;
    public ranking_Adapter(Activity context,ArrayList<UserData> list){
        this.list = list;
        this.context = context;
    }
    class CustomViewHolder extends RecyclerView.ViewHolder{
        protected TextView ranking,ID,point;
        public CustomViewHolder(View v){
            super(v);
            ranking = (TextView)v.findViewById(R.id.ranking_list_ranking);
            ID = (TextView)v.findViewById(R.id.ranking_list_ID);
            point = (TextView)v.findViewById(R.id.ranking_list_point);
        }}
        @Override
        public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ranking_item_list,null);
            CustomViewHolder customViewHolder = new CustomViewHolder(view);
            return customViewHolder;
        }
        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder viewHolder,int position){
            if(list.get(position).getUserNo().equals("1")){
                viewHolder.ranking.setText("1");
            }else if(list.get(position).getUserNo().equals("2")){
                viewHolder.ranking.setText("2");
            }else if(list.get(position).getUserNo().equals("3")){
                viewHolder.ranking.setText("3");
            }else{
                viewHolder.ranking.setText(list.get(position).getUserNo());
            }
            viewHolder.ID.setText(list.get(position).getUserID());
            viewHolder.point.setText(list.get(position).getUserPoint());
        }
        @Override
        public int getItemCount(){
            return (null !=list ? list.size():0);
        }
    }
