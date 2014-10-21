import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

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

		@Override
		public String toString() {
			return "Field [type=" + type + ", name=" + name + "]";
		}
	}

	private class Method {
		private String returnType;
		private String methodName;
		private ArrayList<Field> arr_field = new ArrayList<BuildTool.Field>();

		public Method(String returnType, String methodName) {
			this.returnType = returnType;
			this.methodName = methodName;
		}

		public Method(String returnType, String methodName, ArrayList<Field> arr_field) {
			this.returnType = returnType;
			this.methodName = methodName;
			this.arr_field = arr_field;
		}

		@Override
		public String toString() {
			return "Method [returnType=" + returnType + ", methodName=" + methodName + ", arr_field=" + arr_field + "]";
		}
	}

	private class Class_ {
		String className;
		ArrayList<Field> arr_field;
		ArrayList<Method> arr_method;

		public Class_(String className, ArrayList<Field> arr_field, ArrayList<Method> arr_method) {
			super();
			this.className = className;
			this.arr_field = arr_field;
			this.arr_method = arr_method;
		}

		@Override
		public String toString() {
			return "Class_ [className=" + className + ", arr_field=" + arr_field + ", arr_method=" + arr_method + "]";
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
	
	public ArrayList<String> getDatabases() throws SQLException, ClassNotFoundException
	{
		ArrayList<String> arr = new ArrayList<String>();
		pst = getPreparedStatement("SELECT Name FROM Master..SysDatabases ORDER BY Name");
		rst = pst.executeQuery();
		while(rst.next())
		{
			arr.add(rst.getString(1));
		}
		return arr;
	}

	private ArrayList<Field> getField(String tableName) throws SQLException, ClassNotFoundException {
		ArrayList<Field> arr = new ArrayList<BuildTool.Field>();
		pst = getPreparedStatement("select * from " + tableName);
		rst = pst.executeQuery();
		ResultSetMetaData data = rst.getMetaData();
		int columnCount = data.getColumnCount();
		String className = makeStrFirstUpper(tableName);
		for (int i = 1; i <= columnCount; i++) {
			String reallyColumnClassName = makeClassNameBeautiful(data.getColumnClassName(i));
			String columnName = data.getColumnName(i);
			arr.add(new Field(reallyColumnClassName, columnName));
		}
		return arr;
	}

	private ArrayList<Method> getMethod(String tableName) throws SQLException, ClassNotFoundException, IOException {
		ArrayList<Method> arr_method = new ArrayList<BuildTool.Method>();
		ArrayList<Field> arr_field = getField(tableName);
		for (int i = 0; i < arr_field.size(); i++) {// 打印方法
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(baos));
			String field_name = arr_field.get(i).name;
			String field_type = arr_field.get(i).type;
			String return_type = "void";
			String method_name = null;
			ArrayList<Field> arr_methodfield = new ArrayList<BuildTool.Field>();
			String get = "get";
			// set
			arr_methodfield.add(new Field(field_type, field_name));
			method_name = "set" + makeStrFirstUpper(field_name);
			arr_method.add(new Method(return_type, method_name, arr_methodfield));
			// is
			String makeStrFirstUpper = makeStrFirstUpper(field_name);
			if (field_type.equals("Boolean")) {
				get = "is";
				if (makeStrFirstUpper.startsWith("Is")) {
					makeStrFirstUpper = removeIs(makeStrFirstUpper);
				}
			}
			// get
			return_type = field_type;
			method_name = get + makeStrFirstUpper;
			arr_method.add(new Method(return_type, method_name));
			bw.close();
		}
		return arr_method;
	}

	private Class_ getClass(String tableName) throws SQLException, ClassNotFoundException, IOException {
		String className = makeStrFirstUpper(tableName);
		Class_ class_ = new Class_(className, getField(tableName), getMethod(tableName));
		return class_;
	}

	public String buildDAO(String tableName) throws IOException, SQLException, ClassNotFoundException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(baos));
		Class_ class_ = getClass(tableName);
		String class_name = class_.className;
		ArrayList<Field> arr_field = class_.arr_field;
		ArrayList<Method> arr_method = class_.arr_method;
		ArrayList<Method> arr_method_get = new ArrayList<BuildTool.Method>();
		ArrayList<Method> arr_method_set = new ArrayList<BuildTool.Method>();
		for (Method m : arr_method) {
			if (m.methodName.startsWith("get")) {
				arr_method_get.add(m);
			} else if (m.methodName.startsWith("set"))
				arr_method_set.add(m);
		}

		bw.write("public class " + class_name + "DAO extends DBMain<" + class_name + ">");
		bw.newLine();
		bw.write("{");
		bw.newLine();

		// add
		bw.write("\tpublic void add(" + class_name + " " + tableName + ") throws ClassNotFoundException, SQLException");
		bw.newLine();
		bw.write("\t{");
		bw.newLine();
		String sql = "\t\tString sql = \"insert into " + tableName + "(";
		for (int i = 1; i < arr_field.size(); i++) {
			Field f = arr_field.get(i);
			sql += f.name + ",";
		}
		sql = sql.substring(0, sql.length() - 1);
		sql += ") values (";
		for (int i = 1; i < arr_field.size(); i++) {
			sql += "?,";
		}
		sql = sql.substring(0, sql.length() - 1);
		sql += ")\";";
		bw.write(sql);
		bw.newLine();
		bw.write("\t\tpst = this.getPreparedStatement(sql);");
		bw.newLine();
		for (int i = 1; i < arr_method_get.size(); i++) {
			Method method = arr_method_get.get(i);
			bw.write("\t\tpst.set" + makeStrFirstUpper(method.returnType) + "(" + (i) + ", " + tableName + "." + method.methodName + "());");
			bw.newLine();
		}
		bw.write("\t\tpst.executeUpdate();");
		bw.newLine();
		bw.write("\t\trealese();");
		bw.newLine();
		bw.write("\t}");
		bw.newLine();
		bw.newLine();

		// delete
		bw.write("\tpublic void delete(int id) throws ClassNotFoundException, SQLException");
		bw.newLine();
		bw.write("\t{");
		bw.newLine();
		bw.write("\t\tString sql = \"delete from users where " + arr_field.get(0).name + "=?\";");
		bw.newLine();
		bw.write("\t\tpst = this.getPreparedStatement(sql);");
		bw.newLine();
		bw.write("\t\tpst.setInt(1, id);");
		bw.newLine();
		bw.write("\t\tpst.executeUpdate();");
		bw.newLine();
		bw.write("\t\trealese();");
		bw.newLine();
		bw.write("\t}");
		bw.newLine();
		bw.newLine();

		// modify
		String newUser = "new" + class_name;
		bw.write("\tpublic void modify(" + class_name + " " + newUser + ") throws ClassNotFoundException, SQLException");
		bw.newLine();
		bw.write("\t{");
		bw.newLine();
		String sql2 = "\t\tString sql = \"update from " + tableName + " set ";
		for (int i = 1; i < arr_field.size(); i++) {
			Field f = arr_field.get(i);
			sql2 += f.name + "=?,";
		}
		sql2 = sql2.substring(0, sql2.length() - 1);
		sql2 += "\";";
		bw.write(sql2);
		bw.newLine();
		for (int i = 1; i < arr_method_get.size(); i++) {
			Method method = arr_method_get.get(i);
			bw.write("\t\tpst.set" + makeStrFirstUpper(method.returnType) + "(" + (i) + ", " + tableName + "." + method.methodName + "());");
			bw.newLine();
		}
		bw.write("\t\tpst.set" + makeStrFirstUpper(arr_method_get.get(0).returnType) + "(" + (arr_method_get.size()) + ", " + tableName + "." + arr_method_get.get(0).methodName + "());");
		bw.newLine();
		bw.write("\t\tpst.executeUpdate();");
		bw.newLine();
		bw.write("\t\trealese();");
		bw.newLine();
		bw.write("\t}");
		bw.newLine();
		bw.newLine();

		// getAll
		String arr_name = tableName;
		if (!arr_name.endsWith("s")) {
			arr_name += "s";
		}
		bw.write("\tpublic ArrayList<" + class_name + "> getAll() throws ClassNotFoundException, SQLException");
		bw.newLine();
		bw.write("\t{");
		bw.newLine();
		bw.write("\t\tString sql = \"select * from " + arr_name + "\";");
		bw.newLine();
		bw.write("\t\tpst = this.getPreparedStatement(sql);");
		bw.newLine();
		bw.write("\t\trst = pst.executeQuery();");
		bw.newLine();
		bw.write("\t\tArrayList<" + class_name + "> " + arr_name + " = new ArrayList<" + class_name + ">();");
		bw.newLine();
		bw.write("\t\twhile (rst.next())");
		bw.newLine();
		bw.write("\t\t{");
		bw.newLine();
		bw.write("\t\t\t" + arr_name + ".add(assemble(rst));");
		bw.newLine();
		bw.write("\t\t}");
		bw.newLine();
		bw.write("\t\trealese();");
		bw.newLine();
		bw.write("\t\treturn " + arr_name + ";");
		bw.newLine();
		bw.write("\t}");
		bw.newLine();
		bw.newLine();

		// getById
		bw.write("\tpublic " + class_name + " getById(int id) throws ClassNotFoundException, SQLException");
		bw.newLine();
		bw.write("\t{");
		bw.newLine();
		bw.write("\t\tString sql = \"select * from " + arr_name + " where " + arr_field.get(0).name + "=?\";");
		bw.newLine();
		bw.write("\t\tpst = this.getPreparedStatement(sql);");
		bw.newLine();
		bw.write("\t\tpst.set" + makeStrFirstUpper(arr_field.get(0).type) + "(1, id);");
		bw.newLine();
		bw.write("\t\trst = pst.executeQuery();");
		bw.newLine();
		bw.write("\t\t" + class_name + " " + tableName + " = null;");
		bw.newLine();
		bw.write("\t\tif (rst.next())");
		bw.newLine();
		bw.write("\t\t{");
		bw.newLine();
		bw.write("\t\t\t" + arr_name + ".add(assemble(rst));");
		bw.newLine();
		bw.write("\t\t}");
		bw.newLine();
		bw.write("\t\trelease();");
		bw.newLine();
		bw.write("\t}");
		bw.newLine();
		bw.newLine();

		// assemble
		bw.write("\tpublic " + class_name + " assemble(ResultSet rst) throws SQLException");
		bw.newLine();
		bw.write("\t{");
		bw.newLine();
		String users_str = "\t\t" + class_name + " " + tableName + " = new " + class_name + "(";
		for (int i = 0; i < arr_field.size() - 1; i++) {
			users_str += "rst.get" + makeStrFirstUpper(arr_field.get(i).type) + "(\"" + arr_field.get(i).name + "\"), ";
		}
		users_str += "rst.get" + makeStrFirstUpper(arr_field.get(arr_field.size() - 1).type) + "(\"" + arr_field.get(arr_field.size() - 1).name + "\"));";
		bw.write(users_str);
		bw.newLine();
		bw.write("\t\treturn " + tableName + ";");
		bw.newLine();
		bw.write("\t}");
		bw.newLine();
		bw.write("}");

		realese();
		bw.close();
		baos.close();
		baos.writeTo(System.out);
		return baos.toString();
	}

	public String buildDBMain() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(baos));
		FileInputStream fis = new FileInputStream("DBMain");
		byte buffer[] = new byte[1024];
		int len = 0;
		while ((len = fis.read(buffer)) != -1) {
			bw.write(new String(buffer,0,len));
		}
		baos.close();
		bw.close();
		fis.close();
		baos.writeTo(System.out);
		return baos.toString();
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
