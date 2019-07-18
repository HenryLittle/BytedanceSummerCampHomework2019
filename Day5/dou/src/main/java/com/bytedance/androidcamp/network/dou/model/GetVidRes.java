package com.bytedance.androidcamp.network.dou.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetVidRes {
    @SerializedName("feeds") public List<Video> feeds;
    @SerializedName("success") public boolean success;
}
