
package org.dvb.application;

/*A DVBHTMLProxy Object is a proxy to a DVBHTML application. */

public interface DVBHTMLProxy extends AppProxy {

/*
Loads the initial entry page of the application and waits for a signal.This method mimics the PREFETCH control code and 
is intended to be called instead of and not as well as start.Calling prefetch on a started application will have no 
effect. Throws: SecurityException -if the calling application does not have permission to start 
applications */
public void prefetch();


/*
Sends the application a start trigger at the speci  ed time. Parameters: starttime -the speci  ed time to send a start 
trigger to the application.If the time has already passed the application manager shall send the trigger 
immediately.Dates pre-epoch shall always cause the application manager to send the trigger immediately. Throws: 
SecurityException -if the calling application does not have permission to start 
applications */
public void startTrigger(java.util.Date starttime);


/*
Sends the application a trigger with the given payload at the speci  ed time.Parameters: time -the speci  ed time to 
send a start trigger to the application.If the time has already passed the application manager should send the trigger 
immediately.Dates pre-epoch shall always cause the application manager to send a 'now'trigger. triggerPayload -the speci 
 ed payload to deliver with the trigger.The payload is speci  ed as object,but this will be re  ned once DVB-HTML 
Triggers are properly de  ned. Throws: SecurityException -if the calling application does not have permission to start 
applications */
public void trigger(java.util.Date time, java.lang.Object triggerPayload);



}
