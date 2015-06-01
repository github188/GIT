package com.efuture.javaPos.Test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import jp.sourceforge.qrcode.QRCodeDecoder;
import jp.sourceforge.qrcode.data.QRCodeImage;
import jp.sourceforge.qrcode.exception.DecodingFailedException;

import com.swetake.util.Qrcode;

public class TwoDimensionCode {
  
	private static String imgType = "png";
	private static String FilePath = "D:/ErWeiCode/";
	private int size = 11;
	// LOGO宽度  
    private static final int WIDTH = 55;  
    // LOGO高度  
    private static final int HEIGHT = 55;
    
    /**
     * 生成无logo的QRCode
     */
    public void encoderQRCode(String content, String pFileName)
    {
    	encoderQRCode(content, pFileName,null);
    }
    
    
	/**
	 * 生成二维码(QRCode)图片
	 * @param content 存储内容
	 * @param imgPath 图片路径
	 * @param size 二维码尺寸
	 */
	public void encoderQRCode(String content, String pFileName,String pIcoPath) {
		File file = new File(FilePath);
		if(!file.exists())
		{
			file.mkdirs();
		}
		try {
			BufferedImage bufImg = this.qRCodeCommon(content,pIcoPath);
			
			File imgFile = new File(FilePath+pFileName+"."+imgType);
			// 生成二维码QRCode图片
			ImageIO.write(bufImg, imgType, imgFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成二维码(QRCode)图片
	 * @param content 存储内容
	 * @param output 输出流
	 */
	public void encoderQRCode(String content, OutputStream output) {
		try {
			BufferedImage bufImg = this.qRCodeCommon(content,null);
			// 生成二维码QRCode图片
			ImageIO.write(bufImg, imgType, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 生成二维码(QRCode)图片的公共方法
	 * @param content 存储内容
	 * @param size 二维码尺寸
	 * @return
	 * @throws Exception 
	 */
	private BufferedImage qRCodeCommon(String content,String icoPath) throws Exception {
		BufferedImage bufImg = null;
		int imgSize = 0;
		try {
			Qrcode qrcodeHandler = new Qrcode();
			// 设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小
			qrcodeHandler.setQrcodeErrorCorrect('M');
			qrcodeHandler.setQrcodeEncodeMode('B');
			// 设置设置二维码尺寸，取值范围1-40，值越大尺寸越大，可存储的信息越大
			qrcodeHandler.setQrcodeVersion(size);
			// 获得内容的字节数组，设置编码格式
			byte[] contentBytes = content.getBytes("utf-8");
			// 图片尺寸
			imgSize = 67 + 12 * (size - 1);
			bufImg = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);
			Graphics2D gs = bufImg.createGraphics();
			// 设置背景颜色
			gs.setBackground(Color.WHITE);
			gs.clearRect(0, 0, imgSize, imgSize);

			// 设定图像颜色> BLACK
			gs.setColor(Color.BLACK);
			// 设置偏移量，不设置可能导致解析出错
			int pixoff = 2;
			// 输出内容> 二维码
			if (contentBytes.length > 0 && contentBytes.length < 800) {
				boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
				for (int i = 0; i < codeOut.length; i++) {
					for (int j = 0; j < codeOut.length; j++) {
						if (codeOut[j][i]) {
							gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
						}
					}
				}
			} else {
				throw new Exception("QRCode content bytes length = " + contentBytes.length + " not in [0, 800].");
			}
			gs.dispose();
			bufImg.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(icoPath.isEmpty())
		{
			return bufImg;
		}
	 
		// 插入图片
		insertIco(bufImg, icoPath,imgSize, true);
		return bufImg;
	}
	
	/** 
     * 插入LOGO 
     *  
     * @param source 
     *            二维码图片 
     * @param imgPath 
     *            LOGO图片地址 
     * @param needCompress 
     *            是否压缩 
	 * @return 
     * @throws Exception 
     */  
    private void insertIco(BufferedImage source, String imgPath,int qrWindth,  
            boolean needCompress) throws Exception {  
        File file = new File(imgPath);  
        if (!file.exists()) {  
            System.err.println(  imgPath+"  该文件不存在！");  
            return ;  
        }  
        Image src = ImageIO.read(new File(imgPath));  
        int width = src.getWidth(null);  
        int height = src.getHeight(null);  
        if (needCompress) { // 压缩LOGO  
            if (width > WIDTH) {  
                width = WIDTH;  
            }  
            if (height > HEIGHT) {  
                height = HEIGHT;  
            }  
            Image image = src.getScaledInstance(width, height,  
                    Image.SCALE_SMOOTH);  
            BufferedImage tag = new BufferedImage(width, height,  
                    BufferedImage.TYPE_INT_RGB);  
            Graphics g = tag.getGraphics();  
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图  
            g.dispose();  
            src = image;  
        }  
        // 插入LOGO  
        Graphics2D graph = source.createGraphics();  
        int x = (qrWindth - width) / 2;  
        int y = (qrWindth - height) / 2;  
        graph.drawImage(src, x, y, width, height, null);  
        Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);  
        graph.setStroke(new BasicStroke(3f));  
        graph.draw(shape);  
        graph.dispose();  
    }
	
	/**
	 * 解析二维码（QRCode）
	 * @param imgPath 图片路径
	 * @return
	 */
	public String decoderQRCode(String imgPath) {
		// QRCode 二维码图片的文件
		File imageFile = new File(imgPath);
		BufferedImage bufImg = null;
		String content = null;
		try {
			bufImg = ImageIO.read(imageFile);
			QRCodeDecoder decoder = new QRCodeDecoder();
			content = new String(decoder.decode(new TwoDimensionCodeImage(bufImg)), "utf-8"); 
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (DecodingFailedException dfe) {
			System.out.println("Error: " + dfe.getMessage());
			dfe.printStackTrace();
		}
		return content;
	}
	
	/**
	 * 解析二维码（QRCode）
	 * @param input 输入流
	 * @return
	 */
	public String decoderQRCode(InputStream input) {
		BufferedImage bufImg = null;
		String content = null;
		try {
			bufImg = ImageIO.read(input);
			QRCodeDecoder decoder = new QRCodeDecoder();
			content = new String(decoder.decode(new TwoDimensionCodeImage(bufImg)), "utf-8"); 
		} catch (IOException e) {
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
		} catch (DecodingFailedException dfe) {
			System.out.println("Error: " + dfe.getMessage());
			dfe.printStackTrace();
		}
		return content;
	}

	public static void main(String[] args) {
		String imgName = "Michael_QRCode";
		String encoderContent = "1Hello 大大、小小,welcome to QRCode!\n" 
				+"2Hello 大大、小小,welcome to QRCode!"
				+"1234567890=098754321=1234567890="
				+"=1234567890"+"=1234567890"+"=1234567890"
				+"=1234567890"+"=1234567890"+"=1234567890"
//				+"=1234567890"+"=1234567890"+"=1234567890"
				;
		TwoDimensionCode handler = new TwoDimensionCode();
		handler.encoderQRCode(encoderContent, imgName,"D:/ic_launcher.png");
//		try {
//			OutputStream output = new FileOutputStream(imgPath);
//			handler.encoderQRCode(content, output);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		System.out.println("========encoder success");
		
		String decoderContent = handler.decoderQRCode( FilePath+"Michael_QRCode.png");
		System.out.println("解析结果如下：");
		System.out.println(decoderContent);
		System.out.println("========decoder success!!!");
	}
	
	class TwoDimensionCodeImage implements QRCodeImage {

		BufferedImage bufImg;
		
		public TwoDimensionCodeImage(BufferedImage bufImg) {
			this.bufImg = bufImg;
		}
		
		//@Override
		public int getHeight() {
			return bufImg.getHeight();
		}

		//@Override
		public int getPixel(int x, int y) {
			return bufImg.getRGB(x, y);
		}

		//@Override
		public int getWidth() {
			return bufImg.getWidth();
		}
	}
}