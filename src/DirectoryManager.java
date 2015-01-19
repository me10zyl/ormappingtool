abstract class AbstractDirectoryManager {
	public String rootDirName = "oROutputs";

	public abstract String getEntityDirPath();

	public abstract String getDAODirPath();

	public abstract String getTestDirPath();

	public abstract String getPackageNameDBMain(String parentPackageName);

	public abstract String getPackageNameDAO(String parentPackageName, String tableName);

	public abstract String getPackageNameTest(String parentPackageName, String tableName);

	public abstract String getPackageNameEntity(String parentPackageName, String tableName);
}

public class DirectoryManager extends AbstractDirectoryManager {
	private static String rootDirName = "oROutputs";
	private String tableName;

	public DirectoryManager() {
		// TODO Auto-generated constructor stub
	}

	public DirectoryManager(String tableName) {
		// TODO Auto-generated constructor stub
		this.tableName = tableName;
	}

	public static String getClassDAODAOTestPathDir(String tableName) {
		String str = rootDirName + "/" + getClassDAODAOTestPathRelativeToRootDir(tableName);
		return str;
	}

	public static String getClassDAODAOTestPathRelativeToRootDir(String tableName) {
		return tableName;
	}

	public String getPackageNameDBMain(String parentPackageName) {
		return parentPackageName;
	}

	public String getPackageNameByTableName(String parentPackageName, String tableName) {
		String str = parentPackageName;
		str += ".";
		str += getClassDAODAOTestPathRelativeToRootDir(tableName);
		return str;
	}

	public static String getDBMainPathName() {
		return getRootDirName();
	}

	public static String getRootDirName() {
		return rootDirName;
	}

	@Override
	public String getEntityDirPath() {
		// TODO Auto-generated method stub
		return getClassDAODAOTestPathDir(tableName);
	}

	@Override
	public String getDAODirPath() {
		// TODO Auto-generated method stub
		return getClassDAODAOTestPathDir(tableName);
	}

	@Override
	public String getTestDirPath() {
		// TODO Auto-generated method stub
		return getClassDAODAOTestPathDir(tableName);
	}

	@Override
	public String getPackageNameDAO(String parentPackageName, String tableName) {
		// TODO Auto-generated method stub
		return getPackageNameByTableName(parentPackageName, tableName);
	}

	@Override
	public String getPackageNameTest(String parentPackageName, String tableName) {
		// TODO Auto-generated method stub
		return getPackageNameByTableName(parentPackageName, tableName);
	}

	@Override
	public String getPackageNameEntity(String parentPackageName, String tableName) {
		// TODO Auto-generated method stub
		return getPackageNameByTableName(parentPackageName, tableName);
	}
}

class DirectorManager2 extends AbstractDirectoryManager {

	@Override
	public String getEntityDirPath() {
		// TODO Auto-generated method stub
		return rootDirName + "/entity";
	}

	@Override
	public String getDAODirPath() {
		// TODO Auto-generated method stub
		return rootDirName + "/dao";
	}

	@Override
	public String getTestDirPath() {
		// TODO Auto-generated method stub
		return rootDirName + "/test";
	}

	@Override
	public String getPackageNameDBMain(String parentPackageName) {
		// TODO Auto-generated method stub
		return parentPackageName;
	}

	@Override
	public String getPackageNameDAO(String parentPackageName, String tableName) {
		// TODO Auto-generated method stub
		return parentPackageName+".dao";
	}

	@Override
	public String getPackageNameTest(String parentPackageName, String tableName) {
		// TODO Auto-generated method stub
		return parentPackageName+".test";
	}

	@Override
	public String getPackageNameEntity(String parentPackageName, String tableName) {
		// TODO Auto-generated method stub
		return parentPackageName+".entity";
	}

}
