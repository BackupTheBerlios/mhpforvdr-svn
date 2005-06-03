
package org.davic.mpeg;

/*NotAuthorizedInterface shall be implemented by classes which can report failure to access 
broadcast content due to failure to descramble that content. The interface provides an 
ability for an application to find out some information about the reason for the 
failure. */

public interface NotAuthorizedInterface {

/*
Major reason - access may be possible under certain conditions. */
public static final int POSSIBLE_UNDER_CONDITIONS = 0;


/*
Major reason - access not possible */
public static final int NOT_POSSIBLE = 1;


/*
Minor reason for POSSIBLE_UNDER_CONDITIONS - user dialog needed for payment */
public static final int COMMERCIAL_DIALOG = 1;


/*
Minor reason for POSSIBLE_UNDER_CONDITIONS - user dialog needed for maturity */
public static final int MATURITY_RATING_DIALOG = 2;


/*
Minor reason for POSSIBLE_UNDER_CONDITIONS - user dialog needed for technical purposes. */
public static final int TECHNICAL_DIALOG = 3;


/*
Minor reason for POSSIBLE_UNDER_CONDITIONS - user dialog needed to explain about free 
preview. */
public static final int FREE_PREVIEW_DIALOG = 4;


/*
Minor reason for NOT_POSSIBLE - user does not have an entitlement */
public static final int NO_ENTITLEMENT = 1;


/*
Minor reason for NOT_POSSIBLE - user does not have suitable maturity */
public static final int MATURITY_RATING = 2;


/*
Minor reason for NOT_POSSIBLE - a technical reason of some kind */
public static final int TECHNICAL = 3;


/*
Minor reason for NOT_POSSIBLE - not allowed for geographical reasons */
public static final int GEOGRAPHICAL_BLACKOUT = 4;


/*
Minor reason for both POSSIBLE_UNDER_CONDITIONS and NOT_POSSIBLE. Another reason. */
public static final int OTHER = 5;


/*
The component to which access was refused was a MPEG Program/DVB Service */
public static final int SERVICE = 0;


/*
Access was refused to one or more elementary streams. */
public static final int ELEMENTARY_STREAM = 1;


/*
Returns: SERVICE or ELEMENTARY_STREAM to indicate that either a service (MPEG program) or one or more elementary streams 
could not be descrambled. */
public abstract int getType();


/*
If getType() returns SERVICE, then this method returns the Service that could not be descrambled. Otherwise it returns 
null. Returns: either the Service that could not be descrambled or null */
public abstract Service getService();


/*
If getType() returns ELEMENTARY_STREAM, then this method returns the set of ElementaryStreams that could not be 
descrambled. Otherwise it returns null. Returns: either the set of ElementaryStreams that could not be descrambled or 
null */
public abstract ElementaryStream[] getElementaryStreams();


/*
Returns the reason(s) why descrambling was not possible. Parameters: index - If the component to which access failed is 
a Service, index shall be 0. Otherwise index shall refer to one stream in the set returned by getElementaryStreams(). 
Returns: an array of length 2 where the first element of the array is the major reason and the second element of the 
array is the minor reason. Throws: IndexOutOfBoundsException If the component to which access failed is a Service, this 
exception will be thrown if index is non zero. If the component(s) to which access failed was a (set of) elementary 
streams then this exception will be thrown when index is not a valid index into the array returned by 
getElementaryStreams. See Also: getElementaryStreams */
public abstract int[] getReason(int index) throws IndexOutOfBoundsException;



}
