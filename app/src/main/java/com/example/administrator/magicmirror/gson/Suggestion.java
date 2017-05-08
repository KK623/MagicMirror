package com.example.administrator.magicmirror.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/5/8.
 */
public class Suggestion {
    @SerializedName("drsg")
    public Dressing dressing;
    public class Dressing{
        @SerializedName("txt")
        public String info;
    }

    @SerializedName("uv")
    public UV uv;
    public  class UV{
        @SerializedName("txt")
        public String info;
    }

    @SerializedName("flu")
    public Flu flu;
    public class Flu{
        @SerializedName("txt")
        public String info;
    }
}
