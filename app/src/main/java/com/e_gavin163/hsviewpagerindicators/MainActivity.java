package com.e_gavin163.hsviewpagerindicators;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.e_gavin163.hsviewpageindicators.Indicator;


public class MainActivity extends Activity {
    private Indicator pageControl;
    private ViewPager viewPager;
    private TextView[] tvList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pageControl = (Indicator) findViewById(R.id.pagecontrol);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        createData();

        pageControl.setup(
                new Indicator.Builder()
                        .setAdapter(adapter)
                        .bindViewPager(viewPager)

        );
    }

    private void createData() {
        tvList = new TextView[5];
        for (int i = 0; i < tvList.length; i++) {
            TextView tv = new TextView(MainActivity.this);
            tv.setText(i + "");
            tv.setTextSize(30);
            tv.setTextColor(Color.BLACK);
            tvList[i] = tv;
        }
    }

    PagerAdapter adapter = new PagerAdapter() {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return tvList.length;
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            view.addView(tvList[position]);
            return tvList[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(tvList[position]);
        }

    };

}
