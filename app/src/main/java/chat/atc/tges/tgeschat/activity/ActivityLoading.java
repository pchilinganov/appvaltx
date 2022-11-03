package chat.atc.tges.tgeschat.activity;

import android.os.Bundle;
import android.widget.ProgressBar;

import chat.atc.tges.tgeschat.R;
import chat.atc.tges.tgeschat.databaseOnline.BaseVolleyActivity;

public class ActivityLoading extends BaseVolleyActivity {
    private ProgressBar spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        spinner = (ProgressBar)findViewById(R.id.progressBar);
    }
}
