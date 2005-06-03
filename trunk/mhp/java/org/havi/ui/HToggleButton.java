package org.havi.ui;

import java.awt.Image;
import org.havi.ui.event.*;
import org.openmhp.util.LinkedList;

/**
* @author tejopa
* @date 5.3.2004
* @status fully implemented
* @module internal
*/
public class HToggleButton extends HGraphicButton implements HSwitchable {

	private boolean state = false;
	private HToggleGroup toggleGroup = null;
	private LinkedList hActionListeners;
	private String actionCommand = "";
	private boolean switchableState = false;
	private HSound unsetActionSound = null;
	private static HGraphicLook defaultLook = null;
	private Object[] graphicContent = new Object[8];

	public HToggleButton(){
		setDefaults();
    }

    public HToggleButton(Image image){
		setDefaults();
		setGraphicToAllStates(image);
    }

    public HToggleButton(Image image, boolean a_state, HToggleGroup group){
		setDefaults();
		setGraphicToAllStates(image);
		state = a_state;
		toggleGroup = group;
    }

    public HToggleButton(Image image, int x, int y, int width, int height){
		setDefaults();
		setGraphicToAllStates(image);
		setLocation(x,y);
		setSize(width,height);
    }

    public HToggleButton(Image image, int x, int y, int width, int height, boolean a_state){
		setDefaults();
		setGraphicToAllStates(image);
		setLocation(x,y);
		setSize(width,height);
		state = a_state;
    }

    public HToggleButton(Image imageNormal, Image imageFocused, Image imageActioned, Image imageNormalActioned, int x, int y, int width, int height, boolean a_state){
		setDefaults();
		setGraphicContent(imageNormal,NORMAL_STATE);
		setGraphicContent(imageFocused,FOCUSED_STATE);
		setGraphicContent(imageActioned,ACTIONED_STATE);
		setGraphicContent(imageNormalActioned,ACTIONED_STATE);
		setBounds(x,y,width,height);
		state = a_state;
    }

    public HToggleButton(Image imageNormal, Image imageFocused, Image imageActioned, Image imageNormalActioned, boolean a_state){
		setDefaults();
		setGraphicContent(imageNormal,NORMAL_STATE);
		setGraphicContent(imageFocused,FOCUSED_STATE);
		setGraphicContent(imageActioned,ACTIONED_STATE);
		setGraphicContent(imageNormalActioned,ACTIONED_STATE);
		state = a_state;
    }

    public HToggleButton(Image image, int x, int y, int width, int height, boolean a_state, HToggleGroup group){
		setDefaults();
		setBounds(x,y,width,height);
		toggleGroup = group;
		state = a_state;
    }

    public HToggleButton(Image imageNormal, Image imageFocused, Image imageActioned, Image imageNormalActioned, int x, int y, int width, int height, boolean a_state, HToggleGroup group){
		setDefaults();
		setGraphicContent(imageNormal,NORMAL_STATE);
		setGraphicContent(imageFocused,FOCUSED_STATE);
		setGraphicContent(imageActioned,ACTIONED_STATE);
		setGraphicContent(imageNormalActioned,ACTIONED_STATE);
		setBounds(x,y,width,height);
		state = a_state;
		toggleGroup = group;
    }

    public HToggleButton(Image imageNormal, Image imageFocused, Image imageActioned, Image imageNormalActioned, boolean a_state, HToggleGroup group){
		setDefaults();
		setGraphicContent(imageNormal,NORMAL_STATE);
		setGraphicContent(imageFocused,FOCUSED_STATE);
		setGraphicContent(imageActioned,ACTIONED_STATE);
		setGraphicContent(imageNormalActioned,ACTIONED_STATE);
		state = a_state;
		toggleGroup = group;
    }

	//* set defaults *//
	private void setDefaults() {
		setBounds(0,0,0,0);
		state = false;
		toggleGroup = null;
		try {
			setMatte(null);
		}
		catch (Exception e) { System.out.println(e); }
		setTextLayoutManager(null);
		setBackgroundMode(NO_BACKGROUND_FILL);
		setDefaultSize(null);
		setHorizontalAlignment(HALIGN_CENTER);
		setVerticalAlignment(HALIGN_CENTER);
		setResizeMode(RESIZE_NONE);
		setDefaultLook(null); // PLATFORM SPECIFIC LOOK
		try {
			setLook(getDefaultLook());
		}
		catch (Exception e) { System.out.println(e); }
		setGainFocusSound(null);
		setLoseFocusSound(null);
		setActionSound(null);
		setUnsetActionSound(null);
		hActionListeners = new LinkedList();
	}

	public void addHActionListener(HActionListener l) {
		hActionListeners.add(l);
	}

	public String getActionCommand() {
		return actionCommand;
	}

	public HSound getActionSound() {
		return super.getActionSound();
	}

    public static HGraphicLook getDefaultLook(){
        return defaultLook;
    }

    public HSound getGainFocusSound() {
    	return super.getGainFocusSound();
    }

    public HSound getLoseFocusSound() {
    	return super.getLoseFocusSound();
    }

    public HNavigable getMove(int keycode) {
    	return super.getMove(keycode);
    }

    public int[] getNavigationKeys() {
    	return super.getNavigationKeys();
    }

    public boolean getSwitchableState() {
    	return switchableState;
    }

    public HToggleGroup getToggleGroup() {
    	return toggleGroup;
    }

    public HSound getUnsetActionSound(){
        return unsetActionSound;
    }

    public boolean isSelected() {
    	return super.isSelected();
    }

    public void processHActionEvent(HActionEvent evt) {
    	super.processHActionEvent(evt);
    }

    public void processHFocusEvent(HFocusEvent evt) {
    	super.processHFocusEvent(evt);
    }

    public void removeHActionListener(HActionListener l) {
    	hActionListeners.remove(l);
    }

    public void removeToggleGroup() {
    	toggleGroup.remove(this);
    	toggleGroup = null;
    }

    public void setActionCommand(String command) {
    	actionCommand = command;
    }

    public void setActionSound(HSound sound) {
    	super.setActionSound(sound);
    }

    public static void setDefaultLook(HGraphicLook hlook){
		defaultLook = hlook;
    }

	public void setFocusTraversal(HNavigable up, HNavigable down, HNavigable left, HNavigable right) {
		super.setFocusTraversal(up,down,left,right);
	}

    public void setGainFocusSound(HSound sound) {
    	super.setGainFocusSound(sound);
    }

    public void setLoseFocusSound(HSound sound) {
    	super.setLoseFocusSound(sound);
    }

	public void setMove(int keycode, HNavigable target) {
		super.setMove(keycode,target);
	}

	public void setSwitchableState(boolean state) {
		switchableState = state;
	}

	public void setToggleGroup(HToggleGroup group) {
		removeToggleGroup();
		toggleGroup = group;
		toggleGroup.add(this);
	}

    public void setUnsetActionSound(HSound sound) {
    	unsetActionSound = sound;
    }


    protected void setGraphicToAllStates(Image image) {
    	for (int i=0;i<graphicContent.length;i++) {
    		graphicContent[i] = image;
    	}
    }








}
