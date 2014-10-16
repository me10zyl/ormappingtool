/**
 * 用于代码复用，即放置所有DB表操作类中的相同代码
 * 是所有DB表操作类的父类
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

/**
 * @author Administrator
 *
 */
public abstract class DBMain 
{
	/**
	 * 所有DB表操作类都共同拥有的变量
	 */
	protected Connection con = null;
	protected PreparedStatement pst = null;
	protected ResultSet rst = null;
	
	/**
	 * 获得PreparedStatement对象
	 * @param sql
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	protected  abstract PreparedStatement getPreparedStatement(String sql) throws ClassNotFoundException, SQLException;
	/**
	 * 释放数据库连接
	 * @throws SQLException
	 */
	public void realese() throws SQLException
	{
		if(rst != null)
		{
			rst.close();
		}
		if(pst != null)
		{
			pst.close();
		}
		if(con != null)
		{
			con.close();
		}

	}
	
	
	
}
