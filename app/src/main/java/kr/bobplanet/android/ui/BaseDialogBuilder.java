package kr.bobplanet.android.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Button;

import kr.bobplanet.android.event.UserLogEvent;

/**
 * 실행기록을 남기는 Dialog를 만들기 위한 기본 builder.
 *
 * @author heonkyu.jin
 * @version 15. 10. 25
 */
public class BaseDialogBuilder extends AlertDialog.Builder {
    private Context context;
    private String category;
    private DialogInterface.OnCancelListener onCancelListener;

    public BaseDialogBuilder(Context context, String category) {
        super(context);
        this.category = category;
    }

    @Override
    public AlertDialog.Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
        return super.setOnCancelListener(onCancelListener);
    }

    @Override
    public AlertDialog create() {
        AlertDialog instance = super.create();

        instance.setOnShowListener((dialog) -> {
            UserLogEvent.dialogView(category);

            Button negativeButton = instance.getButton(DialogInterface.BUTTON_NEGATIVE);
            if (negativeButton != null)
                negativeButton.setOnClickListener((view) -> {
                    UserLogEvent.dialogCancel(category);
                    instance.dismiss();
                });
        });

        instance.setOnCancelListener((dialog) -> {
            if (onCancelListener != null) onCancelListener.onCancel(dialog);
            UserLogEvent.dialogCancel(category);
        });

        return instance;
    }
}
