package kr.bobplanet.android.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.greenrobot.event.EventBus;
import kr.bobplanet.android.App;
import kr.bobplanet.android.R;
import kr.bobplanet.backend.bobplanetApi.model.Menu;

/**
 *
 * @author heonkyu.jin
 * @version 2015. 10. 10
 */
public class MenuStatFragment extends BaseFragment {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.menu_stat_fragment, container, false);
    }

}
