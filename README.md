# Telephone-Communication-System

This project is done for the partial fulfillment of the requirements for the degree of B.E in Computer For Simulation and Modelling under supervision of Asst. Prof. Santosh Bhattarai on 2083/04/25 B.S.


This project presents a simulation of a simple telephone system consisting of eight telephone lines connected to a switchboard through a fixed number of links, using a lost-call model. The simulation examines call behavior under different system conditions, particularly when the called line or available links are busy. It records and analyzes the proportions of successfully completed, blocked, and busy calls by representing system components such as telephone lines, links, and calls as entities with relevant attributes, including availability and call duration.

Call arrivals are generated using the bootstrap method, while events such as call arrivals and disconnections are processed according to the current state of the system. In addition to the lost-call model, the simulation supports delayed-call handling, in which calls that cannot be connected immediately are placed in a waiting state until the required connection becomes available. A Java JFrame-based graphical user interface (GUI) provides a real-time representation of the simulation, displaying line availability, link utilization, calls in progress, and call statistics.

The developed simulation provides a practical way to observe and evaluate the behavior of a telephone system under varying conditions. It can be used to study system performance, understand resource utilization, and analyze the effects of busy, blocked, and delayed calls, thereby providing a useful foundation for the design and evaluation of efficient telephone systems.

## Simulation of a telephone system

To demonstrate the basic concept of discrete-system simulation, a simple telephone system can be considered. The system consists of eight telephone lines connected to a switchboard through a fixed number of links. The switchboard uses links to establish connections between telephone lines. Each line can participate in only one connection at a time. In the basic model, the system follows a lost-call approach, where a call that cannot be connected immediately is abandoned. A call may be unsuccessful for two main reasons: the called line may already be engaged, resulting in a busy call, or all available links may be occupied, resulting in a blocked call.
The simulation is used to determine the proportion of calls that are successfully completed, blocked, or found busy. For example, if line 2 is connected to line 5 and line 4 is connected to line 7.

<img width="829" height="784" alt="image" src="https://github.com/user-attachments/assets/bae025c6-086a-4abb-ba46-38681b6eb42a" />

Each telephone line is modeled as an entity with an availability status. A value of 0 represents a free line, while 1 represents a busy line. Instead of maintaining detailed information about every individual link, the links are represented as a single entity with two main attributes: the maximum number of available links and the number currently in use.

The simulation also maintains a system clock to track events. In this model, one unit of simulation time represents one second. The clock advances to the time of the next scheduled event. Each call is represented as an entity with attributes such as its originating line, destination line, call duration, and completion time.
Call arrivals are generated using the bootstrap method. The simulation begins with the system state at time 1027 and continues by processing events in chronological order. The two primary events are new call arrivals and call disconnections. For example, the calls between lines 2 and 5 and between lines 4 and 7 are scheduled to finish at times 1053 and 1075, respectively, while another call is scheduled to arrive at time 1057.



### The simulation processes each event through the following steps:


  •	Identify the next event: The event with the earliest scheduled time is selected, and the simulation clock is updated.

  •	Determine the event type: The activity responsible for the event, such as a call arrival or disconnection, is identified.

  •	Check the event conditions: The system determines whether the event can be processed under the current state.

  •	Update the system state: Line availability, link utilization, and the calls-in-progress list are updated according to the event.

  •	Update statistics: Counters are maintained for processed, completed, blocked, and busy calls.



At time 1053, the call between lines 2 and 5 is completed. Both lines are released, the number of links in use is reduced, and the completed call is removed from the calls-in-progress list.

At time 1057, a new call arrives. The system checks the availability of the required line and links. Since line 7 is busy, the call cannot be connected and is therefore classified as a busy call. The processed-call and busy-call counters are updated accordingly. Another call arrives at time 1063 between lines 3 and 6 with a duration of 98 seconds, producing the system state shown below.

The next event is another call arrival. This time, the required resources are available, allowing the call to be successfully connected.

## Delayed Call

The previous model assumes that calls that cannot be connected immediately are lost. In the delayed-call model, however, such calls are placed in a waiting state until the required line or link becomes available.

To manage these calls, a separate delayed-call list is maintained in addition to the calls-in-progress list. When a call encounters a busy line or unavailable link, it is placed in the delayed list rather than being discarded. The system continues to track these calls until a connection becomes available.

