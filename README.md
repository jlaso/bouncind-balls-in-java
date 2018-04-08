This started as a UNED practice and I reviewed it trying to apply some best practices I learned so far.

I use IntelliJ IDEA from JetBrains, it's easy as clone the project and File -> New -> Project from existing sources...
but I think it can be compiled/opened with others IDEs

# Description

There is a playfield plenty of balls, each one with different color, radio and speed.

Since this is only a POC the measures are not exact, so speed it's just the
amount of pixels the ball moves each time, nothing to do with real speed.

# Keys

The user can press some keys in order to have some interaction with the board:

1) spacebar pauses and restarts the movement
1) "D" shows debug info on screen
1) "+" and "-"  increases and decreases the speed of frames
1) "T" decreases the transparency of the background (less transparency means less reminiscence or trail of
past movements)
1) "R" the opposite to "T",  transparency goes from 50 to 255
1) "P" moves the first ball to the right (0º direction)
1) "O" moves the first ball to the left  (180º direction)

# Notes

x,y and radius in the balls are double internally but rounded to int when shown, with maths cos and sin I
needed to have some resolution to not lose the new calculated direction in bouncing.