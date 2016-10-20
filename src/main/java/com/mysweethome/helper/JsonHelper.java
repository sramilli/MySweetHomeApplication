package com.mysweethome.helper;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonHelper {
	
	  public static String readField(String aJsonStr, String aField) throws IOException {
		    if (aJsonStr != null && aField != null && !"".equals(aJsonStr) && !"".equals(aField)) {
				ObjectNode object = new ObjectMapper().readValue(aJsonStr, ObjectNode.class);
				JsonNode node = object.get(aField);
				return (node == null ? null : node.textValue());
		    }
		    return null;
	  }
	  
	  public static JsonNode findNode(String aJsonStr, String aField) throws IOException {
		    if (aJsonStr != null && aField != null && !"".equals(aJsonStr) && !"".equals(aField)) {
				ObjectNode object = new ObjectMapper().readValue(aJsonStr, ObjectNode.class);
				JsonNode node = object.get(aField);
				return (node == null ? null : node);
		    }
		    return null;
	  }
	  
	  public static boolean nodePresent(String aJsonStr, String aField) throws IOException {
		  JsonNode node = findNode(aJsonStr, aField);
		  return (node == null ? false : true);
	  }

}
