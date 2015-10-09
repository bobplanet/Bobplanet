package kr.bobplanet.android;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import de.greenrobot.event.EventBus;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;
import kr.bobplanet.android.event.NetworkExceptionEvent;
import kr.bobplanet.backend.bobplanetApi.model.DailyMenu;

/**
 * 특정 일자의 아침-점심-저녁 메뉴를 보여주는 fragment.
 * DayViewActivity에 삽입되어 실제 메뉴를 화면에 보여주는 역할을 담당함.
 * 
 * - 날짜 parameter는 fragment 생성시에 bundle로 전달되어 <code>getArguments()</code>를 통해 조회
 * - 서버로부터 메뉴 데이터를 가져오면 activity에도 알려줌 (좌우 fragment를 미리 만들어둘 수 있도록)
 * - 화면은 listview로 구성하고 DayViewAdapter를 이용해 UI 구성.
 *
 */
public class DayViewFragment extends ListFragment implements AppConstants {
    private static final String TAG = DayViewFragment.class.getSimpleName();
    private static final String ARGUMENT_DATE = "ARGUMENT_DATE";
    private static final String STATE_DAILYMENU = "STATE_DAILYMENU";

    private static final DailyMenu INVALID_DAILY_MENU = new DailyMenu();
    private DailyMenu dailyMenu;
    private ProgressBar progressBar;

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private DayViewListAdapter adapter;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_daily_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Restore the previously serialized activated item position.
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
                setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
            }
        }

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setIndeterminateDrawable(new SmoothProgressDrawable.Builder(getActivity()).
                interpolator(new AccelerateInterpolator()).build());

        TextView t = (TextView) view.findViewById(R.id.daily_view_date_header);
        t.setText(getDate(true));

        adapter = new DayViewListAdapter(this);
        setListAdapter(adapter);
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

                DayViewFragment.this.dailyMenu = dailyMenu;

                adapter.setMenuList(dailyMenu.getMenu());
                adapter.notifyDataSetChanged();

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

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState");

        if (mActivatedPosition != ListView.INVALID_POSITION) {
            // Serialize and persist the activated item position.
            outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
        }
    }

    private void setActivatedPosition(int position) {
        if (position == ListView.INVALID_POSITION) {
            getListView().setItemChecked(mActivatedPosition, false);
        } else {
            getListView().setItemChecked(position, true);
        }

        mActivatedPosition = position;
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
     * 해당 일자의 메뉴데이터 로딩이 끝날 경우 DailyViewActivity로 전달되는 이벤트 클래스.
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
