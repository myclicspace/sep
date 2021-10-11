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

import java.io.*;
import java.util.*; 

class PrintReport {   
   public static void printReport( String report ) throws IOException {
       // Get the command
       String cmd ;
       cmd = "c:\\sep\\Iexplore.exe " + report;            
           
       // Execute command
       Runtime runtime = Runtime.getRuntime();
       java.lang.Process pr = runtime.exec(cmd);
   }
   	
	public static String paragrafo() {
		
		String p = "";
		for ( int i = 1; i < 10; i = i + 1) {
			p = p + "&nbsp;";
		}
		return p;	
	}		
	
	public static String insereBR( String texto ) {
		
		String p = paragrafo();
		StringTokenizer st = new StringTokenizer( texto );
     	while ( st.hasMoreTokens() ) {
         	p = p + st.nextToken( "\n" ) + "<br>" + paragrafo();
     	}
		return p;	
	}		
	

}  
