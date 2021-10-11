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

package sep.util;
import sep.EmpresaAtual;
import java.util.*;
import java.sql.*;

/**
 * @author Francisco Carlos
 * @created on Sept 29, 2003
 * 
 * This class takes care of providing an "intelligent" engineer for doing
 * search during the input data
 * In short, this class:
 * 1. Read data from database and store it in some hashmap correponding
 *    to some specific fields displayed on the user interface 
 * 2. After the database to be updated reload the database again in that hashmaps
 */
public class SEPLookupMapaEscolar {
	private ArrayList nomeCursoList = new ArrayList();
	private ArrayList anoRefList = new ArrayList();
	private Connection myConnection;
	private Statement myStatement;


	public SEPLookupMapaEscolar(Connection con){
		try {
			myConnection = con;
			updateSearching();	
			
		}
		catch ( SQLException ignore ) {
			ignore.printStackTrace();
		}
	}
	
	public String[] getNomeCursosList() {
		String[] tmp = new String[ nomeCursoList.size() ];
		return ((String[]) nomeCursoList.toArray( tmp ) );
	}
	
	public String[] getAnosRef() {
		String[] tmp = new String[ anoRefList.size() ];
		return ((String[]) anoRefList.toArray( tmp ) );
	}
	
	/**
	 * Reloads database in order to reflect the most recently updates done
	 * 
	 * @throws <code>SQLException</code> thrown if an unexpected error to access
	 *                                   database happened 
	 */
	public void updateSearching() throws SQLException {
		String currOS = EmpresaAtual.getNumeroOS();
		String currCMC = EmpresaAtual.getCMCEmpresa();
		
		myStatement = myConnection.createStatement();
		
		String query = " SELECT cursoMapaEnsino, exercicioMapaEnsino " +
					   " FROM tabMapaEnsino " +
					   " WHERE osMapaEnsino ='" + currOS + "'" +
					   "  AND cmcMapaEnsino ='" + currCMC + "'" +
					   " ORDER BY cursoMapaEnsino ";

		ResultSet rs =  myStatement.executeQuery( query );
		// Getting the records of the database
		createLookupFileMapaEscolarTable( rs );		
	}
	
	
	
	
	
	/**
	 * Creates some hashmap to help in the typing during the input data
	 * on the user interface
	 * 
	 * @param <code>rs</code> Indicates the result set oppened 
	 */
	private void createLookupFileMapaEscolarTable( ResultSet rs ) {
	   
		try {	   	
	   
			/**
			 * Read all database and populate specified hashmaps to records
			 * previously read. This hashmap will be used to create comboboxes
			 * containing values stored in the database
			 */
			 while ( rs.next() ) {
	        	
				 String nomeCurso = rs.getString( 1 );
				 String anoRef = rs.getString( 2 );
				 
				 nomeCursoList.add( nomeCurso );
				 anoRefList.add( anoRef );
	    	    
			 }
		}    
		catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		}	
		
		
	}
	
	


}
