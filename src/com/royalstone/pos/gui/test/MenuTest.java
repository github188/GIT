package com.royalstone.pos.gui.test;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JPopupMenu;

/**
 * <p>Copyright: Copyright (c) 2002</p>
 * @author Turbo Chen
 * @version 1.01
 */
public class MenuTest{

}

class CJPopupMenu extends JPopupMenu
{
    public void show(Component invoker,int x, int y)
    {
        Point ps = invoker.getLocationOnScreen();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        int mw = this.getPreferredSize().width;
        int mh = this.getPreferredSize().height;
        int newX = x;
        int newY = y;
        int aX = ps.x+x+mw;
        int aY = ps.y+y+mh;
        if ( aX>d.width )
            newX = x -(aX - d.width);
        if ( aY>d.height )
            newY = y -(aY - d.height);
        super.show(invoker,newX,newY);
    }
}
