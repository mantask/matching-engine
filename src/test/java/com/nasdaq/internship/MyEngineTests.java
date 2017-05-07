package com.nasdaq.internship;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class MyEngineTests {
    private MatchingEngine engine;

    @Before
    public void setUp() {
        engine = new MyEngine();
    }

    private void enterOrderAndAssertTrades(String order, String... expectedTrades) {
        List<Trade> actualTrades = engine.enterOrder(new Order(order));
        assertEquals(expectedTrades.length, actualTrades.size());
        for (int i = 0; i < expectedTrades.length; i++) {
            assertEquals(expectedTrades[i], actualTrades.get(i).toStringShort());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testEnterOrder_whenFirstOrder_thenNoMatch() {
        enterOrderAndAssertTrades("Broker1 SELL NDAQ 14@68.66");
    }

    @Test
    public void testEnterOrder_whenOppositeOrders_thenMatch() {
        enterOrderAndAssertTrades("Broker1 SELL NDAQ 14@68.66");
        enterOrderAndAssertTrades("Bank1 BUY NDAQ 14@68.66",
                "Broker1 -> Bank1 NDAQ 14@68.66");
    }

    @Test
    public void testEnterOrder_whenOppositeOrdersWithDiffPrice_thenMatchSalePrice() {
        enterOrderAndAssertTrades("Broker1 SELL NDAQ 14@68.66");
        enterOrderAndAssertTrades("Bank1 BUY NDAQ 14@68.66",
                "Broker1 -> Bank1 NDAQ 14@68.66");
    }

    @Test
    public void testEnterOrder_whenSellOrder_thenTradeWithLimitPriceFromOrderBook() {
        enterOrderAndAssertTrades("Broker1 SELL NDAQ 14@68.67");
        enterOrderAndAssertTrades("Bank1 BUY NDAQ 30@68.70",
                "Broker1 -> Bank1 NDAQ 14@68.67");
    }

    @Test
    public void testEnterOrder_whenBuyOrder_thenTradeWithLimitPriceFromOrderBook() {
        enterOrderAndAssertTrades("Bank1 BUY NDAQ 30@68.70");
        enterOrderAndAssertTrades("Broker1 SELL NDAQ 14@68.67",
                "Broker1 -> Bank1 NDAQ 14@68.70");
    }

    @Test
    public void testEnterOrder_whenBuyOrdersWithDiffPrices_thenMatchSellHighToLow() {
        enterOrderAndAssertTrades("Bank1 BUY NDAQ 30@68.70");
        enterOrderAndAssertTrades("Bank2 BUY NDAQ 30@68.71");
        enterOrderAndAssertTrades("Bank3 BUY NDAQ 30@68.72");
        enterOrderAndAssertTrades("Broker1 SELL NDAQ 100@68.70",
                "Broker1 -> Bank3 NDAQ 30@68.72",
                "Broker1 -> Bank2 NDAQ 30@68.71",
                "Broker1 -> Bank1 NDAQ 30@68.70");
    }

    @Test
    public void testEnterOrder_whenBuyOrdersWithDiffPrices_thenMatchOnlyLower() {
        enterOrderAndAssertTrades("Bank1 BUY NDAQ 30@68.70");
        enterOrderAndAssertTrades("Bank2 BUY NDAQ 30@68.71");
        enterOrderAndAssertTrades("Bank3 BUY NDAQ 30@68.72");
        enterOrderAndAssertTrades("Broker1 SELL NDAQ 90@68.71",
                "Broker1 -> Bank3 NDAQ 30@68.72",
                "Broker1 -> Bank2 NDAQ 30@68.71");
    }

    @Test
    public void testEnterOrder_whenBuyOrdersWithSamePriceButDiffTimestamps_thenMatchEarliestFirst() {
        enterOrderAndAssertTrades("Bank1 BUY NDAQ 30@68.70");
        enterOrderAndAssertTrades("Bank2 BUY NDAQ 30@68.70");
        enterOrderAndAssertTrades("Bank3 BUY NDAQ 30@68.71");
        enterOrderAndAssertTrades("Broker1 SELL NDAQ 90@68.70",
                "Broker1 -> Bank3 NDAQ 30@68.71",
                "Broker1 -> Bank1 NDAQ 30@68.70",
                "Broker1 -> Bank2 NDAQ 30@68.70");
    }

    @Test
    public void testEnterOrder_whenOrderBookCleared_theTradeContinues() {
        enterOrderAndAssertTrades("Broker1 SELL NDAQ 1@68.67");
        enterOrderAndAssertTrades("Bank1 BUY NDAQ 1@68.67",
                "Broker1 -> Bank1 NDAQ 1@68.67");
        enterOrderAndAssertTrades("Broker1 SELL NDAQ 1@68.67");
        enterOrderAndAssertTrades("Bank1 BUY NDAQ 1@68.67",
                "Broker1 -> Bank1 NDAQ 1@68.67");
    }

    @Test
    public void testEnterOrder_whenSampleInput_thenSampleOutput() {
        enterOrderAndAssertTrades("Broker1 SELL NDAQ 14@68.66");
        enterOrderAndAssertTrades("Bank2 BUY NDAQ 3@68.73",
                "Broker1 -> Bank2 NDAQ 3@68.66");
        enterOrderAndAssertTrades("Bank1 BUY NDAQ 91@68.71",
                "Broker1 -> Bank1 NDAQ 11@68.66");
        enterOrderAndAssertTrades("Bank4 BUY GOOGL 35@840.86");
        enterOrderAndAssertTrades("Bank4 BUY NDAQ 29@68.66");
        enterOrderAndAssertTrades("Bank2 BUY GOOGL 80@842.21");
        enterOrderAndAssertTrades("Broker2 SELL GOOGL 12@840.71",
                "Broker2 -> Bank2 GOOGL 12@842.21");
        enterOrderAndAssertTrades("Broker4 SELL NDAQ 95@68.73");
        enterOrderAndAssertTrades("Bank3 BUY NDAQ 7@68.72");
        enterOrderAndAssertTrades("Broker4 SELL NDAQ 95@68.69",
                "Broker4 -> Bank3 NDAQ 7@68.72",
                "Broker4 -> Bank1 NDAQ 80@68.71");
    }

}
