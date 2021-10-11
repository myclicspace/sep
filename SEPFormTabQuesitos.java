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

//:: PENDENCIA -> Desabilitar e autocommit e descomentar os commit
//>> Alterado para permitir manter compatilidade com a versão em produção


public class SEPFormTabQuesitos extends JPanel {

	//private int[] arrCodigoQuesitos = null;
	private Map rows = null;
	/**
	 * Help to supply a search mechanish for finding name given its code 
	 **/
	private Map codeRotinasSearchEngine = null;
	/**
	 * Help to supply a search mechanish for finding code given its name
	 **/
	private Map nameRotinasSearchEngine = null;
	
	
	private Vector quesitos = null;
		
	private JTextField codigoTxt = null; // holds codigoQuesito
	private JTextField nomeTxt = null; // holds descQuesito
	private JComboBox rotinasCombo = null ;
	/**
	 * Holds the descRotina of table tabrotinas used in the combo rotinasCombo
	 */ 
	private Vector rotinasNames = null;
	
	/**
	 * bufferRecordPos represents the raw-order was stored in the buffer for 
	 * the record read of table when of starting load of that records
	 */
	private int bufferRecordPos = 0;
	
	private Connection myConnection = null;
	private Statement myStatement = null;
//	private Statement batchStatement = null;
	
	TabQuesitosModel model	= null;
    JTable quesitosTable = null; 	
    
	//private Object[][] quesitos;
			
    
			
	private String[] columnNames =
	{ "Código da Rotina", "Código do quesito", "Descrição do quesito"};
	
	public SEPFormTabQuesitos( Connection con ) {
		try {
			myConnection = con;
			con.setAutoCommit( true );			
			myConnection.setAutoCommit( true );
			myStatement = con.createStatement();
			Statement stmt = con.createStatement();
			String query = "SELECT codigoRotina, codigoQuesito, descQuesito " +
			               " FROM tabQuesito ORDER BY codigoRotina, codigoQuesito" ;
			ResultSet rs = stmt.executeQuery( query );
			loadQuesitosTable( rs );	
			loadRotinas( con );
			
			
		} catch ( SQLException ignore ) {
			System.out.println( ignore.getMessage() );
			ignore.printStackTrace();
		}	
	}

