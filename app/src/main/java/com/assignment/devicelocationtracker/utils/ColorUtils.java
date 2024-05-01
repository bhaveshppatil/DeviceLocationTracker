package com.assignment.devicelocationtracker.utils;

import android.graphics.Color;
import android.widget.TextView;

import java.util.Random;

public class ColorUtils {

    public static void setRandomTextColor(TextView textView) {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        textView.setTextColor(color);
    }
}
