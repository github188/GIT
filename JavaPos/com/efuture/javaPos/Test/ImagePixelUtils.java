package com.efuture.javaPos.Test;

//public static byte[] imagePixelToPosByte_8(String filePath, int m)throws Exception;
//public static byte[] imagePixelToPosByte_8(File bmpFile, int m)throws Exception;
//public static byte[] imagePixelToPosByte_8(BufferedImage bmp, int m)throws Exception;
//public static byte[] imagePixelToPosByte_24(String filePath, int m)throws Exception;
//public static byte[] imagePixelToPosByte_24(File bmpFile, int m)throws Exception;
//public static byte[] imagePixelToPosByte_24(BufferedImage bmp, int m)throws Exception;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.efuture.javaPos.Device.SerialPort.SerialConnection;

public class ImagePixelUtils
{
	
	private ImagePixelUtils(SerialConnection port)
	{
	
	}

	public static String imagePixelToPosString_8(String filePath, int m) throws Exception
	{
		File file = new File(filePath);
		return imagePixelToPosString_8(file, m);
	}

	public static String imagePixelToPosString_8(File bmpFile, int m) throws Exception
	{
		BufferedImage read = ImageIO.read(bmpFile);
		return imagePixelToPosString_8(read, m);
	}

	public static String imagePixelToPosString_8(BufferedImage bmp, int m) throws Exception
	{
		if (!(m == 0 || m == 1))
		{
			m = 0;
		}
		return (String) imagePixelToPos(bmp, m, 8, 1023, "");
	}

	public static byte[] imagePixelToPosByte_8(String filePath, int m) throws Exception
	{
		File file = new File(filePath);
		return imagePixelToPosByte_8(file, m);
	}

	public static byte[] imagePixelToPosByte_8(File bmpFile, int m) throws Exception
	{
		BufferedImage read = ImageIO.read(bmpFile);
		return imagePixelToPosByte_8(read, m);
	}

	public static byte[] imagePixelToPosByte_8(BufferedImage bmp, int m) throws Exception
	{
		if (!(m == 0 || m == 1))
		{
			m = 0;
		}
		return (byte[]) imagePixelToPos(bmp, m, 8, 1023, new byte[0]);
	}

	public static byte[] imagePixelToPosByte_24(String filePath, int m) throws Exception
	{
		File file = new File(filePath);
		return imagePixelToPosByte_24(file, m);
	}

	public static byte[] imagePixelToPosByte_24(File bmpFile, int m) throws Exception
	{
		BufferedImage read = ImageIO.read(bmpFile);
		return imagePixelToPosByte_24(read, m);
	}

	public static byte[] imagePixelToPosByte_24(BufferedImage bmp, int m) throws Exception
	{
		if (!(m == 32 || m == 33))
		{
			m = 32;
		}
		return (byte[]) imagePixelToPos(bmp, m, 24, 1023, new byte[0]);
	}

	public static String imagePixelToPosString_24(String filePath, int m) throws Exception
	{
		File file = new File(filePath);
		return imagePixelToPosString_24(file, m);
	}

	public static String imagePixelToPosString_24(File bmpFile, int m) throws Exception
	{
		BufferedImage read = ImageIO.read(bmpFile);
		return imagePixelToPosString_24(read, m);
	}

	public static String imagePixelToPosString_24(BufferedImage bmp, int m) throws Exception
	{
		if (!(m == 32 || m == 33))
		{
			m = 32;
		}
		return (String) imagePixelToPos(bmp, m, 24, 1023, "");
	}

