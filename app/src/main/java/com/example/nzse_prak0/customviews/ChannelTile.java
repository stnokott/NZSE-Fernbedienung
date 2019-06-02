package com.example.nzse_prak0.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.nzse_prak0.ActivitySwitchedOn;
import com.example.nzse_prak0.R;
import com.example.nzse_prak0.helpers.Channel;
import com.example.nzse_prak0.helpers.Units;

import java.io.IOException;
import java.io.InputStream;

public class ChannelTile extends ConstraintLayout {
    private Channel channelInstance;
    private TextView lblTitle;
    private TextView lblBg;
    private ImageView bgIcon;
    private ImageButton btnFav;

    public ChannelTile(Context context, int bgColor) {
        super(context);

        setId(View.generateViewId());
        initLayoutParams();
        createBackgroundIcon();
        createFavButton();
        createTitleLabel();
        setBackgroundColor(bgColor);
    }

    public void initLayoutParams() {
        // set ConstraintLayout Parameters
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        int px = Units.dpToPx(8, getContext()); // margins
        params.setMargins(px, px, px, px);
        setLayoutParams(params);
    }

    private void createBackgroundIcon(){
        ImageView newBgIcon = new ImageView(getContext());
        newBgIcon.setId(View.generateViewId());

        // Layout
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                LayoutParams.MATCH_CONSTRAINT,
                LayoutParams.MATCH_CONSTRAINT
        );
        params.startToStart = ConstraintSet.PARENT_ID;
        params.endToEnd = ConstraintSet.PARENT_ID;
        params.topToTop = ConstraintSet.PARENT_ID;
        params.bottomToBottom = ConstraintSet.PARENT_ID;
        newBgIcon.setLayoutParams(params);
        int px = Units.dpToPx(8, getContext());
        newBgIcon.setPadding(px, px, px, px);

        newBgIcon.setTextAlignment(TEXT_ALIGNMENT_CENTER);

        addView(newBgIcon);
        bgIcon = newBgIcon;
    }

    private void createTitleLabel() {
        TextView newlblTitle = new TextView(getContext());
        newlblTitle.setId(View.generateViewId());

        // Layout
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                LayoutParams.MATCH_CONSTRAINT,
                LayoutParams.WRAP_CONTENT
        );
        params.startToStart = ConstraintSet.PARENT_ID;
        params.endToEnd = ConstraintSet.PARENT_ID;
        params.bottomToBottom = ConstraintSet.PARENT_ID;
        newlblTitle.setLayoutParams(params);

        int px = Units.dpToPx(8, getContext());
        newlblTitle.setPadding(px, px, px, px);

        // Weitere Parameter
        newlblTitle.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        newlblTitle.setTextColor(Color.WHITE);
        newlblTitle.setBackgroundColor(0x26000000);

        addView(newlblTitle);
        lblTitle = newlblTitle;
    }

    private void createFavButton() {
        ImageButton newbtnFav = new ImageButton(getContext());
        newbtnFav.setId(View.generateViewId());

        // Layout
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_CONSTRAINT
        );
        params.endToEnd = ConstraintSet.PARENT_ID;
        params.topToTop = ConstraintSet.PARENT_ID;
        int px = Units.dpToPx(8, getContext()); // margins
        params.setMargins(0, px, px, 0);
        newbtnFav.setLayoutParams(params);
        newbtnFav.setPadding(0, 0, 0, 0);

        // weitere Parameter
        newbtnFav.setElevation(Units.dpToPx(3, getContext()));
        newbtnFav.setOutlineProvider(null); // Schatten durch Elevation verhindern
        newbtnFav.setImageResource(R.drawable.fav_fill_white_anim);
        newbtnFav.setBackground(null);

        // Listener
        newbtnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavButton();
            }
        });

        addView(newbtnFav);
        btnFav = newbtnFav;
    }

    public void toggleFavButton() {
        Boolean isFav = getChannelInstance().getIsFav();
        updateFavStatus(isFav);

        Animatable animatable = (Animatable) btnFav.getDrawable();
        animatable.start();

        getChannelInstance().setIsFav(!isFav);
    }

    public void updateFavStatus(Boolean isFav) {
        if (isFav) {
            btnFav.setImageResource(R.drawable.fav_fill_reverse_white_anim);
        } else {
            btnFav.setImageResource(R.drawable.fav_fill_white_anim);
        }
    }

    public void setColor(int c) {
        setBackgroundColor(c);
        float[] hsv = new float[3];
        Color.colorToHSV(c, hsv);
        hsv[2] *= 0.85f;
        bgIcon.setImageAlpha(65);
        //bgIcon.setColorFilter(Color.HSVToColor(hsv), PorterDuff.Mode.SRC_IN);
    }

    public Channel getChannelInstance() {
        return channelInstance;
    }

    public void setChannelInstance(Channel channelInstance) {
        this.channelInstance = channelInstance;
        lblTitle.setText(channelInstance.getProgram());
        updateFavStatus(channelInstance.getIsFav());
        try (InputStream ims = getContext().getAssets().open(ActivitySwitchedOn.channelIconFilenames.get(channelInstance.getProgram()))) {
            Drawable d = Drawable.createFromStream(ims, null);
            bgIcon.setImageDrawable(d);
        } catch (IOException | IllegalArgumentException e) {
            Log.e("createBackgroundIcon", e.getMessage());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // quadratisch machen
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
