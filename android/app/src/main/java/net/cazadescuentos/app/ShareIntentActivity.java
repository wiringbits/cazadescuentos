package net.cazadescuentos.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class ShareIntentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_intent);
        String sharedText = "";

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                sharedText = intent.getStringExtra(Intent.EXTRA_TEXT); //
            }
        }

        if(!sharedText.isEmpty()){
            Intent intent2 = new Intent(this, com.google.androidbrowserhelper.trusted.LauncherActivity.class);
            String uri = "app.cazadescuentos.net/index?add-from-url=["+sharedText+"]";
            intent.setData(Uri.parse(uri));
            startActivity(intent2);
        }else{
            finish();
        }


    }
}