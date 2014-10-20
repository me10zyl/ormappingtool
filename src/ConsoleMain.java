import java.io.IOException;
import java.sql.SQLException;


public class ConsoleMain {
	public static void main(String[] args) {
		BuildTool tool = new BuildTool("100.1.0.46","1433","bookshop_case","sa","sa");
		try {
//			tool.buildClass("userGroup");
			tool.buildDBMain();
			tool.buildDAO("userGroup");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
