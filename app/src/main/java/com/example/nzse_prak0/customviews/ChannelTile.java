// TODO: Statt ConstraintLayout RecyclerView mit GridLayoutManager verwenden

package com.example.nzse_prak0.customviews;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.example.nzse_prak0.R;
import com.example.nzse_prak0.helpers.Units;

public class ChannelTile extends ConstraintLayout {
    private ImageButton btnFav;
    private boolean isFav = false;

    public ChannelTile(Context context, String title, String bgNum, int bgColor) {
        super(context);

        setId(View.generateViewId());
        initLayoutParams();
        createBackgroundLabel(bgNum, bgColor);
        createFavButton();
        createTitleLabel(title);
        setBackgroundColor(bgColor);
    }

    private void initLayoutParams() {
        // set ConstraintLayout Parameters
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_CONSTRAINT,
                LayoutParams.MATCH_CONSTRAINT
        );
        params.bottomToBottom = ConstraintSet.PARENT_ID;
        params.topToTop = ConstraintSet.PARENT_ID;
        params.dimensionRatio = "1";
        int px = Units.dpToPx(8, getContext()); // margins
        params.setMargins(px, px, px, px);
        setLayoutParams(params);
    }

    private void createBackgroundLabel(String text, int bgColor) {
        TextView lblBg = new TextView(getContext());
        lblBg.setId(View.generateViewId());
        lblBg.setText(text);

        // Layout
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                LayoutParams.MATCH_CONSTRAINT,
                LayoutParams.MATCH_CONSTRAINT
        );
        params.startToStart = ConstraintSet.PARENT_ID;
        params.endToEnd = ConstraintSet.PARENT_ID;
        params.topToTop = ConstraintSet.PARENT_ID;
        params.bottomToBottom = ConstraintSet.PARENT_ID;
        lblBg.setLayoutParams(params);

        // Weitere Parameter
        float[] hsv = new float[3];
        Color.colorToHSV(bgColor, hsv);
        hsv[2] *= 0.8f; // abdunkeln
        lblBg.setTextColor(Color.HSVToColor(hsv));
        lblBg.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        lblBg.setTextAlignment(TEXT_ALIGNMENT_CENTER);

        addView(lblBg);
    }

    private void createTitleLabel(String title) {
        TextView lblTitle = new TextView(getContext());
        lblTitle.setId(View.generateViewId());
        lblTitle.setText(title);

        // Layout
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                LayoutParams.MATCH_CONSTRAINT,
                LayoutParams.WRAP_CONTENT
        );
        params.startToStart = ConstraintSet.PARENT_ID;
        params.endToEnd = ConstraintSet.PARENT_ID;
        params.bottomToBottom = ConstraintSet.PARENT_ID;
        lblTitle.setLayoutParams(params);

        int px = Units.dpToPx(8, getContext());
        lblTitle.setPadding(px, px, px, px);

        // Weitere Parameter
        lblTitle.setTextAlignment(TEXT_ALIGNMENT_CENTER);
        lblTitle.setTextColor(Color.WHITE);
        lblTitle.setBackgroundColor(0x26000000);

        addView(lblTitle);
    }

    private void createFavButton() {
        ImageButton btnFav = new ImageButton(getContext());
        btnFav.setId(View.generateViewId());

        // Layout
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_CONSTRAINT
        );
        params.endToEnd = ConstraintSet.PARENT_ID;
        params.topToTop = ConstraintSet.PARENT_ID;
        int px = Units.dpToPx(8, getContext()); // margins
        params.setMargins(0, px, px, 0);
        btnFav.setLayoutParams(params);

        btnFav.setPadding(0, 0, 0, 0);
        btnFav.setScaleType(ImageView.ScaleType.FIT_CENTER);
        // TODO: statt manueller Skalierung höherskaliertes Icon verwenden
        px = Units.dpToPx(32, getContext());
        btnFav.setMinimumHeight(px);
        btnFav.setMinimumWidth(px);

        // weitere Parameter
        btnFav.setElevation(Units.dpToPx(3, getContext()));
        btnFav.setOutlineProvider(null); // Schatten durch Elevation verhindern
        btnFav.setImageResource(R.drawable.star_fill_white_anim);
        btnFav.setBackground(null);

        // Listener
        btnFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFavButton();
            }
        });

        addView(btnFav);
        this.btnFav = btnFav;
    }

    public void toggleFavButton() {
        if (isFav) {
            btnFav.setImageResource(R.drawable.star_fill_reverse_white_anim);
        } else {
            btnFav.setImageResource(R.drawable.star_fill_white_anim);
        }

        Animatable animatable = (Animatable) btnFav.getDrawable();
        animatable.start();

        isFav = !isFav;
    }
}
