package com.nasdaq.internship;

import org.junit.Test;

import static org.junit.Assert.*;

public class OrderBookTests {

    @Test
    public void testBuildBuyOrderBook_whenBuyOrderBookBuilt_thenNotNull() {
        OrderBook orderBook = OrderBook.buildBuyOrderBook();
        assertNotNull(orderBook);
    }

    @Test
    public void testBuildBuyOrderBook_whenSellOrderBookBuilt_thenNotNull() {
        OrderBook orderBook = OrderBook.buildSellOrderBook();
        assertNotNull(orderBook);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test(expected = IllegalArgumentException.class)
    public void testAdd_whenIllegalSide_thenException() {
        OrderBook orderBook = OrderBook.buildBuyOrderBook();
        orderBook.add(new Order("Broker1 SELL NDAQ 10@68.66"));
    }

    @Test
    public void testAdd_whenLegalSide_thenNoException() {
        OrderBook orderBook = OrderBook.buildSellOrderBook();
        orderBook.add(new Order("Broker1 SELL NDAQ 10@68.66"));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test(expected = IllegalArgumentException.class)
    public void testMatch_whenIllegalSide_thenException() {
        OrderBook orderBook = OrderBook.buildSellOrderBook();
        orderBook.match(new Order("Broker1 SELL NDAQ 10@68.66"));
    }

    @Test
    public void testMatch_WhenAddedSingleAtSamePrice_thenMatch()
    {
        OrderBook orderBook = OrderBook.buildSellOrderBook();
        orderBook.add(new Order("Broker1 SELL NDAQ 10@68.66"));
        Order matchingOrder = orderBook.match(new Order("Bank2 BUY NDAQ 57@68.67"));
        assertNotNull(matchingOrder);
        assertEquals("10", matchingOrder.getQuantity().toString());
        assertEquals("68.66", matchingOrder.getPrice().toString());
    }

    @Test
    public void testMatch_WhenSellingMultipleWithDiffPrices_thenMatchCheaperFirst()
    {
        OrderBook orderBook = OrderBook.buildSellOrderBook();
        orderBook.add(new Order("Broker1 SELL NDAQ 10@68.66"));
        orderBook.add(new Order("Broker4 SELL NDAQ 67@68.61"));
        Order matchingOrder = orderBook.match(new Order("Bank1 BUY NDAQ 26@68.99"));
        assertNotNull(matchingOrder);
        assertEquals("67", matchingOrder.getQuantity().toString());
        assertEquals("68.61", matchingOrder.getPrice().toString());
    }

    @Test
    public void testMatch_WhenBuyingMultipleWithDiffPrices_thenMatchMoreExpensiveFirst()
    {
        OrderBook orderBook = OrderBook.buildBuyOrderBook();
        orderBook.add(new Order("Broker4 BUY NDAQ 67@68.61"));
        orderBook.add(new Order("Broker1 BUY NDAQ 10@68.66"));
        Order matchingOrder = orderBook.match(new Order("Bank1 SELL NDAQ 26@68.60"));
        assertNotNull(matchingOrder);
        assertEquals("10", matchingOrder.getQuantity().toString());
        assertEquals("68.66", matchingOrder.getPrice().toString());
    }

    @Test
    public void testMatch_WhenBuyingSamePrice_thenMatchMoreRecent()
    {
        OrderBook orderBook = OrderBook.buildBuyOrderBook();
        orderBook.add(new Order("Broker4 BUY NDAQ 67@68.61"));
        orderBook.add(new Order("Broker1 BUY NDAQ 10@68.61"));
        Order matchingOrder = orderBook.match(new Order("Bank1 SELL NDAQ 26@68.60"));
        assertNotNull(matchingOrder);
        assertEquals("67", matchingOrder.getQuantity().toString());
        assertEquals("68.61", matchingOrder.getPrice().toString());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test(expected = IllegalArgumentException.class)
    public void testRemove_whenIllegalSide_thenException() {
        OrderBook orderBook = OrderBook.buildBuyOrderBook();
        orderBook.remove(new Order("Broker1 SELL NDAQ 10@68.66"));
    }

    @Test
    public void testRemove_whenRemovingNonExistent_thenNoException() {
        OrderBook orderBook = OrderBook.buildBuyOrderBook();
        orderBook.remove(new Order("Broker1 BUY NDAQ 10@68.66"));
    }

    @Test
    public void testRemove_whenRemovingExistent_thenNoMatch() {
        OrderBook orderBook = OrderBook.buildBuyOrderBook();
        Order order = new Order("Broker4 BUY NDAQ 67@68.61");
        orderBook.add(order);
        orderBook.remove(order);
        Order matchingOrder = orderBook.match(new Order("Bank1 SELL NDAQ 67@68.61"));
        assertNull(matchingOrder);
    }

    @Test
    public void testRemove_whenRemovingFromMultipleWithDiffPrices_thenNoMatchnRemoved() {
        OrderBook orderBook = OrderBook.buildBuyOrderBook();
        Order order = new Order("Broker4 BUY NDAQ 67@68.68");
        orderBook.add(order);
        orderBook.add(new Order("Broker2 BUY NDAQ 37@68.67"));
        orderBook.add(new Order("Broker1 BUY NDAQ 10@68.61"));
        orderBook.remove(order);
        Order matchingOrder = orderBook.match(new Order("Bank1 SELL NDAQ 67@68.60"));
        assertNotNull(matchingOrder);
        assertEquals("37", matchingOrder.getQuantity().toString());
        assertEquals("68.67", matchingOrder.getPrice().toString());
    }

    @Test
    public void testRemove_whenRemovingFromMultipleWithSamePrice_thenMatchNonRemoved() {
        OrderBook orderBook = OrderBook.buildBuyOrderBook();
        Order order = new Order("Broker4 BUY NDAQ 67@68.68");
        orderBook.add(order);
        orderBook.add(new Order("Broker2 BUY NDAQ 37@68.68"));
        orderBook.add(new Order("Broker1 BUY NDAQ 10@68.68"));
        orderBook.remove(order);
        Order matchingOrder = orderBook.match(new Order("Bank1 SELL NDAQ 67@68.60"));
        assertNotNull(matchingOrder);
        assertEquals("37", matchingOrder.getQuantity().toString());
        assertEquals("68.68", matchingOrder.getPrice().toString());
    }

}
