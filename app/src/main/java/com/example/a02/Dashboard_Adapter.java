package com.example.a02;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class Dashboard_Adapter  extends RecyclerView.Adapter<Dashboard_Adapter.CustomViewHolder> {

    private ArrayList<StoreData> mList = null;
    private Context context = null;
    private String userID;

    public Dashboard_Adapter(Context context, ArrayList<StoreData> list,String ID) {
        this.context = context;
        this.mList = list;
        userID = ID;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView title,address,category,rating;
        protected ImageView category_image;
        protected LinearLayout group;
        public CustomViewHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.dashboard_list_title);
            this.address = (TextView) view.findViewById(R.id.dashboard_list_address);
            this.category = (TextView) view.findViewById(R.id.dashboard_list_category);
            this.category_image = (ImageView) view.findViewById(R.id.dashboard_list_category_image);
            this.rating = (TextView) view.findViewById(R.id.dashboard_list_rating);
            this.group = (LinearLayout)view.findViewById(R.id.dashboard_list_group);
        }
    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dashborad_list_item, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        viewholder.title.setText(mList.get(position).getStoreName());
        viewholder.address.setText(mList.get(position).getStoreAddress());
        viewholder.category.setText(mList.get(position).getStoreKinds());
        viewholder.rating.setText(mList.get(position).getStoreRating());
        if(mList.get(position).getStoreKinds().equals("RESTAURANT")){
            viewholder.category_image.setImageResource(R.drawable.com);
        }else if(mList.get(position).getStoreKinds().equals("CHURCH")){
            viewholder.category_image.setImageResource(R.drawable.church);
        }else if(mList.get(position).getStoreKinds().equals("CAFE")){
            viewholder.category_image.setImageResource(R.drawable.cafe);
        }else if(mList.get(position).getStoreKinds().equals("SUPERMARKET")){
            viewholder.category_image.setImageResource(R.drawable.shop);
        }else if(mList.get(position).getStoreKinds().equals("BAKERY")){
            viewholder.category_image.setImageResource(R.drawable.bread);
        }else if(mList.get(position).getStoreKinds().equals("CONVENIENCE_STORE")){
            viewholder.category_image.setImageResource(R.drawable.convience_store);
        }else if(mList.get(position).getStoreKinds().equals("HOSPITAL")){
            viewholder.category_image.setImageResource(R.drawable.hos);
        }else if(mList.get(position).getStoreKinds().equals("ATM")){
            viewholder.category_image.setImageResource(R.drawable.atm);
        }else if(mList.get(position).getStoreKinds().equals("BOOK_STORE")){
            viewholder.category_image.setImageResource(R.drawable.book_store);
        }
        viewholder.group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent community = new Intent(v.getContext(),comActivity_top.class);
                community.putExtra("title",mList.get(position).getStoreName());
                community.putExtra("category",mList.get(position).getStoreKinds());
                community.putExtra("userID",userID);
                context.startActivity(community);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

}
