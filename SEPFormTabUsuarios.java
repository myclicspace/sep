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
import java.net.*;

public class SEPFormTabUsuarios extends JPanel {

	private int[] arrCodigoUsuarios;
	private Map rows;
	private Vector usuarios;
		
	private JTextField userIDTxt;
	private JPasswordField userPSWDTxt;
	/**
	 * bufferRecordPos represents the raw-order was stored in the buffer for 
	 * the record read of table when of starting load of that records
	 */
	private int bufferRecordPos = 0;
	
	private Connection myConnection = null;
	private Statement myStatement = null;
//	private Statement batchStatement = null;
	
	TabUsuariosModel model	= null;
    JTable usuariosTable = null; 	
			
	private String[] columnNames =
	{ "Matricula do usuario", "Senha criptografada"};
	
	public SEPFormTabUsuarios( Connection con ) {
		try {
			myConnection = con;
			myStatement = con.createStatement();
			Statement stmt = con.createStatement();
			String query = "SELECT usuario, senha " +
			               " FROM tabUsuarios ORDER BY usuario" ;
			ResultSet rs = stmt.executeQuery( query );
			loadUsuariosTable( rs );	
			
		} catch ( SQLException ignore ) {
			System.out.println( ignore.getMessage() );
			ignore.printStackTrace();
		}	
	}

	public JPanel createTabUsuariosPanel(){
		
        JPanel tabUsuariosPanel = new JPanel( new BorderLayout() );		
		JPanel inputTabUsuariosPanel = new JPanel( new GridLayout( 2, 2, 5, 5 ) );
		
		JLabel userIDLabel = new JLabel("Matricula");
		userIDTxt = new JTextField( 7 );
		JLabel userPSWDLabel = new JLabel("Senha");
		userPSWDTxt = new JPasswordField(40);
		
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder(
		       etched, "Usuarios");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabUsuariosPanel.setBorder( titleBorder );					
		
		inputTabUsuariosPanel.add( userIDLabel );
		inputTabUsuariosPanel.add( userIDTxt ); 
		inputTabUsuariosPanel.add( userPSWDLabel ); 
		inputTabUsuariosPanel.add( userPSWDTxt );
		
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
		JPanel controlUsuariosPanel = new JPanel( new FlowLayout() );
		
		controlUsuariosPanel.add( incluirButton );
		controlUsuariosPanel.add( excluirButton );
		excluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
				    deleteRecord( myConnection );					
				} catch( SQLException ignore ) {
					ignore.printStackTrace();					
				}    
			}
		});
		
		controlUsuariosPanel.add( gravarButton );
