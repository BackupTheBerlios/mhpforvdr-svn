
package vdr.mhp;

public abstract class SettingsPolicy {

static SettingsPolicy policy;

static public SettingsPolicy getPolicy() {
   return policy;
}

static public void setPolicy(SettingsPolicy p) {
   policy = p;
}

public abstract boolean hasInternetAccess();


}