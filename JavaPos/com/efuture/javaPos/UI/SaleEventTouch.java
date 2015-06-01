package com.efuture.javaPos.UI;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;

import com.efuture.commonKit.Convert;
import com.efuture.commonKit.ExpressionDeal;
import com.efuture.javaPos.Global.ConfigClass;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.UI.Design.SaleForm;
import com.swtdesigner.SWTResourceManager;

public class SaleEventTouch extends SaleEvent
{	
    public SaleEventTouch(SaleForm saleform) {
		super(saleform);
		
		readTouchModel();
		
		paintbutton(saleform);
		
		initsalegroup(saleform);
	}
    public int TG1Row = 2;
    public int TG1Column = 6;
    public int TG1RowInterval = 12;
    public int TG1ColumnInterval = 6;
    public int TG1width = 60;
    public int TG1height = 36;
    public String TG1FontName = "宋体";
    public int TG1FontSize = 10;
    
    public int TG1KeyUpBKCorR = 253;
    public int TG1KeyUpBKCorG = 242;
    public int TG1KeyUpBKCorB = 108;
    public int TG1KeyUpFECorR = 0;
    public int TG1KeyUpFECorG = 64;
    public int TG1KeyUpFECorB = 128;
    
    public int TG1KeyDownBKCorR = 0;
    public int TG1KeyDownBKCorG = 64;
    public int TG1KeyDownBKCorB = 128;
    public int TG1KeyDownFECorR = 255;
    public int TG1KeyDownFECorG = 255;
    public int TG1KeyDownFECorB = 255;
    
    public int TG2Row = 6;
    public int TG2Column = 6;
    public int TG2RowInterval = 10;
    public int TG2ColumnInterval = 6;
    public int TG2width = 60;
    public int TG2height = 60;
    public String TG2FontName = "宋体";
    public int TG2FontSize = 10;
    
    public int TG2KeyUpBKCorR = 255;
    public int TG2KeyUpBKCorG = 142;
    public int TG2KeyUpBKCorB = 142;
    public int TG2KeyUpFECorR = 0;
    public int TG2KeyUpFECorG = 64;
    public int TG2KeyUpFECorB = 128;
    
    public int TG2KeyDownBKCorR = 0;
    public int TG2KeyDownBKCorG = 64;
    public int TG2KeyDownBKCorB = 128;
    public int TG2KeyDownFECorR = 255;
    public int TG2KeyDownFECorG = 255;
    public int TG2KeyDownFECorB = 255;
    
    final int grouptype = 0;
    final int goodstype = 1;
    
	public CLabel[][] groupBotton = null;
    public String[][] groupCode = null;
    public int[] groupPageUp = new int[]{2,5};
    public int[] groupPageDown = new int[]{2,6};
    public int curgrouppage = 1;
    
    public CLabel[][] goodsBotton = null;
    public String[][] goodsCode = null;
    public int[] goodsPageUp = new int[]{5,5};
    public int[] goodsPageDown = new int[]{5,5};
    public int curgoodspage = 1;
    
    // 翻页标识
    public String pageUpFlag = "<<";
    public String pageDownFlag = ">>";
    
    /*
    final Font pageUpDownfont = SWTResourceManager.getFont("宋体", 10, SWT.BOLD);
	
    // 按键颜色
    
	final Color groupbkmouseup = SWTResourceManager.getColor(253,242,108);
	final Color groupfemouseup = SWTResourceManager.getColor(0, 64, 128);
	
	final Color groupbkmousedown = SWTResourceManager.getColor(0, 64, 128);
	final Color groupfemousedown = SWTResourceManager.getColor(255, 255, 255);
	
	final Color goodsbkmouseup = SWTResourceManager.getColor(255,142,142);
	final Color goodsfemouseup = SWTResourceManager.getColor(0, 64, 128);
	
	final Color goodsbkmousedown = SWTResourceManager.getColor(0, 64, 128);
	final Color goodsfemousedown = SWTResourceManager.getColor(255, 255, 255);
	*/
    
    final Font pageUpDownfont = SWTResourceManager.getFont("宋体", 10, SWT.BOLD);
    
    // 按键颜色
	Color groupbkmouseup = SWTResourceManager.getColor(TG1KeyUpBKCorR,TG1KeyUpBKCorG,TG1KeyUpBKCorB);
	Color groupfemouseup = SWTResourceManager.getColor(TG1KeyUpFECorR, TG1KeyUpFECorG, TG1KeyUpFECorB);
	
