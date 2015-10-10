package kr.bobplanet.android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import kr.bobplanet.backend.bobplanetApi.model.Menu;

/**
 * 메뉴 개요를 보여주는 fragment. MenuViewActivity 안에서 실행된다.
 * 
 * - 메뉴정보는 MenuViewActivity의 intent에서 빼온다. (없으면 큰일남)
 * 
 * @author heonkyu.jin
 * @version 2015. 10. 10
 */
public class MenuOverviewFragment extends Fragment implements AppConstants {
    private static final String TAG = MenuOverviewFragment.class.getSimpleName();

    private Menu menu;

    public MenuOverviewFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EntityVault entityVault = MainApplication.getInstance().getEntityVault();
        String menu_json = getActivity().getIntent().getStringExtra(MENU_ARGUMENT);
        menu = entityVault.getEntity(Menu.class, menu_json);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_menu_overview, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(menu.getItem().getId());

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recylerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(new SubmenuGridAdapter(getContext(), menu.getSubmenu()));
    }
}
