package vdr.mhp;

//Class to make predefined error messages appear on OSD


public class Osd {

static Osd osd = new Osd();

public static void StartingFailed() {
   osd.startingFailed();
}
private native void startingFailed();

public static void LoadingFailed() {
   osd.loadingFailed();
}
private native void loadingFailed();

}