package com.stfalcon.frescoimageviewersample.ui.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.stfalcon.frescoimageviewersample.R;
import com.stfalcon.frescoimageviewersample.ui.Demo;
import com.stfalcon.frescoimageviewersample.ui.views.ImageOverlayView;

/*
 * Created by Alexander Krol (troy379) on 29.08.16.
 */
public class MainActivity extends AppCompatActivity {

    private String[] posters, lqPosters, descriptions;

    private static final int[] ids = new int[]{
            R.id.firstImage, R.id.secondImage,
            R.id.thirdImage, R.id.fourthImage,
            R.id.fifthImage, R.id.sixthImage,
            R.id.seventhImage, R.id.eighthImage,
            R.id.ninethImage
    };

    private ImageOverlayView overlayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        posters = Demo.getPosters();
        lqPosters = Demo.getLqPosters();
        descriptions = Demo.getDescriptions();
        initViews();
    }

    private void initViews() {
        for (int i = 0; i < ids.length; i++) {
            SimpleDraweeView drawee = (SimpleDraweeView) findViewById(ids[i]);
            initDrawee(drawee, i);

        }
    }

    private void initDrawee(SimpleDraweeView drawee, final int startPosition) {
        drawee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicker(startPosition);
            }
        });
        drawee.setImageURI(posters[startPosition]);
    }

    private void showPicker(int startPosition) {
        overlayView = new ImageOverlayView(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        new ImageViewer.Builder(MainActivity.this, posters, lqPosters)
                .setStartPosition(startPosition)
                //.hideStatusBar(false)
                .isCircular(false) // default is false - no circular
                .setImageMargin(this, R.dimen.image_margin)
                .setImageChangeListener(getImageChangeListener())
                .setOnDismissListener(getDismissListener())
                .setCustomDraweeHierarchyBuilder(getHierarchyBuilder())
                .setOverlayView(overlayView)
                .setCustomViews(createCustomViews())
                .show();
    }

    private ImageViewer.OnImageChangeListener getImageChangeListener() {
        return new ImageViewer.OnImageChangeListener() {
            @Override
            public void onImageChange(int position, int originPosition) {
                int actualPosition = position % posters.length;
                String url = posters[actualPosition];
                overlayView.setShareText(url);
                overlayView.setDescription(descriptions[actualPosition]);
            }
        };
    }

    private ImageViewer.OnDismissListener getDismissListener() {
        return new ImageViewer.OnDismissListener() {
            @Override
            public void onDismiss() {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        };
    }

    private GenericDraweeHierarchyBuilder getHierarchyBuilder() {
        RoundingParams roundingParams = new RoundingParams();
        roundingParams.setRoundAsCircle(true);

        return GenericDraweeHierarchyBuilder.newInstance(getResources());
//                .setRoundingParams(roundingParams);
    }

    private SparseArray<View> createCustomViews() {
        SparseArray<View> array = new SparseArray<>();

        FrameLayout f0 = new FrameLayout(this);
        f0.setLayoutParams(new FrameLayout.LayoutParams(200, 300));
        f0.setBackgroundColor(getResources().getColor(android.R.color.white));
        array.put(0, f0);

        FrameLayout f1 = new FrameLayout(this);
        f1.setLayoutParams(new FrameLayout.LayoutParams(200, 300));
        f1.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_dark));
        array.put(2, f1);

        FrameLayout f2 = new FrameLayout(this);
        f2.setLayoutParams(new FrameLayout.LayoutParams(200, 300));
        f2.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        array.put(5, f2);

        FrameLayout f9 = new FrameLayout(this);
        f9.setLayoutParams(new FrameLayout.LayoutParams(200, 300));
        f9.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        array.put(9, f9);

        FrameLayout f22 = new FrameLayout(this);
        f22.setLayoutParams(new FrameLayout.LayoutParams(200, 300));
        f22.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        array.put(22, f22);

        return array;
    }
}
