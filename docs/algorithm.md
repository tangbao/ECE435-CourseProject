
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
