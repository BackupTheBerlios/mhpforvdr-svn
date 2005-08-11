package org.havi.ui;

import java.awt.*;
import org.openmhp.util.Out;

/*The HAnimateLook class is used by any HVisible component to display animated graphical content.This look will be 
provided by the platform and the exact way in which it is rendered will be platform dependant. The HAnimateLook class 
draws the content set on an HVisible .It uses the getAnimateContent(int)getAnimateContent(int " If the sequence is empty 
then the animation shall be treated as a completely transparent area of specific width and height within its enclosing 
Container --- simply for the purposes of layout management (if applicable). " If there is only one image referenced in 
the sequence then that image is rendered statically in a similar manner to HGraphicLook . " If there is more than one 
image referenced in the sequence then these are rendered in sequence, giving the effect of an animation. If a referenced 
image is inaccessible,then it shall be skipped.If no images are accessible,then the animation shall be treated as a 
completely transparent area of speci  c width and height within its enclosing Container ---for the purposes of layout 
management (if applicable). The HAnimateLook is not required to present consecutive images in the animation with the 
delay speci  ed in its associated HStaticAnimation .For example,if the time taken to retrieve or render an image is 
longer than the delay,then then it shall be rendered as soon as possible. Implementations of HAnimateLook should use the 
appropriate methods on HVisible to determine which scaling and alignment modes to use when rendering content.See the 
class description for HLook for more details.HAnimateLook may support scalable animated graphical content.As a 
minimum,all implementations must support the RESIZE_NONE scaling mode,and all alignment modes. Note that the results of 
applying the VALIGN_JUSTIFY and HALIGN_JUSTIFY alignment modes for animated graphical content are de  ned to identical 
to VALIGN_CENTER and HALIGN_CENTER modes respectively,as justi  cation is meaningless in this context. This is the 
default look that is used by HStaticAnimation and its subclasses.The parameters to the constructors are as follows,in 
cases where parameters are not used,then the constructor should use the default values. Default parameter values exposed 
in the constructors Parameter Description Default value Set method Get method None. Default parameter values not exposed 
in the constructors See Also: HStaticAnimation HVisible HLook Description Default value Set method Get method 
None. */

//taken from OpenMHP, license is LGPL
/**
* @author tejopa
* @date 6.3.2004
* @date 6.4.2004 fully implemented
* @status fully implemented
* @module internal
* @tested no
* @TODO better drawing, check maximum, preferred and minimum sizes
*/

public class HAnimateLook implements HLook {

      private Insets currentInsets = new Insets(0,0,0,0);

   public HAnimateLook(){
               Out.printMe(Out.TRACE);
      }

      /* tejopa 6.4.2004 */
   public java.awt.Insets getInsets(HVisible visible){
               Out.printMe(Out.TRACE);
      return currentInsets;
   }

      /* tejopa 6.4.2004 */
   public Dimension getMaximumSize(HVisible hvisible){
               Out.printMe(Out.TRACE);
      return hvisible.getSize();
   }

      /* tejopa 6.4.2004 */
   public Dimension getMinimumSize(HVisible hvisible){
               Out.printMe(Out.TRACE);
      return hvisible.getMinimumSize();
      }

      /* tejopa 6.4.2004 */
   public Dimension getPreferredSize(HVisible hvisible){
               Out.printMe(Out.TRACE);
      return hvisible.getDefaultSize();
   }

      /* tejopa 6.4.2004 */
   public boolean isOpaque(HVisible visible){
               Out.printMe(Out.TRACE);
      return visible.isOpaque();
   }
   
      /* tejopa 6.4.2004 */
   public void showLook(java.awt.Graphics g, HVisible hvisible, int state){
               
               Dimension d = hvisible.getSize();
      if (hvisible.getBackgroundMode()==HVisible.BACKGROUND_FILL){
            Color c = hvisible.getBackground();
            if(c != null){
               g.setColor(c);
               g.fillRect(0, 0, d.width, d.height);
            }
      }

               Image image = ((HStaticAnimation)hvisible).getCurrentImage();

      if (image!=null) {

            int x = 0;
                        int ha = hvisible.getHorizontalAlignment();
            int iw  = image.getWidth(hvisible);
            int w = hvisible.getWidth();

            if(ha==HVisible.HALIGN_CENTER||ha==HVisible.HALIGN_JUSTIFY){
               x = (w/2)-(iw/2);
            }
            if (ha==HVisible.HALIGN_LEFT){
               x = 0;
            }
            if (ha==HVisible.HALIGN_RIGHT){
               x = w-iw;
            }

            int y = 0;
            int va = hvisible.getVerticalAlignment();
            int h = hvisible.getHeight();
            int ih = image.getHeight(hvisible);

            if (va==HVisible.VALIGN_CENTER||va==HVisible.HALIGN_JUSTIFY) {
               y = (h/2)-(ih/2);
            }
            if (va==HVisible.VALIGN_TOP){
               y = 0;
            }
            if (va==HVisible.VALIGN_BOTTOM){
               y = h-ih;
            }

            g.drawImage(image, x, y, null);

      }
   }

      /* tejopa 6.4.2004 */
   public void widgetChanged (HVisible visible, HChangeData[] changes){
               Out.printMe(Out.TRACE);
               // do the minimum...
               visible.repaint();
   }
}
