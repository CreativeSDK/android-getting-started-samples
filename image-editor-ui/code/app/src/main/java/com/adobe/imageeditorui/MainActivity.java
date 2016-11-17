package com.adobe.imageeditorui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.adobe.creativesdk.aviary.AdobeImageIntent;

public class MainActivity extends AppCompatActivity {

    private ImageView mEditedImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEditedImageView = (ImageView) findViewById(R.id.editedImageView);

        /* 1) Make a new Uri object (Replace this with a real image on your device) */
        Uri imageUri = Uri.parse("content://media/external/images/media/1248");

        //ToolsFactory.Tools quickLaunchTool = ToolsFactory.Tools.CROP;

        /* 2) Create a new Intent */
        Intent imageEditorIntent = new AdobeImageIntent.Builder(this)
                .setData(imageUri)
                //.quickLaunchTool(quickLaunchTool, savedInstanceState)
                .build();

        /* 3) Start the Image Editor with request code 1 */
        startActivityForResult(imageEditorIntent, 1);
    }

    /* Handle the results */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {

                /* Make a case for the request code we passed to startActivityForResult() */
                case 1:

                    /* Show the image! */
                    Uri editedImageUri = data.getParcelableExtra(AdobeImageIntent.EXTRA_OUTPUT_URI);
                    mEditedImageView.setImageURI(editedImageUri);

                    break;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
