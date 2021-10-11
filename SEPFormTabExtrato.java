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

public class SEPFormTabExtrato extends JPanel {

	private Map rows;
	private Vector extratos;
	
	/**
	 * Holds information about the choicen company
	 */
	private static String currOS = null;   // ordem de serviço
	private static String currCMC = null;  // insc. municipal
	
	
	private TabExtratosModel extratosTableModel = null ;		
	private JTable	extratosTable = null; 

		
	private JTextField mesReferenciaTxt = null;
	private JTextField dataPagamentoTxt = null;			
	private JTextField valorPagoTxt = null;	
	
	/**
	 * bufferRecordPos represents the raw-order was stored in the buffer for 
	 * the record read of table when of starting load of that records
	 */
	private int bufferRecordPos = 0;
	
	private Connection myConnection = null;
	private Statement myStatement = null;
	
			
	private String[] columnNames =
	{ "Mês Referência", "Data Pagamento", "Valor Pago" };	
	
	public SEPFormTabExtrato( Connection con ) {
		try {
			myConnection = con;
			myStatement = con.createStatement();
			
			currOS = EmpresaAtual.getNumeroOS();
			currCMC = EmpresaAtual.getCMCEmpresa();
			
			Statement stmt = con.createStatement();
			String query = "SELECT mespagextrato, datapagextrato, valorpagextrato " +
			               " FROM tabExtratos " +
			               " WHERE osextrato='" + currOS + "'" +
			               " AND cmcextrato='" + currCMC + "'" +
			               " ORDER BY osextrato, cmcextrato, mespagextrato" ;
			ResultSet rs = stmt.executeQuery( query );
			loadExtratosTable( rs );	
			
			
		} catch ( SQLException ignore ) {
			System.out.println( ignore.getMessage() );
			ignore.printStackTrace();
		}	
	}

