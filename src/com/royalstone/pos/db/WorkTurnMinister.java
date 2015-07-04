/*
 * Created on 2004-6-16
 */
package com.royalstone.pos.db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.royalstone.pos.common.Operator;
import com.royalstone.pos.common.PosEnv;
import com.royalstone.pos.common.PosRequest;
import com.royalstone.pos.common.RequestLogon;
import com.royalstone.pos.journal.DataSource;
import com.royalstone.pos.util.Day;
import com.royalstone.pos.util.Response;
import com.royalstone.pos.util.WorkTurn;

/**
 * 背景: POS4.1 要求支持班次管理.
 * 引入班次管理后,在业务上有以下变化:<br/>
 * 1)需要增加新开班次和关闭班次的功能.<br/>
 * 2)登录时需要检查POS机的班次是否活动. 如果<br/>
 * @author Mengluoyi<br/>
 *
 */
public class WorkTurnMinister {

	public static void main(String[] args) 
	{
		DataSource datasrc = new DataSource( "172.16.7.163", 1433, "ApplePos" );
		Connection con = datasrc.open( "sa", "sa" );
		WorkTurnMinister minister = new WorkTurnMinister( con );
		RequestLogon req = new RequestLogon( "P001", "0001", "0001", 4 );
		Response r = minister.logon( req );
		PosEnv env = (PosEnv) r.getObject();
		System.out.println( r );
		System.out.println( env );
	}

	/**
	 * Constructor.
	 */
	public WorkTurnMinister( Connection connection )
	{
		this.connection = connection;
	}
	
