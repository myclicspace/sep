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

public class SEPFormTabServicos extends JPanel {

	private int[] arrCodigoServicos;
	private Map rows;
	private Vector servicos;
		
	private JTextField codigoTxt;
	private JTextArea nomeTxt;
	private JTextField aliquotaTxt;	
	/**
	 * bufferRecordPos represents the raw-order was stored in the buffer for 
	 * the record read of table when of starting load of that records
	 */
	private int bufferRecordPos = 0;
	
	private Connection myConnection = null;
	private Statement myStatement = null;
//	private Statement batchStatement = null;
	
	TabServicosModel model	= null;
    JTable servicosTable = null; 	
			
	private String[] columnNames =
	{ "Código", "Descrição do serviço", "Alíquota do serviço" };
	
	public SEPFormTabServicos( Connection con ) {
		try {
			myConnection = con;
			myStatement = con.createStatement();
			Statement stmt = con.createStatement();
			String query = "SELECT codigoServico, descricaoServico, " +
			               "aliquotaServico " +
			               "FROM tabServicos ORDER BY codigoServico";
			ResultSet rs = stmt.executeQuery( query );
			loadServicosTable( rs );
			
			
		} catch ( SQLException ignore ) {
			ignore.printStackTrace();
		}	
	}

	public JPanel createTabServicosPanel(){
		
		
		JPanel inputTabServicosPanel = new JPanel();
		inputTabServicosPanel.setLayout( 
		    new  BoxLayout( inputTabServicosPanel, BoxLayout.Y_AXIS ) );  									
		
        JPanel tabServicosPanel = new JPanel( new BorderLayout() );		        
		
		JLabel codigoLabel = new JLabel("Item da lista");
		codigoTxt = new JTextField();
		JLabel nomeLabel = new JLabel("Discriminação das atividades");
		nomeTxt = new JTextArea( 3, 70 );
		JLabel aliquotaLabel = new JLabel("Alíquota");
		aliquotaTxt = new JTextField("0,00");	
		
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder(
		       etched, "Lista de serviços");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabServicosPanel.setBorder( titleBorder );					
		
		
		JPanel descricaoItemPanel = new JPanel( new BorderLayout()  ) ; 
		JScrollPane descricaoItemScrollPane = new JScrollPane( nomeTxt );
		descricaoItemPanel.add( nomeLabel, BorderLayout.NORTH  );
		descricaoItemPanel.add( descricaoItemScrollPane );	
		
		
		JPanel codigoPanel = new JPanel(  new GridLayout( 1, 2, 1, 1 ) );
		codigoPanel.add( codigoLabel);
		codigoPanel.add( codigoTxt  );
		
		
		JPanel aliquotaPanel = new JPanel( new GridLayout( 1, 2, 1, 1  ) );
		aliquotaPanel.add( aliquotaLabel );
		aliquotaPanel.add( aliquotaTxt );
		
		inputTabServicosPanel.add( codigoPanel );
		inputTabServicosPanel.add( descricaoItemPanel ); 
		inputTabServicosPanel.add( aliquotaPanel ); 
		
		JButton incluirButton = new JButton("Incluir");
		incluirButton.setEnabled( false );		
		JButton excluirButton = new JButton("Excluir");
		excluirButton.setEnabled( false );
		JButton gravarButton = new JButton("Gravar");
		gravarButton.setEnabled( false );
		
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
		
		JPanel controlServicosPanel = new JPanel( new FlowLayout() );
		
		controlServicosPanel.add( incluirButton );
		controlServicosPanel.add( excluirButton );
		excluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
				    deleteRecord( myConnection );					
				} catch( SQLException ignore ) {
					ignore.printStackTrace();					
				}    
			}
		});
		
		controlServicosPanel.add( gravarButton );
		tabServicosPanel.add( inputTabServicosPanel );
				
		model = new TabServicosModel();				
		
		servicosTable = new JTable(); 
		JTableHeader headers = servicosTable.getTableHeader();
		headers.setReorderingAllowed( false );
		
		
		servicosTable.setModel( model );
		servicosTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		servicosTable.addMouseListener( new MouseAdapter()  {
			public void mouseClicked( MouseEvent e ) {
				int count = servicosTable.getSelectedRowCount();
				if ( count > 0 ) {
                    inputTabServicosPanelRefresh();					
				}
			}
		});
		
		JScrollPane scrollpane = new JScrollPane( servicosTable );
		
		JPanel servicosDisplayPanel = new JPanel( new BorderLayout() ) ;
		
		servicosDisplayPanel.add( scrollpane );
		
		tabServicosPanel.add( inputTabServicosPanel, BorderLayout.NORTH );
		tabServicosPanel.add( servicosDisplayPanel, BorderLayout.CENTER );
		tabServicosPanel.add( controlServicosPanel, BorderLayout.SOUTH );		
		
		return tabServicosPanel;	
	}
	
	private void loadServicosTable( ResultSet rs ) {	
		try {
			rows = new HashMap();
			servicos = new Vector();
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
			 	
			 	String item = rs.getString( 1 );
			 	String descricaoItem = rs.getString( 2 );
			 	String aliquota = rs.getString( 3 ) ; 
			 	
			 	theRow[ 0 ] = item;
			 	theRow[ 1 ] = descricaoItem;
			 	theRow[ 2 ] = SEPConverter.convertFrmtCurrencyFromMySQL(aliquota);
			 	
		 	    key = (String) rs.getString( 1 );
			 	rows.put( new Integer(key) , new Integer(  bufferRecordPos ) );	 	
			 	servicos.add( theRow );
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
	 * @param <code>con</code> represents a Connection object
	 **         necessary for creating the statement  
	 * @throws SQLException if ocurrs some erro with Connection
	 */	
	private void addRecord(Connection con ) throws SQLException {	
		int codigoServico = Integer.parseInt( codigoTxt.getText() );
		boolean bufferHas = rows.containsKey( new Integer( codigoServico ) );
		if ( bufferHas ) {
			String err = "Tentativa de inserir registro duplicado";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			// -- Includes data in table model, and so does refreshes gui 
			String codigo =  codigoTxt.getText();
			String descricao = nomeTxt.getText();
			String aliquota = aliquotaTxt.getText();	
			String servico[] = new String[ 3 ];		
			servico[ 0 ] = codigo;
			servico[ 1 ] = descricao;
			servico[ 2 ] = aliquota;					
			
			
			// -- The calling for including records is done using batch process			
			String cmd = "INSERT INTO tabServicos VALUES  ('" +
			  codigo + "','" +  descricao + "','"  + aliquota +  "')"; 
			myStatement.executeUpdate( cmd );
			con.commit();			
			
			servicos.add( servico );
			rows.put( new Integer ( codigo ) , new Integer( bufferRecordPos ) );
			bufferRecordPos++;
			
			codigoTxt.setText("");
			nomeTxt.setText("");
			aliquotaTxt.setText("0,00");

			bufferRefresh();                // updating buffer
			servicosTable.setModel( model );
			servicosTable.revalidate();
			repaint();
			
			System.out.println( "... inclusão ok. ");

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
		int selectedRow = servicosTable.getSelectedRow();
		
		// -- primary key of choicen record 
		String value = (String) servicosTable.getValueAt(selectedRow, 0 );
		                     
		Integer rowID = (Integer) rows.get(  new Integer( value ) );
		// -- position in the buffer of choicen record
		int index = rowID.intValue();

		String[] record = new String[ 3 ];	
		record = (String[]) servicos.get( index  );		
                   
		servicos.remove( index );
		bufferRefresh();                // updating buffer
		servicosTable.setModel( model );
		servicosTable.revalidate();

		// -- The calling for deleting records is done
		String cmd = "DELETE FROM  tabServicos" + 
		             " WHERE codigoServico = '" + value + "'" ; 
 		myStatement.executeUpdate( cmd );

		con.commit();			
		
	}
	
	/*	
	 * Updates the selected record currently of servicosTable table 
	 *
	 * precondition: Must check that there is a subjacent key in database
	 *               if it will update the record
	 * @param <code>con</code> represents a Connection object
	 *         necessary for creating the statement  
	 * @throws SQLException if ocurrs some error with Connection
	 */	
	private void updateRecord( Connection con ) throws SQLException {
		int codigoServico = Integer.parseInt( codigoTxt.getText() );
		boolean bufferHas = rows.containsKey( new Integer( codigoServico ) );
		if ( !bufferHas ) {
			String err = "Tentativa de atualizar registro inexistente";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			
			String codigo =  codigoTxt.getText();
			String descricao = nomeTxt.getText();
			String aliquota = aliquotaTxt.getText();			
			String servico[] = new String[ 3 ];		
			servico[ 0 ] = codigo;
			servico[ 1 ] = descricao;			
			servico[ 2 ] = aliquota;
			
			Integer rowID = (Integer) rows.get(  new Integer( codigo ) );
			// -- position in the buffer of choicen record
			int index = rowID.intValue();
			// -- remove first and insert again. Doing this, is not 
			// -- necessary to worry with refreshing cacher buffer
			servicos.remove( index );
			servicos.add( index, servico );		


			servicosTable.setModel( model );
			servicosTable.revalidate();
			servicosTable.repaint();
			
			
			// -- The calling for updating records is done
			String cmd = "UPDATE tabServicos " + 
			             "SET descricaoServico ='" + descricao + "'," +
			             " aliquotaServico ='"    
			              +  SEPConverter.convertFrmtCurrencyFromMySQL(aliquota) + "'" +		             
		             " WHERE codigoServico = '" + codigo + "'" ; 
	 		myStatement.executeUpdate( cmd );		
	 		
			con.commit();			
			
			System.out.print(" gravou " );
			
			
		}		
		
	}
	
	/**
	 *  It will update the inputTabServicosPanel if the user selects
	 *  one row in displayTablServicosPanel
	 *
	 */
    private void inputTabServicosPanelRefresh() {   	
    	String value;
		int selectedRow = servicosTable.getSelectedRow();
		value = (String) servicosTable.getValueAt(selectedRow, 0 );
		codigoTxt.setText( value );
		value = (String) servicosTable.getValueAt(selectedRow, 1 );
		nomeTxt.setText( value );
		value = (String) servicosTable.getValueAt(selectedRow, 2 );
		aliquotaTxt.setText( value );
		
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
		for ( int i = 0; i < servicos.size(); i++ ) {
			infracao = (String[])  servicos.get( i );
			codigo = infracao[ 0 ];
			rows.put( new Integer ( codigo ) , new Integer( i ) );		
		}
	}
	
	
	
	private class TabServicosModel extends AbstractTableModel {
		
		public int getRowCount() {
			return rows.size();
		}
		
		public Object getValueAt( int r , int c ) {			
		String[] theRow = null ;
		    if ( r < servicos.size() ) {
		    	Object obj = servicos.get( r );
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
