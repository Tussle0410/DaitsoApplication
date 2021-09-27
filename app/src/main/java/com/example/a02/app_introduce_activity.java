package com.example.a02;

import android.os.Bundle;
import mehdi.sakout.aboutpage.Element;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import mehdi.sakout.aboutpage.AboutPage;

public class app_introduce_activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        Element adsElement = new Element();
        adsElement.setTitle("광고 문의 : 010-0000-0000");

        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.introduce_main_img)
                .addItem(new Element().setTitle("버전 1.0.0"))
                .addItem(adsElement)
                .addGroup("우리와 함께해요")
                .addEmail("gkak1310@daum.net")
                .addWebsite("https://www.suwon.ac.kr/index.html?menuno=593")
                .addFacebook("SuwonUniv")
                .addYoutube("수원대학교usw")
                .addPlayStore("com/store/apps/details?id=kr.co.bvs.echeck.usw")
                .addInstagram("usw1982?utm_medium=copy_link")
                .addItem(getCopyRightsElement())
                .create();

        setContentView(aboutPage);
    }


    Element getCopyRightsElement() {
        Element copyRightsElement = new Element();
        final String copyrights = String.format(getString(R.string.copy_right), Calendar.getInstance().get(Calendar.YEAR));
        copyRightsElement.setTitle(copyrights);
        copyRightsElement.setIconDrawable(R.drawable.about_icon_copy_right);
        copyRightsElement.setAutoApplyIconTint(true);
        copyRightsElement.setIconTint(mehdi.sakout.aboutpage.R.color.about_item_icon_color);
        copyRightsElement.setIconNightTint(android.R.color.white);
        copyRightsElement.setGravity(Gravity.CENTER);
        copyRightsElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(app_introduce_activity.this, copyrights, Toast.LENGTH_SHORT).show();
            }
        });
        return copyRightsElement;
    }
}
