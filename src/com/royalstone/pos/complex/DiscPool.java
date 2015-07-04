/*
 * Created on 2004-6-1
 */
package com.royalstone.pos.complex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * DiscPool 用于实现组合促销中的等效商品组.
 * 在同一个DiscPool 中的商品具有可互换性. 例如, 在商品a/b/c中10元任选3只,则商品a/b/c在同一个DiscPool中.
 * @author Mengluoyi
 */
public class DiscPool implements Serializable {
	public static void main(String[] args) {
		DiscPool a, b, c, d, m;
		a = new DiscPool("666301", "666301", 3, 1000);
		b = new DiscPool("666301", "666302", 3, 1000);
		c = new DiscPool("666301", "666303", 3, 1000);

		m = DiscPool.merge(a, b);
		m = DiscPool.merge(m, c);

		System.out.println(a);
		System.out.println(b);
		System.out.println(c);
		System.out.println(m);

		a = new DiscPool("666701", "666701", 7, 2000);
		b = new DiscPool("666701", "666702", 7, 2000);
		c = new DiscPool("666701", "666703", 7, 2000);
		d = new DiscPool("666701", "666704", 7, 2000);
		m = DiscPool.merge(a, b);
		m = DiscPool.merge(m, c);
		m = DiscPool.merge(m, d);

		System.out.println(m);
	}

	/**
	 * @param leader	等效商品组的组长
	 */
	public DiscPool(String leader) {
		this.leader = leader;
	}

	/**	Constructor
	 * @param leader	商品组长
	 * @param member	商品组成员
	 * @param qty		商品组的生效数量
	 * @param pool_price	商品组的促销价格
	 */
	public DiscPool(String leader, String member, int qty, int pool_price) {
		this.leader = leader;
		members = new String[1];
		members[0] = member;
		qty_per_group = qty;
		this.pool_price = pool_price;
	}

	/**	判断商品编码是否在DiscPool 的成员表中.
	 * @param vgno	商品编码
	 * @return	true	传入的商品编码就在商品小组中;<br/>
	 * 			false	传入的商品编码不在商品小组中.
	 */
	public boolean hasVgno(String vgno) {
		for (int i = 0; i < members.length; i++)
			if (vgno.equals(members[i]))
				return true;

		return false;
	}

	/**	判断两个DiscPool 是否有相同的组长. 如果组长相同,就认为是同一个等效组,可以进行合并.
	 * @param b		DiscPool 对象
	 * @return	true	传入参数与this对象有相同的组长;<br/>
	 * 			false	组长不同.
	 */
	public boolean isSamePool(DiscPool b) {
		return (this.leader.equals(b.leader));
	}

	/**	判断等效商品组是否赠品.
	 * @return		true	赠品;<br/>
	 * 				false	非赠品.
	 */
	public boolean isGift() {
		return (pool_price == 0);
	}

	/**	如果两个等效商品组的组长相同,可以把两个等效组合并. 由该函数完成合并.
	 * @param a	DiscPool对象
	 * @param b	DiscPool对象
	 * @return	新的	DiscPool对象, 其中成员商品为a与b的并集.
	 */
	public static DiscPool merge(DiscPool a, DiscPool b) {
		// 如果不是同一个等效组,则返回为null.
		if (!a.isSamePool(b))
			return null;

		DiscPool new_item = new DiscPool(a.leader);
		new_item.qty_per_group =
			(a.qty_per_group > b.qty_per_group)
				? a.qty_per_group
				: b.qty_per_group;
		new_item.pool_price =
			(a.pool_price > b.pool_price) ? a.pool_price : b.pool_price;
		new_item.members = new String[a.members.length + b.members.length];

		int i;
		for (i = 0; i < a.members.length; i++)
			new_item.members[i] = a.members[i];
		for (int j = 0; j < b.members.length; i++, j++)
			new_item.members[i] = b.members[j];

		return new_item;
	}

	/**	取生效数量
	 * @return	为使促销方案生效,小组内商品数量和应达到的最小数量.
	 */
	public int getMinQty() {
		return qty_per_group;
	}

	/**	取促销售价
	 * @return	DiscPool 的促销售价(该售价对应的商品数量为qty_per_group)
	 */
	public int getPromPrice() {
		return pool_price;
	}

	/**
	 * for debug use.
	 */
	public String toString() {
		String s = (members.length > 0) ? members[0] : "";
		for (int i = 1; i < members.length; i++)
			s += (", " + members[i]);

		return "{ "
			+ leader
			+ " * "
			+ qty_per_group
			+ " @"
			+ pool_price
			+ " : "
			+ " [ "
			+ s
			+ " ] "
			+ " }";
	}

	public DiscPool[] split() {

		ArrayList memberList = new ArrayList();

		if (qty_per_group < members.length) {

			if (members.length > 1 && qty_per_group > 1) {

				for (int i = 0; i < members.length; i++) {
					for (int j = 0; j < members.length; j++) {
						if (i != j) {
							ArrayList horizonList = new ArrayList();
							horizonList.add(members[i]);
							horizonList.add(members[j]);
							memberList.add(horizonList);
						}
					}
				}

				for (int k = 2; k < qty_per_group; k++) {

					ArrayList newMemberList = new ArrayList();
					for (int i = 0; i < members.length; i++) {
						for (int j = 0; j < memberList.size(); j++) {
							if (!((ArrayList) memberList.get(j))
								.contains(members[i])) {
								ArrayList horizonList = new ArrayList();
								horizonList.addAll(
									(ArrayList) memberList.get(j));
								horizonList.add(members[i]);
								newMemberList.add(horizonList);
							}
						}
					}
					memberList = newMemberList;

				}

			}

		}

		ArrayList allMemberList = new ArrayList();
		allMemberList.addAll(Arrays.asList(members));
		memberList.add(allMemberList);

		DiscPool[] result = new DiscPool[members.length + memberList.size()];

		for (int i = 0; i < members.length; i++) {
			result[i] =
				new DiscPool(members[i], members[i], qty_per_group, pool_price);
		}

		for (int i = 0; i < memberList.size(); i++) {

			ArrayList horizonList = (ArrayList) memberList.get(i);
			if (horizonList.size() > 0) {
				result[members.length + i] =
					new DiscPool(
						(String) horizonList.get(0),
						(String) horizonList.get(0),
						qty_per_group,
						pool_price);
			}

			String[] s = new String[horizonList.size()];
			for (int j = 0; j < horizonList.size(); j++) {
				s[j] = (String) horizonList.get(j);
			}

			result[members.length + i].setMembers(s);
		}

		return result;

	}

	public void setMembers(String[] members) {
		this.members = members;
	}

	/**
	 * <code>leader</code>		等效商品组的"组长"
	 */
	private String leader = "";

	/**
	 * <code>members</code>		等效商品组所包括的成员,每个成员是一种商品.
	 */
	private String[] members = null;

	/**
	 * <code>qty_per_group</code>	为使促销方案生效,小组内商品数量和应达到的最小数量.
	 */
	private int qty_per_group = 1;

	/**
	 * <code>pool_price</code>		在促销方案中,该小组的售价(以分为单位).
	 */
	private int pool_price = 0;
}
