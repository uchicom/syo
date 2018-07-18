// (c) 2015 uchicom
package com.uchicom.syo;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument.DefaultDocumentEvent;
import javax.swing.undo.UndoManager;

import com.uchicom.syo.action.script.ScriptAction;
import com.uchicom.syo.util.FileUtil;
import com.uchicom.syo.util.LineNumberView;
import com.uchicom.ui.ResumeFrame;
import com.uchicom.ui.util.ImageUtil;
import com.uchicom.ui.util.UIStore;
import com.uchicom.util.ResourceUtil;

/**
 * テキストエディタ.
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class EditorFrame extends ResumeFrame implements UIStore<EditorFrame>, ClipboardOwner {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextArea textArea = new JTextArea();
	private UndoManager undoManager = new UndoManager();
	private Properties resource;
	private static final ResourceBundle resourceBundle = ResourceBundle
			.getBundle("com.uchicom.syo.resource");
	// ボタンの準備
	private JPanel northPanel = new JPanel(new FlowLayout());
	private JPanel southPanel = new JPanel(new FlowLayout());
	private JPanel eastPanel = new JPanel(new FlowLayout());
	private JPanel westPanel = new JPanel(new FlowLayout());

	Map<String, Action> actionMap = new HashMap<>();

	JPopupMenu popupMenu = new JPopupMenu();

	/**
	 * 選択状態を通知するリスト.
	 */
	private List<Action> notifyList = new ArrayList<>();

	public static final String PROP_EXT = ".properties";
	/**
	 * 設定プロパティーファイルの相対パス
	 */
	private static final String CONF_FILE_PATH = "./conf/syo" + PROP_EXT;

	private File file;
	private boolean selected;
	private int value;
	private int editCount;

	public EditorFrame(int value) {
		super(new File(CONF_FILE_PATH), "syo.window");
		this.value = value;
		initComponents(null, null);
	}

	public EditorFrame(File file) {
		this(file, null);
	}

	public EditorFrame(File file, Rectangle rectangle) {
		super(new File(CONF_FILE_PATH), "syo.window");
		this.file = file;
		initComponents(file, rectangle);
	}

	private void initComponents(File file, Rectangle rectangle) {
		try {
			setIconImage(ImageUtil.getImageIcon("images/icon.png").getImage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (file != null) {
			// ファイル読み込み
			try {
				textArea.setText(FileUtil.readFile(file, getCharset()));
				textArea.setCaretPosition(0);
				setTitle(file.getName());
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		} else {
			setTitle(null);
		}
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		//他のファイルの読み込みをどうするのか
		resource = ResourceUtil.createProperties(
				getClass().getClassLoader().getResourceAsStream("com/uchicom/syo/resource.properties"), "UTF-8");

		textArea.setFont(new Font("Monospaced", Font.PLAIN,  12));//TODO 設定ファイル化
		textArea.setTabSize(4);//TODO 設定ファイル化
		textArea.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				if (e instanceof DefaultDocumentEvent) {
					DefaultDocumentEvent de = (DefaultDocumentEvent) e;
					undoManager.addEdit(de);
					editCount++;
					edited(1);
				}
			}

			public void removeUpdate(DocumentEvent e) {
				if (e instanceof DefaultDocumentEvent) {
					DefaultDocumentEvent de = (DefaultDocumentEvent) e;
					undoManager.addEdit(de);
					editCount++;
					edited(1);
				}
			}

			public void changedUpdate(DocumentEvent e) {
				// 属性が変わったときは、何もしなくてよい。

			}
		});

		//ドラッグアンドドロップでファイルオープン機能
		TransferHandler handler = new TransferHandler() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

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
									textArea.setText(FileUtil.readFile(file, getCharset()));
									textArea.setCaretPosition(0);
									start = 1;
									setTitle(file.getName());
									EditorFrame.this.file = file;
								}
								Rectangle rectangle = (Rectangle) EditorFrame.this
										.getBounds().clone();
								for (int i = start; i < fileList.size(); i++) {
									file = fileList.get(i);
									if (!file.equals(EditorFrame.this.file)) {
										rectangle.x += 26;
										rectangle.y += 26;
										Starter.open(file, (Rectangle)rectangle.clone());
									}
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
		String[] menus = resource.getProperty("menu").split(",");
		for (String menuProp : menus) {
			menuBar.add(createMenu("menu." + menuProp));
		}
		menus = resource.getProperty("popup").split(",");
		for (String menuProp : menus) {
			if (menuProp.equals("[-]")) {
				popupMenu.addSeparator();
			} else {
				JMenuItem menu = createMenu("popup." + menuProp);
				System.out.println(menu);
				if (menu == null) continue;
				popupMenu.add(menu);
			}
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

		pack();
		if (rectangle != null) {
			setBounds(rectangle);
		} else {
			if (file == null) {
				//表示位置をずらす
				Point p = getLocation();
				p.x += value * 26;
				p.y += value * 26;
				//超えたら
				Rectangle rec = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
				rectangle = getBounds();
				System.out.println(rectangle);
				if (p.x + rectangle.width < rec.width &&
						p.y + rectangle.height < rec.height) {
					setLocation(p);
				} else {
					// widthで引いて、 33 + a
					int a=0;
					int val = 0;
					int count = (rec.width - rectangle.width - 66) / 26;
					if (count > (rec.height - rectangle.height - 66) / 26) {
						count = (rec.height - rectangle.height - 66) / 26;
						a = (rectangle.y + rectangle.height - rec.height - 33) / 26;
					} else {
						a = (rectangle.x + rectangle.width - rec.width - 33) / 26;
					}
					count++;

					val = (value - a + 1) % count;
					p.x = 33 + 26 * val;
					p.y = 33 + 26 * val;
					setLocation(p);
				}
			}
		}
//		textArea.addFocusListener(new FocusListener() {
//
//			@Override
//			public void focusLost(FocusEvent e) {
//				System.out.println("c");
//
//			}
//
//			@Override
//			public void focusGained(FocusEvent e) {
//
//				System.out.println("b" + e.getOppositeComponent());
//
//			}
//		});
//		popupMenu.addMouseListener(new MouseListener() {
//
//			@Override
//			public void mouseReleased(MouseEvent e) {
//				// TODO 自動生成されたメソッド・スタブ
//				System.out.println("aa");
//
//			}
//
//			@Override
//			public void mousePressed(MouseEvent e) {
//				// TODO 自動生成されたメソッド・スタブ
//
//			}
//
//			@Override
//			public void mouseExited(MouseEvent e) {
//				// TODO 自動生成されたメソッド・スタブ
//
//			}
//
//			@Override
//			public void mouseEntered(MouseEvent e) {
//				// TODO 自動生成されたメソッド・スタブ
//
//			}
//
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				// TODO 自動生成されたメソッド・スタブ
//
//			}
//		});
		textArea.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
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
			if (resource.containsKey(key)) {
				String[] childMenu = resource.getProperty(key).split(",");
				for (String child : childMenu) {
					if ("".equals(child)) continue;
					if (child.charAt(0) == '[' && child.charAt(child.length() - 1) == ']') {
						String special = child.substring(1, child.length() -1);
						if (special.length() == 0) continue;
						if (special.length() == 1) {
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
						}
						String scriptName = null;
						int nameIndex = special.indexOf(':');
						if (nameIndex > 0) {
							scriptName = special.substring(nameIndex + 1);
							special = special.substring(0, nameIndex);
						}
						String scriptKey = key + "." + special;
						if (resource.containsKey(scriptKey)) {
							String scriptPath = resource.getProperty(scriptKey);
							if (scriptPath == null) continue;
							int index = scriptPath.indexOf(':');
							String param = null;
							File scriptFile = null;
							if (index > 0) {
								param = scriptPath.substring(index + 1);
								scriptFile = new File(scriptPath.substring(0, index));
							} else {
								scriptFile = new File(scriptPath);
							}
							try {
								createScriptMenu(scriptFile, menu, scriptName, param);
							} catch (IOException e) {
								// TODO 自動生成された catch ブロック
								e.printStackTrace();
							}
							continue;
						}
					}
					try {
						if (resource.containsKey(key + "." + child +".ACTION")) {
							menu.add(new JMenuItem(createAction(resource.getProperty(key + "."
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
				if (resource.containsKey(key + ".ACTION")) {
					return new JMenuItem(createAction(resource.getProperty(key + ".ACTION")));
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
	private Action createScriptAction(File file, String name, String param) throws IOException {
		if (file.getName().contains("[h]")) {
			return null;
		}
		StringBuffer keyBuff = new StringBuffer();
		keyBuff.append(file.getCanonicalPath());
		if (name != null) {
			keyBuff.append(":");
			keyBuff.append(param);
		}
		if (param != null) {
			keyBuff.append(" ");
			keyBuff.append(param);
		}
		String key = keyBuff.toString();
		Action action = actionMap.get(key);
		if (action == null) {
			action = new ScriptAction(this, file, name, param);
			actionMap.put(key, action);
		}
		return action;
	}

	private void setScriptMenu(File dir, JMenu menu) throws IOException {
		for (File file : dir.listFiles()) {
			createScriptMenu(file, menu, null, null);
		}
	}
	public void createScriptMenu(File file, JMenu menu, String name, String param) throws IOException {

		if (file.exists()) {
			Action action = createScriptAction(file, name, param);
			if (action != null) {
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

	/**
	 * 上書き保存
	 */
	public void overwrite() {
		if (file != null && file.exists()) {
			try (FileOutputStream fos = new FileOutputStream(file);) {
				fos.write(textArea.getText().getBytes("utf-8"));
				fos.flush();
				editCount = 0;
				setTitle();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			save();
		}

	}
	/**
	 * 名前を付けて保存
	 */
	public void save() {
		JFileChooser chooser = new JFileChooser();
		int result = chooser.showSaveDialog(textArea);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectFile = chooser.getSelectedFile();
			if (selectFile.exists()) {
				//JOptionPane.showOptionDialog(textArea, "ファイルが存在します.上書きしますか?", "上書き確認", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane., ImageIcon., options, initialValue)
			}

			try (FileOutputStream fos = new FileOutputStream(selectFile);) {
				fos.write(textArea.getText().getBytes("utf-8"));
				fos.flush();

				setFile(selectFile);
				editCount = 0;
				setTitle();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

	}

	public void setFile(File file) {
		this.file = file;
		setTitle(file.getName());
	}

	@Override
	public EditorFrame getMainComponent() {
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
		int count = 0;
		if (editCount > 0) {
			count += 2;
		}
		if (fileName != null) {
			count += 3;
			strBuff = new StringBuffer(Constants.APP_NAME.length() + fileName.length() + count);
			if (editCount != 0) {
				strBuff.append("* ");
			}
			strBuff.append(fileName)
			.append(" - ")
			.append(Constants.APP_NAME);
		} else {
			count += 9 + 3;
			String val = String.valueOf(value);
			strBuff = new StringBuffer(Constants.APP_NAME.length() + val.length() + count);
			if (editCount != 0) {
				strBuff.append("* ");
			}
			strBuff
			.append(val)
			.append(" no title  - ")
			.append(Constants.APP_NAME);
		}
		super.setTitle(strBuff.toString());
	}
	public void setTitle() {
		if (file == null) {
			setTitle(null);
		} else {
			setTitle(file.getName());
		}
	}

	/**
	 *
	 * @return
	 */
	public JTextArea getTextArea() {
		return textArea;
	}

	/**
	 *
	 * @return
	 */
	public List<Action> getNotifyList() {
		return notifyList;
	}

	/* (非 Javadoc)
	 * @see com.uchicom.ui.util.UIStore#getResourceBundle()
	 */
	@Override
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public String getCharset() {
		return config.getProperty("charset");
	}
	
	public void redo() {
		if (undoManager.canRedo()) {
			undoManager.redo();
			editCount++;
			edited(1);
		}
	}
	public void undo() {
		if (undoManager.canUndo()) {
			undoManager.undo();
			editCount--;
			edited(0);
		}
	}
	public static final int MINUS = 0;
	public static final int PLUS = 1;
	public void edited(int compareType) {
		if (compareType == MINUS) {
			if (editCount == 0 || editCount == -1) {
				setTitle();
			}
		} else {
			if (editCount == 0 || editCount == 1) {
				setTitle();
			}
		}
	}
	
	@Override
	public void dispose() {
		if (editCount != 0) {
			//上書き保存か別名保存か
			int result = JOptionPane.showConfirmDialog(this, "変更されています。保存しますか？", "保存の確認", JOptionPane.YES_NO_CANCEL_OPTION);
			if (JOptionPane.YES_OPTION == result) {
				overwrite();
			} else if (JOptionPane.CANCEL_OPTION == result) {
				return;
			}
		}
		super.dispose();
	}
	
	public void openCharset() {
		String[] charsets = Charset.availableCharsets().keySet().toArray(new String[0]);
		JList<String> list = new JList<>(charsets);
		int result = JOptionPane.showOptionDialog(this, "文字コードを選択してください", "文字コード選択", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{new JScrollPane(list), "OK"}, "SJIS");
		System.out.println(result);
		if (result == 1) {
			System.out.println(list.getSelectedValue());
			try {
				textArea.setText(FileUtil.readFile(file, list.getSelectedValue()));
				textArea.setCaretPosition(0);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public void search() {
		//TODO 検索処理未実装
		//置換と同じダイアログを使用する。表示を切り替えるのみ
	}
	public void replace() {
		//TODO 置換処理未実装
		//検索に置換後の文字列を入力するボックスを追加する。
	}
}
