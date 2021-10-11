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

//::FIX
// -> NOV, 2003
// -> Apply cascade deletion of record for the tables tabRotinas and tabQuesitos

public class SEPFormTabRotinas extends JPanel {

	private int[] arrCodigoRotinas;
	private Map rows;
	private Vector rotinas;
		
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
	
	TabRotinasModel model	= null;
    JTable rotinasTable = null; 	
			
	private String[] columnNames =
	{ "Código da rotina", "Descrição da rotina"};
	
	public SEPFormTabRotinas( Connection con ) {
		try {
			myConnection = con;
			myStatement = con.createStatement();
			Statement stmt = con.createStatement();
			String query = "SELECT codigoRotina, descricaoRotina " +
			               " FROM tabRotinas ORDER BY codigoRotina" ;
			ResultSet rs = stmt.executeQuery( query );
			loadRotinasTable( rs );	
			
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

	public JPanel createTabRotinasPanel(){
		
        JPanel tabRotinasPanel = new JPanel( new BorderLayout() );		
		JPanel inputTabRotinasPanel = new JPanel( new GridLayout( 2, 2, 5, 5 ) );
		
		JLabel codigoLabel = new JLabel("Código da rotina");
		codigoTxt = new JTextField( 7 );
		JLabel nomeLabel = new JLabel("Descrição da rotina");
		nomeTxt = new JTextField(40);
		
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder(
		       etched, "Rotinas");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabRotinasPanel.setBorder( titleBorder );					
		
		inputTabRotinasPanel.add( codigoLabel );
		inputTabRotinasPanel.add( codigoTxt ); 
		inputTabRotinasPanel.add( nomeLabel ); 
		inputTabRotinasPanel.add( nomeTxt );
		
		JButton incluirButton = new JButton("Incluir");
		//incluirButton.setEnabled( false );		
		JButton excluirButton = new JButton("Excluir");
		//excluirButton.setEnabled( false );
		JButton gravarButton = new JButton("Gravar");
		//gravarButton.setEnabled( false );
		
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
		
		JPanel controlRotinasPanel = new JPanel( new FlowLayout() );
		
		controlRotinasPanel.add( incluirButton );
		controlRotinasPanel.add( excluirButton );
		excluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
				    deleteRecord( myConnection );					
				} catch( SQLException ignore ) {
					ignore.printStackTrace();					
				}    
			}
		});
		
		controlRotinasPanel.add( gravarButton );

		tabRotinasPanel.add( inputTabRotinasPanel );
				
		model = new TabRotinasModel();				
		rotinasTable = new JTable();
		JTableHeader headers = rotinasTable.getTableHeader();
		headers.setReorderingAllowed( false );
		

		rotinasTable.setModel( model );
		rotinasTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		rotinasTable.addMouseListener( new MouseAdapter()  {
			public void mouseClicked( MouseEvent e ) {
				int count = rotinasTable.getSelectedRowCount();
				if ( count > 0 ) {
                    inputTabRotinasPanelRefresh();					
				}
			}
		});
		
		JScrollPane scrollpane = new JScrollPane( rotinasTable );
		
		JPanel rotinasDisplayPanel = new JPanel( new BorderLayout() ) ;
		
		rotinasDisplayPanel.add( scrollpane );
		
		tabRotinasPanel.add( inputTabRotinasPanel, BorderLayout.NORTH );
		tabRotinasPanel.add( rotinasDisplayPanel, BorderLayout.CENTER );
		tabRotinasPanel.add( controlRotinasPanel, BorderLayout.SOUTH );		
		
		return tabRotinasPanel;	
	}
	
	private void loadRotinasTable( ResultSet rs ) {	
		try {
			rows = new HashMap();
			rotinas = new Vector();
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
			 	rows.put( new Integer(key) , new Integer(  bufferRecordPos ) );	 	
			 	rotinas.add( theRow );
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
		int codigoRotina = Integer.parseInt( codigoTxt.getText() );
		boolean bufferHas = rows.containsKey( new Integer( codigoRotina ) );
		if ( bufferHas ) {
			String err = "Tentativa de inserir registro duplicado";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			// -- Includes data in table model, and so does refreshes gui 
			String rotina[] = new String[ 2 ];		
			String codigo =  codigoTxt.getText();
			String nome = nomeTxt.getText();
			rotina[ 0 ] = codigo;
			rotina[ 1 ] = nome;
			
			// -- The calling for including records is done using batch process			
			String cmd = "INSERT INTO tabRotinas VALUES  ('" 
			              + codigo + "','" +  nome +  "')"; 
			myStatement.executeUpdate( cmd );
			con.commit();			
			
			rotinas.add( rotina );
			rows.put( new Integer ( codigo ) , new Integer( bufferRecordPos ) );
			bufferRecordPos++;
			
			codigoTxt.setText("");
			nomeTxt.setText("");
			
			bufferRefresh();      // updating buffer
			rotinasTable.setModel( model );
			rotinasTable.revalidate();
			rotinasTable.repaint();	
			revalidate();
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
		int selectedRow = rotinasTable.getSelectedRow();
		// System.out.println(" linha selecionada " + selectedRow );
		// -- primary key of choicen record 
		String value = (String) rotinasTable.getValueAt(selectedRow, 0 );
		//System.out.println(" coluna 0 da linha " + selectedRow + " é = " +
		//                     value );
		                     
		Integer rowID = (Integer) rows.get(  new Integer( value ) );
		// -- position in the buffer of choicen record
		int index = rowID.intValue();
		// System.out.println( " pos = " + rowID );				

		String[] record = new String[ 4 ];	
		record = (String[]) rotinas.get( index  );		
		// System.out.println(" registro " + record[ 0 ] + " removido do vetor " +
        //             " fiscaiss " );		
                   
		rotinas.remove( index );

		// -- The calling for deleting records is done
		String cmd = "DELETE FROM  tabRotinas" + 
		             " WHERE codigoRotina = '" + value + "'" ; 
 		myStatement.executeUpdate( cmd );

		con.commit();
		
		bufferRefresh();      // updating buffer
		rotinasTable.setModel( model );
		rotinasTable.revalidate();
		rotinasTable.repaint();	
		revalidate();
		repaint();
					
		
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
		int codigoRotina = Integer.parseInt( codigoTxt.getText() );
		boolean bufferHas = rows.containsKey( new Integer( codigoRotina ) );
		if ( !bufferHas ) {
			String err = "Tentativa de atualizar registro inexistente";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			String rotina[] = new String[ 2 ];		
			String codigo =  codigoTxt.getText();
			String nome = nomeTxt.getText();
			rotina[ 0 ] = codigo;
			rotina[ 1 ] = nome;
			Integer rowID = (Integer) rows.get(  new Integer( codigo ) );
			// -- position in the buffer of choicen record
			int index = rowID.intValue();
			// -- remove first and insert again. Doing this, is not 
			// -- necessary to worry with refreshing cacher buffer
			rotinas.remove( index );
			rotinas.add( index, rotina );		


			rotinasTable.setModel( model );
			rotinasTable.revalidate();
			
			// -- The calling for updating records is done
			String cmd = "UPDATE tabRotinas "
						  +  "SET descricaoRotina ='" + nome + "'" 
			              + " WHERE codigoRotina = '" + codigo + "'" ; 
	 		myStatement.executeUpdate( cmd );		
	 		
			con.commit();			
			
			/*String query = "SELECT codigoInfracao, descricaoInfracao, " +
			               "enquadramentoInfracao, penalidadeInfracao " +
			               "FROM tabInfracao ORDER BY codigoInfracao";
			ResultSet rs = myStatement.executeQuery( query );
			loadInfracoesTable( rs );	
			finfracoesTable.revalidate(); */		

			bufferRefresh();      // updating buffer
			rotinasTable.setModel( model );
			rotinasTable.revalidate();
			rotinasTable.repaint();	
			revalidate();
			repaint();
			
			System.out.print(" gravou " );
			
			
		}		
		
	}
	
	/**
	 *  It will update the inputTabInfracoesPanel if the user selects
	 *  one row in displayTablInfracoesPanel
	 *
	 */
    private void inputTabRotinasPanelRefresh() {   	
    	String value;
		int selectedRow = rotinasTable.getSelectedRow();
		value = (String) rotinasTable.getValueAt(selectedRow, 0 );
		codigoTxt.setText( value );
		value = (String) rotinasTable.getValueAt(selectedRow, 1 );
		nomeTxt.setText( value );
		
		revalidate();		
    }						
	
	/**
	 * It will update the rows hash if it deletes a record
	 *
	 */
	private void bufferRefresh() {
		String[] rotina;
		String codigo;
		rows.clear();
		for ( int i = 0; i < rotinas.size(); i++ ) {
			rotina = (String[])  rotinas.get( i );
			codigo = rotina[ 0 ];
			rows.put( new Integer ( codigo ) , new Integer( i ) );		
		}
	}
	
	
	
	private class TabRotinasModel extends AbstractTableModel {
		
		public int getRowCount() {
			return rows.size();
		}
		
		public Object getValueAt( int r , int c ) {			
		String[] theRow = null ;
		    if ( r < rotinas.size() ) {
		    	Object obj = rotinas.get( r );
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
