
package javax.tv.util;

/*

An event indicating that a timer specification has gone off.

*/
public class TVTimerWentOffEvent extends java.util.EventObject {

/*
 
 Creates a new TVTimerWentOffEvent with the specified timer and
 timer specification. 
 Parameters:  source - the timer that sent this event spec - the timer specification that went off 
 
 */
TVTimerSpec spec;
 
public TVTimerWentOffEvent ( TVTimer source,
              TVTimerSpec spec){
   super((Object)source);
   this.spec=spec;
}


/*
 
 Returns the timer specification for this event. 
 Returns: The TVTimerSpec for this event. 
 
 
*/

public TVTimerSpec  getTimerSpec (){
   return spec;
}



}

