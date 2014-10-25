import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class BuildTool extends DBMain
{
	private String host;
	private String port;
	private String dataBaseName;
	private String username;
	private String password;

	public BuildTool(String host, String port, String dataBaseName, String username, String password)
	{
		super();
		this.host = host;
		this.port = port;
		this.dataBaseName = dataBaseName;
		this.username = username;
		this.password = password;
	}

	private class Field
	{
		private String type;
		private String name;

		public Field(String type, String name)
		{
			this.type = type;
			this.name = name;
		}
		@Override
		public String toString()
		{
			return "Field [type=" + type + ", name=" + name + "]";
		}
	}

	private class Method
	{
		private String returnType;
		private String methodName;
		private ArrayList<Field> arr_field = new ArrayList<BuildTool.Field>();

		public Method(String returnType, String methodName)
		{
			this.returnType = returnType;
			this.methodName = methodName;
		}
		public Method(String returnType, String methodName, ArrayList<Field> arr_field)
		{
			this.returnType = returnType;
			this.methodName = methodName;
			this.arr_field = arr_field;
		}
		@Override
		public String toString()
		{
			return "Method [returnType=" + returnType + ", methodName=" + methodName + ", arr_field=" + arr_field + "]";
		}
	}

	private class Class_
	{
		String className;
		ArrayList<Field> arr_field;
		ArrayList<Method> arr_method;

		public Class_(String className, ArrayList<Field> arr_field, ArrayList<Method> arr_method)
		{
			super();
			this.className = className;
			this.arr_field = arr_field;
			this.arr_method = arr_method;
		}
		@Override
		public String toString()
		{
			return "Class_ [className=" + className + ", arr_field=" + arr_field + ", arr_method=" + arr_method + "]";
		}
	}

	public ArrayList<String> getTables() throws SQLException
	{
		ArrayList<String> arr = new ArrayList<String>();
		con = DriverManager.getConnection("jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + dataBaseName, username, password);
		ResultSet rs = con.getMetaData().getTables(null, null, null, new String[]
		{ "TABLE" });
		while (rs.next())
		{
			arr.add(rs.getString(3));
		}
		return arr;
	}
	public ArrayList<String> getDatabases() throws SQLException, ClassNotFoundException
	{
		ArrayList<String> arr = new ArrayList<String>();
		// pst =
		// getPreparedStatement("SELECT Name FROM Master..SysDatabases ORDER BY Name");
		// rst = pst.executeQuery();
		con = DriverManager.getConnection("jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + dataBaseName, username, password);
		DatabaseMetaData metaData = con.getMetaData();
		rst = metaData.getCatalogs();
		while (rst.next())
		{
			arr.add(rst.getString(1));
		}
		return arr;
	}
	private ArrayList<String> getImportKeysTableNames(String tableName) throws SQLException
	{
		ArrayList<String> arr = new ArrayList<String>();
		con = DriverManager.getConnection("jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + dataBaseName, username, password);
		DatabaseMetaData metaData = con.getMetaData();
		ResultSet importKeys = metaData.getImportedKeys(con.getCatalog(), null, tableName);//获取结果集后将自动关闭连接
		while (importKeys.next())
		{
			String fkColumnName = importKeys.getString("FKCOLUMN_NAME");
			String pkTablenName = importKeys.getString("PKTABLE_NAME");
			String pkColumnName = importKeys.getString("PKCOLUMN_NAME");
			arr.add(pkTablenName);
		}
		realese(); 
		return arr;
	}
	private ArrayList<String> getImportKeysFKColumnNames(String tableName) throws SQLException
	{
		ArrayList<String> arr = new ArrayList<String>();
		con = DriverManager.getConnection("jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + dataBaseName, username, password);
		DatabaseMetaData metaData = con.getMetaData();
		ResultSet importKeys = metaData.getImportedKeys(con.getCatalog(), null, tableName);//获取结果集后将自动关闭连接
		while (importKeys.next())
		{
			String fkColumnName = importKeys.getString("FKCOLUMN_NAME");
			String pkTablenName = importKeys.getString("PKTABLE_NAME");
			String pkColumnName = importKeys.getString("PKCOLUMN_NAME");
			arr.add(fkColumnName);
		}
		realese(); 
		return arr;
	}
	private ArrayList<String> getExportKeysTableNames(String tableName) throws SQLException
	{
		ArrayList<String> arr = new ArrayList<String>();
		con = DriverManager.getConnection("jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + dataBaseName, username, password);
		DatabaseMetaData metaData = con.getMetaData();
		ResultSet exportedKeys = metaData.getExportedKeys(con.getCatalog(), null, tableName);
		while (exportedKeys.next())
		{
			String fkTableName = exportedKeys.getString("FKTABLE_NAME");
			String fkColumnName = exportedKeys.getString("FKCOLUMN_NAME");
			String pkTablenName = exportedKeys.getString("PKTABLE_NAME");
			String pkColumnName = exportedKeys.getString("PKCOLUMN_NAME");
			arr.add(fkTableName);
		}
		realese();
		return arr;
	}
	private ArrayList<Field> getImportKeysFields(String tableName) throws SQLException
	{
		ArrayList<Field> arr_field = new ArrayList<BuildTool.Field>();
		ArrayList<String> arr = getImportKeysTableNames(tableName);
		for(String str : arr)
		{
			arr_field.add(new Field(getClassName(str), getVariableName(str)));
		}
		return arr_field;
	}
	private ArrayList<Field> getExportKeysFields(String tableName) throws SQLException
	{
		ArrayList<Field> arr_field = new ArrayList<BuildTool.Field>();
		ArrayList<String> arr = getExportKeysTableNames(tableName);
		for(String str : arr)
		{
			arr_field.add(new Field(getClassName(str), getVariableName(str)));
		}
		return arr_field;
	}
	private ArrayList<Field> getField(String tableName) throws SQLException, ClassNotFoundException
	{
		ArrayList<Field> arr = new ArrayList<BuildTool.Field>();
		pst = getPreparedStatement("select * from " + tableName);
		rst = pst.executeQuery();
		ResultSetMetaData data = rst.getMetaData();
		int columnCount = data.getColumnCount();
		String className = makeStrFirstUpper(tableName);
		for (int i = 1; i <= columnCount; i++)
		{
			String reallyColumnClassName = makeClassNameBeautiful(data.getColumnClassName(i));
			String columnName = data.getColumnName(i);
			arr.add(new Field(reallyColumnClassName, columnName));
		}
		return arr;
	}
	private ArrayList<Method> getMethod(String tableName) throws SQLException, ClassNotFoundException, IOException
	{
		ArrayList<Method> arr_method = new ArrayList<BuildTool.Method>();
		ArrayList<Field> arr_field = getField(tableName);
		for (int i = 0; i < arr_field.size(); i++)
		{// 打印方法
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
			if (field_type.equals("Boolean"))
			{
				get = "is";
				if (makeStrFirstUpper.startsWith("Is"))
				{
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
	private Class_ getClass(String tableName) throws SQLException, ClassNotFoundException, IOException
	{
		String className = makeStrFirstUpper(tableName);
		Class_ class_ = new Class_(className, getField(tableName), getMethod(tableName));
		return class_;
	}
	public String buildDAO(String tableName) throws IOException, SQLException, ClassNotFoundException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(baos));
		Class_ class_ = getClass(tableName);
		String class_name = class_.className;
		ArrayList<Field> arr_field = class_.arr_field;
		ArrayList<Method> arr_method = class_.arr_method;
		ArrayList<Method> arr_method_get = new ArrayList<BuildTool.Method>();
		ArrayList<Method> arr_method_set = new ArrayList<BuildTool.Method>();
		for (Method m : arr_method)
		{
			if (m.methodName.startsWith("get"))
			{
				arr_method_get.add(m);
			} else if (m.methodName.startsWith("set"))
				arr_method_set.add(m);
		}
		bw.write("import java.sql.ResultSet;");
		bw.newLine();
		bw.write("import java.sql.SQLException;");
		bw.newLine();
		bw.write("import java.util.ArrayList;");
		bw.newLine();
		bw.newLine();
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
		for (int i = 1; i < arr_field.size(); i++)
		{
			Field f = arr_field.get(i);
			sql += f.name + ",";
		}
		sql = sql.substring(0, sql.length() - 1);
		sql += ") values (";
		for (int i = 1; i < arr_field.size(); i++)
		{
			sql += "?,";
		}
		sql = sql.substring(0, sql.length() - 1);
		sql += ")\";";
		bw.write(sql);
		bw.newLine();
		bw.write("\t\tpst = this.getPreparedStatement(sql);");
		bw.newLine();
		for (int i = 1; i < arr_method_get.size(); i++)
		{
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
		String sql2 = "\t\tString sql = \"update " + tableName + " set ";
		for (int i = 1; i < arr_field.size(); i++)
		{
			Field f = arr_field.get(i);
			sql2 += f.name + "=?,";
		}
		sql2 = sql2.substring(0, sql2.length() - 1);
		sql2 += " where " + arr_field.get(0).name + "=?";
		sql2 += "\";";
		bw.write(sql2);
		bw.newLine();
		bw.write("pst = this.getPreparedStatement(sql);");
		bw.newLine();
		for (int i = 1; i < arr_method_get.size(); i++)
		{
			Method method = arr_method_get.get(i);
			bw.write("\t\tpst.set" + makeStrFirstUpper(method.returnType) + "(" + (i) + ", " + newUser + "." + method.methodName + "());");
			bw.newLine();
		}
		bw.write("\t\tpst.set" + makeStrFirstUpper(arr_method_get.get(0).returnType) + "(" + (arr_method_get.size()) + ", " + newUser + "." + arr_method_get.get(0).methodName + "());");
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
		if (!arr_name.endsWith("s"))
		{
			arr_name += "s";
		}
		bw.write("\tpublic ArrayList<" + class_name + "> getAll() throws ClassNotFoundException, SQLException");
		bw.newLine();
		bw.write("\t{");
		bw.newLine();
		bw.write("\t\tString sql = \"select * from " + tableName + "\";");
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
		bw.write("\t\tString sql = \"select * from " + tableName + " where " + arr_field.get(0).name + "=?\";");
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
		bw.write("\t\t\t" + tableName + "= assemble(rst);");
		bw.newLine();
		bw.write("\t\t}");
		bw.newLine();
		bw.write("\t\trealese();");
		bw.newLine();
		bw.write("\t\treturn " + tableName + ";");
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
		for (int i = 0; i < arr_field.size() - 1; i++)
		{
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
	private void printGetDetailById(ByteArrayOutputStream baos,ArrayList<Field> arr_field)
	{
		String sql = "select * from <table1>,<table2> where <table1>.<importKey_columnName> = <table2>.<importKey_columnName> and <table1>.<table1_id> = ?";
		printImportKeysTable(baos,arr_field);
	}
	private void printImportKeysTable(ByteArrayOutputStream baos,ArrayList<Field> arr_field)
	{
		Field field1 = arr_field.get(0);
		// TODO Auto-generated method stub
		
	}
	public String buildDBMain() throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(baos));
		InputStream fis = this.getClass().getClassLoader().getResourceAsStream("DBMain");
		byte buffer[] = new byte[1024];
		int len = 0;
		while ((len = fis.read(buffer)) != -1)
		{
			bw.write(new String(buffer, 0, len));
		}
		baos.close();
		bw.close();
		fis.close();
		baos.writeTo(System.out);
		return baos.toString();
	}
	public String buildDAOTest(String tableName) throws IOException, SQLException, ClassNotFoundException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(baos));
		InputStream fis = this.getClass().getClassLoader().getResourceAsStream("DAOTest");
		ArrayList<Method> arr_method = getMethod(tableName);
		ArrayList<Field> arr_field = getField(tableName);
		byte buffer[] = new byte[1024];
		int len = 0;
		while ((len = fis.read(buffer)) != -1)
		{
			bw.write(new String(buffer, 0, len));
		}
		baos.close();
		bw.close();
		fis.close();
		String replaceStr = baos.toString();
		replaceStr = replaceStr.replace("<class>", getClassName(tableName));
		replaceStr = replaceStr.replace("<lower_class>", getVariableName(tableName));
		String constructor = "";
		for (int i = 0; i < arr_field.size(); i++)
		{
			if (arr_field.get(i).type.equals("int") || arr_field.get(i).type.equals("double"))
			{
				constructor += "1";
			} else if (arr_field.get(i).type.equals("boolean"))
			{
				constructor += "true";
			} else
			{
				constructor += "\"" + arr_field.get(i).name + "\"";
			}
			if (i != (arr_field.size() - 1))
			{
				constructor += ",";
			}
		}
		replaceStr = replaceStr.replace("<constructor>", constructor);
		String set = "";
		ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
		BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(baos2));
		for (int i = 1; i < arr_field.size(); i++)
		{
			String var = "";
			if (arr_field.get(i).type.equals("int") || arr_field.get(i).type.equals("double"))
			{
				var = "1";
			} else if (arr_field.get(i).type.equals("boolean"))
			{
				var = "true";
			} else
			{
				var = "\"" + arr_field.get(i).name + "\"";
			}
			String tab = "";
			if (i != 1)
			{
				tab = "\t\t";
			} else
			{
				tab = "";
			}
			bw2.write(tab + getVariableName(tableName) + ".set" + makeStrFirstUpper(arr_field.get(i).name) + "(" + var + ");");
			bw2.newLine();
		}
		baos2.close();
		bw2.close();
		set = baos2.toString();
		replaceStr = replaceStr.replace("<set>", set);
		System.out.println(replaceStr);
		return replaceStr;
	}
	public String buildClass(String tableName) throws ClassNotFoundException, SQLException, IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(baos));
		pst = getPreparedStatement("select * from " + tableName);
		rst = pst.executeQuery();
		ResultSetMetaData data = rst.getMetaData();
		int columnCount = data.getColumnCount();
		String className = makeStrFirstUpper(tableName);
		ArrayList<Field> arr_field = new ArrayList<BuildTool.Field>();
		ArrayList<String> importKeysTableNames = getImportKeysTableNames(tableName);
		ArrayList<String> exportKeysTableNames = getExportKeysTableNames(tableName);
		ArrayList<Field> arr_relationship = new ArrayList<BuildTool.Field>();
		
		bw.write("public class " + className);
		bw.newLine();
		bw.write("{");
		bw.newLine();
		for (int i = 1; i <= columnCount; i++)
		{// 打印成员变量
			String columnClassName = data.getColumnClassName(i);
			String reallyColumnClassName = makeClassNameBeautiful(columnClassName);
			String columnName = data.getColumnName(i);
			arr_field.add(new Field(reallyColumnClassName, columnName));
			bw.write("\tprivate " + reallyColumnClassName + " ");
			bw.write(columnName + ";");
			bw.newLine();
		}
		for(int i = 0;i < importKeysTableNames.size();i++)// 打印导入的键
		{
			bw.write("\tprivate "+getClassName(importKeysTableNames.get(i))+" "+getVariableName(importKeysTableNames.get(i))+";");
			arr_relationship.add(new Field(getClassName(importKeysTableNames.get(i)),getVariableName(importKeysTableNames.get(i))));
			bw.newLine();
		}
		for(int i =0;i < exportKeysTableNames.size();i++)// 打印导出的键
		{
			bw.write("\tprivate ArrayList<"+getClassName(exportKeysTableNames.get(i))+"> "+getVariableName(exportKeysTableNames.get(i))+";");
			arr_relationship.add(new Field("ArrayList<"+getClassName(exportKeysTableNames.get(i))+">",getVariableName(exportKeysTableNames.get(i))));
			bw.newLine();
		}
		bw.newLine();
		
		
		bw.write("\tpublic " + className + "()");// 打印构造方法1
		bw.newLine();
		bw.write("\t{");
		bw.newLine();
		bw.newLine();
		bw.write("\t}");
		bw.newLine();
		bw.newLine();
		bw.write("\tpublic " + className + "(");
		for (int i = 0; i < arr_field.size() - 1; i++)
		{// 打印构造方法2
			bw.write(arr_field.get(i).type + " " + arr_field.get(i).name + ",");
		}
		bw.write(arr_field.get(arr_field.size() - 1).type + " " + arr_field.get(arr_field.size() - 1).name + ")");
		bw.newLine();
		bw.write("\t{");
		bw.newLine();
		for (int i = 0; i < arr_field.size(); i++)
		{
			bw.write("\t\tthis." + arr_field.get(i).name + " = " + arr_field.get(i).name + ";");
			bw.newLine();
		}
		bw.write("\t}");
		bw.newLine();
		bw.newLine();
		
		for (int i = 1; i <= columnCount; i++)
		{// 打印方法
			String columnClassName = data.getColumnClassName(i);
			String columnName = data.getColumnName(i);
			String reallyColumnClassName = makeClassNameBeautiful(columnClassName);
			String get = "get";
			// set
			bw.write("\tpublic void " + "set" + makeStrFirstUpper(columnName) + "(" + reallyColumnClassName + " " + columnName + ")");
			bw.newLine();
			bw.write("\t{");
			bw.newLine();
			bw.write("\t\tthis." + columnName + " = "+columnName+";");
			bw.newLine();
			bw.write("\t}");
			bw.newLine();
			bw.newLine();
			// is
			String makeStrFirstUpper = makeStrFirstUpper(columnName);
			if (reallyColumnClassName.equals("boolean"))
			{
				get = "is";
				if (makeStrFirstUpper.startsWith("Is"))
				{
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
		// 打印关系的GetSet方法
		printGetSet(bw,arr_relationship);
		
		String methodName = "\tpublic String toString()";// toString
		bw.write(methodName);
		bw.newLine();
		bw.write("\t" + "{");
		bw.newLine();
		bw.write("\t\treturn ");
		for (int i = 0; i < arr_field.size(); i++)
		{
			bw.write(arr_field.get(i).name + "+\"\\t\"+");
		}
		for(int i =0; i < arr_relationship.size() - 1;i++)
		{
			String variable = arr_relationship.get(i).name;
			bw.write("(this."+variable+" == null ? \"\" : \"\\t\"+"+variable+") +");
		}
		bw.write("(this."+arr_relationship.get(arr_relationship.size() - 1).name+" == null ? \"\" : \"\\t\"+"+arr_relationship.get(arr_relationship.size() - 1).name+");");
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
	private void printGetSet(BufferedWriter bw,ArrayList<BuildTool.Field> arr) throws IOException
	{
		for(int i =0 ;i < arr.size();i++)
		{
			String type = arr.get(i).type;
			String variable = arr.get(i).name;
			String makeVariableFirstUpper = makeStrFirstUpper(variable);
			String get = "get";
			//set
			bw.write("\tpublic set"+makeVariableFirstUpper+"("+type+" "+variable+")");
			bw.newLine();
			bw.write("\t{");
			bw.newLine();
			bw.write("\t\tthis."+variable+" = "+variable+";");
			bw.newLine();
			bw.write("\t}");
			bw.newLine();
			bw.newLine();
			//is
			if (type.equals("boolean"))
			{
				get = "is";
				if (makeVariableFirstUpper.startsWith("Is"))
				{
					makeVariableFirstUpper = removeIs(makeVariableFirstUpper);
				}
			}
			//get
			bw.write("\tpublic "+get+makeVariableFirstUpper+"()");
			bw.newLine();
			bw.write("\t{");
			bw.newLine();
			bw.write("\t\treturn "+variable+";");
			bw.newLine();
			bw.write("\t}");
			bw.newLine();
			bw.newLine();
			
		}
	}
	private String getClassName(String tableName)
	{
		return makeStrFirstUpper(tableName);
	}
	private String getVariableName(String tableName)
	{
		return makeStrFirstLower(getClassName(tableName));
	}
	private String removeIs(String str)
	{
		return str.substring(2, str.length());
	}
	private String makeClassNameBeautiful(String columnClassName)
	{
		return makeWrapperClassToPreClass(removePackageName(columnClassName));
	}
	private String makeStrFirstUpper(String str)
	{
		String classNameFirst = (str.charAt(0) + "").toUpperCase();
		return classNameFirst + str.substring(1, str.length());
	}
	private String makeStrFirstLower(String str)
	{
		String classNameFirst = (str.charAt(0) + "").toLowerCase();
		return classNameFirst + str.substring(1, str.length());
	}
	private String removePackageName(String str)
	{
		String[] strs = str.split("\\.");
		return strs[strs.length - 1];
	}
	private String makeWrapperClassToPreClass(String str)
	{
		if (str.equals("Integer"))
		{
			return "int";
		} else if (str.equals("Boolean"))
		{
			return "bool";
		}
		return str;
	}
	@Override
	protected PreparedStatement getPreparedStatement(String sql) throws ClassNotFoundException, SQLException
	{
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
