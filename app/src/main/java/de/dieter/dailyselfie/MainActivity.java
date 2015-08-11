package de.dieter.dailyselfie;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends Activity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private ListView lv;
    private CustomAdapter mAdapter;
    public ArrayList<PictureItem> mPictureItems = new ArrayList<PictureItem>();
    private AlarmManager mAlarmManager;
    private PendingIntent alarmIntent;
    private File pictureDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // check if external storage is available
        if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){

            // no storage available, exit app
            Toast toast = Toast.makeText(this,getText(R.string.message_no_external_storage), Toast.LENGTH_LONG);
            toast.show();
            finish();

        }

        setContentView(R.layout.activity_main);
        lv = (ListView) findViewById(R.id.listselfies);
        // load pictures in ArrayList
        pictureDirectory = getApplicationContext().getExternalFilesDir(null);

        if(pictureDirectory == null){
            Toast toast = Toast.makeText(this,getText(R.string.message_no_external_storage), Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        mPictureItems = getPictureItemsFromPath(pictureDirectory);

        mAdapter = new CustomAdapter(mPictureItems,getApplicationContext());

        lv.setAdapter(mAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                PictureItem item = (PictureItem) adapterView.getItemAtPosition(i);
                Uri mUri = Uri.parse(pictureDirectory + "/" + item.getName());

                Intent mIntent = new Intent(getApplicationContext(), DetailActivity.class);
                mIntent.putExtra("uri", mUri);
                startActivity(mIntent);

            }
        });

        // set Alarm
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // build pending intent to start AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

        startAlarm();

    }

    private void startAlarm(){

        // recurring every 2 Minutes
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (2*60*1000), 2*60*1000, alarmIntent);
    }

    private ArrayList<PictureItem> getPictureItemsFromPath(File path){

        ArrayList<PictureItem> items = new ArrayList<PictureItem>();
        File files[] = new File(path.getPath()).listFiles();

        for (File f: files){

            if(f.isFile()) {

                PictureItem pictureItem = new PictureItem();
                pictureItem.setName(f.getName());
                //pictureItem.setImage(BitmapFactory.decodeFile(f.getPath()));
                pictureItem.setImage(setPic(f));
                items.add(pictureItem);
            }

        }

        return items;
    }

    private Bitmap setPic(File mCurrentPhotoPath) {

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath.getPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/200, photoH/200);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(mCurrentPhotoPath.getPath(), bmOptions);
    }

    private void refreshPictures() {

        mPictureItems = getPictureItemsFromPath(pictureDirectory);
        mAdapter.refresh(mPictureItems);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
            if (resultCode == RESULT_OK) {

                refreshPictures();

            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
                Toast.makeText(this,getText(R.string.message_picture_not_taken),Toast.LENGTH_SHORT).show();

            } else {
                // Image capture failed, advise user
                Toast.makeText(this,getText(R.string.message_picture_error),Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_take_picture) {

            // create Intent to take a picture and return control to the calling application
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // build filename from timestamp
            String fileName = new SimpleDateFormat("yyyyMMddhhmmss'.jpg'").format(new Date());
            File outfile = new File(pictureDirectory,fileName);

            // set the image file name
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse("file://" + outfile.getAbsolutePath()));

            // start the image capture Intent
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

            return true;
        }
        if (id == R.id.action_stop_reminder) {

            // If the alarm has been set, cancel it.
            if (mAlarmManager!= null) {
                mAlarmManager.cancel(alarmIntent);

                Toast.makeText(getApplicationContext(),getText(R.string.message_reminder_stopped),Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        if (id == R.id.action_start_reminder) {

            startAlarm();
            Toast.makeText(getApplicationContext(),getText(R.string.message_reminder_started),Toast.LENGTH_SHORT).show();

        }

        return super.onOptionsItemSelected(item);
    }
}
