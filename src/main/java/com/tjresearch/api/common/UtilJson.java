package com.tjresearch.api.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class UtilJson {

	public static Map<String, Object> toMap(String paramJson) {
		Map<String,Object> map = new HashMap<String,Object>();
		JSONObject jsonObject = (JSONObject) JSON.parse(paramJson);
		Set<Entry<String, Object>> entrySet = jsonObject.entrySet();
		for (Entry<String, Object> entry : entrySet) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}

	public static String writeValueAsString(Object result) {
		JSONObject jsonObject = (JSONObject) JSON.toJSON(result);
		return jsonObject.toJSONString();
	}

}
