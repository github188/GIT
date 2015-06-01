package com.efuture.javaPos.UI;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.efuture.commonKit.ManipulatePrecision;
import com.efuture.commonKit.PosTable;
import com.efuture.javaPos.Device.NewKeyEvent;
import com.efuture.javaPos.Device.NewKeyListener;
import com.efuture.javaPos.Global.GlobalInfo;
import com.efuture.javaPos.Global.GlobalVar;
import com.efuture.javaPos.Global.Language;
import com.efuture.javaPos.Struct.GoodsDef;
import com.efuture.javaPos.Struct.GoodsPopDef;
import com.efuture.javaPos.Struct.ICallBack;
import com.efuture.javaPos.UI.Design.GoodsDetailQueryForm;
import com.swtdesigner.SWTResourceManager;

public class GoodsDetailQueryEvent 
{
	/*private Group grpInfo = null;
	private Text txtYhType = null;
	private Text txtYhPfj = null;
	private Text txtYhHyj = null;
	private Text txtYhLsj = null;
	private Text txtEndDate = null;
	private Text txtStartDate = null;*/
	
	private Text txtManyUnit = null;
	private Text txtVipAgio = null;
	private Text txtAgio = null;
	private Text txtBatchCode = null;
	private Text txtQuashRed = null;
	private Text txtAgioQuota = null;
	private Text txtWholesalePrice = null;
	private Text txtMemberPrice = null;
	private Text txtRetailPrice = null;
	private Text txtContent = null;
	private Text txtUnit = null;
	private Text txtGoodsBrand = null;
	private Text txtGoodsName = null;
	private Text txtAnalyseCode = null;
	private Text txtGoodsCode = null;
	private Text txtGoodsBarCode = null;
	private Text txtCodeType = null;
	private Text txtArkGroup = null;
	private Text txtMinBatch = null;
	private Text txtStockpile = null;
	private Text txtAgioRate = null;
	private Text txtPricePrecision = null;
	private Text txtWholesaleAgioPartake = null;
	private Text txtMemberAgioPartake = null;
	private Text txtBalance = null;
	private Text txtLargess = null;
	private Text txtPickGoods = null;
	private Text txtFittings = null;
	private Text txtMemo = null;
	//private Table tabSalesPromotion = null;
	//private Table tabBatch = null;
	private PosTable tabpop = null;
	private StringBuffer sbIsCloseForm = null;
	protected Shell shell = null;
	
	private PosTable focus = null;
	
	private GoodsDef goods = null;
	//private ArrayList batchList = null;
	private ArrayList yhList = null;
	private ICallBack callBack = null;
	private int currow = 0;
	private int pagesize = 0;
	private StringBuffer sbBarcode = null;
		
