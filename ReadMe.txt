Requirements/User case stories:

-User sets power (optional, defaults in place), sets target temperature(optional), sets the time, and starts desired cooking option (defrost, microwave, radiator, microwave+radiator)
-User attempts to cook without setting time, triggers display message informing them a time needs to be specified

-User attemps to start a queue of operations (example defrost for 120 seconds and then microwave for 30), but cancels thereby erasing the queue
-User attemps to start a queue of operations (example defrost for 120 seconds and then microwave for 30), and finishes thereby creating a list of operations the oven will go through one by one.
-User presses stop during an operation within the queue, thereby wiping the queue and stopping the current operation.
-User presses pause during an operation within the queue, operation is paused, queue is not affected.

-Oven is currently cooking, user stops the cooking. This automatically sets the time to zero and makes the appropriate number of beeps.
-Oven is currently cooking, user presses pause/opens door. Oven pauses cooking and timer. If user attemps to resume while door open, display will warn that door needs to be closed, else cooking resumes and timer resumes.
-Oven is currently cooking, timer (set by user) ends and oven beeps 3 times.
-Oven is currently cooking/timer is currently on. Users presses stop to reset timer back to zero and end all cooking. Appropriate number of beeps made (3 for cooking, 4 for timer)

-User sets timer without cooking. At the end of the timer 4 beeps are made.
-Timer is on, user starts operation (for example defrost), operation will start using the time remaining from the timer.