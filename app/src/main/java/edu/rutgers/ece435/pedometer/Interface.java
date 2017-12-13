package edu.rutgers.ece435.pedometer;

/**
 * Created by tzzma on 2017/12/12.
 *
 *
 */

class Interface {

    // the interface for implementing count the step function
    public interface StepCountListener {
        void countStep();
    }

    // pass the step counts each time a effective new step detected
    public interface StepValuePassListener {
        void stepChanged(int steps);
    }

    // to notify the ui thread update the data.
    public interface UpdateUiCallBack {
        void updateUi();
    }
}
