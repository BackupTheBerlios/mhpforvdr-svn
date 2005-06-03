
package org.havi.ui;

/*This HMatteLayer interface enables the presentation of components,together with an associated HMatte ,for matte 
compositing. */

public interface HMatteLayer {

/*
Get any HMatte currently associated with this component. Returns: the HMatte currently associated with this component or 
null if there is no associated matte. */
public HMatte getMatte();


/*
Applies an HMatte to this component,for matte compositing.Any existing animated matte must be stopped before this method 
is called or an HMatteException will be thrown. Parameters: m -The HMatte to be applied to this component --note that 
only one matte may be associated with the component,thus any previous matte will be replaced.If m is null,then any matte 
associated with the component is removed and further calls to getMatte()shall return null.The component shall behave as 
if it had a fully opaque HFlatMatte associated with it (i.e an HFlatMatte with the default value of 1.0.) Throws: 
HMatteException -if the HMatte cannot be associated with the component.This can occur: " if the specific matte type is 
not supported " if the platform does not support any matte type " if the component is associated with an already running 
HFlatEffectMatte or HImageEffectMatte . The exception is thrown even if m is null. See Also: 
HMatte */
public void setMatte(HMatte m);



}
