package org.havi.ui;

import java.awt.Image;
import org.openmhp.util.Out;

/**
* @author tejopa
* @date 6.3.2004
* @date 6.4.2004 fully implemented
* @status fully implemented
* @module internal
* @tested no
* TODO check defaults and constructors
* TODO check how look is passed
* TODO check added method getCurrentImage()
* HOME
*/
public class HStaticAnimation extends HVisible implements HNoInputPreferred, HAnimateEffect, Runnable {

	private int delay = 1;
	private int playmode = PLAY_REPEATING;
	private int position = 0;
	private int repeatCount = 0;
	private boolean running = false;
	private static HAnimateLook defaultLook = new HAnimateLook();
	private Image[] images;
	private int repeatsLeft = 0;
	private int direction = 1;
	private Thread thread;
	private HLook currentLook = null;

	public HStaticAnimation(){
	}

    public HStaticAnimation(Image[] imagesNormal, int delay, int playMode, int repeatCount, int x, int y, int width, int height){
		images = imagesNormal;
		this.delay = delay;
		playmode = playMode;
		this.repeatCount = repeatCount;
		setLocation(x,y);
		setSize(width,height);
    }

    public HStaticAnimation(Image[] imagesNormal, int delay, int playMode, int repeatCount){
    	this(imagesNormal,delay,playMode,repeatCount,0,0,0,0);
    }

    public static HAnimateLook getDefaultLook(){
		Out.printMe(Out.TRACE);
		return defaultLook;
    }

    public int getDelay(){
        return delay;
    }

    public int getPlayMode(){
        return playmode;
    }

    public int getPosition(){
        return position;
    }

	/** added public method */
	public Image getCurrentImage() {
		return images[getPosition()];
	}

    public int getRepeatCount(){
        return repeatCount;
    }

    public boolean isAnimated(){
        return running;
    }

    public static void setDefaultLook(HAnimateLook hlook){
		defaultLook = hlook;
    }

    public void setDelay(int count){
        delay = count;
    }

    public void setLook(HLook hlook) throws HInvalidLookException{
		Out.printMe(Out.TODO,"Check look's type");
		currentLook = hlook;
	}

    public void setPlayMode(int mode){
		if (mode==PLAY_ALTERNATING||mode==PLAY_REPEATING) {
	        playmode = mode;
		}
    }

    public void setPosition(int pos){
    	if (images!=null&&images.length>0) {
	    	if (pos<0) {
	    		position = 0;
				return;
			}
			if (pos>images.length) {
				position = images.length-1;
				return;
			}
			position = pos;
		}
    }

    public void setRepeatCount(int count){
        repeatCount = count;
        repeatsLeft = count;
    }

    public void start(){
		if (!running) {
			thread = new Thread(this);
			thread.start();
		}
		else {
			position = 0;
			repeatsLeft = repeatCount;
		}
    }

    public void stop(){
		running = false;
    }

	public void run() {
		while (running) {
			try {
				Thread.sleep(100*delay);
			}
			catch (Exception e) { }

			if (playmode==PLAY_REPEATING) {
				position++;
				if (position>images.length) {
					repeatsLeft--;
					position = 0;
				}
				if (repeatsLeft<=0) {
					running = false;
				}
			}

			if (playmode==PLAY_ALTERNATING) {
				position+=direction;
				if (position<0) {
					position = 1;
					direction =1;
					repeatsLeft--;
				}
				if (position>images.length) {
					position = images.length-2;
					direction = -1;
					repeatsLeft--;
				}
				if (repeatsLeft<=0) {
					running = false;
				}
			}
		}
	}



}








