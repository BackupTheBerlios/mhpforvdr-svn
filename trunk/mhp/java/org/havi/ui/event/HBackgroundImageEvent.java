
package org.havi.ui.event;

/*This event informs an application that a loading operation for an HBackgroundImage has  
nished.The parameters to the constructors are as follows,in cases where parameters are not 
used,then the constructor should use the default values. */

public class HBackgroundImageEvent extends java.util.EventObject {

/*
The loading failed before attempting to load any data from the  le.e.g.the  le not existing or due to a badly formed or 
otherwise broken  lename */
public static final int BACKGROUNDIMAGE_FILE_NOT_FOUND = 2;


/*
Marks the  rst integer for the range of background image events */
public static final int BACKGROUNDIMAGE_FIRST = 1;


/*
The loading failed because the data loaded is not valid.e.g.not a supported coding format for background 
images. */
public static final int BACKGROUNDIMAGE_INVALID = 4;


/*
The loading failed due to an error while loading the data.e.g.the  le is not accessible or loading of it was 
interrupted */
public static final int BACKGROUNDIMAGE_IOERROR = 3;


/*
Marks the last integer for the range of background image events */
public static final int BACKGROUNDIMAGE_LAST = 4;


/*
The loading succeeded */
public static final int BACKGROUNDIMAGE_LOADED = 1;

protected int id;

/*
Constructs a new HBackgroundImageEvent . Parameters: source -the HBackgroundImage which has been loaded. id -the type of 
event (one of BACKGROUNDIMAGE_LOADED , BACKGROUNDIMAGE_FILE_NOT_FOUND ,BACKGROUNDIMAGE_IOERROR or 
BACKGROUNDIMAGE_INVALID ). */
public HBackgroundImageEvent(java.lang.Object source, int _id) {
   super(source);
   id=_id;
}

/*
Returns the type for this event. Returns: the event type (one of BACKGROUNDIMAGE_LOADED ,BACKGROUNDIMAGE_FILE_NOT_FOUND 
, BACKGROUNDIMAGE_IOERROR or BACKGROUNDIMAGE_INVALID ). */
public int getID() {
   return id;
}

/*
Returns the HBackgroundImage for which the data has been loaded.Overrides: java.util.EventObject.getSource()in class 
java.util.EventObject Returns: the object which has been loaded. */
public java.lang.Object getSource() {
   return super.getSource();
}


}
