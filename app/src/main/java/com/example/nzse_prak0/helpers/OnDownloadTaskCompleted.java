package com.example.nzse_prak0.helpers;

import org.json.JSONObject;

public interface OnDownloadTaskCompleted {
    void onDownloadTaskCompleted(Boolean success, JSONObject jsonObj);
}
