package com.triplea;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class SearchController {

	@RequestMapping(value = "search/{keywords}", method = RequestMethod.GET)
	@ResponseBody
	public String searchAmazon(@PathVariable String keywords) {
		ArrayList<Item> items = ItemController.getItems(keywords);
		final ObjectMapper mapper = new ObjectMapper();
		String resultJSON = "";
		try {
			resultJSON = mapper.writeValueAsString(items);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "JSON problem";
		}
		return resultJSON;
	}
	
	@RequestMapping(value = "search/{pageNumber}/{keywords}", method = RequestMethod.GET)
	@ResponseBody
	public String searchAmazonAtPage(@PathVariable("pageNumber") String pageNumber, @PathVariable("keywords") String keywords) {
		int page = Integer.valueOf(pageNumber);
		ArrayList<Item> items = ItemController.getItems(keywords, page);
		final ObjectMapper mapper = new ObjectMapper();
		String resultJSON = "";
		try {
			resultJSON = mapper.writeValueAsString(items);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "JSON problem";
		}
		return resultJSON;
	}

}
