import java.io.IOException;
import java.sql.SQLException;

public class ConsoleMain
{
	public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException
	{
		BuildTool tool = new BuildTool("100.1.0.46", "1433", "bookshop_case", "sa", "sa", BuildTool.DATABASE_SQLSERVER);
//			tool.buildClass("userGroup","com.ccniit.bookshop.db.oRMapping");
			tool.buildDBMain("com.ccniit.bookshop.db.oRMapping");
//			tool.buildDAO("userGroup","com.ccniit.bookshop.db.oRMapping");
//			tool.buildDAOTest("userGroup");
	}
}
