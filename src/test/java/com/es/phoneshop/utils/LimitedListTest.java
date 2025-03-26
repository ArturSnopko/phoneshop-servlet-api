package com.es.phoneshop.utils;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LimitedListTest
{
    private LimitedList<Integer> list;

    @Before
    public void setup() {
        list = new LimitedList<>(3);
    }

    @Test
    public void testAdd() {
        list.addItem(1);
        list.addItem(2);
        assertEquals(2, list.getItems().size());
        list.addItem(3);
        assertEquals(3, list.getItems().size());
    }

    @Test
    public void testAddWithOverload() {
        list.addItem(1);
        list.addItem(2);
        list.addItem(3);
        list.addItem(4);
        assertEquals(3, list.getItems().size());
    }

}
