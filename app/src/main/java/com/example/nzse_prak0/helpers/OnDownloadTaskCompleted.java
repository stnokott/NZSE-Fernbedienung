package com.example.nzse_prak0.helpers;

import org.json.JSONObject;

public interface OnDownloadTaskCompleted {
    void onDownloadTaskCompleted(int requestCode, Boolean success, JSONObject jsonObj);
}
