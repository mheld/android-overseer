package com.overseer.models;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.csvreader.CsvReader;

import android.content.Context;
import android.content.res.AssetManager;

public class Purchase {
	private String description;
	private String price;
	private Date transactionDate;
	private Date postDate;
	
	private Purchase(){}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}
	
	@Override
	public String toString(){
		return "description: " + getDescription() +
				"\nprice: " +  getPrice() +
				"\ntransDate: " + getTransactionDate() + 
				"\npostDate: " + getPostDate();
	}

	public static List<Purchase> all(Context context){
		List<Purchase> purchases = new ArrayList<Purchase>();
		AssetManager assets = context.getAssets();
        CsvReader products = null;
        
		try {
            Purchase tempPurchase;
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
            products = new CsvReader(assets.open("ActivityExample.csv"), Charset.defaultCharset());

            products.readHeaders();

            while (products.readRecord()){
            	try{
            		products.get("Description");
            	}catch(IOException e){
            		//we've hit a blank line
            		break;
            	}
            	tempPurchase = new Purchase();
            	
            	tempPurchase.setDescription(products.get("Description"));
            	tempPurchase.setPrice(products.get("Amount"));
            	tempPurchase.setTransactionDate(sdf.parse(products.get("Trans Date")));
            	tempPurchase.setPostDate(sdf.parse(products.get("Post Date")));
            	
            	purchases.add(tempPurchase);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
        	products.close();
        }
		
		return purchases;
	}
}
