package ui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.CompoundBorder;

import aya.Element;


@SuppressWarnings("serial")
public class EditorWindow extends JPanel {
	
	private static final int WIDTH = 400;
	
	private static final CompoundBorder BORDER = new CompoundBorder(
				BorderFactory.createMatteBorder(5, 5, 5, 5, StyleTheme.ACCENT_COLOR),
				BorderFactory.createMatteBorder(5, 5, 5, 5, StyleTheme.DEFAULT.getBgColor()));
	
	
	private JScrollPane scrollResults;
	private CodeTextPane editor = new CodeTextPane();
	
	public static JFrame activeFrame;
	public static EditorWindow activeEditor;
	private JMenu menu;
	private JMenuBar menuBar;
	private ElementIDE ide;
	
	public static void newEditorFrame(ElementIDE ide) {
		activeFrame = new JFrame("Editor");
		activeEditor = new EditorWindow(ide);
		activeFrame.add(activeEditor);
		activeFrame.pack();
		activeFrame.setVisible(true);
		activeFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		activeFrame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                activeFrame.setVisible(false);
            }
        });
	}
	public static boolean isFrameActive() {
		return activeFrame != null;
	}
	public static void frameFocus() {
		activeFrame.setVisible(true);
		activeEditor.grabFocus();
	}

	
	public EditorWindow(ElementIDE ide) {	
		this.ide = ide;
		
		//Size
		setMaximumSize(new Dimension(WIDTH, 500));
		setMinimumSize(new Dimension(WIDTH, 500));
		setPreferredSize(new Dimension(WIDTH, 500));
		
		//Border
		setBorder(BORDER);

		
		//Layout
		setLayout(new BorderLayout());
	
		
		KeyListener keyListener = new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
				if(arg0.getKeyChar()==KeyEvent.VK_TAB) {
					editor.tabPressed();
				}
			}
			//@Override
			//public void keyTyped(KeyEvent e) {}
			//@Override
			//public void keyReleased(KeyEvent e) {}

			public void keyReleased(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			public void keyTyped(KeyEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		};
		editor.addKeyListener(keyListener);
			
		
		//Menu Bar
		menuBar = new JMenuBar();
		menuBar.setPreferredSize(new Dimension(100, 20));
		
//		//File
//		menu = new JMenu("File");
//		menu.setMnemonic(KeyEvent.VK_A);
//		menu.getAccessibleContext().setAccessibleDescription("");
//		//Load
//		JMenuItem mi =new JMenuItem(new Action() {
//			public void actionPerformed(ActionEvent e) {
//				JOptionPane.showMessageDialog(activeFrame, "Load not yet implemted", "ERROR", JOptionPane.ERROR_MESSAGE);
//			}
//			public void addPropertyChangeListener(PropertyChangeListener l) {}
//			public Object getValue(String k) {return null;}
//			public boolean isEnabled() {return true;}
//			public void putValue(String k, Object v) {}
//			public void removePropertyChangeListener(PropertyChangeListener l) {}
//			public void setEnabled(boolean b) {}
//		});
//		mi.setText("Load");
//		menu.add(mi);
//		menuBar.add(menu);
			
		//Tools
		menu = new JMenu("Tools");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription("");
		//Insert Filename
		JMenuItem mi =new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				insertFilenameAtCarat();
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Insert Filename..");
		menu.add(mi);
		
		//Run
		mi =new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				run();
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Run");
		menu.add(mi);
		menuBar.add(menu);
				
		
		
		//Help
		//Quick Search
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription("");
		mi = new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				if(QuickSearch.isFrameActive()) {
					QuickSearch.frameFocus();
				} else {
					QuickSearch.newQSFrame(Element.getQuickSearchData());
				}
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Quick Search");
		menu.add(mi);
		
		//Key Bindings
		mi = new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(activeFrame, ""
						+ "ctrl+Q		  Quick Search\n"
						+ "ctrl+I		  Interpreter\n"
						+ "ctrl+E         Editor\n"
						);
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("Key Bindings");
		menu.add(mi);
		mi = new JMenuItem(new Action() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(activeFrame, "Element IDE\nNicholas Paul\nVersion: " + ElementIDE.VERSION_NAME + "\nElement Version: " + Element.VERSION_NAME);
			}
			public void addPropertyChangeListener(PropertyChangeListener l) {}
			public Object getValue(String k) {return null;}
			public boolean isEnabled() {return true;}
			public void putValue(String k, Object v) {}
			public void removePropertyChangeListener(PropertyChangeListener l) {}
			public void setEnabled(boolean b) {}
		});
		mi.setText("About");
		menu.add(mi);
		menuBar.add(menu);
		
		
		activeFrame.setJMenuBar(menuBar);
		
		//Editor
		editor.setPreferredSize(new Dimension(WIDTH-10, 400));
		add(editor, BorderLayout.NORTH);

		//Wrap the results in a scroll pane
		scrollResults = new JScrollPane(editor);
		scrollResults.setBackground(StyleTheme.DEFAULT.getBgColor());
		scrollResults.setBorder(BorderFactory.createEmptyBorder());
		scrollResults.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		//scrollResults.getVerticalScrollBar().setBackground(StyleTheme.DEFAULT.getBgColor());
		scrollResults.getVerticalScrollBar().setBorder(BorderFactory.createEmptyBorder());
		add(scrollResults);
		
	}
	
	
	/** Scrolls to the top of the list */
	public void scrollToTop() {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
		   public void run() { 
		       scrollResults.getVerticalScrollBar().setValue(0);
		   }
		});
	}
	
	public void insertFilenameAtCarat() {
		File file = ElementIDE.chooseFile();
		if(file != null) {
			String path = file.getPath();
			path = path.replace("\\", "\\\\");
            editor.insertAtCaret(path);
		}
	}
	
	public static boolean hasText() {
		return activeEditor != null ? !activeEditor.editor.getText().equals("") : false;
	}
	
	public void run() {
		String txt = editor.getText();
		ide.eval(txt, "[Editor Window]");
	}
	
	@Override
	public void grabFocus() {
		editor.grabFocus();
	}
}
