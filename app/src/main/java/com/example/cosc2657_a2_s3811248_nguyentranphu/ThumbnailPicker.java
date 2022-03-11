package com.example.cosc2657_a2_s3811248_nguyentranphu;

import java.util.Random;

public class ThumbnailPicker {
    public static int randomPick() {
        int images[] = {R.drawable.thumbnail1, R.drawable.thumbnail2, R.drawable.thumbnail3,
        R.drawable.thumbnail4, R.drawable.thumbnail5};
        Random random = new Random();
        return images[random.nextInt(images.length)];
    }
}