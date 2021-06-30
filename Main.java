import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

public class Main {
	
	public static void main(String [] args){
		JFrame mainWindow = CreateNewWindow();
		Container pane = mainWindow.getContentPane();
		
		JPanel subPanel = new JPanel();
		
		//defining reading button and add it to the pane
		JButton readButton=new JButton("Read C Drive");
		subPanel.add(readButton);
		
		pane.add(subPanel, BorderLayout.PAGE_START);
		readButton.setBounds(50, 150, 100, 30);
		
		//define tree with its tree model
		File c = new File("C:\\Users\\lenovo\\Desktop");
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(new FileNode(c).getFilename());
		DefaultTreeModel treeModel = new DefaultTreeModel(root);
		JTree tree = new JTree(treeModel);
		
		//Defining ScrollPane
		JScrollPane scroll = new JScrollPane(tree);

		JPanel infoPanel = new JPanel();
		infoPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
		//put data in vertical box layout (under each other)
		BoxLayout boxlayout = new BoxLayout(infoPanel, BoxLayout.Y_AXIS);
		infoPanel.setLayout(boxlayout);
		
		//Defining SplitPane to put the scroll and infoPanel next to each other
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scroll, infoPanel);
		scroll.setMinimumSize(new Dimension(950, 950));
		infoPanel.setMinimumSize(new Dimension(700, 700));
		
		pane.add(splitPane, BorderLayout.CENTER);
		
		ActionListener actionListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				AddChildren(root, c);
				treeModel.reload(root);
			}
		};
		readButton.addActionListener(actionListener);
		
		//this method calls all the updateUI methods in every component
		SwingUtilities.updateComponentTreeUI(mainWindow);
		ChangeIcons(tree);
		
		tree.addTreeSelectionListener(new TreeSelectionListener() {
		    public void valueChanged(TreeSelectionEvent e) {
		        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent(); 
		        if (node == null) return;
		        FileNode f = (FileNode)node.getUserObject();
		        Properties(f, infoPanel);
		    }
		});
		
	}
	
	//method to create a JFrame
	public static JFrame CreateNewWindow() {
		JFrame frame = new JFrame("List of C Drive");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		frame.setLocationRelativeTo(null);
		frame.setSize(1000,600);
		frame.setVisible(true);
		return frame;
	}
	
	//Method that adds the children of a node to the tree
	public static void AddChildren(DefaultMutableTreeNode parent, File parentFile) {
		File [] filesInDirectory = parentFile.listFiles();
		if (filesInDirectory == null) return;
		for(int i=0; i<filesInDirectory.length; i++) {
			DefaultMutableTreeNode child = new DefaultMutableTreeNode(new FileNode(filesInDirectory[i]));
			parent.add(child);
			if(filesInDirectory[i].isDirectory()) {
				AddChildren(child, filesInDirectory[i]);
			}
		}
	}
	
	public static void ChangeIcons(JTree tree) {
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
		ImageIcon closeOpenIcon = new ImageIcon("leaf.png");
		Image i = closeOpenIcon.getImage();
		Image new_img = i.getScaledInstance(15, 15, Image.SCALE_SMOOTH);
		closeOpenIcon = new ImageIcon(new_img);
		renderer.setOpenIcon(closeOpenIcon);
		renderer.setClosedIcon(closeOpenIcon);
		
		ImageIcon leafIcon = new ImageIcon("closed.png");
		i = leafIcon.getImage();
		new_img = i.getScaledInstance(15, 15, Image.SCALE_SMOOTH);
		leafIcon = new ImageIcon(new_img);
		renderer.setLeafIcon(leafIcon);
	}
	
	
	public static void Properties(FileNode file, JPanel pane) {
		try {
			//attr gets the information of the file
			BasicFileAttributes attr = Files.readAttributes(file.getFile().toPath(), BasicFileAttributes.class);
			pane.removeAll();
			
			//add file type
			JLabel type;
			if(!file.getFile().isDirectory()) {
				int index = file.toString().lastIndexOf('.');
				String extension = "";
				if (index > 0) {
				    extension = file.toString().substring(index + 1);
				}
				type = new JLabel("Type of file: ."+ extension);
				
			}
			else {
				type = new JLabel("Type of file: File Folder");
			}
			pane.add(type);
			
			//add file location
			JLabel location = new JLabel("Location: "+ file.getFile().getAbsolutePath());
			pane.add(location);
			
			//add file size
			JLabel size = new JLabel("Size: "+ attr.size());
			pane.add(size);
			
			//add creation date
			String creationS = attr.creationTime().toString();
			String creation = "";
			int i=0;
			while(creationS.charAt(i)!='.') {
				creation+=creationS.charAt(i);
				i++;
			}
			creation = creation.replace("T","     ");
			JLabel created = new JLabel("Created : "+ creation);
			pane.add(created);
			
			
			//add access date
			String accessedS = attr.lastAccessTime().toString();
			String access = "";
			i=0;
			while(accessedS.charAt(i)!='.') {
				access+=accessedS.charAt(i);
				i++;
			}
			access = access.replace("T","     ");
			JLabel accessed = new JLabel("Last Accessed : "+ access);
			pane.add(accessed);
			
			
			//add modification date
			String modifiedS = attr.lastModifiedTime().toString();
			String modify = "";
			i=0;
			while(modifiedS.charAt(i)!='.') {
				modify+=modifiedS.charAt(i);
				i++;
			}
			modify = modify.replace("T","     ");
			JLabel modified = new JLabel("Last modified : "+ modify);
			pane.add(modified);
			
			//add open button and the action with it
			JButton Open = new JButton("Open");
			pane.add(Open);
			ActionListener actionListener = new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					Desktop desktop = Desktop.getDesktop();  
					if(file.getFile().exists())
						try {
							desktop.open(file.getFile());
						} catch (IOException e) {
							e.printStackTrace();
						}    
				}
			};
			Open.addActionListener(actionListener);
			pane.updateUI();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
}
