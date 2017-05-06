package com.nasdaq.internship;

import java.util.*;

class MyEngine implements MatchingEngine {
    MyEngine() {
    }

    public List<Trade> enterOrder(Order orderNew){
        List<Trade> trades = new ArrayList<>();

        //Place your implementation here

        //An example:

        //Constructing a fake opposite matching order for demo purposes only
        //In the real implementation an opposite matching order should be identified among the orders entered already
        Order orderOppposite = new Order(orderNew.getClient() +
                "_Opposite " + orderNew.getSide().getOppositeSide() + " " +
                orderNew.getStock() + " " +
                Environment.decimalFormat.format(orderNew.getQuantity()) + "@" +
                Environment.decimalFormat.format(orderNew.getPrice()));

        //Constructing a new trade from (orderSell, orderBuy....)
        Trade tradeNew;
        if (orderNew.getSide() == Side.SELL) {
            tradeNew = new Trade(orderNew, orderOppposite, orderNew.getQuantity(), orderNew.getPrice());
        }else{
            tradeNew = new Trade(orderOppposite, orderNew, orderNew.getQuantity(), orderNew.getPrice());
        }

        //Adding a new trade
        trades.add(tradeNew);

        return trades;
    }
}