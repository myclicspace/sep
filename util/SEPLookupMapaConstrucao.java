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
 * @created on Sept 30, 2003
 * 
 * This class takes care of providing an "intelligent" engineer for doing
 * search during the input data
 * In short, this class:
 * 1. Read data from database and store it in some hashmap correponding
 *    to some specific fields displayed on the user interface 
 * 2. After the database to be updated reload the database again in that hashmaps
 */
public class SEPLookupMapaConstrucao {
	private ArrayList nomeContratanteList = null;
	private ArrayList descServicoList = null;;
	private ArrayList dataCompetenciaList = null;
	private ArrayList itemServicoList = null;
	private ArrayList localExecucaoList = null;
	private ArrayList numeroDocumentoList = null;
	private ArrayList dataEmissaoList = null;
	private ArrayList receitaBrutaList = null;
	private ArrayList subEmpreitadaList = null;
	private ArrayList obrasForaMunicList = null;
	private ArrayList materialAplicadoList = null;
	private ArrayList recTributavelList = null;
	private ArrayList tipoRecList = null;
	private ArrayList tipoDocList = null;
	private ArrayList tipoDedList = null;
	private ArrayList percentualList = null;
	private ArrayList tipoLancamentoList = null;
	private ArrayList timestampList = null;
	
	private Connection myConnection;
	private Statement myStatement;

	public SEPLookupMapaConstrucao(Connection con){
		try {
			myConnection = con;
			updateSearching();	
			
		}
		catch ( SQLException ignore ) {
			ignore.printStackTrace();
		}
	}
	
	public String[] getNomeContratanteList() {
		String[] tmp = new String[ nomeContratanteList.size() ];
		return ((String[]) nomeContratanteList.toArray( tmp ) );
	}
	
	
	public String[] getDescServicoList() {
		String[] tmp = new String[ descServicoList.size() ];
		return ((String[]) descServicoList.toArray( tmp ) );
	}
	
	public String getDescServicoList( int index ) {
		return (String) descServicoList.get( index );
	}
	
	public String[] getItemServicoList() {
		String[] tmp = new String[ itemServicoList.size() ];
		return ((String[]) itemServicoList.toArray( tmp ) );
	}
	
	public String getItemServicoList( int index ) {
		return (String) itemServicoList.get( index );
	}
	
	public String[] getDataCompetenciaList() {
		String[] tmp = new String[ dataCompetenciaList.size() ];
		return ((String[]) dataCompetenciaList.toArray( tmp ) );
	}
	
	public String[] getLocalExecucaoList() {
		String[] tmp = new String[ localExecucaoList.size() ];
		return ((String[]) localExecucaoList.toArray( tmp ) );
	}
	
	public String getLocalExecucaoList( int index ) {
		return (String) localExecucaoList.get( index );
	}
	
	public String[] getNumDocList() {
		String[] tmp = new String[ numeroDocumentoList.size() ];
		return ((String[]) numeroDocumentoList.toArray( tmp ) );
	}
	
	public String getNumDocList( int index ) {
		return (String) numeroDocumentoList.get( index );
	}
	
	public String[] getDataEmissaoList() {
		String[] tmp = new String[ dataEmissaoList.size() ];
		return ((String[]) dataEmissaoList.toArray( tmp ) );
	}
	
	public String getDataEmissaoList( int index ) {
		return (String) dataEmissaoList.get( index );
		
	}
	
	public String[] getRecBrutaList() {
		String[] tmp = new String[ receitaBrutaList.size() ];
		return ((String[]) receitaBrutaList.toArray( tmp ) );
	}
	
	public String getRecBrutaList( int index ) {
		return (String) receitaBrutaList.get( index );
		
	}
	
	public String[] getSubEmpreitadaList() {
		String[] tmp = new String[ subEmpreitadaList.size() ];
		return ((String[]) subEmpreitadaList.toArray( tmp ) );
	}
	
	public String getSubEmpreitadaList( int index ) {
		return (String) subEmpreitadaList.get( index );
		
	}
	
	public String[] getObrasForaMunicList() {
		String[] tmp = new String[ obrasForaMunicList.size() ];
		return ((String[]) obrasForaMunicList.toArray( tmp ) );
	}
	
	public String getObrasForaMunicList( int index ) {
		return (String) obrasForaMunicList.get( index );
		
	}

	public String[] getMaterialAplicadoMunicList() {
		String[] tmp = new String[ materialAplicadoList.size() ];
		return ((String[]) materialAplicadoList.toArray( tmp ) );
	}
	
	public String getMaterialAplicadoList( int index ) {
		return (String) materialAplicadoList.get( index );
		
	}
	
	public String[] getRecTributavelList() {
		String[] tmp = new String[ recTributavelList.size() ];
		return ((String[]) recTributavelList.toArray( tmp ) );
	}
	
	public String getRecTributavelList( int index ) {
		return (String) recTributavelList.get( index );
		
	}
	
	public String[] getTipoReceitaList() {
		String[] tmp = new String[ tipoRecList.size() ];
		return ((String[]) tipoRecList.toArray( tmp ) );
	}
	
	public String getTipoReceitaList( int index ) {
		return (String) tipoRecList.get( index );
		
	}
	
	public String[] getTipoDocList() {
		String[] tmp = new String[ tipoDocList.size() ];
		return ((String[]) tipoDocList.toArray( tmp ) );
	}
	
