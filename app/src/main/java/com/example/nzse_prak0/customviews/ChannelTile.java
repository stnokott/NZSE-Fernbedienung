package com.example.nzse_prak0.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.widget.TextViewCompat;

import com.example.nzse_prak0.Channel;
import com.example.nzse_prak0.R;
import com.example.nzse_prak0.helpers.Units;

public class ChannelTile extends ConstraintLayout {
    private Channel channelInstance;
    private TextView lblTitle;
    private TextView lblBg;
    private ImageButton btnFav;

    public ChannelTile(Context context, String bgNum, int bgColor) {
        super(context);

        setId(View.generateViewId());
        initLayoutParams();
        createBackgroundLabel(bgNum, bgColor);
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

    private void createBackgroundLabel(String text, int bgColor) {
        TextView newlblBg = new TextView(getContext());
        newlblBg.setId(View.generateViewId());
        newlblBg.setText(text);

        // Layout
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                LayoutParams.MATCH_CONSTRAINT,
                LayoutParams.MATCH_CONSTRAINT
        );
        params.startToStart = ConstraintSet.PARENT_ID;
        params.endToEnd = ConstraintSet.PARENT_ID;
        params.topToTop = ConstraintSet.PARENT_ID;
        params.bottomToBottom = ConstraintSet.PARENT_ID;
        newlblBg.setLayoutParams(params);

        // Weitere Parameter
        float[] hsv = new float[3];
        Color.colorToHSV(bgColor, hsv);
        hsv[2] *= 0.8f; // abdunkeln
        newlblBg.setTextColor(Color.HSVToColor(hsv));
        TextViewCompat.setAutoSizeTextTypeWithDefaults(newlblBg, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        newlblBg.setTextAlignment(TEXT_ALIGNMENT_CENTER);

        addView(newlblBg);
        lblBg = newlblBg;
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
        newbtnFav.setImageResource(R.drawable.star_fill_white_anim);
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
            btnFav.setImageResource(R.drawable.star_fill_reverse_white_anim);
        } else {
            btnFav.setImageResource(R.drawable.star_fill_white_anim);
        }
    }

    public void setBgNum(String n) {
        lblBg.setText(n);
    }

    public void setColor(int c) {
        setBackgroundColor(c);
        float[] hsv = new float[3];
        Color.colorToHSV(c, hsv);
        hsv[2] *= 0.85f;
        lblBg.setTextColor(Color.HSVToColor(hsv));
    }

    public Channel getChannelInstance() {
        return channelInstance;
    }

    public void setChannelInstance(Channel channelInstance) {
        this.channelInstance = channelInstance;
        lblTitle.setText(channelInstance.getProgram());
        updateFavStatus(channelInstance.getIsFav());

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // quadratisch machen
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