	/**
	 * 登录过程需要进行以下检查：
	 * 1.该用户是否存在?
	 * 2.用户密码是否正确?
	 * 3.POS机号是否正确?
	 * 4.是否已有用户在指定的收银机上开始一个班次? 班次号是否正确? 
	 * @param req
	 * @return
	 */
	public Response logon( RequestLogon req )
	{
		String posid  		= req.getPosid();
		String cashierid 	= req.getCashierid();
		String plainpin 	= req.getPlainPin();
		int    shiftid		= req.getShiftid();  
		int	   listno; 
		WorkTurn turn;
		String localip = req.getIp();
		Response response = null;

		// search clerk_lst for operator by id.
		try {
			OperatorMinister m = new OperatorMinister( connection );
			
			System.err.println( "查找收银机..." );
			if( !isPosAvailable( posid, cashierid ) )  {
				String warning = "本店数据库中找不到收银机" + posid + "，请检查配置文件。";
				return new Response ( -1, warning );
			}
			
			System.err.println( "查找收银员..." );
			// 查找收银员.
			Operator op;
			if( ( op = m.getOperator(cashierid) ) == null ){
				String warning = "本店数据库中找不到收银员" + cashierid + "，请重新输入正确的收银员号。";
				return new Response ( -1, warning );
			}
			
			String curOp = getPower(posid); 
			// 检查收银员是否可以在指定的收银机上登录.
			if(!checkPower(cashierid,curOp)){
				String warning = "您无权打开POS机" + posid + "，请重新获得授权。";
				return new Response ( -1, warning );
			}

			System.err.println( "检查密码..." );
			// 检查密码.
			if( !op.checkPlainPin( plainpin ) ) 
				return new Response( -1, "密码不正确" );
			
			System.err.println( "查看收银员是否已在其他收银机登录..." );
			// 查看收银员是否已在其他收银机登录.
			String pos_other = getPos4Cashier( cashierid );
			if( pos_other != null && !pos_other.equals(posid) ) 
				return new Response( -1, "请先从收银台 " + pos_other + " 退出。" );
			
			System.err.println( "查看收银机班次..." );
			// 查看收银机的活动班次.
			// 如果收银机在班次表中有活动的班次,但该活动班次与收银员要求登录的班次号不一致,则拒绝登录.
			WorkTurn turn_active = getActivePosTurn( posid );
			if( turn_active != null && turn_active.getShiftid() != shiftid ){
				String warning = "您输入的班次号不正确，收银机" 
					+ posid + "尚未作班结，当前工作班次为：" 
					+ turn_active.toString() + "。";
				return 	new Response( -1, warning );
			}
			
			// 检查收银机IP地址是否与后台记录的一致.
			//if(!localip.trim().equals(getPosip(posid).trim()) ){
			if(localip.trim().compareTo(getPosip(posid).trim())!=0 ){
				String warning = "您的IP与POS机" 
									+ posid +"的IP不符，" +posid+ "的IP为：" 
									+ getPosip(posid) + "。";
				return 	new Response( -1, warning );
		    }

			// 如果收银机有活动的班次, 要根据这一活动班次号检查收银员输入的班次号是否正确.
			// 取出相应的流水号.
			if( turn_active != null && turn_active.getShiftid() == shiftid ){
				listno = getPosListno( posid );
				setCurrentOperator( posid, cashierid );
				return new Response( 0, "OK", new PosEnv(turn_active, listno+1) );
			}
				
			// 如果在班次表中没有查找到收银机的活动班次, 则需要为该收银机开始一个新的班次.
			// 首先查看卖场班次表. 核对收银员要求登录的班次与卖场班次是否一致. 
			// 如果不一致,则拒绝登录;如果一致,则新开一个班次.
			if( turn_active == null ){
				System.err.println( "查看卖场班次..." );
				WorkTurn turn_new = getShopClock();
				if( turn_new == null ){
					return new Response( -1, "错误：班次表为空。" );
				} else  if( turn_new != null && turn_new.getShiftid() != shiftid ){
					String warning = "班次号不正确。当前班次为：" + turn_new.toString() + "。";
					return 	new Response( -1, warning );
				} else if( turn_new != null && isPosTurnClosed( posid, turn_new ) ){
					return new Response( -1, "错误：要求登录的班次已经关闭。" );
				} else {
					boolean isNewDay = false;
					if (isNewDay4Pos(posid, turn_new)) {
						resetPosListno(posid);
						isNewDay = true;
					}
					System.err.println( "开始新的班次..." );
					openPosTurn( posid, turn_new );
					listno = getPosListno( posid );
					System.err.println( "在指定收银台上登录..." );
					setCurrentOperator( posid, cashierid );
					return new Response(
						0,
						"OK",
						new PosEnv(turn_new, listno + 1, isNewDay));
				}
			} 
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Response( -1, "登录失败：数据库操作出错。" );
		}

		return new Response( -1, "登录失败：原因不明。" );
	}
	
	/**
	 * 后台退出处理模块.
	 * @param req	前台发来的退出请求.
	 * @return		发往前台的应答.
	 */
	public Response logoff( PosRequest req )
	{
		String posid = req.getPosid();
		try {
			resetCurrentOperator( posid );
			return new Response( 0, "后台清退工作已完成。" );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Response( -1, "后台清退失败，再次登录可能有困难。" );
		}
	}
	
	/**
	 * 此方法用于后台班结处理.
	 * @param req	前台发来的班结请求.
	 * @return		发往前台的应答.
	 */
	public Response closeWork( PosRequest req ) 
	{
		WorkTurn posturn, shopturn;
		Response response;
		String posid = req.getPosid();
		
		try {
			shopturn = getShopClock();
			posturn  = getActivePosTurn( posid );
			if( shopturn != null && posturn != null && shopturn.compareTo(posturn) <= 0 ){
				response = new Response( -1, "班次未到，还不允许班结。" );
			} else {
				if(posturn!=null){
					closePosTurn( posid );
					response = new Response ( 0, "OK");
				}else{
					response = new Response( -1, "班次已经关闭。" );
				}
			}
		} catch (SQLException e) {
			response = new Response( -1, "Fail!" );
			e.printStackTrace();
		}
		return response;
	}
	