	public GoodsDetailQueryEvent(StringBuffer sbBarcode,ICallBack callBack,GoodsDetailQueryForm gdqf,GoodsDef goods,ArrayList batchList,ArrayList yhList,StringBuffer sbIsCloseForm)
	{
		tabpop = gdqf.getTabpop();
		
		this.txtManyUnit = gdqf.getTxtManyUnit();
		this.txtVipAgio = gdqf.getTxtVipAgio();
		this.txtAgio = gdqf.getTxtAgio();
		this.txtBatchCode = gdqf.getTxtBatchCode();
		this.txtQuashRed = gdqf.getTxtQuashRed();
		this.txtAgioQuota = gdqf.getTxtAgioQuota();
		this.txtWholesalePrice = gdqf.getTxtWholesalePrice();
		this.txtMemberPrice = gdqf.getTxtMemberPrice();
		this.txtRetailPrice = gdqf.getTxtRetailPrice();
		this.txtContent = gdqf.getTxtContent();
		this.txtUnit = gdqf.getTxtUnit();
		this.txtGoodsBrand = gdqf.getTxtGoodsBrand();
		
		this.txtGoodsName = gdqf.getTxtGoodsName();
		this.txtAnalyseCode = gdqf.getTxtAnalyseCode();
		this.txtGoodsCode = gdqf.getTxtGoodsCode();
		this.txtGoodsBarCode = gdqf.getTxtGoodsBarCode();
		this.txtCodeType = gdqf.getTxtCodeType();
		this.txtArkGroup = gdqf.getTxtArkGroup();

		this.txtMinBatch = gdqf.getTxtMinBatch();
		this.txtStockpile = gdqf.getTxtStockpile();
	
		this.txtAgioRate = gdqf.getTxtAgioRate();
		this.txtPricePrecision = gdqf.getTxtPricePrecision();
		this.txtMemberAgioPartake = gdqf.getTxtMemberAgioPartake();
		this.txtWholesaleAgioPartake = gdqf.getTxtWholesaleAgioPartake();
		this.txtBalance = gdqf.getTxtBalance();
		this.txtLargess = gdqf.getTxtLargess();
		this.txtPickGoods = gdqf.getTxtPickGoods();
		this.txtFittings = gdqf.getTxtFittings();
		this.txtMemo = gdqf.getTxtMemo();
		
		/*tabSalesPromotion = gdqf.getTabSalesPromotion();
		tabBatch = gdqf.getTabBatch();
		
		this.grpInfo = gdqf.getGrpInfo();
		this.txtYhType = gdqf.getTxtYhType();
		this.txtYhPfj = gdqf.getTxtYhPfj();
		this.txtYhHyj = gdqf.getTxtYhHyj();
		this.txtYhLsj = gdqf.getTxtYhLsj();
		this.txtEndDate = gdqf.getTxtEndDate();
		this.txtStartDate = gdqf.getTxtStartDate();*/
		
		this.goods = goods;
		//this.batchList = batchList;
		this.yhList = yhList;
		this.sbBarcode = sbBarcode;
		this.callBack = callBack;
		this.sbIsCloseForm = sbIsCloseForm;
		
		shell = gdqf.getShell();
		 
		//设定键盘事件
        NewKeyEvent event = new NewKeyEvent()
	    {
	            public void keyDown(KeyEvent e,int key)
	            {
	            	keyPressed(e,key);
	            }
	
	            public void keyUp(KeyEvent e,int key)
	            {
	            	keyReleased(e,key);
	            }
	     };
	     
	       FocusListener listener = new FocusListener()
	        {
	            public void focusGained(FocusEvent e)
	            {
	                if (focus != e.widget)
	                {
	                    focus.setFocus();
	                }
	            }

	            public void focusLost(FocusEvent e)
	            {
	            	
	            }
	        };
	     
	     NewKeyListener key = new NewKeyListener();
	     key.event = event;
	     
	     tabpop.addKeyListener(key);
	     tabpop.addFocusListener(listener);
	     
	     setFocus(tabpop);
	     
	    /* tabSalesPromotion.addKeyListener(key);
	     tabSalesPromotion.addFocusListener(listener);*/
	     
		 txtManyUnit.addFocusListener(listener);
		 txtVipAgio.addFocusListener(listener);
		 txtAgio.addFocusListener(listener);
		 txtBatchCode.addFocusListener(listener);
		 txtQuashRed.addFocusListener(listener);
		 txtAgioQuota.addFocusListener(listener);
		 txtWholesalePrice.addFocusListener(listener);
		 txtMemberPrice.addFocusListener(listener);
		 txtRetailPrice.addFocusListener(listener);
		 txtContent.addFocusListener(listener);
		 txtUnit.addFocusListener(listener);
		 txtGoodsBrand.addFocusListener(listener);
		
		 txtGoodsName.addFocusListener(listener);
		 txtAnalyseCode.addFocusListener(listener);
		 txtGoodsCode.addFocusListener(listener);
		 txtGoodsBarCode.addFocusListener(listener);
		 txtCodeType.addFocusListener(listener);
		 txtArkGroup.addFocusListener(listener);

		 txtMinBatch.addFocusListener(listener);
		 txtStockpile.addFocusListener(listener);
	
		 txtAgioRate.addFocusListener(listener);
		 txtPricePrecision.addFocusListener(listener);
		 txtMemberAgioPartake.addFocusListener(listener);
		 txtWholesaleAgioPartake.addFocusListener(listener);
		 txtBalance.addFocusListener(listener);
		 txtLargess.addFocusListener(listener);
		 txtPickGoods.addFocusListener(listener);
		 txtFittings.addFocusListener(listener);
		 txtMemo.addFocusListener(listener);
	
	     /*txtYhType.addFocusListener(listener);
	     txtYhPfj.addFocusListener(listener); 
	     txtYhHyj.addFocusListener(listener); 
	     txtYhLsj.addFocusListener(listener);
	     txtEndDate.addFocusListener(listener);
	     txtStartDate.addFocusListener(listener);*/
			
	     init();
	}
	
