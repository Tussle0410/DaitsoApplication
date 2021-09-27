package com.example.a02;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.a02.ui.home.HomeFragment;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class HomeLayoutAdapter extends RecyclerView.Adapter<HomeLayoutAdapter.ViewHolder> {

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private Context mContext;
    private String category,mlatitude,mlongitude,muserID;

    public HomeLayoutAdapter(Context context, ArrayList<String> names, ArrayList<String> imageUrls,
                             String latitude, String longitude,String userID) {
        mNames = names;
        mImageUrls = imageUrls;
        mContext = context;
        mlatitude = latitude;
        mlongitude = longitude;
        muserID = userID;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView")  final int position) {

        RequestOptions requestOptions = new RequestOptions()
                .placeholder(R.drawable.ic_launcher_background);

        Glide.with(mContext)
                .load(mImageUrls.get(position))
                .apply(requestOptions)
                .into(holder.image);

        holder.name.setText(mNames.get(position));

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mNames.get(position).equals("음식점")){
                    category = "RESTAURANT";
                    intent_act(view.getContext());
                }else if(mNames.get(position).equals("카페")){
                    category = "CAFE";
                    intent_act(view.getContext());
                }else if(mNames.get(position).equals("교회")){
                    category = "CHURCH";
                    intent_act(view.getContext());
                }else if(mNames.get(position).equals("슈퍼마켓")){
                    category = "SUPERMARKET";
                    intent_act(view.getContext());
                }else if(mNames.get(position).equals("베이커리")){
                    category = "BAKERY";
                    intent_act(view.getContext());
                }else if(mNames.get(position).equals("편의점")){
                    category = "CONVENIENCE_STORE";
                    intent_act(view.getContext());
                }else if(mNames.get(position).equals("병원")){
                    category = "HOSPITAL";
                    intent_act(view.getContext());
                }else if(mNames.get(position).equals("ATM")){
                    category = "ATM";
                    intent_act(view.getContext());
                }else if(mNames.get(position).equals("서점")){
                    category = "BOOK_STORE";
                    intent_act(view.getContext());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mImageUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.imageview_widget);
            this.name = itemView.findViewById(R.id.name_widget);
        }
    }
        public void intent_act(Context context){
            Intent intent = new Intent(context,map_main_activity.class);
            intent.putExtra("category",category);
            intent.putExtra("latitude",mlatitude);
            intent.putExtra("longitude",mlongitude);
            intent.putExtra("userID",muserID);
            mContext.startActivity(intent);
    }
}