	public JPanel createTabQuesitosPanel(){
		
		
		JPanel outerQuesitosPanel =  new JPanel();
		outerQuesitosPanel.setLayout( new BoxLayout( outerQuesitosPanel,
										  BoxLayout.Y_AXIS) );
		
		JPanel tabQuesitosPanel = new JPanel( new BorderLayout() );
				
		JPanel choosenQuesitosPanel  = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
		Border etchedQuesitosPanel = BorderFactory.createEtchedBorder();
        TitledBorder titleBorderQuesitosPanel;
        titleBorderQuesitosPanel = BorderFactory.createTitledBorder(
		       etchedQuesitosPanel, "Selecione uma rotina");
        titleBorderQuesitosPanel.setTitleJustification(TitledBorder.RIGHT);
        choosenQuesitosPanel.setBorder( titleBorderQuesitosPanel );

		JLabel rotinaLabel = new JLabel("Descrição da rotina ");
		rotinasCombo = new JComboBox( rotinasNames );
		choosenQuesitosPanel.add( rotinaLabel );
		choosenQuesitosPanel.add( rotinasCombo );
		
		
		JPanel inputTabQuesitosPanel = new JPanel( new GridLayout( 2, 2, 5, 5 ) );
		JLabel codigoLabel = new JLabel("Código do quesito");
		codigoTxt = new JTextField( 02 );
		JLabel nomeLabel = new JLabel("Descrição do quesito");
		nomeTxt = new JTextField( 20 );
		
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder(
		       etched, "Quesitos");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabQuesitosPanel.setBorder( titleBorder );				
		inputTabQuesitosPanel.add( codigoLabel );
		inputTabQuesitosPanel.add( codigoTxt ); 
		inputTabQuesitosPanel.add( nomeLabel );
		inputTabQuesitosPanel.add( nomeTxt ); 
			
	
		tabQuesitosPanel.add( inputTabQuesitosPanel );
				
		model = new TabQuesitosModel();		
		quesitosTable = new JTable(); 
		JTableHeader headers = quesitosTable.getTableHeader();
		headers.setReorderingAllowed( false );
		
		
		quesitosTable.setModel( model );
		quesitosTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		quesitosTable.addMouseListener( new MouseAdapter()  {
			public void mouseClicked( MouseEvent e ) {
				int count = quesitosTable.getSelectedRowCount();
				if ( count > 0 ) {
//					codigoTxt.setEditable( false );
//					rotinasCombo.setEnabled( false );					
                    inputTabQuesitosPanelRefresh();					
				}
			}
		});
		
		
		JScrollPane scrollpane = new JScrollPane( quesitosTable );
		
		JPanel quesitosDisplayPanel = new JPanel( new BorderLayout() ) ;
		
		quesitosDisplayPanel.add( scrollpane );
		
		JPanel controlQuesitosPanel = createControlQuesitosPanel();
		
		tabQuesitosPanel.add( inputTabQuesitosPanel, BorderLayout.NORTH );
		tabQuesitosPanel.add( quesitosDisplayPanel, BorderLayout.CENTER );
		tabQuesitosPanel.add( controlQuesitosPanel, BorderLayout.SOUTH );		
		
		outerQuesitosPanel.add( choosenQuesitosPanel );
		outerQuesitosPanel.add( tabQuesitosPanel );
		
		return outerQuesitosPanel;
		
	}
	
	
	private JPanel createControlQuesitosPanel() {	
		JButton incluirButton = new JButton("Incluir");		
		JButton excluirButton = new JButton("Excluir");
		JButton gravarButton = new JButton("Gravar");
		
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
		excluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
				    deleteRecord( myConnection );					
				} catch( SQLException ignore ) {
					ignore.printStackTrace();					
				}    
			}
		});
		
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
		
