package kr.bobplanet.android;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import de.greenrobot.event.EventBus;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;
import kr.bobplanet.android.event.NetworkExceptionEvent;
import kr.bobplanet.backend.bobplanetApi.model.DailyMenu;
import kr.bobplanet.backend.bobplanetApi.model.Menu;

/**
 * 특정 일자의 아침-점심-저녁 메뉴를 보여주는 fragment.
 * DayViewActivity에 삽입되어 실제 메뉴를 화면에 보여주는 역할을 담당함.
 * 
 * - 날짜 parameter는 fragment 생성시에 bundle로 전달되어 <code>getArguments()</code>를 통해 조회
 * - 서버로부터 메뉴 데이터를 가져오면 activity에도 알려줌 (좌우 fragment를 미리 만들어둘 수 있도록)
 * - 화면은 listview로 구성하고 DayViewAdapter를 이용해 UI 구성.
 *
 */
public class DayViewFragment extends Fragment implements AppConstants {
    private static final String TAG = DayViewFragment.class.getSimpleName();
    private static final String ARGUMENT_DATE = "ARGUMENT_DATE";

    private static final List<Menu> EMPTY_MENU_LIST = new ArrayList<Menu>();
    private List<Menu> menuList = EMPTY_MENU_LIST;
    private ProgressBar progressBar;

    private RecyclerView recyclerView;
    private View emptyView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DayViewFragment() {
    }

    public static DayViewFragment newInstance(String date) {
        DayViewFragment f = new DayViewFragment();

        Bundle args = new Bundle();
        args.putString(ARGUMENT_DATE, date);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_day_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        Drawable d = new SmoothProgressDrawable.Builder(getActivity())
                .interpolator(new AccelerateInterpolator()).build();
        d.setColorFilter(ContextCompat.getColor(getContext(), R.color.progress),
                android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setIndeterminateDrawable(d);

        TextView t = (TextView) view.findViewById(R.id.daily_view_date_header);
        t.setText(getDate(true));

        recyclerView = (RecyclerView) view.findViewById(R.id.recylerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        recyclerView.setHasFixedSize(true);

        emptyView = view.findViewById(R.id.empty);
    }

	/**
	 * 구동되자마자 서버에서 일간메뉴데이터를 가져옴.
	 * 데이터 캐싱을 위해 <code>EntityVault</code>를 이용함.
	 *
	 */
    @Override
    public void onStart() {
        super.onStart();

		// 데이터 로딩이 끝나면 그에 맞게 UI 업데이트하고 activity에도 데이터로딩 끝났음을 전달
        EntityVault.OnEntityLoadListener listener = new EntityVault.OnEntityLoadListener<DailyMenu>() {
            @Override
            public void onEntityLoad(DailyMenu dailyMenu) {
                if (dailyMenu == null) return;

                menuList = dailyMenu.getMenu();

                if (menuList != null) {
                    MenuListAdapter adapter = new MenuListAdapter(DayViewFragment.this.getContext(),
                            menuList);
                    recyclerView.setAdapter(adapter);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                }

                progressBar.setIndeterminate(false);
                progressBar.setVisibility(View.INVISIBLE);

                EventBus.getDefault().post(new DataLoadCompleteEvent(DayViewFragment.this, dailyMenu));
            }
        };

        MainApplication.getInstance().getEntityVault().loadMenuOfDate(getDate(false), listener);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(NetworkExceptionEvent e) {
        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
    }

	/**
	 * <code>getArguments()</code>를 이용하여 이 fragment가 보여주는 메뉴데이터의 날짜를 조회.
	 * true는 헤더용 텍스트, false는 서버 API에 전달할 parameter값으로 사용.
	 * 
	 * @argument isForTitle true면 "2015/10/09(금)"처럼 포맷, false면 "2015-10-09"
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
        private DailyMenu dailyMenu;

        private DayViewFragment fragment;

        protected DataLoadCompleteEvent(DayViewFragment fragment, DailyMenu dailyMenu) {
            this.fragment = fragment;
            this.dailyMenu = dailyMenu;
        }

        protected DayViewFragment getFragment() {
            return fragment;
        }

        protected DailyMenu getDailyMenu() {
            return dailyMenu;
        }
    }
}
