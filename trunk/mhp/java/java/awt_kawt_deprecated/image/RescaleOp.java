
package java.awt.image;

//This is not a complete implementation of this class.
//It rather implements only the parts needed by org.havi.ui.HAbstractFlatMatte
//using org.dvb.ui.DVBBufferedImage instead of java.awt.image.BufferedImage

//TODO: make it native

public class RescaleOp
//implements BufferedImageOp, RasterOp
{
float[] scaleFactors;
float[] offsets;

public RescaleOp(float[] _scaleFactors, float[] _offsets, /*RenderingHints*/Object hints) {
   scaleFactors=_scaleFactors;
   offsets=_offsets;
}

/* Rescales the source BufferedImage. */
//does currently not use per-band (a,r,g,b) scaling, but per-pixel scaling
public BufferedImage filter(BufferedImage src, BufferedImage dst) {
   if (scaleFactors.length == 4) { //see above - no full implementation
      int/*float*/ oldX=0, oldY=0;
      float walt=src.getWidth();
      float halt=src.getHeight();
      float wneu=dst.getWidth();
      float hneu=dst.getHeight();
      int[] oldBitmap=new int[(int)(walt*halt)];
      src.getRGB(0,0, (int)walt, (int)halt, oldBitmap, 0, (int)walt);
      for (float x=0;x<wneu;x++) {
         for (float y=0;y<hneu;y++) {
            //mathematically: Index_neu(x,y) = Index_alt(x0+ratioW*x, y0+ratioH*y)
            //oldX=ratioW*x;
            //oldY=ratioH*y;
            oldX=(int)(((walt*10000/wneu)*x/10000));
            oldY=(int)(((halt*10000/hneu)*y/10000));
            dst.setRGB((int)x,(int)y,oldBitmap[(int)(offsets[0]+oldX +(oldY)*walt)] );
         }
      }
   }
   return dst;
}

}