	Color groupbkmousedown = SWTResourceManager.getColor(TG1KeyDownBKCorR, TG1KeyDownBKCorG, TG1KeyDownBKCorB);
	Color groupfemousedown = SWTResourceManager.getColor(TG1KeyDownFECorR, TG1KeyDownFECorG, TG1KeyDownFECorB);
	
	Color goodsbkmouseup = SWTResourceManager.getColor(TG2KeyUpBKCorR,TG2KeyUpBKCorG,TG2KeyUpBKCorB);
	Color goodsfemouseup = SWTResourceManager.getColor(TG2KeyUpFECorR, TG2KeyUpFECorG, TG2KeyUpFECorB);
	
	Color goodsbkmousedown = SWTResourceManager.getColor(TG2KeyDownBKCorR, TG2KeyDownBKCorG, TG2KeyDownBKCorB);
	Color goodsfemousedown = SWTResourceManager.getColor(TG2KeyDownFECorR, TG2KeyDownFECorG, TG2KeyDownFECorB);
	
	// 字体
	Font groupfont = SWTResourceManager.getFont(TG1FontName, TG1FontSize, SWT.NONE);
	Font goodsfont = SWTResourceManager.getFont(TG2FontName, TG2FontSize, SWT.NONE);
	
    Vector v = new Vector(); 

    public void paintbutton(SaleForm saleform)
    {
    	if (saleform.group_group == null) return;
        
    	saleform.groupBotton = new CLabel[TG1Row][TG1Column];
    	saleform.groupCode = new String[TG1Row][TG1Column];
    	
    	groupBotton = saleform.groupBotton;
    	groupCode = saleform.groupCode;
    		
        for (int i=0;i<groupBotton.length;i++)
        {
        	for (int j=0;j<groupBotton[i].length;j++)
        	{
        		CLabel label_2 = new CLabel(saleform.group_group, SWT.CENTER | SWT.SHADOW_OUT | SWT.BORDER);
        		label_2.setAlignment(SWT.CENTER);
                FormData fd_label_2 = new FormData();
                fd_label_2.top = new FormAttachment(0, -3 + i*(TG1height+TG1RowInterval));
                fd_label_2.bottom = new FormAttachment(0, 30 + i*(TG1height+TG1RowInterval));
                fd_label_2.left = new FormAttachment(0, 6 + j*(TG1width+TG1ColumnInterval));
                fd_label_2.right = new FormAttachment(0, 66 + j*(TG1width+TG1ColumnInterval));
                label_2.setLayoutData(fd_label_2);                
                label_2.setText(Language.apply("散装月饼"));
                
                groupBotton[i][j] = label_2;
        	}
        }
		
        saleform.group_group.redraw();
        
        if (saleform.group_goods == null) return;
        
    	saleform.goodsBotton = new CLabel[TG2Row][TG2Column];
    	saleform.goodsCode = new String[TG2Row][TG2Column];
    	
		goodsBotton = saleform.goodsBotton;
		goodsCode = saleform.goodsCode;
		
		for (int i=0;i<goodsBotton.length;i++)
        {
        	for (int j=0;j<goodsBotton[i].length;j++)
        	{
                final CLabel label_3 = new CLabel(saleform.group_goods, SWT.CENTER | SWT.SHADOW_OUT | SWT.BORDER);
                final FormData fd_label_3 = new FormData();
                fd_label_3.top = new FormAttachment(0, 1 + i*(TG2height+TG2RowInterval));
                fd_label_3.bottom = new FormAttachment(0, 60 + i*(TG2height+TG2RowInterval));
                fd_label_3.left = new FormAttachment(0, 6 + j*(TG2width+TG2ColumnInterval));
                fd_label_3.right = new FormAttachment(0, 66 + j*(TG2width+TG2ColumnInterval));
                label_3.setLayoutData(fd_label_3);
                label_3.setText(Language.apply("三明治和\n奶黄面包\n¥ 100.00"));        
                
    	        goodsBotton[i][j] = label_3;
        	}
        }   
        
        saleform.group_goods.redraw();
    }
    
