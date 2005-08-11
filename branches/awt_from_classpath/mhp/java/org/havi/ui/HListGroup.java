package org.havi.ui;

import java.awt.*;
import java.awt.event.*;
import org.openmhp.util.*;


/**
* @author tejopa
* @date 6.3.2004 not implemented
* @date 7.4.2004 partially implemented
* @status partially implemented
* @module internal
* @priority high
* @TODO create events
* @tested no
* HOME
*/
public class HListGroup extends HVisible implements HItemValue {

    public static final int ITEM_NOT_FOUND       = -1;
    public static final int ADD_INDEX_END        = -1;
    public static final int DEFAULT_LABEL_WIDTH  = 100;
    public static final int DEFAULT_LABEL_HEIGHT = 30;
    public static final int DEFAULT_ICON_WIDTH   = 100;
    public static final int DEFAULT_ICON_HEIGHT  = 30;

	public static HListGroupLook defaultLook = new HListGroupLook();

	private LinkedList elements;
	private LinkedList selection;	

	private LinkedList itemListeners;
	private LinkedList focusListeners;
	private LinkedList targets;
	private LinkedList keys;

	private int currentIndex = -1;
	private int orientation = HOrientable.ORIENT_TOP_TO_BOTTOM;
	
	private int scrollPosition = -1;

	private Dimension labelSize = new Dimension(30,100);
	private Dimension iconSize = new Dimension(30,100);

	private boolean selectionMode = false;
	private HSound selectionSound = null;
	
    private HSound gainFocusSound;
    private HSound loseFocusSound;
	
	private boolean multiSelection = false;

    public HListGroup(){
		Out.printMe(Out.TRACE,"const 1");
		look = new HListGroupLook();
    }

    public HListGroup(HListElement[] items){
    	Out.printMe(Out.TRACE,"const 2");
    	elements = new LinkedList();
    	selection = new LinkedList();
    	for (int i=0;i<items.length;i++) {
    		elements.add(items[i]);	
    		selection.add(new Boolean(false));
    	}
		look = new HListGroupLook();
    }

    public HListGroup(HListElement[] items, int x, int y, int width, int height){
		Out.printMe(Out.TRACE,"const 3");
    	setLocation(x,y);
    	setSize(width,height);
		look = new HListGroupLook();
    }
    

    public void setLook(HLook hlook) throws HInvalidLookException{
		Out.printMe(Out.FIXME,"check if HLook is valid.");
		if (hlook!=null) { 
			Out.printMe(Out.FIXME,"Now set: "+hlook.toString());
		}
		else {
			Out.printMe(Out.FIXME,"Given hlook was NULL!");
		}
		look = hlook;
    }

    public static void setDefaultLook(HListGroupLook look){
		Out.printMe(Out.TRACE);
		defaultLook = look;
    }

    public static HListGroupLook getDefaultLook(){
		Out.printMe(Out.TRACE);
        return defaultLook;
    }

    public HListElement[] getListContent(){
		//Out.printMe(Out.TRACE);
		if (elements==null) return null;
		Object[] temp = elements.getElements();
		HListElement[] result = new HListElement[temp.length];
		for (int i=0;i<temp.length;i++) {
			result[i] = (HListElement)temp[i];	
		}
        return result;
    }

    public void setListContent(HListElement[] items){
		Out.printMe(Out.TRACE,"items.length: "+items.length);
    	elements = new LinkedList();
    	selection = new LinkedList();
    	for (int i=0;i<items.length;i++) {
    		elements.add(items[i]);
    		selection.add(new Boolean(false));	
    	}
    }

    public void addItem(HListElement item, int index){
		Out.printMe(Out.TRACE);
		if (elements == null) {
			elements = new LinkedList();
			selection = new LinkedList();
			elements.add(item);
			selection.add(new Boolean(false));	
		}
		if (index==ADD_INDEX_END) {
			elements.addLast(item);
			selection.addLast(new Boolean(false));
		} else {
			elements.addTo(index,item);
			selection.addTo(index,new Boolean(false));
    	}
    }

    public void addItems(HListElement[] items, int index){
		Out.printMe(Out.TRACE);
		if (elements == null) {
			elements = new LinkedList();
			selection = new LinkedList();
			index = 0;
		}
		if (index==ADD_INDEX_END) {
    		for (int i=0;i<items.length;i++) {
    			elements.addLast(items[i]);
    			selection.addLast(new Boolean(false));	
    		}
		} else {
    		for (int i=0;i<items.length;i++) {
    			elements.addTo(index+i,items[i]);
    			selection.addTo(index+i,new Boolean(false));	
    		}
    	}		
    }

