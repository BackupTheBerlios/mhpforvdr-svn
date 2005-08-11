/***********************************************************************************
 *
 * THIS SOFTWARE IS COPYRIGHT STEVEN MORRIS 2002. ALL RIGHTS RESERVED
 *
 * THIS SOFTWARE IS FREE FOR NON COMMERCIAL USE FOR THE PURPOSE OF LEARNING MHP.  ANY
 * USE FOR OTHER PURPOSES IS PROHIBITED UNLESS WRITTEN AGREEMENT IS OBTAINED.
 *
 * DISTRIBUTION OF THIS CODE IS PROHIBITED
 */

// Import the HAVi UI classes that we need for this
import org.havi.ui.HScreen;
import org.havi.ui.HBackgroundDevice;
import org.havi.ui.HBackgroundConfiguration;
import org.havi.ui.HBackgroundImage;
import org.havi.ui.HStillImageBackgroundConfiguration;
import org.havi.ui.HConfigurationException;
import org.havi.ui.HPermissionDeniedException;
import org.havi.ui.HBackgroundConfigTemplate;


// Since some of the graphics configuration requires scarce resources, we
// need to use the DAVIC resource notification API
import org.davic.resources.*;

/**
 * This class handles the display of background images in an easy-to
 * use way.  Setting this up can be quite complex, and so in the case
 * we've relegated it to a separate class to make it a little easier to
 * understand.
 */
class HaviBackgroundController implements org.davic.resources.ResourceClient{

	// The background device that we will use for displaying the image
	private HBackgroundDevice backdevice;

	// The configuration for that background device
	private HStillImageBackgroundConfiguration backconfig;


	/**
	 * This method will initialise the video and graphics devices to allow
	 * them to display the background image.
	 */
	public boolean init()
	{
		// We first need to get the background device that we will use for
		// displaying the image.  To do this, we need to first decide which
		// HScreen we will use.  In this case (and most others), we will use
		// the default one.
		HScreen screen = HScreen.getDefaultHScreen();
		// Once we have that, we get the default background device for that HScreen
		HBackgroundDevice backdevice = screen.getDefaultHBackgroundDevice();

		// Once we have a reference to the device, we can get a configuration for it.
		// Get a configuration that we can use
		HBackgroundConfigTemplate backgroundConfigurationTemplate = new
		HBackgroundConfigTemplate();
		
		backgroundConfigurationTemplate.setPreference(
			HBackgroundConfigTemplate.FLICKER_FILTERING,
			HBackgroundConfigTemplate.REQUIRED);

		HBackgroundConfiguration backconfig;
		backconfig = backdevice.getBestConfiguration(
			backgroundConfigurationTemplate);

                System.out.println("HaviBackgroundController: Trying to reserve backdevice...");

		// Can we reserve the device so that we can change the settings on it?
		if (backdevice.reserveDevice(this))
		{
                      System.out.println("HaviBackgroundController: reserved backdevice");
			// If we can, we set the configuration of the background device to
			// the one we got above - this is the default configuration for
			// this device.
			try
			{
				backdevice.setBackgroundConfiguration(backconfig);
			}
			catch (Exception ex)
			{
				System.out.println("HaviBackgroundController: Can't initialise the background device");
				// Release the device so that other applications can use it, if necessary.
				backdevice.releaseDevice();
				return false;
			}

                        System.out.println("HaviBackgroundController: Is backconfig a still image one? "+(backconfig instanceof HStillImageBackgroundConfiguration)+backconfig);
			// We need to check if we can put an image in the background in
			// this configuration, since we can't do this in every configuration.
			if(backconfig instanceof HStillImageBackgroundConfiguration)
			{
				// We can use this
				this.backconfig = (HStillImageBackgroundConfiguration)backconfig;
				this.backdevice = backdevice;
				return true;
			}
			else
			{
				// If we can't, we again release the device since it's
				// no use to us.
				backdevice.releaseDevice();
			}
		}
		return false;
	}

	/**
	 * Free the resources we needed to display background images.
	 * Some implementations leave the image there, but there is an explicit
	 * warning in the MHP specification that this may not happen on all
	 * implementations.  If you want to be sure that your image is still
	 * visible, don't do this.
	*/
	public void dispose()
	{
		// Check if we have something to dispose of
		if (backdevice != null)
		{
			// RZlease the device and clear any references
			backdevice.releaseDevice();
			backdevice = null;
			backconfig = null;
		}
	}


	/**
	 * Display a background image
	 */
	public void display(String filename)
	{
		// Check we have the resources we need to display a background image
                System.out.println("HaviBackgroundController: Displaying a the image on on "+backconfig);
		if(backconfig != null) {
			// Create a new background image. The image is loaded from the
			// filename that we pass in.
			HBackgroundImage backimage = new HBackgroundImage(filename);

			// Now display the image.  This can throw several exceptions, so we
			// enclose it in a 'try' block
			try {
				backconfig.displayImage(backimage);
			}
			catch (java.io.IOException ioe) {
				// Ignore it, but print a message to tell the user what's happened.
				System.out.println("HaviBackgroundController: Can't display background image - IO exception");
				ioe.printStackTrace();
			}
			catch (HPermissionDeniedException hpde) {
				// We don't have permission to displayan image.  We just ignore it.
				System.out.println("HaviBackgroundController: Can't display background image - permission denied");
			}
			catch (HConfigurationException hce) {
				// We don't have permission to displayan image.  We just ignore it.
				System.out.println("HaviBackgroundController: Can't display background image - configuration exception");
				hce.printStackTrace();
			}
		}
	}

	/**************************************************************************
	 *
	 * These methods are inherited from the ResourceClient interface and are used
	 * to tell the application when it has lost access to its resources (or
	 * when it is about to lose access to them).  This gives the application a
	 * chance to clean up when it loses access to a resource, and gives it a
	 * chance to handle things gracefully.
	 */

	/**
	 * This method gets called when the resource manager requests that we give
	 * up a resource.  We can refuse to do so, and that's what we do in this
	 * case (even though we shouldn't).
	 */
	public boolean requestRelease(ResourceProxy proxy, Object requestData) {
		return false;
	}

	/**
	 * This method gets called when the resource manager informs us that we must
	 * release a resource
	 */
	public void release(ResourceProxy proxy) {
		// release control of the background device
		backdevice.releaseDevice();
	}

	/**
	 * This method gets called when the resource manager tells us we've
	 * lost access to a resource and we should clean up after ourselves.
	 */
	public void notifyRelease(ResourceProxy proxy) {
		// release control of the background device.  Even though we don't
		// have control of it, this makes sure everything is synchronized.
		backdevice.releaseDevice();
	}


}

