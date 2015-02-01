# keyboard
A chording keyboard, prototyped on Raspberry Pi, eventually to be hosted on an arduino

The keyboard and mouse comprise two handheld units that can be compared to bicycle
handgrips. Each is connected to a processing unit via a multi-conductor cable. Each
unit has one button for each of the four non-thumb fingers plus two buttons for each
thumb. Each button has a force-sensitive resistor, allowing the system to detect how
hard a button is pressed, which in turn allows one to assign multiple functions to
a button based on pressure.

In addition, the left unit (and perhaps the right as well?) contains an accelerometer,
allowing the system to detect orientation and possibly gestures. The going-in concept
is to implement a joystick-style mouse, where (when the system is in mouse-mode), the
user moves the mouse by tilting the left hand left, right, forward or back. Larger tilts
produce greater speeds, and specific button sequences will cause the mouse to go to
pre-assigned screen locations. Those locations could be manually set (e.g., move the
mouse to a location and then press an assignment sequence), or they could be set by the
GUI (e.g., the GUI detects the current application's menu bar and tells the keyboard
where the File, Edit, etc., menues are located).

## Components

### Analysis Software

Create an understanding of the relative frequency of characters:
context-free; given the previous (one? two?) characters typed;
given the currently-active application.

Software that maps characters to fingering assignments such that:

*  The most common keys are mapped to the easiest key assignments (e.g.,
   primary fingers, least chording, lowest pressure); and

*  Keys that share state space (e.g., are likely to be typed given the
   previous *N* characters, possibly in the given application) are
   mapped to dissimilar key assignments.

Software for the Raspberry Pi (RP) and/or Arduino that measures the rate
at which the buttons can be sampled, the rate at which buttons are
pressed and released, and the degree to which soft (S), medium (M), and
firm (F) presses can be distinguished.

### Runtime Software

#### Embedded Software

*  The code that samples the key presses and the accelerometer.

*  The code that estimates which character/keystrokes have been
   selected.

*  The code that sends the keystrokes and mouse-movements to the
   computer via USB.

*  Management software that interacts with a service on the computer.

#### On-Host Software

*  GUI software that runs on the computer and interacts with the
   keyboards management software. Provides graphical help if the
   user cannot figure out which key sequence is for a given character,
   reveals the key sequences that have been recently identified,
   outputs internal logs, allows one to run a self-test, and tells the
   keyboard which application is currently taking input, where the mouse
   currently is located, and where nearby menues, buttons and text areas
   are located.

## Organization

### Prototype/

Code that performs preliminary prototyping of functionality.

### ContentAnalysis/

Code that analyzes documents and source code for common character sequences,
plus Monte Carlo techniques for character-to-key assignment.

### GUI/

Code that visualizes the key assignments, tracks active application and
mouse location and infers the location of GUI artifacts like menues and
text areas.

### KeyPressTracking/

Embedded code that tracks button press and release plus the accelerometer.

### StateTracking/

Embedded code that performs state estimation and emits keystroke sequences.

### Comms/

Embedded code that interacts with the host via USB. Sends the keystrokes and
exchanges information with the GUI.
