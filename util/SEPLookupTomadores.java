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

//PENDENCIA: 
// 1. Implementar rotina para atualizar hashmap quando ocorrer mudança
//    do banco de dados
// 2. Fazer buscar de tomadores indepedentemente da ordem de serviço aberta
// 3. Testar função em SEPFormTabTomadores
//

/**
 * @author Francisco Carlos
 * @created on Sept 04, 2003
 * 
 * This class takes care of providing an "intelligent" engineer for doing
 * search during the input data
 * In short, this class:
 * 1. Read data from database and store it in some hashmap correponding
 *    to some specific fields displayed on the user interface 
 * 2. After the database to be updated reload the database again in that hashmaps
 */
public class SEPLookupTomadores {
	
	private static final int LOCAL = 1;
	private Connection myConnection;
	private Statement myStatement;
//	private HashMap cmcTomadorMap = new HashMap();
//	private HashMap nomeTomadorMap = new HashMap();
//	private HashMap cnpjTomadorMap = new HashMap();
	
	private ArrayList cmcTomadorList = new ArrayList();
	private ArrayList nomeTomadorList = new ArrayList();
	private ArrayList cnpjTomadorList = new ArrayList();	
	private ArrayList dataEmissaoList = new ArrayList();
	private ArrayList tipoDocList = new ArrayList();
	private ArrayList serieList = new ArrayList();
	private ArrayList subSerieList = new ArrayList();
	private ArrayList numDocList = new ArrayList();
	private ArrayList valorDocList = new ArrayList();
	private ArrayList timestampList = new ArrayList();	
	
	public SEPLookupTomadores(Connection con ){
		try {
			myConnection = con;
			myStatement = con.createStatement();
			
			String currOS = EmpresaAtual.getNumeroOS();
			String currCMC = EmpresaAtual.getCMCEmpresa();
			
			String query = null;
			query = " SELECT cmcTomadorMapaTomador, nomeMapaTomador, " +
							   " cnpjMapaTomador, dataEmissaoMapaTomador, " +
							   " tipoDocMapaTomador, serieMapaTomador, " +
							   " subSerieMapaTomador, numeroDocMapaTomador, " +
							   " valorDocMapaTomador, seqMapaTomador " +
							   " FROM tabMapaTomador " +
							   " WHERE osMapaTomador ='" + currOS + "'" +
							   "  AND cmcPrestadorMapaTomador ='" + currCMC + "'" +
							   " ORDER BY cmcTomadorMapaTomador ";
			              
			ResultSet rs =  myStatement.executeQuery( query );
			// Getting the records of the database
			createLookupFileTomadoresTable( rs );	
			
		}
		catch ( SQLException ignore ) {
			ignore.printStackTrace();
		}
	}
	
	
/*	public String[] getCmcTomadoresList() {
		String[] tmp = new String[ cmcTomadorList.size() ];
		return ((String[]) cmcTomadorList.toArray( tmp ) );
	}

	public String[] getNomeTomadoresList() {
		String[] tmp = new String[ nomeTomadorList.size() ];
		return ((String[]) nomeTomadorList.toArray( tmp ) );
	}

	public String[] getCnpjTomadoresList() {
		String[] tmp = new String[ cnpjTomadorList.size() ];
		return ((String[]) cnpjTomadorList.toArray( tmp ) );
	}  */
	
	/**
	 * Returns CMC of the companies that got some service
	 * @return A <code>String[]</code> holds all key fields from companies	 
	 */
	/*public String[] getCmcTomadores() {
		String[] tmp = new String[ cmcTomadorMap.size() ];
		return ((String[])cmcTomadorMap.keySet().toArray( tmp ));
	}*/
	
	/**
	 * Returns name of the companies that got some service
	 * @return A <code>String[]</code> holding all names from the companies
	 */	
	/*public String[] getNomeTomadores(){
		String[] tmp = new String[ nomeTomadorMap.size() ];
		return ((String[])nomeTomadorMap.keySet().toArray(tmp));
	} */
	
	
	/**
	 * Returns cnpj of the companies that got some service
	 * @return A <code>String[]</code> holding all cnpk from the companies
	 */
	/*public String[] getCnpjTomadores() {
		String[] tmp = new String[ cnpjTomadorMap.size() ];
		return ((String[])cnpjTomadorMap.keySet().toArray( tmp) );
	} */
	


	public String[] getCmcTomadorList() {
		String[] tmp = new String[ cmcTomadorList.size() ];
		return ((String[]) cmcTomadorList.toArray( tmp ) );
	}
	
	public String getCmcTomadorList( int index ) {
		return (String) cmcTomadorList.get( index );
	}
	
	public String[] getNomeTomadorList() {
		String[] tmp = new String[ nomeTomadorList.size() ];
		return ((String[]) nomeTomadorList.toArray( tmp ) );
	}
	
	public String getNomeTomadorList( int index ) {
		return (String) nomeTomadorList.get( index );
	}
	
	public String[] getCnpjTomadorList() {
		String[] tmp = new String[ cnpjTomadorList.size() ];
		return ((String[]) cnpjTomadorList.toArray( tmp ) );
	}
	
