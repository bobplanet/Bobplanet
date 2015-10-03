package kr.bobplanet.android;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;


import java.io.IOException;

import de.greenrobot.event.EventBus;
import kr.bobplanet.backend.bobplanetApi.BobplanetApi;
import kr.bobplanet.backend.bobplanetApi.model.DailyMenu;

public class DailyViewFragment extends ListFragment {
    private static final String TAG = DailyViewFragment.class.getSimpleName();
    private static final String ARGUMENT_DATE = "ARGUMENT_DATE";

    /**
     * The serialization (saved instance state) Bundle key representing the
     * activated item position. Only used on tablets.
     */
    private static final String STATE_ACTIVATED_POSITION = "activated_position";

    /**
     * The current activated item position. Only used on tablets.
     */
    private int mActivatedPosition = ListView.INVALID_POSITION;

    private DailyViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DailyViewFragment() {
    }

    public static DailyViewFragment newInstance(String date) {
        DailyViewFragment f = new DailyViewFragment();

        Bundle args = new Bundle();
        args.putString(ARGUMENT_DATE, date);
        f.setArguments(args);

        return f;
    }

    private void setDailyMenu(DailyMenu dailyMenu) {
        adapter.setMenuList(dailyMenu.getMenu());
        adapter.notifyDataSetChanged();

        TextView t = (TextView) getView().findViewById(R.id.daily_view_date_header);
        t.setText(dailyMenu.getDate());
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
        if (savedInstanceState != null
                && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
            setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
        }

        View header = getLayoutInflater(savedInstanceState).inflate(R.layout.list_daily_header, null, false);
        getListView().addHeaderView(header);

        adapter = new DailyViewAdapter(getActivity());
        setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        new DailyMenuAsyncRetriever() {
            @Override
            protected void onPostExecute(DailyMenu dailyMenu) {
                if (dailyMenu == null) return;

                EventBus.getDefault().post(new DataLoadCompleteEvent(dailyMenu));
                setDailyMenu(dailyMenu);
            }
        }.execute(getArguments().getString(ARGUMENT_DATE));
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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

    abstract static class DailyMenuAsyncRetriever extends AsyncTask<String, Void, DailyMenu> {
        @Override
        protected DailyMenu doInBackground(String... params) {
            BobplanetApi api = EndpointHelper.getAPI();
            try {
                Log.i(TAG, "fetching menuOfDate() = " + params[0]);
                DailyMenu dailyMenu = api.menuOfDate(params[0]).execute();
                Log.d(TAG, "dailyMenu = " + dailyMenu);

                return dailyMenu;
            } catch (IOException e) {
                Log.d(TAG, "error", e);
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Void... progress) {

        }
    }

    static class DataLoadCompleteEvent {
        private DailyMenu dailyMenu;

        DataLoadCompleteEvent(DailyMenu dailyMenu) {
            this.dailyMenu = dailyMenu;
        }

        public DailyMenu getDailyMenu() {
            return dailyMenu;
        }
    }
}
