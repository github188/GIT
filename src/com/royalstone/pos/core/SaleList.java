package com.royalstone.pos.core;
import java.io.Serializable;
import java.util.Vector;

import com.royalstone.pos.common.Goods;
import com.royalstone.pos.common.Sale;
import com.royalstone.pos.complex.DiscCell;
import com.royalstone.pos.complex.DiscComplex;
import com.royalstone.pos.complex.DiscComplexList;
import com.royalstone.pos.favor.BulkFavor;
import com.royalstone.pos.favor.DiscCriteria;
import com.royalstone.pos.favor.DiscPrice;
import com.royalstone.pos.favor.DiscRate;
import com.royalstone.pos.favor.Discount;

/**
   @version 1.0 2004.05.13
   @author  Mengluoyi, Royalstone Co., Ltd.
 */

public class SaleList implements Serializable {
	/**
	 * 
	 */
	public SaleList() {
		soldlst = new Vector();
	}

	/**
	 * @return
	 */
	public int size() {
		return soldlst.size();
	}

	/**
	 * @param i
	 * @return
	 */
	public Sale get(int i) {
		return (Sale) soldlst.get(i);
	}

	/**
	   @return TotalValue = Sold - Corrected - QuickCorrected - Withdrawed.
	 */
	/**
	 * @return
	 */
	public int getTotalValue() {
		int v = 0;
		for (int i = 0; i < soldlst.size(); i++) {
			Sale s = (Sale) soldlst.get(i);
			v += s.getFactValue();
		}
		return v;
	}
	
	/**
	 * @return
	 */
	public int getValueWithoutDisc(){
		int v = 0;
		for(int i = 0;i<soldlst.size();i++){
			Sale s = (Sale)soldlst.get(i);
			if(s != null && s.getDiscValue() == 0){			
				v +=s.getStdValue();
			}
		}
		return v;
	}
	
	/**
	 * @return
	 */
	public Vector getNumsWithoutDisc(){
		Vector nums = new Vector();
		for(int i = 0;i<soldlst.size();i++){
			Sale s = (Sale)soldlst.get(i);
			if(s != null && s.getDiscValue() == 0){			
				nums.add(new Integer(i));
			}
		}
		return nums;
	}

	/**
	   @return Total Discount Value, including auto disc, promotion, altprice disc, manual disc. 
	 */
	/**
	 * @return
	 */
	public int getTotalDisc() {
		int v = 0;
		for (int i = 0; i < soldlst.size(); i++) {
			Sale s = (Sale) soldlst.get(i);	
			v += s.getDiscValue();		
		}	
//System.out.println("²âÊÔs.getDiscValue():" +v);			
		return v;
	}

	// get the last obj in soldlist.
	/**
	 * @return
	 */
	public Sale getLastItem() {
		int n = soldlst.size();
		if (n == 0)
			return null;
		return (Sale) soldlst.get(n - 1);
	}
	
	/**
	 * @param code
	 * @return
	 */
	public Sale getItemBCode(String code){
		Sale s = null;
		for (int i = 0; i < soldlst.size(); i++) {
			s = (Sale) soldlst.get(i);
			if (s.getType() != Sale.WITHDRAW && s.matches(code))
				return s;
		}
		return null;
	}

	/**
	   @param code may be vgno or barcode;
	   @return total goods qty ( Sold - Corrected - QickCorrected ). Withdrawed items are not counted.
	 */
	/**
	 * @param code
	 * @return
	 */
	public int getSoldQty(String code) {
		int q = 0;
		for (int i = 0; i < soldlst.size(); i++) {
			Sale s = (Sale) soldlst.get(i);
			if (s.getType() != Sale.WITHDRAW && s.matches(code))
				q += s.getQty();
		}
		return q;
	}

	/**
	 * @param g
	 * @return
	 */
	public int getSoldQty(Goods g) {
		int q = 0;
		for (int i = 0; i < soldlst.size(); i++) {
			Sale s = (Sale) soldlst.get(i);
			if (s.getType() != Sale.WITHDRAW && s.matches(g))
				q += s.getQty();
		}
		return q;
	}
	
	public int getSoldQty() {
		int q = 0;
		for (int i = 0; i < soldlst.size(); i++) {
			
			Sale s = (Sale) soldlst.get(i);
			
			if(s.getGoods().getX()==1){
				q+=s.getQty();	
			}else{
				if(s.getQty()<0){
					q--;	
				}else{
					q++;
				}
			}
		}
		return q;
	}


	/**
	 * @param g
	 * @return
	 */
	public int getQtyDisc(Goods g) {
		int q = 0;
		for (int i = 0; i < soldlst.size(); i++) {
			Sale s = (Sale) soldlst.get(i);
			if (s.getType() != Sale.WITHDRAW && s.matches(g))
				q += s.getQtyDisc();
		}
		return q;
	}

	/**
	 * @param saleitem
	 */
	public void add(Sale saleitem) {
		soldlst.add(saleitem);
	}

