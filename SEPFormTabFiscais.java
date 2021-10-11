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

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class SEPFormTabFiscais extends JPanel {

	private int[] arrCodigoFiscais;
	private Map rows;
	private Vector fiscais;
		
	private JTextField codigoTxt;
	private JTextField nomeTxt;
	/**
	 * bufferRecordPos represents the raw-order was stored in the buffer for 
	 * the record read of table when of starting load of that records
	 */
	private int bufferRecordPos = 0;
	
	private Connection myConnection = null;
	private Statement myStatement = null;
//	private Statement batchStatement = null;
	
	TabFiscaisModel model	= null;
    JTable fiscaisTable = null; 	
			
	private String[] columnNames =
	{ "Matricula do fiscal", "Nome do fiscal"};
	
	public SEPFormTabFiscais( Connection con ) {
		try {
			myConnection = con;
			myStatement = con.createStatement();
			Statement stmt = con.createStatement();
			String query = "SELECT codigoFiscal, nomeFiscal " +
			               " FROM tabFiscal ORDER BY codigoFiscal" ;
			ResultSet rs = stmt.executeQuery( query );
			loadFiscaisTable( rs );	
			
			// speeding the performance for access tabInfracao through
			// using batch mechanism of JDBC
/*-->			boolean autoCommit = myConnection.getAutoCommit();
			System.out.println( " valor do autocommit " + autoCommit );
			myConnection.setAutoCommit( false );
			System.out.println( " desabilitou autocomit " );
			batchStatement = myConnection.createStatement(); */
			
			
		} catch ( SQLException ignore ) {
			System.out.println( ignore.getMessage() );
			ignore.printStackTrace();
		}	
	}

	public JPanel createTabFiscaisPanel(){
		
        JPanel tabFiscaisPanel = new JPanel( new BorderLayout() );		
		JPanel inputTabFiscaisPanel = new JPanel( new GridLayout( 2, 2, 5, 5 ) );
		
		JLabel codigoLabel = new JLabel("Matricula do fiscal");
		codigoTxt = new JTextField( 7 );
		JLabel nomeLabel = new JLabel("Nome do fiscal");
		nomeTxt = new JTextField(40);
		
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder(
		       etched, "Fiscais");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabFiscaisPanel.setBorder( titleBorder );					
		
		inputTabFiscaisPanel.add( codigoLabel );
		inputTabFiscaisPanel.add( codigoTxt ); 
		inputTabFiscaisPanel.add( nomeLabel ); 
		inputTabFiscaisPanel.add( nomeTxt );
		
		JButton incluirButton = new JButton("Incluir");		
		JButton excluirButton = new JButton("Excluir");
		JButton gravarButton = new JButton("Gravar");
//		JButton sairButton = new JButton("Sair");
		
		incluirButton.setMnemonic('I');
		incluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
				    addRecord( myConnection );					
				} catch( SQLException ignore ) {
					ignore.printStackTrace();					
				}    
			}
		});
		
		excluirButton.setMnemonic('E');
		gravarButton.setMnemonic('G');
		gravarButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
					updateRecord( myConnection );
				} catch ( SQLException ignore ) {
					ignore.printStackTrace();
				}    
			}
		}); 
		
//		sairButton.setMnemonic('S');
		JPanel controlFiscaisPanel = new JPanel( new FlowLayout() );
		
		controlFiscaisPanel.add( incluirButton );
		controlFiscaisPanel.add( excluirButton );
		excluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
				    deleteRecord( myConnection );					
				} catch( SQLException ignore ) {
					ignore.printStackTrace();					
				}    
			}
		});
		
		controlFiscaisPanel.add( gravarButton );
