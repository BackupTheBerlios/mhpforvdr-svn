
package javax.tv.service;

//for pseudo-asynchronous requests

public class TrivialRequest implements SIRequest {

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

public boolean cancel () {
   return false;
}



}

