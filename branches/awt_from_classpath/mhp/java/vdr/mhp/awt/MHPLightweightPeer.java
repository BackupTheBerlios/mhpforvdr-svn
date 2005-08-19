
package vdr.mhp.awt;

import java.awt.Component;
import java.awt.Graphics;

// GLightweightPeer is the peer for all lightweight components and implements ComponentPeer.
// In MHP all component except the toplevel MHPPlanes are lightweight.

public class MHPLightweightPeer extends gnu.java.awt.peer.GLightweightPeer {

private Component comp;

public MHPLightweightPeer(Component comp) {
   super(comp);
   this.comp = comp;
}

public Graphics getGraphics() {
   // The implementation GLieghtweightPeer is
   //return null;
   // which tells Component that this is lightweight,
   // Component will take the parent's graphics and translate it.
   // For DirectFB there is a more elegant method:
   return MHPNativeGraphics.createClippedGraphics(comp);
}


}

