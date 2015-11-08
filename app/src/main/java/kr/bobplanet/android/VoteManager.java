package kr.bobplanet.android;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;
import kr.bobplanet.android.event.UserAccountEvent;
import kr.bobplanet.android.log.UserLogEvent;
import kr.bobplanet.android.ui.BaseActivity;
import kr.bobplanet.android.ui.BaseDialogBuilder;
import kr.bobplanet.backend.bobplanetApi.model.Item;
import kr.bobplanet.backend.bobplanetApi.model.Menu;
import kr.bobplanet.backend.bobplanetApi.model.Vote;

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
public class VoteManager {
    private static final String TAG = VoteManager.class.getSimpleName();

    private static final String DIALOG_VOTE = "DIALOG_VOTE";

    private BaseActivity activity;
    private Context context;

    private boolean userHasAccount;
    private Menu menu;
    private int myScore;

    private VoteCompletionListener listener;

    public VoteManager(BaseActivity activity, Menu menu) {
        this.activity = activity;
        this.context = activity.getBaseContext();
        this.menu = menu;

        EventBus.getDefault().register(this);
    }

    public void showVoteDialog() {
        requestMyScore();

        View view = LayoutInflater.from(context).inflate(R.layout.vote_dialog, null);

        Dialog voteDialog = new BaseDialogBuilder(activity, DIALOG_VOTE)
                .setTitle(R.string.dialog_vote_label)
                .setView(view)
                .setNegativeButton(R.string.button_cancel, null)
                .create();

        ImageButton buttonUp = ButterKnife.findById(view, R.id.button_thumb_up);
        buttonUp.setOnClickListener(v -> voteOrRequestSignin(Constants.VOTE_UP, voteDialog));
        ImageButton buttonDown = ButterKnife.findById(view, R.id.button_thumb_down);
        buttonDown.setOnClickListener(v -> voteOrRequestSignin(Constants.VOTE_DOWN, voteDialog));

        voteDialog.show();
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
                    (Vote result) -> {
                        if (result != null) {
                            myScore = result.getScore();
                        }
                    }
            );
        }
    }

    @DebugLog
    private void voteOrRequestSignin(int score, Dialog voteDialog) {
        myScore = score;

        if (userHasAccount) {
            uploadVote();
        } else {
            App.getSignInManager().showSignInDialog(activity);
        }
        voteDialog.dismiss();
    }

    /**
     * 평가결과를 서버로 전송.
     */
    @DebugLog
    private void uploadVote() {
        if (myScore != 0) {
            ApiProxy proxy = App.getApiProxy();
            proxy.vote(App.getUserManager().getUserId(), menu, myScore,
                    null);

            String level = context.getString(myScore > 0 ? R.string.vote_level_up : R.string.vote_level_down);
            String message = String.format(context.getString(R.string.vote_notice_fmt),
                    Util.appendParticle(menu.getItem().getName(), "을", "를"),
                    level, Util.endsWithConsonant(level) ? "이" : ""
            );

            activity.showSnackbar(message);
        }
    }

    /**
     * 로그인이 완료되었을 때 호출되는 callback.
     *
     * @param event
     */
    @SuppressWarnings("UnusedDeclaration")
    @DebugLog
    public void onEvent(UserAccountEvent event) {
        if (event instanceof UserAccountEvent.SignIn) {
            UserLogEvent.login(event.accountType);
            uploadVote();
        }
    }

    public void setVoteCompletionListener(VoteCompletionListener listener) {
        this.listener = listener;
    }

    public static interface VoteCompletionListener {
        void onVoteCompleted(Item item);
    }
}