//		incluirButton.setEnabled( false );
//		excluirButton.setEnabled( false );
//		gravarButton.setEnabled( false ); 
		
		
		JPanel controlPanel = new JPanel( new FlowLayout() );		
		controlPanel.add( incluirButton );
		controlPanel.add( excluirButton );	
		controlPanel.add( gravarButton );
		
		return controlPanel;
		
	}
	
	/**
	 * Loading a combo component with the field descRotina of table tabrotinas
	 * Also, is filling the hash rotinas with the fields codRotina and descRotina
	 * of that table
	 * 
	 * @param <code>con</code> representing the active connection
	 * @throws if ocurrs some exception for the access of table tabrotinas
	 *
	 */
	
	private void loadRotinas( Connection con ) {
		int cols = 2; // columnNames.length;
		codeRotinasSearchEngine = new HashMap();
		nameRotinasSearchEngine = new HashMap();
		rotinasNames = new Vector();
		String rotinaCode;
		String rotinaName;
		try {
			
			Connection theConn = con;
			Statement stm = theConn.createStatement();
			Statement stmt = theConn.createStatement();
			String query = "SELECT codigoRotina, descricaoRotina " +
		               " FROM tabRotinas ORDER BY codigoRotina" ;
			ResultSet rs = stmt.executeQuery( query );		
		
			/**
		 	* Assembles the rotinasCombo component and the rotinas hash with
		 	* your matching records of tabrotinas table
		 	*/
		 	while ( rs.next() ) {
		 		String[] theRow = new String[ cols ] ;
		 		rotinaCode = (String)rs.getString( 1 );
		 		rotinaName = (String)rs.getString( 2 );
		 		codeRotinasSearchEngine.put( rotinaCode, rotinaName );   
		 		nameRotinasSearchEngine.put( rotinaName, rotinaCode );
		 		rotinasNames.add( rotinaName );
		 	}		 
		}
		catch ( SQLException sqlException ) {
			System.out.println( sqlException.getMessage() + " aqui");
			sqlException.printStackTrace();
		}
	}	
	
	
	private void loadQuesitosTable( ResultSet rs ) {	
		try {
			rows = new HashMap();
			quesitos = new Vector();
			String strCodeRotina = null;
			String strCodeQuesito = null;
			String key = null;
			int cols = columnNames.length;
			bufferRecordPos = 0;
			/** 
			 *  Inserts all the data as a vector of Object[] rows
			/*  It was not used Object[][] because is not knew how many rows
			/*  the ResultSet has
			 **/
			 int count = 0;
			 while ( rs.next() ) {
			 	count++;
			 	String[] theRow = new String[ cols ];
			 	for ( int j = 0; j < theRow.length; j++ )  {
			 	    theRow[ j ] = (String)rs.getString( j + 1 );
			 	}
		 	    strCodeRotina =  rs.getString( 1 );
		 	    strCodeQuesito = rs.getString( 2 );
		 	    key = strCodeRotina + strCodeQuesito;
			 	rows.put( new String(key) , new Integer(  bufferRecordPos ) );	 	
			 	quesitos.add( theRow );
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
	
		String currNameRotina = (String) rotinasCombo.getSelectedItem();
		String codigoRotina = 
		     (String) nameRotinasSearchEngine.get( currNameRotina );
		String codigoQuesito =  codigoTxt.getText();
		String searchKey = codigoRotina + codigoQuesito;
		boolean bufferHas = rows.containsKey( new Integer( searchKey ) );
		
		if ( bufferHas ) {
			String err = "Tentativa de inserir registro duplicado";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			// -- Includes data in table model, and so does refreshes gui 

			String quesito[] = new String[ 3 ];		
			String nomeQuesito   = nomeTxt.getText();
			quesito[ 0 ] = codigoRotina;
			quesito[ 1 ] = codigoQuesito;
			quesito[ 2 ] = nomeQuesito;
			
			
			// -- The calling for including records is done using batch process			
			String cmd = "INSERT INTO tabQuesito VALUES  ('" 
			              + codigoRotina + "','"
			              + codigoQuesito + "','" 
			              + nomeQuesito +  "')"; 
			myStatement.executeUpdate( cmd );
			//con.commit();			
			
			quesitos.add( quesito );
			rows.put( new String ( searchKey ) , new Integer( bufferRecordPos ) );
			bufferRecordPos++;
			
			codigoTxt.setText("");
			nomeTxt.setText("");
			
			bufferRefresh();      // updating buffer
			quesitosTable.setModel( model );
			quesitosTable.revalidate();
			quesitosTable.repaint();	
			revalidate();
			repaint();
			
			
//-->			batchStatement.addBatch( cmd );

		}
			
	}

	/**
	 * Delete records from the database
	 *
	 * precondition: Must be garanted that there is a record 
	 *               with the key to be deleted	 
	 * @param <code>con</code> represents a Connection object
	 **         necessary for creating the statement  
	 * @throws SQLException if ocurrs some erro with Connection
	 */	
	private void deleteRecord( Connection con ) throws SQLException {
		
		String currNameRotina = (String) rotinasCombo.getSelectedItem();
		String codigoRotina = 
		     (String) nameRotinasSearchEngine.get( currNameRotina );
		String codigoQuesito =  codigoTxt.getText();
		String searchKey = codigoRotina + codigoQuesito;
		boolean bufferHas = rows.containsKey( new Integer( searchKey ) );
		if ( !bufferHas ) {
			String err = "Tentativa de remover registro inexistente";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
			
		}
		else {
			Integer rowID = (Integer) rows.get( new Integer( searchKey ) );
			int index = rowID.intValue();
			quesitos.remove( index );

			// -- The calling for deleting records is done
			String cmd = "DELETE FROM  tabQuesito" + 
		             " WHERE codigoRotina = '" + codigoRotina + "'" 
		             + " AND codigoQuesito = '" + codigoQuesito + "'" ; 
 			myStatement.executeUpdate( cmd );
 			
			bufferRefresh();      // updating buffer
			quesitosTable.setModel( model );
			quesitosTable.revalidate();
			quesitosTable.repaint();	
			revalidate();
			repaint();
 			

			//->con.commit();						
		}	
		
	}
	
	/*	
	 * Updates the selected record currently of table quesitosTable 
	 *
	 * precondition: Must check that there is a subjacent key in database
	 *               if it will update the record
	 * @param <code>con</code> represents a Connection object
	 *         necessary for creating the statement  
	 * @throws SQLException if ocurrs some error with Connection
	 */	
	private void updateRecord( Connection con ) throws SQLException {

	
		String currNameRotina = (String) rotinasCombo.getSelectedItem();
		String codigoRotina = 
		     (String) nameRotinasSearchEngine.get( currNameRotina );
		String codigoQuesito =  codigoTxt.getText();
		String searchKey = codigoRotina + codigoQuesito;
		boolean bufferHas = rows.containsKey( new Integer( searchKey ) );
		if ( !bufferHas ) {
			String err = "Tentativa de atualizar registro inexistente";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
			
		}
		else {
			String quesito[] = new String[ 3 ];		
			String nomeQuesito   = nomeTxt.getText();
			quesito[ 0 ] = codigoRotina;
			quesito[ 1 ] = codigoQuesito;
			quesito[ 2 ] = nomeQuesito;
			Integer rowID = (Integer) rows.get(  new Integer( searchKey ) );
			// -- position in the buffer of choicen record
			int index = rowID.intValue();
			// -- remove first and insert again. Doing this, is not 
			// -- necessary to worry with refreshing cacher buffer
			quesitos.remove( index );
			quesitos.add( index, quesito );		

			
			// -- The calling for updating records is done
			String cmd = "UPDATE tabQuesito "
					  +  "SET descQuesito ='" + nomeQuesito + "'" 
		              + " WHERE codigoRotina = '" + codigoRotina + "'" 
		              + " AND codigoQuesito = '" + codigoQuesito + "'" ; 
		 	myStatement.executeUpdate( cmd );		
	 		
			//con.commit();
			
			bufferRefresh();      // updating buffer
			quesitosTable.setModel( model );
			quesitosTable.revalidate();
			quesitosTable.repaint();	
			revalidate();
			repaint();
						
			
			System.out.print(" gravou " );	
		}
		
	}
	
	/**
	 *  It will update the inputTabInfracoesPanel if the user selects
	 *  one row in displayTablInfracoesPanel
	 *  Notice, it will update the combo rotinasCombo for reflect that choicen row 
	 *
	 */
    private void inputTabQuesitosPanelRefresh() {   	
    	String value;
		int selectedRow = quesitosTable.getSelectedRow();
		value = (String) quesitosTable.getValueAt(selectedRow, 1 );
		codigoTxt.setText( value );
		value = (String) quesitosTable.getValueAt(selectedRow, 2 );
		nomeTxt.setText( value );
		
		// Find the matching combo and refresh it for reflect the selected row
		// in displayTableInfracoesPanel
		String currCodeRotina = (String) quesitosTable.getValueAt( selectedRow, 0 );
		String desc = (String) codeRotinasSearchEngine.get( currCodeRotina );
		rotinasCombo.setSelectedItem( desc );
		
		revalidate();		
    }						
	
	/**
	 * It will update the rows hash if it deletes a record
	 *
	 */
	private void bufferRefresh() {
		String[] quesito;
		String codigoRotina;
		String codigoQuesito;
		rows.clear();
		for ( int i = 0; i < quesitos.size(); i++ ) {
			quesito = (String[])  quesitos.get( i );
			codigoRotina = quesito[ 0 ];
			codigoQuesito = quesito[ 1 ];
			String key = codigoRotina + codigoQuesito;			
			System.out.println( " key = " + key );
			rows.put( new String ( key ) , new Integer( i ) );		
		}
	}
	
	
	
	private class TabQuesitosModel extends AbstractTableModel {
		
		public int getRowCount() {
			return rows.size();
		}
		
		public Object getValueAt( int r , int c ) {			
		String[] theRow = null ;
		    if ( r < quesitos.size() ) {
		    	Object obj = quesitos.get( r );
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
