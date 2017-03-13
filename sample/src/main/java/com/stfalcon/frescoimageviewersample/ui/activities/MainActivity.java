package com.stfalcon.frescoimageviewersample.ui.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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

    private String[] posters, descriptions;

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
        new ImageViewer.Builder(MainActivity.this, posters)
                .setStartPosition(startPosition)
                //.hideStatusBar(false)
                .isCircular(true) // default is false - no circular
                .setImageMargin(this, R.dimen.image_margin)
                .setImageChangeListener(getImageChangeListener())
                .setOnDismissListener(getDismissListener())
                .setCustomDraweeHierarchyBuilder(getHierarchyBuilder())
                .setOverlayView(overlayView)
                .show();
    }

    private ImageViewer.OnImageChangeListener getImageChangeListener() {
        return new ImageViewer.OnImageChangeListener() {
            @Override
            public void onImageChange(int position) {
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
}
