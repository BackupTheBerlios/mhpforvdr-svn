
package javax.tv.service.selection;
import javax.tv.locator.Locator;

/*

This exception is thrown when one or more service components are
 not valid for usage in a particular context.  If multiple service
 components are simultaneously invalid, this exception reports
 one of them.

*/
public class InvalidServiceComponentException extends ServiceContextException {

/*
 
 Constructs an InvalidServiceComponentException 
 with no detail message. 
 Parameters:  component - A locator indicating the offending service
 component. 
 
 
 */
 
Locator component;

public InvalidServiceComponentException ( Locator component){
   super();
   this.component=component;
}


/*
 
 Constructs an InvalidServiceComponentException with
 the specified detail message. 
 Parameters:  component - A locator indicating the offending service
 component. reason - The reason why this component is invalid. 
 
 */

public InvalidServiceComponentException ( Locator component,
                    java.lang.String reason){
   super(reason);
   this.component=component;
}


/*
 
 Reports the offending service components. 
 Returns: A locator indicating the service component
 that caused the exception. 
 
 
*/

public Locator  getInvalidServiceComponent (){
   return component;
}



}

