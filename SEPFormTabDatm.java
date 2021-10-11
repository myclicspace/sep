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


public class SEPFormTabDatm extends JPanel {

	private Map rows;
	private Vector datms;
	
	/**
	 * Holds information about the choicen company
	 */
	private static String currOS = null;   // ordem de serviço
	private static String currCMC = null;  // insc. municipal
	
	private static final int ANO_COL = 0;
	private static final int MES_COL = 1;
	private static final int DATA_COL = 2;
	private static final int VALOR_COL = 3;
	private static final int CONTROL_SEQ = 4;	
	
	private TabDatmsModel datmsTableModel = null ;		
	private JTable	datmsTable = null; 

		
	private JTextField mesReferenciaTxt = null;
    private JTextField anoReferenciaTxt	= null;
	private JTextField dataPagamentoTxt = null;			
	private JTextField valorPagoTxt = null;
	private SEPFormTabMDATM sepFormTabMDATM  = null;	
	
	/**
	 * bufferRecordPos represents the raw-order was stored in the buffer for 
	 * the record read of table when of starting load of that records
	 */
	private int bufferRecordPos = 0;
	
	private Connection myConnection = null;
	private Statement myStatement = null;
	
			
	private String[] columnNames =
	{ "Ano Referência", "Mês referencia", "Data Pagamento", "Valor Pago", "Data de registro" };	
	
	public SEPFormTabDatm( Connection con ) {
		try {
			myConnection = con;
			myStatement = con.createStatement();
			
			currOS = EmpresaAtual.getNumeroOS();
			currCMC = EmpresaAtual.getCMCEmpresa();
			
			Statement stmt = con.createStatement();
			String query = "SELECT mespagdatm, anopagdatm, datapagdatm," +
			               " valorpagdatm, seqdatm " +
			               " FROM tabDatm " +
			               " WHERE osdatm='" + currOS + "'" +
			               " AND cmcdatm='" + currCMC + "'" +
			               " ORDER BY anopagdatm, mespagdatm" ;
			ResultSet rs = stmt.executeQuery( query );
			loadDatmsTable( rs );	
			
			
		} catch ( SQLException ignore ) {
			System.out.println( ignore.getMessage() );
			ignore.printStackTrace();
		}	
	}

	public JPanel createTabDatmsPanel(){
		
		
        JPanel tabDatmPanel = new JPanel( new BorderLayout() );		
		JPanel inputTabDatmPanel = new JPanel( new GridLayout( 1, 6, 5, 5 ) );
		
		JLabel mesReferenciaLabel = new JLabel("Mês Referência");
		mesReferenciaTxt = new JTextField( 
						new FixedNumericDocument( 2, true ), "", 2 );
		mesReferenciaTxt.setToolTipText("Formato válido: MM ");                 
						
		
		JLabel anoReferenciaLabel = new JLabel("Ano Referência");
		anoReferenciaTxt = new JTextField( 
						new FixedNumericDocument( 4, true ), "", 4 );
		mesReferenciaTxt.setToolTipText("Formato válido: AAAA ");                 
		
		
		// identificação do documento
//		JLabel dataDocumentoLabel = new JLabel("Data da emissão");

		JLabel dataPagamentoLabel = new JLabel("Data Pagmto.");
		dataPagamentoTxt = new JTextField( 
						 new FixedNumericDocument( 10, false ), "", 8 );
		dataPagamentoTxt.setToolTipText("Formato válido: DD/MM/AAAA");                 
		dataPagamentoTxt.addKeyListener( new FmtPaymentDataListener() );		
		
		JLabel valorPagoLabel = new JLabel("Valor Pago");
		valorPagoTxt = new JTextField(
			new FixedNumericDocument( 14, false ), "0,00", 14 );
		valorPagoTxt.setToolTipText("Formato válido: 99.999,99 ou 99999,99");
		valorPagoTxt.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				fmtCurrencyValues();					
			}
		});		
		
		
		dataPagamentoTxt.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
				    String paymentDate = dataPagamentoTxt.getText();
				    StringTokenizer st = new StringTokenizer( paymentDate,"/");
				    String dummy = st.nextToken();
				    String month = st.nextToken();
				    String year = st.nextToken();
				    mesReferenciaTxt.setText( month );
				    anoReferenciaTxt.setText( year );
				} catch (NoSuchElementException ex ) {
					String err = "Formato correto é : DD/MM/AAAA.";
					JOptionPane.showMessageDialog( null, err,
				            "Formato de data inválido", JOptionPane.ERROR_MESSAGE );

				}    
		
			}
		});
		
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Datm");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabDatmPanel.setBorder( titleBorder );					
		
		inputTabDatmPanel.add( dataPagamentoLabel ); 
		inputTabDatmPanel.add( dataPagamentoTxt );
		inputTabDatmPanel.add( mesReferenciaLabel );
		inputTabDatmPanel.add( mesReferenciaTxt ); 
		inputTabDatmPanel.add( anoReferenciaLabel );
		inputTabDatmPanel.add( anoReferenciaTxt ); 
		inputTabDatmPanel.add( valorPagoLabel ); 
		inputTabDatmPanel.add( valorPagoTxt );
		
		JButton incluirButton = new JButton("Incluir");		
		JButton excluirButton = new JButton("Excluir");
		JButton gravarButton = new JButton("Gravar");
		
		incluirButton.setMnemonic('I');
		incluirButton.setToolTipText("Cadastra pagamento");
		
		
		excluirButton.setMnemonic('E');
		excluirButton.setToolTipText("Excluir registro de pagamento");
		
		gravarButton.setMnemonic('G');
		gravarButton.setToolTipText("Atualiza pagamento");
		
		JPanel controlDatmPanel = new JPanel( new FlowLayout() );	
	
		
		incluirButton.setMnemonic('I');
		incluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
		//			fmtDataCompetencia();					
					fmtCurrencyValues()	;				
				    addRecord( myConnection );					
				} catch( SQLException ignore ) {
					ignore.printStackTrace();					
				}    
			}
		});
		
		
		gravarButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
		//			fmtDataCompetencia();					
					fmtCurrencyValues()	;				
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
		
		JButton imprimirButton = new JButton("Imprimir");
		imprimirButton.setToolTipText("Imprimir mapa de apuração");
		imprimirButton.setMnemonic('R');
		imprimirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
