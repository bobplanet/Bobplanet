package kr.bobplanet.android;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.provider.TextCardProvider;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import hugo.weaving.DebugLog;
import kr.bobplanet.backend.bobplanetApi.model.Submenu;

/**
 * Created by hkjinlee on 15. 10. 17..
 */
public class SubmenuCardProvider extends TextCardProvider<SubmenuCardProvider> {

    List<Submenu> submenuList;

    RecyclerView.Adapter adapter;

    public SubmenuCardProvider setSubmenu(final List<Submenu> submenuList) {
        this.submenuList = submenuList;
        notifyDataSetChanged();

        return this;
    }

    @Override
    public int getLayout() {
        return R.layout.menu_submenu_card;
    }

    @Override
    public void render(View view, Card card) {
        super.render(view, card);

        RecyclerView recyclerView = ButterKnife.findById(view, R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(getAdapter());
    }

    private RecyclerView.Adapter getAdapter() {
        if (adapter == null) {
            adapter = new RecyclerView.Adapter<SubmenuCardProvider.ViewHolder>() {
                @Override
                public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_submenu_item,
                            parent, false);
                    return new ViewHolder(view);
                }

                @Override
                public void onBindViewHolder(SubmenuCardProvider.ViewHolder holder, int position) {
                    Submenu submenu = submenuList.get(position);

                    holder.thumbnail.setImageUrl(submenu.getItem().getThumbnail(),
                            MainApplication.getInstance().getImageLoader());
                    holder.title.setText(submenu.getItem().getId());
                }

                @Override
                public int getItemCount() {
                    return submenuList.size();
                }
            };
        }

        return adapter;
    }

    static public class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.thumbnail)
        NetworkImageView thumbnail;
        @Bind(R.id.title)
        TextView title;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
