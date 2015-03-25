package edu.ap.jaxrs;

import java.io.*;
import java.util.*;

import javax.enterprise.context.RequestScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.*;
import javax.xml.bind.*;
import javax.xml.transform.stream.StreamSource;

@RequestScoped
@Path("/products")
public class ProductResource {
	
	@GET
	@Produces({"application/json"})
	//in de poster doe je een header toevoegen accept	application/json
	public String getProductsJSON() throws IOException {
		String jsonString = "{\"products\" : [";
		
			File Jsonfile = new File("/Users/Tinne/Desktop/Product.txt");
			
			try {
				InputStream fis = new FileInputStream(Jsonfile);
				JsonReader jsonReader = Json.createReader(fis);
				JsonObject jsonObject = jsonReader.readObject();
				jsonReader.close();
				fis.close();
				JsonArray data = jsonObject.getJsonArray("data");
				
				for(int i = 0; i < data.size(); i++) {
					jsonString += "{\"shortname\" : \"" + data.get(2) + "\",";
					jsonString += "\"id\" : " + data.get(0) + ",";
					jsonString += "\"brand\" : \"" + data.get(3) + "\",";
					jsonString += "\"description\" : \"" + data.get(4) + "\",";
					jsonString += "\"price\" : " + data.get(1) + "},";
				}
				jsonString = jsonString.substring(0, jsonString.length()-1);
				jsonString += "]}";
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return jsonString;
		} 
		
	@GET
	@Path("/{shortname}")
	@Produces({"application/json"})
	public String getProductJSON(@PathParam("shortname") String shortname) throws IOException {
		String jsonString = "";
		
		File Jsonfile = new File("/Users/Tinne/Desktop/Product.txt");
		
		try {
			//All data
			InputStream fis = new FileInputStream(Jsonfile);
			JsonReader jsonReader = Json.createReader(fis);
			JsonObject jsonObject = jsonReader.readObject();
			jsonReader.close();
			fis.close();
			JsonArray data = jsonObject.getJsonArray("data");
			
			for(int i = 0; i < data.size(); i++) {
				if(shortname.equals(data.get(1)))
				jsonString += "{\"shortname\" : \"" + data.get(2) + "\",";
				jsonString += "\"id\" : " + data.get(0) + ",";
				jsonString += "\"brand\" : \"" + data.get(3) + "\",";
				jsonString += "\"description\" : \"" + data.get(4) + "\",";
				jsonString += "\"price\" : " + data.get(1) + "},";
			}
			jsonString = jsonString.substring(0, jsonString.length()-1);
			jsonString += "]}";
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsonString;
		
	}
	
	@GET
	@Path("/{shortname}")
	@Produces({"text/xml"})
	public String getProductXML(@PathParam("shortname") String shortname) {
		String xmlString = "";
		try {
			// get all products
			JAXBContext jaxbContext1 = JAXBContext.newInstance(ProductsJson.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();
			File XMLfile = new File("/Users/Tinne/Desktop/Product.txt");
			ProductsJson productsXML = (ProductsJson)jaxbUnmarshaller.unmarshal(XMLfile);
			ArrayList<Product> listOfProducts = productsXML.getProducts();
			
			// look for the product, using the shortname
			for(Product product : listOfProducts) {
				if(shortname.equalsIgnoreCase(product.getName())) {
					JAXBContext jaxbContext2 = JAXBContext.newInstance(Product.class);
					Marshaller jaxbMarshaller = jaxbContext2.createMarshaller();
					StringWriter sw = new StringWriter();
					jaxbMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
					jaxbMarshaller.marshal(product, sw);
					xmlString = sw.toString();
				}
			}
		} 
		catch (JAXBException e) {
		   e.printStackTrace();
		}
		return xmlString;
	}
	
	@POST
	@Consumes({"text/xml"})
	//In poster, doe je een POST, content to sent plak je het voorbeeld XML, content type niet vergeten (text/xml)
	public void processFromXML(String productXML) {
		
		/* newProductXML should look like this :
		 *  
		 <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
		 <product>
        	<brand>BRAND</brand>
        	<description>DESCRIPTION</description>
        	<id>123456</id>
        	<price>20.0</price>
        	<shortname>SHORTNAME</shortname>
        	<sku>SKU</sku>
		 </product>
		 */
		
		try {
			// get all products
			JAXBContext jaxbContext1 = JAXBContext.newInstance(ProductsJson.class);
			Unmarshaller jaxbUnmarshaller1 = jaxbContext1.createUnmarshaller();
			File XMLfile = new File("/Users/Tinne/Desktop/Product.txt");
			ProductsJson productsXML = (ProductsJson)jaxbUnmarshaller1.unmarshal(XMLfile);
			ArrayList<Product> listOfProducts = productsXML.getProducts();
			
			// unmarshal new product
			JAXBContext jaxbContext2 = JAXBContext.newInstance(Product.class);
			Unmarshaller jaxbUnmarshaller2 = jaxbContext2.createUnmarshaller();
			StringReader reader = new StringReader(productXML);
			Product newProduct = (Product)jaxbUnmarshaller2.unmarshal(reader);
			
			// add product to existing product list 
			// and update list of products in  productsXML
			listOfProducts.add(newProduct);
			productsXML.setProducts(listOfProducts);
			
			// marshal the updated productsXML object
			Marshaller jaxbMarshaller = jaxbContext1.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarshaller.marshal(productsXML, XMLfile);
		} 
		catch (JAXBException e) {
		   e.printStackTrace();
		}
	}
}