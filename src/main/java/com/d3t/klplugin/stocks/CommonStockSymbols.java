package com.d3t.klplugin.stocks;

import java.io.File;
import java.util.ArrayList;

import com.d3t.klplugin.FileUtil;
import com.d3t.klplugin.KLPlugin;

public class CommonStockSymbols {
	
	public static ArrayList<String> commonSymbols = new ArrayList<String>();
	
	public static void save() {
		try {
			FileUtil f = new FileUtil();
			f.SetArrayList("symbols", commonSymbols);
			f.Save("", "commonsymbols.txt");
		} catch(Exception e) {
			System.out.println("failed to save stock symbols list!");
			e.printStackTrace();
		}
	}

	public static void load() {
		try {
			commonSymbols.clear();
			File file = new File(KLPlugin.getDataFolderPath(), "commonsymbols.txt");
			FileUtil f = FileUtil.createFromFile(file);
			for(String s : f.GetArray("symbols")) commonSymbols.add(s);
		} catch(Exception e) {
			System.out.println("failed to load stock symbols list!");
			e.printStackTrace();
		}
	}
}