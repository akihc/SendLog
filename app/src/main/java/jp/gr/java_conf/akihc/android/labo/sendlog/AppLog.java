package jp.gr.java_conf.akihc.android.labo.sendlog;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * クラッシュレポート送信用クラス
 * ACRA(Application Crash Report for Android)を使用してクラッシュログを取得
 */
@ReportsCrashes(
        mode = ReportingInteractionMode.DIALOG,
        mailTo = "chikaoeki@gmail.com", //e-mail address to send log
        resToastText = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info,
        resDialogTitle = R.string.crash_dialog_title,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt// optional. When defined, adds a user text field input with this text resource as a label
)
public class AppLog extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ACRA.init(this);
    }
}
