package net.cazadescuentos.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class ShareIntentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_intent);
        String sharedText = "";

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
            sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        }

        if (sharedText == null || sharedText.isEmpty()) {
            finish();
        } else{
            Intent twaIntent = new Intent(this, com.google.androidbrowserhelper.trusted.LauncherActivity.class);
            Uri uri = Uri.parse("https://app.cazadescuentos.net")
                    .buildUpon()
                    .appendQueryParameter("add-from-url", sharedText)
                    .appendQueryParameter("utm_source", "trusted-web-activity")
                    .build();

            twaIntent.setData(uri);
            startActivity(twaIntent);
        }
    }
}