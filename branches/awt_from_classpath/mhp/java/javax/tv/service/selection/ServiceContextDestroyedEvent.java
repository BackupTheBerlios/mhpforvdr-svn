
package javax.tv.service.selection;

/*

<code>ServiceContextDestroyedEvent</code> is generated when a
 <code>ServiceContext</code> is destroyed via its
 <code>destroy()</code> method.

*/
public class ServiceContextDestroyedEvent extends ServiceContextEvent {

/*
 
 Constructs the event. 
 Parameters:  source - The ServiceContext that was destroyed. 
 
 */

public ServiceContextDestroyedEvent ( ServiceContext source){
   super(source);
}



}

