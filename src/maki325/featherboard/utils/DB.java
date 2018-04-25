package maki325.featherboard.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import maki325.featherboard.Featherboard;

public class DB {

	public static Connection getConnection() {
		return Featherboard.instance.getConnection();
	}
	
	public static void update(String... args) {
		
		String totalUpdateMessage = "";
		for(int i = 0; i < args.length; i++) {
			totalUpdateMessage += args[i];
		}
		
		Statement s = null;
		try {
			s = getConnection().createStatement();
		} catch (SQLException e) {
		}
		
		if(s!=null) {
			
			try {
				s.executeUpdate(totalUpdateMessage);
			} catch (SQLException e) {
			}
			
		}
		
	}
	
	public static ResultSet query(String... args) {
		
		String totalUpdateMessage = "";
		for(int i = 0; i < args.length; i++) {
			totalUpdateMessage += args[i];
		}
		
		Statement s = null;
		try {
			s = getConnection().createStatement();
		} catch (SQLException e) {
			System.out.println("Something Went Wrong With The Statement");
			e.printStackTrace();
		}
		
		if(s!=null) {
			
			try {
				ResultSet r = s.executeQuery(totalUpdateMessage);
				return r;
			} catch (SQLException e) {
				System.out.println("Something Went Wrong With ResultSet");
				e.printStackTrace();
			}
			
		}
		
		
		return null;
		
	}
	
	public static Boolean next(ResultSet r) {
		try {
			if(r.next()) {
				return true;
			}
		} catch (SQLException e) {
		}
		return false;
	}
	
	public static Object value(ResultSet r, String columnName) {
		
		try {
			return r.getObject(columnName);
		} catch (SQLException e) {
		}
		
		return null;
	}
	
}
