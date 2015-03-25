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
					JsonObject item = data.getJsonObject(i);
					jsonString += "{\"shortname\" : \"" + item.getString("name") + "\",";
					jsonString += "\"id\" : " + item.getString("id") + ",";
					jsonString += "\"brand\" : \"" + item.getString("brand") + "\",";
					jsonString += "\"description\" : \"" + item.getString("description") + "\",";
					jsonString += "\"price\" : " + item.getInt("price") + "},";
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
			JsonObject jsonObject1 = jsonReader.readObject();
			jsonReader.close();
			fis.close();
			JsonArray data = jsonObject1.getJsonArray("data");
			
			for(int i = 0; i < data.size(); i++) {
				JsonObject item = data.getJsonObject(i);
				if(shortname.equalsIgnoreCase(item.getString("name")))
				{
					jsonString += "{\"shortname\" : \"" + item.getString("name") + "\",";
					jsonString += "\"id\" : " + item.getString("id") + ",";
					jsonString += "\"brand\" : \"" + item.getString("brand") + "\",";
					jsonString += "\"description\" : \"" + item.getString("description") + "\",";
					jsonString += "\"price\" : " + item.getInt("price") + "}";
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsonString;
		
	}
	
	@POST
	@Consumes({"text/xml"})
	//In poster, doe je een POST, content to sent plak je het voorbeeld XML, content type niet vergeten (text/xml)
	public void processFromXML(String productJson) throws IOException {
		
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
				
				// unmarshal new product
				JAXBContext jaxbContext2 = JAXBContext.newInstance(Product.class);
				Unmarshaller jaxbUnmarshaller2 = jaxbContext2.createUnmarshaller();
				StringReader reader = new StringReader(productJson);
				Product newProduct = (Product)jaxbUnmarshaller2.unmarshal(reader);
				
				// add product to existing product list 
				// and update list of products in  productsXML
				int size = data.size();
				
				
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} 
		catch (JAXBException e) {
		   e.printStackTrace();
		}
	}
}