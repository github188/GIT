package com.efuture.javaPos.Test;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.media.CannotRealizeException;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSourceException;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.PrefetchCompleteEvent;
import javax.media.RealizeCompleteEvent;
import javax.media.Time;
import javax.media.bean.playerbean.MediaPlayer;
import javax.media.protocol.DataSource;
import javax.swing.JWindow;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.sun.media.ui.MessageBox;


public class TestMedia extends JWindow implements ControllerListener
{
    /**
         *
         */
    private static final long serialVersionUID = 1L;
    MediaPlayer mplayer;
    Component visual = null;
    Component control = null;
    int videoWidth = 0;
    int videoHeight = 0;
    int controlHeight = 30;
    int insetWidth = 10;
    int insetHeight = 30;
    boolean firstTime = true;
    DataSource[] array = null;
    String path = "";


    JWindow frame = null;
    
    MediaLocator ab = null;
    boolean invalid = false;
    Vector v = new Vector();
    int index = 0;

    public TestMedia(String title)
    {
        this.path = title;
    }

    public static void main(String[] args)
    {
    	TestMedia main = new TestMedia(args[0]);
        main.init();
    }

    static void Fatal(String s)
    {
        new MessageBox("JMF Error", s);
    }

    public boolean openFile(String filename)
    {
        return setPlayerFile();
    }

    public boolean getFileList(String name)
    {
        File file = new File(name);

        if (file.isDirectory())
        {
            String[] files = file.list();

            for (int i = 0; i < files.length; i++)
            {

            		try {
						DataSource src = Manager.createDataSource(new URL("file:"+files[i]));
						v.add(src);
            		} catch (NoDataSourceException e) {
						// TODO 自动生成 catch 块
						e.printStackTrace();
					} catch (IOException e) {
						// TODO 自动生成 catch 块
						e.printStackTrace();
					} 
                
            	
            }

            if (v.size() > 0)
            {
            	array = new DataSource[v.size()];
            	for (int i = 0; i < v.size();i++)
            	{
            		array[i] = (DataSource)v.elementAt(i);
            	}
                return true;
            }
        }

        return false;
    }

    public void init()
    {
    	/**
        if (!getFileList(path))
        {
            return;
        }
		*/
        
        frame = this;

        getContentPane().setLayout(new BorderLayout());

        setLocation(50, 50);

        mplayer = new MediaPlayer();


		try {
			DataSource src = Manager.createDataSource(new URL("file:E:\\video\\sample_video.1.mp4"));
			Player p= Manager.createRealizedPlayer(src); 

			Player p1= Manager.createRealizedPlayer(src); 
			
			mplayer.setPlayer(p1);
	        mplayer.setPlayer(p);
		} catch (NoPlayerException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		} catch (CannotRealizeException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		} catch (NoDataSourceException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		}
    	
    	
        /**
        if (!setPlayerFile())
        {
            return;
        }*/

        mplayer.addControllerListener((ControllerListener) this);
        mplayer.realize();


        setVisible(true);

        if (Display.getDefault().getMonitors().length > 1)
        {
            Rectangle area = null;

            for (int i = 0; i < Display.getDefault().getMonitors().length;
                     i++)
            {
                if (!Display.getDefault().getMonitors()[i].equals(Display.getDefault()
                                                                             .getPrimaryMonitor()))
                {
                    area = Display.getDefault().getMonitors()[i].getBounds();

                    break;
                }
            }

            if (area == null)
            {
                return;
            }

            area = Display.getDefault().getMonitors()[1].getBounds();

            int x = area.x;
            int y = area.y;
            int width = area.width;
            int height = area.height;

            this.setBounds(x, y, width, height);

           
        }
        else
        {
            
            this.setBounds(0, 0, 800, 600);
        }
    }

    public void controllerUpdate(ControllerEvent ce)
    {
        if (ce instanceof RealizeCompleteEvent)
        {
            System.out.println("Realize");
            mplayer.prefetch();
        }
        else if (ce instanceof PrefetchCompleteEvent)
        {
            System.out.println("Prefetch");

            getContentPane().removeAll();

            if ((visual = mplayer.getVisualComponent()) != null)
            {
                Dimension size = visual.getPreferredSize();
                videoWidth  = size.width;
                videoHeight = size.height;
                getContentPane().add(visual, BorderLayout.CENTER);
            }
            else
            {
                videoWidth = 320;
            }

            
            control = null;
            if ((control = mplayer.getControlPanelComponent()) != null) {
                        controlHeight = control.getPreferredSize().height;
                        getContentPane().add(control,BorderLayout.SOUTH);
            }
            validate();
            mplayer.start();
            
        }
        else if (ce instanceof EndOfMediaEvent)
        {
            mplayer.setMediaTime(new Time(0));

            if (v.size() <= 1)
            {
                mplayer.setPlaybackLoop(true);
            }
            else
            {
                setPlayerFile();
            }

            //mplayer.realize();
        }
    }

    public boolean setPlayerFile()
    {
        while (true)
        {
            if (index >= v.size())
            {
                if (!invalid)
                {
                    return false;
                }

                index = 0;
            }

            String mediaFile = (String) v.elementAt(index);
            Player player = null;
            System.gc();

            URL url = null;

            try
            {
                // Create an url from the file name and the url to the
                // document containing this applet.
                System.out.println(mediaFile);

                if ((url = new URL("file:" + mediaFile)) == null)
                {
                    Fatal("Can't build URL for " + mediaFile);

                    continue;
                }

                // Create an instance of a player for this media
                try
                {
                    player = Manager.createPlayer(url);
                    mplayer.close();
                    mplayer.setPlayer(player);
                    invalid = true;
                    index++;

                    return true;
                }
                catch (NoPlayerException e)
                {
                }
            }
            catch (MalformedURLException e)
            {
                Fatal("Error:" + e);
            }
            catch (IOException e)
            {
                Fatal("Error:" + e);
            }

            index++;
        }
    }

    
    
}
