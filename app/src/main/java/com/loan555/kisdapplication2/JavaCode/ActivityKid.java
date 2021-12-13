package com.loan555.kisdapplication2.JavaCode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.loan555.kisdapplication2.JavaCode.Adapter.PagerAdapter;
import com.loan555.kisdapplication2.R;

public class ActivityKid extends AppCompatActivity {
    private static final String TAG = "KA.AcKid";

    String idkid;
    String namekid;
    ImageView btnBack;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kid);
        initView();

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        Intent i = getIntent();
        idkid = i.getStringExtra("idTre");
        String tenTre = i.getStringExtra("tkTre");
        title.setText(tenTre);
        Log.d("abcccccc", idkid);
//        idkid = "60ffb5fd65c4b3001593befb";
        TabItem tabChart = findViewById(R.id.tabChart);
        TabItem tabHistory = findViewById(R.id.tabHistory);
        TabItem tabBlacklist = findViewById(R.id.tabBlacklist);
        ViewPager viewPager = findViewById(R.id.viewPager);
        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initView() {
        btnBack = findViewById(R.id.btnBackToolbarCommon);
        title = findViewById(R.id.titlToolbarCommon);
        findViewById(R.id.btnMoreToolbarCommon).setVisibility(View.GONE);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public String getMyData() {
        return idkid;
    }
}