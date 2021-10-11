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

public class SEPFormTabInfracoes extends JPanel {

	private static final String IGNORE = "N/A";

	private int[] arrCodigoInfracoes;
	private Map rows;
	private Vector infracoes;
		
	private JTextField codigoTxt;
	private JTextArea nomeTxt;
	private JTextArea enquadramentoTxt;
	private JTextField penalidadeTxt;	
	private JTextField valorTxt;
	/**
	 * bufferRecordPos represents the raw-order was stored in the buffer for 
	 * the record read of table when of starting load of that records
	 */
	private int bufferRecordPos = 0;
	
	private Connection myConnection = null;
	private Statement myStatement = null;
//	private Statement batchStatement = null;
	
	TabInfracoesModel model	= null;
    JTable infracoesTable = null; 	
			
	private String[] columnNames =
	{ "Código", "Descrição", "Enquadramento", "Penalidade", "Valor" };
	
	public SEPFormTabInfracoes( Connection con ) {
		try {
			myConnection = con;
			myStatement = con.createStatement();
			Statement stmt = con.createStatement();
			String query = "SELECT codigoInfracao, descricaoInfracao, " +
			               "enquadramentoInfracao, penalidadeInfracao, valorMultaInfracao " +
			               "FROM tabInfracao ORDER BY codigoInfracao";
			ResultSet rs = stmt.executeQuery( query );
			loadInfracoesTable( rs );	
			
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

	public JPanel createTabInfracoesPanel(){
		
		
		JPanel inputTabInfracoesPanel = new JPanel();
		inputTabInfracoesPanel.setLayout( 
		    new  BoxLayout( inputTabInfracoesPanel, BoxLayout.Y_AXIS ) );  									
		
        JPanel tabInfracoesPanel = new JPanel( new BorderLayout() );		        
		
		JLabel codigoLabel = new JLabel("Código");
		codigoTxt = new JTextField();
		JLabel nomeLabel = new JLabel("Descrição da infração");
		nomeTxt = new JTextArea( 3, 70 );
		JLabel enquadramentoLabel = new JLabel("Enquadramento legal");
		enquadramentoTxt = new JTextArea( 3, 70 );
		enquadramentoTxt.setText( "  " );
		JLabel penalidadeLabel = new JLabel("Penalidade");
		penalidadeTxt = new JTextField();
		JLabel valorLabel = new JLabel("Valor da multa corrigida (IPCA-E)");
		valorTxt = new JTextField();	
		
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder(
		       etched, "Infrações");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabInfracoesPanel.setBorder( titleBorder );					
		
		
		JPanel infracaoPanel = new JPanel( new BorderLayout()  ) ; 
		JScrollPane infracaoScrollPane = new JScrollPane( nomeTxt );
		infracaoPanel.add( nomeLabel, BorderLayout.NORTH  );
		infracaoPanel.add( infracaoScrollPane );	
		
		
		JPanel enquadramentoPanel = new JPanel( new BorderLayout() ); 
		JScrollPane enquadramentoPane = new JScrollPane( enquadramentoTxt );
		enquadramentoPanel.add( enquadramentoLabel, BorderLayout.NORTH );
		enquadramentoPanel.add( enquadramentoPane );
		
		
		JPanel codigoPanel = new JPanel(  new GridLayout( 1, 2, 1, 1 ) );
		codigoPanel.add( codigoLabel);
		codigoPanel.add( codigoTxt  );
		
		
		JPanel penalidadePanel = new JPanel( new GridLayout( 1, 2, 1, 1  ) );
		penalidadePanel.add( penalidadeLabel );
		penalidadePanel.add( penalidadeTxt );
		
		JPanel valorPanel = new JPanel( new GridLayout( 1,2, 1, 1 ));
		valorPanel.add( valorLabel );
		valorPanel.add( valorTxt );
		
		inputTabInfracoesPanel.add( codigoPanel );
		inputTabInfracoesPanel.add( infracaoPanel ); 
		inputTabInfracoesPanel.add( enquadramentoPanel ); 
		inputTabInfracoesPanel.add( penalidadePanel ); 
		inputTabInfracoesPanel.add( valorPanel );
		
		JButton incluirButton = new JButton("Incluir");		
		JButton excluirButton = new JButton("Excluir");
		JButton gravarButton = new JButton("Gravar");
		JButton imprimirButton = new JButton("Imprimir");
		//incluirButton.setEnabled( false );
		//excluirButton.setEnabled( false );
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
		
		imprimirButton.setMnemonic('R');
		imprimirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
					addRecord( myConnection );					
				} catch( SQLException ignore ) {
					ignore.printStackTrace();					
				}    
			}
		});

		
		JPanel controlInfracoesPanel = new JPanel( new FlowLayout() );
		
		controlInfracoesPanel.add( incluirButton );
		controlInfracoesPanel.add( excluirButton );
		excluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
				    deleteRecord( myConnection );					
				} catch( SQLException ignore ) {
					ignore.printStackTrace();					
				}    
			}
		});
		
		controlInfracoesPanel.add( gravarButton );
		controlInfracoesPanel.add( imprimirButton );
		
		tabInfracoesPanel.add( inputTabInfracoesPanel );
				
		model = new TabInfracoesModel();				
		
		infracoesTable = new JTable(); 
		JTableHeader headers = infracoesTable.getTableHeader();
		headers.setReorderingAllowed( false );
		
		infracoesTable.setModel( model );
		infracoesTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		infracoesTable.addMouseListener( new MouseAdapter()  {
			public void mouseClicked( MouseEvent e ) {
				int count = infracoesTable.getSelectedRowCount();
				if ( count > 0 ) {
                    inputTabInfracoesPanelRefresh();					
				}
			}
		});
		
		JScrollPane scrollpane = new JScrollPane( infracoesTable );
		
		JPanel infracoesDisplayPanel = new JPanel( new BorderLayout() ) ;
		
		infracoesDisplayPanel.add( scrollpane );
		
		tabInfracoesPanel.add( inputTabInfracoesPanel, BorderLayout.NORTH );
		tabInfracoesPanel.add( infracoesDisplayPanel, BorderLayout.CENTER );
		tabInfracoesPanel.add( controlInfracoesPanel, BorderLayout.SOUTH );		
		
		return tabInfracoesPanel;	
	}
	
	private void loadInfracoesTable( ResultSet rs ) {	
		try {
			rows = new HashMap();
			infracoes = new Vector();
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
			 	String tmp = new String("");		 	
			 	
			 	String codigo =  rs.getString( 1 );
			 	String descricao = rs.getString( 2 );
			 	String enquadramento = rs.getString( 3 );
			 	String penalidade = rs.getString( 4 );
			 	
			 	tmp = rs.getString( 5 );
			 	String vlrMultaCorrigida = IGNORE;
			 	
			 	if ( !tmp.equals(IGNORE) ) {
					String vlrMulta = 
						SEPConverter.adapterCurrencyFrmt( rs.getString( 5 ) );
					double dummy = Double.parseDouble( vlrMulta ) ;
					String fmtVlrMulta = SEPConverter.getFmtCurrency( dummy );	    	    
					vlrMultaCorrigida = fmtVlrMulta;
			 	}			 	
				
				theRow[ 0 ] = codigo;
				theRow[ 1 ] = descricao;
				theRow[ 2 ] = enquadramento;
				theRow[ 3 ] = penalidade;
				theRow[ 4 ] = vlrMultaCorrigida; 			 	
			 	
/*			 	for ( int j = 0; j < theRow.length; j++ )  {
			 	    theRow[ j ] = (String)rs.getString( j + 1 );
			 	} */
		 	    key = (String) rs.getString( 1 );
			 	rows.put( new Integer(key) , new Integer(  bufferRecordPos ) );	 	
			 	infracoes.add( theRow );
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
	 * @param <code>con</code> represents a Connection object
	 **         necessary for creating the statement  
	 * @throws SQLException if ocurrs some erro with Connection
	 */	
	private void addRecord(Connection con ) throws SQLException {	
		int codigoInfracao = Integer.parseInt( codigoTxt.getText() );
		boolean bufferHas = rows.containsKey( new Integer( codigoInfracao ) );
		if ( bufferHas ) {
			String err = "Tentativa de inserir registro duplicado";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			 System.out.println("incluindo registro.." );
			// -- Includes data in table model, and so does refreshes gui 
			String codigo =  codigoTxt.getText();
			String descricao = nomeTxt.getText();
			String enquadramento = enquadramentoTxt.getText();
			String penalidade = penalidadeTxt.getText();
			String valor = valorTxt.getText();	
			String infracao[] = new String[ columnNames.length ];		
			infracao[ 0 ] = codigo;
			infracao[ 1 ] = descricao;
			infracao[ 2 ] = enquadramento;
			infracao[ 3 ] = penalidade;		
			infracao[ 4 ] = valor;			
			
			// -- The calling for including records is done using batch process
			String cmd = "";
			if ( valor.equals( IGNORE ) ) {
				cmd = "INSERT INTO tabInfracao VALUES  ('" +
				  codigo + "','" +  descricao + "','" + enquadramento + "','" +
				  penalidade + "','" + IGNORE + "')"; 
			}
			else {
				cmd = "INSERT INTO tabInfracao VALUES  ('" +
				  codigo + "','" +  descricao + "','" + enquadramento + "','" +
				  penalidade + "','" + SEPConverter.convertFrmtCurrencyToMySQL(valor) + "')"; 
			}
						
			myStatement.executeUpdate( cmd );
			con.commit();			
			
			infracoes.add( infracao );
			rows.put( new Integer ( codigo ) , new Integer( bufferRecordPos ) );
			bufferRecordPos++;
			
			codigoTxt.setText("");
			nomeTxt.setText("");
			enquadramentoTxt.setText("");
			penalidadeTxt.setText("");			

			
			bufferRefresh();      // updating buffer
			infracoesTable.setModel( model );
			infracoesTable.revalidate();
			infracoesTable.repaint();	
			revalidate();
			repaint();
			
			
			System.out.println( "... inclusão ok. ");
			
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
		int selectedRow = infracoesTable.getSelectedRow();
		// System.out.println(" linha selecionada " + selectedRow );
		// -- primary key of choicen record 
		String value = (String) infracoesTable.getValueAt(selectedRow, 0 );
		//System.out.println(" coluna 0 da linha " + selectedRow + " é = " +
		//                     value );
		                     
		Integer rowID = (Integer) rows.get(  new Integer( value ) );
		// -- position in the buffer of choicen record
		int index = rowID.intValue();
		// System.out.println( " pos = " + rowID );				

		String[] record = new String[ columnNames.length ];	
		record = (String[]) infracoes.get( index  );		
		// System.out.println(" registro " + record[ 0 ] + " removido do vetor " +
        //             " infracoes " );		
                   
		infracoes.remove( index );

		// -- The calling for deleting records is done
		String cmd = "DELETE FROM  tabInfracao" + 
		             " WHERE codigoInfracao = '" + value + "'" ; 
 		myStatement.executeUpdate( cmd );

		con.commit();
		
		bufferRefresh();      // updating buffer
		infracoesTable.setModel( model );
		infracoesTable.revalidate();
		infracoesTable.repaint();	
		revalidate();
		repaint();
					
		
	}
	
	/*	
	 * Updates the selected record currently of infracoesTable table 
	 *
	 * precondition: Must check that there is a subjacent key in database
	 *               if it will update the record
	 * @param <code>con</code> represents a Connection object
	 *         necessary for creating the statement  
	 * @throws SQLException if ocurrs some error with Connection
	 */	
	private void updateRecord( Connection con ) throws SQLException {
		int codigoInfracao = Integer.parseInt( codigoTxt.getText() );
		boolean bufferHas = rows.containsKey( new Integer( codigoInfracao ) );
		if ( !bufferHas ) {
			String err = "Tentativa de atualizar registro inexistente";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			String codigo =  codigoTxt.getText();
			String descricao = nomeTxt.getText();
			String enquadramento = enquadramentoTxt.getText();
			String penalidade = penalidadeTxt.getText();	
			String valor = valorTxt.getText();		
			String infracao[] = new String[ columnNames.length ];		
			infracao[ 0 ] = codigo;
			infracao[ 1 ] = descricao;
			infracao[ 2 ] = enquadramento;
			infracao[ 3 ] = penalidade;		
			infracao[ 4 ] = valor;			
			Integer rowID = (Integer) rows.get(  new Integer( codigo ) );
			// -- position in the buffer of choicen record
			int index = rowID.intValue();
			// -- remove first and insert again. Doing this, is not 
			// -- necessary to worry with refreshing cacher buffer
			infracoes.remove( index );
			infracoes.add( index, infracao );		


			infracoesTable.setModel( model );
			infracoesTable.revalidate();
			
			// -- The calling for updating records is done
			String cmd = "";		
			if ( valor.equals( IGNORE  )) {
				cmd = "UPDATE tabInfracao " + 
							 "SET descricaoInfracao ='" + descricao + "'," +
							 " enquadramentoInfracao ='" + enquadramento + "'," +
							 " penalidadeInfracao ='" + penalidade + "'," +
							 " valorMultaInfracao = '" + IGNORE + "' " +		             
						 " WHERE codigoInfracao = '" + codigo + "'" ; 
			}
			else {
				cmd = "UPDATE tabInfracao " + 
							 "SET descricaoInfracao ='" + descricao + "'," +
							 " enquadramentoInfracao ='" + enquadramento + "'," +
							 " penalidadeInfracao ='" + penalidade + "'," +
							 " valorMultaInfracao = '" + SEPConverter.convertFrmtCurrencyToMySQL(valor) + "' " +		             
						 " WHERE codigoInfracao = '" + codigo + "'" ; 
			}
	 		myStatement.executeUpdate( cmd );		
	 		
			con.commit();			
			
			/*String query = "SELECT codigoInfracao, descricaoInfracao, " +
			               "enquadramentoInfracao, penalidadeInfracao " +
			               "FROM tabInfracao ORDER BY codigoInfracao";
			ResultSet rs = myStatement.executeQuery( query );
			loadInfracoesTable( rs );	
			infracoesTable.revalidate(); */
			
			bufferRefresh();      // updating buffer
			infracoesTable.setModel( model );
			infracoesTable.revalidate();
			infracoesTable.repaint();	
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
    private void inputTabInfracoesPanelRefresh() {   	
    	String value;
		int selectedRow = infracoesTable.getSelectedRow();
		value = (String) infracoesTable.getValueAt(selectedRow, 0 );
		codigoTxt.setText( value );
		value = (String) infracoesTable.getValueAt(selectedRow, 1 );
		nomeTxt.setText( value );
		value = (String) infracoesTable.getValueAt(selectedRow, 2 );
		enquadramentoTxt.setText( value );
		value = (String) infracoesTable.getValueAt(selectedRow, 3 );
		penalidadeTxt.setText( value );		
		value = (String) infracoesTable.getValueAt( selectedRow, 4 );
		valorTxt.setText( value );
		
		revalidate();		
    }						
	
	/**
	 * It will update the rows hash if it deletes a record
	 *
	 */
	private void bufferRefresh() {
		String[] infracao;
		String codigo;
		rows.clear();
		for ( int i = 0; i < infracoes.size(); i++ ) {
			infracao = (String[])  infracoes.get( i );
			codigo = infracao[ 0 ];
			rows.put( new Integer ( codigo ) , new Integer( i ) );		
		}
	}
	
	
	
	private class TabInfracoesModel extends AbstractTableModel {
		
		public int getRowCount() {
			return rows.size();
		}
		
		public Object getValueAt( int r , int c ) {			
		String[] theRow = null ;
		    if ( r < infracoes.size() ) {
		    	Object obj = infracoes.get( r );
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
