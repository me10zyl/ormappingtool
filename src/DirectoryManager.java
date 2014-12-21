
public class DirectoryManager
{
	private static String rootDirName = "oROutputs";
	public static String getClassDAODAOTestPathDir(String tableName)
	{
		String str = rootDirName + "/" + getClassDAODAOTestPathRelativeToRootDir(tableName);
		return str;
	}
	public static String getClassDAODAOTestPathFile(String tableName,String className)
	{
		return getClassDAODAOTestPathDir(tableName) + "/" + className + ".java";
	}
	public static String getClassDAODAOTestPathRelativeToRootDir(String tableName)
	{
		return tableName;
	}
	public static String getPackageNameDBMain(String parentPackageName)
	{
		return parentPackageName;
	}
	public static String getPackageNameByTableName(String parentPackageName,String tableName)
	{
		String str = parentPackageName;
		str += ".";
		str += getClassDAODAOTestPathRelativeToRootDir(tableName);
		return str;
	}
	public static String getDBMainPathName()
	{
		return getRootDirName();
	}
	public static String getRootDirName()
	{
		return rootDirName;
	}
}
