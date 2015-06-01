package com.efuture.javaPos.Device.SerialPort;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.ParallelPort;
import gnu.io.PortInUseException;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import com.efuture.commonKit.MessageBox;


public class ParallelConnection
{
    private String portname;
    private OutputStream os;
    private CommPortIdentifier portId;
    private ParallelPort sPort;
    private boolean open;

    public ParallelConnection(String name)
    {
        portname = name;
        open     = false;
    }

    public void openConnection() throws ParallelConnectionException
    {
        // Obtain a CommPortIdentifier object for the port you want to open.
        try
        {
        	// List Port
        	boolean ok = false;
        	StringBuffer sb = new StringBuffer();
        	sb.append("必须是以下端口\n");
        	Enumeration portList = CommPortIdentifier.getPortIdentifiers();
        	while(portList != null)
        	{
        		CommPortIdentifier p = (CommPortIdentifier)portList.nextElement();
        		if (p == null) break;
        		else
        		{
        			sb.append(p.getName());
        			sb.append("\n");
        			if (p.getName().equals(portname))
        			{
        				ok = true;
        				break;
        			}
        		}
        	}
        	if (!ok)
        	{
        		throw new ParallelConnectionException(sb.toString());
        	}
        	
        	//
            portId = CommPortIdentifier.getPortIdentifier(portname);
        }
        catch (NoSuchPortException e)
        {
            throw new ParallelConnectionException(portname+":"+e.getMessage());
        }
        
        // Open the port represented by the CommPortIdentifier object. Give
        // the open call a relatively long timeout of 30 seconds to allow
        // a different application to reliquish the port if the user
        // wants to.
        try
        {
            sPort = (ParallelPort) portId.open("Parallel" + portname, 30000);
        }
        catch (PortInUseException e)
        {
            throw new ParallelConnectionException(portname+" Open Error:"+e.getMessage());
        }

        // Open the input and output streams for the connection. If they won't
        // open, close the port before throwing an exception.
        try
        {
            os = sPort.getOutputStream();
        }
        catch (IOException e)
        {
            sPort.close();
            throw new ParallelConnectionException("Error opening i/o streams");
        }

        open = true;
    }

    /**
    * Close the port and clean up associated elements.
    */
    public void closeConnection()
    {
        // If port is alread closed just return.
        if (!open)
        {
            return;
        }

        // Check to make sure sPort has reference to avoid a NPE.
        if (sPort != null)
        {
            try
            {
                // close the i/o streams.
                os.close();
            }
            catch (IOException e)
            {
                System.err.println(e);
            }

            // Close the port.
            sPort.close();
        }

        open = false;
    }

    /**
    * Reports the open status of the port.
    * @return true if port is open, false if port is closed.
    */
    public boolean isOpen()
    {
        return open;
    }

    public void sendChar(char c)
    {
        try
        {
            os.write((int) c);
            os.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void sendString(String s)
    {
        try
        {
        	// 直接os.write(s.getBytes())有一定机率导致JVM退出
        	// 特别是打印挂单时收银机号和收银员号有9时,怀疑为DLL的冲突
        	// 一个一个输出并捕获异常则能解决
        	byte[] b = s.getBytes();
        	for (int i=0;i<b.length;i++)
        	{
        		try
        		{
        			os.write(b[i]);
        		}
        		catch (IOException e)
        		{
        			
        		}
        	}
            //os.write(s.getBytes());
            os.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    
    //这个是上海寺冈WH打印机以图片形式打印文本内容的代码
    public void sendImgae(String s)
    {
        try
        {
        	//注意图片格式  BufferedImage.TYPE_BYTE_BINARY
        	BufferedImage img = new BufferedImage(624, 35, BufferedImage.TYPE_BYTE_BINARY);
        	Graphics g = img.getGraphics();
        	//不能设置图片颜色，否定将导致内容无法打印
        	//g.setColor(Color.white); //不能设置图片颜色，否定将导致内容无法打印
        	//g.fillRect(0,0,img.getWidth(), img.getHeight());
        	//g.setColor(Color.black);
        	
        	g.setFont(new Font("宋体", Font.PLAIN, 16));
        	g.drawString(s, 100, 25);
        	g.dispose();        	
			img.flush();
        	
			DataBufferByte dataBufferByte = (DataBufferByte) img.getRaster().getDataBuffer();
			byte[] data = dataBufferByte.getData();
			int width = img.getWidth() / 8;
			int height = img.getHeight();
			int data_len = data.length;
			
			int dividor = 24;
			int print_times = 0;
			int mod = height % dividor;
			if (mod == 0) {
				print_times = height / dividor;
			} else {
				print_times = height / dividor + 1;
			}							
			int last_height = height - dividor * (print_times - 1);
			
			int posi = 0;
			int len = 0;
			for (int i = 0; i < print_times; ++i) {
				try {								
					if (i == print_times -1) {
						len = data_len - i * width * dividor;
						byte[] cmd_end = new byte[8+len];
						cmd_end[0] = 0x1D;//Ascii.GS; 
						cmd_end[1] = 'v'; // 0x76
						cmd_end[2] = 0x30;
						cmd_end[3] = 0x30;
						cmd_end[4] = (byte) (width % 256);
						cmd_end[5] = (byte) (width / 256);
						cmd_end[6] = (byte) (last_height % 256);
						cmd_end[7] = (byte) (last_height / 256);
						System.arraycopy(data, posi, cmd_end, 8, len);
						os.write(cmd_end);
						os.flush();
					} else {
						len = width * dividor;
						byte[] cmd = new byte[8+len];
						cmd[0] = 0x1D;//Ascii.GS;
						cmd[1] = 'v'; // 0x76
						cmd[2] = 0x30;
						cmd[3] = 0x30;
						cmd[4] = (byte) (width % 256);
						cmd[5] = (byte) (width / 256);
						cmd[6] = (byte) (dividor % 256);
						cmd[7] = (byte) (dividor / 256);
						System.arraycopy(data, posi, cmd, 8, len);
						os.write(cmd);
						os.flush();
					}
					//os.write(data, posi, len);
					posi = posi + len;
					if (i % 10 == 0 && i != 0) {
						Thread.sleep(300);
					}
					if (i % 150 == 0 && i != 0) {
						Thread.sleep(2000);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					new MessageBox("点行图形数据转换异常：" + e.getMessage());
				}
			}
            
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            new MessageBox("打印点行图形异常:" + e.getMessage());
        }
    }
}
