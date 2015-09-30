package net.valentinc.uppa.planning.test;

import android.test.ActivityInstrumentationTestCase2;
import net.valentinc.uppa.hyperplanning.MainActivity;

/**
 * Created by valentinc on 22/09/2015.
 * Generate and Execute android Test after build
 */

public class androidTest extends ActivityInstrumentationTestCase2<MainActivity> {
    public androidTest(Class<MainActivity> activityClass) {
        super(activityClass);
    }

    /*
    // Start the main activity of the application under test
    mActivity = getActivity();

    // Get a handle to the Activity object's main UI widget, a Spinner
    mSpinner = (Spinner)mActivity.findViewById(com.android.example.spinner.R.id.Spinner01);

    // Set the Spinner to a known position
    mActivity.setSpinnerPosition(TEST_STATE_DESTROY_POSITION);

    // Stop the activity - The onDestroy() method should save the state of the Spinner
    mActivity.finish();

    // Re-start the Activity - the onResume() method should restore the state of the Spinner
    mActivity = getActivity();

    // Get the Spinner's current position
    int currentPosition = mActivity.getSpinnerPosition();

    // Assert that the current position is the same as the starting position
    assertEquals(TEST_STATE_DESTROY_POSITION, currentPosition);
     */

    /*TODO :
    - Test spinner instanciation
    - Test remember value of promo
    - Test cache works
    - Test export works
    - Test StartUp works
    */

}
