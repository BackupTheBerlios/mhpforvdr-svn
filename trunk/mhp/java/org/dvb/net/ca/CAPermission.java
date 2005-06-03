package org.dvb.net.ca;

/*This class is for CA permissions. A CAPermission contains a name, but no
actions list. A CAPermission contains a range of CA system ids and a speci c
permission for that range of CA system ids. Instead of a range of CA system ids,
the CAPermission can also refer to a single CA system id. The name has the
following syntax: CASystemIdRange ":" Permission where CASystemIdRange =
CASystemId [ "-" CASystemId ] | "*" and Permission = "MMI" | "buy" |
"entitlementInfo" | "messagePassing" | "*" Examples: " 0x1200-0x120A:buy (The
permission to buy entitlement for all the CA systems with ids between 0x1200 and
0x120A inclusive.) " 0x1201:entitlementInfo (The permission to get entitlement
information for the CA system with id 0x1201) " 0x120d:* (This wildcard
expresses all the permissions for the CA system with id 0x120d). Note: The
CASystemId is expressed as a hexadecimal value. The permission "MMI" corresponds
with the SecurityException on CAModuleManager.addMMIListener(). The permission
"buy" corresponds with the SecurityException on CAModule.buyEntitlement(). The
permission "entitlementInfo" corresponds with the SecurityException on
CAModule.queryEntitlement() and CAModule.listEntitlements(). The permission
"messagePassing" corresponds with CAModule.openMessageSession(MessageListener)*/
public class CAPermission extends java.security.BasicPermission {

/*Creates a new CAPermission with the speci ed name. The name is the symbolic
name of the CAPermission. Parameters: name - the name of the CAPermission*/
public CAPermission(java.lang.String name) {
   super(name);
}

/*Creates a new CAPermission object with the speci ed name. The name is the
symbolic name of the CAPermission, and the actions String is unused and should
be null. This constructor exists for use by the Policy object to instantiate new
Permission objects. Parameters: name - the name of the CAPermission actions -
should be null.*/
public CAPermission(java.lang.String name, java.lang.String actions) {
   super(name , actions);
}

}