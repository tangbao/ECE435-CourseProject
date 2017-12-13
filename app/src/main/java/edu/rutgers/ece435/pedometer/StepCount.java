package edu.rutgers.ece435.pedometer;

/**
 * Created by tzzma on 2017/12/12.
 *
 */

import edu.rutgers.ece435.pedometer.Interface.*;



public class StepCount implements StepCountListener {

    private int count = 0;
    private int mCount = 0;
    private StepValuePassListener mStepValuePassListener;
    private long timeOfLastPeak = 0;
    private long timeOfThisPeak = 0;

    // notify the UI thread to update the UI
    // before update the UI, we need to detect :
    // 1. whether there are 10 steps already
    // 2. if a user walkes 9 steps continuously and stops longer than 3 sec, the count will be set to 0.
    // 3. Only after detecting the users have already had 9 steps, the data starts to pass to the step counter.

    @Override
    public void countStep() {
        this.timeOfLastPeak = this.timeOfThisPeak;
        this.timeOfThisPeak = System.currentTimeMillis();
        if (this.timeOfThisPeak - this.timeOfLastPeak <= 3000L) {
            if (this.count < 9) {
                this.count++;
            } else if (this.count == 9) {
                this.count++;
                this.mCount += this.count;
                notifyListener();
            } else {
                this.mCount++;
                notifyListener();
            }
        } else {
            this.count = 1;
        }

    }

    public void initListener(StepValuePassListener listener) {
        this.mStepValuePassListener = listener;
    }

    public void notifyListener() {
        if (this.mStepValuePassListener != null)
            this.mStepValuePassListener.stepChanged(this.mCount);
    }


    public void setSteps(int initValue) {
        this.mCount = initValue;
        this.count = 0;
        timeOfLastPeak = 0;
        timeOfThisPeak = 0;
        notifyListener();
    }
}