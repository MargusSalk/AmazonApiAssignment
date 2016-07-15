package com.triplea;

import java.util.ArrayList;

import com.triplea.AmazonApi.ItemSearch;

public class ItemController {

	// Characteristic of the application, how many items should the app display
	// on one page (double for more accurate calculations)
	private static final double ITEMS_PER_PAGE = 13.0;
	// amount of items Amazon Product Advertisement API can display at once.
	private static final int ITEMS_PER_API_PAGE = 10;

	/*
	 * The first request is without page number and loads page #1 as default. In
	 * addition to page #1 it returns items from page #2 for preloading the next
	 * page.
	 * 
	 */
	public static ArrayList<Item> getItems(String keyword) {
		ArrayList<Item> items = new ArrayList<>();
		addItems(items, 1, keyword);
		addItems(items, 2, keyword);
		return items;
	}

	public static ArrayList<Item> getItems(String keyword, int page) {
		ArrayList<Item> items = new ArrayList<>();
		addItems(items, page, keyword);
		return items;
	}

	// Fetches items from API for the given page and keyword
	private static void addItems(ArrayList<Item> items, int page, String keyword) {
		for (Integer integer : pagesRequired(page)) {
			items.addAll(ItemSearch.getItems(keyword, integer.intValue()));
		}
	}

	/*
	 * The application assumes that if the users requests page 3 then the
	 * application already has pages 1 and 2.
	 * 
	 * The function returns array of pages needed from the API to render the
	 * page given as a parameter.
	 * 
	 * The function won't work when ITEMS_PER_PAGE is 20.
	 */
	private static ArrayList<Integer> pagesRequired(int pageNumber) {
		double numberOfItemsNeeded = pageNumber * ITEMS_PER_PAGE;
		double itemsAlreadyLoaded = (pageNumber - 1) * ITEMS_PER_PAGE;
		double firstItemNeeded = itemsAlreadyLoaded + 1;
		int firstPageNeeded;
		if (pageNumber == 1 || (ITEMS_PER_PAGE % 10 == 0)) {
			firstPageNeeded = (int) Math.ceil(firstItemNeeded / ITEMS_PER_API_PAGE);
		} else
			firstPageNeeded = (int) Math.ceil(firstItemNeeded / ITEMS_PER_API_PAGE) + 1;

		int lastPageNeeded = (int) Math.ceil((numberOfItemsNeeded / ITEMS_PER_API_PAGE));
		// no duplicate pages
		ArrayList<Integer> pages = new ArrayList<Integer>();
		if (firstPageNeeded == lastPageNeeded) {
			pages.add(firstPageNeeded);
			return pages;
		}

		// Only needed for flexible ITEMS_PER_PAGE constant... start - ...

		// smaller values of ITEMS_PER_PAGES have last page < first page
		if (lastPageNeeded < firstPageNeeded) {
			pages.add(lastPageNeeded);
			return pages;
		}

		if ((lastPageNeeded - firstPageNeeded) > 1) {
			for (int i = firstPageNeeded; i <= lastPageNeeded; i++) {
				pages.add(i);
			}
			return pages;
		}

		// ... - end

		pages.add(firstPageNeeded);
		pages.add(lastPageNeeded);
		return pages;
	}
}
