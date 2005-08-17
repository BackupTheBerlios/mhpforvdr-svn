package java.awt.event;


// This interface contains the key constants
// because they are used not only by KeyEvent, but also
// by org.havi.ui.event.HRcEvent and org.dvb.event.UserEvent.
// All definitions are inherited from the two interfaces.

public interface KeyConstants 
extends KeyEventKeyConstants,
        RcKeyConstants
{

   public static final int VK_STOP = RcKeyConstants.VK_STOP;

}