	public JPanel createTabExtratosPanel(){
		
		
        JPanel tabExtratoPanel = new JPanel( new BorderLayout() );		
		JPanel inputTabExtratoPanel = new JPanel( new GridLayout( 3, 2, 5, 5 ) );
		
		JLabel mesReferenciaLabel = new JLabel("Mês Referência");
		mesReferenciaTxt = new JTextField( 20 );
		JLabel dataPagamentoLabel = new JLabel("Data Pagamento");
		dataPagamentoTxt = new JTextField( 20 );
		JLabel valorPagoLabel = new JLabel("Valor Pago");
		valorPagoTxt = new JTextField( 20 );
		
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Extrato");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabExtratoPanel.setBorder( titleBorder );					
		
		inputTabExtratoPanel.add( mesReferenciaLabel );
		inputTabExtratoPanel.add( mesReferenciaTxt ); 
		inputTabExtratoPanel.add( dataPagamentoLabel ); 
		inputTabExtratoPanel.add( dataPagamentoTxt );
		inputTabExtratoPanel.add( valorPagoLabel ); 
		inputTabExtratoPanel.add( valorPagoTxt );
		
		JButton incluirButton = new JButton("Incluir");		
		JButton excluirButton = new JButton("Excluir");
		JButton gravarButton = new JButton("Gravar");
		
		incluirButton.setMnemonic('I');
		excluirButton.setMnemonic('E');
		gravarButton.setMnemonic('G');
		JPanel controlExtratoPanel = new JPanel( new FlowLayout() );	
	
		
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
		
		excluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
				    deleteRecord( myConnection );					
				} catch( SQLException ignore ) {
					ignore.printStackTrace();					
				}    
			}
		});
		
		
		controlExtratoPanel.add( incluirButton );
		controlExtratoPanel.add( excluirButton );
		controlExtratoPanel.add( gravarButton );
		
		
		tabExtratoPanel.add( inputTabExtratoPanel );
				
		extratosTableModel = new TabExtratosModel();		
		extratosTable = new JTable(); 
		extratosTable.setModel( extratosTableModel );
		extratosTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		extratosTable.addMouseListener( new MouseAdapter()  {
			public void mouseClicked( MouseEvent e ) {
				int count = extratosTable.getSelectedRowCount();
				if ( count > 0 ) {
                    inputTabExtratosPanelRefresh();					
				}
			}
		});
		
		
		JScrollPane scrollpane = new JScrollPane( extratosTable );
		
		JPanel extratoDisplayPanel = new JPanel( new BorderLayout() ) ;
		
		extratoDisplayPanel.add( scrollpane );
		
		tabExtratoPanel.add( inputTabExtratoPanel, BorderLayout.NORTH );
		tabExtratoPanel.add( extratoDisplayPanel, BorderLayout.CENTER );
		tabExtratoPanel.add( controlExtratoPanel, BorderLayout.SOUTH );		
		
		return tabExtratoPanel;	
		
	}
	
	private void loadExtratosTable( ResultSet rs ) {	
		try {
			rows = new HashMap();
			extratos = new Vector();
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

			 	String mesPag = (String) rs.getString( 1 );
			 	String readData = (String) rs.getString( 2 );
			 	String dataPag = SEPConverter.converteFrmtDataFromMySQL( readData );			 	
			 	String valorPag = (String) rs.getString( 3 );
			 	
			 	theRow[ 0 ] = mesPag;
			 	theRow[ 1 ] = dataPag;
			 	theRow[ 2 ] = SEPConverter.convertFrmtCurrencyFromMySQL( valorPag );							 	
			 	
		 	    key = mesPag + dataPag;
		 	    
			 	rows.put( key , new Integer(  bufferRecordPos ) );	 	
			 	extratos.add( theRow );
			 	bufferRecordPos++;	 	
			 	
			 }
			 
		}
		catch ( SQLException sqlException ) {
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

	    String mesRef = mesReferenciaTxt.getText();
	    String dataPagamento = dataPagamentoTxt.getText();
	    String key = mesRef + dataPagamento;
	    
		boolean bufferHas = rows.containsKey( key );
		if ( bufferHas ) {
			String err = "Tentativa de inserir registro duplicado";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			// -- Includes data in table model, and so does refreshes gui 
			String extrato[] = new String[ 3 ];		
			String mes =  mesReferenciaTxt.getText();
			String data = dataPagamentoTxt.getText();			
			String valor = valorPagoTxt.getText(); 
			extrato[ 0 ] = mes;
			extrato[ 1 ] = data;
			extrato[ 2 ] = valor;
			
			String ignore = "0";
						
			// The chunck of data is right,then proceed to addding them to database
			String cmd = "INSERT INTO tabExtratos " +
			" VALUES  ('"  + currOS + "','" + currCMC + "','" 
			               +  mes + "','"
			               +  SEPConverter.converteFrmtDataToMySQL(data) + "','"
			               +  SEPConverter.convertFrmtCurrencyToMySQL(valor) + "','"
			               + ignore + " ')"; 
			               
			myStatement.executeUpdate( cmd );
			con.commit();			
			
			extratos.add( extrato );
			rows.put( key  , new Integer( bufferRecordPos ) );
			bufferRecordPos++;
			
			mesReferenciaTxt.setText("");
			dataPagamentoTxt.setText("");
			valorPagoTxt.setText("");
			
			bufferRefresh();                // updating buffer
			extratosTable.setModel( extratosTableModel );
			extratosTable.revalidate();
			extratosTable.repaint();
			
			System.out.println( " incluiu " );		
			
			
		}
			
	}

	/**
	 * Delete records from database
	 *
	 * precondition: Must be garanted that there is just a record 
	 *               with the key to be deleted	 
	 * @param <code>con</code> represents a Connection object
	 *         necessary for creating the statement  
	 * @throws SQLException if ocurrs some error with Connection
	 */	
	private void deleteRecord( Connection con ) throws SQLException {
		
		int selectedRow = extratosTable.getSelectedRow();
		// -- primary key into to choicen record 
		String mes = (String) extratosTable.getValueAt(selectedRow, 0 );
		String data = (String) extratosTable.getValueAt( selectedRow, 1 );
		String searchKey = mes + data ;
		
		                     
		Integer rowID = (Integer) rows.get( searchKey );
		
		// -- position in the buffer of choicen record
		int index = rowID.intValue();
                   
		extratos.remove( index );
		bufferRefresh();                // updating buffer
		extratosTable.setModel( extratosTableModel );
		extratosTable.revalidate();
		extratosTable.repaint();

		// -- The calling for deleting records is done
		String cmd = "DELETE FROM  tabExtratos" + 
		             " WHERE osExtrato = '" + currOS + "'" +
		             " AND cmcExtrato ='" + currCMC + "'" +
		             " AND mesPagExtrato ='" + mes + "'" +
		             " AND dataPagExtrato ='" 
		               + SEPConverter.converteFrmtDataToMySQL(data) + "'" ; 
 		myStatement.executeUpdate( cmd );

		con.commit();			
		
		System.out.println( " removeu " );		
		
		
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
		
	    String mesRef = mesReferenciaTxt.getText();
	    String dataPagamento = dataPagamentoTxt.getText();
	    String key = mesRef + dataPagamento;
	    
		boolean bufferHas = rows.containsKey( key );
		if ( !bufferHas ) {
			String err = "Tentativa de atualizar registro inexistente";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			String extrato[] = new String[ 3 ];		
			
			String mes =  mesReferenciaTxt.getText();
			String data = dataPagamentoTxt.getText();
			String valor = valorPagoTxt.getText();
			
			extrato[ 0 ] = mes;
			extrato[ 1 ] = data;
			extrato[ 2 ] = valor;
			
			String searchKey = mes + data;
			
			Integer rowID = (Integer) rows.get(  searchKey  );
			// -- position in the buffer of choicen record
			int index = rowID.intValue();
			// -- remove first and insert again. Doing this, is not 
			// -- necessary to worry with refreshing cacher buffer
			extratos.remove( index );
			extratos.add( index, extrato );		

			extratosTable.setModel( extratosTableModel );
			extratosTable.revalidate();
			extratosTable.repaint(); 
			
			// -- The calling for updating records is done
			// Here, is upadted just the field valor because
			// the other fields behaviour as unique keys
			
			String str = SEPConverter.convertFrmtCurrencyToMySQL(valor);
			System.out.println( " valor = " + valor );
			System.out.println( " str = " + str );
			   
			
			String cmd = "UPDATE tabExtratos "
						  +  "SET valorPagExtrato ='"  +  str  + "'" 
						  +  " WHERE osExtrato = '" + currOS + "'" 
						  +  " AND cmcExtrato = '" + currCMC + "'" 
						  +  " AND mesPagExtrato ='" + mes + "'" 
						  +  " AND dataPagExtrato ='" 
						  + SEPConverter.converteFrmtDataToMySQL(data) + "'";
			
			
	 		myStatement.executeUpdate( cmd );		
	 		
			con.commit();	
			System.out.println( " gravou " );		
			
		}		
		 
	}
	
	/**
	 *  It will update the inputTabInfracoesPanel if the user selects
	 *  one row in displayTablInfracoesPanel
	 *
	 */
    private void inputTabExtratosPanelRefresh() {   	
    	String value;
		int selectedRow = extratosTable.getSelectedRow();
		
		String mes = (String) extratosTable.getValueAt(selectedRow, 0 );
		mesReferenciaTxt.setText( mes );
		
		String data = (String) extratosTable.getValueAt(selectedRow, 1 );
		dataPagamentoTxt.setText( data );
	
	    String valor = (String) extratosTable.getValueAt( selectedRow, 2 );
	    valorPagoTxt.setText( valor );	
		
		revalidate();		
    }						
	
	/**
	 * It will update the rows hash if it deletes a record
	 *
	 */
	private void bufferRefresh() {
		String[] extrato = new String[ 3 ];
		String searchKey;
		rows.clear();
		for ( int i = 0; i < extratos.size(); i++ ) {
			extrato = (String[])  extratos.get( i );
			String mes = extrato[ 0 ];
			String data = extrato[ 1 ];
			searchKey = mes + data ;
			rows.put( searchKey  , new Integer( i ) );		
		}
	}
	
	
	
	private class TabExtratosModel extends AbstractTableModel {
		
		public int getRowCount() {
			return rows.size();
		}
		
		public Object getValueAt( int r , int c ) {			
		String[] theRow = null ;
		    if ( r < extratos.size() ) {
		    	Object obj = extratos.get( r );
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
