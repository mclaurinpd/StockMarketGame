package com.example.phillip.stockmarketgame;

import java.io.*;
import java.net.*;

/**
 * Created by Phillip on 4/24/2017.
 */

public class Stock {

        private String symbol; // the stock's symbol as given by the NYSE
        private float buyPrice; // the price the player purchased the stock at
        private boolean owns = false; // true if the player owns the stock.  false otherwise
        private int numberOwned; // the number of stocks the player owns

        private final String baseURL = "http://query.yahooapis.com/v1/public/yql?q=";

        // a basic constructor that initializes the attributes of a stock
        // this constructor will be used whenever the game loads a player's portfolio
        public Stock (String symbol, float buyPrice, boolean owns, int numberOwned) {
            this.symbol = symbol;
            this.buyPrice = buyPrice;
            this.owns = owns;
            this.numberOwned = numberOwned;
        }

        // a method to query the yahoo API and get the current price of a stock
        public float getPrice() {
            try {
                String query = "select * from csv where url='http://download.finance.yahoo.com/d/quotes.csv?s=" + symbol
                        + "&f=sl1d1t1c1ohgv&e=.csv' and columns='symbol,price'";
                String fullURL = baseURL + URLEncoder.encode(query, "UTF-8") + "&format=json";
                URL url = new URL(fullURL);
                InputStream is = url.openStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String str = reader.readLine();
                str = str.substring(str.indexOf("<price>") + 1);
                str = str.substring(0, str.indexOf(0, str.indexOf("</price>")));

                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

}
