package org.havi.ui;

import java.awt.*;
import org.openmhp.util.Out;

/**
* @author tejopa
* @date 6.3.2004
* @status not implemented
* @module internal
* HOME
*/
public class HListGroupLook implements HAdjustableLook{

      public HListGroupLook(){
               Out.printMe(Out.TODO);
   }

   public void showLook(java.awt.Graphics g, HVisible visible, int state){
               //Out.printMe(Out.TRACE);
               if (visible.getBackground()!=null) {
                        g.setColor(visible.getBackground());
                        g.fillRect(0,0,visible.getWidth(),visible.getHeight());	
               }
               HListGroup listgroup = (HListGroup)visible;
               
               HListElement[] elements = listgroup.getListContent();

               FontMetrics fm = g.getFontMetrics(g.getFont());

            g.setFont(visible.getFont());
      g.setColor(visible.getForeground());

      int width = visible.getWidth();
      int height = visible.getHeight();
      int ascent = fm.getAscent();
      int descent = fm.getDescent();
      int leading = fm.getLeading();
      int stringHeight = (ascent + descent + leading);
                        
               int startX = 0;
               int startY = stringHeight;

               for (int lineNumber=0;lineNumber<elements.length;lineNumber++) {

                        String string = elements[lineNumber].getLabel();

                        int stringWidth = fm.stringWidth(string);
                        
                        switch (visible.getHorizontalAlignment()) {
                              case HVisible.HALIGN_CENTER:
                                       startX = (width/2)-(stringWidth/2); 
                                       break;
                              case HVisible.HALIGN_JUSTIFY: 
                                       startX = (width/2)-(stringWidth/2); 
                                       break;
                              case HVisible.HALIGN_LEFT: 
                                       startX = 0;
                                       break;	
                              case HVisible.HALIGN_RIGHT: 
                                       startX = width-stringWidth-0;
                                       break;																										
                        }

                        switch (visible.getVerticalAlignment()) {
                              case HVisible.VALIGN_CENTER:
                                       //startY = textHeight+((height-stringHeight)/2)-descent+lineNumber*stringHeight; 
                                       break;
                              case HVisible.VALIGN_JUSTIFY: 
                                       //startY = textHeight+((height-stringHeight)/2)-descent+lineNumber*stringHeight; 

                                       //startY = (height-textHeight)/2+lineNumber*stringHeight; 
                                       break;
                              case HVisible.VALIGN_TOP: 
                                       //startY = 0+lineNumber*stringHeight;
                                       break;	
                              case HVisible.VALIGN_BOTTOM: 
                                       //startY = height-0-textHeight+ascent+(lineNumber*stringHeight);
                              break;																										
                        }		
               

                        g.drawString(string,startX,startY);
                        startY+=stringHeight+4;	
               }
               
   }

   public void widgetChanged (HVisible visible, HChangeData[] changes){
               Out.printMe(Out.TODO);
   }

   public Dimension getMinimumSize(HVisible visible){
               Out.printMe(Out.TODO);
      return null;
   }

   public Dimension getPreferredSize(HVisible visible){
               Out.printMe(Out.TODO);
      return null;
   }

   public Dimension getMaximumSize(HVisible visible){
               Out.printMe(Out.TODO);
      return null;
   }

   public boolean isOpaque(HVisible visible){
               Out.printMe(Out.TODO);
      return false;
   }

   public java.awt.Insets getInsets(HVisible visible){
               Out.printMe(Out.TODO);
      return null;
   }

   public int hitTest(HOrientable component, java.awt.Point pt){
               Out.printMe(Out.TODO);
      return -1;
   }

   public java.lang.Integer getValue(HOrientable component, java.awt.Point pt){
               Out.printMe(Out.TODO);
      return null;
   }

   public java.awt.Insets getElementInsets(){
               Out.printMe(Out.TODO);
               return null;
   }

   public int getNumVisible(HVisible visible){
               Out.printMe(Out.TODO);
               return -1;
   }
   
/*
Returns a value which indicates the pointer click position in the on-screen representation of the adjustable 
component.Note that it is a valid implementation option to always return ADJUST_NONE . Parameters: component -the 
HAdjustmentValue component for which the hit position should be calculated. pt -the pointer click point. Returns: one of 
ADJUST_NONE ,ADJUST_BUTTON_LESS ,ADJUST_PAGE_LESS ,ADJUST_THUMB , ADJUST_PAGE_MORE or 
ADJUST_BUTTON_MORE */
   public int hitTest(HAdjustmentValue component, java.awt.Point pt) {
      Out.printMe(Out.TODO);
      return ADJUST_NONE;
   }

}
