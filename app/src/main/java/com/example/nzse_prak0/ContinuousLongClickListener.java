package com.example.nzse_prak0;

import android.os.Handler;
import android.os.Message;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

public class ContinuousLongClickListener implements View.OnTouchListener, View.OnLongClickListener {
    private static final int WHAT = 264776257;
    private final View.OnLongClickListener onLongClickListener;
    private final Handler handler;
    private int delay;

    ContinuousLongClickListener(View view, View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
        delay = 200;
        handler = new Handler();
        view.setOnLongClickListener(this);
        view.setOnTouchListener(this);
    }

    @Override
    public boolean onLongClick(final View view) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                onLongClickListener.onLongClick(view);

                Message message = Message.obtain(handler, this);
                message.what = WHAT;
                handler.sendMessageDelayed(message, delay);
            }
        });
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            handler.removeMessages(WHAT);
            v.performClick();
        }
        return false;
    }
}