	/**
	   All Sale record matches code will be altered.
	   @param code	may be vgno or barcode of goods.
	   @param dtype may be DISC_QUANTITY, DISC_MANUAL ...
	   @param discpoint discount point off the std price. 
	   	If discpoint is 5, the value customers should pay is 95% * std value.
	 */

	/**
	 * @param g
	 * @param disc
	 */
	public void setGoodsDisc(Goods g, DiscRate disc) {
		for (int i = 0; i < soldlst.size(); i++) {
			Sale s = (Sale) soldlst.get(i);
			if (s.getType() != Sale.WITHDRAW && s.matches(g))
				s.setDiscount(disc);
		}
	}

	/**
	 * @param g
	 * @param disc
	 */
	public void setGoodsDisc(Goods g, DiscPrice disc) {
		for (int i = 0; i < soldlst.size(); i++) {
			Sale s = (Sale) soldlst.get(i);
			if( s.getType() != Sale.WITHDRAW && s.matches( g ) )
				s.setDiscount( disc );
		}
	}

	/**
	 * @param g
	 */
	public void clearDiscount(Goods g) {
		for (int i = 0; i < soldlst.size(); i++) {
			Sale s = (Sale) soldlst.get(i);
			if (s.getType() != Sale.WITHDRAW
				&& s.matches(g)
				&& s.getDiscType() != Discount.ALTPRICE && s.getDiscType() != Discount.SINGLE 
				&& s.getDiscType() != Discount.TOTAL && s.getDiscType() != Discount.MONEY
				&& s.getDiscType() != Discount.LOANDISC)
				s.clearDiscount();
		}
	}

	/**
	   @param dtype may be DISC_PROMOTION, DISC_MEMBER ...
	 */
	/**
	 * @param code
	 * @param dtype
	 * @param newPrice
	 */
	public void setGoodsPrice(String code, int dtype, int newPrice) {
		for (int i = 0; i < soldlst.size(); i++) {
			Sale s = (Sale) soldlst.get(i);
			if (s.getType() != Sale.WITHDRAW && s.matches(code))
				s.setFactPrice(dtype, newPrice);
		}
	}

	/**
	 * @param disc_lst
	 */
	public void consumeFavor(DiscComplexList disc_lst) {
		//clearFavor();
		for (int i = 0; i < disc_lst.size(); i++)
			consumeFavor(disc_lst.get(i));
	}

	///////////////////////// private methods follow /////////////////////////// 	
	/**
	 * 
	 */
	private void clearFavor() {
		for (int i = 0; i < this.size(); i++)
			this.get(i).clearFavor();
	}

	/**
	 * @param disc
	 */
	public void consumeFavor(DiscComplex disc) {
		for (int i = 0; i < disc.size(); i++)
			consumeFavor(disc.get(i), disc.name());
	}
	
	
	public long caculateFavor(DiscComplex disc) {
		
		long totalDisc=0;
		for (int i = 0; i < disc.size(); i++){
			totalDisc+=caculateFavorValue(disc.get(i), disc.name());
		}
		return totalDisc;
			
	}


	public long caculateFavorAfter(DiscComplex disc) {
		
		long totalDisc=0;
		for (int i = 0; i < disc.size(); i++){
			totalDisc+=caculateFavorValueAfter(disc.get(i), disc.name());
		}
		return totalDisc;
			
	}



	/**
	 * @param disc
	 */
	public void consumeFavorAfter(DiscComplex disc) {
		for (int i = 0; i < disc.size(); i++)
			consumeFavorAfter(disc.get(i), disc.name());
	}


	/**
	 * @param cell
	 * @param favor_name
	 */
	private void consumeFavor(DiscCell cell, String favor_name) {
		int qty_favor = cell.getFavorQty();
		int value_favor = cell.getFavorValue();

		for (int i = 0; qty_favor > 0 && i < soldlst.size(); i++) {
			Sale sale = this.get(i);

			// NOTE: only sold items consume favor, corrected & withdrawed do not.
			if (sale.getType() == Sale.SALE && sale.getquickcorrect()!='y' 
				&& cell.hasVgno(sale.getVgno())
				&& (sale.getGoods().getPType() != null
					&& sale.getGoods().getPType().equals(
						DiscCriteria.DISCCOMPLEX))) {
				
				int lastfavor = soldlst.size() -1;
				if( i == lastfavor ){
					sale.setLastFavor();
				}
				
				sale.clearFavor();
				int qty_favorX=qty_favor*sale.getGoods().getX();
				int qty_consume = 0;
				int value_consume = 0;
				qty_consume =
					(sale.getQty() < qty_favorX) ? sale.getQty() : qty_favorX;
				value_consume =
					(qty_favorX == qty_consume)
						? value_favor
						: (value_favor * qty_consume / qty_favorX);
				sale.consumeFavor(qty_consume, value_consume, favor_name);
				qty_favor -= qty_consume/sale.getGoods().getX();
				value_favor -= value_consume;
			}
			
		}
	}
	
