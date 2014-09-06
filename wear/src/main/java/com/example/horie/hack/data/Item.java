package com.example.horie.hack.data;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by nissiy on 2014/09/07.
 */
public class Item implements Serializable {
    public String Title;
    public String BrandName;
    public String Link;
    public String DescLong;
    public String Price;
    public String ImageUrl;
    public ArrayList<String> PlaceList;
    public Bitmap bitmapImage;

    public Item() {}

    public Item(String Title,
                String BrandName,
                String Link,
                String DescLong,
                String Price,
                String ImageUrl,
                ArrayList<String> PlaceList) {
        this.Title = Title;
        this.BrandName = BrandName;
        this.Link = Link;
        this.DescLong = DescLong;
        this.Price = Price;
        this.ImageUrl = ImageUrl;
        this.PlaceList = PlaceList;
    }
}
