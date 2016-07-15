package com.triplea;

public class Item {

	private double price;
	private String title;
	private String currency;
	private String productGroup;
	private String url;

	public Item(String price, String title, String currency, String productGroup, String url) {
		double priceDouble = convertPriceToDouble(price);
		setPrice(priceDouble);
		setTitle(title);
		setCurrency(currency);
		setProductGroup(productGroup);
		setUrl(url);
	}

	private double convertPriceToDouble(String priceString) {
		String price = priceString.substring(1);
		price = price.replace(",", "");
		return Double.valueOf(price);
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double priceDouble) {
		this.price = priceDouble;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getTitle());
		sb.append(" - ");
		sb.append(getPrice());
		sb.append(" - ");
		sb.append(getCurrency());
		return sb.toString();
	}
}
