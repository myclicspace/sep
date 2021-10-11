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

import sep.util.GeneratorReport;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.text.*; 


public class SEPFormTabParcelamento extends JPanel {

	private Map rows;
	private Vector parcelamentos;
	
	/**
	 * Holds information about the choicen company
	 */
	private static String currOS = null;   // ordem de serviço
	private static String currCMC = null;  // insc. municipal
	
	
	private TabParcelamentosModel parcelamentosTableModel = null;		
	private JTable	parcelamentosTable = null; 
	

	private JTextField processoTxt = null;
	private JTextField mesReferenciaTxt = null;			
	private JTextField anoReferenciaTxt = null;
	private JTextField valorOriginalTxt = null;	
	private JTextField impostoParcelamentoTxt = null;
	
	/**
	 * bufferRecordPos represents the raw-order was stored in the buffer for 
	 * the record read of table when of starting load of that records
	 */
	private int bufferRecordPos = 0;
	
	private Connection myConnection = null;
	private Statement myStatement = null;
	
			
	private String[] columnNames =
	{ "Número do processo", "Mes referência", "Ano referência", 
            "Receita confessada paga", "Imposto recolhido"};	
	
	public SEPFormTabParcelamento( Connection con ) {
		try {
			myConnection = con;
			myStatement = con.createStatement();
			
			currOS = EmpresaAtual.getNumeroOS();
			currCMC = EmpresaAtual.getCMCEmpresa();
			
			Statement stmt = con.createStatement();
			String query = "SELECT numprocparcelamento, mesparcelamento, " +
			               " anoparcelamento, valorparcelamento, impostoparcelamento " +
			               " FROM tabParcelamento " +
			               " WHERE osparcelamento='" + currOS + "'" +
			               " AND cmcparcelamento='" + currCMC + "'" +
			               " ORDER BY osparcelamento, cmcparcelamento," +
			                          "numprocparcelamento, mesparcelamento " ;
			ResultSet rs = stmt.executeQuery( query );
			loadParcelamentosTable( rs );	
			
			
		} catch ( SQLException ignore ) {
			System.out.println( ignore.getMessage() );
			ignore.printStackTrace();
		}	
	}

