/*
 * NIST/DASE API Reference Implementation
 * $File: HImageEffectMatte.java $
 * Last changed on $Date: 2001/02/07 18:47:43 UTC $
 *
 * See the file "nist.disclaimer" in the top-level directory for information
 * on usage and redistribution of this file.
 *
 */

package org.havi.ui;
import java.awt.Image;
import java.awt.Point;


/**
 * See (C) official HaVi documentation for reference
 * <p>
 * Implementation of HImageEffectMatte.
 * See HaVi for reference.
 *
 * <p>Revision information:<br>
 * $Revision: 1.1 $
 *
 */

public class HImageEffectMatte
  extends HAbstractImageMatte implements HAnimateEffect {

  /** Sequence of data to use for this matte.
      Default value: null for fully opaque unvarying matte */
  private Image[] dataSequence = null;
  
  /** Associated offset for each frame of the matte data array
      Default value: see DEFAULT_MATTE_OFFSET */
  private Point[] dataOffset = null;

  /** Static default offset for all animated frames */
  private static final Point DEFAULT_MATTE_OFFSET = new Point(0,0);
  
  /** Constructor with no parameter */
  public HImageEffectMatte() {
    super();
  }

  /** Constructor with a sequence of matte data.
      @param data array of matte values */
  public HImageEffectMatte(Image[] data) {

    this.setMatteData(data);
    
  }


  /** Set the opacity sequency for the matte.
      @param data new sequence for this matte. A null value removes data. */
  public void setMatteData(Image[] data) {

    /* Note: it is safe to change the data while the animation thread is
       running */
    
    synchronized(data) {
      /* Reset the position */
      this.setPosition(0);
      this.dataSequence = data;
      if(dataSequence!=null) {
        this.dataOffset = new Point[dataSequence.length];
        for(int i=0; i<dataOffset.length; i++) {
          this.dataOffset[i] = DEFAULT_MATTE_OFFSET;
        }
      } else {
        this.dataOffset = null;
      }
    }
  }

  /** Return the current matte data
      @return reference to the current matte data array */
  public Image[] getMatteData() {
    return this.dataSequence;
  }


  /** Set the offset for a given frame in the animation
      @param p offset for this frame
      @param index index of the frame to modify */
  public void setOffset(Point p, int index) {
    if( (dataOffset!=null) && (index>=0) && (index<dataOffset.length) ) {
      dataOffset[index]=p;
    }
  }

  /** Get the current matte frame offset for the index-th frame
      @param index index of the requested frame
      @return current offset */
  public Point getOffset(int index) {
    if( (dataOffset!=null) && (index>=0) && (index<dataOffset.length) ) {
      return dataOffset[index];
    } else {
      return DEFAULT_MATTE_OFFSET;
    }
  }

  
  /* *************** HAnimateEffect Interface ****************** */

  /** Current position in the sequence. Default is 0 (first) */
  private int position = 0;

  /** Current animation status (stopped or started).
      Default is stopped (false). The start() method must be invoked
      to start it. */
  private boolean isAnimated = false;
  
  /** Play mode. Default = PLAY_REPEATING */
  private int playMode = HAnimateEffect.PLAY_REPEATING;

  /** Play increment (+1 or -1). Default = +1 */
  private int playIncrement = 1;

  /** Repeat count. Default = REPEAT_INFINITE */
  private int repeatCount = HAnimateEffect.REPEAT_INFINITE;
  
  /** Remaining count */
  private int remainingCount = HAnimateEffect.REPEAT_INFINITE;
  
  /** Presentation delay in 100ms units between elements in the sequence.
      Default = 1  (100ms) */
  private int delay = 1;
  

  /** Start the animation */
  public void start() {
    
    this.isAnimated = true;

    Thread animateThread = new Thread() {
        
        public void run() {

          while(isAnimated) {
            
            /* This is the setMatteData of HAbstractImageMatte */
            synchronized(dataSequence) {
              if(dataSequence != null) {
                setMatteData(dataSequence[position]);
                setOffset(dataOffset[position]);
              }
            }

            /* Go to sleep */
            try {
              sleep(delay * 100);
            } catch (InterruptedException e) {
              System.err.println("Animation thread was interrupted: " + e);
            }

            synchronized(dataSequence) {
              /* Special handling for null data */
              if(dataSequence==null) {
                continue;
              }
            
              /* Update the position according to the play mode */
              switch(playMode) {
              case HAnimateEffect.PLAY_REPEATING:
                position++;
                if(position>=dataSequence.length) {
                  position = 0;
                  /* Honor repeatCount */
                  if(repeatCount != HAnimateEffect.REPEAT_INFINITE) {
                    remainingCount--;
                    if(remainingCount<=0) {
                      isAnimated = false;
                    }
                  }
                }
                break;
              case HAnimateEffect.PLAY_ALTERNATING:
                position += playIncrement;
                if( position < 0 ) {
                  position = 1;
                  playIncrement = 1;
                  /* Honor repeatCount */
                  if(repeatCount != HAnimateEffect.REPEAT_INFINITE) {
                    remainingCount--;
                    if(remainingCount<=0) {
                      isAnimated = false;
                    }
                  } // repeatCount check
                } else if( position >= dataSequence.length ) {
                  position = dataSequence.length - 2;
                  playIncrement = -1;
                  /* Honor repeatCount */
                  if(repeatCount != HAnimateEffect.REPEAT_INFINITE) {
                    remainingCount--;
                    if(remainingCount<=0) {
                      isAnimated = false;
                    }
                  } // repeatCount check
                } // end of sequence
              } // play mode
            } // synchronized(dataSequence)
            } // infinite loop on isAnimated
          } // run()
      };
    animateThread.start();
  }

  /** Stop the animation */
  public void stop() {
    this.isAnimated = false;
  }

  /** Return the current status of the animation.
      @return true if the animation is running, false otherwise. */
  public boolean isAnimated() {
    return isAnimated;
  }

  /** Set the current animation position within the array
      @param position new position. If out of bound, use the nearest
      valid position */
  public void setPosition(int position) {

    synchronized(dataSequence) {
      
      if(dataSequence==null) {
        this.position = 0;
        /* Set to fully opaque */
        super.setMatteData(null);
        return;
      }
    
      if(position<0) {
        this.position = 0;
      } else if (position >= dataSequence.length) {
        this.position = dataSequence.length - 1;
      } else {
        this.position = position;
      }
      super.setMatteData(dataSequence[position]);
      super.setOffset(dataOffset[position]);
      return;
    }
    
  }
  

  /** Return the current position in the matte array
      @return current position */
  public int getPosition() {
    return position;
  }

  /** Set the number of time the animation should run before it stops
      @param count number of times or REPEAT_INFINITE. Invalid values (<=0)
      are treated as 1. */
  public void setRepeatCount(int count) {
    if( (count<=0)  && (count != HAnimateEffect.REPEAT_INFINITE) ) {
      this.repeatCount = 1;
    } else {
      this.repeatCount = count;
    }
    this.remainingCount = this.repeatCount;
  }


  /** Return the repeat count for this matte
      @return repeat count */
  public int getRepeatCount() {
    return repeatCount;
  }


  /** Set the presentation delay (1 unit = 100ms)
      @param count new delay. If <=0, default to 1 */
  public void setDelay(int count) {
    if(count <=0) {
      delay = 1;
    } else {
      delay = count;
    }
  }


  /** Get the current presentation delay
      @return the current presentation delay in 100ms ticks */
  public int getDelay() {
    return delay;
  }

  /** Set the play mode
      @param mode new mode; silently ignored if not a valid mode */
  public void setPlayMode(int mode) {
    if( (mode != HAnimateEffect.PLAY_REPEATING) &&
        (mode != HAnimateEffect.PLAY_ALTERNATING) ) {
      return;
    } else {
      this.playMode = mode;
    }
  }

  /** Get the current play mode.
      @return current play mode */
  public int getPlayMode() {
    return playMode;
  }
  

}