	private void init()
	{
		txtArkGroup.setText(goods.gz);
		txtCodeType.setText(String.valueOf(goods.type));
		txtAnalyseCode.setText(goods.fxm);
		txtGoodsName.setText(goods.name);
		txtGoodsBarCode.setText(goods.barcode);
		txtGoodsCode.setText(goods.code);
		
		txtGoodsBrand.setText(goods.ppcode);
		txtUnit.setText(goods.unit);
		txtContent.setText(ManipulatePrecision.doubleToString(goods.bzhl));
		
		txtMinBatch.setText(String.valueOf(goods.minplsl));
		txtStockpile.setText(ManipulatePrecision.doubleToString(goods.kcsl));
		txtRetailPrice.setText(ManipulatePrecision.doubleToString(goods.lsj));
		txtMemberPrice.setText(ManipulatePrecision.doubleToString(goods.hyj));
		txtWholesalePrice.setText(ManipulatePrecision.doubleToString(goods.pfj));
	
		txtAgioQuota.setText(ManipulatePrecision.doubleToString(goods.maxzke));
		txtAgioRate.setText(ManipulatePrecision.doubleToString(goods.maxzkl));
		txtPricePrecision.setText(ManipulatePrecision.doubleToString(goods.jgjd));
		txtMemberAgioPartake.setText(ManipulatePrecision.doubleToString(goods.hyjzkfd));
		txtWholesaleAgioPartake.setText(ManipulatePrecision.doubleToString(goods.pfjzkfd));
		txtQuashRed.setText(String.valueOf(goods.isxh));
		txtBatchCode.setText(String.valueOf(goods.isbatch));
		txtAgio.setText(String.valueOf(goods.issqkzk));
		txtVipAgio.setText(String.valueOf(goods.isvipzk));
		txtManyUnit.setText(String.valueOf(goods.isuid));
		txtLargess.setText(String.valueOf(goods.iszs));
		txtBalance.setText(String.valueOf(goods.isdzc));
		txtFittings.setText(String.valueOf(goods.ispj));
		txtPickGoods.setText(String.valueOf(goods.iszt));
		txtMemo.setText(goods.memo);
		
	
		/*tabBatch.setBounds(10, 360,774, 108);
		tabSalesPromotion.setBounds(10,360,124,108);
		grpInfo.setBounds(140,354,640,114);*/
		
		/*if (batchList != null && batchList.size() > 0)
		{
			for (int i = 0;i < batchList.size();i++)
			{
				GoodsAmountDef gad = (GoodsAmountDef)batchList.get(i);
				
				String value[] = {String.valueOf(gad.plsl),ManipulatePrecision.doubleToString(gad.pllsj),ManipulatePrecision.doubleToString(gad.plhyj),gad.ksrq,gad.jsrq};
				
				TableItem item = new TableItem(tabBatch, SWT.NONE);
				
				item.setText(value);
			}
			
			tabBatch.setSelection(0);
		}*/
		
		if (yhList != null && yhList.size() > 0)
		{
			for (int i = 0;i < yhList.size();i++)
			{
				GoodsPopDef gpd = (GoodsPopDef)yhList.get(i);
				
				String value[] = {String.valueOf(changeType(gpd.type)),
				                  String.valueOf(gpd.ksrq +" "+ gpd.kssj),
				                  String.valueOf(gpd.jsrq +" "+ gpd.jssj),
				                  ManipulatePrecision.doubleToString(gpd.poplsj),
				                  ManipulatePrecision.doubleToString(gpd.pophyj),
				                  ManipulatePrecision.doubleToString(gpd.poppfj)};
				
				TableItem item = new TableItem(tabpop, SWT.NONE);
				
				item.setText(value);
			}
			
			tabpop.setSelection(0);
		}
	/*	if (yhList != null && yhList.size() > 0)
		{
			for (int i = 0;i < yhList.size();i++)
			{
				GoodsPopDef gpd = (GoodsPopDef)yhList.get(i);
				
				String value[] = {String.valueOf(gpd.seqno)};
				
				TableItem item = new TableItem(tabSalesPromotion, SWT.NONE);
				
				item.setText(value);
			}
			
			GoodsPopDef gpd = (GoodsPopDef)yhList.get(0);
			
			txtYhType.setText(changeType(gpd.type)); 
			txtYhPfj.setText(ManipulatePrecision.doubleToString(gpd.poppfj)); 
			txtYhHyj.setText(ManipulatePrecision.doubleToString(gpd.pophyj));
			txtYhLsj.setText(ManipulatePrecision.doubleToString(gpd.poplsj)); 
			txtEndDate.setText(gpd.jsrq +" "+ gpd.jssj); 
			txtStartDate.setText(gpd.ksrq + " " + gpd.kssj); 
		}*/
	}
	/*
	private void findYhInfo(long seqno)
	{
		try
		{
			if (yhList == null ||  yhList.size() <1) return ;
			
			for (int i = 0;i < yhList.size();i++)
			{
				GoodsPopDef gpd = (GoodsPopDef)yhList.get(i);
				
				if (gpd.seqno == seqno)
				{
					txtYhType.setText(changeType(gpd.type)); 
					txtYhPfj.setText(ManipulatePrecision.doubleToString(gpd.poppfj)); 
					txtYhHyj.setText(ManipulatePrecision.doubleToString(gpd.pophyj));
					txtYhLsj.setText(ManipulatePrecision.doubleToString(gpd.poplsj)); 
					txtEndDate.setText(gpd.jsrq +" "+ gpd.jssj); 
					txtStartDate.setText(gpd.ksrq + " " + gpd.kssj); 
					break;
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	*/
    private void setFocus(PosTable focus)
    {
        this.focus = focus;
        focus.setFocus();
    }
    
