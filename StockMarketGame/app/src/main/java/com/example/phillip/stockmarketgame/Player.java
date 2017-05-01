package com.example.pdm3872.a450final;

import android.content.Context;
import android.content.res.AssetManager;

/*import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;*/

import java.util.ArrayList;
import java.util.List;

/**
 * @author Phillip McLaurin
 * @version 1.0
 */

public class Player {
    ArrayList<Stock> portfolio = new ArrayList<Stock>();
    double cash = 0;

    public Player () {
        fillPortfolio();
//        try {
//            HttpClient httpclient = HttpClients.createDefault();
//            HttpPost post = new HttpPost("http://webdev.cislabs.uncw.edu/~mjs9768/stock_api.php");
//            List<NameValuePair> parameters = new ArrayList<NameValuePair>();
//            parameters.add(new BasicNameValuePair("username", "testuser"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    // a test function made just for the in-class demo
    private void fillPortfolio() {
        portfolio.add(new Stock("AAPL", 5.00f, true, 10000));
        portfolio.add(new Stock("NVDA", 16.23f, true, 300));
        portfolio.add(new Stock("TSLA", 34.65f, true, 2000));
        portfolio.add(new Stock("KO", 9.23f, true, 2000));
        cash = 10000;
    }

    /**
     *
     * @param symbol the stock symbol
     * @param num the number the user desires to sell
     * @return
     */
    public boolean sellStock(String symbol, int num) {
        boolean owns = false;
        int index = -1;
        // determine if they own the stock
        for (int i=0; i < portfolio.size(); i++) {
            if (portfolio.get(i).getSymbol().equals(symbol) && portfolio.get(i).getNumberOwned() > num) {
                owns = true;
                index = i;
            }
        }
        // if they don't own it return false
        if (!owns) {
            return false;
        }
        // some basic math to determine how much was made
        float currPrice = portfolio.get(index).getPrice();
        cash += currPrice * num;
        portfolio.get(index).setNumberOwned(portfolio.get(index).getNumberOwned() - num);

        return true;

    }

    /**
     *
     * @param symbol the stock symbol
     * @param num the number the user wishes to purchase
     * @return
     */
    public boolean buyStock(String symbol, int num) {
        try {
            Stock sq = null;
            // see if they already own some stock
            for (int i = 0; i < portfolio.size(); i++) {
                if (portfolio.get(i).getSymbol().equals(symbol)) {
                    sq = portfolio.get(i);
                }
            }
            // if they didn't initialize
            if (sq == null) {
                sq = new Stock(symbol, 0, false, 0);
            }
            // multiply price times number desired
            // if its over the amount of cash, they cannot purchase... return false
            if (sq.getPrice() * num > cash)
                return false;
            // subtract the amount of the stocks*price from cash
            cash = cash - sq.getPrice() * num;
            // add those stocks to portfolio
            Stock purchased = new Stock(symbol, sq.getPrice(), true, num);
            portfolio.add(purchased);
        } catch (Exception e) {
            return false;
        }
        // return true if purchase was successful
        return true;
    }

}