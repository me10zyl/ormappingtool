/**
 * ���ڴ��븴�ã�����������DB��������е���ͬ����
 * ������DB�������ĸ���
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
	 * ����DB������඼��ͬӵ�еı���
	 */
	protected Connection con = null;
	protected PreparedStatement pst = null;
	protected ResultSet rst = null;
	
	/**
	 * ���PreparedStatement����
	 * @param sql
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	protected  abstract PreparedStatement getPreparedStatement(String sql) throws ClassNotFoundException, SQLException;
	/**
	 * �ͷ����ݿ�����
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
