package org.dvb.application;

public class RunningApplicationsFilter extends AppsDatabaseFilter {


public RunningApplicationsFilter() {
}

public boolean accept(AppID key) {
   AppProxy proxy = AppsDatabase.getAppsDatabase().getAppProxy(key);
   return proxy != null && proxy.getState() == AppProxy.STARTED;
}

}

