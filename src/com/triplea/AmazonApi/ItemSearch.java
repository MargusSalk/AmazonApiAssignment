package com.triplea.AmazonApi;

/**********************************************************************************************
 * Copyright 2009 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file 
 * except in compliance with the License. A copy of the License is located at
 *
 *       http://aws.amazon.com/apache2.0/
 *
 * or in the "LICENSE.txt" file accompanying this file. This file is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under the License. 
 *
 * ********************************************************************************************
 *
 *  Amazon Product Advertising API
 *  Signed Requests Sample Code
 *
 *  API Version: 2009-03-31
 *
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.triplea.Item;

/*
 * This class shows how to make a simple authenticated ItemLookup call to the
 * Amazon Product Advertising API.
 * 
 * See the README.html that came with this sample for instructions on
 * configuring and running the sample.
 */
public class ItemSearch {
	/*
	 * Your AWS Access Key ID, as taken from the AWS Your Account page.
	 */
	private static final String AWS_ACCESS_KEY_ID = "";

	/*
	 * Your AWS Associate Tag
	 */
	private static final String AWS_ASSOCIATE_TAG = "";

	/*
	 * Your AWS Secret Key corresponding to the above ID, as taken from the AWS
	 * Your Account page.
	 */
	private static final String AWS_SECRET_KEY = "";

	/*
	 * Use one of the following end-points, according to the region you are
	 * interested in:
	 * 
	 * US: ecs.amazonaws.com CA: ecs.amazonaws.ca UK: ecs.amazonaws.co.uk DE:
	 * ecs.amazonaws.de FR: ecs.amazonaws.fr JP: ecs.amazonaws.jp
	 * 
	 */
	private static final String ENDPOINT = "webservices.amazon.com";

	public static ArrayList<Item> getItems(String keyword, int itemPage) {
		/*
		 * Set up the signed requests helper
		 */
		SignedRequestsHelper helper;
		try {
			helper = SignedRequestsHelper.getInstance(ENDPOINT, AWS_ACCESS_KEY_ID, AWS_SECRET_KEY);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		String requestUrl = null;
		ArrayList<Item> items = null;

		/*
		 * The helper can sign requests in two forms - map form and string form
		 */

		/*
		 * Here is an example in map form, where the request parameters are
		 * stored in a map.
		 */
		Map<String, String> params = new HashMap<String, String>();
		params.put("Service", "AWSECommerceService");
		params.put("AssociateTag", AWS_ASSOCIATE_TAG);
		params.put("Operation", "ItemSearch");
		params.put("SearchIndex", "All");
		params.put("ItemPage", itemPage + "");
		params.put("ResponseGroup", "ItemAttributes,OfferFull");
		params.put("Keywords", keyword);

		requestUrl = helper.sign(params);

		items = fetchItems(requestUrl);
		return items;

	}

	private static ArrayList<Item> fetchItems(String requestUrl) {
		ArrayList<Item> items = new ArrayList<Item>();
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(requestUrl);
			NodeList itemElements = doc.getElementsByTagName("Item");
			for (int i = 0; i < itemElements.getLength(); i++) {
				Element el = (Element) itemElements.item(i);
				Node titleNode = el.getElementsByTagName("Title").item(0);
				String title = "";
				String price = "";
				String currency = "";
				title = titleNode.getTextContent();
				Node urlNode = el.getElementsByTagName("DetailPageURL").item(0);
				String url = urlNode.getTextContent();
				Node groupNode = el.getElementsByTagName("ProductGroup").item(0);
				String productGroup = groupNode.getTextContent();
				try {
					Node priceNode = el.getElementsByTagName("LowestNewPrice").item(0);
					Node priceElement = priceNode.getChildNodes().item(2);
					Node currencyElement = priceNode.getChildNodes().item(1);
					price = priceElement.getTextContent();
					currency = currencyElement.getTextContent();
				} catch (NullPointerException e) {
					// There sometimes is only a Price element.
					try {
						Node priceNode = el.getElementsByTagName("Price").item(0);
						Node priceElement = priceNode.getChildNodes().item(2);
						Node currencyElement = priceNode.getChildNodes().item(1);
						price = priceElement.getTextContent();
						currency = currencyElement.getTextContent();
					} catch (NullPointerException e1) {
						// If the product has no Price or LowestNewPrice available through the
						// API, it will be displayed as "-"
						// It's mostly ebooks.

						price = "0.00";
						currency = "USD";
					}

				}
				items.add(new Item(price, title, currency, productGroup, url));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return items;
	}

}
