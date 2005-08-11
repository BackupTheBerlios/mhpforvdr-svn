
package org.havi.ui;

/*The HVideoConfiguration class describes the characteristics (settings)of an HVideoDevice . 
There can be many HVideoConfiguration objects associated with a single HVideoDevice .The 
parameters to the constructors are as follows,in cases where parameters are not used,then 
the constructor should use the default values. */

public class HVideoConfiguration extends HScreenConfiguration {

private HVideoDevice device = null;


/*
It is not intended that applications should directly construct HVideoConfiguration objects. Creates an 
HVideoConfiguration object.See the class description for details of constructor parameters and default 
values. */
protected HVideoConfiguration(boolean flicker, boolean interlaced,
                                java.awt.Dimension aspectRatio, java.awt.Dimension resolution,
                                HScreenRectangle area,
                                HVideoDevice source) {
    super(flicker, interlaced, aspectRatio, resolution, area);
    this.device = source;
}

/*
Returns an HVideoConfigTemplate object that describes and uniquely identi  es this HVideoConfiguration . Hence,the 
following sequence should return the original HVideoConfiguration . HVideoDevice.getBestMatch(HVideoConfiguration 
.getConfigTemplate()) Features that are implemented in the HVideoConfiguration will return REQUIRED priority. Features 
that are not implemented in the HVideoConfiguration will return REQUIRED_NOT priority. Returns: an HVideoConfigTemplate 
object which both describes and uniquely identi  es this HVideoConfiguration 
. */
public HVideoConfigTemplate getConfigTemplate() {
   System.err.println("HVideoConfigTemplate.getConfigTemplate: Only returning empty template");
   return new HVideoConfigTemplate();
}

/*
Returns the HVideoDevice associated with this HVideoConfiguration. Returns: the HVideoDevice object that is associated 
with this HVideoConfiguration , */
public HVideoDevice getDevice() {
   return device;
}


}
