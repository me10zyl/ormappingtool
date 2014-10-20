import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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

	public ArrayList<String> getTables() throws SQLException {
		ArrayList<String> arr = new ArrayList<String>();
		con = DriverManager.getConnection("jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + dataBaseName, username, password);
		ResultSet rs = con.getMetaData().getTables(null, null, null, new String[] { "TABLE" });
		while (rs.next()) {
			arr.add(rs.getString(3));
		}
		return arr;
	}

	public String buildClass(String tableName) throws ClassNotFoundException, SQLException, IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(baos));

		pst = getPreparedStatement("select * from " + tableName);
		rst = pst.executeQuery();
		ResultSetMetaData data = rst.getMetaData();
		int columnCount = data.getColumnCount();
		String className = makeStrFirstUpper(tableName);
		ArrayList<Field> arr_field = new ArrayList<BuildTool.Field>();

		bw.write("public class " + className);
		bw.newLine();
		bw.write("{");
		bw.newLine();
		for (int i = 1; i <= columnCount; i++) {// 打印成员变量
			String columnClassName = data.getColumnClassName(i);
			String reallyColumnClassName = makeClassNameBeautiful(columnClassName);
			String columnName = data.getColumnName(i);
			arr_field.add(new Field(reallyColumnClassName, columnName));
			bw.write("\tprivate " + reallyColumnClassName + " ");
			bw.write(columnName + ";");
			bw.newLine();
		}
		bw.newLine();
		
		bw.write("\tpublic " + className + "(");
		for (int i = 0; i < arr_field.size() - 1; i++) {// 打印构造方法
			bw.write(arr_field.get(i).type + " " + arr_field.get(i).name + ",");
		}
		bw.write(arr_field.get(arr_field.size() - 1).type + " " + arr_field.get(arr_field.size() - 1).name + ")");
		bw.newLine();
		bw.write("\t{");
		bw.newLine();
		for (int i = 0; i < arr_field.size(); i++) {
			bw.write("\t\tthis." + arr_field.get(i).name + " = " + arr_field.get(i).name + ";");
			bw.newLine();
		}
		bw.write("\t}");
		bw.newLine();
		bw.newLine();
		
		for (int i = 1; i <= columnCount; i++) {// 打印方法
			String columnClassName = data.getColumnClassName(i);
			String columnName = data.getColumnName(i);
			String reallyColumnClassName = makeClassNameBeautiful(columnClassName);
			String get = "get";
			// set
			bw.write("\tpublic void " + "set" + makeStrFirstUpper(columnName) + "(" + reallyColumnClassName + " " + columnName + ")");
			bw.newLine();
			bw.write("\t{");
			bw.newLine();
			bw.write("\t\treturn " + columnName + ";");
			bw.newLine();
			bw.write("\t}");
			bw.newLine();
			bw.newLine();
			// is
			String makeStrFirstUpper = makeStrFirstUpper(columnName);
			if (reallyColumnClassName.equals("Boolean")) {
				get = "is";
				if (makeStrFirstUpper.startsWith("Is")) {
					makeStrFirstUpper = removeIs(makeStrFirstUpper);
				}
			}
			// get
			bw.write("\tpublic " + reallyColumnClassName + " " + get + makeStrFirstUpper + "()");
			bw.newLine();
			bw.write("\t{");
			bw.newLine();
			bw.write("\t\treturn " + columnName + ";");
			bw.newLine();
			bw.write("\t}");
			bw.newLine();
			bw.newLine();
		}

		String methodName = "\tpublic String toString()";// toString
		bw.write(methodName);
		bw.newLine();
		bw.write("\t" + "{");
		bw.newLine();
		bw.write("\t\treturn ");
		for (int i = 0; i < arr_field.size() - 1; i++) {
			bw.write(arr_field.get(i).name + "+\"\\t\"+");
		}
		bw.write(arr_field.get(arr_field.size() - 1).name + ";");
		bw.newLine();
		bw.write("\t}");
		bw.newLine();

		bw.write("}");
		bw.newLine();
		realese();
		bw.close();
		baos.close();
		baos.writeTo(System.out);
		return baos.toString();
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
