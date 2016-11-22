package com.uchicom.syo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
import javax.swing.undo.UndoManager;

import com.uchicom.syo.action.script.ScriptAction;
import com.uchicom.syo.util.FileUtil;
import com.uchicom.syo.util.ImageUtil;
import com.uchicom.syo.util.LineNumberView;
import com.uchicom.syo.util.UIStore;

/**
 * テキストエディタ.
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class EditorFrame extends JFrame implements UIStore, ClipboardOwner {

	private JTextArea textArea = new JTextArea();
	private UndoManager undoManager = new UndoManager();
	private Properties menuResource = new Properties();
	private Properties resource = new Properties();
	private Properties config = new Properties();
	// ボタンの準備
	private JPanel northPanel = new JPanel(new FlowLayout());
	private JPanel southPanel = new JPanel(new FlowLayout());
	private JPanel eastPanel = new JPanel(new FlowLayout());
	private JPanel westPanel = new JPanel(new FlowLayout());

	Map<String, Action> actionMap = new HashMap<>();

	JPopupMenu popupMenu = new JPopupMenu();

	private List<Action> notifyList = new ArrayList<>();

	public static final String PROP_EXT = ".properties";
	/**
	 * 設定プロパティーファイルの相対パス
	 */
	private static final String CONF_FILE_PATH = "./conf/syo" + PROP_EXT;
	/**
	 * 画面の初期位置保持キー
	 */
	private static final String PROP_INIT_KEY = "init";

	private File file;
	private boolean selected = false;
	private int value;

	public EditorFrame(int value) {
		this.value = value;
		initComponents(null, null);
	}

	public EditorFrame(File file) {
		initComponents(file, null);
	}

	public EditorFrame(File file, Rectangle rectangle) {
		initComponents(file, rectangle);
	}

	private void initComponents(File file, Rectangle rectangle) {
		setIconImage(ImageUtil.getImageIcon("images/icon.png").getImage());
		if (file != null) {
			setTitle(file.getName());
		} else {
			setTitle(null);
		}
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		loadConf();


		try {
			menuResource.load(new FileInputStream("./conf/menu.properties"));
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		try {
			resource.load(new FileInputStream("./conf/resource.properties"));
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		textArea.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				if (e instanceof DefaultDocumentEvent) {
					DefaultDocumentEvent de = (DefaultDocumentEvent) e;
					undoManager.addEdit(de);
					// TODO 元に戻すとかのenable制御
				}
			}

			public void removeUpdate(DocumentEvent e) {
				if (e instanceof DefaultDocumentEvent) {
					DefaultDocumentEvent de = (DefaultDocumentEvent) e;
					undoManager.addEdit(de);
				}
			}

			public void changedUpdate(DocumentEvent e) {
				// 属性が変わったときは、何もしなくてよい。

			}
		});

		//ドラッグアンドドロップでファイルオープン機能
		TransferHandler handler = new TransferHandler() {
			@Override
			public void exportToClipboard(JComponent comp, Clipboard clip,
					int action) {
				String text = textArea.getSelectedText();

				if (text != null) {
					switch (action) {
					case 1:
						clip.setContents(new StringSelection(text),
								EditorFrame.this);
						break;
					case 2:
						clip.setContents(new StringSelection(text),
								EditorFrame.this);

						int start = textArea.getSelectionStart();
						int end = textArea.getSelectionEnd();
						textArea.replaceRange(null, start, end);
						break;
					}

				}
			}

			@Override
			public boolean canImport(TransferSupport support) {
				DataFlavor[] dfs = support.getDataFlavors();
				for (DataFlavor df : dfs) {
					if (DataFlavor.javaFileListFlavor.equals(df)) {
						return support.getDropAction() == MOVE;
					} else if (DataFlavor.stringFlavor.equals(df)) {
						return support.getDropAction() == COPY_OR_MOVE;
					}
				}
				return false;
			}

			@Override
			public boolean importData(TransferSupport support) {
				DataFlavor[] dfs = support.getDataFlavors();
				for (DataFlavor df : dfs) {
					if (DataFlavor.javaFileListFlavor.equals(df)) {
					} else if (DataFlavor.stringFlavor.equals(df)) {
						Transferable t = support.getTransferable();
						String value = null;
						try {
							value = (String) t
									.getTransferData(DataFlavor.stringFlavor);
							int start = textArea.getSelectionStart();
							int end = textArea.getSelectionEnd();
							if (start < end) {
								textArea.replaceRange(value, start, end);
							} else {
								textArea.insert(value,
										textArea.getCaretPosition());
							}
							System.out.println(value);
						} catch (UnsupportedFlavorException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
						} catch (IOException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
						}
					}
				}
				if (support.isDrop()) {
					// ドロップ処理
					int action = support.getDropAction();
					Transferable t = support.getTransferable();
					if (action == MOVE) {

						try {
							@SuppressWarnings("unchecked")
							List<File> fileList = (List<File>) t
									.getTransferData(DataFlavor.javaFileListFlavor);
							if (fileList.size() > 0) {
								File file = fileList.get(0);
								int start = 0;
								if (textArea.getText().length() == 0) {
									textArea.setText(FileUtil.readFile(file));
									textArea.setCaretPosition(0);
									start = 1;
									setTitle(file.getName());
								}
								Rectangle rectangle = EditorFrame.this
										.getBounds();
								for (int i = start; i < fileList.size(); i++) {

									if (!file.equals(EditorFrame.this.file)) {
										rectangle.x += 5;
										rectangle.y += 5;
									}
									Starter.open(file, rectangle);
								}
							}
						} catch (UnsupportedFlavorException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
						} catch (IOException e) {
							// TODO 自動生成された catch ブロック
							e.printStackTrace();
						}
					}
					return true;
				}
				return false;
			}
		};
		this.setTransferHandler(handler);

		textArea.setTransferHandler(handler);
		// 選択状態通知
		textArea.addCaretListener(new CaretListener() {

			@Override
			public void caretUpdate(CaretEvent e) {
				if (textArea.getSelectionStart() < textArea.getSelectionEnd()) {
					if (!selected) {
						selected = true;
						for (Action scriptAction : notifyList) {
							scriptAction.setEnabled(true);
						}
					}
				} else {
					if (selected) {
						selected = false;
						for (Action scriptAction : notifyList) {
							scriptAction.setEnabled(false);
						}
					}
				}
			}

		});
		// メニューの準備（ファイル、編集、スクリプト、ヘルプ）
		JMenuBar menuBar = new JMenuBar();
		String[] menus = menuResource.getProperty("menu").split(",");
		for (String menuProp : menus) {
			menuBar.add(createMenu("menu." + menuProp));
		}
		menus = menuResource.getProperty("popup").split(",");
		for (String menuProp : menus) {
			JMenuItem menu = createMenu("popup." + menuProp);
			if (menu == null) continue;
			popupMenu.add(menu);
		}

		setJMenuBar(menuBar);
		JPanel panel = new JPanel(new BorderLayout());
		if (northPanel.getComponentCount() > 0) {
			panel.add(northPanel, BorderLayout.NORTH);
		}
		if (southPanel.getComponentCount() > 0) {
			panel.add(southPanel, BorderLayout.SOUTH);
		}
		if (westPanel.getComponentCount() > 0) {
			panel.add(westPanel, BorderLayout.WEST);
		}
		if (eastPanel.getComponentCount() > 0) {
			panel.add(eastPanel, BorderLayout.EAST);
		}
		LineNumberView view = new LineNumberView(textArea);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setRowHeaderView(view);
		panel.add(scrollPane, BorderLayout.CENTER);
		getContentPane().add(panel);
		textArea.getFont();
		// ファイル読み込み
		if (file != null) {
			textArea.setText(FileUtil.readFile(file));
			textArea.setCaretPosition(0);
			setTitle(file.getName() + " - " + Constants.APP_NAME);
		}
		pack();
		if (rectangle != null) {
			setBounds(rectangle);
		} else {

		}
		textArea.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				System.out.println("c");

			}

			@Override
			public void focusGained(FocusEvent e) {

				System.out.println("b" + e.getOppositeComponent());

			}
		});
		popupMenu.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO 自動生成されたメソッド・スタブ
				System.out.println("aa");

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO 自動生成されたメソッド・スタブ

			}
		});
		textArea.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println("a");
				// TODO 自動生成されたメソッド・スタブ
				if (e.getButton() == MouseEvent.BUTTON3) {
					popupMenu.show(textArea, e.getX(), e.getY());
				}

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO 自動生成されたメソッド・スタブ

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO 自動生成されたメソッド・スタブ

			}

		});
	}

	private JMenuItem createMenu(String key) {
		String name = key + ".name";
		if (resource.containsKey(name)) {
			JMenu menu = new JMenu(resource.getProperty(name));
			if (menuResource.containsKey(key)) {
				String[] childMenu = menuResource.getProperty(key).split(",");
				for (String child : childMenu) {
					if ("".equals(child)) continue;
					if (child.charAt(0) == '[' && child.charAt(child.length() - 1) == ']') {
						String special = child.substring(1, child.length() -1);
						if (special.length() == 0) continue;
						//ラインセパレータ
						if ("-".equals(special)) {
							menu.addSeparator();
							continue;
						}
						//スクリプト処理
						if ("*".equals(special)) {
							File scriptDir = new File("script");
							if (scriptDir.exists()) {
								try {
									setScriptMenu(scriptDir, menu);
								} catch (IOException e) {
									// TODO 自動生成された catch ブロック
									e.printStackTrace();
								}
							} else {
								scriptDir.mkdir();
							}
							continue;
						}
						String scriptKey = key + "." + special;
						if (menuResource.containsKey(scriptKey)) {
							String scriptPath = menuResource.getProperty(scriptKey);
							if (scriptPath == null) continue;
							int lastIndex = scriptPath.lastIndexOf('[');
							String[] params = null;
							File scriptFile = null;
							if (scriptPath.charAt(scriptPath.length() - 1) == ']' && lastIndex > 0) {
								params = scriptPath.substring(lastIndex + 1, scriptPath.length() - 1).split(",");
								scriptFile = new File(scriptPath.substring(0, lastIndex));
							} else {
								scriptFile = new File(scriptPath);
							}
							try {
								createScriptMenu(scriptFile, menu, params);
							} catch (IOException e) {
								// TODO 自動生成された catch ブロック
								e.printStackTrace();
							}
							continue;
						}
					}
					try {
						if (menuResource.containsKey(key + "." + child +".ACTION")) {
							menu.add(new JMenuItem(createAction(menuResource.getProperty(key + "."
													+ child + ".ACTION"))));
						} else {
							menu.add(createMenu(key + "." + child));
						}
					} catch (Exception e1) {
						// TODO 自動生成された catch ブロック
						e1.printStackTrace();
					}
				}
			}
			return menu;
		} else {
			try {
				if (menuResource.containsKey(key + ".ACTION")) {
					return new JMenuItem(createAction(menuResource.getProperty(key + ".ACTION")));
				}
			} catch (Exception e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
			}
		}
		return null;
	}

	private Action createAction(String className) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, ClassNotFoundException {
		Action action = actionMap.get(className);
		if (action == null) {
			action = (Action)Class
			.forName(className)
			.getConstructor(UIStore.class)
			.newInstance(this);
			actionMap.put(className, action);
		}
		return action;
	}
	private Action createScriptAction(File file, String[] params) throws IOException {
		StringBuffer keyBuff = new StringBuffer();
		keyBuff.append(file.getCanonicalPath());
		if (params != null) {
		for (String param : params) {
			keyBuff.append(" ");
			keyBuff.append(param);
		}
		}
		String key = keyBuff.toString();
		Action action = actionMap.get(key);
		if (action == null) {
			action = new ScriptAction(this, file, params);
			actionMap.put(key, action);
		}
		return action;
	}

	private void loadConf() {
		// 設定プロパティーファイル読み込み
		File file = new File(CONF_FILE_PATH);
		FileInputStream fis = null;
		try {
			if (file.exists()) {
				fis = new FileInputStream(file);
				config.load(fis);
				// 画面の初期位置取得
				if (config.containsKey(PROP_INIT_KEY)) {
					String initPoint = config.getProperty(PROP_INIT_KEY);
					String[] points = initPoint.split(":");
					if (points.length == 4) {
						setLocation(Integer.parseInt(points[0]),
								Integer.parseInt(points[1]));
						setPreferredSize(new Dimension(
								Integer.parseInt(points[2]),
								Integer.parseInt(points[3])));
					}
				}
				if (config.containsKey("lookAndFeel")) {
					String lookAndFeel = config.getProperty("lookAndFeel");
					UIManager.setLookAndFeel(lookAndFeel);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		} finally {
			// ファイル入力ストリームのクローズ処理
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					fis = null;
				}
			}
		}
	}

	private void setScriptMenu(File dir, JMenu menu) throws IOException {
		for (File file : dir.listFiles()) {
			createScriptMenu(file, menu, null);
		}
	}
	public void createScriptMenu(File file, JMenu menu, String[] params) throws IOException {

		if (file.exists()) {
			Action action = createScriptAction(file, params);
			initSelected(file, action);
			if (file.isDirectory()) {
				JMenu childMenu = new JMenu(action);
				setScriptMenu(file, childMenu);
				menu.add(childMenu);
			} else {
				initButton(file, action);
				menu.add(new JMenuItem(action));
			}
		}
	}

	private void initSelected(File file, Action action) {
		if (file.getName().contains("[s]")) {
			action.setEnabled(false);
			notifyList.add(action);
		}
	}
	private void initButton(File file, Action action) {
		String name = file.getName();
		int index = name.indexOf("[b");
		if (index >= 0) {
			switch (name.substring(index + 2, index + 4)) {
			case "t]":
				//上
				addButton(northPanel, action);
				break;
			case "b]":
				//下
				addButton(southPanel, action);
				break;
			case "l]":
				//左
				addButton(westPanel, action);
				break;
			case "r]":
				//右
				addButton(eastPanel, action);
				break;
			}
		}

	}

	private void addButton(JPanel panel, Action action) {
		JButton button = new JButton(action);
		button.setFocusable(false);
		panel.add(button);
	}

	@Override
	public JTextArea getTextArea() {
		return textArea;
	}

	@Override
	public Component getMainComponent() {
		return this;
	}

	@Override
	public UndoManager getUndoManager() {
		return undoManager;
	}

	@Override
	public Properties getActionResource() {
		return resource;
	}

	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// TODO 自動生成されたメソッド・スタブ

	}
	@Override
	public void setTitle(String fileName) {
		StringBuffer strBuff = null;
		if (fileName != null) {
			strBuff = new StringBuffer(Constants.APP_NAME.length() + 3 + fileName.length());
			strBuff.append(fileName)
			.append(" - ")
			.append(Constants.APP_NAME);
		} else {
			String val = String.valueOf(value);
			strBuff = new StringBuffer(Constants.APP_NAME.length() + 9 + 3 + val.length());
			strBuff
			.append(val)
			.append(" no title  - ")
			.append(Constants.APP_NAME);
		}
		super.setTitle(strBuff.toString());
	}
}
