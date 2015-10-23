package kr.bobplanet.android;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;
import kr.bobplanet.android.event.ItemChangeEvent;
import kr.bobplanet.android.event.NetworkExceptionEvent;
import kr.bobplanet.backend.bobplanetApi.model.DailyMenu;
import kr.bobplanet.backend.bobplanetApi.model.Menu;

/**
 * 특정 일자의 아침-점심-저녁 메뉴를 보여주는 fragment.
 * DayViewActivity에 삽입되어 실제 메뉴를 화면에 보여주는 역할을 담당함.
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
    @SuppressWarnings("UnusedDeclaration")
    private static final String TAG = DayFragment.class.getSimpleName();
    private static final String ARGUMENT_DATE = "ARGUMENT_DATE";

    private static final List<Menu> EMPTY_MENU_LIST = new ArrayList<>();
    private List<Menu> menuList = EMPTY_MENU_LIST;

    /**
     * 네트웤에서 데이터를 가져올 때 동작하는 ProgressBar
     */
    ProgressBar progressBar;

    @Bind(R.id.header_text)
    TextView headerTextView;

    /**
     * 메뉴정보를 표시하는 RecyclerView
     */
    RecyclerView recyclerView;

    BaseListAdapter adapter;

    /**
     * 메뉴가 없을 때(식당 노는날) 대신 표시되는 View. 안내메시지 포함.
     */
    @Bind(R.id.empty)
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

        headerTextView.setText(getDate(true));

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
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

        // 데이터 로딩이 끝나면 그에 맞게 UI 업데이트하고 activity에도 데이터로딩 끝났음을 전달
        ApiProxy.ApiResultListener<DailyMenu> listener = (DailyMenu dailyMenu) -> {
            if (dailyMenu == null) return;

            menuList = dailyMenu.getMenu();

            // 이게 null이면 식당 노는날이라는 뜻임.
            if (menuList != null) {
                BaseListAdapter.BaseViewHolderFactory factory = (View view) -> new DayViewHolder(view);

                adapter = new BaseListAdapter(factory, menuList, R.layout.day_item);
                recyclerView.setAdapter(adapter);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }

            progressBar.setIndeterminate(false);
            progressBar.setVisibility(View.INVISIBLE);

            EventBus.getDefault().post(new DataLoadCompleteEvent(dailyMenu));
        };

        App.getInstance().getApiProxy().loadMenuOfDate(getDate(false), listener);
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

    public void onEventMainThread(ItemChangeEvent e) {
        recyclerView.invalidate();
    }

    /**
     * <code>getArguments()</code>를 이용하여 이 fragment가 보여주는 메뉴데이터의 날짜를 조회.
     * true는 헤더용 텍스트, false는 서버 API에 전달할 parameter값으로 사용.
     *
     * @param isForTitle true면 "2015/10/09(금)"처럼 포맷, false면 "2015-10-09"
     */
    private String getDate(boolean isForTitle) {
        String date = getArguments().getString(ARGUMENT_DATE);

        if (isForTitle) {
            try {
                return DATEFORMAT_YMDE.format(DATEFORMAT_YMD.parse(date));
            } catch (Exception e) {
                return date;
            }
        } else {
            return date;
        }
    }

    /**
     * 해당 일자의 메뉴데이터 로딩이 끝날 경우 DayViewActivity로 전달되는 이벤트 클래스.
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
