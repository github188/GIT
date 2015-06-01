package com.efuture.javaPos.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Vector;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import com.efuture.commonKit.CommonMethod;
import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.MessageBox;
import com.efuture.commonKit.PathFile;
import com.efuture.javaPos.Struct.BankLogDef;

class Test
{
	Test()
	{
        PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
        DocPrintJob job = printService.createPrintJob();            
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
        DocAttributeSet das = new HashDocAttributeSet();

        try
        {
            DocFlavor flavor = DocFlavor.INPUT_STREAM.JPEG;
        	FileInputStream obj = new FileInputStream("c:\\1.jpg");
        	Doc doc = new SimpleDoc(obj, flavor, das);
            job.print(doc, pras);
        }
        catch(PrintException pe)
        {
            pe.printStackTrace();
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
	}
	
    public static void main(String[] args) 
    {
    	double[] row_set = new double[]{259,36,18};
		double yfd = 0;
		int row = -1;
		double lje = -1;
		for (int j = 0; j < row_set.length; j++)
		{
			// 把剩余未分摊金额，直接分摊到最后一个商品,最后一个商品不处理价格精度
			double lszszk = 0;
			lszszk = ManipulatePrecision.doubleConvert( row_set[j]/ 313.00 * 100, 2, 1);
			double je = ManipulatePrecision.doubleConvert(row_set[j] - lszszk, 2, 1);
	    	je = ManipulatePrecision.doubleConvert(ManipulatePrecision.doubleConvert(ManipulatePrecision.doubleConvert(je / 0.1,2,1), 0, 1) * 0.1, 2, 1);	        
	        double zk = ManipulatePrecision.doubleConvert(row_set[j] - lszszk - je, 2, 1);
	        zk = ManipulatePrecision.doubleConvert(lszszk + zk, 2, 1);
	        lszszk = zk;
	        
			//计算已分摊的金额
			yfd = ManipulatePrecision.doubleConvert(yfd+lszszk, 2, 1);
			
			// 计算完全后记录下当前最大可分摊的商品，用于将剩余金额分摊到商品上
			double jsje1 = ManipulatePrecision.doubleConvert(row_set[j]);
			if (jsje1 >= lje)
			{
				row = j;
				lje = jsje1;
			}

			// 如果是最后一个可分摊的商品计算完全后，检查是否还有未分摊的金额
			if (j == row_set.length - 1 && ManipulatePrecision.doubleCompare(100,yfd, 2) != 0 && row > -1)
			{
				lszszk = ManipulatePrecision.doubleConvert(100 - yfd, 2, 1);
				System.out.println(String.valueOf(lszszk));
			}
		}	
    	
    	Vector vec = new Vector();
    	vec.add(String.valueOf("1"));
    	vec.add(String.valueOf("2"));
    	vec.add(String.valueOf("3"));
    	
    	Vector vec1 = new Vector();
    	vec1.add(vec.elementAt(0));
    	
/*    	
    	Vector vec = new Vector();
    	CmPopGoodsDef cmp = new CmPopGoodsDef();
    	vec.add(cmp);
    	Vector newvec = new Vector();
    	newvec.add(vec.elementAt(0));
    	
		long start = System.currentTimeMillis();
		System.out.println("Start:" + start);
    	System.out.println(ExpressionDeal.SpiltExpression("SUB(' 789',0,4)"));
		long end = System.currentTimeMillis();
		System.out.println("End  :" + end + "   耗时:"+(end - start));
		System.out.println(String.format("33%6s %08.2f", new Object[]{"12345","12.34"}));
    	return;

    	System.out.println(Thread.currentThread().getContextClassLoader().getResource(""));    
    	System.out.println(Test.class.getClassLoader().getResource(""));    
    	System.out.println(ClassLoader.getSystemResource(""));    
    	System.out.println(Test.class.getResource(""));    
    	System.out.println(Test.class.getResource("/"));    
    	System.out.println(new File("").getAbsolutePath());    
    	System.out.println(System.getProperty("user.dir"));  
*/
    }
    
	public static boolean XYKReadResult()
	{
		BufferedReader br = null;

		try
		{
			if (!PathFile.fileExist("G:\\result.txt") || ((br = CommonMethod.readFileGBK("G:\\result.txt")) == null))
			{
				new MessageBox("读取金卡工程应答数据失败!", null, false);

				return false;
			}
			BankLogDef bld = new BankLogDef();
			
			// 读取请求数据
			String line = br.readLine();

			String[] ret = line.split(","); 
			bld.retcode = ret[13];

			if (bld.retcode.equals("00"))
			{
				bld.bankinfo = ret[26];
				bld.cardno =ret[1];
			}
			else
			{
				if (ret.length > 29) bld.retmsg = ret[30].trim();
			}

			return true;
		}
		catch (Exception ex)
		{
			new MessageBox("读取金卡工程应答数据异常!" + ex.getMessage(), null, false);
			ex.printStackTrace();
			return false;
		}
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				catch (IOException e)
				{
					// TODO 自动生成 catch 块
					new MessageBox("PFACE.TXT 关闭失败\n重试后如果仍然失败，请联系信息部");
					e.printStackTrace();
				}
			}
		}
	}
}
