package com.ta.bean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;


public class RunNanoServer {
	
	
	public void doNanoServerStart(String fileLOCATION,String fileTagged) {
		System.out.print("NanoServer Start...");
		PrintWriter tagged;
		try {
			tagged = new PrintWriter(new File(fileTagged));
			
			ProcessBuilder pb = new ProcessBuilder("java", "-server", "-Xmx4g", "-jar", fileLOCATION+"blazegraph.jar");

			Process p;

			p = pb.start();

			String aLine = "";
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((aLine = input.readLine()) != null) {
				tagged.write(aLine.trim() + "\n");
			}
			input.close();
			tagged.flush();
			System.out.print("Server started");
		} catch (FileNotFoundException e) {
			System.out.print(e.getMessage());
		} catch (IOException e) {
			System.out.print(e.getMessage());
		}
	}
	
	public void doNanoServerStart_Unix(String fileLOCATION,String fileTagged) {
		System.out.print("NanoServer Start...");
		PrintWriter tagged;
		try {
			tagged = new PrintWriter(new File(fileTagged));
			
			ProcessBuilder pb = new ProcessBuilder("java -server -Xmx4g -jar "+ fileLOCATION+"blazegraph.jar");

			
			Process p;

			p = pb.start();

			String aLine = "";
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((aLine = input.readLine()) != null) {
				tagged.write(aLine.trim() + "\n");
			}
			input.close();
			tagged.flush();
		} catch (FileNotFoundException e) {
			System.out.print("unix++"+e.getMessage());
		} catch (IOException e) {
			System.out.print("unix++"+e.getMessage());
		}
	}
}
