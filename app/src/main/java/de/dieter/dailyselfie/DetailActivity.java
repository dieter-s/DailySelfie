package de.dieter.dailyselfie;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


public class DetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);

        // get image Uri
        Bundle mBundle = getIntent().getExtras();
        Uri mUri = mBundle.getParcelable("uri");

        // get ImageView and set Picture
        ImageView mImageView = (ImageView) findViewById(R.id.imageDetailView);
        mImageView.setImageBitmap(BitmapFactory.decodeFile(mUri.getPath()));

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here
        int id = item.getItemId();

        // exit activity when ok pressed
        if (id == R.id.action_ok) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