//		controlUsuariosPanel.add( sairButton );
		tabUsuariosPanel.add( inputTabUsuariosPanel );
				
		model = new TabUsuariosModel();				
		
		usuariosTable = new JTable(); 
		JTableHeader headers = usuariosTable.getTableHeader();
		headers.setReorderingAllowed( false );
		
		usuariosTable.setModel( model );
		usuariosTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		usuariosTable.addMouseListener( new MouseAdapter()  {
			public void mouseClicked( MouseEvent e ) {
				int count = usuariosTable.getSelectedRowCount();
				if ( count > 0 ) {
                    inputTabUsuariosPanelRefresh();					
				}
			}
		});
		
		JScrollPane scrollpane = new JScrollPane( usuariosTable );
		
		JPanel usuariosDisplayPanel = new JPanel( new BorderLayout() ) ;
		
		usuariosDisplayPanel.add( scrollpane );
		
		tabUsuariosPanel.add( inputTabUsuariosPanel, BorderLayout.NORTH );
		tabUsuariosPanel.add( usuariosDisplayPanel, BorderLayout.CENTER );
		tabUsuariosPanel.add( controlUsuariosPanel, BorderLayout.SOUTH );		
		
		return tabUsuariosPanel;	
	}
	
	private void loadUsuariosTable( ResultSet rs ) {	
		try {
			rows = new HashMap();
			usuarios = new Vector();
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
			 	usuarios.add( theRow );
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
		//String userID = Integer.parseInt( userIDTxt.getText() );
        String userID =  userIDTxt.getText() ;		
		boolean bufferHas = rows.containsKey( userID );
		if ( bufferHas ) {
			String err = "Tentativa de inserir registro duplicado";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			// -- Includes data in table model, and so does refreshes gui 
			String usuario[] = new String[ 2 ];		
			String user =  userIDTxt.getText();
			String pswd = userPSWDTxt.getText();
			usuario[ 0 ] = user;
			usuario[ 1 ] = URLEncoder.encode(pswd);
			System.out.println( " usuario[ 1 ] " + usuario[ 1 ] );
			
			// -- The calling for including records is done using batch process			
			String cmd = "INSERT INTO tabUsuarios VALUES  ('" 
			              + user + "','" +  URLEncoder.encode(pswd) +  "')"; 
			myStatement.executeUpdate( cmd );
			con.commit();			
			
			usuarios.add( usuario );
			rows.put( new String ( userID ) , new Integer( bufferRecordPos ) );
			bufferRecordPos++;
			
			userIDTxt.setText("");
			userPSWDTxt.setText("");
			
			bufferRefresh();                // updating buffer
			usuariosTable.setModel( model );
			usuariosTable.revalidate();
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
		int selectedRow = usuariosTable.getSelectedRow();
		// System.out.println(" linha selecionada " + selectedRow );
		// -- primary key of choicen record 
		String value = (String) usuariosTable.getValueAt(selectedRow, 0 );
		//System.out.println(" coluna 0 da linha " + selectedRow + " é = " +
		//                     value );
		                     
		Integer rowID = (Integer) rows.get(  new String( value ) );
		// -- position in the buffer of choicen record
		int index = rowID.intValue();
		// System.out.println( " pos = " + rowID );				

		String[] record = new String[ 4 ];	
		record = (String[]) usuarios.get( index  );		
		// System.out.println(" registro " + record[ 0 ] + " removido do vetor " +
        //             " usuarioss " );		
                   
		usuarios.remove( index );
		bufferRefresh();                // updating buffer
		usuariosTable.setModel( model );
		usuariosTable.revalidate();

		// -- The calling for deleting records is done
		String cmd = "DELETE FROM  tabUsuarios" + 
		             " WHERE usuario = '" + value + "'" ; 
 		myStatement.executeUpdate( cmd );

		con.commit();			
		
	}
	
	/*	
	 * Updates the selected record currently of usuariosTable table 
	 *
	 * precondition: Must check that there is a subjacent key in database
	 *               if it will update the record
	 * @param <code>con</code> represents a Connection object
	 *         necessary for creating the statement  
	 * @throws SQLException if ocurrs some error with Connection
	 */	
	private void updateRecord( Connection con ) throws SQLException {
//		int userID = Integer.parseInt( userIDTxt.getText() );
		String userID =  userIDTxt.getText() ;
		boolean bufferHas = rows.containsKey( userID );
		if ( !bufferHas ) {
			String err = "Tentativa de atualizar registro inexistente";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			String usuario[] = new String[ 2 ];		
			String user =  userIDTxt.getText();
			String pswd = userPSWDTxt.getText();
			usuario[ 0 ] = user;
			usuario[ 1 ] = URLEncoder.encode(pswd);
			Integer rowID = (Integer) rows.get(  new String( user ) );
			// -- position in the buffer of choicen record
			int index = rowID.intValue();
			// -- remove first and insert again. Doing this, is not 
			// -- necessary to worry with refreshing cacher buffer
			usuarios.remove( index );
			usuarios.add( index, usuario );		


			usuariosTable.setModel( model );
			usuariosTable.revalidate();
			
			// -- The calling for updating records is done
			String cmd = "UPDATE tabUsuarios "
						  +  "SET senha ='" + pswd + "'" 
			              + " WHERE usuario = '" + user + "'" ; 
	 		myStatement.executeUpdate( cmd );		
	 		
			con.commit();			
			
			System.out.print(" gravou " );
			
			
		}		
		
	}
	
	/**
	 *  It will update the inputTabInfracoesPanel if the user selects
	 *  one row in displayTablInfracoesPanel
	 *
	 */
    private void inputTabUsuariosPanelRefresh() {   	
    	String value;
		int selectedRow = usuariosTable.getSelectedRow();
		value = (String) usuariosTable.getValueAt(selectedRow, 0 );
		userIDTxt.setText( value );
		value = (String) usuariosTable.getValueAt(selectedRow, 1 );
		userPSWDTxt.setText( value );
		
		revalidate();		
    }						
	
	/**
	 * It will update the rows hash if it deletes a record
	 *
	 */
	private void bufferRefresh() {
		String[] usuario;
		String codigo;
		rows.clear();
		for ( int i = 0; i < usuarios.size(); i++ ) {
			usuario = (String[])  usuarios.get( i );
			codigo = usuario[ 0 ];
			rows.put( new String ( codigo ) , new Integer( i ) );		
		}
	}
	
	
	
	private class TabUsuariosModel extends AbstractTableModel {
		
		public int getRowCount() {
			return rows.size();
		}
		
		public Object getValueAt( int r , int c ) {			
		String[] theRow = null ;
		    if ( r < usuarios.size() ) {
		    	Object obj = usuarios.get( r );
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
