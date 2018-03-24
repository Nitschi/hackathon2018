package com.beertastic.beertastic;

/**
 * Created by felix on 25/03/18.
 */

public class ColorFactory {
    private static int i = 0;
    private static String[] colors = {"#F44336", "#2196F3", "#FFC107","#4CAF50", "#795548",
            "#607D8B", "#E91E63", "#9C27B0", "#CDDC39", "#00BCD4"};

    public static String getColor(){
        // Get new color
        String color = colors[i%colors.length];
        // Increase iterator for next request
        i++;
        return color;
    }

}
