package kr.bobplanet.android;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.provider.TextCardProvider;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kr.bobplanet.backend.bobplanetApi.model.Submenu;

/**
 *
 *
 * @author heonkyu.jin
 * @version 15. 10. 17
 */
public class MenuScoreCardProvider extends TextCardProvider<MenuScoreCardProvider> {

    List<Submenu> submenuList;

    float averageScore;
    int myScore;

    @Bind(R.id.rating)
    RatingBar ratingBar;

    @Bind(R.id.myRating)
    RatingBar myRatingBar;

    public MenuScoreCardProvider setAverageScore(float averageScore) {
        this.averageScore = averageScore;
        notifyDataSetChanged();

        return this;
    }

    public MenuScoreCardProvider setMyScore(int myScore) {
        this.myScore = myScore;
        notifyDataSetChanged();

        return this;
    }

    @Override
    public int getLayout() {
        return R.layout.menu_score_card;
    }

    @Override
    public void render(View view, Card card) {
        super.render(view, card);
        ButterKnife.bind(this, view);

        ratingBar.setRating(averageScore);
        myRatingBar.setRating(myScore);
    }
}
