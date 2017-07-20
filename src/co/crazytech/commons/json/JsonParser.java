package co.crazytech.commons.json;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonParser {
	private String url;
	
	public JsonParser(String url) {
		super();
		this.url = url;
		// TODO Auto-generated constructor stub
	}
	
	public JSONObject parse() throws UnknownHostException, MalformedURLException, ParseException, IOException{
		Object obj = new JSONParser().parse(getJsonFromUrl(url));
		JSONObject jsonObj = (JSONObject) obj;
		return jsonObj;
	}

	public String getJsonFromUrl(String url) throws UnknownHostException, MalformedURLException, IOException{
		InputStream in;
		String jsonText = "";
		in = new URL(url).openStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
		jsonText = readJson(br);
		return jsonText;
	}
	
	private String readJson(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	}
}
