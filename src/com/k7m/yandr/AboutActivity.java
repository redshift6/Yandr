package com.k7m.yandr;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Andy on 26/07/2014.
 */
public class AboutActivity extends Activity {

    private static int ABOUT_ACTIVITY = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.aboutyandr);

        final Button soundBtn = (Button)findViewById(R.id.atributelink);
        soundBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.soundurl)));
                startActivity(browserIntent);
            }
        });
    }
}