	public JPanel createTabParcelamentosPanel(){
		
		
        JPanel tabParcelamentoPanel = new JPanel( new BorderLayout() );		
		JPanel inputTabParcelamentoPanel = new JPanel( new GridLayout( 5, 2, 5, 5 ) );
		
		JLabel processoLabel = new JLabel("Processo");
		processoTxt = new JTextField( 20 );
		JLabel mesReferenciaLabel = new JLabel("Mês Referência");		
		mesReferenciaTxt = new JTextField( 
						new FixedNumericDocument( 2, true ), "", 2 );
		mesReferenciaTxt.setToolTipText("Formato válido: MM ");                 
		
		JLabel anoReferenciaLabel = new JLabel("Ano Referência");
		anoReferenciaTxt = new JTextField( 
						new FixedNumericDocument( 4, true ), "", 4 );
		JLabel valorOriginalLabel = new JLabel("Receita confessada paga");
		//valorOriginalTxt = new JTextField( 20 );
		valorOriginalTxt = new JTextField(
			new FixedNumericDocument( 14, false ), "0,00", 14 );
		valorOriginalTxt.setToolTipText("Formato válido: 99.999,99 ou 99999,99");
		valorOriginalTxt.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				fmtCurrencyValues();					
			}
		});		
		

		JLabel impostoLabel = new JLabel("Valor ISSQN pago");
		//valorOriginalTxt = new JTextField( 20 );
		impostoParcelamentoTxt = new JTextField(
			new FixedNumericDocument( 14, false ), "0,00", 14 );
		impostoParcelamentoTxt.setToolTipText("Formato válido: 99.999,99 ou 99999,99");
		impostoParcelamentoTxt.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				fmtCurrencyValues();					
			}
		});		

		
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Parcelamento");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabParcelamentoPanel.setBorder( titleBorder );					
		
		inputTabParcelamentoPanel.add( processoLabel );
		inputTabParcelamentoPanel.add( processoTxt ); 
		inputTabParcelamentoPanel.add( mesReferenciaLabel ); 
		inputTabParcelamentoPanel.add( mesReferenciaTxt );
		inputTabParcelamentoPanel.add( anoReferenciaLabel ); 
		inputTabParcelamentoPanel.add( anoReferenciaTxt );
		inputTabParcelamentoPanel.add( valorOriginalLabel ); 
		inputTabParcelamentoPanel.add( valorOriginalTxt );
		inputTabParcelamentoPanel.add( impostoLabel ); 
		inputTabParcelamentoPanel.add( impostoParcelamentoTxt );
		
		
		JButton incluirButton = new JButton("Incluir");		
		JButton excluirButton = new JButton("Excluir");
		JButton gravarButton = new JButton("Gravar");
		
		incluirButton.setMnemonic('I');
		incluirButton.setToolTipText("Cadastra parcelamento");
		
		
		excluirButton.setMnemonic('E');
		excluirButton.setToolTipText("Excluir registro de parcelamento");
		
		gravarButton.setMnemonic('G');
		gravarButton.setToolTipText("Atualiza parcelamento");
		
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
		
		JButton imprimirButton = new JButton("Imprimir");
		imprimirButton.setToolTipText("Imprimir mapa de apuração");
		imprimirButton.setMnemonic('R');
		imprimirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
					Statement stmt = myConnection.createStatement();

					String query = "SELECT numprocparcelamento, mesparcelamento, " +
								   " anoparcelamento, valorparcelamento, impostoParcelamento " +
								   " FROM tabParcelamento " +
								   " WHERE osparcelamento='" + currOS + "'" +
								   " AND cmcparcelamento='" + currCMC + "'" +
								   " ORDER BY numprocparcelamento, anoparcelamento," +
											  "mesparcelamento" ;

					ResultSet rs = stmt.executeQuery( query );   	    
					printReportMapaParcelamento( rs );
				}
				catch ( SQLException sqlEx ) { 
					  sqlEx.printStackTrace() ;	
				}
			}
		}); 		
		
		
		JPanel controlParcelamentoPanel = new JPanel( new FlowLayout() );
		
		controlParcelamentoPanel.add( incluirButton );
		controlParcelamentoPanel.add( excluirButton );
		controlParcelamentoPanel.add( gravarButton );
		//controlParcelamentoPanel.add( imprimirButton );
		tabParcelamentoPanel.add( inputTabParcelamentoPanel );
				
		parcelamentosTableModel = new TabParcelamentosModel();		
		parcelamentosTable = new JTable(); 
		
		JTableHeader headers = parcelamentosTable.getTableHeader();
		headers.setReorderingAllowed( false );
		
		parcelamentosTable.setModel( parcelamentosTableModel );
		parcelamentosTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		parcelamentosTable.addMouseListener( new MouseAdapter()  {
			public void mouseClicked( MouseEvent e ) {
				int count = parcelamentosTable.getSelectedRowCount();
				if ( count > 0 ) {
                    inputTabParcelamentosPanelRefresh();					
				}
			}
		});

		
		JScrollPane scrollpane = new JScrollPane( parcelamentosTable );
		
		JPanel parcelamentoDisplayPanel = new JPanel( new BorderLayout() ) ;
		
		parcelamentoDisplayPanel.add( scrollpane );
		
		tabParcelamentoPanel.add( inputTabParcelamentoPanel, BorderLayout.NORTH );
		tabParcelamentoPanel.add( parcelamentoDisplayPanel, BorderLayout.CENTER );
		tabParcelamentoPanel.add( controlParcelamentoPanel, BorderLayout.SOUTH );		
		
		return tabParcelamentoPanel;	
		
		
	}
	
	private void loadParcelamentosTable( ResultSet rs ) {	
		try {
			rows = new HashMap();
			parcelamentos = new Vector();
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
			 	
			 	/*for ( int j = 0; j < theRow.length; j++ )  {
			 	    theRow[ j ] = (String)rs.getString( j + 1  );
			 	} */
			 	
			 	String numProcesso = (String) rs.getString( 1 );
			 	String mesReferencia = (String) rs.getString( 2 );			 	
			 	String anoReferencia = (String) rs.getString( 3 );
			 	
				String vlrPago = 
					SEPConverter.adapterCurrencyFrmt( rs.getString( 4 ) );
				double dummy = Double.parseDouble( vlrPago ) ;
				String fmtVlrPago = getFmtCurrency( dummy );	    	    
				String receitaTributavel = fmtVlrPago;
				
				String vlrIssPago = 
					SEPConverter.adapterCurrencyFrmt( rs.getString( 5 ) );
				dummy = Double.parseDouble( vlrIssPago ) ;
				String fmtVlrIssPago = getFmtCurrency( dummy );	    	    
				String valorIss = fmtVlrIssPago;
			 	
			 	theRow[ 0 ] = numProcesso;
			 	theRow[ 1 ] = mesReferencia;
			 	theRow[ 2 ] = anoReferencia;
			 	theRow[ 3 ] = receitaTributavel;
				theRow[ 4 ] = valorIss;

			 	
		 	    key = numProcesso + mesReferencia + anoReferencia ;
		 	    
			 	rows.put( key , new Integer(  bufferRecordPos ) );	 	
			 	parcelamentos.add( theRow );
			 	bufferRecordPos++;	 	
			 	
			 }
			 
		}
		catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		}
	}
	
	
	private void printReportMapaParcelamento( ResultSet rs ) {
		String[] columnsNames = { "NRO. PROCESSO", "ANO REF.", "MES REF.",
								 "VALOR RECEITA", "TOTAL RECEITA ACUMULADA",
								 "VALOR ISSQN", "TOTAL ISSQN ACUMULADO" };

		Vector rows = new Vector();
		double vlrRecAcum = 0;
		double vlrISSQNAcum = 0;
		
		try {	   	
	   
			 while ( rs.next() ) {
	        	
				 String[] theRow = new String[ columnsNames.length ];
	    	    
				 String numProc = rs.getString( 1 );
				 String mesRef = rs.getString( 2 );
				 String anoRef = rs.getString( 3 );
	    	    
				 String dummy = 
					 SEPConverter.adapterCurrencyFrmt( rs.getString( 4 ) );
				 double vlrReceita = Double.parseDouble( dummy ) ;
				 String fmtVlrReceita = SEPConverter.getFmtCurrency( vlrReceita );
				 String vlrRecBruta = fmtVlrReceita;
	    	     
				 vlrRecAcum += vlrReceita;
				 String fmtTotalRecAcum = 
						  SEPConverter.getFmtCurrency( vlrRecAcum );
				 String totalRecAcumPago = fmtTotalRecAcum;         

				dummy = 
					SEPConverter.adapterCurrencyFrmt( rs.getString( 5 ) );
				double vlrPagtoISSQN = Double.parseDouble( dummy ) ;
				String fmtVlrPagtoISSQN = SEPConverter.getFmtCurrency( vlrPagtoISSQN );
				String vlrISSQNPagto = fmtVlrPagtoISSQN;
	    	     
				vlrISSQNAcum += vlrPagtoISSQN;
				String fmtTotalAcumPago = 
						 SEPConverter.getFmtCurrency( vlrISSQNAcum );
				String totalAcumISSQNPago = fmtTotalAcumPago;         



				 theRow[ 0 ] = numProc;
				 theRow[ 1 ] = anoRef;
				 theRow[ 2 ] = mesRef;
				 theRow[ 3 ] = vlrRecBruta;
				 theRow[ 4 ] = totalRecAcumPago;
				 theRow[ 5 ] = vlrISSQNPagto;
				 theRow[ 6 ] = totalAcumISSQNPago;
				
				 rows.add( theRow );
			 }
		}    
		catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		}
		
		String[][] values = new String[ rows.size()][ columnsNames.length ];
		for ( int i = 0 ; i < rows.size(); i++ ) {
			Object obj =  rows.get( i ) ;
			String[] rec = (String[]) obj;
			for ( int j = 0; j < columnsNames.length; j++ ) {
//				System.out.println( "rec[" + j + "]= " +  rec[j]);
				values[ i ][ j ] = rec[ j ];
//				System.out.println( "values[" + i + "]= " + values[ i ][ j ]);
			}     
		}
		new GeneratorReport( columnsNames, values, rows.size(), myConnection );
		
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


	    String numProcesso = processoTxt.getText();
	    String mesReferencia = mesReferenciaTxt.getText();
	    String anoReferencia = anoReferenciaTxt.getText();
	    String valorParcelamento = valorOriginalTxt.getText();
	    String valorISSQN = impostoParcelamentoTxt.getText();
	    
	    String key = numProcesso + mesReferencia  + anoReferencia ;
	    
		boolean bufferHas = rows.containsKey( key );
		if ( bufferHas ) {
			String err = "Tentativa de inserir registro duplicado";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			// -- Includes data in table model, and so does refreshes gui 
			String parcelamento[] = new String[ columnNames.length ];		
			parcelamento[ 0 ] = numProcesso;
			parcelamento[ 1 ] = mesReferencia;
			parcelamento[ 2 ] = anoReferencia;
			parcelamento[ 3 ] = valorParcelamento;
			parcelamento[ 4 ] = valorISSQN;
			
			String ignore = "0";
						
			// The chunck of data is right,then proceed to addding them to database
			String cmd = "INSERT INTO tabParcelamento " +
			" VALUES  ('"  + currOS + "','" + currCMC + "','" 
			               +  numProcesso + "','" +  mesReferencia + "','"
			               + anoReferencia + "','" 
			   + SEPConverter.convertFrmtCurrencyToMySQL( valorParcelamento) + "','"
			   + SEPConverter.convertFrmtCurrencyToMySQL( valorISSQN) + " ')"; 
			               
			myStatement.executeUpdate( cmd );
			con.commit();			
			
			parcelamentos.add( parcelamento );
			rows.put( key  , new Integer( bufferRecordPos ) );
			bufferRecordPos++;
			
			processoTxt.setText("");
			mesReferenciaTxt.setText("");
			anoReferenciaTxt.setText("");
			valorOriginalTxt.setText("0,00");
			impostoParcelamentoTxt.setText("0,00");
			
			bufferRefresh();                // updating buffer
			parcelamentosTable.setModel( parcelamentosTableModel );
			parcelamentosTable.revalidate();
			parcelamentosTable.repaint();
			
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
		
		int selectedRow = parcelamentosTable.getSelectedRow();
		// -- primary key into to choicen record 
		String numProcesso = (String) parcelamentosTable.getValueAt(selectedRow, 0 );
		String mesReferencia = (String) parcelamentosTable.getValueAt( selectedRow, 1 );
		String anoReferencia = (String) parcelamentosTable.getValueAt( selectedRow, 2 );
		String searchKey = numProcesso + mesReferencia + anoReferencia ;
		
		                     
		Integer rowID = (Integer) rows.get( searchKey );
		
		// -- position in the buffer of choicen record
		int index = rowID.intValue();
                   
		parcelamentos.remove( index );
		bufferRefresh();                // updating buffer
		parcelamentosTable.setModel( parcelamentosTableModel );
		parcelamentosTable.revalidate();
		parcelamentosTable.repaint();

		// -- The calling for deleting records is done
		String cmd = "DELETE FROM  tabParcelamento" + 
		             " WHERE osParcelamento = '" + currOS + "'" +
		             " AND cmcParcelamento ='" + currCMC + "'" +
		             " AND numProcParcelamento ='" + numProcesso + "'" +
		             " AND mesParcelamento ='" + mesReferencia + "'"  +
		             " AND anoParcelamento = '" + anoReferencia + "'" ; 
 		myStatement.executeUpdate( cmd );

		con.commit();			
		
		System.out.println(" removeu " );
		
		
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
		
		// -- primary key into to choicen record 
		String numProcesso = processoTxt.getText();
		String mesReferencia = mesReferenciaTxt.getText();
		String anoReferencia = anoReferenciaTxt.getText();
		String valorParcelamento = valorOriginalTxt.getText();
		String impostoParcelamento = impostoParcelamentoTxt.getText();
		
		String searchKey = numProcesso + mesReferencia + anoReferencia ;


		boolean bufferHas = rows.containsKey( searchKey );
		if ( !bufferHas ) {
			String err = "Tentativa de atualizar registro inexistente";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			String parcelamento[] = new String[ columnNames.length ];		
			
			parcelamento[ 0 ] = numProcesso;
			parcelamento[ 1 ] = mesReferencia;
			parcelamento[ 2 ] = anoReferencia;
			parcelamento[ 3 ] = valorParcelamento;
			parcelamento[ 4 ] = impostoParcelamento; 
			
			Integer rowID = (Integer) rows.get(  searchKey  );
			// -- position in the buffer of choicen record
			int index = rowID.intValue();
			// -- remove first and insert again. Doing this, is not 
			// -- necessary to worry with refreshing cacher buffer
			parcelamentos.remove( index );
			parcelamentos.add( index, parcelamento );		

			parcelamentosTable.setModel( parcelamentosTableModel );
			parcelamentosTable.revalidate();
			parcelamentosTable.repaint(); 
			
			// -- The calling for updating records is done
			// Here, is upadted just the field valor because
			// the other fields behaviour as unique keys
			
			System.out.println( " valorParcelamento= " + valorParcelamento );
			System.out.println( " valor convertido = " +
			SEPConverter.convertFrmtCurrencyToMySQL(valorParcelamento) );
			
			
			String cmd = "UPDATE tabParcelamento "
					  +  "SET valorParcelamento ='" + 
		      SEPConverter.convertFrmtCurrencyToMySQL(valorParcelamento) + "', "
			   + "impostoParcelamento ='"
			   + SEPConverter.convertFrmtCurrencyToMySQL(impostoParcelamento) + "'" 
			   +  " WHERE osParcelamento ='" + currOS + "'" 
			   +  " AND cmcParcelamento ='" + currCMC + "'" 
			   +  " AND numProcParcelamento ='" + numProcesso + "'" 
			   +  " AND mesParcelamento ='" + mesReferencia + "'"  
			   +  " AND anoParcelamento = '" + anoReferencia + "'" ; 
			    
			
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
    private void inputTabParcelamentosPanelRefresh() {   	
    	String value;
		int selectedRow = parcelamentosTable.getSelectedRow();
		
		String processo = (String) parcelamentosTable.getValueAt(selectedRow, 0 );
		processoTxt.setText( processo );
		
		String mes = (String) parcelamentosTable.getValueAt(selectedRow, 1 );
		mesReferenciaTxt.setText( mes );
		
		String ano = (String) parcelamentosTable.getValueAt( selectedRow, 2 ) ;
		anoReferenciaTxt.setText( ano );
	
	    String valor = (String) parcelamentosTable.getValueAt( selectedRow, 3 );
	    valorOriginalTxt.setText( valor );	

		String issqn = (String) parcelamentosTable.getValueAt( selectedRow, 4 );
		impostoParcelamentoTxt.setText( issqn );	

		
		revalidate();		
    }						
	
	/**
	 * It will update the rows hash if it deletes a record
	 *
	 */
	private void bufferRefresh() {
		String[] parcelamento = new String[ columnNames.length ];
		String searchKey;
		rows.clear();
		for ( int i = 0; i < parcelamentos.size(); i++ ) {
			parcelamento = (String[])  parcelamentos.get( i );
			String processo = parcelamento[ 0 ];
			String mes = parcelamento[ 1 ];
			String ano = parcelamento[ 2 ] ;
			searchKey = processo + mes + ano ;
			rows.put( searchKey  , new Integer( i ) );		
		}
	}
	
	
	
	private class TabParcelamentosModel extends AbstractTableModel {
		
		public int getRowCount() {
			return rows.size();
		}
		
		public Object getValueAt( int r , int c ) {			
		String[] theRow = null ;
		    if ( r < parcelamentos.size() ) {
		    	Object obj = parcelamentos.get( r );
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
	
	private void fmtCurrencyValues() {
		  // Computes the values from taxes due   
		  String vlrPago = valorOriginalTxt.getText();
		  String value = SEPConverter.adapterCurrencyFrmt( vlrPago );
		  double vlrPagoNominal = Double.parseDouble( value ) ;
		  String fmtVlrPago = getFmtCurrency( vlrPagoNominal );
		  valorOriginalTxt.setText("");
		  valorOriginalTxt.setText( fmtVlrPago );
	}
	
	private String getFmtCurrency( double value ){	
		NumberFormat nf = NumberFormat.getInstance( Locale.GERMANY );
		nf.setMaximumFractionDigits( 2 );
		nf.setMinimumFractionDigits( 2 );
		String formattedNumber = nf.format( value );		
		return formattedNumber;
	} 	
	
	
	
	
}