    public HListElement getItem(int index){
		Out.printMe(Out.TRACE);
        if (elements!=null) {
	        if (index<0) throw new java.lang.IllegalArgumentException("index was negative");
    	    if (index>elements.size()) return null;
        	return (HListElement)elements.get(index);
        }
        return null;
    }

    public int getIndex (HListElement item){
		Out.printMe(Out.TRACE);
        if (elements!=null) {
			for (int i=0;i<elements.size();i++) {
				if ((HListElement)elements.get(i)==item) {
					return i;	
				}	
			}
		}
        return -1;
    }

    public int getNumItems(){
		Out.printMe(Out.TRACE);
		if (elements==null) return 0;
        return elements.size();
    }

    public HListElement removeItem(int index){
		Out.printMe(Out.TRACE);
		if (elements==null) return null;
		if (index<0||index>elements.size()) return null;
        HListElement temp = (HListElement)elements.get(index);
        elements.remove(index);
        selection.remove(index);
        return temp;
    }

    public void removeAllItems(){
		Out.printMe(Out.TRACE);
		elements.clear();
    }

    public int getCurrentIndex(){
		Out.printMe(Out.TRACE);
        return currentIndex;
    }

    public HListElement getCurrentItem(){
		Out.printMe(Out.TRACE);
        return (HListElement)elements.get(currentIndex);
    }

    public boolean setCurrentItem(int index){
		Out.printMe(Out.TRACE);
		if (index==currentIndex) return false;
        if (index<0) return false;
        if (index>elements.size()) return false;
        currentIndex = index;
        return true;
    }

    public int[] getSelectionIndices(){
		Out.printMe(Out.TRACE);
		int s = getNumSelected();
		if (s==0) { return null; }
		int[] result = new int[s];
		int ri = 0;
		for (int i=0;i<selection.size();i++) {
			if (isItemSelected(i)) {
				result[ri]=i;
				ri++;
			}
		}
        return result;    }

    public HListElement[] getSelection(){
		Out.printMe(Out.TRACE);
		int s = getNumSelected();
		if (s==0) { return null; }
		HListElement[] result = new HListElement[s];
		int ri = 0;
		for (int i=0;i<selection.size();i++) {
			if (isItemSelected(i)) {
				result[ri]=(HListElement)elements.get(i);
				ri++;
			}
		}
        return result;
    }

    public void clearSelection(){
		Out.printMe(Out.TRACE);
		if (selection!=null) {
			int selectionSize = selection.size();
			selection.clear();
			for (int i=0;i<selectionSize;i++) {
				selection.add(new Boolean(false));	
			}
		}
    }

    public int getNumSelected(){
		Out.printMe(Out.TRACE);
		int result = 0;
		for (int i=0;i<selection.size();i++) {
			if (isItemSelected(i)) {
				result++;	
			}
		}
        return result;
    }

    public boolean getMultiSelection(){
		Out.printMe(Out.TRACE);
        return multiSelection;
    }

    public void setMultiSelection(boolean multi){
		Out.printMe(Out.TRACE,""+multi);
		multiSelection = multi;
    }

    public void setItemSelected(int index, boolean sel){
		Out.printMe(Out.TRACE);
		if (elements==null) throw new IllegalArgumentException();
		if (index<0||index>elements.size()) throw new IllegalArgumentException();
		selection.remove(index);
		selection.addTo(index,new Boolean(sel));
    }

    public boolean isItemSelected(int index){
		Out.printMe(Out.TRACE);
		if (elements==null) throw new IllegalArgumentException();
		if (index<0||index>elements.size()) throw new IllegalArgumentException();        
        return ((Boolean)selection.get(index)).booleanValue();
    }

    public int getScrollPosition(){
		Out.printMe(Out.TRACE);
        return scrollPosition;
    }

    public void setScrollPosition(int scroll){
		Out.printMe(Out.TRACE);
		if (elements!=null) {
			if (scroll<0) {
				throw new IllegalArgumentException("list is empty");	
			}
			if (scroll>elements.size()) {
				throw new IllegalArgumentException("list is empty");	
			}
			scrollPosition = scroll;
		}
		else {
			throw new IllegalArgumentException("list is empty");	
		}
    }

