## Basketball Data

Assumptions / Decisions
- In the interests of brevity, I have decided to consider the timestamp to be correct, so will always try to insert a data point
where its timestamp suggests and use this to determine the previous datapoint, which I can use to make corrections if necessary

- Without specific instructions on how to handle the various edge cases, I have applied the following logic:
    - event can't be decoded correctly - discard
    - event has timestamp before last event - Insert in correct place
    - event is duplicate of some already received event - Discard
    - event is invalid: 0 points scored - Discard
    - event both totals changed - Include, as could be down to missing event that will come in after
    - event that is significantly after the last event received - cache and attempt to re-add later

##### Extensions
Given more time, I would improve on the following:

- Refactor to stop using mutable state to store the values as they come in, replacing the mutable fields with immutable parameters to
be passed around the recursive functions.

- Use a data structure that has better performance for insertion than the list
 
- Use ScalaCheck test library to write a test battery using randomised data to test both a wide variety of possible individual hex inputs and also a variety of sequences of inputs

- Improve the edge case handling to attempt to correct more edge cases that I am currently discarding (i.e. try to use subsequent events to correct former events after the fact)
    - e.g. Deal with cases where both totals have changed, store them and attempt to add in after each subsequent addition, with more information to use to correct them. 