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
* 算法的主要部分,检测是否是步点
* */

public class StepDetector implements SensorEventListener {

    //存放三轴数据
    float[] originalValue = new float[3]; // float[] originalValue
    final int currentValCount = 4;  // final int currentValCount;
    //用于存放计算阈值的波峰波谷差值
    float[] tempValue = new float[currentValCount]; // remain
    int tempCount = 0; // remain
    //是否上升的标志位
    boolean isUp = false; // boolean isUp
    //持续上升次数
    int keepUpCount = 0;   // int keepUpCount
    //上一点的持续上升的次数，为了记录波峰的上升次数
    int prevKeepUpCount = 0; // int prevKeepUpCount
    //上一点的状态，上升还是下降
    boolean prevStatus = false; // boolean prevStatus
    //波峰值
    float peakVal = 0; // float peakVal
    //波谷值
    float valleyVal = 0; // float valleyVal
    //此次波峰的时间
    long currentPeakTime = 0; // long currentPeakTime
    //上次波峰的时间
    long prevPeakTime = 0; // long prevPeakTime
    //当前的时间
    long currentTime = 0; // currentTime
    //当前传感器的值
    float currentSensorVal = 0; // currentSensorVal
    //上次传感器的值
    float prevSensorVal = 0; // prevSensorVal
    //动态阈值需要动态的数据，这个值用于这些动态数据的阈值
    final float originThreshold = (float) 1.3; // originThreshold;
    //初始阈值
    float thresholdVal = (float) 2.0; //thresholdVal
    //波峰波谷时间差
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

    /*
    * 检测步子，并开始计步
    * 1.传入sersor中的数据
    * 2.如果检测到了波峰，并且符合时间差以及阈值的条件，则判定为1步
    * 3.符合时间差条件，波峰波谷差值大于initialValue，则将该差值纳入阈值的计算中
    * */
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
                    /*
                     * 更新界面的处理，不涉及到算法
                     * 一般在通知更新界面之前，增加下面处理，为了处理无效运动：
                     * 1.连续记录10才开始计步
                     * 2.例如记录的9步用户停住超过3秒，则前面的记录失效，下次从头开始
                     * 3.连续记录了9步用户还在运动，之前的数据才有效
                     * */
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

    /*
     * 检测波峰
     * 以下四个条件判断为波峰：
     * 1.目前点为下降的趋势：isDirectionUp为false
     * 2.之前的点为上升的趋势：lastStatus为true
     * 3.到波峰为止，持续上升大于等于2次
     * 4.波峰值大于20
     * 记录波谷值
     * 1.观察波形图，可以发现在出现步子的地方，波谷的下一个就是波峰，有比较明显的特征以及差值
     * 2.所以要记录每次的波谷值，为了和下次的波峰做对比
     * */
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
    public float calculateThreshold(float value) {
        float tempThread = thresholdVal;
        if (tempCount < currentValCount) {
            tempValue[tempCount] = value;
            tempCount++;
        } else {
            //tempThread = averageValue(tempValue, currentValCount);
               /*
                * 梯度化阈值
                * 1.计算数组的均值
                * 2.通过均值将阈值梯度化在一个范围里
                * */

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

    /*
     * 梯度化阈值
     * 1.计算数组的均值
     * 2.通过均值将阈值梯度化在一个范围里
     * */
//    public float averageValue(float value[], int n) {
//        float ave = 0;
//        for (int i = 0; i < n; i++) {
//            ave += value[i];
//        }
//        ave = ave / currentValCount;
//        if (ave >= 8)
//            ave = (float) 4.3;
//        else if (ave >= 7 && ave < 8)
//            ave = (float) 3.3;
//        else if (ave >= 4 && ave < 7)
//            ave = (float) 2.3;
//        else if (ave >= 3 && ave < 4)
//            ave = (float) 2.0;
//        else {
//            ave = (float) 1.3;
//        }
//        return ave;
//    }

}