package com.nasdaq.internship;

import java.math.BigDecimal;
import java.util.*;

class OrderBook {

    /**
     * {stock:GOOGL} => {price:42.21} => [ {order2}, {order1}, ... ]
     */
    private final Map<String, SortedMap<BigDecimal, SortedSet<Order>>> orderBook = new HashMap<>();

    private final Side side;

    private final Comparator<BigDecimal> priceComparator;

    private final Comparator<Order> orderComparator = (Order o1, Order o2) -> {
        int result = o1.getTimeStamp().compareTo( o2.getTimeStamp() );
        if (result == 0) {
            result = o1.getId().compareTo( o2.getId() );
        }
        return result;
    };

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    static OrderBook buildBuyOrderBook()
    {
        return new OrderBook(Side.BUY, Comparator.reverseOrder());
    }

    static OrderBook buildSellOrderBook()
    {
        return new OrderBook(Side.SELL, BigDecimal::compareTo);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private OrderBook(final Side side, final Comparator<BigDecimal> priceComparator) {
        this.side = side;
        this.priceComparator = priceComparator;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    void add(final Order order) {
        if (order.getSide() != this.side)
            throw new IllegalArgumentException("Trying to add an illegal order side.");

        orderBook.putIfAbsent(order.getStock(), new TreeMap<>(priceComparator));
        orderBook.get(order.getStock()).putIfAbsent(order.getPrice(), new TreeSet<>(orderComparator));
        orderBook.get(order.getStock()).get(order.getPrice()).add(order);
    }

    Order match(final Order order) {
        if (order.getSide() != this.side.getOppositeSide())
            throw new IllegalArgumentException("Trying to match an illegal order side.");

        if (!orderBook.containsKey(order.getStock()))
            return null;

        SortedMap<BigDecimal, SortedSet<Order>> ordersByPrice = orderBook.get(order.getStock());
        BigDecimal matchingPrice = !ordersByPrice.isEmpty() ? ordersByPrice.firstKey() : null;
        if (matchingPrice != null && isPriceAcceptable(matchingPrice, order.getPrice())) {
            return ordersByPrice.get(matchingPrice).first();
        } else {
            return null;
        }
    }

    private boolean isPriceAcceptable(final BigDecimal ourPrice, final BigDecimal priceOffered) {
        return side == Side.BUY && ourPrice.compareTo(priceOffered) >= 0 ||
                side == Side.SELL && ourPrice.compareTo(priceOffered) <= 0;
    }

    void remove(final Order order) {
        if (order.getSide() != this.side)
            throw new IllegalArgumentException("Trying to remove an illegal order side.");

        SortedMap<BigDecimal, SortedSet<Order>> ordersByPrice = orderBook.get(order.getStock());
        if (ordersByPrice != null && ordersByPrice.containsKey(order.getPrice())) {
            ordersByPrice.get(order.getPrice()).remove(order);
            if (ordersByPrice.get(order.getPrice()).isEmpty()) {
                ordersByPrice.remove(order.getPrice());
            }
        }
    }
}
