package edu.rutgers.ece435.pedometer;

/**
 * Created by tzzma on 2017/12/12.
 *
 *
 */

public class Interface {

    public interface StepCountListener {
        public void countStep();
    }

    public interface StepValuePassListener {
        public void stepChanged(int steps);
    }

    public interface UpdateUiCallBack {
        public void updateUi();
    }
}