	private static Raster getRaster(BufferedImage sourceImg)
	{
		Raster raster = sourceImg.getRaster();

		try
		{
			int pixelSize = sourceImg.getColorModel().getPixelSize();
			if (pixelSize > 2)
			{
				System.out.println("==不是单色图像==该图像的像素为：" + pixelSize);
				sourceImg = createSingleBMP(sourceImg);

				if ((pixelSize = sourceImg.getColorModel().getPixelSize()) > 2)
				{
					System.out.println("==不是单色图像==该图像的像素为：" + pixelSize);
					return null;
				}
				raster = sourceImg.getRaster();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return raster;
	}

	private static BufferedImage createSingleBMP(BufferedImage sourceImg) throws Exception
	{
		int h = sourceImg.getHeight();
		int w = sourceImg.getWidth();
		int[] pixels = new int[w * h]; // 定义一数组，用来存储图片的象素
		int gray;

		PixelGrabber pg = new PixelGrabber(sourceImg, 0, 0, w, h, pixels, 0, w);
		try
		{
			pg.grabPixels(); // 读取像素值
		}
		catch (InterruptedException e)
		{
			System.err.println("处理被异常中断！请重试！");
		}

		for (int j = 0; j < h; j++) // 扫描列
		{
			for (int i = 0; i < w; i++) // 扫描行
			{ // 由红，绿，蓝值得到灰度值
				gray = (int) (((pixels[w * j + i] >> 16) & 0xff) * 0.8);
				gray += (int) (((pixels[w * j + i] >> 8) & 0xff) * 0.1);
				gray += (int) (((pixels[w * j + i]) & 0xff) * 0.1);
				pixels[w * j + i] = (255 << 24) | (gray << 16) | (gray << 8) | gray;
			}
		}
		MemoryImageSource s = new MemoryImageSource(w, h, pixels, 0, w);
		Image img = Toolkit.getDefaultToolkit().createImage(s);
		BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);
		// buf.createGraphics().drawImage(img, 0, 0, null);
		return buf;
		// ImageIO.write(buf, "BMP", new File("c:\\new.bmp"));
	}

	private static void resultStratergy(Object o, int[] pixels) throws Exception
	{
		if (o instanceof OutputStream)
		{
			OutputStream os = (OutputStream) o;
			for (int i = 0; i < pixels.length; i++)
			{
				os.write(pixels[i]);
			}
		}
		else if (o instanceof StringBuffer)
		{
			StringBuffer sb = (StringBuffer) o;
			for (int i = 0; i < pixels.length; i++)
			{
				sb.append(", (byte)0x").append(Integer.toHexString(pixels[i]));
			}
		}
	}

	private static int[] getBytes(int[] pixels)
	{
		int bitc = Byte.SIZE;
		int len = pixels.length;
		int times = (len + bitc - 1) / bitc;
		int[] result = new int[times];
		int count = 0;
		for (int i = 0; i < times; i++)
		{
			int px = 0;
			for (int j = 0; j < 8; j++)
			{
				if (count >= len)
				{
					px |= 0x01 << (bitc - 1 - j);
				}
				else
				{
					px |= pixels[count++] << (bitc - 1 - j);
				}
			}
			px = (~px | (1 << 31)) & 0xFF;
			result[i] = px;
		}
		return result;
	}

	public static byte[] printBlank(int dots)
	{
		if (dots < 1)
		{
			return new byte[0];
		}
		int nl = dots % 256;
		int nh = dots / 256;
		byte[] cmdByte1 = new byte[] { 0x1B, 0x2A, 0x01, (byte) nl, (byte) nh };
		byte[] bs = new byte[dots + cmdByte1.length];
		System.arraycopy(cmdByte1, 0, bs, 0, cmdByte1.length);
		return bs;
	}

	private static Object imagePixelToPos(BufferedImage bmp, int m, int dots, int widthRange, Object toReturn) throws Exception
	{
		// ------------- 图像的基本属性 ----------------
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		int nl = width % 256;
		int nh = width / 256;
		int ch = (height + dots - 1) / dots;
		if (width > widthRange)
		{
			throw new Exception("==图像宽度超打印范围==");
		}
		// ====================END======================
		// ------------- 输出String或Byte形式的POS命令 ----------------
		StringBuffer sb = null;
		ByteArrayOutputStream baos =null;
		String cmdStr1 = null;
		String cmdStr2 = ", (byte)0x0A, \n";
		int[] cmdByte1 = new int[] { 0x1B, 0x2A, m, nl, nh };
		int[] cmdByte2 = new int[] { 0x0A };
		if (toReturn instanceof String)
		{
			sb = new StringBuffer();
			cmdStr1 = "(byte)0x1B, (byte)0x2A, (byte)0x%s, (byte)0x%s, (byte)0x%s";
			cmdStr1 = String.format(cmdStr1, new Object[] { Integer.toHexString(m), Integer.toHexString(nl), Integer.toHexString(nh) });
		}
		else if (toReturn instanceof byte[])
		{
			baos = new ByteArrayOutputStream();
		}
		else
		{
			baos = new ByteArrayOutputStream();
		}
		// =======================END==================================
		// 获得像素Raster
		Raster raster = getRaster(bmp);
		
		if (raster == null)
			throw new Exception("raster is null");

		// 按列取点
		int[] p3 = new int[dots];
		for (int h = 0; h < ch; h++)
		{
			// ------- 输出POS命令 ESC * m nl nh --------
			if (sb != null)
			{
				//port.sendString(cmdStr2);
				posCmd(sb, cmdStr1);
			}
			else if (baos != null)
			{
				posCmd(baos, cmdByte1);
			}
			// ===================END======================
			for (int w = 0; w < width; w++)
			{
				// 值填充，白点
				Arrays.fill(p3, 0xFF);
				raster.getPixels(w, h * dots, 1, (h + 1 == ch ? height - dots * h : dots), p3);
				// 把每8个点输出成一个字节
				int[] bytes = getBytes(p3);
				// 把每个字节以POS命令形式输出
				if (sb != null)
				{
					resultStratergy(sb, bytes);
				}
				else if (baos != null)
				{
					resultStratergy(baos, bytes);
				}
			}
			// ------- 输出POS命令 0x0A --------
			if (sb != null)
			{
				posCmd(sb, cmdStr2);
		
			}
			else if (baos != null)
			{
				//port.sendByte((byte[])cmdByte2);
				posCmd(baos, cmdByte2);
			}
			// ===============END=================
		}
		// ------- 字符串修正 --------
		if (sb != null && sb.length() > 3)
		{
			sb.setLength(sb.length() - 3);
			String toString = sb.toString();
			System.out.println(toString);
		}
		// =============END============
		return sb == null ? baos.toByteArray() : sb.toString();
	}

	public  static void posCmd(OutputStream os, int[] pixels) throws Exception
	{
		for (int i = 0; i < pixels.length; i++)
		{
			os.write(pixels[i]);
		}
	}

	private static void posCmd(StringBuffer sb, String cmd) throws Exception
	{
		sb.append(cmd);
	}

	public static void main(String[] args)
	{
		try
		{
			ImagePixelUtils.imagePixelToPosByte_24("c:\\ewm.bmp",32);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public static void printImageToDots(String filePath) throws Exception
	{
		File file = new File(filePath);
		BufferedImage read = ImageIO.read(file);
		WritableRaster raster = read.getRaster();
		int pixelSize = read.getColorModel().getPixelSize();
		System.out.println("==像素==" + pixelSize);
		if (pixelSize > 2)
		{
			return;
		}
		int width = read.getWidth();
		int height = read.getHeight();
		int[] pixels = new int[width];
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < height; i++)
		{
			Arrays.fill(pixels, 0xFF);
			raster.getPixels(0, i, width, 1, pixels);
			int[] p1 = getBytes(pixels);
			for (int j = 0; j < p1.length; j++)
			{
				java.text.DecimalFormat df = new java.text.DecimalFormat("00000000");
				sb.append(df.format(Integer.valueOf(Integer.toBinaryString(p1[j]))));
			}
			sb.append("\n");
		}
		System.out.println(sb.toString());
	}
}