/*				try {
					
					Statement stmt = myConnection.createStatement();

					String query = "SELECT mespagdatm, anopagdatm, datapagdatm," +
								   " valorpagdatm, seqdatm " +
								   " FROM tabDatm " +
								   " WHERE osdatm='" + currOS + "'" +
								   " AND cmcdatm='" + currCMC + "'" +
								   " ORDER BY anopagdatm, mespagdatm" ;
   	    	
					ResultSet rs = stmt.executeQuery( query );   	    
					printReportMapaDATM( rs );
				}
				catch ( SQLException sqlEx ) { 
					  sqlEx.printStackTrace() ;	
				} */
			}
		}); 		
		
		
		
		controlDatmPanel.add( incluirButton );
		controlDatmPanel.add( excluirButton );
		controlDatmPanel.add( gravarButton );
//		controlDatmPanel.add( imprimirButton );		
		
		tabDatmPanel.add( inputTabDatmPanel );
				
		datmsTableModel = new TabDatmsModel();		
		datmsTable = new JTable(); 
		datmsTable.setModel( datmsTableModel );
		
		JTableHeader headers = datmsTable.getTableHeader();
		headers.setReorderingAllowed( false );
		
		datmsTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		datmsTable.addMouseListener( new MouseAdapter()  {
			public void mouseClicked( MouseEvent e ) {
				int count = datmsTable.getSelectedRowCount();
				if ( count > 0 ) {
                    inputTabDatmsPanelRefresh();					
				}
			}
		});
		
		
		JScrollPane scrollpane = new JScrollPane( datmsTable );
		
		JPanel datmDisplayPanel = new JPanel( new BorderLayout() ) ;
		
		datmDisplayPanel.add( scrollpane );
		
		tabDatmPanel.add( inputTabDatmPanel, BorderLayout.NORTH );
		tabDatmPanel.add( datmDisplayPanel, BorderLayout.CENTER );
		tabDatmPanel.add( controlDatmPanel, BorderLayout.SOUTH );		
		
		return tabDatmPanel;	
		
	}
	
	private void loadDatmsTable( ResultSet rs ) {	
		try {
			rows = new HashMap();
			datms = new Vector();
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

			 	String mesRef = (String) rs.getString( 1 );
			 	String anoRef = (String) rs.getString( 2 );			 				 	
			 	String readData = (String) rs.getString( 3 );
			 	
			 	String dataPag = SEPConverter.converteFrmtDataFromMySQL( readData );
			 	
				String vlrIssPago = 
					SEPConverter.adapterCurrencyFrmt( rs.getString( 4 ) );
				double dummy = Double.parseDouble( vlrIssPago ) ;
				String fmtVlrIssPago = getFmtCurrency( dummy );	    	    
				String valorPag = fmtVlrIssPago;
			 	
			 				 	
				String controlSeqLanc = rs.getString( 5 );
			 	
				theRow[ ANO_COL ] = anoRef;			 	
			 	theRow[ MES_COL ] = mesRef;
			 	theRow[ DATA_COL ] = dataPag;
			 	theRow[ VALOR_COL ] = valorPag;
			 	theRow[ CONTROL_SEQ ] = controlSeqLanc;
			 	
		 	    key = controlSeqLanc;
		 	    
			 	rows.put( key , new Integer(  bufferRecordPos ) );	 	
			 	datms.add( theRow );
			 	bufferRecordPos++;	 	
			 	
			 }
			 
		}
		catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		}
	}
	
	private void printReportMapaDATM( ResultSet rs ) {
		String[] columnsNames = { "ANO REF.", "MÊS REF.", "DATA PGTO.",
								 "VALOR PAGO", "TOTAL ACUMULADO PAGO" };

		Vector rows = new Vector();
		double vlrAcum = 0;
		
		try {	   	
	   
			 while ( rs.next() ) {
	        	
				 String[] theRow = new String[ columnsNames.length ];
	    	    
				 String mesRef = rs.getString( 1 );
				 String anoRef = rs.getString( 2 );
				 String dataPagto =
				            SEPConverter.converteFrmtDataFromMySQL( 
				                   rs.getString( 3 ) );
				 	    	    
	    	    
				 String dummy = 
					 SEPConverter.adapterCurrencyFrmt( rs.getString( 4 ) );
				 double vlrPagtoISSQN = Double.parseDouble( dummy ) ;
				 String fmtVlrPagtoISSQN = SEPConverter.getFmtCurrency( vlrPagtoISSQN );
	    	     String vlrPagto = fmtVlrPagtoISSQN;
	    	     
	    	     vlrAcum += vlrPagtoISSQN;
	    	     String fmtTotalAcumPago = 
				          SEPConverter.getFmtCurrency( vlrAcum );
				 String totalAcumPago = fmtTotalAcumPago;         

				 theRow[ 0 ] = anoRef;
				 theRow[ 1 ] = mesRef;
				 theRow[ 2 ] = dataPagto;
				 theRow[ 3 ] = vlrPagto;
				 theRow[ 4 ] = totalAcumPago;
				
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

		java.util.Date now = new java.util.Date();
		long timeStamp = now.getTime();
		String strTimeStamp = Long.toString( timeStamp );
		DateFormat longTimestamp
		   = DateFormat.getDateTimeInstance( DateFormat.FULL, DateFormat.FULL );			          
		 java.util.Date dummy =  new java.util.Date( timeStamp );
		 String controlSeqLanc = longTimestamp.format( dummy );	
		
		String key = controlSeqLanc;	    
	    
		boolean bufferHas = rows.containsKey( key );
		if ( bufferHas ) {
			String err = "Tentativa de inserir registro duplicado";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			// -- Includes data in table model, and so does refreshes gui 
			String datm[] = new String[ columnNames.length ];		
			String mes =  mesReferenciaTxt.getText();
			String ano = anoReferenciaTxt.getText();
			String data = dataPagamentoTxt.getText();			
			String valor = valorPagoTxt.getText();		
					 
			datm[ ANO_COL ] = ano;
			datm[ MES_COL ] = mes;
			datm[ DATA_COL ] = data;
			datm[ VALOR_COL ] = valor;
			datm[ CONTROL_SEQ ] = controlSeqLanc;
			
			// The chunck of data is right,then proceed to addding them to database
			String cmd = "INSERT INTO tabDatm " +
			" VALUES  ('"  + currOS + "','" + currCMC + "','" 
			               +  mes + "','" +  ano  + "','"
			               +  SEPConverter.converteFrmtDataToMySQL(data) + "','"
			               +  SEPConverter.convertFrmtCurrencyToMySQL(valor)  + "','" 
                           +  controlSeqLanc + " ')"; 
			               
			myStatement.executeUpdate( cmd );
			con.commit();			
			
			datms.add( datm );
			rows.put( key  , new Integer( bufferRecordPos ) );
			bufferRecordPos++;
			
			valorPagoTxt.setText("0,00");
			
			bufferRefresh();      // updating buffer
			datmsTable.setModel( datmsTableModel );
			datmsTable.revalidate();
			datmsTable.repaint();	
			revalidate();
			repaint();
			
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
		
		int selectedRow = datmsTable.getSelectedRow();
		// -- primary key into to choicen record 
		String controlSeq 
		   = (String) datmsTable.getValueAt(selectedRow, CONTROL_SEQ );   
		
		String searchKey = controlSeq;
		
		                     
		Integer rowID = (Integer) rows.get( searchKey );
		
		// -- position in the buffer of choicen record
		int index = rowID.intValue();
                   
		datms.remove( index );
		bufferRefresh();                // updating buffer
		datmsTable.setModel( datmsTableModel );
		datmsTable.revalidate();
		datmsTable.repaint();

		// -- The calling for deleting records is done
		String cmd = "DELETE FROM  tabDatm" + 
		             " WHERE osDatm = '" + currOS + "'" +
		             " AND cmcDatm ='" + currCMC + "'" +
	 	             " AND seqDatm ='" + searchKey + "'";		             

 		myStatement.executeUpdate( cmd );

		con.commit();
		
		bufferRefresh();      // updating buffer
		datmsTable.setModel( datmsTableModel );
		datmsTable.revalidate();
		datmsTable.repaint();	
		revalidate();
		repaint();
					
		
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

		int selectedRow = datmsTable.getSelectedRow();		
		
	    
		// -- primary key into to choicen record 
		 String controlSeq 
			= (String) datmsTable.getValueAt( selectedRow, CONTROL_SEQ );
		
		 String searchKey = controlSeq;	    
	    
		boolean bufferHas = rows.containsKey( searchKey );
		if ( !bufferHas ) {
			String err = "Tentativa de atualizar registro inexistente";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			String datm[] = new String[ columnNames.length ];		
			
			String mes =  mesReferenciaTxt.getText();
			String ano =  anoReferenciaTxt.getText();
			String data = dataPagamentoTxt.getText();
			String valor = valorPagoTxt.getText();
			
			java.util.Date now = new java.util.Date();
			long timeStamp = now.getTime();
			String strTimeStamp = Long.toString( timeStamp );
			DateFormat longTimestamp
			   = DateFormat.getDateTimeInstance( DateFormat.FULL, DateFormat.FULL );			          
			 java.util.Date dummy =  new java.util.Date( timeStamp );
			 String controlSeqLanc = longTimestamp.format( dummy );
			
			datm[ ANO_COL ] = ano;
			datm[ MES_COL ] = mes;
			datm[ DATA_COL ] = data;
			datm[ VALOR_COL ] = valor;
			datm[ CONTROL_SEQ ] = controlSeqLanc;
			

			String str = SEPConverter.convertFrmtCurrencyToMySQL(valor);
			String dataPag = SEPConverter.converteFrmtDataToMySQL(data);
			
		    System.out.println(" numero OS = " + currOS );
			
			String cmd = "UPDATE tabDatm "
						  +  "SET valorPagDatm ='"  +  str  + "', "
						  +  "dataPagDatm='" +  data + "', "
						  +  "anoPagDatm='" + ano + "', "
						  +  "mesPagDatm='" + mes + "', "
						  +  "seqDatm='" + controlSeqLanc + "'"  						   
						  +  " WHERE osDatm ='" + currOS + "'" 
						  +  " AND cmcDatm ='" + currCMC + "'" 
 			              +  " AND seqDatm ='" + searchKey + "'" ; 
					
						  
			myStatement.executeUpdate( cmd );		
	 		
			con.commit();	
			
			Integer rowID = (Integer) rows.get(  searchKey  );
			// -- position in the buffer of choicen record
			int index = rowID.intValue();
			// -- remove first and insert again. Doing this, is not 
			// -- necessary to worry with refreshing cacher buffer
			datms.remove( index );			
			datms.add( index, datm );
			bufferRefresh();                // updating buffer				

			bufferRefresh();      // updating buffer
			datmsTable.setModel( datmsTableModel );
			datmsTable.revalidate();
			datmsTable.repaint();	
			revalidate();
			repaint();
			
			System.out.println( " gravou " );		
			
		}		
		 
	}
	
	/**
	 *  It will update the inputTabInfracoesPanel if the user selects
	 *  one row in displayTablInfracoesPanel
	 *
	 */
    private void inputTabDatmsPanelRefresh() {   	
    	String value;
		int selectedRow = datmsTable.getSelectedRow();
		
		String ano = (String) datmsTable.getValueAt( selectedRow, 0 );
		anoReferenciaTxt.setText( ano );

		String mes = (String) datmsTable.getValueAt(selectedRow, 1 );
		mesReferenciaTxt.setText( mes );
		
		String data = (String) datmsTable.getValueAt(selectedRow, 2 );
		dataPagamentoTxt.setText( data );
	
	    String valor = (String) datmsTable.getValueAt( selectedRow, 3 );
	    valorPagoTxt.setText( valor );	
	    
		
		revalidate();		
    }						
	
	/**
	 * It will update the rows hash if it deletes a record
	 *
	 */
	private void bufferRefresh() {
		String[] datm = new String[ columnNames.length ];
		String searchKey;
		rows.clear();
		for ( int i = 0; i < datms.size(); i++ ) {
			datm = (String[])  datms.get( i );
			String controlSeq = datm[ CONTROL_SEQ ];
			searchKey = controlSeq;
			rows.put( searchKey  , new Integer( i ) );		
		}
	}
	
	
	
	private class TabDatmsModel extends AbstractTableModel {
		
		public int getRowCount() {
			return rows.size();
		}
		
		public Object getValueAt( int r , int c ) {			
		String[] theRow = null ;
		    if ( r < datms.size() ) {
		    	Object obj = datms.get( r );
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
	
	private class FmtPaymentDataListener extends KeyAdapter {
		String searchKey = "";
		boolean f = false;
		public void keyTyped(KeyEvent e ) {
			if ( f == false ) {
				char ch = e.getKeyChar();
				if ( !Character.isLetterOrDigit( ch ))
					return;
				searchKey += Character.toLowerCase( ch );
			}
			if ( searchKey.length() > 0 && f == true ) {
				searchKey = "" ;
				dataPagamentoTxt.setText("");
				f = false;
			}
			if ( searchKey.length() == 8 ) {
				String day = searchKey.substring( 0, 2 );
				String month = searchKey.substring( 2, 4 );
				String year = searchKey.substring( 4, 8);				
				dataPagamentoTxt.setText("");
				String dummy = day + "/" + month + "/" + year;

				dataPagamentoTxt.setText( dummy );
				
				StringTokenizer st = 
					new StringTokenizer( dataPagamentoTxt.getText(),"/");
				String dd = st.nextToken();
				String mm = st.nextToken();
				String yy = st.nextToken();		
				String date =  mm + "/" + yy;
				mesReferenciaTxt.setText( mm );
				anoReferenciaTxt.setText( yy );
								
				f = true;
				return;				   
			}
		}
	}
	
	private void fmtCurrencyValues() {
		  // Computes the values from taxes due   
		  String vlrPago = valorPagoTxt.getText();
		  String value = SEPConverter.adapterCurrencyFrmt( vlrPago );
		  double vlrPagoNominal = Double.parseDouble( value ) ;
		  String fmtVlrPago = getFmtCurrency( vlrPagoNominal );
		  valorPagoTxt.setText("");
		  valorPagoTxt.setText( fmtVlrPago );
	}
	
	private void fmtDataCompetencia() {
		String paymentDate = dataPagamentoTxt.getText();
		StringTokenizer st = new StringTokenizer( paymentDate,"/");
		String dummy = st.nextToken();
		String month = st.nextToken();
		String year = st.nextToken();
		mesReferenciaTxt.setText( month );
		anoReferenciaTxt.setText( year );
	}
	
	private String getFmtCurrency( double value ){	
		NumberFormat nf = NumberFormat.getInstance( Locale.GERMANY );
		nf.setMaximumFractionDigits( 2 );
		nf.setMinimumFractionDigits( 2 );
		String formattedNumber = nf.format( value );		
		return formattedNumber;
	} 	
	
	
}
