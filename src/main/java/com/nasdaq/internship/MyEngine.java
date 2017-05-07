package com.nasdaq.internship;

import java.util.ArrayList;
import java.util.List;

class MyEngine implements MatchingEngine {

    private final OrderBook buyOrderBook = OrderBook.buildBuyOrderBook();

    private final OrderBook sellOrderBook = OrderBook.buildSellOrderBook();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<Trade> enterOrder(final Order newOrder) {
        List<Trade> trades = new ArrayList<>();
        tradeMatching(newOrder, trades);
        return trades;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void tradeMatching(final Order newOrder, final List<Trade> trades) {
        Order matchingOrder = getOppositeOrderBook(newOrder).match(newOrder);
        if (matchingOrder != null) {
            Trade trade = trade(newOrder, matchingOrder);
            trades.add(trade);

            if (isFilled(matchingOrder, trade)) {
                getOrderBook(matchingOrder).remove(matchingOrder);
            } else {
                matchingOrder.decrease(trade.getQuantity());
            }

            if (!isFilled(newOrder, trade)) {
                newOrder.decrease(trade.getQuantity());
                tradeMatching(newOrder, trades);
            }

        } else {
            getOrderBook(newOrder).add(newOrder);
        }
    }

    private OrderBook getOrderBook(final Order newOrder) {
        return (newOrder.getSide() == Side.BUY) ? buyOrderBook : sellOrderBook;
    }

    private OrderBook getOppositeOrderBook(final Order newOrder) {
        return (newOrder.getSide() == Side.BUY) ? sellOrderBook : buyOrderBook;
    }

    private boolean isFilled(final Order order, final Trade trade) {
        return order.getQuantity().compareTo(trade.getQuantity()) == 0;
    }

    private Trade trade(final Order newOrder, final Order matchingOrder) {
        return new Trade(
                newOrder.getSide() == Side.SELL ? newOrder : matchingOrder,
                newOrder.getSide() == Side.BUY ? newOrder : matchingOrder,
                newOrder.getQuantity().min( matchingOrder.getQuantity() ),
                matchingOrder.getPrice()); // "limit order" always trades at the order book price
    }
}