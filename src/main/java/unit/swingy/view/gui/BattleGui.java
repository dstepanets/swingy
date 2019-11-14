package unit.swingy.view.gui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import darrylbu.icon.StretchIcon;
import unit.swingy.controller.Game;
import unit.swingy.model.characters.Enemy;
import unit.swingy.model.characters.Hero;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

public class BattleGui {

	private Game game;
	private Hero hero;
	private Enemy enemy;

	private JFrame frame;
	private JPanel battlePanel;
	private JLabel enemyAvatar;
	private JLabel heroAvatar;
	private JScrollPane logPane;
	private JTextPane log;
	private JProgressBar enemyHP;
	private JProgressBar heroHP;
	private JButton bDice;
	private JLabel dice;
	private JButton bExit;
	private JLabel enemyDefence;
	private JLabel enemyAttack;
	private JLabel heroAttack;
	private JLabel heroDefence;
	private JLabel enemyClass;
	private JLabel heroClass;

	//	TODO: Fix all windows sizes!!!
	BattleGui(Hero h, Enemy e) {

		game = Game.getInstance();
		hero = h;
		enemy = e;

		$$$setupUI$$$();
		initComponents();
		createListeners();
		initFrame();

		battleIntro();

	}

	private void initComponents() {
		heroClass.setText(hero.getName() + ", " + hero.getClas().getClassName() + " (" + hero.getLevel() + " lvl)");
		enemyClass.setText(enemy.getClas().getClassName() + " (" + enemy.getLevel() + " lvl)");
		heroAttack.setText("Attack: " + hero.getAttack());
		enemyAttack.setText("Attack: " + enemy.getAttack());
		heroDefence.setText("Defence: " + hero.getDefence());
		enemyDefence.setText("Defence: " + enemy.getDefence());
		heroHP.setMaximum(hero.getMaxHp());
		enemyHP.setMaximum(enemy.getMaxHp());
		updateStats();

		heroAvatar.setIcon(hero.getClas().getAvatar());
		enemyAvatar.setIcon(enemy.getClas().getAvatar());
		dice.setPreferredSize(new Dimension(32, 32));
		dice.setIcon(new StretchIcon("src/main/resources/img/dice/dice.png"));
	}

	void updateStats() {
		heroHP.setValue(hero.getHp());
		enemyHP.setValue(enemy.getHp());
		heroHP.setString("HP: " + hero.getHp() + "/" + hero.getMaxHp());
		enemyHP.setString("HP: " + enemy.getHp() + "/" + enemy.getMaxHp());
	}

	private void initFrame() {

		frame = new JFrame("Battle");
		frame.setContentPane(battlePanel);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);	// center window on the screen
		frame.setResizable(false);
		frame.setVisible(true);
	}

	private void createListeners() {

		bDice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				random int from 1 to 6
				int diceNum = new Random().nextInt(6) + 1;
				showDice(diceNum);
				bDice.setEnabled(false);
				game.battle(diceNum);
			}
		});

	}

	void logMessage(String msg, SimpleAttributeSet style) {

		Document doc = log.getStyledDocument();
		try {
			doc.insertString(doc.getLength(), msg + "\n", style);
			log.setCaretPosition(doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void battleIntro() {
		logMessage("Roll the dice. Strength of your attacks depends on the result.", TextStyle.norm);
	}

	private void showDice(int num) {
		StretchIcon icon = new StretchIcon("src/main/resources/img/dice/" + num + ".png");
		dice.setIcon(icon);
		if (num > 3)
			logMessage("Your attacks are stronger.", TextStyle.bold);
		else
			logMessage("Your attacks are weaker.", TextStyle.bold);
	}

//	allow closing the window
	public void enableExit(final int expReward) {

//		close window with a standard cross button
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				if (expReward > 0)
					game.getGui().winBattle(expReward);
				else
					game.youDie();
			}
		});

//		close window with a custom button
		bExit.setEnabled(true);
		bExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (expReward > 0)
					game.getGui().winBattle(expReward);
				else
					game.youDie();

				frame.dispose();
			}
		});


	}


	/**
	 * Method generated by IntelliJ IDEA GUI Designer
	 * >>> IMPORTANT!! <<<
	 * DO NOT edit this method OR call it in your code!
	 *
	 * @noinspection ALL
	 */
	private void $$$setupUI$$$() {
		battlePanel = new JPanel();
		battlePanel.setLayout(new GridLayoutManager(4, 7, new Insets(0, 0, 0, 0), -1, -1));
		enemyAvatar = new JLabel();
		enemyAvatar.setText("");
		battlePanel.add(enemyAvatar, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(256, 256), null, 0, false));
		heroAvatar = new JLabel();
		heroAvatar.setText("");
		battlePanel.add(heroAvatar, new GridConstraints(0, 5, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(256, 256), null, 0, false));
		logPane = new JScrollPane();
		battlePanel.add(logPane, new GridConstraints(0, 2, 3, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(400, 320), null, 0, false));
		log = new JTextPane();
		log.setEditable(false);
		logPane.setViewportView(log);
		enemyHP = new JProgressBar();
		enemyHP.setStringPainted(true);
		battlePanel.add(enemyHP, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		heroHP = new JProgressBar();
		heroHP.setStringPainted(true);
		battlePanel.add(heroHP, new GridConstraints(3, 5, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		bDice = new JButton();
		bDice.setForeground(new Color(-4521472));
		bDice.setHideActionText(false);
		bDice.setText("Roll the Dice");
		battlePanel.add(bDice, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		dice = new JLabel();
		dice.setText("");
		battlePanel.add(dice, new GridConstraints(3, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		bExit = new JButton();
		bExit.setEnabled(false);
		bExit.setText("Exit");
		battlePanel.add(bExit, new GridConstraints(3, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		enemyAttack = new JLabel();
		enemyAttack.setText("Attack");
		battlePanel.add(enemyAttack, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		enemyDefence = new JLabel();
		enemyDefence.setText("Defence");
		battlePanel.add(enemyDefence, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		heroAttack = new JLabel();
		heroAttack.setText("Attack");
		battlePanel.add(heroAttack, new GridConstraints(2, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		heroDefence = new JLabel();
		heroDefence.setText("Defence");
		battlePanel.add(heroDefence, new GridConstraints(2, 6, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		enemyClass = new JLabel();
		Font enemyClassFont = this.$$$getFont$$$(null, Font.BOLD, -1, enemyClass.getFont());
		if (enemyClassFont != null) enemyClass.setFont(enemyClassFont);
		enemyClass.setText("Class");
		battlePanel.add(enemyClass, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
		heroClass = new JLabel();
		Font heroClassFont = this.$$$getFont$$$(null, Font.BOLD, -1, heroClass.getFont());
		if (heroClassFont != null) heroClass.setFont(heroClassFont);
		heroClass.setText("Class");
		battlePanel.add(heroClass, new GridConstraints(1, 5, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
		return battlePanel;
	}

}
