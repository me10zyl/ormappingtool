import java.io.IOException;
import java.sql.SQLException;


public class ConsoleMain {
	public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
		BuildTool tool = new BuildTool("100.1.0.46","1433","bookshop_case","sa","sa");
//			tool.buildClass("userGroup");
//			tool.buildDBMain();
//			tool.buildDAO("userGroup");
//			tool.buildDAOTest("users");
	}
}
