
package com.mednovo.chart_myself.notimportant;

import android.support.v4.app.FragmentActivity;

import com.mednovo.mti_pii.R;

/**
 * Baseclass of all Activities of the Demo Application.
 * 
 * @author Philipp Jahoda
 */
public abstract class DemoBase extends FragmentActivity {

    protected String[] mMonths = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"
    };
    protected String[] hour_value = new String[] {
            "时段00—01","时段01—02","时段02—03","时段03—04",
            "时段04—05","时段05—06","时段06—07","时段07—08",
            "时段08—09","时段09—10","时段10—11","时段11—12",
            "时段12—13","时段13—14","时段14—15","时段15—16",
            "时段16—17","时段17—18","时段18—19","时段19—20",
            "时段20—21","时段21—22","时段22—23","时段23—24"
    };

    protected String[] mParties = new String[] {
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
    }
}