	public String getTipoDocList( int index ) {
		return (String) tipoDocList.get( index );
		
	}

	public String[] getTipoDedList() {
		String[] tmp = new String[ tipoDedList.size() ];
		return ((String[]) tipoDedList.toArray( tmp ) );
	}
	
	public String getTipoDedList( int index ) {
		return (String) tipoDedList.get( index );
		
	}
	
	public String[] getPercentualList() {
		String[] tmp = new String[ percentualList.size() ];
		return ((String[]) percentualList.toArray( tmp ) );
	}
	
	public String getPercentualList( int index ) {
		return (String) percentualList.get( index );
		
	}

	public String[] getTipoLancamento() {
		String[] tmp = new String[ tipoLancamentoList.size() ];
		return ((String[]) tipoLancamentoList.toArray( tmp ) );
	}
	
	public String getTipoLancamento( int index ) {
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
		
		String query = " SELECT contratanteMapaConstrucao, servicoMapaConstrucao, " +
					   " itemMapaConstrucao, dataCompetenciaMapaConstrucao, " + 
 	                   " localMapaConstrucao, numDocMapaConstrucao, " +
 	                   " dataEmissaoMapaConstrucao, " +
		               " recBrutaMapaConstrucao, subEmpMapaConstrucao, " +
		               " obrasMapaConstrucao, materialMapaConstrucao, " +
		               " recTribMapaConstrucao, tipoRecMapaConstrucao, " +
		               " tipoDocMapaConstrucao, tipoDeducaoMapaConstrucao, " +
	 	               " percentualMapaConstrucao, tipoRecMapaConstrucao, " +
					   " seqMapaConstrucao " +
 	                   " FROM tabMapaConstrucao " +
					   " WHERE osMapaConstrucao ='" + currOS + "'" +
					   "  AND cmcMapaConstrucao ='" + currCMC + "'" +
					   " ORDER BY  dataCompetenciaMapaConstrucao, contratanteMapaConstrucao ";
			              

		ResultSet rs =  myStatement.executeQuery( query );
		// Getting the records of the database
		createLookupFileMapaConstrucaoTable( rs );
	}

	/**
	 * Creates some hashmap to help in the typing during the input data
	 * on the user interface
	 * 
	 * @param <code>rs</code> Indicates the result set oppened 
	 */
	private void createLookupFileMapaConstrucaoTable( ResultSet rs ) {
	    
		try {
			
			nomeContratanteList = new ArrayList();
			descServicoList = new ArrayList();
			itemServicoList = new ArrayList();
			dataCompetenciaList = new ArrayList();
			localExecucaoList = new ArrayList();
			numeroDocumentoList = new ArrayList();
			dataEmissaoList = new ArrayList();
			receitaBrutaList = new ArrayList();
			subEmpreitadaList = new ArrayList();
			obrasForaMunicList = new ArrayList();
			materialAplicadoList = new ArrayList();
			recTributavelList = new ArrayList();
			tipoRecList = new ArrayList();
			tipoDocList = new ArrayList();
			tipoDedList = new ArrayList();
			percentualList = new ArrayList();
			tipoLancamentoList = new ArrayList();
			timestampList = new ArrayList();
			
	   
			/**
			 * Read all database and populate specified hashmaps to records
			 * previously read. This hashmap will be used to create comboboxes
			 * containing values stored in the database
			 */
			 while ( rs.next() ) {
	        	
				 String nomeContratante = rs.getString( 1 );
				 String descServico = rs.getString( 2 );
				 String itemServico = rs.getString( 3 );
				 String dataCompetencia = rs.getString( 4 );
				 String localExecucao = rs.getString( 5 );
				 String numeroDocumento = rs.getString( 6 );
				 String dataEmissao = rs.getString( 7 );
				 String receitaBruta = rs.getString( 8 );
				 String subEmpreitada = rs.getString( 9 );
				 String obrasForaMunic = rs.getString( 10 );
				 String materialAplicado = rs.getString( 11 );
				 String receitaTributavel = rs.getString( 12 );
				 String tipoRec = rs.getString( 13 );
				 String tipoDoc = rs.getString( 14 );
				 String tipoDed = rs.getString( 15 );
				 String percentual = rs.getString( 16 );
				 String tipoLancamento = rs.getString( 17 );
				 String timestamp = rs.getString( 18 );
				 
				 nomeContratanteList.add( nomeContratante );
				 descServicoList.add( descServico );
				 itemServicoList.add( itemServico );
				 dataCompetenciaList.add( dataCompetencia );
				 localExecucaoList.add( localExecucao );
				 numeroDocumentoList.add( numeroDocumento );
				 dataEmissaoList.add( dataEmissao );
				 receitaBrutaList.add( receitaBruta );
				 subEmpreitadaList.add( subEmpreitada );
				 obrasForaMunicList.add( obrasForaMunic );
				 materialAplicadoList.add( materialAplicado );
				 recTributavelList.add( receitaTributavel );
				 tipoRecList.add( tipoRec );
				 tipoDocList.add( tipoDoc );
				 tipoDedList.add( tipoDed );
				 percentualList.add( percentual );
				 tipoLancamentoList.add( tipoLancamento );
				 timestampList.add( timestamp );				 
	    	    
			 }
		}    
		catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		}	
		
		
	}
	

}
