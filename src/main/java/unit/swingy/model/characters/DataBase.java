package unit.swingy.model.characters;

import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.h2.jdbc.JdbcSQLNonTransientConnectionException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

public class DataBase {

	private static DataBase instance;

	public static DataBase getInstance() {
		if (instance == null)
			instance = new DataBase();
		return instance;
	}

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.h2.Driver";
	static final String DB_URL = "jdbc:h2:./src/main/resources/HeroesDB/HeroesH2";
	//  Database credentials
	static final String USER = "user";
	static final String PASS = "";

	private Connection connection = null;
	private Statement statement = null;

	private DataBase() {
		connectToDB();
	}

	public void connectToDB() {

		try {
			// STEP 1: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			//STEP 2: Open a connection
			System.out.println(">>> Connecting to database...");
			connection = DriverManager.getConnection(DB_URL + ";IFEXISTS=TRUE", USER, PASS);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException esql) {
			resetDB();
		}
	}

	private void resetDB() {
		System.out.println(">>> Making a new DataBase...");

		try {

			//read sql script to a String Buffer
			FileReader fr = new FileReader(new File("./src/main/resources/HeroesDB/createTable.sql"));
			BufferedReader br = new BufferedReader(fr);
			StringBuffer sb = new StringBuffer();
			String str;
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
			br.close();

			//split script to separate sql statements
			String[] commands = sb.toString().split(";");

			//create database
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement();

			//execute statements, ignoring possible empty ones
			for (String s : commands) {
				if (s.trim().length() > 0) {
					statement.execute(s);
				}
			}

		} catch (JdbcSQLNonTransientConnectionException e) {
			System.err.println("Hero Database is unavailable. Close all other instances of the game.");
			System.exit(-1);
		} catch (SQLException esql) {
			esql.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Hero> getHeroesList(HeroBuilder builder) {

		ArrayList<Hero> heroesList = new ArrayList<>();

		try {
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM HEROES");

			while (rs.next()) {
				builder.setId(rs.getInt("id"));
				builder.setName(rs.getString("name"));
				builder.setClas(rs.getString("class"));
				builder.setLevel(rs.getInt("level"));
				builder.setExp(rs.getInt("exp"));
				builder.setHp(rs.getInt("hp"));
				builder.setAttack(rs.getInt("attack"));
				builder.setDefence(rs.getInt("defence"));
				builder.setWeapon(rs.getInt("weapon"));
				builder.setArmor(rs.getInt("armor"));
				builder.setHelm(rs.getInt("helm"));
				heroesList.add(builder.getHero());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return heroesList;
	}

	public void addHero(Hero h) {

		int wepPow = (h.getWeapon() != null) ? h.getWeapon().getPower() : 0;
		int armPow = (h.getArmor() != null) ? h.getArmor().getPower() : 0;
		int helmPow = (h.getHelm() != null) ? h.getHelm().getPower() : 0;

		String sql = "INSERT INTO Heroes (name, class, level, exp, hp, attack, defence, weapon, armor, helm) " +
						"VALUES (\'" +
						h.getName() + "\', \'" +
						h.getClas() + "\', " +
						h.getLevel() + ", " +
						h.getExp() + ", " +
						h.getMaxHp() + ", " +
						h.getAttack() + ", " +
						h.getDefence() + ", " +
						wepPow + ", " +
						armPow + ", " +
						helmPow + ");";
		try {
			statement.executeUpdate(sql);
		} catch (JdbcSQLIntegrityConstraintViolationException e) {
			System.err.println("Hero's name must be unique!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void  removeHero(int id) {
		String sql = "DELETE FROM Heroes WHERE id = " + id;
		try {
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void closeConnection() {
		System.out.println(">>> Closing DB connection...");
		try {
			if (statement != null)
				statement.close();
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public void updateHero(Hero h) {

		int wepPow = (h.getWeapon() != null) ? h.getWeapon().getPower() : 0;
		int armPow = (h.getArmor() != null) ? h.getArmor().getPower() : 0;
		int helmPow = (h.getHelm() != null) ? h.getHelm().getPower() : 0;

		String sql = "UPDATE Heroes SET " +
						"level = " + h.getLevel() +
						", exp = " + h.getExp() +
						", hp = " + h.getMaxHp() +
						", attack = " + h.getAttack() +
						", defence = " + h.getDefence() +
						", weapon = " + wepPow +
						", armor = " + armPow +
						", helm = " + helmPow +
					" WHERE id = " + h.getId();
		try {
			statement = connection.createStatement();
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}



	/*	====================  FOR DEBUGGING ========================	*/

	public String[] getColumnNames() {
		String[] columnNames = null;
		try {
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM Heroes");
			columnNames = new String[rs.getMetaData().getColumnCount()];
			for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
				columnNames[i - 1] = rs.getMetaData().getColumnName(i);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return columnNames;
	}

	private void printTable() {

		System.out.println("====================DB=======================");
		try {
			statement = connection.createStatement();

			ResultSet rs = statement.executeQuery("SHOW TABLES;");
			while (rs.next()) {
				for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
					System.out.print(" " + rs.getMetaData().getColumnName(i) + "=" + rs.getObject(i));
					System.out.println();
				}
			}

			rs = statement.executeQuery("SELECT * FROM heroes");
			while (rs.next()) {
				for (int i = 1; i < rs.getMetaData().getColumnCount() + 1; i++) {
					System.out.print(" " + rs.getMetaData().getColumnName(i) + "=" + rs.getObject(i));
					System.out.println();
				}
				System.out.println("---------------------");
			}

		} catch (SQLException esql) {
			esql.printStackTrace();
		}

	}


}