	/**	查看收银机清单pos_lst 中是否存在指定ID的收银机.
	 * @param posid		收银机ID.
	 * @param cashierid	收银员ID.
	 * @return
	 * @throws SQLException
	 */
	public boolean isPosAvailable( String posid, String cashierid ) throws SQLException
	{
		posid = posid.toUpperCase();
		boolean available;
		String sql = " SELECT pos_id, current_op FROM pos_lst WHERE pos_id = ?; ";
		PreparedStatement pstmt = connection.prepareStatement( sql );
		pstmt.setString( 1, posid );
		ResultSet rs 	= pstmt.executeQuery();
		available = rs.next();
		rs.close();
		pstmt.close();
		return available;
	}
	
	/**	查询指定POS机上是否有活动的班次记录.
	 * @param posid		收银机ID.
	 * @return	true	有活动的班次记录;<br/>
	 * 			false	没有活动的班次记录;
	 * @throws SQLException
	 */
	private boolean isPosTurnActive( String posid ) throws SQLException
	{
		posid = posid.toUpperCase();
		boolean isActive;
		String sql = " SELECT workdate, shiftid FROM pos_turn WHERE stat = 0 AND pos_id = ?;";
		PreparedStatement pstmt = connection.prepareStatement( sql );
		pstmt.setString( 1, posid );
		ResultSet rs 	= pstmt.executeQuery();
		isActive = rs.next();
		rs.close();
		return isActive;
	}
	
	/**	取收银机posid 的"活动班次".
	 * @param posid		收银机ID.
	 * @return a WorkTurn obj if a active pos_turn found, else return null.
	 * @throws SQLException
	 */
	private WorkTurn getActivePosTurn( String posid ) throws SQLException
	{
		posid = posid.toUpperCase();
		String sql = " SELECT workdate, shiftid FROM pos_turn WHERE stat = 0 AND pos_id = ?;";
		PreparedStatement pstmt = connection.prepareStatement( sql );
		pstmt.setString( 1, posid );
		ResultSet rs 	= pstmt.executeQuery();
		if( rs.next() ){
			Date workdate = rs.getDate( "workdate" );
			int shiftid = rs.getInt( "shiftid" );
			return new WorkTurn( new Day(workdate), shiftid );
		}
		return null;
	}
	
	/**	查看班次turn 在收银机posid 上是否已经关闭.
	 * @param posid		收银机ID.
	 * @param turn		工作班次.
	 * @return	true	已经关闭;<br/> false	尚未关闭.
	 * @throws SQLException
	 */
	private boolean isPosTurnClosed( String posid, WorkTurn turn ) throws SQLException
	{
		posid = posid.toUpperCase();
		boolean isClosed = false;
		
		String sql = " SELECT stat FROM pos_turn WHERE pos_id = ? AND workdate = ? AND shiftid = ? ; ";
		PreparedStatement pstmt = connection.prepareStatement( sql );
		pstmt.setString( 1, posid );
		pstmt.setDate( 2, new Date( turn.getWorkDate().getGregorian().getTimeInMillis() ) );
		pstmt.setInt( 3, turn.getShiftid() );
		ResultSet rs 	= pstmt.executeQuery();
		isClosed = ( rs.next() && rs.getInt(1) == 1 );
		rs.close();
		
		return isClosed;	
	}
	
	/**	判断班次turn 对编号为posid 的收银机是否新的一个营业日. 
	 * 此方法在收银员登录时使用. 如果进行营业日切换,则流水号listno 需要清零,否则不作清零处理.
	 * @param posid		收银机ID.
	 * @param turn		工作班次.
	 * @return		true	新的营业日.<br/> false	不是新的营业日.
	 * @throws SQLException
	 */
	public boolean isNewDay4Pos( String posid, WorkTurn turn ) throws SQLException
	{
		posid = posid.toUpperCase();
		boolean isNewDay = false;
		Date workdate = new Date( turn.getWorkDate().getGregorian().getTimeInMillis() ) ;
		String sql = " SELECT pos_id FROM pos_turn WHERE pos_id = ? AND workdate = ?; ";
		PreparedStatement pstmt = connection.prepareStatement( sql );
		pstmt.setString( 1, posid );
		pstmt.setDate( 2, workdate );
		ResultSet rs 	= pstmt.executeQuery();
		isNewDay = ! rs.next();
		rs.close();
		return isNewDay;
	}
	
