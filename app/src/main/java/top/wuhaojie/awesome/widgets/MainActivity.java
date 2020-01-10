package top.wuhaojie.awesome.widgets;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepare();

        // TipView
        findViewById(R.id.view_tip_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new TipView.Builder()
                        .with(MainActivity.this)
                        .content("这是一个小提示")
                        .on(getWindow())
                        .build()
                        .show(view);
            }
        });


        // TagView
        ((TagView) findViewById(R.id.view_tag_view)).refresh(Arrays.asList(
                "简单", "易懂", "快速实现"
        ));


        // PagerIndicatorView
        ViewPager viewPager = findViewById(R.id.view_pager);
        PagerIndicatorView indicatorView = findViewById(R.id.view_indicator);
        indicatorView.bind(viewPager);

    }

    private void prepare() {
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new PagerAdapter() {

            private final List<String> colors = new ArrayList<>();

            {
                colors.add("#63A512");
                colors.add("#2196F3");
                colors.add("#FF5722");
                colors.add("#9C27B0");
            }

            @Override
            public int getCount() {
                return colors.size();
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
                return view == o;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                View view = new View(container.getContext());
                view.setBackgroundColor(Color.parseColor(colors.get(position)));
                container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                return view;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                if (object instanceof View) {
                    container.removeView((View) object);
                }
            }

        });

    }
}
