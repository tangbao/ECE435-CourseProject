package edu.rutgers.ece435.pedometer;

/**
 * Created by tzzma on 2017/12/12.
 *
 *
 */

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import edu.rutgers.ece435.pedometer.Interface.*;

/*
* main part of the step detector to detect it is a step-point or not
* */

public class StepDetector implements SensorEventListener {

    //store the data from three axises
    float[] originalValue = new float[3]; // float[] originalValue
    final int currentValCount = 4;  // final int currentValCount;
    //store the interval between peak and valley, which used to calculate the threshold.
    float[] tempValue = new float[currentValCount]; // remain
    int tempCount = 0; // remain
    // if the curve keeps going up
    boolean isUp = false; // boolean isUp
    // The number of times the curve continues to rise
    int keepUpCount = 0;   // int keepUpCount
    // The number of times the prev curve continues to rise
    int prevKeepUpCount = 0; // int prevKeepUpCount
    // The status of prev point. up or down
    boolean prevStatus = false; // boolean prevStatus
    // peak value
    float peakVal = 0; // float peakVal
    // valley value
    float valleyVal = 0; // float valleyVal
    // current peak time
    long currentPeakTime = 0; // long currentPeakTime
    // prev peak time
    long prevPeakTime = 0; // long prevPeakTime
    // current time
    long currentTime = 0; // currentTime
    // current sensor value
    float currentSensorVal = 0; // currentSensorVal
    // prev sensor value
    float prevSensorVal = 0; // prevSensorVal
    //the value for calculating the dynamic threshold
    final float originThreshold = (float) 1.3; // originThreshold;
    // the original threshold
    float thresholdVal = (float) 2.0; //thresholdVal
    // time interval between peak and valley
    int TimeInterval = 250;// remain
    private StepCountListener mStepListeners; 

    @Override
    public void onSensorChanged(SensorEvent event) {
        for (int i = 0; i < 3; i++) {
            originalValue[i] = event.values[i];
        }
        currentSensorVal = (float) Math.sqrt(originalValue[0] * originalValue[0]
                + originalValue[1] * originalValue[1] + originalValue[2] * originalValue[2]);
        newStepDetector(currentSensorVal);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //
    }

    public void initializeListener(StepCountListener listener) {
        this.mStepListeners = listener;
    }


    // Read the data from sensor
    // If a peaks is detected and it meets the requirement for time interval and threshold
    // it means a step is detected.
    // If the time interval and the difference between peaks and valleys meets the requirement
    // the difference will be used to calculate threshold.
    public void newStepDetector(float values) {
        if (prevSensorVal == 0) {
            prevSensorVal = values;
        } else {
            if (peakDetector(values, prevSensorVal)) {
                prevPeakTime = currentPeakTime;
                currentTime = System.currentTimeMillis();
                if (currentTime - prevPeakTime >= TimeInterval
                        && (peakVal - valleyVal >= thresholdVal)) {
                    currentPeakTime = currentTime;

                    // notify the UI thread to update the UI
                    // before update the UI, we need to detect :
                    // 1. whether there are 10 steps already
                    // 2. if a user walkes 9 steps continuously and stops longer than 3 sec, the count will be set to 0.
                    // 3. Only after detecting the users have already had 9 steps, the data starts to pass to the step counter.
                    mStepListeners.countStep();
                }
                if (currentTime - prevPeakTime >= TimeInterval
                        && (peakVal - valleyVal >= originThreshold)) {
                    currentPeakTime = currentTime;
                    thresholdVal = calculateThreshold(peakVal - valleyVal);
                }
            }
        }
        prevSensorVal = values;
    }

    // if the following requirements met, a peak is detected.
    // 1. current direction of this point goes down.
    // 2. prev point's direction goes up
    // 3. has risen two times
    // 4. the value of the peak is greater than 20.

    // Record the value of valleys.
    // 1.If there is a step, a peak will appear next to the valley, which means there will be obvious features.
    // 2.So we record the value of valleys to compare with the next peak.
    public boolean peakDetector(float newValue, float oldValue) {
        prevStatus = isUp;
        // 之前是否上升趋势
        if (newValue >= oldValue) {
            isUp = true;
            keepUpCount++;
        } else {
            prevKeepUpCount = keepUpCount;
            keepUpCount = 0;
            isUp = false;
        }

        if (!isUp && prevStatus
                && (prevKeepUpCount >= 2 || oldValue >= 20)) {
            peakVal = oldValue;
            return true;
        } else if (!prevStatus && isUp) {
            valleyVal = oldValue;
            return false;
        } else {
            return false;
        }
    }

    /*
     * 阈值的计算
     * 1.通过波峰波谷的差值计算阈值
     * 2.记录4个值，存入tempValue[]数组中
     * 3.在将数组传入函数averageValue中计算阈值
     * */

    // calculate the threshold.
    // 1.Use four differences between peaks and valleys to calculate an average.
    // 2.Then gradient it to get a threshold.

    public float calculateThreshold(float value) {
        float tempThread = thresholdVal;
        if (tempCount < currentValCount) {
            tempValue[tempCount] = value;
            tempCount++;
        } else {

            // calculate the average of the array
            // use the average to limit the gradient into

            float ave=0;
            for (int i = 0; i < currentValCount; i++) {
                ave += tempValue[i];
            }
            ave/=currentValCount;
            if (ave >= 8)
                ave = (float) 4.3;
            else if (ave >= 7 && ave < 8)
                ave = (float) 3.3;
            else if (ave >= 4 && ave < 7)
                ave = (float) 2.3;
            else if (ave >= 3 && ave < 4)
                ave = (float) 2.0;
            else {
                ave = (float) 1.3;
            }

            tempThread=ave;

            for (int i = 1; i < currentValCount; i++) {
                tempValue[i - 1] = tempValue[i];
            }
            tempValue[currentValCount - 1] = value;
        }
        return tempThread;

    }

}