import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class BuildTool extends DBMain {
	private String host;
	private String port;
	private String dataBaseName;
	private String username;
	private String password;

	public BuildTool(String host, String port, String dataBaseName, String username, String password) {
		super();
		this.host = host;
		this.port = port;
		this.dataBaseName = dataBaseName;
		this.username = username;
		this.password = password;
	}

	private class Field {
		private String type;
		private String name;

		public Field(String type, String name) {
			this.type = type;
			this.name = name;
		}
	}

	public void build(String tableName) throws ClassNotFoundException, SQLException {
		pst = getPreparedStatement("select * from " + tableName);
		rst = pst.executeQuery();
		ResultSetMetaData data = rst.getMetaData();
		int columnCount = data.getColumnCount();
		String className = makeStrFirstUpper(tableName);
		ArrayList<Field> arr = new ArrayList<BuildTool.Field>();

		System.out.println("public class " + className + "{");
		for (int i = 1; i <= columnCount; i++) {// 打印成员变量
			String columnClassName = data.getColumnClassName(i);
			String reallyColumnClassName = makeClassNameBeautiful(columnClassName);
			String columnName = data.getColumnName(i);
			arr.add(new Field(reallyColumnClassName, columnName));
			System.out.print("\tprivate " + reallyColumnClassName + " ");
			System.out.println(columnName + ";");
		}
		System.out.println();

		System.out.print("\tpublic " + className + "(");
		for (int i = 0; i < arr.size() - 1; i++) {// 打印构造方法
			System.out.print(arr.get(i).type + " " + arr.get(i).name + ",");
		}
		System.out.println(arr.get(arr.size() - 1).type + " " + arr.get(arr.size() - 1).name + ")");
		System.out.println("\t{");
		for (int i = 0; i < arr.size(); i++) {
			System.out.println("\t\tthis." + arr.get(i).name + " = " + arr.get(i).name + ";");
		}
		System.out.println("\t}");
		System.out.println();

		for (int i = 1; i <= columnCount; i++) {// 打印方法
			String columnClassName = data.getColumnClassName(i);
			String columnName = data.getColumnName(i);
			String reallyColumnClassName = makeClassNameBeautiful(columnClassName);
			String get = "get";
			// set
			System.out.println("\tpublic void " + "set" + makeStrFirstUpper(columnName) + "(" + reallyColumnClassName + " " + columnName + ")");
			System.out.println("\t{");
			System.out.println("\t\tthis." + columnName + " = " + columnName + ";");
			System.out.println("\t}");
			System.out.println();
			// is
			String makeStrFirstUpper = makeStrFirstUpper(columnName);
			if (reallyColumnClassName.equals("Boolean")) {
				get = "is";
				if (makeStrFirstUpper.startsWith("Is")) {
					makeStrFirstUpper = removeIs(makeStrFirstUpper);
				}
			}
			// get
			System.out.println("\tpublic " + reallyColumnClassName + " " + get + makeStrFirstUpper + "()");
			System.out.println("\t{");
			System.out.println("\t\treturn " + columnName + ";");
			System.out.println("\t}");
			if (i != columnCount)
				System.out.println();
		}
		System.out.println("}");
		realese();
	}

	private String removeIs(String str) {
		return str.substring(2, str.length());
	}

	private String makeClassNameBeautiful(String columnClassName) {
		return makeWrapperClassToPreClass(removePackageName(columnClassName));
	}

	private String makeStrFirstUpper(String str) {
		String classNameFirst = (str.charAt(0) + "").toUpperCase();
		return classNameFirst + str.substring(1, str.length());
	}

	private String removePackageName(String str) {
		String[] strs = str.split("\\.");
		return strs[strs.length - 1];
	}

	private String makeWrapperClassToPreClass(String str) {
		if (str.equals("Integer")) {
			return "int";
		} else if (str.equals("Boolean")) {
			return "bool";
		}
		return str;
	}

	@Override
	protected PreparedStatement getPreparedStatement(String sql) throws ClassNotFoundException, SQLException {
		// ------加载数据库驱动---------------------
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		// ------获得数据库连接----------------------
		con = DriverManager.getConnection("jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + dataBaseName, username, password);
		// -------封装SQL语句---------------------
		// String sql = "select * from users";
		pst = con.prepareStatement(sql);
		return pst;
	}
}
