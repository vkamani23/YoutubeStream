package com.streamthis;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class listofvideos {
@SerializedName("videos")
@Expose
List<String> videos= new ArrayList<>();
}
