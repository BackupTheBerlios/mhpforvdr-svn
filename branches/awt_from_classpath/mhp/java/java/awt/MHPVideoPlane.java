package java.awt;

import java.awt.MHPScreen;
import org.dvb.application.MHPApplication;


public class MHPVideoPlane extends MHPPlane {

MHPVideoPlane(int x, int y, int width, int height) {
   super(x, y, width, height, null, false, getVideoStacking(), MHPScreen.hasVideoLayer() ? MHPScreen.getVideoLayer() : MHPScreen.getMainLayer());
}


}