	public String getCnpjTomadorList( int index ) {
		return (String) cnpjTomadorList.get( index );
	}


	public String[] getDataEmissaoList() {
		String[] tmp = new String[ dataEmissaoList.size() ];
		return ((String[]) dataEmissaoList.toArray( tmp ) );
	}
	
	public String getDataEmissaoList( int index ) {
		return (String) dataEmissaoList.get( index );
	}
	
	public String[] getDataTipoDocList() {
		String[] tmp = new String[ tipoDocList.size() ];
		return ((String[]) tipoDocList.toArray( tmp ) );
	}
	
	public String getTipoDocList( int index ) {
		return (String) tipoDocList.get( index );
	}
	
	public String[] getSerieList() {
		String[] tmp = new String[ serieList.size() ];
		return ((String[]) serieList.toArray( tmp ) );
	}
	
	public String getSerieList( int index ) {
		return (String) serieList.get( index );
	}
	
	public String[] getSubSerieList() {
		String[] tmp = new String[ subSerieList.size() ];
		return ((String[]) subSerieList.toArray( tmp ) );
	}
	
	public String getSubSerieList( int index ) {
		return (String) subSerieList.get( index );
	}
	
	public String[] getNumDocList() {
		String[] tmp = new String[ numDocList.size() ];
		return ((String[]) numDocList.toArray( tmp ) );
	}
	
	public String getNumDocList( int index ) {
		return (String) numDocList.get( index );
	}

	public String[] getValorDocList() {
		String[] tmp = new String[ valorDocList.size() ];
		return ((String[]) valorDocList.toArray( tmp ) );
	}
	
	public String getValorDocList( int index ) {
		return (String) valorDocList.get( index );
	}
	
	public String[] getTimestamp() {
		String[] tmp = new String[ timestampList.size() ];
		return ((String[]) timestampList.toArray( tmp ) );
	}
	
	public String getTimestamp( int index ) {
		return (String) timestampList.get( index );
	}
	
	

	
	/**
	 * Returns the position of  a specified cmc in the underlying hashmap 
	 * @param A <code>cmcTomador</code> indicates the number of 
	 * @return <code>int</code> indicates the position of key field in the hasmap
	 */
/*	public int findPosCmcTomadores( String cmcTomador ){		
		Integer dummy = (Integer) cmcTomadorMap.get( cmcTomador );
		int retVal = dummy.intValue();
		return retVal;		
	} */

	/**
	 * Returns the position of  a specified cmc in the underlying hashmap 
	 * @param A <code>cmcTomador</code> indicates the number of 
	 * @return <code>int</code> indicates the position of key field in the hasmap
	 */
/*	public int findPosNomeTomadores( String nomeTomador ){		
		Integer dummy = (Integer) nomeTomadorMap.get( nomeTomador );
		int retVal = dummy.intValue();
		return retVal;		
	} */ 
	
	/**
	 * Returns the position of  a specified cmc in the underlying hashmap 
	 * @param A <code>cmcTomador</code> indicates the number of 
	 * @return <code>int</code> indicates the position of key field in the hasmap
	 */
/*	public int findPosCnpjTomadores( String cnpjTomador ){		
		Integer dummy = (Integer) cnpjTomadorMap.get( cnpjTomador );
		int retVal = dummy.intValue();
		return retVal;		
	} */		
	
	/**
	 * Creates some hashmap to help in the typing during the input data
	 * on the user interface
	 * 
	 * @param <code>rs</code> Indicates the result set oppened 
	 */
	private void createLookupFileTomadoresTable( ResultSet rs ) {
		int posRec = 0;
	   
		try {	   	
	   
			/**
			 * Read all database and populate specified hashmaps to records
			 * previously read. This hashmap will be used to create comboboxes
			 * containing values stored in the database
			 */
			 while ( rs.next() ) {
	        	
				 String cmcTomador = rs.getString( 1 );
				 String nomeTomador = rs.getString( 2 );
				 String cnpjTomador = rs.getString( 3 );
				 String dataEmissao = rs.getString( 4 );
				 String tipoDoc = rs.getString( 5 );
				 String serie = rs.getString( 6 );
				 String subSerie = rs.getString( 7 );
				 String numDoc = rs.getString( 8 );
				 String valorDoc = rs.getString( 9 );
				 String timestamp = rs.getString( 10 );
				 
				 
//				 posRec++;
				 
//				 cmcTomadorMap.put( cmcTomador, new Integer( posRec) );
//				 nomeTomadorMap.put( nomeTomador, new Integer( posRec)  );
//				 cnpjTomadorMap.put( cnpjTomador, new Integer( posRec) );				 
				 
				 cmcTomadorList.add( cmcTomador );
				 nomeTomadorList.add( nomeTomador );
				 cnpjTomadorList.add( cnpjTomador );				 
				 dataEmissaoList.add( dataEmissao );
				 tipoDocList.add( tipoDoc );
				 serieList.add( serie );
				 subSerieList.add( subSerie );
				 numDocList.add( numDoc );
				 valorDocList.add( valorDoc );
				 timestampList.add( timestamp );
	    	    
			 }
		}    
		catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		}	
		
		
	}
	

}
