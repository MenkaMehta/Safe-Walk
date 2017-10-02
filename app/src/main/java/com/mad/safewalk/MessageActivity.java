package com.mad.safewalk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

/**
 * This class allows users to save their personalised message
 *
 */

public class MessageActivity extends AppCompatActivity {

    private EditText mMessageEditText;
    private File mImageFile;

    /**
     * This method just gets access to the editText fields
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mMessageEditText = (EditText) findViewById(R.id.activity_message_editText);
    }

    /**
     * This method saves the personalised message into shared preferences
     *
     * @param view
     */
    public void saveButtonClicked(View view)
    {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.MESSAGE_DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.MESSAGE_VALUE, mMessageEditText.getText().toString());
        editor.commit();
        Toast.makeText(this, getResources().getString(R.string.message_saved), Toast.LENGTH_LONG).show();
        finish();

    }

    /**
     * Clears the ediTextFields
     *
     * @param view
     */
    public void clearButtonClicked(View view)
    {
        mMessageEditText.setText("");
    }

    /**
     * Captures the camera image and saves in on your phone memory or SD card
     *
     * @param view
     */

    public void cameraButtonClicked(View view)
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //Sets the environment where to save the image
        mImageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),getResources().getString(R.string.help_image));
        //Creates the Uri (Unique resource identification) for the imageFile
        Uri tempImageUri = Uri.fromFile(mImageFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImageUri);
        //Request a higher quality camera : 0 if lower quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, Constants.REQUEST);
        //Starts the intent for result
        startActivityForResult(intent, Constants.DEFAULT_VALUE);
    }

    /**
     * Checks on activity result to confirm the action after the camera activity was started
     *
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.DEFAULT_VALUE)
        {
            switch(resultCode)
            {
                //Checks if the imageFile was saved successfully
                case Activity.RESULT_OK:
                    if(mImageFile.exists())
                    {
                        Toast.makeText(this, getResources().getString(R.string.file_save) + " " + mImageFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(this, getResources().getString(R.string.file_error) + " ", Toast.LENGTH_LONG).show();
                    }
                    break;
                //Checks if the action was cancelled
                case Activity.RESULT_CANCELED:
                    break;
                default:
                    break;

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

