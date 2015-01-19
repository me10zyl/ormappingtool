import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import java.awt.Color;

import javax.swing.border.TitledBorder;
import javax.swing.JSlider;
import javax.swing.ButtonGroup;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

public class UIFile extends javax.swing.JDialog {
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
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private final ButtonGroup buttonGroup_1 = new ButtonGroup();
	private JRadioButton rdbtnEntity;
	private JRadioButton radioButtonFenkai;
	private AbstractDirectoryManager directoryManager = new DirectorManager2();
	/**
	 * Auto-generated main method to display this JDialog
	 * 
	 * @param dataBaseSoft
	 *            TODO
	 */
	public UIFile(JFrame frame, String host, String port, String dataBaseName, String username, String password, String tableName, String dataBaseSoft) {
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

	private String getClassName(String str) {
		String classname = null;
		Pattern p = Pattern.compile("class \\w+");
		Matcher m = p.matcher(str);
		if (m.find()) {
			classname = m.group().split(" ")[1];
		}
		return classname;
	}

	private void initGUI() {
		try {
			{
				getContentPane().setLayout(null);
				{
					jLabel1 = new JLabel();
					getContentPane().add(jLabel1);
					jLabel1.setText("\u5305\u540d");
					jLabel1.setBounds(22, 27, 54, 20);
				}
				{
					jtf_packageName = new JTextField();
					getContentPane().add(jtf_packageName);
					jtf_packageName.setBounds(105, 23, 214, 24);
				}
				{
					jb_fileall = new JButton();
					getContentPane().add(jb_fileall);
					jb_fileall.setText("\u751f\u6210\u6240\u6709\u8868");
					jb_fileall.setBounds(179, 80, 113, 31);
					jb_fileall.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							{
								// TODO add your
								// code for
								// jb_file.actionPerformed
								UIFile.this.dispose();
								saveProperties();
								int res = JOptionPane.showConfirmDialog(UIFile.this, "将输出「所有」DBMAIN、类、DAO、DAOTest的JAVA文件至本目录下的oROutputs目录", "输出至文件...", JOptionPane.YES_NO_OPTION);
								// 是
								if (res == 0) {
									try {
										final BuildTool tool = new BuildTool(host, port, dataBaseName, username, password, dataBaseSoft,directoryManager);
										ArrayList<String> tables = tool.getTables();
										generateDBMain(tool, DirectoryManager.getDBMainPathName());
										for (int j = 0; j < tables.size(); j++) {
											String tableName_ = tables.get(j);
											generateJavasEx(tool, tableName_);
										}
										JOptionPane.showMessageDialog(UIFile.this, "成功!");
									} catch (ClassNotFoundException e) {
										// TODO
										// Auto-generated
										// catch
										// block
										e.printStackTrace();
										JOptionPane.showMessageDialog(UIFile.this, e.getMessage());
									} catch (SQLException e) {
										// TODO
										// Auto-generated
										// catch
										// block
										e.printStackTrace();
										JOptionPane.showMessageDialog(UIFile.this, e.getMessage());
									} catch (IOException e) {
										// TODO
										// Auto-generated
										// catch
										// block
										e.printStackTrace();
										JOptionPane.showMessageDialog(UIFile.this, e.getMessage());
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
					jb_filesingle.setBounds(22, 80, 96, 31);

					JPanel panel = new JPanel();
					panel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0), 1, true), "\u5305\u7BA1\u7406\u5668", TitledBorder.LEADING, TitledBorder.TOP, null, null));
					panel.setBounds(337, 13, 187, 102);
					getContentPane().add(panel);
					panel.setLayout(null);

					radioButtonFenkai = new JRadioButton("\u6BCF\u4E2A\u5B9E\u4F53\u6587\u4EF6\u5939\u5206\u5F00\u653E");
					radioButtonFenkai.addChangeListener(new ChangeListener() {
						public void stateChanged(ChangeEvent e) {
							if(radioButtonFenkai.isSelected())
							{
								directoryManager = new DirectoryManager();
							}
						}
					});
					buttonGroup_1.add(radioButtonFenkai);
					radioButtonFenkai.setBounds(22, 25, 142, 18);
					panel.add(radioButtonFenkai);

					rdbtnEntity = new JRadioButton("entity  /  dao  /  test");
					rdbtnEntity.addChangeListener(new ChangeListener() {
						public void stateChanged(ChangeEvent e) {
							if(rdbtnEntity.isSelected())
							{
								directoryManager = new DirectorManager2();
							}
						}
					});
					rdbtnEntity.setSelected(true);
					buttonGroup_1.add(rdbtnEntity);
					rdbtnEntity.setBounds(22, 59, 121, 18);
					panel.add(rdbtnEntity);

					JPanel panel_1 = new JPanel();
					panel_1.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "\u6D4B\u8BD5\u7BA1\u7406\u5668", TitledBorder.LEADING, TitledBorder.TOP, null, null));
					panel_1.setBounds(22, 134, 304, 66);
					getContentPane().add(panel_1);

					JRadioButton rdbtnNewRadioButton = new JRadioButton("\u4E3B\u65B9\u6CD5\u6D4B\u8BD5");
					rdbtnNewRadioButton.setSelected(true);
					buttonGroup.add(rdbtnNewRadioButton);
					panel_1.add(rdbtnNewRadioButton);

					JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("JUnit4");
					buttonGroup.add(rdbtnNewRadioButton_1);
					panel_1.add(rdbtnNewRadioButton_1);

					JLabel lblormapping = new JLabel("\u751F\u6210\u7684\u6587\u4EF6\u5B58\u653E\u5728oROutputs\u6587\u4EF6\u5939");
					lblormapping.setBounds(343, 158, 202, 18);
					getContentPane().add(lblormapping);
					jb_filesingle.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent evt) {
							{
								// TODO add your
								// code for
								// jb_file.actionPerformed
								UIFile.this.dispose();
								saveProperties();
								int res = JOptionPane.showConfirmDialog(UIFile.this, "将输出DBMAIN、类、DAO、DAOTest的JAVA文件至本目录下的oROutputs目录", "输出至文件...", JOptionPane.YES_NO_OPTION);
								// 是
								if (res == 0) {
									try {
										final BuildTool tool = new BuildTool(host, port, dataBaseName, username, password, dataBaseSoft,directoryManager);
										generateDBMain(tool, DirectoryManager.getDBMainPathName());
										generateJavasEx(tool, tableName);
										JOptionPane.showMessageDialog(UIFile.this, "成功!");
									} catch (ClassNotFoundException e) {
										// TODO
										// Auto-generated
										// catch
										// block
										e.printStackTrace();
										JOptionPane.showMessageDialog(UIFile.this, e.getMessage());
									} catch (SQLException e) {
										// TODO
										// Auto-generated
										// catch
										// block
										e.printStackTrace();
										JOptionPane.showMessageDialog(UIFile.this, e.getMessage());
										JOptionPane.showMessageDialog(null, e.getMessage());
									} catch (IOException e) {
										// TODO
										// Auto-generated
										// catch
										// block
										e.printStackTrace();
										JOptionPane.showMessageDialog(UIFile.this, e.getMessage());
									}
								}
							}
						}
					});
				}
			}
			this.setTitle("输出至文件...");
			this.setSize(553, 252);
			this.setVisible(true);
			this.setLocationRelativeTo(parent);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(UIFile.this, e.getMessage());
		}
		this.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				BufferedReader br = null;
				try {
					Properties p = new Properties();
					br = new BufferedReader(new FileReader("zy1.properties"));
					p.load(br);
					jtf_packageName.setText(p.getProperty("packageName"));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
//					JOptionPane.showMessageDialog(UIFile.this, e1.getMessage());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					JOptionPane.showMessageDialog(UIFile.this, e1.getMessage());
				} finally {
					try {
						if (br != null)
							br.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch
						// block
						e1.printStackTrace();
						JOptionPane.showMessageDialog(UIFile.this, e1.getMessage());
					}
				}
			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				saveProperties();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub
			}
		});
	}

	private void generateDBMain(final BuildTool tool, String pathName) throws IOException, FileNotFoundException {
		String dbmain = tool.buildDBMain(jtf_packageName.getText());
		File rootDir = new File(pathName);
		if (!rootDir.exists()) {
			rootDir.mkdir();
		}
		FileOutputStream fos_dbmain = new FileOutputStream(pathName + "/" + getClassName(dbmain) + ".java");
		fos_dbmain.write(dbmain.getBytes());
		fos_dbmain.close();
	}

	private void generateJavas(final BuildTool tool, String tableName, Map<String, String> pathName) throws ClassNotFoundException, SQLException, IOException, FileNotFoundException {
		String class_ = tool.buildClass(tableName, jtf_packageName.getText());
		String dao = tool.buildDAO(tableName, jtf_packageName.getText());
		String daoTest = tool.buildDAOTest(tableName, jtf_packageName.getText());
		String[] javas = { class_, dao, daoTest };
		String[] keys = { "entity", "dao", "test" };
		for (int i = 0; i < javas.length; i++) {
			String pathname2 = pathName.get(keys[i]);
			File dir = new File(pathname2);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(pathname2 + "/" + getClassName(javas[i]) + ".java");
			fos.write(javas[i].getBytes());
			fos.close();
		}
	}
	
	private void generateJavasEx(final BuildTool tool, String tableName_) throws ClassNotFoundException, SQLException, IOException, FileNotFoundException {
		HashMap<String, String> paths = new HashMap<String, String>();
		if(getRadioButtonFenkai().isSelected())
		{
			directoryManager = new DirectoryManager(tableName_);
			paths.put("entity", directoryManager.getEntityDirPath());
			paths.put("dao", directoryManager.getDAODirPath());
			paths.put("test", directoryManager.getTestDirPath());
		}else if(getRdbtnEntity().isSelected())
		{
			directoryManager = new DirectorManager2();
			paths.put("entity", directoryManager.getEntityDirPath());
			paths.put("dao", directoryManager.getDAODirPath());
			paths.put("test", directoryManager.getTestDirPath());
		}
		generateJavas(tool, tableName_, paths);
	}
	
	private void saveProperties() {
		Properties p = new Properties();
		p.setProperty("packageName", jtf_packageName.getText());
		PrintWriter pw = null;
		try {
			pw = new PrintWriter("zy1.properties");
			p.list(pw);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			JOptionPane.showMessageDialog(UIFile.this, e1.getMessage());
		} finally {
			if (pw != null)
				pw.close();
		}
	}
	public JRadioButton getRdbtnEntity() {
		return rdbtnEntity;
	}
	public JRadioButton getRadioButtonFenkai() {
		return radioButtonFenkai;
	}
}
