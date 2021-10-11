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
 * @created on Oct 10, 2003
 * 
 * This class takes care of providing an "intelligent" engineer for doing
 * search during the input data
 * In short, this class:
 * 1. Read data from database and store it in some hashmap correponding
 *    to some specific fields displayed on the user interface 
 * 2. After the database to be updated reload the database again in that hashmaps
 */
public class SEPLookupMapaGeral {
	
	private ArrayList nomeTomadorList = null;
	private ArrayList numItemList = null;
	private ArrayList descServicoList = null;
	private ArrayList dataEmissaoList = null;
	private ArrayList dataCompetenciaList = null;
	private ArrayList numDocList = null;
	private ArrayList tipoDocList = null;
	private ArrayList recBrutaList = null;
	private ArrayList issDevidoList = null;
	private ArrayList recSubstTribList = null;
	private ArrayList issSubstTribList = null;
	private ArrayList tipoLancamentoList = null;
	private ArrayList timestampList = null;
			
	private Connection myConnection;
	private Statement myStatement;

	public SEPLookupMapaGeral( Connection con ){
		try {
			myConnection = con;
			updateSearching();	
			
		}
		catch ( SQLException ignore ) {
			ignore.printStackTrace();
		}
	}
	
	public String[] getNomeTomadorList() {
		String[] tmp = new String[ nomeTomadorList.size() ];
		return ((String[]) nomeTomadorList.toArray( tmp ) );
	}
	
	public String getNomeTomadorList( int index ) {
		return (String) nomeTomadorList.get( index );
	}
	
	
	public String[] getNumItemList() {
		String[] tmp = new String[ numItemList.size() ];
		return ((String[]) numItemList.toArray( tmp ) );
	}
	
	public String getNumItemList( int index ) {
		return (String) numItemList.get( index );
	}
	
	public String[] getDescServicoList() {
		String[] tmp = new String[ descServicoList.size() ];
		return ((String[]) descServicoList.toArray( tmp ) );
	}
	
	public String getDescServicoList( int index ) {
		return (String) descServicoList.get( index );
	}
	
	
	public String[] getDataEmissaoList() {
		String[] tmp = new String[ dataEmissaoList.size() ];
		return ((String[]) dataEmissaoList.toArray( tmp ) );
	}
	
	public String getDataEmissaoList( int index ) {
		return (String) dataEmissaoList.get( index );
	}

	public String[] getDataCompetenciaList() {
		String[] tmp = new String[ dataCompetenciaList.size() ];
		return ((String[]) dataCompetenciaList.toArray( tmp ) );
	}
	
	public String getDataCompetenciaList( int index ) {
		return (String) dataCompetenciaList.get( index );
	}
	
	
	public String[] getNumDocList() {
		String[] tmp = new String[ numDocList.size() ];
		return ((String[]) numDocList.toArray( tmp ) );
	}
	
	public String getNumDocListList( int index ) {
		return (String) numDocList.get( index );
	}
	
	
	public String[] getTipoDocList() {
		String[] tmp = new String[ tipoDocList.size() ];
		return ((String[]) tipoDocList.toArray( tmp ) );
	}
	
	public String getTipoDocList( int index ) {
		return (String) tipoDocList.get( index );
	}
	
	public String[] getRecBrutaList() {
		String[] tmp = new String[ recBrutaList.size() ];
		return ((String[]) recBrutaList.toArray( tmp ) );
	}
	
	public String getRecBrutaList( int index ) {
		return (String) recBrutaList.get( index );
	}
	
	public String[] getIssDevidoList() {
		String[] tmp = new String[ issDevidoList.size() ];
		return ((String[]) issDevidoList.toArray( tmp ) );
	}
	
	public String getIssDevidoList( int index ) {
		return (String) issDevidoList.get( index );
	}
	
	
	public String[] getRecSubstTribList() {
		String[] tmp = new String[ recSubstTribList.size() ];
		return ((String[]) recSubstTribList.toArray( tmp ) );
	}
	
	public String getRecSubstTribList( int index ) {
		return (String) recSubstTribList.get( index );
	}
	

