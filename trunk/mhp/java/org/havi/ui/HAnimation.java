package org.havi.ui;

import java.awt.Image;
import java.awt.event.*;
import org.openmhp.util.*;
import org.havi.ui.event.*;

/**
* @author tejopa
* @date 6.3.2004
* @date 6.4.2004
* @status fully implemented
* @module internal
* @tested no
* HOME
*/
public class HAnimation extends HStaticAnimation implements HNavigable{

      private LinkedList targets;
      private LinkedList keys;

      private Image[] images = null;
      private Image[] imagesNormal = null;
      private Image[] imagesFocused = null;

   private HSound gainFocusSound;
   private HSound loseFocusSound;
   private HFocusListener focusListener;

      public static HAnimateLook defaultLook = null;

   public HAnimation(){
      super();
      setDefaults();	
   }

   public HAnimation(Image[] images, int delay, int playMode, int repeatCount, int x, int y, int width, int height){
               super(images,delay,playMode,repeatCount,x,y,width,height);
   }

   public HAnimation(Image[] imagesNormal, Image[] imagesFocused, int delay, int playMode, int repeatCount, int x, int y, int width, int height){
               super(null,delay,playMode,repeatCount,x,y,width,height);
      this.imagesNormal = imagesNormal;
      this.imagesFocused = imagesFocused;
   }

   public HAnimation(Image[] images, int delay, int playMode, int repeatCount){
               super(images,delay,playMode,repeatCount,0,0,0,0);
   }

   public HAnimation(Image[] imagesNormal, Image[] imagesFocused, int delay, int playMode, int repeatCount){
               super(null,delay,playMode,repeatCount,0,0,0,0);
      this.imagesNormal = imagesNormal;
      this.imagesFocused = imagesFocused;			
   }

      private void setDefaults() {
               setLocation(0,0);
               setSize(0,0);
               setDelay(1);
               setRepeatCount(HAnimateEffect.REPEAT_INFINITE);
               setPlayMode(HAnimateEffect.PLAY_REPEATING);
      }

   public static void setDefaultLook(HAnimateLook hlook){
               Out.printMe(Out.TRACE);
               defaultLook = hlook;
   }

   public static HAnimateLook getDefaultLook(){
               Out.printMe(Out.TRACE);
      return defaultLook;
   }

      public void setMove(int keyCode, HNavigable target){
      if (keys==null) {
               targets = new LinkedList();
               keys   = new LinkedList();	
      }

      Integer newcode = new Integer(keyCode);
               int index = keys.indexOf(newcode);
               
               if (index!=-1) {
                        keys.remove(keys.get(index));
                        targets.remove(targets.get(index));
               }
               keys.add(newcode);
               targets.add(target);
   }


   public HNavigable getMove(int keyCode){
               int index = keys.indexOf(new Integer(keyCode));
               if (index!=-1) {
                        return (HNavigable)targets.get(index);	
               }
               else {
                        return null;	
               }
   }
   
   
   public void setFocusTraversal(HNavigable up, HNavigable down, HNavigable left, HNavigable right){
      setMove(KeyEvent.VK_UP, up);
      setMove(KeyEvent.VK_DOWN, down);
      setMove(KeyEvent.VK_LEFT, left);
      setMove(KeyEvent.VK_RIGHT, up);
   }
      
      public boolean isSelected(){
      return hasFocus();
   }

   public void setGainFocusSound(HSound sound){
      gainFocusSound = sound;
   }

   public void setLoseFocusSound(HSound sound){
      loseFocusSound = sound;
   }

   public HSound getGainFocusSound(){
      return gainFocusSound;
   }

   public HSound getLoseFocusSound(){
      return loseFocusSound;
   }


   public synchronized void addHFocusListener(HFocusListener listener){
      if (listener == null) {
            return;
      }
      focusListener = HEventMulticaster.add(focusListener, listener);
   }

   public synchronized void removeHFocusListener(HFocusListener listener) {
      if (listener == null) {
            return;
      }
      focusListener = HEventMulticaster.remove(focusListener, listener);
   }
   
   
   public int[] getNavigationKeys(){
               int[] result = null;
               if (keys!=null) {
                        result = new int[keys.size()];
                        for (int i=0;i<keys.size();i++) {
                              result[i] = ((Integer)keys.get(i)).intValue();
                        }
      }
               return result;
   }

   public void processHFocusEvent(HFocusEvent evt) {
               Out.printMe(Out.TODO);
   }
}