	/** 关闭指定POS机在班次表pos_turn 中的班次记录.
	 * @param posid		收银机ID.
	 * @throws SQLException
	 */
	private void closePosTurn( String posid ) throws SQLException
	{
		String sql = " UPDATE pos_turn SET stat = 1, end_time = getdate(),IsEndOffLine=0"
					+ " WHERE stat = 0 AND pos_id = ?; ";
		PreparedStatement pstmt = connection.prepareStatement( sql );
		pstmt.setString( 1, posid );
		pstmt.executeUpdate();
	}
	
	/**	在POS班次表pos_turn 中添加一条班次记录.
	 * @param posid		收银机ID.
	 * @param turn		工作班次.
	 * @throws SQLException
	 */
	private void openPosTurn( String posid, WorkTurn turn ) throws SQLException
	{
		String sql = " INSERT INTO pos_turn ( pos_id, workdate, shiftid ) VALUES ( ?, ?, ? ); ";
		PreparedStatement pstmt = connection.prepareStatement( sql );
		pstmt.setString( 1, posid );
		pstmt.setDate( 2, new Date( turn.getWorkDate().getGregorian().getTimeInMillis() ) );
		pstmt.setInt( 3, turn.getShiftid() );
		pstmt.executeUpdate();
	}
	
	/**
	 * 查询指定收银机的工作流水号.
	 * @param posid		收银机ID.
	 * @return
	 * @throws SQLException
	 */
	private int getPosListno( String posid ) throws SQLException
	{
		String sql = " SELECT listno FROM pos_lst WHERE pos_id = ?; ";
		PreparedStatement pstmt = connection.prepareStatement( sql );
		pstmt.setString( 1, posid );
		ResultSet rs 	= pstmt.executeQuery();
		int listno = -1;
		
		if( rs.next() ) listno = rs.getInt( "listno" );
		return listno;
	}

	/**
	 * 查询收银机的IP地址.
	 * @param posid	收银员ID.
	 * @return		IP地址.
	 * @throws SQLException
	 * @author huangxuean
	 */
	public String getPosip(String posid) throws SQLException{
		posid = posid.toUpperCase();
		String ip = null;
		String sql = " SELECT ip FROM pos_lst WHERE pos_id = ?";
		PreparedStatement pstmt = connection.prepareStatement( sql );
		pstmt.setString( 1, posid );
		ResultSet rs = pstmt.executeQuery();
		while(rs.next()){
			ip = rs.getString("ip");
		}
		return ip;
	}
	
	/**
	 * @param posid
	 * @return
	 * @throws SQLException
	 * @author huangxuean
	 */
	public String getPower(String posid){
		posid = posid.toUpperCase();
		String curOp = null;
		String sql = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try{
			sql = "select * from pos_lst where pos_id = ?";	
			pstmt = connection.prepareStatement(sql);
			pstmt.setString(1,posid);
			rs = pstmt.executeQuery();
			while(rs.next()){
				curOp = rs.getString("current_op");
			}

		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}					
		return curOp;
	}
	
