
package org.havi.ui;

/*The HAnimateEffect interface de  nes effect constants and controls for time-varying animations. Implementations of 
HAnimateEffect should have the following default behaviors: " By default the HAnimateEffect should be stopped. Hence, to 
start an HAnimateEffect the start() method must be explicitly invoked. This mechanism allows for animations that are 
programmati-cally controlled, e.g. via the setPosition(int) method. " By default the position for rendering should be 
the first image in the sequence, i.e. 0. " By default the play mode should be PLAY_REPEATING . " By default the repeat 
count should be REPEAT_INFINITE . " The default rendering should simply display the single image at the current position 
of the animation within the sequence. */

public interface HAnimateEffect {

/*
Indicates that the animation should be played in a repeating loop,alternating between the forward and reverse direction. 
The images are rendered in the same order that they are present in the sequence (array): 0, 1, 2, 3, ... length-2, 
length-1 If the animation has not repeated suf  ciently,then the rendering of the sequence is reversed -i.e.the images 
are rendered in the order length-2, length-3, ... 1, 0 If the animation has not repeated suf  ciently,then the rendering 
of the sequence is reversed (again) back to a forwards direction.I.e.the images are rendered in the order 1, 2, 3, ... 
length-2, length-1 Each rendering of the sequence of images forwards or backwards,should be considered as a single 
"repeat". Note that when the sequence repeats,the last image ( rst image)is not rendered consecutively,i.e. twice. 
 */
public static final int PLAY_ALTERNATING = 2;


/*
Indicates that the animation should be played forwards (in a repeating loop). The images are rendered in the same order 
that they are present in the sequence (array): 0, 1, 2, 3, ... length-1 If the animation has not repeated suf  
ciently,then the rendering of the sequence is restarted from the  rst image,i.e.the images will continue to be rendered 
in the order: 0, 1, 2, 3, ... length-1 Each rendering of the sequence of images 0 to (length-1),should be considered as 
a single "repeat". */
public static final int PLAY_REPEATING = 1;


/*
This value,when passed to setRepeatCount indicates that the animation shall repeat until the stop()method is 
invoked. */
public static final int REPEAT_INFINITE = -1;


/*
Gets the presentation delay for this HAnimateEffect . Returns: the presentation delay in units of 0.1 
seconds. */
public int getDelay();


/*
Gets the playing mode for this HAnimateEffect . Returns: the play mode for this 
HAnimateEffect */
public int getPlayMode();


/*
Get the current index into the content array which this HAnimateEffect is using to display content. Returns: the index 
of the content currently being displayed,in the range 0 <= index < length */
public int getPosition();


/*
Gets the number of times that this HAnimateEffect is to be played.Note that this method does not return the number of 
repeats that are remaining to be played. Returns: the total number of times that an HAnimateEffect is to be played.The 
returned value shall be greater than zero,or REPEAT_INFINITE . */
public int getRepeatCount();


/*
This method indicates the animation (running)state of the HAnimateEffect . Returns: true if this HAnimateEffect is 
running,i.e.the start method has been invoked -false otherwise. */
public boolean isAnimated();


/*
Sets the delay between the presentation of successive pieces of content (frames). After calling setDelay(int)on a 
currently playing HAnimateEffect ,there is no guarantee that one or more frames will not be displayed using the previous 
delay until the new delay value takes effect. Parameters: count -the content presentation delay in units of 0.1 seconds 
duration.If count is less than one "unit",then it shall be treated as if it were a delay of one "unit",i.e.0.1 
seconds. */
public void setDelay(int count);


/*
Sets the playing mode for this HAnimateEffect .If the animation is already running a call to setPlayMode will change the 
current value and affect the animation immediately.The position of the animation is unchanged. Parameters: mode -the 
play mode for this HAnimateEffect ,which must be either PLAY_ALTERNATING or PLAY_REPEATING 
. */
public void setPlayMode(int mode);


/*
Set this HAnimateEffect to display the content at the speci  ed position.If the animation is already running a call to 
setPosition will change the current value and affect the animation immediately. Parameters: position -an index into the 
content array which speci  es the next piece of content to be displayed.If position is less than 0,then the array 
element at index 0 is displayed,if position is greater than or equal to the length of the content array,then the array 
element at index [length 1 ] will be used.. */
public void setPosition(int position);


/*
Sets the number of times that this HAnimateEffect should be played.If the animation is already running a call to 
setRepeatCount will change the current value and reset the current number of repeats to 0,affecting the animation 
immediately.Parameters: count -the number of times that an HAnimateEffect should be played.Valid values of the repeat 
count are one or more,and REPEAT_INFINITE */
public void setRepeatCount(int count);


/*
This method starts this HAnimateEffect playing.If start is called when the animation is already running it resets the 
animation according to the current play mode,as returned by getPlayMode(). */
public void start();


/*
This method indicates that the running HAnimateEffect should be stopped.After calling this method,there is no guarantee 
that one or more frames will not be displayed before the animation actually stops playing.If the animation is already 
stopped further calls to stop have no effect. */
public void stop();



}
