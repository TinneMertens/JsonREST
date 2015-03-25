package edu.ap.jaxrs;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlRootElement;

public class ProductsJson {

	private ArrayList<Product> products;
	
	public ArrayList<Product> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<Product> products) {
		this.products = products;
	}
}
