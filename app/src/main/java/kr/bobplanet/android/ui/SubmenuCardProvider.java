package kr.bobplanet.android.ui;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import kr.bobplanet.android.R;
import kr.bobplanet.backend.bobplanetApi.model.Submenu;

/**
 *
 *
 * @author heonkyu.jin
 * @version 15. 10. 17
 */
public class SubmenuCardProvider extends CardProvider<SubmenuCardProvider> {

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

        BaseListAdapter.BaseViewHolderFactory factory = new BaseListAdapter.BaseViewHolderFactory() {
            @Override
            public BaseListAdapter.BaseViewHolder newInstance(View view) {
                return new SubmenuViewHolder(view);
            }
        };
        adapter = new BaseListAdapter(factory, submenuList, R.layout.menu_submenu_item);

        RecyclerView recyclerView = ButterKnife.findById(view, R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    class SubmenuViewHolder extends BaseListAdapter.BaseViewHolder<Submenu> {
        @BindView(R.id.thumbnail)
        NetworkImageView thumbnail;
        @BindView(R.id.title)
        TextView title;

        public SubmenuViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void setItem(Submenu submenu) {
            thumbnail.setImageUrl(submenu.getItem().getThumbnail(), getImageLoader());
            title.setText(submenu.getItem().getName());
        }
    }
}
