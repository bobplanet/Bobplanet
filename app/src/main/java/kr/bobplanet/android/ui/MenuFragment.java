package kr.bobplanet.android.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.view.MaterialListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import kr.bobplanet.android.App;
import kr.bobplanet.android.log.EcommerceLog;
import kr.bobplanet.android.util.EntityTranslator;
import kr.bobplanet.android.R;
import kr.bobplanet.android.event.ItemScoreChangeEvent;
import kr.bobplanet.backend.bobplanetApi.model.ItemStat;
import kr.bobplanet.backend.bobplanetApi.model.Menu;

/**
 * 메뉴 개요를 보여주는 fragment. MenuActivity 안에서 실행된다.
 * 
 * - 메뉴정보는 MenuActivity의 intent에서 빼온다.
 *   (이것 때문에 fragment랑 activity랑 연결채널 만들기도 귀찮고..)
 * 
 * @author heonkyu.jin
 * @version 2015. 10. 10
 */
public class MenuFragment extends BaseFragment {
    @SuppressWarnings("unused")
    private static final String TAG = MenuFragment.class.getSimpleName();

    private Menu menu;

    MaterialListView materialListView;

    public MenuFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String menu_json = getActivity().getIntent().getStringExtra(KEY_MENU);
        menu = EntityTranslator.parseEntity(Menu.class, menu_json);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.menu_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        materialListView = view.findViewById(R.id.material_listview);

        Card summaryCard = new Card.Builder(getContext())
                .withProvider(new MenuSummaryCardProvider())
                .setTitle(R.string.card_summary_label)
                .setMenu(menu)
                .endConfig().build();
        materialListView.getAdapter().add(summaryCard);

        App.getApiProxy().loadItemStat(menu.getItem().getName(), itemStat -> {
            ((MenuSummaryCardProvider) summaryCard.getProvider()).setItemStat(itemStat);
        });

        Card submenuCard = new Card.Builder(getContext())
                .withProvider(new SubmenuCardProvider())
                .setTitle(R.string.card_submenu_label)
                .setDescription(R.string.card_submenu_description)
                .setSubmenu(menu.getSubmenu())
                .endConfig().build();
        materialListView.getAdapter().add(submenuCard);
    }

    @Override
    public void onResume() {
        EcommerceLog.measureItemView(menu);

        super.onResume();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onEvent(ItemScoreChangeEvent event) {
        if (event.isFor(menu)) {
            event.apply(menu);
            materialListView.getAdapter().notifyDataSetChanged();
        }
    }
}
