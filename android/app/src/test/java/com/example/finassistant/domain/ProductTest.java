package com.example.finassistant.domain;

import org.junit.Assert;
import org.junit.Test;

/**
 * The type Product test.
 */
public class ProductTest {

    /**
     * Check if price provided is passed correctly.
     */
    @Test
    public void testProduct(){
        Product product = new Product("toothbrush",2.99);
        Assert.assertEquals("toothbrush",product.getTitle());
        Assert.assertEquals(2.99,product.getPrice(),0.0001);
    }

    /**
     * Test setters.
     */
    @Test
    public void TestSetters(){
        Product product = new Product();
        product.setTitle("ice cream");
        product.setPrice(5.99);
        Assert.assertEquals("ice cream",product.getTitle());
        Assert.assertEquals(5.99,product.getPrice(),0.00001);
    }

    @Test
    public void testString(){
        Product product = new Product();
        product.setTitle("apple");
        Assert.assertEquals(product.getTitle(),product.toString());
    }

}
