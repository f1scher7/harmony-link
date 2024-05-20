package com.harmonylink.harmonylink.utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public final class JSONUtil {

    public static List<Integer> getIntegerListFromJSON(JSONArray jsonArray) {
        List<Integer> list = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.optInt(i));
            }
        }

        return list;
    }

    public static List<String> getStringListFromJSON(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();

        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                list.add(jsonArray.optString(i));
            }
        }

        return list;
    }

}
