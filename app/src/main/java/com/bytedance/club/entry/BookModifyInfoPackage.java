package com.bytedance.club.entry;

import com.bytedance.club.entry.BasePackageBean;
import com.bytedance.club.entry.BookModifyInfoResult;
import com.google.gson.annotations.SerializedName;

public class BookModifyInfoPackage extends BasePackageBean {


    @SerializedName("ResultData")
    private BookModifyInfoResult result;

    public BookModifyInfoResult getResult() {
        return result;
    }

    public void setResult(BookModifyInfoResult result) {
        this.result = result;
    }
}
