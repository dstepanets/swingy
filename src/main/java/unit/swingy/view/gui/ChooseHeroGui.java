package unit.swingy.view.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import unit.swingy.controller.Game;
import unit.swingy.model.characters.DataBase;
import unit.swingy.model.characters.Hero;
import unit.swingy.model.characters.HeroBuilder;
import unit.swingy.model.characters.HeroClass;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class ChooseHeroGui {

	private DataBase db = DataBase.getInstance();
	private HeroBuilder hb = new HeroBuilder();
	private ArrayList<Hero> heroesList;
	private Hero hero = null;

	private JFrame frame;
	private JPanel mainPanel;
	private JScrollPane paneLeft;
	private JLabel avatar;
	private JTable table;

	private JScrollPane bioPane;
	private JTextPane bio;
	private JScrollPane statsPane;
	private JTextPane stats;

	private JButton bPlay;
	private JButton bAddHero;
	private JButton bDeleteHero;


	public ChooseHeroGui() {

		this.frame = new JFrame("Choose your hero");
		$$$setupUI$$$();
		updateTable();


		//		select table row event
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				int row = table.getSelectedRow();
				if (row >= 0 && row < heroesList.size()) {
					hero = heroesList.get(row);
					displayHeroStats();
				}
			}
		});

		// '+' button event - create New Hero
		bAddHero.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createNewHero();
			}
		});

		// '-' button event - remove selected Hero
		bDeleteHero.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (hero != null && table.getSelectedRow() >= 0) {
					deleteHero();
				}
			}
		});

		// set selected Hero for the Game and close the window
		bPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (hero != null && table.getSelectedRow() >= 0) {
					Game.getInstance().setHero(hero);
					frame.dispose();
				}
			}
		});

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Game.getInstance().exitGame();
			}
		});

	}


	public void chooseHero() {

		//init frame
		frame.setContentPane(mainPanel);
//		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);    // center window on the screen
		frame.setResizable(false);
		frame.setVisible(true);

	}

	private void updateTable() {

		heroesList = db.getHeroesList(hb);
		int rows = heroesList.size();

		String[] columns = {"NAME", "CLASS", "LEVEL", "EXP"};
		Object[][] data = new Object[rows][4];
		for (int r = 0; r < rows; r++) {
			data[r][0] = heroesList.get(r).getName();
			data[r][1] = heroesList.get(r).getClas();
			data[r][2] = heroesList.get(r).getLevel();
			data[r][3] = heroesList.get(r).getExp();
		}

		// make cells uneditable
		DefaultTableModel tableModel = new DefaultTableModel(data, columns) {
			@Override
			public boolean isCellEditable(int row, int column) {
				//all cells false
				return false;
			}
		};

		table.setModel(tableModel);

	}

	private void displayHeroStats() {
//		clear panes when hero removed
		if (hero == null) {
			avatar.setIcon(null);
			bio.setText("");
			stats.setText("");

		} else {
			avatar.setIcon(hero.getClas().getAvatar());

			bio.setText("");
			stats.setText("");

			SimpleAttributeSet atr = new SimpleAttributeSet();
			StyleConstants.setFontSize(atr, 13);
			StyleConstants.setItalic(atr, true);

			Document bioDoc = bio.getStyledDocument();
			Document statsDoc = stats.getStyledDocument();

			try {
				bioDoc.insertString(statsDoc.getLength(), hero.getClas().getDescription() + "\n\n", atr);
				StyleConstants.setFontSize(atr, 14);
				StyleConstants.setItalic(atr, false);

				statsDoc.insertString(statsDoc.getLength(), "Name: " + hero.getName() + "\n", atr);
				statsDoc.insertString(statsDoc.getLength(), "Class: " + hero.getClas() + "\n\n", atr);
				statsDoc.insertString(statsDoc.getLength(), "Level: " + hero.getLevel() + "\n", atr);
				statsDoc.insertString(statsDoc.getLength(), "Exp: " + hero.getExp() + "\n\n", atr);
				statsDoc.insertString(statsDoc.getLength(), "HP: " + (hero.getMaxHp() + hero.getBonusHp()) + "\n", atr);
				statsDoc.insertString(statsDoc.getLength(), "Attack: " + (hero.getAttack() + hero.getBonusAttack()) + "\n", atr);
				statsDoc.insertString(statsDoc.getLength(), "Defence: " + (hero.getDefence() + hero.getBonusDefence()) + "\n\n", atr);

				String str = (hero.getWeapon() == null) ? "none\n" : hero.getWeapon().getName() + " (Attack +" + hero.getBonusAttack() + ")\n";
				statsDoc.insertString(statsDoc.getLength(), "Weapon: " + str, atr);
				str = (hero.getArmor() == null) ? "none\n" : hero.getArmor().getName() + " (Defence +" + hero.getBonusDefence() + ")\n";
				statsDoc.insertString(statsDoc.getLength(), "Armor: " + str, atr);
				str = (hero.getHelm() == null) ? "none\n" : hero.getHelm().getName() + " (HP +" + hero.getBonusHp() + ")\n";
				statsDoc.insertString(statsDoc.getLength(), "Helm: " + str, atr);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}

	}

	private void createNewHero() {

//		get a name from input
		String input = "";
		do {
			input = (String) JOptionPane.showInputDialog(frame, "Enter your Hero's name (20 characters max):",
					"New Hero's Name", JOptionPane.PLAIN_MESSAGE);
			if (input == null)
				return;
		} while (input.isEmpty() || input.trim().isEmpty() || input.length() > 20);
		input = input.trim();
		final String name = input;


//		GET THE HERO CLASS FROM A DIALOG
//		classes list
		final JFrame f = new JFrame();
		DefaultListModel<String> lm = new DefaultListModel<>();
		for (HeroClass c : HeroClass.values()) {
			lm.addElement(c.toString());
		}
		final JList<String> list = new JList<>(lm);
		list.setBounds(20, 20, 150, 256);
		f.add(list);

//		add avatar
		final JLabel lAvatar = new JLabel();
		lAvatar.setBounds(224, 20, 256, 256);
		f.add(lAvatar);

//		add description
		final JLabel lInfo = new JLabel();
		lInfo.setBounds(20, 282, 400, 128);
		f.add(lInfo);

//		buttons
		JButton bCancel = new JButton("Cancel");
		bCancel.setBounds(50, 430, 120, 40);
		f.add(bCancel);
		JButton bOk = new JButton("Create Hero");
		bOk.setBounds(330, 430, 120, 40);
		f.add(bOk);

		f.setSize(500, 500);
		f.setLayout(null);
		f.setLocationRelativeTo(null);    // center window on the screen
		f.setResizable(false);
		f.setVisible(true);

		// display avatar and description when a list item is selected
		list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				HeroClass clas = HeroClass.valueOf(list.getSelectedValue());
				lAvatar.setIcon(clas.getAvatar());
				String description = "<html>" + clas.getDescription() + "<br><br>" +
						clas.getStartingStatsInfo() + "</html>";
				lInfo.setText(description);
			}
		});

		//CreateHero button listener
		bOk.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (list.getSelectedIndex() != -1) {
					// build hero with builder and add to DataBase
					hb.reset();
					hb.setUpNewHero(name, HeroClass.valueOf(list.getSelectedValue()));
					db.addHero(hb.getHero());
					// close frame, update table
					f.dispose();
					updateTable();
				}
			}
		});

		//Cancel button listener
		bCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hb.reset();
				f.dispose();
			}
		});

	}

	private void deleteHero() {

		// Confirmation dialog
		Object[] options = {"MUST DIE!!!11", "Cancel"};
		int n = JOptionPane.showOptionDialog(frame,
				"Are you sure you want to eliminate this jerk\nFOREVER AND EVER, with no going back?",
				"Delete Hero?",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				options,
				options[1]);

		//	remove hero from the Database
		if (n == 0) {
			int row = table.getSelectedRow();
			int id = heroesList.get(row).getId();
			db.removeHero(id);
			updateTable();
			hero = null;
			displayHeroStats();
		}

	}



	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayoutManager(4, 4, new Insets(0, 0, 0, 0), -1, -1));
		bPlay = new JButton();
		bPlay.setText("Play");
		mainPanel.add(bPlay, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		final Spacer spacer1 = new Spacer();
		mainPanel.add(spacer1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
		bDeleteHero = new JButton();
		bDeleteHero.setText("-");
		mainPanel.add(bDeleteHero, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(27, 30), null, 0, false));
		bAddHero = new JButton();
		bAddHero.setText("+");
		mainPanel.add(bAddHero, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		bioPane = new JScrollPane();
		mainPanel.add(bioPane, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, new Dimension(-1, 128), 0, false));
		bio = new JTextPane();
		bio.setEditable(false);
		bioPane.setViewportView(bio);
		statsPane = new JScrollPane();
		mainPanel.add(statsPane, new GridConstraints(1, 3, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(256, 256), new Dimension(320, -1), null, 0, false));
		statsPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null));
		stats = new JTextPane();
		stats.setEditable(false);
		Font statsFont = this.$$$getFont$$$(null, -1, -1, stats.getFont());
		if (statsFont != null) stats.setFont(statsFont);
		stats.setText("Select a hero");
		statsPane.setViewportView(stats);
		avatar = new JLabel();
		avatar.setText("");
		mainPanel.add(avatar, new GridConstraints(0, 0, 2, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, new Dimension(256, 256), new Dimension(320, 320), null, 0, false));
		paneLeft = new JScrollPane();
		mainPanel.add(paneLeft, new GridConstraints(2, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
		paneLeft.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null));
		table = new JTable();
		table.setEnabled(true);
		table.setFillsViewportHeight(true);
		Font tableFont = this.$$$getFont$$$("Ayuthaya", -1, 14, table.getFont());
		if (tableFont != null) table.setFont(tableFont);
		paneLeft.setViewportView(table);
		avatar.setLabelFor(bio);
	}

	/**
	 * @noinspection ALL
	 */
	private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
		if (currentFont == null) return null;
		String resultName;
		if (fontName == null) {
			resultName = currentFont.getName();
		} else {
			Font testFont = new Font(fontName, Font.PLAIN, 10);
			if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
				resultName = fontName;
			} else {
				resultName = currentFont.getName();
			}
		}
		return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
	}

	/**
	 * @noinspection ALL
	 */
	public JComponent $$$getRootComponent$$$() {
		return mainPanel;
	}
}
