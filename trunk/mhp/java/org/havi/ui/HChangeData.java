
package org.havi.ui;

/*A class used as argument for widgetChanged(HVisible, HChangeData[]).The hint constants are 
de  ned on HVisible . */

public class HChangeData {

/*
The data object for this HChangeData.The types of this object for the different hints are de  ned on HVisible 
. */
public java.lang.Object data;


/*
The hint for this HChangeData.The hint constants are de  ned on HVisible . */
public int hint;


/*
Creates an HChangeData object. Parameters: hint -the hint constant for this HChangeData. data -the data object for this 
HChangeData. */
public HChangeData(int _hint, java.lang.Object _data) {
   hint=_hint;
   data=_data;
}


}
