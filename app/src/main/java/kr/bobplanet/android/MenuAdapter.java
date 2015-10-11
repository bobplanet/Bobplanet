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
 * {@link MenuFragment}에서 사용되는 Adapter.
 * 쌀밥, 김치 등과 같은 서브메뉴를 grid 형태로 표현할 때 사용됨.
 *
 * - setMenuList()를 통해 fragment로부터 메뉴 데이터를 전달받음
 * - 메뉴 썸네일은 Volley에서 제공하는 <code>NetworkImageView</code>를 이용하여 async로 가져옴
 *
 * @author heonkyu.jin
 * @version 15. 9. 29
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    @SuppressWarnings("UnusedDeclaration")
    private static final String TAG = MenuAdapter.class.getSimpleName();

    private final Context context;
    private List<Submenu> submenuList = new ArrayList<>();
    private final ImageLoader imageLoader = MainApplication.getInstance().getImageLoader();

    public MenuAdapter(Context context, List<Submenu> submenuList) {
        this.context = context;
        this.submenuList = submenuList;
    }

    @Override
    public int getItemCount() {
        return submenuList == null ? 0 : submenuList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.menu_item, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Submenu submenu = submenuList.get(position);
        if (submenu == null) return;

        viewHolder.setSubmenu(submenu);

        viewHolder.icon.setImageUrl(submenu.getItem().getIconURL(), imageLoader);
        viewHolder.title.setText(submenu.getItem().getId());
    }

    /**
     * 서브메뉴 정보를 담게 되는 ViewHolder.
	 *
     * TODO 얘를 clickable하게 만들어서 '쌀밥'에 대한 통계도 볼 수 있게 하자.
     */
    static class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {
        Submenu submenu;

        final NetworkImageView icon;
        final TextView title;

        public ViewHolder(View itemView) {
            super(itemView);

            icon = (NetworkImageView) itemView.findViewById(R.id.icon);
            title = (TextView) itemView.findViewById(R.id.title);

            //itemLayoutView.setOnClickListener(this);
        }

        void setSubmenu(Submenu submenu) {
            this.submenu = submenu;
        }
    }
}
