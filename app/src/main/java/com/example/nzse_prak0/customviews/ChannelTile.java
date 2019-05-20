package com.example.nzse_prak0.customviews;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.nzse_prak0.R;
import com.example.nzse_prak0.helpers.Units;

public class ChannelTile extends ConstraintLayout {
    private TextView lblBg, lblTitle;
    private ImageButton btnFav;

    public ChannelTile(Context context, String title, String bgNum, int bgColor) {
        super(context);

        initLayoutParams();
        createBackgroundLabel(bgNum);
        createTitleLabel(title);
        createFavButton();
        setBackgroundColor(bgColor);
    }

    private void initLayoutParams() {
        // set ConstraintLayout Parameters
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                LayoutParams.MATCH_CONSTRAINT,
                LayoutParams.MATCH_CONSTRAINT
        );
        params.dimensionRatio = "1";
        int px = Units.dpToPx(8, getContext()); // margins
        params.setMargins(px, px, px, px);
        setLayoutParams(params);
    }

    private void createBackgroundLabel(String text) {
        lblBg = new TextView(getContext());
        lblBg.setText(text);

        // Layout
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                LayoutParams.MATCH_CONSTRAINT,
                LayoutParams.MATCH_CONSTRAINT
        );
        lblBg.setLayoutParams(params);

        ConstraintSet cset = new ConstraintSet();
        cset.connect(lblBg.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        cset.connect(lblBg.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        cset.connect(lblBg.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        cset.connect(lblBg.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        cset.applyTo(this);

        // Weitere Parameter
        lblBg.setAlpha(0.2f);
        lblBg.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        lblBg.setTextAlignment(TEXT_ALIGNMENT_CENTER);
    }

    private void createTitleLabel(String title) {
        lblTitle = new TextView(getContext());
        lblTitle.setText(title);

        // Layout
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                LayoutParams.MATCH_CONSTRAINT,
                LayoutParams.WRAP_CONTENT
        );
        lblTitle.setLayoutParams(params);

        ConstraintSet cset = new ConstraintSet();
        cset.connect(lblTitle.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        cset.connect(lblTitle.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        cset.connect(lblTitle.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        cset.applyTo(this);

        int px = Units.dpToPx(8, getContext());
        lblTitle.setPadding(px, px, px, px);

        // Weitere Parameter
        lblTitle.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        lblTitle.setTextColor(Color.WHITE);
        lblTitle.setBackgroundColor(0x26000000);
    }

    private void createFavButton() {
        btnFav = new ImageButton(getContext());

        // Layout
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
        );
        int px = Units.dpToPx(8, getContext()); // margins
        params.setMargins(px, px, px, px);
        btnFav.setLayoutParams(params);

        ConstraintSet cset = new ConstraintSet();
        cset.connect(btnFav.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        cset.connect(btnFav.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        cset.applyTo(this);

        // weitere Parameter
        btnFav.setElevation(Units.dpToPx(3, getContext()));
        btnFav.setOutlineProvider(null); // Schatten durch Elevation verhindern
        btnFav.setImageResource(R.drawable.ic_star_border_white_36dp);
    }
}
