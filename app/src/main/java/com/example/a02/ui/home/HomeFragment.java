package com.example.a02.ui.home;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.a02.GpsTracker;
import com.example.a02.R;
import com.example.a02.HomeLayoutAdapter;
import com.example.a02.map_main_activity;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private static final int NUM_COLUMNS = 2;
    private TextView address_text;
    private String address,latitude="37.56",longitude="126.97",userID;
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        address_text = (TextView) root.findViewById(R.id.main_layout_address);
        if(getArguments()!=null){
            address = getArguments().getString("address");
            latitude = getArguments().getString("latitude");
            longitude = getArguments().getString("longitude");
            userID = getArguments().getString("userID");
            address_text.setText(address);
        }
        initImageBitmaps(root);
        return root;

    }
    private void initImageBitmaps(View root){
        if(mImageUrls.isEmpty()) {
            mImageUrls.add("http://menu.mt.co.kr/moneyweek/thumb/2013/05/31/06/2013053114428058639_1.jpg");
            mNames.add("음식점");

            mImageUrls.add("https://mp-seoul-image-production-s3.mangoplate.com/495395_1616909611779280.jpg?fit=around|738:738&crop=738:738;*,*&output-format=jpg&output-quality=80");
            mNames.add("카페");

            mImageUrls.add("https://www.click2houston.com/resizer/MvWuxElngYBEA45wkCQA30wir4g=/800x532/smart/filters:format(jpeg):strip_exif(true):strip_icc(true):no_upscale(true):quality(65)/cloudfront-us-east-1.images.arcpublishing.com/gmg/5KMJ4U2HIRBNFMHQ55UDVX7WDA.jpg");
            mNames.add("교회");

            mImageUrls.add("http://tong.visitkorea.or.kr/cms/resource_etc/13/2411913_image_1.jpg");
            mNames.add("슈퍼마켓");

            mImageUrls.add("https://cdnimg.webstaurantstore.com/uploads/blog/2018/8/start-a-bakery-bakery-display-case.jpg");
            mNames.add("베이커리");

            mImageUrls.add("https://www.sydneyolympicpark.com.au/-/media/images/sopa/sydney-olympic-park-site/the-park/hero-image-1920-x-1280/2016-city-convenience-store.jpg?ImageResizing=true&w=1024&h=683&mode=crop&hash=A3A532DDBAA29CD022028614BB00455C7F4D2277");
            mNames.add("편의점");

            mImageUrls.add("https://images.unsplash.com/photo-1538108149393-fbbd81895907?ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8aG9zcGl0YWx8ZW58MHx8MHx8&ixlib=rb-1.2.1&w=1000&q=80");
            mNames.add("병원");

            mImageUrls.add("https://estnn.com/wp-content/uploads/2021/08/atm-free-guy-fortnite.jpg");
            mNames.add("ATM");

            mImageUrls.add("https://lh3.googleusercontent.com/proxy/axUi9zBOpxLCzWz7EUsvkhIyHWmaxE9u3tpZebvQHAaFf_1GwY5EfXQH2jideki70E_X3xrXdzQ662Nl-E69KQJwdb1T5lu8fgC2JPyTOLEKQPT9ebnH-Q");
            mNames.add("서점");

        }

        initRecyclerView(root,latitude,longitude,userID);

    }

    private void initRecyclerView(View root,String latitude,String longitude,String userID){

        RecyclerView recyclerView = root.findViewById(R.id.home_recycler_view);
        HomeLayoutAdapter Adapter =
                new HomeLayoutAdapter(root.getContext(), mNames, mImageUrls,latitude,longitude,userID);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(NUM_COLUMNS, LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setAdapter(Adapter);
    }
}


