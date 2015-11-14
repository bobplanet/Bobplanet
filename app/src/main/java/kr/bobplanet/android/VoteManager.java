package kr.bobplanet.android;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;
import kr.bobplanet.android.event.ItemScoreChangeEvent;
import kr.bobplanet.android.event.UserAccountEvent;
import kr.bobplanet.android.log.UserLogEvent;
import kr.bobplanet.android.ui.BaseActivity;
import kr.bobplanet.android.ui.BaseDialogBuilder;
import kr.bobplanet.backend.bobplanetApi.model.Menu;
import kr.bobplanet.backend.bobplanetApi.model.Vote;
import me.gujun.android.taggroup.TagGroup;

/**
 * 사용자의 메뉴 평가 기능을 제공하는 객체.
 *
 * - 평가 dialog 생성
 * - 로그인하지 않은 사용자는 SignInManager로 넘겨 계정등록 유도
 * - 평가 데이터의 서버 전송
 * - 평가 내용은 Snackbar에 띄워 사용자에게 '평가가 끝났다'는 시각적 힌트를 제공
 *
 * @author heonkyu.jin
 * @version 15. 10. 24
 */
public class VoteManager implements Constants {
    private static final String TAG = VoteManager.class.getSimpleName();

    private static final String DIALOG_VOTE = "DIALOG_VOTE";

    private BaseActivity baseActivity;
    private Context context;

    private boolean userHasAccount;
    private Menu menu;

    private int score;
    private List<String> comments;
    private TagGroup tags;

    private EditText commentsEditText;

    public VoteManager(BaseActivity baseActivity, Menu menu) {
        this.baseActivity = baseActivity;
        this.context = baseActivity.getBaseContext();
        this.menu = menu;

        EventBus.getDefault().register(this);
    }

    public void showVoteDialog(int score) {
        this.score = score;
        requestMyScore();

        View view = LayoutInflater.from(context).inflate(R.layout.vote_dialog, null);

        String title = baseActivity.getString(R.string.dialog_vote_label) + " - " +
                baseActivity.getString(score == VOTE_UP ?
                R.string.action_thumb_up : R.string.action_thumb_down);
        AlertDialog voteDialog = new BaseDialogBuilder(baseActivity, DIALOG_VOTE)
                .setTitle(title)
                .setView(view)
                .setPositiveButton(R.string.button_ok, (dialog, which) -> {
                    voteOrRequestSignIn();
                    dialog.dismiss();
                })
                .setNegativeButton(R.string.button_cancel, null)
                .create();

        commentsEditText = ButterKnife.findById(view, R.id.comment_edit);

        tags = ButterKnife.findById(view, R.id.tags);
        tags.setOnTagClickListener(comment -> {
            comments.remove(comment);
            tags.setTags(getQuotedComments());
            addComment(comment);
        });

        voteDialog.show();
    }

    private List<String> getQuotedComments() {
        return Lists.transform(comments, c -> Util.getQuotedString(c));
    }

    /**
     * 유저가 과거에 매긴 점수 가져옴.
     */
    @DebugLog
    private void requestMyScore() {
        UserManager um = App.getUserManager();
        if (um.hasAccount()) {
            userHasAccount = true;
            App.getApiProxy().myVote(um.getUserId(), menu,
                    vote -> {
                        comments = vote.getComments();
                        if (comments.size() > 0) {
                            commentsEditText.setText(TextUtils.join(" ", comments));
                            commentsEditText.setSelection(commentsEditText.length());
                            tags.setTags(getQuotedComments());
                        }
                    }
            );
        }
    }

    @DebugLog
    private void voteOrRequestSignIn() {
        if (userHasAccount) {
            uploadVote();
        } else {
            App.getSignInManager().showSignInDialog(baseActivity);
        }
    }

    /**
     * 평가결과를 서버로 전송.
     */
    @DebugLog
    private void uploadVote() {
        if (score != 0) {
            ApiProxy proxy = App.getApiProxy();

            List<String> comments = commentsEditText.toString().trim().length() > 0 ?
                    Arrays.asList(commentsEditText.getText().toString().split(" ")) : null;

            Vote vote = new Vote()
                    .setUserId(App.getUserManager().getUserId())
                    .setMenuId(menu.getId())
                    .setItemName(menu.getItem().getName())
                    .setScore(score)
                    .setComments(comments);

            proxy.vote(vote, result -> {
                Log.v(TAG, result.toString());
                EventBus.getDefault().post(new ItemScoreChangeEvent(result));
            });

            String level = context.getString(score > 0 ? R.string.vote_level_up : R.string.vote_level_down);
            String message = String.format(context.getString(R.string.vote_notice_fmt),
                    Util.appendParticle(menu.getItem().getName(), "을", "를"),
                    level, Util.endsWithConsonant(level) ? "이" : ""
            );

            baseActivity.showSnackbar(message);
        }
    }

    /**
     * 로그인이 완료되었을 때 호출되는 callback.
     *
     * @param event
     */
    @SuppressWarnings("unused")
    @DebugLog
    public void onEvent(UserAccountEvent event) {
        if (event instanceof UserAccountEvent.SignIn) {
            UserLogEvent.login(event.accountType);
            uploadVote();
        }
    }

    private void addComment(String comment) {
        Editable comments = commentsEditText.getText();
        comments.append(comment).append(' ');
    }
}
