package com.example.a02;

import android.app.Activity;
import android.content.Intent;
import android.media.Rating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class inquiry_check_Adapter  extends RecyclerView.Adapter<inquiry_check_Adapter.CustomViewHolder> {

    private ArrayList<inquiryData> mList = null;
    private Activity context = null;
    private String userID;


    public inquiry_check_Adapter(Activity context, ArrayList<inquiryData> list,String userID) {
        this.context = context;
        this.mList = list;
        this.userID = userID;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView title,check;
        protected LinearLayout group;


        public CustomViewHolder(View view) {
            super(view);
            this.title = (TextView) view.findViewById(R.id.service_center_inquiry_check_list_title);
            this.check = (TextView) view.findViewById(R.id.service_center_inquiry_check_list_answer_check);
            this.group = (LinearLayout) view.findViewById(R.id.service_center_inquiry_check_list_group);
        }
    }


    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.service_center_inquiry_list_item, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder viewholder, int position) {

        viewholder.title.setText(mList.get(position).getInquiry_title());
        if(mList.get(position).getAnswerCheck().equals("1")){
            int red = ContextCompat.getColor(context,R.color.red);
            viewholder.check.setText("답변 완료");
            viewholder.check.setTextColor(red);
        }else{
            viewholder.check.setText("답변 미등록");
        }
        viewholder.group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userID.equals("admin")){
                    Intent service_center_admin_page = new Intent(v.getContext(),service_center_inquiry_admin_activity.class);
                    service_center_admin_page.putExtra("title",mList.get(position).getInquiry_title());
                    service_center_admin_page.putExtra("comment",mList.get(position).getInquiry_comment());
                    service_center_admin_page.putExtra("inquiry_no",mList.get(position).getInquiry_no());
                    context.startActivity(service_center_admin_page);
                }else{
                    Intent service_center_inquiry_answer_page = new Intent(v.getContext(),service_center_inquiry_answer_activity.class);
                    service_center_inquiry_answer_page.putExtra("title",mList.get(position).getInquiry_title());
                    service_center_inquiry_answer_page.putExtra("comment",mList.get(position).getInquiry_comment());
                    service_center_inquiry_answer_page.putExtra("inquiry_no",mList.get(position).getInquiry_no());
                    context.startActivity(service_center_inquiry_answer_page);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }

}
