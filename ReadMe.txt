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

How to execute/run this program:
-Ensure java is installed properly (https://java.com/en/download/)
-Open your command console and go to the directory where the program has been downloaded.
-Type "javac Oven.java"
-Type "java Oven"
-Note to quit the program, enter q or Ctrl-C

Tests:
To test the program combine one action from each equivalence class and check if the program gives you feedback for wrongly using the controls or if it works.
Though the program is meant for actions to be taken in order from 1st equivalence class to 3rd, changing the order is possible but will often give feedback to direct the user to take actions in order
Equivalence classes: 
1. [Setting power, Set target temperature, set time] (More than one action possible for this equivalent class)
2. [Choose defrost, Choose microwave, Choose radiator, Choose radiator+microwave, Create a queue, Start a timer]
3. [Wait for timer to end, press stop button, open door/pause, resume, change internal temperature] (More than one action possible, but some actions within this class will bring you back to the first equivalence class).

Architecture pattern:
The State pattern (http://w3sdesign.com/?gr=b08&ugr=proble) is used in this program. 
Essentially in this pattern allows objects to change their behavior depending on their states and allowing states to be completely independent from each other.
In order to add new features to this program, new queued states or internal states can be added with their own respective behaviors and methods.
Furthermore, ensure that these state machines can be influced directly or indirectly by the main method. The main method interacts with the user.



