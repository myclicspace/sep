/**
 * Copyright (C) 2003, 2021 by Francisco Carlos   (hi dev ;-) have funny , pywarrior :-)at yahoo.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 **/


package sep;

//import javax.swing.*;
//import javax.swing.border.*;
//import java.awt.*;
//import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.sql.*;

public class MakeDB {
	
    private Connection con;
    
    public MakeDB() {
    	con = openConnection();
    }
	
    private Connection openConnection() {
  	    Connection myCon = null;
  	    String driver = "org.gjt.mm.mysql.Driver";
  	    String driverUrl = "jdbc:mysql://localhost/sep";
  	    String user ="root";
  	    String pwd = "";
  	    try {  	
  	
		    Class.forName(driver);
		    if (myCon==null) {
			    myCon = DriverManager.getConnection( driverUrl, user, pwd );
		    }  	
        }
        catch ( Exception e ) {
    	    e.printStackTrace();
        } 	
    
   	    return myCon;
     }
	
	
    private void insertLineDB( String line ) throws SQLException {
    	
    	System.out.println( " line = " + line );
    	
        StringTokenizer st = new StringTokenizer( line, "\t");
        String item = st.nextToken();
        String desc = st.nextToken();      
        String aliquota = st.nextToken();        
        

		String cmd = "INSERT INTO tabServicos "
		            + " VALUES  ('"  + item + "','" 
		                        + desc + "','"  
		                        + aliquota + " ')" ;
		
		Statement stmt = con.createStatement();	               
		stmt.executeUpdate( cmd );
		con.commit();		
		
        
        
        String sql = null;


    }
    
    

    public static void main( String[] args ) {
    	
    	MakeDB makeDB = new MakeDB();

        try {
    	
            File f = new File("C:\\LIXO\\servicos.TXT");
            BufferedReader br = new BufferedReader( new FileReader( f ) );            
            
            while ( true ) {
                String line = br.readLine();            	
            	if ( line == null ) break;
                makeDB.insertLineDB( line );
            }
            
            br.close();
            
        }
        catch (Exception e ) {
            System.err.println ( e );
        }
   }

}

