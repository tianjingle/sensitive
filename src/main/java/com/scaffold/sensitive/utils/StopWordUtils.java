package com.scaffold.sensitive.utils;

import java.util.ArrayList;
import java.util.List;

public class StopWordUtils {
	
	public static List<String> getStopWord(){
		List<String> words = new ArrayList<String>();
		words.add("!");
		words.add("\"");
		words.add("#");
		words.add("$");
		words.add("%");
		words.add("&");
		words.add("(");
		words.add(")");
		words.add("*");
		words.add(",");
		words.add(".");
		words.add("/");
		words.add(";");
		words.add("?");
		words.add("@");
		words.add("|");
		words.add("，");
		words.add("。");
		words.add("{");
		words.add("}");
		words.add("【");
		words.add("】");
		words.add("；");
		words.add("‘");
		words.add("，");
		words.add("。");
		words.add("、");
		words.add("·");
		words.add("、");
		return words;
	}

}
