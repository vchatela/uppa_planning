package net.valentinc.uppa.hyperplanning;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

public class AppRater {
    private static String mAppTitle = "YOUR-APPLICATION-TITLE";
    private static String mAppPackageName;

    private final static String APP_RATER    = "apprater";
    private final static String DONT_SHOW    = "dontshowagain";
    private final static String LAUNCH_COUNT = "launch_count";
    private final static String FIRST_LAUNCH = "date_firstlaunch";
    private final MainActivity mAct;

    private int daysUntilPrompt = 3;
    private int launchesUntilPrompt = 7;

    private static Context mContext;

    public AppRater(Context context, MainActivity act) {
        this.mContext = context;
        this.mAct = act;
        this.mAppPackageName = context.getPackageName();
    }

    public AppRater init() {
        SharedPreferences prefs = mContext.getSharedPreferences(APP_RATER, 0);
        if (prefs.getBoolean(DONT_SHOW, false)) { return null; }

        SharedPreferences.Editor editor = prefs.edit();
        long launch_count = prefs.getLong(LAUNCH_COUNT, 0) + 1;
        editor.putLong(LAUNCH_COUNT, launch_count);

        Long date_firstLaunch = prefs.getLong(FIRST_LAUNCH, 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong(FIRST_LAUNCH, date_firstLaunch);
        }

        if (launch_count >= launchesUntilPrompt) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (daysUntilPrompt * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }
        editor.commit();
        return this;
    }

    public AppRater showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getString(R.string.dialog_text, mAppTitle));
        builder.setTitle(mAppTitle);
        builder.setNegativeButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                editor.putBoolean(DONT_SHOW, true);
                editor.commit();
                dialog.dismiss();
            }
        });

        builder.setPositiveButton(mContext.getResources().getString(R.string.rate), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                editor.putBoolean(DONT_SHOW, true);
                editor.commit();
                ReviewManager manager = ReviewManagerFactory.create(mContext);
                Task<ReviewInfo> request = manager.requestReviewFlow();
                request.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // We can get the ReviewInfo object
                        ReviewInfo reviewInfo = task.getResult();
                        Task<Void> flow = manager.launchReviewFlow(mAct, reviewInfo);
                        flow.addOnCompleteListener(task2 -> {
                            // The flow has finished. The API does not indicate whether the user
                            // reviewed or not, or even whether the review dialog was shown. Thus, no
                            // matter the result, we continue our app flow.
                        });
                    } else {
                        // There was some problem, continue regardless of the result.
                    }
                });

                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        return this;
    }

    public AppRater setMinDays(int minDays) {
        daysUntilPrompt = minDays;
        return this;
    }

    public AppRater setMinLaunches(int minLaunches) {
        launchesUntilPrompt = minLaunches;
        return this;
    }

    public AppRater setAppTitle(String appTitle) {
        mAppTitle = appTitle;
        return this;
    }

}