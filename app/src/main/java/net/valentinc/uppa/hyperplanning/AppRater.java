package net.valentinc.uppa.hyperplanning;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

public class AppRater {
    private static String mAppTitle = "YOUR-APPLICATION-TITLE";
    private static String mAppPackageName = "YOUR-PACKAGE-NAME";

    private final static String APP_RATER    = "apprater";
    private final static String DONT_SHOW    = "dontshowagain";
    private final static String LAUNCH_COUNT = "launch_count";
    private final static String FIRST_LAUNCH = "date_firstlaunch";

    private int daysUntilPrompt = 3;
    private int launchesUntilPrompt = 7;

    private static Context mContext;

    public AppRater(Context context) {
        this.mContext = context;
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
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
                        .parse("market://details?id=" + mAppPackageName)));
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