//parts of the code are taken from kawt, (c) Convergence Integrated Media

package vdr.mhp.awt;


import org.dvb.ui.DVBColor;
import org.dvb.ui.DVBAlphaComposite;
import org.dvb.ui.DVBBufferedImage;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Color;
import org.dvb.ui.DVBGraphics;



public class MHPNativeGraphics extends DVBGraphics {

static Font standardFont=new Font("tiresias", Font.PLAIN, 10);
static Color standardColor=new DVBColor(0, 0, 0, 0);

static {
   if (standardFont.nativeData == 0) {
      throw new NullPointerException("Graphics subsystem: Default font is invalid/null!");
   }
}

Font font=null;
DVBColor color=null;
Color bgColor=null;
public DVBAlphaComposite alphaComposite = null;
private long nativeData = 0; //pointer to an IDirectFBSurface, potentially a SubSurface
private long nativeFlipData=0; //pointer to a FlipData


int clipX=0, clipY=0, clipWidth=0, clipHeight=0; //these describe the user clip in AWT coordinates
int offsetX=0, offsetY=0; //for origin translation

protected MHPNativeGraphics() {
   //There are exactly two static functions where a Graphics object is created:
   //createClippedGraphics and createImageGraphics.
   //All initialization is done there.
}



void initializeNativeData(long nativeData, boolean addRef) {
   this.nativeData=nativeData;
   //In some cases we are operating on a surface that belongs to some other structure (Image, MHPPlane)
   //so we must call addRef because we will call removeRef in dispose().
   //In other cases we created the surface for our private use (subsurfaces), so it should be actually 
   //deleted in dispose - don't addRef.
   if (addRef)
      addRef(nativeData);
   checkNativeData();
   nativeFlipData=createFlipData(nativeData);
}

void checkNativeData() {
   //this method should be called before any access to nativeData.
   //I think the implementation should never crash, so this currently throws an exception.
   if (nativeData == 0)
      throw new NullPointerException("native graphics data is 0");
}

private native void addRef(long nativeData);
private native void removeRef(long nativeData);
private native long createFlipData(long nativeData);
private native void deleteFlipData(long nativeFlipData);
/*private native void addRefFlip(long nativeFlipData);
private native void removeRefFlip(long nativeFlipData);*/

public Graphics create () {
   return create(0-offsetX, 0-offsetY, getWidth(), getHeight());
}

public void translate ( int x, int y ) {
   offsetX+=x;
   offsetY+=y;
   //System.out.println("MHPNativeGraphics.translate");
   
   /*if (x<=0 || y<=0)
      return;
   long oldNativeData=nativeData;
   long oldNativeFlipData=nativeFlipData;
   
   nativeData=createSubSurface(oldNativeData, x, y, getWidth()-x, getHeight()-y);
   checkNativeData();
   nativeFlipData=createSubFlipData(nativeFlipData, x, y);
   
   removeRef(oldNativeData);
   deleteFlipData(oldNativeFlipData);
   
   //System.out.println("MHPNativeGraphics.translate: calling setTranslatedIntersectedClip");
   setTranslatedIntersectedClip(x, y, getWidth()-x, getHeight()-y, clipX, clipY, clipWidth, clipHeight);
   copyState(this);*/
}

public Graphics create ( int x, int y, int width, int height ) {
   //System.out.println("MHPNativeGraphics.create");
   x+=offsetX;
   y+=offsetY;
   MHPNativeGraphics g = new MHPNativeGraphics();
      
   //create independent native object that points to the same buffer
   g.nativeData=createSubSurface(nativeData, x, y, getWidth()-x, getHeight()-y);
   g.checkNativeData();
   g.nativeFlipData=createSubFlipData(nativeFlipData, x, y);
   
   //System.out.println("MHPNativeGraphics.create: calling setTranslatedIntersectedClip");
   g.setTranslatedIntersectedClip(x, y, width, height, clipX, clipY, clipWidth, clipHeight);
   g.copyState(this);

   return g;
}

void setTranslatedIntersectedClip(int x1, int y1, int w, int h, int x3, int y3, int clipW, int clipH) {
   //System.out.println("setTranslatedIntersectedClip: x1 "+x1+" y1 "+y1+" w "+w+" h "+h+" x3 "+x3+" y3 "+y3+" clipW "+clipW+" clipH"+clipH);
   //the arguments are not yet translated to x1|y1   
   if (x1+w < x3 || y3+h < y3) {
      setClip(0,0,0,0); //new region lies outside of original clipping area => clipping of square size 0
   }   
   //the results are already translated
   int xl= x1<=x3 ? x3-x1 : 0;
   int yo= y1<=y3 ? y3-y1 : 0;
   int xr= w+x1>=clipW+x3 ? clipW+x3-x1 : w;
   int yu= h+y1>=clipH+y3 ? clipH+y3-y1 : h;
   //the new translated clipping rectangle is (xl|yo)-(xr|yu)
   setClip(xl, yo, xr-xl, yu-yo);
}

void copyState(MHPNativeGraphics from) {
   offsetX=from.offsetX;
   offsetY=from.offsetY;
   
   try {
      setDVBComposite(from.getDVBComposite());
   } catch (org.dvb.ui.UnsupportedDrawingOperationException e) {
      e.printStackTrace();
   }
   setFont(from.getFont());
   //set color after setting DVBComposite, uses extraAlpha from DVBComposite
   setColor(from.getColor());
}

//returns new native Surface
private native static long createSubSurface(long nativeParentSurface, int x, int y, int width, int height);
private native static long createSubFlipData(long nativeFlipData, int x, int y);

//create Graphics object for a Component
public static Graphics createClippedGraphics(Component comp) {
   //System.out.println("MHPNativeGraphics.createClippedGraphics for"+comp+", toplevel "+comp);
   MHPPlane toplevel = MHPPlane.getMHPPlane(comp);
   
   if (toplevel == null) //in this implementation, MHPPlane is the only toplevel widget
      return null;
   
   DFBWindowPeer peer = toplevel.getPeer();
   
   if (peer == null)
      return null;
   
   MHPNativeGraphics g=new MHPNativeGraphics();   
   long nativeSurface=peer.getNativeSurface();
   
   if (nativeSurface==0)
      throw new RuntimeException();
   
   if (toplevel == comp) //comp is an MHPPlane - dont return a subsurface, but the full surface
      g.initializeNativeData(nativeSurface, true);
   else { //comp is contained in the MHPPlane - return a subsurface
      Rectangle bounds=comp.getBounds();
      g.initializeNativeData(createSubSurface(nativeSurface, u, v, bounds.width, bounds.height), false);
   }
   //in contrast to DirectFB itself and other graphics libraries, in MHP the default
   // Porter-Duff-Rule is SRC (see DVBGraphics documentation).
   try { g.setDVBComposite(DVBAlphaComposite.Src); } catch (org.dvb.ui.UnsupportedDrawingOperationException _) {}
   g.setFont((comp.getFont() == null) ? standardFont : comp.getFont());
   //set color after setting DVBComposite, uses extraAlpha from DVBComposite
   g.setColor((comp.getForeground() == null) ? standardColor : comp.getForeground());
   g.setClip(null); //necessary at least to initialize Java space variables!
   return g;
}

//create Graphics object for an Image
public static Graphics getImageGraphics(Image img) {
   //System.out.println("MHPNativeGraphics.getImageGraphics");
   long surface=img.getNativeSurface();
   
   if (surface==0)
      return null;
      
   MHPNativeGraphics g=new MHPNativeGraphics();
   g.initializeNativeData(surface, true);
   try { g.setDVBComposite(DVBAlphaComposite.Src); } catch (org.dvb.ui.UnsupportedDrawingOperationException _) {}
   g.setFont(standardFont);
   g.setColor(standardColor);
   g.setClip(null); //at least to initialize Java space variables
   return g;
}

public void dispose () {
   if (nativeData != 0) {
      removeRef(nativeData);
      nativeData = 0;
   }
   if (nativeFlipData != 0) {
      deleteFlipData(nativeFlipData);
      nativeFlipData=0;
   }
}

public void finalize() {
   dispose();
   super.finalize();
}


public void clearRect ( int x, int y, int width, int height ) {
   x+=offsetX;
   y+=offsetY;
   Color realColor=getColor();
   setColor(bgColor);
   fillRect(x, y, width, height);
   setColor(realColor);
}

public void clipRect ( int x, int y, int width, int height ) {
   x+=offsetX;
   y+=offsetY;
   // according to the specs, this only shrinks the clip region to the
   // intersection of the current region and the specified rect, i.e. it
   // cannot be used to implement multi-rectangular clipping regions
   int xNew   = ( x > clipX ) ? x : clipX;
   int yNew   = ( y > clipY ) ? y : clipY;
   int clipXw = clipX+clipWidth;
   int clipYh = clipY+clipHeight;
   int xw     = x + width;
   int yh     = y + height;
   int wNew   = ( ( xw > clipXw ) ? clipXw : xw ) - xNew;
   int hNew   = ( ( yh > clipYh ) ? clipYh : yh ) - yNew;
   if ( wNew < 0 ) {
         wNew = 0;
   }
   if ( hNew < 0 ) {
         hNew = 0;
   }
   setClip(xNew-offsetX, yNew-offsetY, wNew, hNew);
}

public void copyArea ( int x, int y, int width, int height, int dx, int dy ) {
   x+=offsetX;
   y+=offsetY;
   checkNativeData();
   copyArea(nativeData, nativeFlipData, x, y, width, height, dx, dy);
}

private native void copyArea(long nativeData, long nativeFlipData, int x, int y, int width, int height, int dx, int dy);

public void draw3DRect ( int x, int y, int width, int height, boolean raised ) {
   x+=offsetX;
   y+=offsetY;
   checkNativeData();
   draw3DRect(nativeData, nativeFlipData, x, y, width, height, raised, color.getNativeValue(), color.brighter().getNativeValue(), color.darker().getNativeValue());
}

private native void draw3DRect(long nativeData, long nativeFlipData, int x, int y, int width, int height, boolean raised, int color, int brighterColor, int darkerColor);

public void drawArc ( int x, int y, int width, int height,
            int startAngle, int arcAngle ) {
   x+=offsetX;
   y+=offsetY;
   checkNativeData();
   drawArc(nativeData, nativeFlipData, x, y, width, height, startAngle, arcAngle);
}

private native void drawArc(long nativeData, long nativeFlipData, int x, int y, int width, int height, int startAngle, int arcAngle);

public void drawBytes ( byte data[], int offset, int length, int x, int y ) {
   String s=new String(data, offset, length);
   drawString(s, x, y);
}

public void drawChars ( char data[], int offset, int length, int x, int y ) {
   String s=new String(data, offset, length);
   drawString(s, x, y);
}

public boolean drawImage (Image img, int x, int y, Color bgcolor,
                           java.awt.image.ImageObserver observer) {
        // if the image isn't loaded yet, start production and return false
            if ( Image.loadImage( img, -1, -1, observer ) == false ) {
                return ( false );
            } else {
                drawImg( img, x, y, 0, 0, img.width, img.height, bgcolor, alphaComposite.multiplyAlpha(255) );
                return true;
            }
}

public boolean drawImage ( Image img, int x, int y, java.awt.image.ImageObserver observer) {
   return drawImage(img, x, y, null, observer);
}

public boolean drawImage ( Image img, int x, int y, int width, int height, Color background,
                           java.awt.image.ImageObserver observer ) {
        // Load image if it's not loaded - we don't scale because we
        // can do this while drawing.
            if ( Image.loadImage( img, -1, -1, observer ) == false ) {
                return ( false );
            }
        // Handle proportional widths and heights
            if ( width < 0 ) {
                width = img.width;
                if ( height < 0 ) {
                    height = img.height;
                } else {
                    width = ( width * height ) / img.height;
                }
            } else if ( height < 0 ) {
                height = ( img.height * width ) / img.width;
            }
            if ( ( img.width != width ) || ( img.height != height ) ) {
                drawImgScaled( img, x, y, x + width, y + height, 0, 0,
                    img.width, img.height, background, alphaComposite.multiplyAlpha(255) );
            } else {
                drawImg( img, x, y, 0, 0, width, height, background, alphaComposite.multiplyAlpha(255) );
            }
            return true;
}

public boolean drawImage ( Image img, int x, int y, int width, int height,
                           java.awt.image.ImageObserver observer) {
   return drawImage( img, x, y, width, height, null, observer );
}

public boolean drawImage ( Image img,
      int dx0, int dy0, int dx1, int dy1,
      int sx0, int sy0, int sx1, int sy1,
      Color bgColor, java.awt.image.ImageObserver observer) {
        // Load image if it's not loaded - we don't scale because we
        // can do this while drawing.
            if ( Image.loadImage( img, -1, -1, observer ) == false ) {
                return ( false );
            }
        // If any of the source points are negative then error
            if ( sx0 < 0 || sy0 < 0 || sx1 < 0 || sy1 < 0 ) {
                return ( false );
            }
            if ( ( ( sx1 - sx0 ) == ( dx1 - dx0 ) ) &&
                ( ( sy1 - sy0 ) == ( dy1 - dy0 ) ) ) {
            // bozo. don't you know about the costs of image scaling?
                    drawImg( img, dx0, dy0, sx0, sy0, ( sx1 - sx0 ), ( sy1 - sy0 ), bgColor, alphaComposite.multiplyAlpha(255) );
            } else {
            // We don't create a scaled Image instance since we can draw scaled
                drawImgScaled( img, dx0, dy0, dx1, dy1, sx0, sy0, sx1,
                    sy1, bgColor, alphaComposite.multiplyAlpha(255) );
            }
            return true;
}

public boolean drawImage ( Image img,
      int dx1, int dy1, int dx2, int dy2,
      int sx1, int sy1, int sx2, int sy2,
      java.awt.image.ImageObserver observer) {
   return drawImage( img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null, observer );
}

//If extraAlpha is != 255, the image is blitted and the given alpha is used additionally
void drawImg( Image img, int x, int y, int sx, int sy, int width, int height, Color background, int extraAlpha) {
   if ( ( img.flags & Image.BLOCK_FRAMELOADER ) != 0 ) {
      img.activateFrameLoader();
   } else if ( ( img.flags & Image.IS_ANIMATION ) != 0 ) {
      Toolkit.imgSetFrame( img.nativeData, 0 );
   }
   x+=offsetX;
   y+=offsetY;
   checkNativeData();
   drawImage( nativeData, nativeFlipData, img.nativeData, sx, sy,
      x, y, width, height, color.getNativeValue(), ( bgColor == null ) ? -1 : bgColor.getNativeValue(), extraAlpha);
}

private native void drawImage(long nativeData, long nativeFlipData, long imgNativeData, int sx, int sy,
         int x, int y, int width, int height, int origColor, int bgColor, int extraAlpha);

void drawImgScaled( Image img, int dx0, int dy0, int dx1, int dy1, int sx0,
        int sy0, int sx1, int sy1, Color background, int extraAlpha) {
   //if ( img.nativeData != 0 ) {
      if ( ( img.flags & Image.BLOCK_FRAMELOADER ) != 0 ) {
         img.activateFrameLoader();
      }
      dx0=dx0+offsetX;
      dy0=dy0+offsetY;
      dx1=dx1+offsetX;
      dy1=dy1+offsetY;
      //System.out.println("drawimg scaled");
      checkNativeData();
      drawImageScaled( nativeData, nativeFlipData, 
         img.nativeData, dx0, dy0, dx1, dy1, sx0, sy0, sx1, sy1,
         color.getNativeValue(), ( bgColor == null ) ? -1 : bgColor.getNativeValue(), extraAlpha );
      //System.out.println("drawimg scaled redo");
   //}
}

private native void drawImageScaled(long nativeData, long nativeFlipData, long imgNativeData, int dx0, int dy0, int dx1, int dy1, int sx0,
        int sy0, int sx1, int sy1, int origColor, int bgColor, int extraAlpha);

//provides TileBlitting, TODO: provide public function for this (API does not request this)
void drawImgTiled( Image img, int x, int y, int sx, int sy, int width, int height, Color background, int extraAlpha) {
   if ( ( img.flags & Image.BLOCK_FRAMELOADER ) != 0 ) {
      img.activateFrameLoader();
   } else if ( ( img.flags & Image.IS_ANIMATION ) != 0 ) {
      Toolkit.imgSetFrame( img.nativeData, 0 );
   }
   x+=offsetX;
   y+=offsetY;
   checkNativeData();
   drawImageTiled( nativeData, nativeFlipData, img.nativeData, sx, sy,
      x, y, width, height, color.getNativeValue(), ( bgColor == null ) ? -1 : bgColor.getNativeValue(), extraAlpha);
}

private native void drawImageTiled(long nativeData, long nativeFlipData, long imgNativeData, int sx, int sy,
         int x, int y, int width, int height, int origColor, int bgColor, int extraAlpha);


//tiles the image alpha over the whole destination surface. Not official API.
public void tileBlitImageAlpha(Image img, int x, int y) {
   if ( ( img.flags & Image.BLOCK_FRAMELOADER ) != 0 ) {
      img.activateFrameLoader();
   }
   x+=offsetX;
   y+=offsetY;
   checkNativeData();
   tileBlitImageAlpha( nativeData, nativeFlipData, img.nativeData, x, y, alphaComposite.getRule());
}
        
private native void tileBlitImageAlpha(long nativeData, long nativeFlipData, long imgNativeData, int x, int y, int porterDuff);
        
public void drawLine ( int x1, int y1, int x2, int y2 ) {
   x1=x1+offsetX;
   y1=y1+offsetY;
   x2=x2+offsetX;
   y2=y2+offsetY;
   checkNativeData();
   drawLine(nativeData, nativeFlipData, x1, y1, x2, y2);
}

private native void drawLine(long nativeData, long nativeFlipData, int x1, int y1, int x2, int y2);


public void drawOval ( int x, int y, int width, int height ) {
   x+=offsetX;
   y+=offsetY;
   checkNativeData();
   drawOval(nativeData, nativeFlipData, x, y, width, height);
}

private native void drawOval(long nativeData, long nativeFlipData, int x, int y, int width, int height);


public void drawPolygon(Polygon p) {
   drawPolygon(p.xpoints, p.ypoints, p.npoints);
}

public void drawPolygon ( int xPoints[], int yPoints[], int nPoints ) {
   for (int i=0;i<nPoints;i++) {
      xPoints[i]+=offsetX;
      yPoints[i]+=offsetX;
   }
   
   checkNativeData();
   drawPolygon(nativeData, nativeFlipData, xPoints, yPoints, nPoints);
   
   for (int i=0;i<nPoints;i++) {
      xPoints[i]-=offsetX;
      yPoints[i]-=offsetX;
   }
}

private native void drawPolygon(long nativeData, long nativeFlipData, int xPoints[], int yPoints[], int nPoints);


public void drawPolyline ( int xPoints[], int yPoints[], int nPoints ) {
   for (int i=0;i<nPoints;i++) {
      xPoints[i]+=offsetX;
      yPoints[i]+=offsetX;
   }
   
   checkNativeData();
   drawPolyline(nativeData, nativeFlipData, xPoints, yPoints, nPoints);
   
   for (int i=0;i<nPoints;i++) {
      xPoints[i]-=offsetX;
      yPoints[i]-=offsetX;
   }
}

private native void drawPolyline(long nativeData, long nativeFlipData, int xPoints[], int yPoints[], int nPoints);


public void drawRect ( int x, int y, int width, int height ) {
   x+=offsetX;
   y+=offsetY;
   checkNativeData();
   drawRect(nativeData, nativeFlipData, x, y, width, height);
}

private native void drawRect(long nativeData, long nativeFlipData, int x, int y, int width, int height);


public void drawRoundRect ( int x, int y, int width, int height,
             int arcWidth, int arcHeight) {
   x+=offsetX;
   y+=offsetY;
   checkNativeData();
   drawRoundRect(nativeData, nativeFlipData, x, y, width, height, arcWidth, arcHeight);
}

private native void drawRoundRect(long nativeData, long nativeFlipData, int x, int y, int width, int height, int arcWidth, int arcHeight);


public void drawString ( String str, int x, int y ) {
   x+=offsetX;
   y+=offsetY;
   checkNativeData();
   drawString(nativeData, nativeFlipData, font.nativeData, str, x, y);
}

private native void drawString(long nativeData, long nativeFlipData, long nativeFontData, String str, int x, int y);

public void fill3DRect ( int x, int y, int width, int height, boolean raised )  {
   x+=offsetX;
   y+=offsetY;
   checkNativeData();
   fill3DRect(nativeData, nativeFlipData, x, y, width, height, raised, color.getNativeValue(), color.brighter().getNativeValue(), color.darker().getNativeValue());
}

private native void fill3DRect(long nativeData, long nativeFlipData, int x, int y, int width, int height, boolean raised, int color, int brighterColor, int darkerColor);


public void fillArc ( int x, int y, int width, int height,
            int startAngle, int arcAngle ) {
   //TODO implement in the native layer
   x+=offsetX;
   y+=offsetY;
   checkNativeData();
   fillArc(nativeData, nativeFlipData, x, y, width, height, startAngle, arcAngle);
}

private native void fillArc(long nativeData, long nativeFlipData, int x, int y, int width, int height,
            int startAngle, int arcAngle);


public void fillOval ( int x, int y, int width, int height ) {
   x+=offsetX;
   y+=offsetY;
   checkNativeData();
   fillOval(nativeData, nativeFlipData, x, y, width, height);
}

private native void fillOval(long nativeData, long nativeFlipData, int x, int y, int width, int height);


public void fillPolygon ( Polygon p ) {
   fillPolygon( p.xpoints, p.ypoints, p.npoints);
}

public void fillPolygon ( int xPoints[], int yPoints[], int nPoints ) {
   for (int i=0;i<nPoints;i++) {
      xPoints[i]+=offsetX;
      yPoints[i]+=offsetX;
   }
   
   checkNativeData();
   fillPolygon(nativeData, nativeFlipData,  xPoints, yPoints, nPoints);
   
   for (int i=0;i<nPoints;i++) {
      xPoints[i]-=offsetX;
      yPoints[i]-=offsetX;
   }
}

private native void fillPolygon(long nativeData, long nativeFlipData, int xPoints[], int yPoints[], int nPoints);


public void fillRect ( int x, int y, int width, int height ) {
   x+=offsetX;
   y+=offsetY;
   checkNativeData();
   fillRect(nativeData, nativeFlipData, x, y, width, height);
}

private native void fillRect(long nativeData, long nativeFlipData, int x, int y, int width, int height);


public void fillRoundRect ( int x, int y, int width, int height,
                            int arcWidth, int arcHeight ) {
   x+=offsetX;
   y+=offsetY;
   checkNativeData();
   fillRoundRect(nativeData, nativeFlipData, x, y, width, height, arcWidth, arcHeight);
}

private native void fillRoundRect(long nativeData, long nativeFlipData, int x, int y, int width, int height,
                            int arcWidth, int arcHeight);


public void enterBuffered() {
   checkNativeData();
   enterBuffered(nativeFlipData);
}
private native void enterBuffered(long nativeFlipData);

public void leaveBuffered() {
   checkNativeData();
   leaveBuffered(nativeFlipData);
}
private native void leaveBuffered(long nativeFlipData);

public Shape getClip () {
   return getClipBounds();
}

public Rectangle getClipBounds() {
   return new Rectangle(clipX, clipY, clipWidth, clipHeight);
}

public Rectangle getClipBounds(Rectangle r) {
   r.x=clipX;
   r.y=clipY;
   r.width=clipWidth;
   r.height=clipHeight;
   return r;
}

public Color getColor() {
   return color;
}

public Font getFont() {
   return font;
}

public FontMetrics getFontMetrics () {
   return (getFontMetrics(getFont()));
}

public FontMetrics getFontMetrics ( Font fnt ) {
   return new FontMetrics(fnt);
}


//not API, does not respect translation origin!
int getWidth() {
   return getWidth(nativeData);
}

int getHeight() {
   return getHeight(nativeData);
}
private native int getHeight(long nativeData);
private native int getWidth(long nativeData);



