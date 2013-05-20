/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.urlshortener.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Seb
 */
public class ConnectSql {

    private static final String DRIVER = "org.postgresql.Driver";
    private static final String ADRESS_POSTGRES = "jdbc:postgresql://localhost/test";
    private static final String USER = "myuser";
    private static final String PASSWD = "123456";
    
    private Connection connect = null;
    
    public void connectDBSQL() {
        
        System.out.println("-------- PostgreSQL" 
                + " JDBC Test Connexion ------------");
        
        try {
            
            Class.forName(DRIVER);
            
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your PostgreSQL JDBC Driver? "
                    + "Include in your library path!");
            e.printStackTrace();
            return;
        }
        
        System.out.println("PostgreSQL JDBC Driver Registered!");
        
        try {
        
            connect = DriverManager.getConnection(ADRESS_POSTGRES);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }
        
        if (connect != null) {
            System.out.println("You made it, take control your database now!");
        }else{
            System.out.println("Failed to make connection");
        }
        
        
    
    }
    
}