	public String[] getIssSubstTribList() {
		String[] tmp = new String[ issSubstTribList.size() ];
		return ((String[]) issSubstTribList.toArray( tmp ) );
	}
	
	public String getIssSubstTribList( int index ) {
		return (String) issSubstTribList.get( index );
	}
	

	public String[] getTipoLancamentoList() {
		String[] tmp = new String[ tipoLancamentoList.size() ];
		return ((String[]) tipoLancamentoList.toArray( tmp ) );
	}
	
	public String getTipoLancamentoList( int index ) {
		return (String) tipoLancamentoList.get( index );
	}
	

	public String[] getTimestamp() {
		String[] tmp = new String[ timestampList.size() ];
		return ((String[]) timestampList.toArray( tmp ) );
	}
	
	public String getTimestamp( int index ) {
		return (String) timestampList.get( index );
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
		
		String query = "SELECT tomadorMapaGeral, itemListaMapaGeral, " +
				   " descServicoMapaGeral, " + 
				   " dataEmissaoMapaGeral, dataCompetenciaMapaGeral, " +
				   " numeroDocMapaGeral, tipoDocumentoMapaGeral," +
				   " recEscritMapaGeral, issDevidoMapaGeral, " +
				   " recSubstTribMapaGeral, issSubstMapaGeral, " +
 		           " tipoRecMapaGeral, seqMapaGeral " +   	                   
				   " FROM tabMapaGeral " + 
				   " WHERE osMapaGeral='" + currOS + "'" +
				   " AND cmcMapaGeral='" + currCMC + "'" +
				   " ORDER BY  dataCompetenciaMapaGeral, tomadorMapaGeral " ;
		

		ResultSet rs =  myStatement.executeQuery( query );
		// Getting the records of the database
		createLookupFileMapaGeralTable( rs );
	}
	
	/**
	 * Creates some hashmap to help in the typing during the input data
	 * on the user interface
	 * 
	 * @param <code>rs</code> Indicates the result set oppened 
	 */
	private void createLookupFileMapaGeralTable( ResultSet rs ) {
	    
		try {

			nomeTomadorList = new ArrayList();
			numItemList = new ArrayList();
			descServicoList = new ArrayList();
			dataEmissaoList = new ArrayList();
			dataCompetenciaList = new ArrayList();
			numDocList = new ArrayList();
			tipoDocList = new ArrayList();
			recBrutaList = new ArrayList();
			issDevidoList = new ArrayList();
			recSubstTribList = new ArrayList();
			issSubstTribList = new ArrayList();
			tipoLancamentoList = new ArrayList();
			timestampList = new ArrayList();
			
	   
			/**
			 * Read all database and populate specified hashmaps to records
			 * previously read. This hashmap will be used to create comboboxes
			 * containing values stored in the database
			 */
			 while ( rs.next() ) {
	        	
				 String nomeTomador = rs.getString( 1 );
				 String numItem = rs.getString( 2 );
				 String descServico = rs.getString( 3 );
				 String dataEmissao = rs.getString( 4 );
				 String dataCompetencia = rs.getString( 5 );
				 String numDoc = rs.getString( 6 );
				 String tipoDoc = rs.getString( 7 );
				 String recBruta = rs.getString( 8 );
				 String issDevido = rs.getString( 9 );
				 String recSubstTrib = rs.getString( 10 );
				 String issSubstTrib = rs.getString( 11 );
				 String tipoLancamento = rs.getString( 12 );
				 String timestamp = rs.getString( 13 );
				 
				 nomeTomadorList.add( nomeTomador );
				 numItemList.add( numItem );
				 descServicoList.add( descServico );
				 dataEmissaoList.add( dataEmissao );
				 dataCompetenciaList.add( dataCompetencia );
				 numDocList.add( numDoc );
				 tipoDocList.add( tipoDoc );
				 recBrutaList.add( recBruta );
				 issDevidoList.add( issDevido );
				 recSubstTribList.add( recSubstTrib );
				 issSubstTribList.add( issSubstTrib );
				 tipoLancamentoList.add( tipoLancamento );
				 timestampList.add( timestamp );
	    	    
			 }
		}    
		catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		}	
		
		
	}

}