    /**
     * Returns the Sample Model (DVBBufferedImage.TYPE_BASE,
     * DVBBufferedImage.TYPE_ADVANCED) which is used in the on/off screen
     * buffer this graphics object draws into.
     * @return the type of the Sample Model
     * @see org.dvb.ui.DVBBufferedImage
     * @since MHP 1.0
     */
public int getType() {
    return DVBBufferedImage.TYPE_BASE; //?
}

void setBackColor ( Color c ) {
   bgColor=c;
}

public void setClip ( Shape clip ) {
   if (clip==null) {
      checkNativeData();
      clipX=0;
      clipY=0;
      clipWidth=getWidth();
      clipHeight=getHeight();
      setClip(nativeData, -1,-1,-1,-1); //caught and known in the native layer
   } else {
      Rectangle r=clip.getBounds();
      setClip(r.x, r.y, r.width, r.height);
   }
}

public void setClip ( int x, int y, int width, int height ) {
   x+=offsetX;
   y+=offsetY;
   if (x<0)
      x=0;
   if (y<0)
      y=0;
   if (width<0)
      width=0;
   if (height<0)
      height=0;
   checkNativeData();
   clipX=x;
   clipY=y;
   clipWidth=width;
   clipHeight=height;
   setClip(nativeData, x, y, width, height);
}

private native void setClip(long nativeData, int x, int y, int width, int height);

public void setColor ( Color clr ) {
   //a nuisance but necessary, all returned colors shall be DVBColors
   //although in this implementation all the work is done by Color.
   if (clr==null)
      return;
   if (clr instanceof DVBColor)
      color=(DVBColor)clr;
   else
      color=new DVBColor(clr);
   checkNativeData();
   //also take care for extraAlpha from dvbComposite
   setColor(nativeData, color.getRed(), color.getGreen(), color.getBlue(), alphaComposite.multiplyAlpha(color.getAlpha()));
}

private native void setColor(long nativeData, int r, int g, int b, int a);

public void setFont ( Font newFnt ) {
   if (newFnt != null && newFnt.nativeData != 0) {
      font=newFnt;
      checkNativeData();
      setFont(nativeData, font.nativeData);
   }
}

private native void setFont(long nativeData, long nativeDataFont);

public void setPaintMode() {
   try {
      setDVBComposite( DVBAlphaComposite.Src);
   } catch (org.dvb.ui.UnsupportedDrawingOperationException _) {
   }
}

//private native void setPaintMode(long nativeData);

public void setXORMode ( Color newXorClr ) {
//"Calling setXORMode on an instance of this class shall be equivalent 
//to calling setDVBComposite with a special and implementation dependent 
//DVBAlphaComposite object with implements the semantics specified for 
//this method in the parent class. " Calling getDVBComposite when setXORMode 
//is the last DVBComposite set shall return this implementation dependent object. 
//Conformant MHP applications shall not do anything with or to this object 
//including calling any methods on it. " This specification does not tighten, 
//refine or detail the definition of the setXORMode beyond what is specified 
//for the parent class."

//TODO: recent versions of DirectFB have the DSDRAW_XOR drawing flag
}

//private native void setXORMode(long nativeData);

/*Graphics subGraphics () {
   return this;
}*/

//DVBGraphics interface

private static DVBAlphaComposite AvailableCompositeRules[] =
    {
        DVBAlphaComposite.Clear,
        DVBAlphaComposite.Src,
        DVBAlphaComposite.SrcOver,
        DVBAlphaComposite.DstOver,
        DVBAlphaComposite.SrcIn,
        DVBAlphaComposite.DstIn,
        DVBAlphaComposite.SrcOut,
        DVBAlphaComposite.DstOut
    };

public DVBAlphaComposite[] getAvailableCompositeRules() {
        return AvailableCompositeRules;
    }
    
public DVBAlphaComposite getDVBComposite() {
   return alphaComposite;
}

public void setDVBComposite( DVBAlphaComposite comp )
        throws org.dvb.ui.UnsupportedDrawingOperationException 
{
   if (comp==null)
      return;
   //No need to check that comp has sane values.
   //This is done in DVBAlphaComposite constructor, and all rules are supported.
   checkNativeData();
   alphaComposite=comp;
   setPorterDuff(nativeData, alphaComposite.getRule());
   //re-set Color so that extra alpha is used
   setColor(getColor());
}

private native void setPorterDuff(long nativeData, int rule);



}
