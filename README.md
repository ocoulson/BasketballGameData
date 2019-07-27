## Basketball Data

 - For any given data point (ie. hex string received), some validation can be performed. 
 - There are some that will be inherently invalid (e.g. Score of 0) meaning the datapoint must be discarded or possibly amended based on other datapoints.
 - Alternatively, there are some that are invalid based on the fact that they don't fit with other received events (e.g. a duplicate or one whose timestamp is far ahead of the next few subsequent events), these must also be handled separately
 
 
Invalid data edge cases
- Received event is duplicate of last event
- Received event is duplicate of some already received event
- Received event is invalid: 0 points scored - no change to totals  
- Received event has timestamp before last event
- Received event has values such that it cannot be immediately following the previous event (i.e. jump in points of > 3 for either or both teams)
- Received event is invalid: 0 points scored - totals have changed such that the jump from the last point was impossible (i.e. +4)
- Received event is invalid: 0 points scored - totals have changed in a possible way

When receiving an invalid / incongruous event, use the previous and subsequent events to determine whether the invalid event should be discarded or amended


##### Extensions
Given more time, I might improve on the following:
 
- Use ScalaCheck test library to write a test battery using randomised data to test both a wide variety of possible individual hex inputs and also a variety of sequences of inputs