package de.dieter.dailyselfie;

import android.graphics.Bitmap;

/**
 * Created by dieter on 16.11.14.
 */
public class PictureItem {

    private String name;
    private Bitmap image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