	/**
	 * @param cashierid
	 * @param curOp
	 * @return
	 * @author huangxuean
	 */
	public boolean checkPower(String cashierid, String curOp)
	{
		int auth = 0;
		String sql = "select auth from clerk_lst where clerk_id = ?";	
		try {
			PreparedStatement pstmt = connection.prepareStatement(sql);
			pstmt.setString(1, cashierid);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				auth = rs.getInt("auth");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

		return ( ( auth == 1 ) 					// if cashierid has force logon right,
 			|| ( curOp == null ) 				// if current operator is null,
			|| ( curOp.equals(cashierid) ) ) ;	// if current operator is cashierid.
		
	}

	/**
	 * 对指定的收银机流水号作清零处理.
	 * @param posid		收银机ID.
	 * @throws SQLException
	 */
	public void resetPosListno( String posid ) throws SQLException
	{
		posid = posid.toUpperCase();
		String sql = " UPDATE pos_lst set listno = 0 WHERE pos_id = ?; ";
		PreparedStatement pstmt = connection.prepareStatement( sql );
		pstmt.setString( 1, posid );
		pstmt.executeUpdate();
	}
	
	/**
	 * 此方法用于查询卖场的统一班次.
	 * @return	卖场的统一班次.
	 * @throws SQLException
	 */
	private WorkTurn getShopClock() throws SQLException
	{
		String sql 		= " SELECT workdate, shiftid FROM ShopClock; ";
		Statement stmt 	= connection.createStatement();
		ResultSet rs 	= stmt.executeQuery( sql );
				
		if( rs.next() ) {
			int shiftid   = rs.getInt( "shiftid" );
			Date workdate = rs.getDate( "workdate" );
			return new WorkTurn( new Day(workdate), shiftid );
		} 

		return null;
	}
	
	/**
	 * 查询收银员目前已经登录的收银机号.
	 * @param cashierid	收银员ID.
	 * @return			收银员当前正在工作的收银机ID.
	 * @throws SQLException
	 */
	public String getPos4Cashier( String cashierid ) throws SQLException
	{
		String sql = " SELECT pos_id FROM pos_lst WHERE current_op = ? " ;
		PreparedStatement pstmt;
		String posid = null;

		pstmt = connection.prepareStatement(sql);
		pstmt.setString(1, cashierid);
		ResultSet rs = pstmt.executeQuery();
		if( rs.next() ) posid = rs.getString( "pos_id" );

		posid = ( posid == null)? posid : posid.toUpperCase();

		return posid;
	}
	
	/**
	 * 查询指定POS机上正在工作的收银员ID.
	 * @param posid 收银机ID.
	 * @return 正在指定POS机上工作的收银员ID.
	 * @throws SQLException
	 */
	private String getCashier4Pos( String posid ) throws SQLException
	{
		posid = posid.toUpperCase();
		String sql = " SELECT current_op FROM pos_lst WHERE pos_id = ? " ;
		PreparedStatement pstmt;
		String operator = null;

		pstmt = connection.prepareStatement(sql);
		pstmt.setString(1, posid);
		ResultSet rs = pstmt.executeQuery();
		if( rs.next() ) operator = rs.getString( "current_op" );

		return posid;
	}
	
	/**
	 * 此方法将pos_lst 表中指定POS机的当前操作员ID置为指定的cashierid. 用于收银员登录处理.
	 * @param posid 收银机ID.
	 * @param cashierid	收银员ID.
	 * @throws SQLException 如果执行SQL出错则抛出例外.
	 */
	private void setCurrentOperator( String posid, String cashierid ) throws SQLException
	{
		posid = posid.toUpperCase();
		String sql = " UPDATE pos_lst SET current_op = ? WHERE pos_id = ?; " ;
		PreparedStatement pstmt;

		pstmt = connection.prepareStatement(sql);
		pstmt.setString(1, cashierid);
		pstmt.setString(2, posid);
		pstmt.executeUpdate();
	}

	/**
	 * 此方法将pos_lst 表中收银员ID置为NULL. 用于收银员退出处理.
	 * @param posid 收银机ID.
	 * @throws SQLException 如果执行SQL出错则抛出例外.
	 */
	private void resetCurrentOperator( String posid ) throws SQLException
	{
		posid = posid.toUpperCase();
		String sql = " UPDATE pos_lst SET current_op = NULL WHERE pos_id = ?; " ;
		PreparedStatement pstmt;
	
		pstmt = connection.prepareStatement(sql);
		pstmt.setString(1, posid);
		pstmt.executeUpdate();
	}

	private Connection connection;
}