    public void initsalegroup(SaleForm saleform)
    {
    	if (saleform.group_group == null) return;
    
	    groupPageUp = new int[]{groupBotton.length-1,groupBotton[groupBotton.length-1].length-2};
	    groupPageDown = new int[]{groupBotton.length-1,groupBotton[groupBotton.length-1].length-1};
		
		goodsPageUp = new int[]{goodsBotton.length-1,goodsBotton[goodsBotton.length-1].length-2};
	    goodsPageDown = new int[]{goodsBotton.length-1,goodsBotton[goodsBotton.length-1].length-1};
	    
	    // 获取所有分组
	    saleBS.getAllGroup();
	    
		MouseAdapter magroup = new MouseAdapter()
		{
			public void mouseUp(final MouseEvent arg0)
			{
				try
				{
					CLabel cl = (CLabel)arg0.widget;
					if (cl.getText().length() > 0)
					{
						cl.setBackground(groupbkmouseup);
						cl.setForeground(groupfemouseup);
					}
					
					buttonclick(grouptype,cl);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}

			public void mouseDown(final MouseEvent arg0)
			{
				CLabel cl = (CLabel)arg0.widget;
				
				if (cl.getText().length() > 0)
				{
					cl.setBackground(groupbkmousedown);
					cl.setForeground(groupfemousedown);
				}
			}
		};
		
		MouseAdapter magoods = new MouseAdapter()
		{
			public void mouseUp(final MouseEvent arg0)
			{
				try
				{
					CLabel cl = (CLabel)arg0.widget;
					if (cl.getText().length() > 0)
					{
						cl.setBackground(goodsbkmouseup);
						cl.setForeground(goodsfemouseup);
					}

					buttonclick(goodstype,cl);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}

			public void mouseDown(final MouseEvent arg0)
			{
				CLabel cl = (CLabel)arg0.widget;
				if (cl.getText().length() > 0)
				{
					cl.setBackground(goodsbkmousedown);
					cl.setForeground(goodsfemousedown);
				}
			}
		};
		
		for (int i = 0;i < groupBotton.length;i++)
		{
			for (int j = 0;j < groupBotton[i].length;j++)
			{
				groupBotton[i][j].addMouseListener(magroup);
				groupBotton[i][j].setBackground(groupbkmouseup);
				groupBotton[i][j].setForeground(groupfemouseup);
				groupBotton[i][j].setFont(groupfont);
				groupBotton[i][j].setText("");
				
				int[] groupindex = new int[]{i,j};
				groupBotton[i][j].setData(groupindex);
				
				if (groupPageUp[0] == i && groupPageUp[1] == j)
				{
					groupBotton[i][j].setText(pageUpFlag);
					groupBotton[i][j].setFont(pageUpDownfont);
				}
				else if (groupPageDown[0] == i && groupPageDown[1] == j)
				{
					groupBotton[i][j].setText(pageDownFlag);
					groupBotton[i][j].setFont(pageUpDownfont);
				}
			}
		}
		
		for (int i = 0;i < goodsBotton.length;i++)
		{
			for (int j = 0;j < goodsBotton[i].length;j++)
			{
				goodsBotton[i][j].addMouseListener(magoods);
				goodsBotton[i][j].setBackground(goodsbkmouseup);
				goodsBotton[i][j].setForeground(goodsfemouseup);
				goodsBotton[i][j].setFont(goodsfont);
				goodsBotton[i][j].setText("");
				
				int[] goodsindex = new int[]{i,j};
				goodsBotton[i][j].setData(goodsindex);
				
				if (goodsPageUp[0] == i && goodsPageUp[1] == j)
				{
					goodsBotton[i][j].setText(pageUpFlag);
					goodsBotton[i][j].setFont(pageUpDownfont);
				}
				else if (goodsPageDown[0] == i && goodsPageDown[1] == j)
				{
					goodsBotton[i][j].setText(pageDownFlag);
					goodsBotton[i][j].setFont(pageUpDownfont);
				}
			}
		}
		
		buttonclick(this.grouptype,groupBotton[groupPageUp[0]][groupPageUp[1]]);
		buttonclick(this.grouptype,groupBotton[0][0]);
    }
    
    public int pageUpDown(CLabel cl,CLabel[][] ggButtons,String[][] ggCode,int[] pageUp,int[] pageDown,int curPage,Vector allContent)
    {
    	boolean ispage = false;
    	int[] index = (int[])cl.getData();
    	
		int onepagecount = ggButtons.length * ggButtons[0].length - 2; 
		int pagecount =allContent.size()/onepagecount + ((allContent.size()%onepagecount)>0?1:0);

    	if (index[0] == pageUp[0] && index[1] == pageUp[1])
    	{
    		// 向上翻页
    		ispage = true;
    		
    		curPage = curPage -1;
    		if (curPage < 1) curPage = 1;
    	}
    	else if (index[0] == pageDown[0] && index[1] == pageDown[1])
    	{
    		// 向下翻页
    		ispage = true;
    		
    		curPage = curPage + 1;
    		if (curPage > pagecount) curPage = pagecount;
    	}

    	// 是翻页键
		if (ispage)
		{
			int startindex = (curPage-1) * onepagecount;

			for (int i = 0;i < ggButtons.length;i++)
			{
				for (int j = 0;j < ggButtons[i].length;j++)
				{
					if (i == pageUp[0] && j == pageUp[1])
					{
						//ggButtons[i][j].setText(((curPage - 1)<0?0:(curPage - 1)) + pageUpFlag);
						if ((curPage - 1) <= 0)
						{
							ggButtons[i][j].setText("");
						}
						else
						{
							ggButtons[i][j].setText(pageUpFlag);
						}
						continue;
					}
					else if (i == pageDown[0] && j == pageDown[1])
					{
						//ggButtons[i][j].setText(pageDownFlag + (((pagecount - curPage)<0)?0:(pagecount - curPage)));
						
						if ((pagecount - curPage) <= 0)
						{
							ggButtons[i][j].setText("");
						}
						else
						{
							ggButtons[i][j].setText(pageDownFlag);
						}
						
						continue;
					}
					                 
					// 柜组，名称
					if (startindex < allContent.size())
					{
						// 0-代码 1-名称
						String[] mfs = (String[])allContent.get(startindex++); 
						
						ggButtons[i][j].setText(mfs[1]);
						
						ggCode[i][j] = mfs[0];
					}
					else
					{
						ggButtons[i][j].setText("");
						
						ggCode[i][j] = "";
					}
				}
			}
			
			return curPage;
		}
		
		return -1;
    }
    
    public void buttonclick(int buttontype, CLabel cl)
    {    	    	
    	int[] index = (int[])cl.getData();
    	
    	CLabel[][] ggButtons = null;
    	String[][] ggCode = null;
    	int[] pageUp = null;
    	int[] pageDown = null;
    	int curPage = 1;
    	
    	Vector allContent = null;
    	
    	if (buttontype == this.grouptype)
    	{
			ggButtons = groupBotton;
			ggCode = groupCode;
			pageUp = groupPageUp;
			pageDown = groupPageDown;
			curPage = curgrouppage;
			allContent = saleBS.AllManaframe;
    	}
    	else if (buttontype == this.goodstype)
    	{
			ggButtons = goodsBotton;
			ggCode = goodsCode;
			pageUp = goodsPageUp;
			pageDown = goodsPageDown;
			curPage = curgoodspage;
			allContent = saleBS.AllGoods;
    	}
    	else
    	{
    		return;
    	}

    	int page = pageUpDown(cl,ggButtons,ggCode,pageUp,pageDown,curPage,allContent);
		
    	// 是翻页键
		if (page > 0)
		{
			if (buttontype == this.grouptype)
			{
				curgrouppage = page;
			}
			else if (buttontype == this.goodstype)
			{
				curgoodspage = page;
			}
		}
		else
		{
			if (buttontype == this.grouptype)
			{
				saleBS.getGoods(ggCode[index[0]][index[1]]);
				
				curgoodspage = 0;
				
				buttonclick(goodstype,goodsBotton[goodsPageUp[0]][goodsPageUp[1]]);
			}
			else if (buttontype == this.goodstype)
			{
				saleBS.addQueryGoodsInfo(ggCode[index[0]][index[1]]);
			}
		}
    }
    
    private void readTouchModel()
	{
		String[] s;
		String[] s1 = ConfigClass.TouchModelCfg.split("\\|");
    	if (s1.length > 0)
    	{
    		s = s1[0].split(",");
    		
    		if (s.length > 0 && s[0].trim().length() > 0) TG1Row = Convert.toInt(s[0].trim());
    		if (s.length > 1 && s[1].trim().length() > 0) TG1Column = Convert.toInt(s[1].trim());
    		if (s.length > 2 && s[2].trim().length() > 0) TG1RowInterval = Convert.toInt(s[2].trim());
    		if (s.length > 3 && s[3].trim().length() > 0) TG1ColumnInterval = Convert.toInt(s[3].trim());
    		if (s.length > 4 && s[4].trim().length() > 0) TG1width = Convert.toInt(s[4].trim());
    		if (s.length > 5 && s[5].trim().length() > 0) TG1height = Convert.toInt(s[5].trim());
    		if (s.length > 6 && s[6].trim().length() > 0) TG1FontName = s[6].trim();
    		if (s.length > 7 && s[7].trim().length() > 0) TG1FontSize = Convert.toInt(s[7].trim());
    		
    		if (s.length > 8 && s[8].trim().length() > 0) 
    		{
    			String srgb = ExpressionDeal.replace(s[8].toLowerCase().trim(),"0x","");
    			if (srgb.trim().length() == 6)
    			{
    				TG1KeyUpBKCorR = Integer.parseInt(srgb.substring(0,2),16);
    				TG1KeyUpBKCorG = Integer.parseInt(srgb.substring(2,4),16);
    				TG1KeyUpBKCorB = Integer.parseInt(srgb.substring(4,6),16);
    			}
    		}
    		
    		if (s.length > 9 && s[9].trim().length() > 0) 
    		{
    			String srgb = ExpressionDeal.replace(s[9].toLowerCase().trim(),"0x","");
    			if (srgb.trim().length() == 6)
    			{
    				TG1KeyUpFECorR = Integer.parseInt(srgb.substring(0,2),16);
    				TG1KeyUpFECorG = Integer.parseInt(srgb.substring(2,4),16);
    				TG1KeyUpFECorB = Integer.parseInt(srgb.substring(4,6),16);
    			}
    		}
    		
    		if (s.length > 10 && s[10].trim().length() > 0) 
    		{
    			String srgb = ExpressionDeal.replace(s[10].toLowerCase().trim(),"0x","");
    			if (srgb.trim().length() == 6)
    			{
    				TG1KeyDownBKCorR = Integer.parseInt(srgb.substring(0,2),16);
    				TG1KeyDownBKCorG = Integer.parseInt(srgb.substring(2,4),16);
    				TG1KeyDownBKCorB = Integer.parseInt(srgb.substring(4,6),16);
    			}
    		}
    		
    		if (s.length > 11 && s[11].trim().length() > 0) 
    		{
    			String srgb = ExpressionDeal.replace(s[11].toLowerCase().trim(),"0x","");
    			if (srgb.trim().length() == 6)
    			{
    				TG1KeyDownFECorR = Integer.parseInt(srgb.substring(0,2),16);
    				TG1KeyDownFECorG = Integer.parseInt(srgb.substring(2,4),16);
    				TG1KeyDownFECorB = Integer.parseInt(srgb.substring(4,6),16);
    			}
    		}
    	}
    	
    	if (s1.length > 1)
    	{
    		s = s1[1].split(",");
    		
    		if (s.length > 0 && s[0].trim().length() > 0) TG2Row = Convert.toInt(s[0].trim());
    		if (s.length > 1 && s[1].trim().length() > 0) TG2Column = Convert.toInt(s[1].trim());
    		if (s.length > 2 && s[2].trim().length() > 0) TG2RowInterval = Convert.toInt(s[2].trim());
    		if (s.length > 3 && s[3].trim().length() > 0) TG2ColumnInterval = Convert.toInt(s[3].trim());
    		if (s.length > 4 && s[4].trim().length() > 0) TG2width = Convert.toInt(s[4].trim());
    		if (s.length > 5 && s[5].trim().length() > 0) TG2height = Convert.toInt(s[5].trim());
    		if (s.length > 6 && s[6].trim().length() > 0) TG2FontName = s[6].trim();
    		if (s.length > 7 && s[7].trim().length() > 0) TG2FontSize = Convert.toInt(s[7].trim());
    		
    		if (s.length > 8 && s[8].trim().length() > 0) 
    		{
    			String srgb = ExpressionDeal.replace(s[8].toLowerCase().trim(),"0x","");
    			if (srgb.trim().length() == 6)
    			{
    				TG2KeyUpBKCorR = Integer.parseInt(srgb.substring(0,2),16);
    				TG2KeyUpBKCorG = Integer.parseInt(srgb.substring(2,4),16);
    				TG2KeyUpBKCorB = Integer.parseInt(srgb.substring(4,6),16);
    			}
    		}
    		
    		if (s.length > 9 && s[9].trim().length() > 0) 
    		{
    			String srgb = ExpressionDeal.replace(s[9].toLowerCase().trim(),"0x","");
    			if (srgb.trim().length() == 6)
    			{
    				TG2KeyUpFECorR = Integer.parseInt(srgb.substring(0,2),16);
    				TG2KeyUpFECorG = Integer.parseInt(srgb.substring(2,4),16);
    				TG2KeyUpFECorB = Integer.parseInt(srgb.substring(4,6),16);
    			}
    		}
    		
    		if (s.length > 10 && s[10].trim().length() > 0) 
    		{
    			String srgb = ExpressionDeal.replace(s[10].toLowerCase().trim(),"0x","");
    			if (srgb.trim().length() == 6)
    			{
    				TG2KeyDownBKCorR = Integer.parseInt(srgb.substring(0,2),16);
    				TG2KeyDownBKCorG = Integer.parseInt(srgb.substring(2,4),16);
    				TG2KeyDownBKCorB = Integer.parseInt(srgb.substring(4,6),16);
    			}
    		}
    		
    		if (s.length > 11 && s[11].trim().length() > 0) 
    		{
    			String srgb = ExpressionDeal.replace(s[11].toLowerCase().trim(),"0x","");
    			if (srgb.trim().length() == 6)
    			{
    				TG2KeyDownFECorR = Integer.parseInt(srgb.substring(0,2),16);
    				TG2KeyDownFECorG = Integer.parseInt(srgb.substring(2,4),16);
    				TG2KeyDownFECorB = Integer.parseInt(srgb.substring(4,6),16);
    			}
    		}
    	}
    	
    	// 按键颜色
    	groupbkmouseup = SWTResourceManager.getColor(TG1KeyUpBKCorR,TG1KeyUpBKCorG,TG1KeyUpBKCorB);
    	groupfemouseup = SWTResourceManager.getColor(TG1KeyUpFECorR, TG1KeyUpFECorG, TG1KeyUpFECorB);
    	
    	groupbkmousedown = SWTResourceManager.getColor(TG1KeyDownBKCorR, TG1KeyDownBKCorG, TG1KeyDownBKCorB);
    	groupfemousedown = SWTResourceManager.getColor(TG1KeyDownFECorR, TG1KeyDownFECorG, TG1KeyDownFECorB);
    	
    	goodsbkmouseup = SWTResourceManager.getColor(TG2KeyUpBKCorR,TG2KeyUpBKCorG,TG2KeyUpBKCorB);
    	goodsfemouseup = SWTResourceManager.getColor(TG2KeyUpFECorR, TG2KeyUpFECorG, TG2KeyUpFECorB);
    	
    	goodsbkmousedown = SWTResourceManager.getColor(TG2KeyDownBKCorR, TG2KeyDownBKCorG, TG2KeyDownBKCorB);
    	goodsfemousedown = SWTResourceManager.getColor(TG2KeyDownFECorR, TG2KeyDownFECorG, TG2KeyDownFECorB);
    	
    	// 字体
    	groupfont = SWTResourceManager.getFont(TG1FontName, TG1FontSize, SWT.NONE);
    	goodsfont = SWTResourceManager.getFont(TG2FontName, TG2FontSize, SWT.NONE);
	}
    
    /*
    public void goodsclick(CLabel cl)
    {
    	int[] index = (int[])cl.getData();
    	
    	boolean ispage = false;
    	
		int onepagecount = goodsBotton.length * goodsBotton[0].length - 2; 
		//int pagecount = saleBS.AllManaframe.size()/onepagecount + (saleBS.AllManaframe.size()%onepagecount)>0?1:0;

    	if (index[0] == goodsPageUp[0] && index[1] == goodsPageUp[1])
    	{
    		// 向上翻页
    		ispage = true;
    		
    		curgrouppage = curgrouppage -1;
    		if (curgrouppage < 1) curgrouppage = 1;
    	}
    	else if (index[0] == goodsPageDown[0] && index[1] == goodsPageDown[1])
    	{
    		// 向下翻页
    		
    		ispage = true;
    		curgrouppage = curgrouppage +1;
    		if (curgrouppage > pagecount) curgrouppage = pagecount;
    	}
    	else
    	{

    	}
    	
    	if (ispage)
		{
			int startindex = (curgrouppage-1) * onepagecount;

			for (int i = 0;i < groupBotton.length;i++)
			{
				for (int j = 0;j < groupBotton[i].length;j++)
				{
					if (i == groupPageUp[0] && j == groupPageUp[1])
					{
						continue;
					}
					else if (i == groupPageDown[0] && j == groupPageDown[1])
					{
						continue;
					}
					                 
					// 柜组，名称
					if (startindex < saleBS.AllManaframe.size())
					{
						String[] mfs = (String[])saleBS.AllManaframe.get(startindex++); 
						groupBotton[i][j].setText(mfs[1]);
						
						groupCode[i][j] = mfs[0];
					}
					else
					{
						groupBotton[i][j].setText("");
						
						groupCode[i][j] = "";
					}
				}
			}
		}
    }
    */
}