//		controlFiscaisPanel.add( sairButton );
		tabFiscaisPanel.add( inputTabFiscaisPanel );
				
		model = new TabFiscaisModel();				
		
		fiscaisTable = new JTable(); 
		fiscaisTable.setModel( model );
		fiscaisTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		fiscaisTable.addMouseListener( new MouseAdapter()  {
			public void mouseClicked( MouseEvent e ) {
				int count = fiscaisTable.getSelectedRowCount();
				if ( count > 0 ) {
                    inputTabFiscaisPanelRefresh();					
				}
			}
		});
		
		JScrollPane scrollpane = new JScrollPane( fiscaisTable );
		
		JPanel fiscaisDisplayPanel = new JPanel( new BorderLayout() ) ;
		
		fiscaisDisplayPanel.add( scrollpane );
		
		tabFiscaisPanel.add( inputTabFiscaisPanel, BorderLayout.NORTH );
		tabFiscaisPanel.add( fiscaisDisplayPanel, BorderLayout.CENTER );
		tabFiscaisPanel.add( controlFiscaisPanel, BorderLayout.SOUTH );		
		
		return tabFiscaisPanel;	
	}
	
	private void loadFiscaisTable( ResultSet rs ) {	
		try {
			rows = new HashMap();
			fiscais = new Vector();
			String key = null;
			int cols = columnNames.length;
			bufferRecordPos = 0;
			/** 
			 *  Inserts all the data as a vector of Object[] rows
			/*  It was not used Object[][] because is not knew how many rows
			/*  the ResultSet has
			 **/
			 while ( rs.next() ) {
			 	String[] theRow = new String[ cols ];
			 	for ( int j = 0; j < theRow.length; j++ )  {
			 	    theRow[ j ] = (String)rs.getString( j + 1 );
			 	}
		 	    key = (String) rs.getString( 1 );
			 	rows.put( new String(key) , new Integer(  bufferRecordPos ) );	 	
			 	fiscais.add( theRow );
			 	bufferRecordPos++;	 	
			 	
			 }
			 
		}
		catch ( SQLException sqlException ) {
			System.out.println( sqlException.getMessage() + " aqui");
			sqlException.printStackTrace();
		}
	}
	
	/**
	 * Adds records to database
	 *
	 * precondition: Must be garanted that there is not 
	 *               duplicated keys in the database	 
	 * @param  <code>con</code> represents a Connection object
	 **         necessary for creating the statement  
	 * @throws SQLException if ocurrs some erro with Connection
	 */	
	private void addRecord(Connection con ) throws SQLException {	
		String codigoInfracao =  codigoTxt.getText() ;
		boolean bufferHas = rows.containsKey( codigoInfracao  );

		//int codigoInfracao = Integer.parseInt( codigoTxt.getText() );
		//boolean bufferHas = rows.containsKey( new Integer( codigoInfracao ) );
		if ( bufferHas ) {
			String err = "Tentativa de inserir registro duplicado";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			// -- Includes data in table model, and so does refreshes gui 
			String fiscal[] = new String[ 2 ];		
			String codigo =  codigoTxt.getText();
			String nome = nomeTxt.getText();
			fiscal[ 0 ] = codigo;
			fiscal[ 1 ] = nome;
			
			// -- The calling for including records is done using batch process			
			String cmd = "INSERT INTO tabFiscal VALUES  ('" 
			              + codigo + "','" +  nome +  "')"; 
			myStatement.executeUpdate( cmd );
			con.commit();			
			
			fiscais.add( fiscal );
			rows.put( new String ( codigo ) , new Integer( bufferRecordPos ) );
			bufferRecordPos++;
			
			codigoTxt.setText("");
			nomeTxt.setText("");
			
			bufferRefresh();                // updating buffer
			fiscaisTable.setModel( model );
			fiscaisTable.revalidate();
			repaint();
			
//-->			batchStatement.addBatch( cmd );

		}
			
	}

	/**
	 * Delete records from database
	 *
	 * precondition: Must be garanted that there is a record 
	 *               with the key to be deleted	 
	 * @param <code>con</code> represents a Connection object
	 **         necessary for creating the statement  
	 * @throws SQLException if ocurrs some erro with Connection
	 */	
	private void deleteRecord( Connection con ) throws SQLException {
		int selectedRow = fiscaisTable.getSelectedRow();
		// System.out.println(" linha selecionada " + selectedRow );
		// -- primary key of choicen record 
		String value = (String) fiscaisTable.getValueAt(selectedRow, 0 );
		//System.out.println(" coluna 0 da linha " + selectedRow + " é = " +
		//                     value );
		                     
		Integer rowID = (Integer) rows.get(  new String( value ) );
		int index = rowID.intValue();
		                     
//		Integer rowID = (Integer) rows.get(  new Integer( value ) );
//		int index = rowID.intValue();

		// -- position in the buffer of choicen record
		// System.out.println( " pos = " + rowID );				

		String[] record = new String[ 4 ];	
		record = (String[]) fiscais.get( index  );		
//		record = (String[]) fiscais.get( index  );		
		// System.out.println(" registro " + record[ 0 ] + " removido do vetor " +
        //             " fiscaiss " );		
                   
		fiscais.remove( index );
		bufferRefresh();                // updating buffer
		fiscaisTable.setModel( model );
		fiscaisTable.revalidate();

		// -- The calling for deleting records is done
		String cmd = "DELETE FROM  tabFiscal" + 
		             " WHERE codigoFiscal = '" + value + "'" ; 
 		myStatement.executeUpdate( cmd );

		con.commit();			
		
	}
	
	/*	
	 * Updates the selected record currently of fiscaisTable table 
	 *
	 * precondition: Must check that there is a subjacent key in database
	 *               if it will update the record
	 * @param <code>con</code> represents a Connection object
	 *         necessary for creating the statement  
	 * @throws SQLException if ocurrs some error with Connection
	 */	
	private void updateRecord( Connection con ) throws SQLException {
		String codigoInfracao =  codigoTxt.getText() ;
		boolean bufferHas = rows.containsKey( codigoInfracao  );

//		int codigoInfracao = Integer.parseInt( codigoTxt.getText() );
//		boolean bufferHas = rows.containsKey( new Integer( codigoInfracao ) );
		if ( !bufferHas ) {
			String err = "Tentativa de atualizar registro inexistente";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			String fiscal[] = new String[ 2 ];		
			String codigo =  codigoTxt.getText();
			String nome = nomeTxt.getText();
			fiscal[ 0 ] = codigo;
			fiscal[ 1 ] = nome;
			Integer rowID = (Integer) rows.get(  new String( codigo ) );
			// -- position in the buffer of choicen record
			int index = rowID.intValue();
			// -- remove first and insert again. Doing this, is not 
			// -- necessary to worry with refreshing cacher buffer
			fiscais.remove( index );
			fiscais.add( index, fiscal );		


			fiscaisTable.setModel( model );
			fiscaisTable.revalidate();
			
			// -- The calling for updating records is done
			String cmd = "UPDATE tabFiscal "
						  +  "SET nomeFiscal ='" + nome + "'" 
			              + " WHERE codigoInfracao = '" + codigo + "'" ; 
	 		myStatement.executeUpdate( cmd );		
	 		
			con.commit();			
			
			/*String query = "SELECT codigoInfracao, descricaoInfracao, " +
			               "enquadramentoInfracao, penalidadeInfracao " +
			               "FROM tabInfracao ORDER BY codigoInfracao";
			ResultSet rs = myStatement.executeQuery( query );
			loadInfracoesTable( rs );	
			finfracoesTable.revalidate(); */		
			
			
			System.out.print(" gravou " );
			
			
		}		
		
	}
	
	/**
	 *  It will update the inputTabInfracoesPanel if the user selects
	 *  one row in displayTablInfracoesPanel
	 *
	 */
    private void inputTabFiscaisPanelRefresh() {   	
    	String value;
		int selectedRow = fiscaisTable.getSelectedRow();
		value = (String) fiscaisTable.getValueAt(selectedRow, 0 );
		codigoTxt.setText( value );
		value = (String) fiscaisTable.getValueAt(selectedRow, 1 );
		nomeTxt.setText( value );
		
		revalidate();		
    }						
	
	/**
	 * It will update the rows hash if it deletes a record
	 *
	 */
	private void bufferRefresh() {
		String[] fiscal;
		String codigo;
		rows.clear();
		for ( int i = 0; i < fiscais.size(); i++ ) {
			fiscal = (String[])  fiscais.get( i );
			codigo = fiscal[ 0 ];
			rows.put( new String ( codigo ) , new Integer( i ) );		
		}
	}
	
	
	
	private class TabFiscaisModel extends AbstractTableModel {
		
		public int getRowCount() {
			return rows.size();
		}
		
		public Object getValueAt( int r , int c ) {			
		String[] theRow = null ;
		    if ( r < fiscais.size() ) {
		    	Object obj = fiscais.get( r );
		    	theRow = (String[]) obj;
		    	return ( theRow[ c ] );
		    }
		    else {
		    	return null;
		    }
		    
		}
		
		public String getColumnName( int c ) {
			
			return columnNames[ c ];
		}
		
		public int getColumnCount() {
			return columnNames.length; 
		} 
	}
	
	
}
