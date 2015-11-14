package kr.bobplanet.android.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;
import hugo.weaving.DebugLog;
import kr.bobplanet.android.App;
import kr.bobplanet.android.R;
import kr.bobplanet.android.event.ItemScoreChangeEvent;
import kr.bobplanet.android.event.MorningMenuToggleEvent;
import kr.bobplanet.android.event.NetworkExceptionEvent;
import kr.bobplanet.backend.bobplanetApi.model.DailyMenu;
import kr.bobplanet.backend.bobplanetApi.model.ItemScore;
import kr.bobplanet.backend.bobplanetApi.model.Menu;
import kr.bobplanet.backend.bobplanetApi.model.UserDevice;

/**
 * 특정 일자의 아침-점심-저녁 메뉴를 보여주는 fragment.
 * DayActivity에 삽입되어 실제 메뉴를 화면에 보여주는 역할을 담당함.
 * <p>
 * - 날짜 parameter는 fragment 생성시에 bundle로 전달되어 <code>getArguments()</code>를 통해 조회
 * - 서버로부터 메뉴 데이터를 가져오면 activity에도 알려줌 (좌우 fragment를 미리 만들어둘 수 있도록)
 * - 화면은 listview로 구성하고 DayViewAdapter를 이용해 UI 구성.
 * - 메뉴 데이터가 없을 경우(식당 노는 날) 안내화면 노출.
 * <p>
 * TODO ProgressBar 색상을 theme에서 지정해볼 것.
 *
 * @author heonkyu.jin
 * @version 2015. 9. 27.
 */
public class DayFragment extends BaseFragment {
    @SuppressWarnings("unused")
    private static final String TAG = DayFragment.class.getSimpleName();
    private static final String ARGUMENT_DATE = "ARGUMENT_DATE";

    /**
     * 아무 데이터도 없는 메뉴리스트 상수.
     */
    private static final List<Menu> EMPTY_MENU_LIST = new ArrayList<>();

    /**
     * 메뉴리스트
     */
    private List<Menu> menuList = EMPTY_MENU_LIST;

    /**
     * 아침메뉴 객체. 아침메뉴 보기를 끌 때 menuList에서 백업됨.
     */
    private Menu morningMenu;

    /**
     * 네트웤에서 데이터를 가져올 때 동작하는 ProgressBar
     */
    ProgressBar progressBar;

    /**
     * 메뉴정보를 관리하는 ListAdapter
     */
    BaseListAdapter<Menu> adapter;

    /**
     * 메뉴정보를 표시하는 RecyclerView
     */
    RecyclerView recyclerView;

    /**
     * recyclerView의 LayoutManager. 스크롤위치 조절할 때 사용됨
     */
    LinearLayoutManager layoutManager;

    /**
     * 메뉴가 없을 때(식당 노는날) 대신 표시되는 View. 안내메시지 포함.
     */
    View emptyView;

    public DayFragment() {
    }

    /**
     * Fragment 팩토리함수.
     *
     * @param date 본 fragment가 표시해야 하는 식당메뉴의 타겟날짜
     * @return fragment instance
     */
    public static DayFragment newInstance(String date) {
        DayFragment f = new DayFragment();

        Bundle args = new Bundle();
        args.putString(ARGUMENT_DATE, date);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.day_fragment, container, false);
        ButterKnife.bind(this, view);

        recyclerView = ButterKnife.findById(view, R.id.recycler_view);
        progressBar = ButterKnife.findById(view, R.id.progress_bar);
        emptyView = ButterKnife.findById(view, R.id.empty);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Drawable d = new SmoothProgressDrawable.Builder(getActivity())
                .interpolator(new AccelerateInterpolator()).build();
        d.setColorFilter(ContextCompat.getColor(getContext(), R.color.progress),
                android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setIndeterminateDrawable(d);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
    }

    /**
     * 구동되자마자 서버에서 일간메뉴데이터를 가져옴.
     * 데이터 캐싱을 위해 <code>ApiProxy</code>를 이용함.
     */
    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);

        if (menuList == EMPTY_MENU_LIST) {
            App.getApiProxy().loadMenuOfDate(getArguments().getString(ARGUMENT_DATE),
                    dailyMenu -> onDailyMenuLoaded(dailyMenu));
        }
    }

    /**
     *
     *
     * @param dailyMenu
     */
    private void onDailyMenuLoaded(DailyMenu dailyMenu) {
        menuList = dailyMenu.getMenu();

        // 이게 null이면 식당 노는날이라는 뜻임.
        if (menuList != null) {
            BaseListAdapter.BaseViewHolderFactory factory = view -> new DayViewHolder(view);

            UserDevice device = App.getUserManager().getDevice();
            if (!device.getMorningMenuEnabled()) {
                morningMenu = menuList.get(0);
                menuList.remove(0);
            }

            adapter = new BaseListAdapter<>(factory, menuList, R.layout.day_item);
            recyclerView.setAdapter(adapter);

            App.getApiProxy().loadItemScores(
                    Lists.transform(menuList, (Menu menu) -> menu.getItem().getName()),
                    itemScores -> onItemScoresLoaded(itemScores.getItems())
            );
        } else {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }

        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.INVISIBLE);

        EventBus.getDefault().post(new DataLoadCompleteEvent(dailyMenu));
    }

    @DebugLog
    private void onItemScoresLoaded(List<ItemScore> itemScores) {
        if (itemScores == null) return;

        Map<String, ItemScore> map = Maps.uniqueIndex(itemScores, score -> score.getItem().getName());

        for (Menu menu : menuList) {
            String key = menu.getItem().getName();
            if (map.containsKey(key)) {
                menu.getItem().setScore(map.get(key));
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(NetworkExceptionEvent e) {
        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }

    /**
     * 아침메뉴 on/off 처리.
     *
     * @param e
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(MorningMenuToggleEvent e) {
        if (adapter == null) return;

        if (!e.isActive) {
            adapter.remove(0);
            adapter.notifyItemRemoved(0);
        } else {
            adapter.add(0, morningMenu);
            adapter.notifyItemInserted(0);
            layoutManager.scrollToPosition(0);
        }
    }

    @SuppressWarnings("unused")
    @DebugLog
    public void onEvent(ItemScoreChangeEvent e) {
        if (adapter == null) return;

        for (int i = 0; i < adapter.getItemCount(); i++) {
            Menu menu = adapter.get(i);
            if (e.isFor(menu)) {
                e.apply(menu);
                adapter.notifyItemChanged(i);
            }
        }
    }

    /**
     * 해당 일자의 메뉴데이터 로딩이 끝날 경우 DayActivity로 전달되는 이벤트 클래스.
     */
    static class DataLoadCompleteEvent {
        private final DailyMenu dailyMenu;

        protected DataLoadCompleteEvent(DailyMenu dailyMenu) {
            this.dailyMenu = dailyMenu;
        }

        protected DailyMenu getDailyMenu() {
            return dailyMenu;
        }
    }
}