	private long caculateFavorValue(DiscCell cell, String favor_name) {
		int qty_favor = cell.getFavorQty();
		int value_favor = cell.getFavorValue();
		
		long totalDisc=0;

		for (int i = 0; qty_favor > 0 && i < soldlst.size(); i++) {
			Sale sale = this.get(i);

			// NOTE: only sold items caculate favor, corrected & withdrawed do not.
			if (sale.getType() == Sale.SALE && sale.getquickcorrect()!='y' 
				&& cell.hasVgno(sale.getVgno())
				&& (sale.getGoods().getPType() != null
					&& sale.getGoods().getPType().equals(
						DiscCriteria.DISCCOMPLEX))) {
				
				int lastfavor = soldlst.size() -1;
				if( i == lastfavor ){
					sale.setLastFavor();
				}
				
				sale.clearFavor();
				int qty_favorX=qty_favor*sale.getGoods().getX();
				int qty_consume = 0;
				int value_consume = 0;
				qty_consume =
					(sale.getQty() < qty_favorX) ? sale.getQty() : qty_favorX;
				value_consume =
					(qty_favorX == qty_consume)
						? value_favor
						: (value_favor * qty_consume / qty_favorX);
				totalDisc+=sale.caculateFavor(qty_consume, value_consume, favor_name);
				qty_favor -= qty_consume/sale.getGoods().getX();
				value_favor -= value_consume;
			}
			
		}
		
		return totalDisc;
	}
	
	
	private long caculateFavorValueAfter(DiscCell cell, String favor_name) {
		int qty_favor = cell.getFavorQty();
		int value_favor = cell.getFavorValue();

		long totalDisc=0;
		for (int i = 0; qty_favor > 0 && i < soldlst.size(); i++) {
			Sale sale = this.get(i);

			// NOTE: only sold items consume favor, corrected & withdrawed do not.
			if (sale.getType() == Sale.SALE && sale.getquickcorrect()!='y'
				&& cell.hasVgno(sale.getVgno())
				&& (sale.getGoods().getPType() != null
					&& sale.getGoods().getPType().equals(
						DiscCriteria.DISCCOMPLEX))) {
				int qty_favorX=qty_favor*sale.getGoods().getX();
				int qty_consume = 0;
				int value_consume = 0;
				qty_consume =
					((sale.getQty()-sale.getQtyDisc()) < qty_favorX) ? (sale.getQty()-sale.getQtyDisc()) : qty_favorX;
				value_consume =
					(qty_favorX == qty_consume)
						? value_favor
						: (value_favor * qty_consume / qty_favorX);
				totalDisc+=sale.caculateFavorAfter(qty_consume, value_consume, favor_name);
				qty_favor -= qty_consume/sale.getGoods().getX();
				value_favor -= value_consume;
			}
		}
		
		return totalDisc;
	}



	/**
	 * @param cell
	 * @param favor_name
	 */
	private void consumeFavorAfter(DiscCell cell, String favor_name) {
		int qty_favor = cell.getFavorQty();
		int value_favor = cell.getFavorValue();

		for (int i = 0; qty_favor > 0 && i < soldlst.size(); i++) {
			Sale sale = this.get(i);

			// NOTE: only sold items consume favor, corrected & withdrawed do not.
			if (sale.getType() == Sale.SALE && sale.getquickcorrect()!='y'
				&& cell.hasVgno(sale.getVgno())
				&& (sale.getGoods().getPType() != null
					&& sale.getGoods().getPType().equals(
						DiscCriteria.DISCCOMPLEX))) {
				int qty_favorX=qty_favor*sale.getGoods().getX();
				int qty_consume = 0;
				int value_consume = 0;
				qty_consume =
					((sale.getQty()-sale.getQtyDisc()) < qty_favorX) ? (sale.getQty()-sale.getQtyDisc()) : qty_favorX;
				value_consume =
					(qty_favorX == qty_consume)
						? value_favor
						: (value_favor * qty_consume / qty_favorX);
				sale.consumeFavorAfter(qty_consume, value_consume, favor_name);
				qty_favor -= qty_consume/sale.getGoods().getX();
				value_favor -= value_consume;
			}
		}
	}


	/**
	 * @param g
	 * @param favor_total
	 */
	public void consumeBulkFavor(Goods g, BulkFavor favor_total) {
		if (favor_total == null || favor_total.getQty() == 0)
			return;
		for (int i = soldlst.size() - 1;
			i >= 0 && favor_total.getQty() > 0;
			i--) {
			Sale sale = this.get(i);
			if (sale.getType() == Sale.SALE && g.matches(sale.getGoods())) {
				int qty_favored = sale.getQtyDisc();
				int qty_unfavor = sale.getQty() - sale.getQtyDisc();
				BulkFavor favor = favor_total.shareFavor(qty_unfavor);
				sale.consumeBulkFavor(favor);
			}
		}
	}
	
	/**
	 * @return
	 */
	public Sale remove(){
		if(soldlst.size()>0){
			return (Sale)soldlst.remove(soldlst.size()-1);	
		}else{
			return null;
		}
	}

	/**
	 * Comment for <code>soldlst</code>
	 */
	private Vector soldlst;
}
