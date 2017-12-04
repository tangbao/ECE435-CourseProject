
newStepDetector (原程序中叫detectorNewStep)
1. Read in the data from sensor

2. If a peaks is detected and it meets the time difference and threshold, it means a step is detected.

3. If the time difference is meeted but the difference between peaks and valleys, the difference will be used to calculate threshold.

peakDetector (原来叫detectorPeak)
1. If these three coditions are met, a peak is detected:
  - The derivative of this point is negative.
  - The derivative of previous point is positive.
  - Has risen two times or the value of the peak is greater than 20.

2. Record the value of valleys.
  - If there is a step, a peak will appear next to the valley, which means there will be obvious features.
  - So we record the value of valleys to compare with the next peak.


calcThreshold (peakValleyThread)
1. Use four differences between peaks and valleys to calculate an average.

2. Then gradient it to get a threshold.

stepCount
1. Only after walking 10 steps continuously, the step counter starts to count.

2. If a user walkes 9 steps continuously and stops longer than 3 sec, the count will be set to 0.

There are several scenes when a man is walking:

1. Walking normally, holding the phone
(watching the phone; not watching and shaking the hand; not watching and not shaking the hand)

2. Walking slowly, holding the phone
(watching the phone; not watching and shaking the hand; not watching and not shaking the hand)

3. Walking quickly, holding the phone
(shaking the hand; not shaking the hand; suppose that the man will not watch his phone when walking quickly)

4. Phone in the pants pocket
(walking normally, slowly and quickly)

5. Phone in the shirt pocket
(walking normally, slowly and quickly)

6. Go up and down stairs
(with the five scenes above)

After analyzing the data gotten from these six scenes, we can find that actually the waves we get are sine waves, and every trough is a step-point.

The task of the algorithm is to find these step-points. There are three principles for the algorithm to filter useless troughs:

1. We have to limit the times of consecutive curve rises

2. The difference between the troughs and valleys must greater than the threshold

3. The threshold changes dynamically.
