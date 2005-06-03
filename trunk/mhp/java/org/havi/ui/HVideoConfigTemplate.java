
package org.havi.ui;

/*The HVideoConfigTemplate class is used to obtain a valid HVideoConfiguration .An 
application instantiates one of these objects and then sets all non-default attributes as 
desired.The object is then passed to the getBestConfiguration(HVideoConfigTemplate)method 
found in the HVideoDevice class.If possible,a valid HVideoConfiguration is returned which 
meets or exceeds the requirements set in the HVideoConfigTemplate . This class may be 
subclassed to support additional properties of video con  gurations which may be requested 
by applications.The parameters to the constructors are as follows,in cases where 
parameters are not used,then the constructor should use the default 
values. */

public class HVideoConfigTemplate extends HScreenConfigTemplate {

/*
A value for use in the preference  eld of the setPreference(int, int)and getPreferencePriority(int)methods in the 
HVideoConfigTemplate that indicates that the device con  guration supports the display of graphics in addition to video 
streams.This display includes both con  gurations where the video pixels and graphics pixels are fully aligned (same 
size) as well as con  gurations where they are displayed together but where a more complex relationship exists between 
the two pixel coordinate spaces.The graphics con  guration for mixing is speci  ed as an HGraphicsConfiguration 
. */
public static final int GRAPHICS_MIXING = 0x0F;

/*
Creates an HVideoConfigTemplate object.See the class description for details of constructor parameters and default 
values. */
public HVideoConfigTemplate() {
}

/*
Returns a boolean indicating whether or not the speci  ed HVideoConfiguration can be used to create a video plane that 
supports the features set in this template. Parameters: hvc --the HVideoConfiguration object to test against this 
template. Returns: true if this HVideoConfiguration object can be used to create a video plane that supports the 
features set in this template,false otherwise. */
public boolean isConfigSupported(HVideoConfiguration hvc) {
   System.err.println("HVideoConfigTemplate.isConfigSupported only returning true");
   return true;
}

void checkPriority(int preference){
   switch (preference) {
   case VIDEO_GRAPHICS_PIXEL_ALIGNED:
   case PIXEL_ASPECT_RATIO:
   case PIXEL_RESOLUTION:
   case SCREEN_RECTANGLE:
   case GRAPHICS_MIXING:
      break;
   default:
      throw new IllegalArgumentException();
   }
}

}
