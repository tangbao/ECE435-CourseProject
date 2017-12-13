package edu.rutgers.ece435.pedometer;

/**
 * Created by tzzma on 2017/12/12.
 *
 *
 */

class Interface {

    public interface StepCountListener {
        void countStep();
    }

    public interface StepValuePassListener {
        void stepChanged(int steps);
    }

    public interface UpdateUiCallBack {
        void updateUi();
    }
}
