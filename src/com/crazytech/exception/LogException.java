package com.crazytech.exception;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class LogException {
	
	public static String log(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exceptionStr = sw.toString();
		try {
			String prevText = readText();
			BufferedWriter out = new BufferedWriter(new FileWriter("Log.txt"));
			out.write(prevText+exceptionStr);
			out.newLine();
			out.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public static String log(Exception e , String message) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exceptionStr = sw.toString();
		try {
			String prevText = readText();
			BufferedWriter out = new BufferedWriter(new FileWriter("Log.txt"));
			out.write(prevText+message+"\n"+exceptionStr);
			out.newLine();
			out.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	private static String readText() throws IOException {
		File file = new File("Log.txt");
		String text = "";
		if (file.exists()) {
			BufferedReader br = new BufferedReader(new FileReader("Log.txt"));
			try {
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();
				
				while (line != null) {
					sb.append(line);
					sb.append('\n');
					line = br.readLine();
				}
				text = sb.toString();
			} finally {
				br.close();
			}
			
		}
	    return text;
	}
}
