package com.stfalcon.frescoimageviewer.adapter;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.stfalcon.frescoimageviewer.drawee.ZoomableDraweeView;

import java.util.HashSet;
import java.util.List;

import jp.wasabeef.fresco.processors.BlurPostprocessor;
import me.relex.photodraweeview.OnScaleChangeListener;

/*
 * Created by troy379 on 07.12.16.
 */
public class ImageViewerAdapter
        extends RecyclingPagerAdapter<ViewHolder> {

    private final int IMAGE_TYPE = -10;
    private final int CUSTOM_VIEW_TYPE = -20;

    private Context context;
    private List<String> urls;
    private List<String> lqUrls;
    private HashSet<ViewHolder> holders;
    private GenericDraweeHierarchyBuilder hierarchyBuilder;
    private boolean isCircular = false;
    private int blurRadius = 4;
    private SparseArray<View> customViews;

    public ImageViewerAdapter(Context context, List<String> urls, List<String> lqUrls,
                              GenericDraweeHierarchyBuilder hierarchyBuilder,
                              boolean isCircular,
                              int blurRadius,
                              SparseArray<View> customViews) {
        this.context = context;
        this.urls = urls;
        this.lqUrls = lqUrls;
        this.holders = new HashSet<>();
        this.hierarchyBuilder = hierarchyBuilder;
        this.isCircular = isCircular;
        this.blurRadius = blurRadius;
        this.customViews = customViews;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewHolder holder;
        if (viewType != IMAGE_TYPE && customViews != null && customViews.get(viewType) != null) {

            RelativeLayout relativeLayout = new RelativeLayout(context);
            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            relativeLayout.addView(customViews.get(viewType));

            holder = new CustomViewHolder(relativeLayout);
        } else {
            holder = new ImageViewHolder(new ZoomableDraweeView(context));
        }
        holders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (customViews != null && customViews.get(position) != null) {
            return position;
        }
        return IMAGE_TYPE;
    }

    @Override
    public int getItemCount() {
        return (urls.size() <= 1 || !isCircular) ? urls.size() : 5000;
    }


    public boolean isScaled(int index) {
        for (ViewHolder holder : holders) {
            if (holder instanceof ImageViewHolder && holder.mPosition == index) {
                return ((ImageViewHolder) holder).isScaled;
            }
        }
        return false;
    }

    public void resetScale(int index) {
        for (ViewHolder holder : holders) {
            if (holder instanceof ImageViewHolder && holder.mPosition == index) {
                ((ImageViewHolder) holder).resetScale();
                break;
            }
        }
    }

    public boolean isCircular() {
        return this.isCircular;
    }

    public int getImagesCount() {
        return urls.size();
    }

    public String getUrl(int index) {
        return urls.get(isCircular ? index % urls.size() : index);
    }

    private BaseControllerListener<ImageInfo>
    getDraweeControllerListener(final ZoomableDraweeView drawee) {
        return new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (imageInfo == null) {
                    return;
                }
                drawee.update(imageInfo.getWidth(), imageInfo.getHeight());
            }
        };
    }

    class CustomViewHolder extends ViewHolder {

        CustomViewHolder(View itemView) {
            super(itemView);
        }

        void bind(int position) {
            super.bind(position);
            this.mPosition = position;
        }
    }

    class ImageViewHolder extends ViewHolder implements OnScaleChangeListener {

        private ZoomableDraweeView drawee;
        private boolean isScaled;

        ImageViewHolder(View itemView) {
            super(itemView);
            drawee = (ZoomableDraweeView) itemView;
        }

        void bind(int position) {
            super.bind(position);
            this.mPosition = position;

            tryToSetHierarchy();

            int pos = isCircular ? position % urls.size() : position;

            if (pos < lqUrls.size()) {
                setController(urls.get(pos), lqUrls.get(pos));
            } else {
                setController(urls.get(pos), null);
            }

            drawee.setOnScaleChangeListener(this);
        }

        @Override
        public void onScaleChange(float scaleFactor, float focusX, float focusY) {
            isScaled = drawee.getScale() > 1.0f;
        }

        public void resetScale() {
            drawee.setScale(1.0f, true);
        }

        private void tryToSetHierarchy() {
            if (hierarchyBuilder != null) {
                hierarchyBuilder.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
                drawee.setHierarchy(hierarchyBuilder.build());
            }
        }

        private void setController(String url, String lqUrl) {
            PipelineDraweeControllerBuilder controllerBuilder = Fresco.newDraweeControllerBuilder();
            controllerBuilder.setUri(url);
            controllerBuilder.setAutoPlayAnimations(true);
            controllerBuilder.setOldController(drawee.getController());
            controllerBuilder.setControllerListener(getDraweeControllerListener(drawee));
            if (lqUrl != null) {
                ImageRequestBuilder lqRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(lqUrl));
                lqRequestBuilder.setPostprocessor(new BlurPostprocessor(context, blurRadius));
                controllerBuilder.setLowResImageRequest(lqRequestBuilder.build());
            }
            drawee.setController(controllerBuilder.build());
        }

    }
}
