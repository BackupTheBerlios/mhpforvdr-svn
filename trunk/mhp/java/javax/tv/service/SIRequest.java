
package javax.tv.service;

/*

An <code>SIRequest</code> object is used to cancel a pending
 asynchronous SI retrieval operation.  Individual asynchronous SI
 retrieval operations are identified by unique
 <code>SIRequest</code> objects generated at the time the operation
 is initiated.

*/
public interface SIRequest {

/*
 
 Cancels a pending SI retrieval request. If the request is still
 pending and can be canceled then the notifyFailure() 
 method of the SIRequestor that initiated the
 asynchronous retrieval will be called with the
 SIRequestFailureType code of
 CANCELED . If the request is no longer pending then no
 action is performed. 
 Returns:  true if the request was pending and
 successfully canceled; false otherwise. See Also:   SIRequestor.notifyFailure(javax.tv.service.SIRequestFailureType) , 
 SIRequestFailureType.CANCELED  
 
 
*/

public boolean cancel ();



}

