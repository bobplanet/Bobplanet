package kr.bobplanet.android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import kr.bobplanet.backend.bobplanetApi.model.Submenu;

/**
 * {@link MenuOverviewFragment}에서 사용되는 GridAdapter.
 *
 * - setMenuList()를 통해 fragment로부터 메뉴 데이터를 전달받음
 * - 메뉴 썸네일은 Volley에서 제공하는 <code>NetworkImageView</code>를 이용하여 async로 가져옴
 *
 * @author heonkyu.jin
 * @version 15. 9. 29
 */
public class SubmenuGridAdapter extends RecyclerView.Adapter<SubmenuGridAdapter.SubmenuViewHolder> {
    private static final String TAG = SubmenuGridAdapter.class.getSimpleName();

    private Context context;
    private List<Submenu> submenuList = new ArrayList<>();
    private ImageLoader imageLoader = MainApplication.getInstance().getImageLoader();

    public SubmenuGridAdapter(Context context, List<Submenu> submenuList) {
        this.context = context;
        this.submenuList = submenuList;
    }

    @Override
    public int getItemCount() {
        return submenuList == null ? 0 : submenuList.size();
    }

    @Override
    public SubmenuViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_menu_cell, viewGroup, false);
        return new SubmenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SubmenuViewHolder viewHolder, int position) {
        Submenu submenu = submenuList.get(position);
        if (submenu == null) return;

        viewHolder.setSubmenu(submenu);

        viewHolder.icon.setImageUrl(submenu.getItem().getIconURL(), imageLoader);
        viewHolder.title.setText(submenu.getItem().getId());
    }

    static class SubmenuViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {
        Submenu submenu;

        NetworkImageView icon;
        TextView title;

        public SubmenuViewHolder(View itemView) {
            super(itemView);

            icon = (NetworkImageView) itemView.findViewById(R.id.icon);
            title = (TextView) itemView.findViewById(R.id.title);

            //itemLayoutView.setOnClickListener(this);
        }

        void setSubmenu(Submenu submenu) {
            this.submenu = submenu;
        }

/*
        @Override
        public void onClick(View v) {
            EventBus.getDefault().post(new MenuClickEvent(this));
        }
*/
    }

/*
    static class MenuClickEvent {
        SubmenuViewHolder submenuViewHolder;

        MenuClickEvent(SubmenuViewHolder submenuViewHolder) {
            this.submenuViewHolder = submenuViewHolder;
        }
    }
*/
}
