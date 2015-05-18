
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
            "ʱ��00��01","ʱ��01��02","ʱ��02��03","ʱ��03��04",
            "ʱ��04��05","ʱ��05��06","ʱ��06��07","ʱ��07��08",
            "ʱ��08��09","ʱ��09��10","ʱ��10��11","ʱ��11��12",
            "ʱ��12��13","ʱ��13��14","ʱ��14��15","ʱ��15��16",
            "ʱ��16��17","ʱ��17��18","ʱ��18��19","ʱ��19��20",
            "ʱ��20��21","ʱ��21��22","ʱ��22��23","ʱ��23��24"
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
