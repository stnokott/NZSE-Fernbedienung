package com.example.nzse_prak0.helpers;

import org.json.JSONObject;

public interface OnChannelScanCompleted {
    void onChannelScanCompleted(Boolean success, JSONObject jsonObj);
}