    private String changeType(char num)
    {
    	String type = "";
    	
    	switch(num)
    	{
    		case '1':
    			type = Language.apply("单品");
    		break;	
    		case '2':
    			type = Language.apply("柜组");
    		break;	
    		case '3':
    			type = Language.apply("品类");
    		break;	
    		case '4':
    			type = Language.apply("柜组品牌");
    		break;	
    		case '5':
    			type = Language.apply("品类品牌");
    		break;	
    		case '6':
    			type = Language.apply("品牌");
    		break;	
    		case '7':
    			type = Language.apply("电子秤");
    		break;		
    	}
    	
    	return type;
    }
    
	public void keyPressed(KeyEvent e,int key)
    {
		try
		{
			switch(key)
			{
				case GlobalVar.ArrowUp:
					if (currow > 0)
					{
						currow = currow - 1;

						setSelection(currow, false);
					}
				break;
				case GlobalVar.ArrowDown:
					if ((currow < (tabpop.getItemCount() - 1)) &&
	                        (tabpop.getItemCount() >= 0))
	                {
	                    currow = currow + 1;
	
	                    setSelection(currow, true);
	                }
				break;	
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
  
    }

    public void keyReleased(KeyEvent e,int key)
    {
    	try
    	{
    		switch(key)
    		{
    			case GlobalVar.Validation:
    				addGoodsToSaleForm();    				
    				
    				break;
    			/*case GlobalVar.Pay:
    				if (e.getSource() == tabBatch)
    				{
    					e.data = "";
    					tabSalesPromotion.setVisible(true);
    					tabBatch.setVisible(false);
    					setFocus(tabSalesPromotion);
    					tabSalesPromotion.setSelection(0);
    					grpInfo.setVisible(true);
    				}
    				else if (e.getSource() == tabSalesPromotion)
    				{
    					e.data = "";
    					tabBatch.setVisible(true);
    					tabSalesPromotion.setVisible(false);
    					grpInfo.setVisible(false);
    					setFocus(tabBatch);
    				}
    			break;
    			case GlobalVar.ArrowDown:
    				if (e.getSource() == tabSalesPromotion)
    				{
    					if ((tabSalesPromotion.getSelectionIndex() <= (tabSalesPromotion.getItemCount() - 1)) && tabSalesPromotion.getItemCount() > 0)
    				 	{
        						TableItem tableItem = tabSalesPromotion.getItem(tabSalesPromotion.getSelectionIndex());
        						findYhInfo(Long.parseLong(tableItem.getText(0)));
    				 	}
    				}
    			break;	
    			case GlobalVar.ArrowUp:
    				if (e.getSource() == tabSalesPromotion)
    				{
    					if (tabSalesPromotion.getSelectionIndex() >= 0)
    					{
    						TableItem tableItem = tabSalesPromotion.getItem(tabSalesPromotion.getSelectionIndex());
    						findYhInfo(Long.parseLong(tableItem.getText(0)));
    					}
    				}

    			break;	*/
    			case GlobalVar.PageUp:
    				if (currow > 0)
                    {
                        int showpage = 0;

                        if (pagesize == 0)
                        {
                            if (currow > ((tabpop.getBounds().height -
                            		tabpop.getHeaderHeight()) / tabpop.getItemHeight()))
                            {
                                pagesize = tabpop.getSelectionIndex() -
                                           ((tabpop.getBounds().height -
                                        		   tabpop.getHeaderHeight()) / tabpop.getItemHeight());
                                showpage = pagesize -
                                           ((tabpop.getBounds().height -
                                        		   tabpop.getHeaderHeight()) / tabpop.getItemHeight()) -
                                           1;
                            }
                            else
                            {
                                showpage = 0;
                            }
                        }
                        else
                        {
                            pagesize = pagesize -
                                       ((tabpop.getBounds().height -
                                    		   tabpop.getHeaderHeight()) / tabpop.getItemHeight());
                            showpage = pagesize -
                                       ((tabpop.getBounds().height -
                                    		   tabpop.getHeaderHeight()) / tabpop.getItemHeight()) -
                                       1;
                        }

                        if (showpage > 0)
                        {
                            setSelection(showpage, currow, false);
                            currow = showpage;
                        }
                        else
                        {
                            setSelection(0, currow, false);
                            currow   = 0;
                            pagesize = 0;
                        }
                    }
    			break;
    			case GlobalVar.PageDown:
    				if ((currow < (tabpop.getItemCount() - 1)) && ((tabpop.getItemCount() - 1) > 1))
                    {
                        int showpage = 0;

                        if (currow < pagesize)
                        {
                            pagesize = currow;
                        }

                        pagesize = pagesize +
                                   ((tabpop.getBounds().height -
                                		   tabpop.getHeaderHeight()) / tabpop.getItemHeight());
                        showpage = pagesize +
                                   ((tabpop.getBounds().height -
                                		   tabpop.getHeaderHeight()) / tabpop.getItemHeight()) +
                                   1;

                        if (showpage < (tabpop.getItemCount() - 1))
                        {
                            setSelection(showpage, currow, true);
                            currow = showpage;
                        }
                        else
                        {
                            setSelection(tabpop.getItemCount() - 1,
                                         currow, true);
                            currow = tabpop.getItemCount() - 1;

                            pagesize = tabpop.getItemCount() - 1;
                        }
                    }
    			break;	
    			case GlobalVar.Exit:    				
    				shell.close();
    				shell.dispose();
    				shell = null;
    			break;
    		}
    		
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }
    
    private void addGoodsToSaleForm()
    {
    	if (callBack == null) return ; 
    	callBack.exec(txtGoodsBarCode.getText());
    	if (GlobalInfo.sysPara.isMoreSelectQuerygoods == 'N')
    	{
    		//单选的时候,关闭窗口
    		if (sbBarcode != null) this.sbBarcode.append(txtGoodsBarCode.getText());
    		this.sbIsCloseForm.append("Y");
    		keyReleased(null,GlobalVar.Exit);
    	}
    }
    
    public void setSelection(int index, boolean flag)
    {
        setSelection(index, -1, flag);
    }

    public void setSelection(int index, int indexb, boolean flag)
    {
        int i = 0;

        if (flag)
        {
            i = index - 1;
        }
        else
        {
            i = index + 1;
        }

        TableItem item = null;

        if (indexb < 0)
        {
            if ((i >= 0) && (i < tabpop.getItemCount()))
            {
                item = tabpop.getItem(i);
                item.setBackground(SWTResourceManager.getColor(255, 255, 255));
                item.setForeground(SWTResourceManager.getColor(0, 0, 0));
            }
        }
        else
        {
            if ((indexb >= 0) && (i < tabpop.getItemCount()))
            {
                item = tabpop.getItem(indexb);
                item.setBackground(SWTResourceManager.getColor(255, 255, 255));
                item.setForeground(SWTResourceManager.getColor(0, 0, 0));
            }
        }

        if ((index < tabpop.getItemCount()) && (index >= 0))
        {
            item = tabpop.getItem(index);
            item.setBackground(SWTResourceManager.getColor(43, 61, 219));
            item.setForeground(SWTResourceManager.getColor(255, 255, 255));
        }

        showSelection(index);
    }

    public void showSelection(int curRow)
    {
        if ((curRow < tabpop.getItemCount()) && (curRow >= 0))
        {
            TableItem item = tabpop.getItem(curRow);
            tabpop.showItem(item);
        }
    }
}
