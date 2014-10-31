import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class UIFile extends javax.swing.JDialog
{
	private JFrame parent;
	private JLabel jLabel1;
	private JTextField jtf_packageName;
	private JButton jb_fileall;
	private String host;
	private String port;
	private JButton jb_filesingle;
	private String dataBaseName;
	private String username;
	private String password;
	private String tableName;
	private String dataBaseSoft;
	/**
	 * Auto-generated main method to display this JDialog
	 * @param dataBaseSoft TODO
	 */
	public UIFile(JFrame frame, String host, String port, String dataBaseName, String username, String password, String tableName, String dataBaseSoft)
	{
		super(frame);
		this.parent = frame;
		this.host = host;
		this.port = port;
		this.dataBaseName = dataBaseName;
		this.username = username;
		this.password = password;
		this.tableName = tableName;
		this.dataBaseSoft = dataBaseSoft;
		initGUI();
	}
	private String getClassName(String str)
	{
		String classname = null;
		Pattern p = Pattern.compile("class \\w+");
		Matcher m = p.matcher(str);
		if (m.find())
		{
			classname = m.group().split(" ")[1];
		}
		return classname;
	}
	private void initGUI()
	{
		try
		{
			{
				getContentPane().setLayout(null);
				{
					jLabel1 = new JLabel();
					getContentPane().add(jLabel1);
					jLabel1.setText("\u5305\u540d");
					jLabel1.setBounds(22, 27, 24, 17);
				}
				{
					jtf_packageName = new JTextField();
					getContentPane().add(jtf_packageName);
					jtf_packageName.setBounds(58, 24, 214, 24);
				}
				{
					jb_fileall = new JButton();
					getContentPane().add(jb_fileall);
					jb_fileall.setText("\u751f\u6210\u6240\u6709\u8868");
					jb_fileall.setBounds(171, 60, 96, 31);
					jb_fileall.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{
							{
								// TODO add your
								// code for
								// jb_file.actionPerformed
								UIFile.this.dispose();
								saveProperties();
								int res = JOptionPane.showConfirmDialog(UIFile.this, "将输出「所有」DBMAIN、类、DAO、DAOTest的JAVA文件至本目录下的oROutputs目录", "输出至文件...", JOptionPane.YES_NO_OPTION);
								// 是
								if (res == 0)
								{
									try
									{
										System.out.println("dataBaseSoft:"+dataBaseSoft);
										final BuildTool tool = new BuildTool(host, port, dataBaseName, username, password, dataBaseSoft);
										ArrayList<String> tables = tool.getTables();
										generateDBMain(tool, DirectoryManager.getDBMainPathName());
										for (int j = 0; j < tables.size(); j++)
										{
											String tableName_ = tables.get(j);
											generateJavas(tool, tableName_, DirectoryManager.getClassDAODAOTestPathDir(tableName_));
										}
										JOptionPane.showMessageDialog(UIFile.this, "成功!");
									} catch (ClassNotFoundException e)
									{
										// TODO
										// Auto-generated
										// catch
										// block
										e.printStackTrace();
										JOptionPane.showMessageDialog(null, e.getMessage());
									} catch (SQLException e)
									{
										// TODO
										// Auto-generated
										// catch
										// block
										e.printStackTrace();
										JOptionPane.showMessageDialog(null, e.getMessage());
									} catch (IOException e)
									{
										// TODO
										// Auto-generated
										// catch
										// block
										e.printStackTrace();
										JOptionPane.showMessageDialog(null, e.getMessage());
									}
								}
							}
						}
					});
				}
				{
					jb_filesingle = new JButton();
					getContentPane().add(jb_filesingle);
					jb_filesingle.setText("\u751f\u6210\u5355\u8868");
					jb_filesingle.setBounds(39, 60, 96, 31);
					jb_filesingle.addActionListener(new ActionListener()
					{
						public void actionPerformed(ActionEvent evt)
						{
							{
								// TODO add your
								// code for
								// jb_file.actionPerformed
								UIFile.this.dispose();
								saveProperties();
								int res = JOptionPane.showConfirmDialog(UIFile.this, "将输出DBMAIN、类、DAO、DAOTest的JAVA文件至本目录下的oROutputs目录", "输出至文件...", JOptionPane.YES_NO_OPTION);
								// 是
								if (res == 0)
								{
									try
									{
										final BuildTool tool = new BuildTool(host, port, dataBaseName, username, password, dataBaseSoft);
										generateDBMain(tool, DirectoryManager.getDBMainPathName());
										generateJavas(tool, tableName, DirectoryManager.getClassDAODAOTestPathDir(tableName));
										JOptionPane.showMessageDialog(UIFile.this, "成功!");
									} catch (ClassNotFoundException e)
									{
										// TODO
										// Auto-generated
										// catch
										// block
										e.printStackTrace();
										JOptionPane.showMessageDialog(null, e.getMessage());
									} catch (SQLException e)
									{
										// TODO
										// Auto-generated
										// catch
										// block
										e.printStackTrace();
										JOptionPane.showMessageDialog(null, e.getMessage());
									} catch (IOException e)
									{
										// TODO
										// Auto-generated
										// catch
										// block
										e.printStackTrace();
										JOptionPane.showMessageDialog(null, e.getMessage());
									}
								}
							}
						}
					});
				}
			}
			this.setTitle("输出至文件...");
			this.setSize(300, 134);
			this.setVisible(true);
			this.setLocationRelativeTo(parent);
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		this.addWindowListener(new WindowListener()
		{
			@Override
			public void windowOpened(WindowEvent e)
			{
				// TODO Auto-generated method stub
				BufferedReader br = null;
				try
				{
					Properties p = new Properties();
					br = new BufferedReader(new FileReader("zy1.properties"));
					p.load(br);
					jtf_packageName.setText(p.getProperty("packageName"));
				} catch (FileNotFoundException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} finally
				{
					try
					{
						if (br != null)
							br.close();
					} catch (IOException e1)
					{
						// TODO Auto-generated catch
						// block
						e1.printStackTrace();
					}
				}
			}
			@Override
			public void windowIconified(WindowEvent e)
			{
				// TODO Auto-generated method stub
			}
			@Override
			public void windowDeiconified(WindowEvent e)
			{
				// TODO Auto-generated method stub
			}
			@Override
			public void windowDeactivated(WindowEvent e)
			{
				// TODO Auto-generated method stub
			}
			@Override
			public void windowClosing(WindowEvent e)
			{
				// TODO Auto-generated method stub
				saveProperties();
			}
			@Override
			public void windowClosed(WindowEvent e)
			{
				// TODO Auto-generated method stub
			}
			@Override
			public void windowActivated(WindowEvent e)
			{
				// TODO Auto-generated method stub
			}
		});
	}
	private void generateDBMain(final BuildTool tool, String pathName) throws IOException, FileNotFoundException
	{
		String dbmain = tool.buildDBMain(jtf_packageName.getText());
		File rootDir = new File(pathName);
		if (!rootDir.exists())
		{
			rootDir.mkdir();
		}
		FileOutputStream fos_dbmain = new FileOutputStream(pathName + "/" + getClassName(dbmain) + ".java");
		fos_dbmain.write(dbmain.getBytes());
		fos_dbmain.close();
	}
	private void generateJavas(final BuildTool tool, String tableName, String pathName) throws ClassNotFoundException, SQLException, IOException, FileNotFoundException
	{
		String class_ = tool.buildClass(tableName, jtf_packageName.getText());
		String dao = tool.buildDAO(tableName, jtf_packageName.getText());
		String daoTest = tool.buildDAOTest(tableName, jtf_packageName.getText());
		String[] javas =
		{ class_, dao, daoTest };
		for (int i = 0; i < javas.length; i++)
		{
			File dir = new File(pathName);
			if (!dir.exists())
			{
				dir.mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(pathName + "/" + getClassName(javas[i]) + ".java");
			fos.write(javas[i].getBytes());
			fos.close();
		}
	}
	private void saveProperties()
	{
		Properties p = new Properties();
		p.setProperty("packageName", jtf_packageName.getText());
		PrintWriter pw = null;
		try
		{
			pw = new PrintWriter("zy1.properties");
			p.list(pw);
		} catch (FileNotFoundException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally
		{
			if (pw != null)
				pw.close();
		}
	}
}
