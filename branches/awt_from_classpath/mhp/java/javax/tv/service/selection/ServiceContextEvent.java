
package javax.tv.service.selection;

/*

The parent class for service context events.

*/
public class ServiceContextEvent extends java.util.EventObject {

/*
 
 Constructs the event. 
 Parameters:  source - The ServiceContext that generated the
 event. 
 
 */

public ServiceContextEvent ( ServiceContext source){
   super(source);
}


/*
 
 Reports the ServiceContext that generated the event. 
 Returns: The ServiceContext that generated the event. 
 
 
*/

public ServiceContext  getServiceContext (){
   return (ServiceContext)getSource();
}



}

