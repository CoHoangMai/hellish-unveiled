package com.hellish.map;

public enum MapType {
	MAP_1("map/map1.tmx"),
	MAP_2("map/map2.tmx");
	
	private String filePath;

	MapType(final String filePath) {
		this.filePath = filePath;
	}
	
	public String getFilePath() {
		return filePath;
	}
}
