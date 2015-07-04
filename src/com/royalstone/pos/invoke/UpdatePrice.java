package com.royalstone.pos.invoke;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileLock;
import java.nio.channels.FileChannel;
import java.util.Properties;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class UpdatePrice
    extends JFrame {
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JPanel jPanel2 = new JPanel();
  JPanel jPanel3 = new JPanel();
  JPanel jPanel4 = new JPanel();
  JPanel jPanel5 = new JPanel();
  BorderLayout borderLayout2 = new BorderLayout();
  JButton jButton5 = new JButton();
  JButton jButton6 = new JButton();
  JPanel jPanel6 = new JPanel();
  JPanel jPanel7 = new JPanel();
  JPanel jPanel8 = new JPanel();
  JPanel jPanel9 = new JPanel();
  JPanel jPanel10 = new JPanel();
  JLabel jLabel1 = new JLabel();
  JProgressBar jProgressBar1 = new JProgressBar();
  JButton jButton1 = new JButton();
  JButton jButton2 = new JButton();
  JLabel jLabel5 = new JLabel();
  	private String host;
	private String port;
  public static FileOutputStream logger = null;

  public UpdatePrice(String file)
		throws FileNotFoundException, IOException {
		Properties prop = new Properties();
		prop.load(new FileInputStream(file));
		host = prop.getProperty("server");
		port = prop.getProperty("port");
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.getContentPane().setLayout(borderLayout1);
    jPanel2.setAlignmentY( (float) 0.5);
    jPanel2.setOpaque(false);
    jPanel2.setPreferredSize(new Dimension(10, 10));
    jPanel1.setBackground(UIManager.getColor("Slider.background"));
    jPanel1.setOpaque(false);
    jPanel1.setPreferredSize(new Dimension(40, 10));
    jPanel3.setBackground(UIManager.getColor("Slider.foreground"));
    jPanel3.setOpaque(false);
    jPanel3.setPreferredSize(new Dimension(100, 100));
    jPanel3.setLayout(borderLayout2);
    jPanel4.setMinimumSize(new Dimension(10, 10));
    jPanel4.setOpaque(false);
    jPanel4.setPreferredSize(new Dimension(10, 10));
    jPanel5.setBackground(Color.lightGray);
    jPanel5.setOpaque(false);
    jPanel5.setPreferredSize(new Dimension(40, 10));
    jButton5.setText("jButton5");
    jButton6.setText("jButton6");
    jPanel6.setBackground(UIManager.getColor(
        "OptionPane.questionDialog.titlePane.background"));
    jPanel6.setMinimumSize(new Dimension(10, 10));
    jPanel6.setOpaque(false);
    jPanel6.setPreferredSize(new Dimension(10, 60));
    jPanel8.setBackground(UIManager.getColor(
        "OptionPane.questionDialog.titlePane.background"));
    jPanel8.setOpaque(false);
    jPanel8.setPreferredSize(new Dimension(10, 40));
    jPanel10.setBackground(UIManager.getColor(
        "OptionPane.questionDialog.titlePane.background"));
    jPanel10.setDebugGraphicsOptions(0);
    jPanel10.setOpaque(false);
    jLabel1.setText("更新进度");
    jButton1.setEnabled(true);
    jButton1.setText("开始");
    jButton1.addMouseListener(new UpdatePrice_jButton1_mouseAdapter(this));
    jButton2.setText("停止");
    jButton2.addMouseListener(new UpdatePrice_jButton2_mouseAdapter(this));
    jProgressBar1.setPreferredSize(new Dimension(248, 20));
    jLabel5.setText("0%");
    jPanel7.setBackground(UIManager.getColor(
        "OptionPane.questionDialog.titlePane.background"));
    jPanel7.setOpaque(false);
    jPanel9.setBackground(UIManager.getColor(
        "OptionPane.questionDialog.titlePane.background"));
    jPanel9.setOpaque(false);
    this.getContentPane().setBackground(new Color(127, 199, 63));
    this.setForeground(Color.black);
    this.setLocale(java.util.Locale.getDefault());
    this.setState(Frame.NORMAL);
    this.setTitle("脱机价格数据更新程序");
    this.addWindowListener(new UpdatePrice_this_windowAdapter(this));
    this.getContentPane().add(jPanel1, BorderLayout.EAST);
    this.getContentPane().add(jPanel2, BorderLayout.NORTH);
    this.getContentPane().add(jPanel4, BorderLayout.SOUTH);
    this.getContentPane().add(jPanel5, BorderLayout.WEST);
    this.getContentPane().add(jPanel3, BorderLayout.CENTER);
    jPanel3.add(jPanel6, BorderLayout.NORTH);
    jPanel6.add(jLabel1, null);
    jPanel6.add(jProgressBar1, null);
    jPanel6.add(jLabel5, null);
    jPanel3.add(jPanel7, BorderLayout.WEST);
    jPanel3.add(jPanel8, BorderLayout.SOUTH);
    jPanel8.add(jButton1, null);
    jPanel8.add(jButton2, null);
    jPanel3.add(jPanel9, BorderLayout.EAST);
    jPanel3.add(jPanel10, BorderLayout.CENTER);



  }
 	private static void redirectOutput() {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			File file = new File("log/update_price.log");
			if (file.exists()) {
				FileChannel infile =
					new FileInputStream("log/update_price.log").getChannel();
				FileChannel outfile =
					new FileOutputStream(
						"log/update_price_" + sdf.format(new Date()) + "_R.log")
						.getChannel();
				infile.transferTo(0, infile.size(), outfile);
			}

			logger = new FileOutputStream("log/update_price.log");
			System.setOut(new PrintStream(logger));
			System.setErr(new PrintStream(logger));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.err.println("ERROR: Connot open log file, exit ...");
			System.exit(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
  void this_windowClosing(WindowEvent e) {
    System.exit(1);
  }

  public static void main(String[] args) {
      UpdatePrice itsFrame = null;
      redirectOutput();
      FileLock lock=null;
       try {
        FileOutputStream fos=new FileOutputStream("lockUpdate");
         lock=fos.getChannel().tryLock();
          if(lock==null){
             JOptionPane.showMessageDialog(
                  null,"脱机价格数据更新程序已经运行！");
                      System.exit(1);
                       }
       } catch (Exception ex) {
          ex.printStackTrace();
            JOptionPane.showMessageDialog(null,"严重错误！");
                  System.exit(1);
      }

      try {
          itsFrame = new UpdatePrice("pos.ini");
      } catch (IOException e) {
          e.printStackTrace();
          JOptionPane.showMessageDialog(null,"读取pos.ini文件发生严重错误！");
		  System.exit(1);
      }

      itsFrame.setSize(400, 150);
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      itsFrame.setLocation(
                        (int) ((screenSize.getWidth() - 400) / 2),
                        (int) ((screenSize.getHeight() - 150) / 2));

      itsFrame.show();

  }

class Update implements Runnable{
    private UpdatePrice updatePrice;
    private String host;
    private String port;
  public Update(UpdatePrice updatePrice){
    this.updatePrice=updatePrice;
    this.host=updatePrice.host;
    this.port=updatePrice.port;
  }
  public void run() {
    String REQUEST_CONTENT_TYPE = "text/html";

    URL servlet;
    HttpURLConnection conn = null;
    InputStream is=null;

    try {
      servlet = new URL("http://"+host+":"+port+"/pos41/UpdatePrice");
      conn = (HttpURLConnection) servlet.openConnection();
      conn.setDoInput(true);
      conn.setDoOutput(true);
      conn.setRequestProperty("ContentType", REQUEST_CONTENT_TYPE);
      conn.setRequestMethod("POST");
      is = conn.getInputStream();
      ObjectInputStream dis= new ObjectInputStream(is);
      int totalCount =  Integer.parseInt((String)dis.readObject());
       for(int i=0;i<totalCount;i++){

         int result=Integer.parseInt((String)dis.readObject());
         if(result==-1){
         	JOptionPane.showMessageDialog(null,"服务器更新数据失败，请检查服务器配置后再尝试！");
         	System.exit(1); 
         }
         	 
         int barValue=(int)((result+1)*((float)100/totalCount));
         if(barValue==100)
             barValue=99;
         this.updatePrice.jProgressBar1.setValue(barValue);

         this.updatePrice.jLabel5.setText(Integer.toString(barValue)+"%");

       }
      int zipFlag=Integer.parseInt((String)dis.readObject());
        if(zipFlag!=1){
           JOptionPane.showMessageDialog(null,"服务器压缩数据失败，请再尝试！");
	       System.exit(1);
        }
       this.updatePrice.jProgressBar1.setValue(100);

         this.updatePrice.jLabel5.setText("100%");
         JOptionPane.showMessageDialog(null,"服务器完成脱机数据的更新！");
         this.updatePrice.jButton2.setText("退出");
    }
    catch (Exception ex) {
      ex.printStackTrace();
      JOptionPane.showMessageDialog(null,"更新期间发生严重错误,请检查网络再尝试！");
	  System.exit(1);
    }finally{
        if (is != null)
        try {
          is.close();
        }catch (IOException ex1) {}
    }

  }
}

  void jButton1_mouseClicked(MouseEvent e) {
    this.jButton1.setEnabled(false);
   Thread update = new Thread(new Update(this));
		update.start();
  }

  void jButton2_mouseClicked(MouseEvent e) {
          System.exit(1);
  }


}

class UpdatePrice_this_windowAdapter
    extends java.awt.event.WindowAdapter {
  UpdatePrice adaptee;

  UpdatePrice_this_windowAdapter(UpdatePrice adaptee) {
    this.adaptee = adaptee;
  }

  public void windowClosing(WindowEvent e) {
    adaptee.this_windowClosing(e);
  }
}

class UpdatePrice_jButton1_mouseAdapter extends java.awt.event.MouseAdapter {
  UpdatePrice adaptee;

  UpdatePrice_jButton1_mouseAdapter(UpdatePrice adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.jButton1_mouseClicked(e);
  }
}

class UpdatePrice_jButton2_mouseAdapter extends java.awt.event.MouseAdapter {
  UpdatePrice adaptee;

  UpdatePrice_jButton2_mouseAdapter(UpdatePrice adaptee) {
    this.adaptee = adaptee;
  }
  public void mouseClicked(MouseEvent e) {
    adaptee.jButton2_mouseClicked(e);
  }
}
