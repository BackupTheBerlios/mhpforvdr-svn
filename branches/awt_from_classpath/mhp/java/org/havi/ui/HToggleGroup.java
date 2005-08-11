package org.havi.ui;

import org.openmhp.util.LinkedList;

/**
* @author tejopa
* @date 5.3.2004
* @status fully implemented
* @module internal
*/
public class HToggleGroup{

	private boolean enabled = true;
	private boolean forcedSelection = false;
	private LinkedList buttons;

    public HToggleGroup(){
    	buttons = new LinkedList();
    }

    protected void add(HToggleButton button){
		button.setEnabled(isEnabled());
		if (buttons.size()==0&&getForcedSelection()) {
			button.setSwitchableState(true);	
		}
		buttons.add(button);
    }

    public HToggleButton getCurrent(){
        HToggleButton result = null;
        for (int i=0;i<buttons.size();i++) {
        	if (((HToggleButton)buttons.get(i)).isSelected()) {
        		result = (HToggleButton)buttons.get(i);	
        	}
    	}
    	return result;
    }

    public boolean getForcedSelection(){
		return forcedSelection;
    }

    public boolean isEnabled(){
		return enabled;
    }

	protected void remove(HToggleButton button){
		if (!buttons.has(button)) {
			throw new IllegalArgumentException("argument button does not belong to this HToggleGroup group");	
		}
		buttons.remove(button);
		
		if (button.isSelected()&&getForcedSelection()) {
			if (buttons.size()>0) {
				((HToggleButton)buttons.get(0)).setSwitchableState(true);	
			}	
		}
    }

    public void setCurrent(HToggleButton selection){
		if (!buttons.has(selection)) {
			for (int i=0;i<buttons.size();i++) {
				HToggleButton b = (HToggleButton)buttons.get(i);
				b.setSwitchableState(false);	
			}
			selection.setSwitchableState(true);
		}
    }

    public void setEnabled(boolean enable){
    	enabled = enable;
		for (int i=0;i<buttons.size();i++) {
			HToggleButton b = (HToggleButton)buttons.get(i);
			b.setEnabled(enable);	
		}
    }

    public void setForcedSelection(boolean forceSelection) {
		forcedSelection = forceSelection;
		if (forceSelection) {
			if (getCurrent()==null&&buttons.size()>0) {
				HToggleButton b = (HToggleButton)buttons.get(0);
				b.setSwitchableState(true);
			}
		}
    }

}

