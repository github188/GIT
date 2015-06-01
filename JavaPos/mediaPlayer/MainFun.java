package mediaPlayer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Vector;

import javax.media.ControllerErrorEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataLostErrorEvent;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.PrefetchCompleteEvent;
import javax.media.RealizeCompleteEvent;
import javax.media.Time;
import javax.media.bean.playerbean.MediaPlayer;
import javax.swing.JWindow;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.widgets.Display;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.Convert;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.swtdesigner.SWTResourceManager;


public class MainFun extends JWindow implements ControllerListener
{
    /**
         *
         */
    private static final long serialVersionUID = 1L;
    MediaPlayer mplayer = null;
    Component visual = null;
    Component control = null;
    int videoWidth = 0;
    int videoHeight = 0;
    int controlHeight = 30;
    int insetWidth = 10;
    int insetHeight = 30;
    boolean firstTime = true;
    String path = "";
    Vector videoEffects = null;
    Vector audioEffects = null;
    JWindow frame = null;
    MessDisp box = null;
    ImageMessDisp lockbox = null;
    MediaLocator ab = null;
    boolean invalid = false;
    Vector v = new Vector();
    int index = 0;
    Process p = null;
    public boolean start = false;

    public MainFun(String title)
    {
        this.path = title;
    }

    public static void main(String[] args)
    {
        MainFun main = new MainFun(args[0]);
        main.init();
    }

    public void Fatal(String s)
    {
        System.out.println(s);
        box.visible(true);

        lockbox.visible(false);
        
        Color color = SWTResourceManager.getColor(0, 0, 0);
        box.setLabel1(s, 20, color);
        box.setLabel2("", 20, color);
    }

    public boolean openFile(String filename)
    {
        return setPlayerFile();
    }

    public boolean getFileList(String name)
    {
        File file = new File(name);
        System.out.println(name + " " + file.isDirectory());

        if (file.isDirectory())
        {
            String[] files = file.list();

            for (int i = 0; i < files.length; i++)
            {
                System.out.println(name + "//" + files[i]);

                File f = new File(name + "//" + files[i]);

                if (f.isFile() && !f.isHidden())
                {
                    System.out.println("test 0");
                    v.add(String.valueOf(name + "//" + files[i]));
                }
            }

            System.out.println("V.size" + v.size());

            if (v.size() > 0)
            {
                return true;
            }
        }

        return false;
    }