    public Dimension getIconSize(){
		Out.printMe(Out.TRACE);
        return iconSize;
    }

    public void setIconSize(Dimension size){
		Out.printMe(Out.TRACE);
		iconSize = size;
    }

    public Dimension getLabelSize(){
		Out.printMe(Out.TRACE);
        return labelSize;
    }

    public void setLabelSize(Dimension size){
		Out.printMe(Out.TRACE);
		labelSize = size;
    }

    public void setMove(int keyCode, HNavigable target){
		Out.printMe(Out.TRACE);
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
		Out.printMe(Out.TRACE);
		int index = keys.indexOf(new Integer(keyCode));
		if (index!=-1) {
			return (HNavigable)targets.get(index);	
		}
		else {
			return null;	
		}
    }

    public void setFocusTraversal(HNavigable up, HNavigable down, HNavigable left, HNavigable right){
		Out.printMe(Out.TRACE);  
        setMove(KeyEvent.VK_UP, up);
        setMove(KeyEvent.VK_DOWN, down);
        setMove(KeyEvent.VK_LEFT, left);
        setMove(KeyEvent.VK_RIGHT, up);
    }

    public boolean isSelected(){
		Out.printMe(Out.TRACE);    	
        return hasFocus();
    }

    public void setGainFocusSound(HSound sound){
		Out.printMe(Out.TRACE);  
        gainFocusSound = sound;
    }

    public void setLoseFocusSound(HSound sound){
		Out.printMe(Out.TRACE);  
        loseFocusSound = sound;
    }

    public HSound getGainFocusSound(){
		Out.printMe(Out.TRACE);  
        return gainFocusSound;
    }

    public HSound getLoseFocusSound(){
		Out.printMe(Out.TRACE);  
        return loseFocusSound;
    }

    public void addHFocusListener(org.havi.ui.event.HFocusListener l){
		Out.printMe(Out.TRACE);
		if (focusListeners==null) { focusListeners = new LinkedList(); }
		if (l!=null) focusListeners.add(l);
	}

    public void removeHFocusListener(org.havi.ui.event.HFocusListener l){
		Out.printMe(Out.TRACE);
		if (focusListeners!=null) { 
			if (l!=null) focusListeners.remove(l);
    	}
    }

    public int[] getNavigationKeys(){
		Out.printMe(Out.TRACE);    	
		int[] result = null;
		if (keys!=null) {
			result = new int[keys.size()];
			for (int i=0;i<keys.size();i++) {
				result[i] = ((Integer)keys.get(i)).intValue();
			}
        }
		return result;
    }

    public int getOrientation(){
		Out.printMe(Out.TRACE);
        return orientation;
    }

    public void setOrientation(int orient){
		Out.printMe(Out.TRACE);
		if (orient==HOrientable.ORIENT_LEFT_TO_RIGHT||orient==HOrientable.ORIENT_RIGHT_TO_LEFT||
			orient==HOrientable.ORIENT_TOP_TO_BOTTOM||orient==HOrientable.ORIENT_BOTTOM_TO_TOP) {
				orientation = orient;
		}		
	}

    public void addItemListener(org.havi.ui.event.HItemListener l){
		Out.printMe(Out.TRACE);
		if (itemListeners==null) { itemListeners = new LinkedList(); }
		if (l!=null) itemListeners.add(l);
    }

    public void removeItemListener(org.havi.ui.event.HItemListener l){
		Out.printMe(Out.TRACE);
		if (itemListeners!=null) {
			if (l!=null) itemListeners.remove(l);
		}
    }

    public void setSelectionSound(HSound sound){
		Out.printMe(Out.TRACE);
		selectionSound = sound;
    }

    public HSound getSelectionSound(){
		Out.printMe(Out.TRACE);
        return selectionSound;
    }

    public boolean getSelectionMode(){
		Out.printMe(Out.TRACE);
        return selectionMode;
    }

    public void setSelectionMode(boolean edit){
		Out.printMe(Out.TRACE);
		selectionMode = edit;
    }




    public void processHItemEvent(org.havi.ui.event.HItemEvent evt){
		Out.printMe(Out.TODO);
	}

    public void processHFocusEvent(org.havi.ui.event.HFocusEvent evt){
		Out.printMe(Out.TODO);
    }

/*
	public void paint(Graphics g) {
		System.out.println("pain");
		g.fillRect(0,0,getWidth(),getHeight());
	}
*/

}

