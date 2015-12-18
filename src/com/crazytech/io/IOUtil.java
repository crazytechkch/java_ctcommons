package com.crazytech.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class IOUtil {
	public static final String DEFAULT_CHARSET = "UTF-8";
	
	/**
	 * Writes to a file. uses default charset UTF-8. <br/>
	 * If file is not empty, contents will not be overwritten. New contents are written after previous contents.
	 * @param line contents to be written
	 * @param outFile complete path of the output file, e.g. C://folder a/folder b/file.ext
	 */
	public static void writeFile(String line, String outFile) throws IOException{
		writeFile(line, outFile, DEFAULT_CHARSET);
	}
	
	/**
	 * Writes to a file. User defines encoding method <br/>
	 * If file is not empty, contents will not be overwritten. New contents are written after previous contents.
	 * @param line contents to be written
	 * @param outFile complete path of the output file, e.g. C://folder a/folder b/file.ext
	 * @param charSet e.g. ASCII,UTF-8,...
	 */
	public static void writeFile(String line, String outFile, String charSet) throws IOException{
		String prevText;
		prevText = readFile(outFile, charSet);
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),charSet));
		out.write(prevText+line);
		out.newLine();
		out.close();
	}
	
	/**
	 * Reads a file and return it as a String, uses default charset UTF-8
	 * @param fileToRead source file to be read
	 * @return content of file as a String
	 * @throws IOException
	 */
	public static String readFile(String fileToRead) throws IOException {
		return readFile(fileToRead, DEFAULT_CHARSET);
	}
	
	/**
	 * Reads a file and return it as a String, user defines decoding charset
	 * @param fileToRead source file to be read
	 * @param charSet e.g. ASCII, UTF-8, ...
	 * @return content of file as a String
	 * @throws IOException
	 */
	public static String readFile(String fileToRead, String charSet) throws IOException {
		File file = new File(fileToRead);
		String text = "";
		if (file.exists()) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(fileToRead), charSet));
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
	
	/**
	 * Overwrites the output file, uses default charset UTF-8 
	 * @param line contents to be written
	 * @param outFile complete path of the output file, e.g. C://folder a/folder b/file.exe
	 * @see IOUtil#readFile(String)
	 * @throws IOException
	 */
	public static void overwriteFile(String line, String outFile) throws IOException {
		overwriteFile(line, outFile, DEFAULT_CHARSET);
	}
	
	/**
	 * Overwrites the output file, user defines encoding charset
	 * @param line contents to be written to output file
	 * @param outFile complete path of the output file, e.g. C://folder a/folder b/file.exe
	 * @param charSet e.g. ASCII,UTF-8,...
	 * @see IOUtil#readFile(String, String)
	 * @throws IOException
	 */
	public static void overwriteFile(String line, String outFile, String charSet) throws IOException{
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),charSet));
		out.write(line);
		out.newLine();
		out.close();
	}
}