    public void init()
    {
    	if (ConfigClass.DisplayMode.toLowerCase().indexOf("videoplayer") >= 0)
    	{
    		long waittime = 0;
    		String[] row = ConfigClass.DisplayMode.split(",");
            if (row.length > 1) waittime = Convert.toLong(row[1]);

            try
            {
            	CommonMethod.waitForExec("videoplayer.exe",false);
                if (waittime != 0) Thread.sleep(waittime);
                else Thread.sleep(3000);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            // 失去当前焦点，抢回焦点
            if ((GlobalInfo.initHandle != OS.GetForegroundWindow()) && (GlobalInfo.initHandle != 0))
            {
                OS.SetForegroundWindow(GlobalInfo.initHandle);
            }
    		return;
    	}
    	
        int x = 0;
        int y = 0;
        int width = 0;
        int height = 0;

        if (Display.getDefault().getMonitors().length > 1)
        {
            Rectangle area = null;

            for (int i = 0; i < Display.getDefault().getMonitors().length; i++)
            {
                if (!Display.getDefault().getMonitors()[i].equals(Display.getDefault().getPrimaryMonitor()))
                {
                    area = Display.getDefault().getMonitors()[i].getBounds();

                    break;
                }
            }

            System.out.println("test 1");

            if (area == null)
            {
                System.out.println("test 2");

                return;
            }

            if (area.width < 1000)
            {
                GlobalVar.secFont = -4;
            }

            x      = area.x;
            y      = area.y;
            width  = area.width;
            height = area.height;
        }
        else
        {
            x      = 0;
            y      = 400;
            width  = 800;
            height = 164;
        }

        try
        {
            System.err.println(ConfigClass.DisplayMode + "|||" + GlobalInfo.sysPara.secMonitorPlayer);
        }
        catch (Exception er)
        {
            System.out.println("NULL-Test");
            er.printStackTrace();
        }

        if ((GlobalInfo.sysPara == null) || ((GlobalInfo.sysPara != null) && (GlobalInfo.sysPara.secMonitorPlayer == 'Y')))
        {
            if (System.getProperties().getProperty("os.name").substring(0, 5).equalsIgnoreCase("Linux"))
            {
            }
            else
            {
                if (ConfigClass.DisplayMode.toLowerCase().indexOf("fobs") >= 0)
                {
                    if (getFileList(path))
                    {
                        System.out.println("init fobs");

                        frame = this;

                        getContentPane().setLayout(new BorderLayout());

                        setLocation(50, 50);

                        mplayer = new MediaPlayer();

                        this.setBounds(x, y, width, height);

                        if (!setPlayerFile())
                        {
                            System.out.println("test 4");

                            return;
                        }

                        mplayer.addControllerListener((ControllerListener) this);
                        mplayer.realize();

                        start = true;

                        setVisible(true);
                    }
                }
                else if (ConfigClass.DisplayMode.toLowerCase().indexOf("mplayer") >= 0)
                {
                    if (Display.getDefault().getMonitors().length > 1)
                    {
                        long waittime = 0;

                        if (ConfigClass.DisplayMode.split(",").length > 1)
                        {
                            String[] row = ConfigClass.DisplayMode.split(",");
                            waittime = Convert.toLong(row[1]);
                        }

                        if (getFileList(path))
                        {
                            try
                            {
                                PrintWriter pw = CommonMethod.writeFile("list.pls");
                                PrintWriter pw1 = CommonMethod.writeFile("list1.pls");
                                pw.println("[playlist]");
                                pw1.println("[playlist]");

                                // 写入playlist
                                for (int i = 0; i < v.size(); i++)
                                {
                                    pw.println("File" + (i + 1) + "=" + v.elementAt(i));
                                    pw1.println("File" + (i + 1) + "=" + v.elementAt(i));
                                }

                                pw.println("NumberOfEntries=" + v.size());
                                pw1.println("NumberOfEntries=" + v.size());
                                pw.flush();
                                pw.close();
                                pw1.flush();
                                pw1.close();
                                
                                System.err.println(width+" "+height);
                                
                                Runtime.getRuntime()
                                       .exec("miniplayer -adapter 2 -x " + (width) + " -y " + (height-20) +
                                             " -geometry 0%:0% -loop 0 -playlist list.pls");

                                try
                                {
                                    if (waittime != 0)
                                    {
                                        Thread.sleep(waittime);
                                    }
                                    else
                                    {
                                        Thread.sleep(3000);
                                    }
                                }
                                catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                }

                                // 失去当前焦点，抢回焦点
                                if ((GlobalInfo.initHandle != OS.GetForegroundWindow()) && (GlobalInfo.initHandle != 0))
                                {
                                    OS.SetForegroundWindow(GlobalInfo.initHandle);
                                }
                            }
                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    else
                    {
                        try
                        {
                            Runtime.getRuntime()
                                   .exec("\"C:\\Program Files\\Ringz Studio\\Storm Codec\\mplayerc.exe \" /add \"" + this.path +
                                         "\" /play  /monitor 1");
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                else if (ConfigClass.DisplayMode.toLowerCase().indexOf("quicktime") >= 0)
                {
                }
                else
                {
                    //return ;
                }
            }
        }

        box = new MessDisp(x, height - 164, width, 164);
        box.open();
        
        lockbox = new ImageMessDisp(x,y,width,height);
        lockbox.open();

        // 失去当前焦点，抢回焦点
        if ((GlobalInfo.initHandle != OS.GetForegroundWindow()) && (GlobalInfo.initHandle != 0))
        {
            OS.SetForegroundWindow(GlobalInfo.initHandle);
        }
        
        //开启socket等待指令
        new socketSer(ConfigClass.Port).start();
    }

    public void controllerUpdate(ControllerEvent ce)
    {
        if (ce instanceof ControllerErrorEvent)
        {
            Fatal("Controller Error");
            System.out.println("Controller Error " + ((ControllerErrorEvent) ce).getSource().toString());
        }
        else if (ce instanceof DataLostErrorEvent)
        {
            Fatal("Data Lost");
            System.out.println("Data Lost " + ((DataLostErrorEvent) ce).getSource().toString());
        }
        else if (ce instanceof RealizeCompleteEvent)
        {
            System.out.println("Realize");

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

            mplayer.start();
        }
        else if (ce instanceof PrefetchCompleteEvent)
        {
            System.out.println("Prefetch");
            validate();
            mplayer.start();
        }
        else if (ce instanceof EndOfMediaEvent)
        {
            mplayer.setMediaTime(new Time(0));

            if (v.size() == 1)
            {
                if (!mplayer.getPlaybackLoop())
                {
                    mplayer.setPlaybackLoop(true);
                }
            }
            else
            {
                setPlayerFile();
                mplayer.realize();
            }
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

            //进行垃圾回收
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
                    //increase Perfermance to .avi and .mpg file
                    Manager.setHint(Manager.PLUGIN_PLAYER, new Boolean(false));
                    player = Manager.createPlayer(url);

                    mplayer.close();

                    mplayer.setPlayer(player);
                    invalid = true;
                    index++;

                    return true;
                }
                catch (NoPlayerException e)
                {
                    Fatal(e.getMessage());
                }
            }
            catch (MalformedURLException e)
            {
                Fatal(e.getMessage());
            }
            catch (IOException e)
            {
                Fatal(e.getMessage());
            }

            index++;
        }
    }

    class socketSer extends Thread
    {
        int port;

        //constructor open a port
        public socketSer(int port)
        {
            this.port = port;
        }

        public void run()
        {
            try
            {
                ServerSocket server = new ServerSocket(port);
                Socket socket;
                BufferedReader br;

                while (true)
                {
                    socket = server.accept();

                    br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    //ps = new PrintStream(socket.getOutputStream());

                    //get co from client
                    String line;

                    //format: [1]#@#[2]#@#[3]#@#[4]
                    //[1]:Label index
                    //[2]:text
                    //[3]:font
                    //[4]:color ex. 255_255_255
                    while (!socket.isInputShutdown() && ((line = br.readLine()) != null))
                    {
                        /**
                        if (GlobalInfo.sysPara.secMonitorPlayer == 'Y' && !start)
                        {
                                setPlayerFile();
                                mplayer.realize();
                        
                                start = true;
                        }
                        else if (GlobalInfo.sysPara.secMonitorPlayer != 'Y' && start)
                        {
                                mplayer.close();
                                start = false;
                        }*/
                        if (line.equals("close"))
                        {
                            box.visible(false);
                        }
                        else if (line.equals("dispose"))
                        {
                            if (frame != null)
                            {
                                box.dispose();
                                lockbox.dispose();
                                frame.dispose();

                                if (p != null)
                                {
                                    p.destroy();
                                }
                            }
                        }
                        else 
                        {
	                        String[] lines = line.split("#@#");
	                        
	                        if ((lines.length < 1) || (lines.length > 4))
	                        {
	                            continue;
	                        }
	                        
	                        if (lines[0].equals("lock"))
	                        {
	                        	if (lines.length > 1)
	                            {
	                            	lockbox.visible(true,lines[1]);
	                            }
	                        	else
	                        	{
	                        		lockbox.visible(true);
	                        	}
	                        }
	                        else if (lines[0].equals("unlock"))
	                        {   
	                        	lockbox.visible(false);
	                        }
	                        else
	                        {
		                        if (lines[0].equals("1"))
		                        {
		                            int font = 20;
		
		                            if (lines.length > 2)
		                            {
		                                font = Integer.parseInt(lines[2]);
		                            }
		
		                            int r = 0;
		                            int g = 0;
		                            int b = 0;
		
		                            if (lines.length > 3)
		                            {
		                                String[] rgb = lines[3].split("_");
		
		                                if (rgb.length == 3)
		                                {
		                                    r = Integer.parseInt(rgb[0]);
		                                    g = Integer.parseInt(rgb[1]);
		                                    b = Integer.parseInt(rgb[2]);
		                                }
		                            }
		
		                            Color color = SWTResourceManager.getColor(r, g, b);
		                            if (lines.length > 1)
		                            {
		                            	box.setLabel1(lines[1], font, color);
		                            }
		                            else
		                            {
		                            	box.setLabel1("", font, color);
		                            }
		                        }
		                        else if (lines[0].equals("2"))
		                        {
		                            int font = 20;
		
		                            if (lines.length > 2)
		                            {
		                                font = Integer.parseInt(lines[2]);
		                            }
		
		                            int r = 0;
		                            int g = 0;
		                            int b = 0;
		
		                            if (lines.length > 3)
		                            {
		                                String[] rgb = lines[3].split("_");
		
		                                if (rgb.length == 3)
		                                {
		                                    r = Integer.parseInt(rgb[0]);
		                                    g = Integer.parseInt(rgb[1]);
		                                    b = Integer.parseInt(rgb[2]);
		                                }
		                            }
		
		                            Color color = SWTResourceManager.getColor(r, g, b);
		                            
		                            if (lines.length > 1)
		                            {
		                            	box.setLabel2(lines[1], font, color);
		                            }
		                            else
		                            {
		                            	box.setLabel2("", font, color);
		                            }
		                        }
		
		                        box.visible(true);
	                        }
	                    }
                    }
                    //print a message
                    System.out.println("new client!");
                }
            }
            catch (Exception E)
            {
            	E.printStackTrace();
            }
        }
    }
}
