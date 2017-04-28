package com.example.phillip.stockmarketgame;

import java.io.*;
import java.net.*;

/**
 * @author Phillip McLaurin
 * @version 1.0
 * This purpose of this class is to hold information about a specific stock.
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

    /**
     *
     * @return price the current stock market price of the stock. returns -1 if there is an error
     */
    public float getPrice() {
            // set the price to negative one.
            // if it returns -1 we know the query didn't work
            float price = -1;
            try {
                // our baseURL holds the url of the database we are querying
                String baseURL = "http://query.yahooapis.com/v1/public/yql?q=";
                // the query string holds the query
                // we concatenate query with the stock symbol
                String query = "select * from csv where url='http://download.finance.yahoo.com/d/quotes.csv?s=" + symbol
                        + "&f=sl1d1t1c1ohgv&e=.csv' and columns='symbol,price,date,time,change,col1,high,low,col2'";
                // then we encode all of it as UTF-8 and request json format
                String fullURL = baseURL + URLEncoder.encode(query, "UTF-8") + "&format=json";
                // convert this string in a url
                URL url = new URL(fullURL);
                // open an inputstream with this url
                InputStream is = url.openStream();

                // the buffered reader below reads the input stream
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                // read the entire stream into one string and then extract the price from the stering
                String str = reader.readLine();
                // the price is preceded by "price": which is 8 chars long, so we add 9 to the index
                // the price is succeeded by ","date so that is the substrings ending index
                str = str.substring(str.indexOf("\"price\":")+9, str.indexOf("\",\"date"));
                // parse the string in a float
                price = Float.parseFloat(str);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return price;
        }

    /**
     *
     * @return buyPrice the stock the price was purchased at
     */
    public float getBuyPrice() {
            return buyPrice;
    }

    /**
     *
     * @return symbol the symbol of the stock
     */
    public String getSymbol() {
            return symbol;
    }

    /**
     *
     * @return numberOwned returns the number of stocks owned by the player.
     */
    public int getNumberOwned() {
        return numberOwned;
    }

    /**
     *
     * @return owns returns true if the players owns the stock. false if the player does not
     */
    public boolean playerOwns() {
        return owns;
    }

}
