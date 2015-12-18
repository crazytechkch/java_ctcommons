package com.crazytech.xslt;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.crazytech.db.NameValuePair;
import com.crazytech.db.DBUtil;
import com.crazytech.io.IOUtil;



public class XSLT{
	
	/**
	 * Create the frame.
	 */
	public XSLT() {
		super();
	}
	/**
	 * Transform the XML Source to a String. 
	 * @param strXsl xsl string
	 * @param strXml xml string
	 * @param nvp NameValuePair parameters passed to xsl
	 * @param charSet decoding charset, e.g. ASCII,UTF-8
	 * @return result as String
	 * @throws TransformerException
	 * @throws IOException
	 */
	public static String transform(String strXsl, String strXml, NameValuePair nvp, String charSet) throws TransformerException, IOException {
	    String response = "";
	    InputStream ds = null;
	    ds = new ByteArrayInputStream(strXml.getBytes(charSet));
	    
	    Source xmlSource = new StreamSource(ds);
	    
	    InputStream xs = new ByteArrayInputStream(strXsl.getBytes(charSet));
	    Source xsltSource = new StreamSource(xs);
	    
	    StringWriter writer = new StringWriter();
	    Result result = new StreamResult(writer);
	    TransformerFactory tFactory = TransformerFactory.newInstance();
	    Transformer transformer = tFactory.newTransformer(xsltSource);
	    if (nvp!=null&&nvp.size()>0) {
	    	for (String key : nvp.keySet()) {
	    		transformer.setParameter(key, nvp.get(key));
	    	}
	    }
	    transformer.transform(xmlSource, result);
	    
	    response = writer.toString();
	    
	    ds.close();
	    xs.close();
	    
	    xmlSource = null;
	    xsltSource = null;

	    return response;
	}
	
	public static String getXslFromSQL(DBUtil sqlUtil, String sql, String colName) throws SQLException {
		String xsl = null;
		ResultSet rs = sqlUtil.getResultSet(sql);
		if (rs!=null) {
			while (rs.next()) {
				xsl = rs.getString(colName);
			}
		}
		rs.close();
		sqlUtil.closeConnection();
		if (xsl==null) throw new SQLException("SQL Exception : XSL not found");
		return xsl;
	}
	
	public static String customXSL(String xslBody) {
		return "<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">"
				+"<xsl:template match=\"/\">"+xslBody+"</xsl:template></xsl:stylesheet>";
	}
	
	public static String getTagValue(String xml, String tag) throws TransformerException, IOException {
		return transform(customXSL("<xsl:value-of select=\""+tag+"\"/>"),xml, 
				null, IOUtil.DEFAULT_CHARSET).replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
	}
	
	public static String getTagCopy(String xml, String tag) throws TransformerException, IOException {
		return transform(customXSL("<xsl:copy-of select=\""+tag+"\"/>"),xml, 
				null, IOUtil.DEFAULT_CHARSET).replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
	}
}
