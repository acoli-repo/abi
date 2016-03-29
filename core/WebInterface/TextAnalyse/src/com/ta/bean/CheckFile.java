package com.ta.bean;

import java.io.File;

public class CheckFile {

	public boolean isOutputSucessed(String filePathString) {
		File f = new File(filePathString);
		if (f.exists() && !f.isDirectory()) {
			return true;
		}
		return false;
	}
	
}
