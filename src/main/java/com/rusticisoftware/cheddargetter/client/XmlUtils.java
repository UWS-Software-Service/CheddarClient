/* Software License Agreement (BSD License)
 * 
 * Copyright (c) 2010-2011, Rustici Software, LLC
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Rustici Software, LLC BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.rusticisoftware.cheddargetter.client;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XmlUtils {
	
	public static String getXmlString (Document xmlDoc) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            StringWriter stringWriter = new StringWriter();
            transformer.transform(
                    new DOMSource(xmlDoc),
                    new StreamResult(stringWriter)
            );
            return stringWriter.toString();
        } catch (TransformerConfigurationException e) {
            throw new IllegalStateException("Could not find plain text transformer", e);
        } catch (TransformerException e) {
            throw new IllegalStateException("Error transforming with plain text transformer", e);
        }
    }
    
    public static Document parseXmlString (String xmlString) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbf.newDocumentBuilder();
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
            Document document = docBuilder.parse(inputStream);
            return document;
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 encoding not found", e);
        }
    }

    
    /// <summary>
    /// Utility function to retrieve typed value of first elem with tag elementName, or defaultVal if not found
    /// </summary>
    /// <param name="parent"></param>
    /// <param name="elementName"></param>
    /// <returns></returns>
    public static Object getNamedElemValue(Element parent, String elementName, Class basicType, Object defaultVal)
    {
        String val = getNamedElemValue(parent, elementName);
        if(val == null){
        	return defaultVal;
        }
        
        try {
        	if(Boolean.class.equals(basicType)){
        		return Boolean.parseBoolean(val);
        	}
        	else if(Integer.class.equals(basicType)){
	        	return Integer.parseInt(val);
	        }
	        else if (Float.class.equals(basicType)){
	        	return Float.parseFloat(val);
	        }
	        else if (Double.class.equals(basicType)){
	        	return Double.parseDouble(val);
	        }
	        else
	        	return val;
        } 
        catch (Exception e){
        	return defaultVal;
        }
    }
    
    /// <summary>
    /// Utility function to retrieve inner text of first elem with tag elementName, or null if not found
    /// </summary>
    /// <param name="parent"></param>
    /// <param name="elementName"></param>
    /// <returns></returns>
    public static String getNamedElemValue(Element parent, String elementName)
    {
        String val = null;
        List<Element> list = getChildrenByTagName(parent, elementName);
        if (list.size() > 0) {
            val = list.get(0).getTextContent();
        }
        return val;
    }

    public static Element getFirstChildByTagName(Node parent, String tagName){
        List<Element> children = getChildrenByTagName(parent, tagName);
        return (children.size() == 0) ? null : children.get(0);
    }
    
    public static List<Element> getChildrenByTagName(Node parent, String tagName){
        ArrayList<Element> elements = new ArrayList<Element>();
        NodeList children = parent.getChildNodes();
        for(int i = 0; i < children.getLength(); i++){
            Node child = children.item(i);
            if(child instanceof Element){
                Element elem = (Element)children.item(i);
                if(tagName.equals(elem.getTagName())){
                    elements.add(elem);
                }
            }
        }
        return elements;
    }

}
