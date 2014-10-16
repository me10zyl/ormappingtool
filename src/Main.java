import java.sql.SQLException;


public class Main {
	public static void main(String[] args) {
		BuildTool tool = new BuildTool("100.1.0.46","1433","bookshop_case","sa","sa");
		try {
			tool.build("users");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
