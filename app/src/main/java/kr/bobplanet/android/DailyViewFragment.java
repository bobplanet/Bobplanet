package kr.bobplanet.android;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.io.IOException;

import de.greenrobot.event.EventBus;
import kr.bobplanet.backend.bobplanetApi.BobplanetApi;
import kr.bobplanet.backend.bobplanetApi.model.DailyMenu;

public class DailyViewFragment extends ListFragment implements AppConstants {
    private static final String TAG = DailyViewFragment.class.getSimpleName();
    private static final String ARGUMENT_DATE = "ARGUMENT_DATE";
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

        TextView t = (TextView) view.findViewById(R.id.daily_view_date_header);
        t.setText(getDate(true));

        adapter = new DailyViewAdapter(getActivity());
        setListAdapter(adapter);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    }

    @Override
    public void onStart() {
        super.onStart();
        new DailyMenuAsyncRetriever().execute(getDate(false));
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

    private String getDate(boolean isForTitle) {
        String date = getArguments().getString(ARGUMENT_DATE);

        if (isForTitle) {
            try {
                return DATEFORMAT_YMDE.format(DATEFORMAT_YMD.parse(date));
            } catch (Exception e){
                return date;
            }
        } else {
            return date;
        }
    }

    private class DailyMenuAsyncRetriever extends AsyncTask<String, Integer, DailyMenu> {
        @Override
        protected DailyMenu doInBackground(String... params) {
            publishProgress(0);
            BobplanetApi api = EndpointHelper.getAPI();
            publishProgress(10);

            try {
                Log.i(TAG, "fetching menuOfDate() = " + params[0]);
                DailyMenu dailyMenu = api.menuOfDate(params[0]).execute();
                publishProgress(100);
                Log.d(TAG, "dailyMenu = " + dailyMenu);

                return dailyMenu;
            } catch (IOException e) {
                Log.d(TAG, "error", e);
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(DailyMenu dailyMenu) {
            if (dailyMenu == null) return;

            adapter.setMenuList(dailyMenu.getMenu());
            adapter.notifyDataSetChanged();

            EventBus.getDefault().post(new DataLoadCompleteEvent(dailyMenu));
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
