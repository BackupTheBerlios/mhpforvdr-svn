package java.awt.event;

import java.util.EventListener;

/**
 * interface KeyListener - 
 *
 * Copyright (c) 1998
 *      Transvirtual Technologies, Inc.  All rights reserved.
 *
 * See the file "license.terms" for information on usage and redistribution
 * of this file.
 *
 * @author P.C.Mehlitz
 */

public interface KeyListener
 extends EventListener
{
void keyPressed ( KeyEvent evt );

void keyReleased ( KeyEvent evt );

void keyTyped ( KeyEvent evt );
}
