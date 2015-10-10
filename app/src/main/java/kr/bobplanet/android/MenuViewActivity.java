package kr.bobplanet.android;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by hkjinlee on 2015. 10. 10..
 */
public class MenuViewActivity extends AppCompatActivity implements AppConstants {
    private ImageLoader imageLoader = MainApplication.getInstance().getImageLoader();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_view);

        NetworkImageView iconView = (NetworkImageView) findViewById(R.id.icon);
        ViewCompat.setTransitionName(iconView, EXTRA_MENU_ICON);
        iconView.setImageUrl(getIntent().getStringExtra(EXTRA_MENU_ICON), imageLoader);

        TextView text = (TextView) findViewById(R.id.title);
        text.setText(getIntent().getStringExtra(EXTRA_MENU_TITLE));
    }
}
