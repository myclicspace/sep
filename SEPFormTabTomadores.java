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

import sep.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.*;
import java.util.*;  
 

/**
 * 
 * @author FCARLOS
 *
 * PENDENCIAS:
 * 1. Permitir entrada de dados e apos clicar <ENTER> colocar o campo 
 *    em um formato de mascara correto ( CMC e CNPJ)
 * 2. Após incluir registro colocar o campo sem a mascara na area de exibicao
 * 3. Ao selecionar um campo da grid exibir na tela de ediçao com o formato
 *    correto de mascara
 * 4. Verificar se e neciessario colocar um mascara no formato monetario
 * 5. Verificar comportamentos dos campos apos teclar <TAB>
 * 6. Construir tela para definir perfil de entrada de dados
 */

public class SEPFormTabTomadores extends JPanel {
	
	
	/** 
	 * Number of fields for the  mapaEscolar form
	 */
	private static final int FIELDS_QTY = 10;


	private static final int LOCAL = 1;
	private static final int GERAL = 2;
	
	private static final int CMC_TOMADOR = 0 ;
	private static final int NOME_TOMADOR = 1;
	private static final int CNPJ_TOMADOR = 2;
	private static final int DATA_DOC = 3;
	private static final int TIPO_DOC =  4;
	private static final int SERIE = 5;
	private static final int SUBSERIE = 6 ;
	private static final int NUMERO_DOC = 7;
	private static final int VALOR_DOC =  8;
	private static final int CONTROL_SEQ = 9;	


	 /**
	  * Mapeia posição do registro selecionada da tabela para um buffer
	  */
	private static int bufferRecordPos = 0;
	
	
	private static String currOS = null; // currently service order openned
	private static String currCMC = null; // currently company choicen
	
	/**
	 * Indicates a buffer manages records into a corresponding service order
	 * Also, is responsible for basic operation of insertion, deleting and
	 * updating of the records in the database
	 */
	private Map mapaTomadorRows = null;
	
	/**
	 * Indicates the source of the table model
	 */
	private Vector mapaTomador = null;
	
	
			
	private String[] columnNames = { "CMC Tomador", "Nome do Tomador", "CNPJ",
									"Data Emissão",  "Tipo Documento", 
									"Série", "Sub-Série", "N° Documento",
									"Valor Documento", "Data de registro"
									 };
	
	private String tipos[] = { "NFS", "Recibo", "Outro" };	
	

	// identificação do tomador do serviço
    private static JTextField cmcTomadorTxt = null;
	private static JTextField nomeTomadorTxt = null;
	private static JTextField cnpjTxt = null;		
	private static JTextField dataDocumentoTxt = null;
	private static JComboBox tipoDocumentoComboBox = null;
	private static JTextField serieTxt = null;
	private static JTextField subserieTxt = null;
	private static JTextField numeroDocumentoTxt = null;
	private static JTextField valorDocumentoTxt = null;
	
	
	TabMapaTomadorModel documentosTableModel = null;		
	private static JTable tomadoresTable = null; 
	
	
	private Connection myConnection = null;
	private Statement myStatement = null;
	
	private SEPLookupTomadores sepLookupTomadores = null;
	private SEPViewerTomadores sepViewerTomadores = null;	
		
	
   
	public SEPFormTabTomadores ( Connection con ) {
		
		try {
			myConnection = con;
			myStatement = con.createStatement();
			
			currOS = EmpresaAtual.getNumeroOS();
			currCMC = EmpresaAtual.getCMCEmpresa();
			
			String query = " SELECT cmcTomadorMapaTomador, nomeMapaTomador, " +
			               " cnpjMapaTomador, dataEmissaoMapaTomador, " +
			               " tipoDocMapaTomador, serieMapaTomador, " +
			               " subSerieMapaTomador, numeroDocMapaTomador, " +
			               " valorDocMapaTomador, seqMapaTomador " +
			               " FROM tabMapaTomador " +
			               " WHERE osMapaTomador ='" + currOS + "'" +
			               "  AND cmcPrestadorMapaTomador ='" + currCMC + "'" +
			               " ORDER BY cmcTomadorMapaTomador ";
			              
			ResultSet rs =  myStatement.executeQuery( query );
			// Getting the records of the database
			loadMapaTomadoresTable( rs );	
			
			sepLookupTomadores = new SEPLookupTomadores(con);
			
		}
		catch ( SQLException ignore ) {
			ignore.printStackTrace();
		}
			
		
		
	}

	
	/*private void loadTomadoresCombo() {
		String[] cnpjTomadores = sepLookupTomadores.getCmcTomadores();
		Arrays.sort( cnpjTomadores );
		for ( int i = 0; i < cnpjTomadores.length; i++ ) {
			cmcTomadorCombo.addItem( new String( cnpjTomadores[ i ] ));
		}
	} */



	public JPanel createTabTomadoresPanel(){
		
		JPanel tabTomadoresPanel = new JPanel( new BorderLayout() );
		
		JPanel inputTabTomadoresPanel = new JPanel( new GridLayout( 3, 2, 5, 5 ) );
								
		JLabel cmcTomadorLabel = new JLabel("CMC do prestador");
		
		cmcTomadorTxt = new JTextField( new FixedNumericDocument( 9, false ), "", 9 );
		cmcTomadorTxt.setToolTipText("Formato válido: 999999-9 ou 9999999");
		cmcTomadorTxt.addKeyListener( new CmcKeySearcher());
		cmcTomadorTxt.addActionListener( new MaskCMCHandler());
		JLabel nomeTomadorLabel = new JLabel("Nome do prestador");
		nomeTomadorTxt = new JTextField( new FixedNumericDocument( 20, false ), "", 20 ); 
		nomeTomadorTxt.addKeyListener( new TomadoresKeySearcher() );
		JLabel cnpjLabel = new JLabel("CNPJ");
		
		cnpjTxt = new JTextField( new FixedNumericDocument( 18, false ), "", 18 );
		cnpjTxt.setToolTipText("Formato válido: 12.345.678/1234-12 ou 12345678901234");                 
			
		cnpjTxt.addKeyListener( new CnpjKeySearcher() );
		cnpjTxt.addActionListener( new MaskCNPJHandler() );	
		JLabel dataDocumentoLabel = new JLabel("Data da Emissão");
		dataDocumentoTxt = new JTextField( new FixedNumericDocument( 10, false ), "", 10 );
		dataDocumentoTxt.setToolTipText("Formato válido: DD/MM/AAAA ou DDMMAAAA");                 
		dataDocumentoTxt.addKeyListener( new FmtPaymentDataListener() );
				
		JLabel tipoDocumentoLabel = new JLabel("Tipo Documento");
		tipoDocumentoComboBox = new JComboBox( tipos );
		JLabel serieLabel = new JLabel("Série"); 
		serieTxt = new JTextField(new FixedNumericDocument( 4, false ), "", 4 );
		
		JLabel subserieLabel = new JLabel("Sub-Série");
		subserieTxt = new JTextField(new FixedNumericDocument( 4, false ), "", 4 );
		
		JLabel numeroDocumentoLabel = new JLabel("Número Documento");
		numeroDocumentoTxt = 
		    new JTextField(new FixedNumericDocument( 6, false ), "", 6 );		
		
		JLabel valorDocumentoLabel = new JLabel("Valor do Documento (R$)");
		valorDocumentoTxt = new JTextField(
		    new FixedNumericDocument( 14, false ), "0,00", 14 );
		valorDocumentoTxt.setToolTipText("Formato válido: 99.999,99 ou 99999,99");
		valorDocumentoTxt.addActionListener( new MaskCurrencyHandler() );    
		
		
	//	loadTomadoresCombo();
		
        Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Prestadores");
        titleBorder.setTitleJustification( TitledBorder.RIGHT );

		inputTabTomadoresPanel.setBorder( titleBorder );

		inputTabTomadoresPanel.add( cmcTomadorLabel );
		inputTabTomadoresPanel.add( cmcTomadorTxt ); 
		inputTabTomadoresPanel.add( nomeTomadorLabel ); 
		inputTabTomadoresPanel.add( nomeTomadorTxt );
		inputTabTomadoresPanel.add( cnpjLabel ); 
		inputTabTomadoresPanel.add( cnpjTxt );
		inputTabTomadoresPanel.add( dataDocumentoLabel ); 
		inputTabTomadoresPanel.add( dataDocumentoTxt );
		inputTabTomadoresPanel.add( tipoDocumentoLabel ); 
		inputTabTomadoresPanel.add( tipoDocumentoComboBox );
		inputTabTomadoresPanel.add( serieLabel ); 
		inputTabTomadoresPanel.add( serieTxt );
		inputTabTomadoresPanel.add( subserieLabel ); 
		inputTabTomadoresPanel.add( subserieTxt );
		inputTabTomadoresPanel.add( numeroDocumentoLabel ); 
		inputTabTomadoresPanel.add( numeroDocumentoTxt );
		inputTabTomadoresPanel.add( valorDocumentoLabel ); 
		inputTabTomadoresPanel.add( valorDocumentoTxt );
					
		JButton incluirButton = new JButton("Incluir");
		incluirButton.setMnemonic('I');
		incluirButton.setToolTipText("Cadastra tomador do serviço");
		incluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
					fmtCurrencyValues();					
				    addRecord( myConnection );					
				} catch( SQLException ignore ) {
					ignore.printStackTrace();					
				}    
			}
		});
		
		
		JButton excluirButton = new JButton("Excluir");
		excluirButton.setMnemonic('E');
		excluirButton.setToolTipText("Excluir tomador do serviço");
		excluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
				    deleteRecord( myConnection );					
				} catch( SQLException ignore ) {
					ignore.printStackTrace();					
				}    
			}
		});
		
		
		JButton gravarButton = new JButton("Gravar");
		gravarButton.setMnemonic('G');
		gravarButton.setToolTipText("Atualiza mudanças no tomador do serviço ");
		gravarButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
					fmtCurrencyValues();					
					updateRecord( myConnection );
				} catch ( SQLException ignore ) {
					ignore.printStackTrace();
				}    
			}
		}); 		
		
		JButton imprimirButton = new JButton("Imprimir");
		imprimirButton.setToolTipText("Imprimir mapa de tomadores");
		imprimirButton.setMnemonic('R');
		imprimirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
			}
		}); 		
		
		
		JButton buscarLocalButton = new JButton("Pesquisa serviços");
		buscarLocalButton.setToolTipText("Lista serviços cadastrados apenas nesta OS");
		buscarLocalButton.setMnemonic('P');
		buscarLocalButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				sepViewerTomadores =
				   new SEPViewerTomadores( myConnection, true );
				sepViewerTomadores.setVisible( true ); 
			}
		}); 		
		
		
		

		JPanel controlTomadoresPanel = new JPanel( new FlowLayout() );
		
		controlTomadoresPanel.add( incluirButton );
		controlTomadoresPanel.add( excluirButton );
		controlTomadoresPanel.add( gravarButton );
		//controlTomadoresPanel.add( imprimirButton );		
		controlTomadoresPanel.add( buscarLocalButton );
				
		
		documentosTableModel = new TabMapaTomadorModel();		
		tomadoresTable = new JTable();
		JTableHeader headers = tomadoresTable.getTableHeader();
		headers.setReorderingAllowed( false );
		 
		tomadoresTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		tomadoresTable.addMouseListener( new MouseAdapter()  {
			public void mouseClicked( MouseEvent e ) {
				int count = tomadoresTable.getSelectedRowCount();
				if ( count > 0 ) {
                    inputTabMapaTomadorPanelRefresh();					
				}
			}
		});
		
		
		tomadoresTable.setModel( documentosTableModel );
		JScrollPane scrollpane = new JScrollPane( tomadoresTable );
		
		JPanel tomadoresDisplayPanel = new JPanel( new BorderLayout() ) ;
		
		tomadoresDisplayPanel.add( scrollpane );	
		
		
		tabTomadoresPanel.add( inputTabTomadoresPanel, BorderLayout.NORTH );
		tabTomadoresPanel.add( tomadoresDisplayPanel, BorderLayout.CENTER );
		tabTomadoresPanel.add( controlTomadoresPanel, BorderLayout.SOUTH );		
		
		return tabTomadoresPanel;	
		 
	}
	
	

	
	
	/**
	 * Sets the textfield named cmcTomadorTxt 
	 * @param cmcTomador indicates the value for setting
	 */
	public static void setCmcTomadorTxt( String cmcTomador ) { 
		cmcTomadorTxt.setText( cmcTomador );		
	}
	/**
	 * Sets the textfield named nomeTomadorTxt
	 * @param nomeTomador indicates the value for setting
	 */
	public static void setNomeTomadorTxt( String nomeTomador ) {
		nomeTomadorTxt.setText( nomeTomador );
	}
	
	/**
	 * Sets the textfield named cnpjTxt
	 * @param cnpjTomador indicates the value for setting
	 */
	public static void setCnpjTomadorTxt( String cnpjTomador ) {
		cnpjTxt.setText( cnpjTomador );
	}
	
	/**
	 * Sets the textfield named dataDocumentoTxt
	 * @param dataEmissao indicates the value for setting
	 */
	public static void setDataEmissaoTxt( String dataEmissao ) {
		dataDocumentoTxt.setText( dataEmissao );
	}
	
	/**
	 * Sets the combox named tipoDocumentoCombo
	 * @param index indicates the value for setting
	 */
	public static void setTipoDocCombo( int index ) {
		tipoDocumentoComboBox.setSelectedIndex( index - 1  );
	}
	
	public static String getTipoDoc() {
		String retVal = (String) tipoDocumentoComboBox.getSelectedItem();		
		return retVal;
	} 
	
	
	/**
	 * Sets the textfield named serieTxt
	 * @param serie indicates the value for setting
	 */
	public static void setSerieTxt( String serie ) {
		serieTxt.setText( serie );
	}
	
	/**
	 * Sets the textfield named subserieTxt
	 * @param subserie indicates the value for setting
	 */
	public static void setSubSerieTxt( String subserie ) {
		subserieTxt.setText( subserie );
	}

	/**
	 * Sets the textfield named subserieTxt
	 * @param subserie indicates the value for setting
	 */
	public static void setNumDocTxt( String numDoc ) {
		numeroDocumentoTxt.setText( numDoc );
	}

	/**
	 * Sets the textfield named valorDocumentoTxt
	 * @param valorDocumento indicates the value for setting
	 */
	public static void setValorDocTxt( String valorDoc ) {
		valorDocumentoTxt.setText( valorDoc );
	}

	public static void setSelectedRow( String timestampSelected ) {
		int numRows = tomadoresTable.getRowCount();
		for ( int row = 0; row < numRows ; row++ ) {
			String timestamp = (String) tomadoresTable.getValueAt( row, CONTROL_SEQ );
			if ( timestampSelected.equals( timestamp ))
				tomadoresTable.setRowSelectionInterval( row, row );
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

		java.util.Date now = new java.util.Date();
		long timeStamp = now.getTime();
		String strTimeStamp = Long.toString( timeStamp );
		DateFormat longTimestamp
		   = DateFormat.getDateTimeInstance( DateFormat.FULL, DateFormat.FULL );			          
		 java.util.Date dummy =  new java.util.Date( timeStamp );
		 String controlSeqLanc = longTimestamp.format( dummy );	
		
		String key = controlSeqLanc;    
		
	    
		boolean bufferHas = mapaTomadorRows.containsKey( key );
		if ( bufferHas ) {
			String err = "Tentativa de inserir registro duplicado";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			
			// identificação do tomador
			String cmcTomador = removeMaskCMC(cmcTomadorTxt.getText());
			String nomeTomador = nomeTomadorTxt.getText();
			String cnpjTomador = removeMaskCNPJ(cnpjTxt.getText());
			String dataDoc = dataDocumentoTxt.getText();
		
			int  tipo = tipoDocumentoComboBox.getSelectedIndex() + 1 ;
			String tipoDoc = Integer.toString( tipo );
		
			String serie = serieTxt.getText();
			String subserie = subserieTxt.getText();
			String numeroDoc = numeroDocumentoTxt.getText();
			String valorDoc = valorDocumentoTxt.getText();
		
			    	
			// -- Includes data in table model, and so does refreshes gui 
		    String tomador[] = new String[ FIELDS_QTY ];				
		    tomador[   CMC_TOMADOR  ] = cmcTomador;				
		    tomador[   NOME_TOMADOR  ] = nomeTomador;
		    tomador[   CNPJ_TOMADOR  ] = cnpjTomador;
		    tomador[   DATA_DOC  ] = dataDoc;
		    tomador[   TIPO_DOC  ] = tipoDoc;
		    tomador[   SERIE  ] = serie;
		    tomador[   SUBSERIE  ] = subserie;
		    tomador[   NUMERO_DOC  ] = numeroDoc;
		    tomador[   VALOR_DOC  ] = valorDoc;
		    tomador[   CONTROL_SEQ  ] = controlSeqLanc; 
		    
		    
	
						
		    // The chunck of data is right,then proceed to addding them to database
		    String cmd = "INSERT INTO tabMapaTomador " +
		         " VALUES  ('"  + currOS + "','" + currCMC + "','" 
		              + cmcTomador + "','" + nomeTomador + "','"
					  + SEPConverter.convertFrmtCNPJToMySql(cnpjTomador) + "','"	            
		              + SEPConverter.converteFrmtDataToMySQL(dataDoc) + "','"
		              + tipoDoc + "','" + serie + "','" 
		              + subserie + "','" + numeroDoc + "','"
		              + SEPConverter.convertFrmtCurrencyToMySQL(valorDoc) + "','" 
			          +  controlSeqLanc + " ')"; 		               
 			               
		    myStatement.executeUpdate( cmd );
		    con.commit();	
						
		    mapaTomador.add( tomador );
			
		    bufferRefresh();                // updating buffer
		    tomadoresTable.setModel( documentosTableModel );
		    tomadoresTable.revalidate();
		    tomadoresTable.repaint();
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
		
		
		int selectedRow = tomadoresTable.getSelectedRow();
		
		// -- primary key into to choicen record 
		String controlSeq 
		   = (String) tomadoresTable.getValueAt(selectedRow, CONTROL_SEQ );   
		
		String searchKey = controlSeq;
	    
		Integer rowID = (Integer) mapaTomadorRows.get( searchKey );
		
	
	 	System.out.println( " rowID = " + rowID );
		// -- position in the buffer of choicen record
		int index = rowID.intValue(); 
		
		System.out.println( "index = " + index );		
                   
		mapaTomador.remove( index );
		bufferRefresh();                // updating buffer
		tomadoresTable.setModel( documentosTableModel );
		tomadoresTable.revalidate();
		tomadoresTable.repaint();
		revalidate();
		repaint();

		String cmd = "DELETE FROM  tabMapaTomador" + 
		             " WHERE osMapaTomador = '" + currOS + "'" +
		             " AND cmcPrestadorMapaTomador ='" + currCMC + "'" +
		             " AND seqMapaTomador ='" + searchKey + "'"  ; 
  		             
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
		
		int selectedRow = tomadoresTable.getSelectedRow();		
		
		// -- primary key into to choicen record 
		String controlSeq 
		   = (String) tomadoresTable.getValueAt( selectedRow, CONTROL_SEQ );
		
		String searchKey = controlSeq;	    
		
		boolean bufferHas = mapaTomadorRows.containsKey( searchKey );
		if ( !bufferHas ) {
			String err = "Tentativa de atualizar registro inexistente";
			JOptionPane.showMessageDialog( null, err,
			      "Atualizacao inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {	
						
		    // identificação do tomador
			String cmcTomador = removeMaskCMC(cmcTomadorTxt.getText());
		    String nomeTomador = nomeTomadorTxt.getText();
			String cnpjTomador = removeMaskCNPJ(cnpjTxt.getText());
		    String dataDoc = dataDocumentoTxt.getText();
		
		    int  tipo = tipoDocumentoComboBox.getSelectedIndex() + 1 ;
		    String tipoDoc = Integer.toString( tipo );
		
		
		    String serie = serieTxt.getText();
		    String subserie = subserieTxt.getText();
		    String numeroDoc = numeroDocumentoTxt.getText();
		    String valorDocumento = valorDocumentoTxt.getText();
		    
			java.util.Date now = new java.util.Date();
			long timeStamp = now.getTime();
			String strTimeStamp = Long.toString( timeStamp );
			DateFormat longTimestamp
			   = DateFormat.getDateTimeInstance( DateFormat.FULL, DateFormat.FULL );			          
			 java.util.Date dummy =  new java.util.Date( timeStamp );
			 String controlSeqLanc = longTimestamp.format( dummy );		    
		    
			
			 String tomador[] = new String[ FIELDS_QTY ];
			        
			 tomador[   CMC_TOMADOR  ] = cmcTomador;
			 tomador[   NOME_TOMADOR  ] = nomeTomador;
			 tomador[   CNPJ_TOMADOR  ] = cnpjTomador;
			 tomador[   DATA_DOC  ] = dataDoc;
			 tomador[   TIPO_DOC  ] = tipoDoc;
			 tomador[   SERIE  ] = serie;
			 tomador[   SUBSERIE  ] = subserie;
			 tomador[   NUMERO_DOC  ] = numeroDoc;
			 tomador[   VALOR_DOC  ] = valorDocumento;
	 		 tomador[   CONTROL_SEQ  ] = controlSeqLanc;
			        

			
			 String cmd = "UPDATE tabMapaTomador "
			       + "SET nomeMapaTomador ='" + nomeTomador + "', "
//				   	  + "cnpjMapaTomador ='" +  cnpjTomador + "', "
			          + "cnpjMapaTomador ='" + 
			                 SEPConverter.convertFrmtCNPJToMySql(cnpjTomador) + "', "
			          + "dataEmissaoMapaTomador ='"   
			          + SEPConverter.converteFrmtDataToMySQL(dataDoc) + "', "
			          + "tipoDocMapaTomador ='" + tipoDoc + "', " 
			          + "serieMapaTomador ='" + serie + "', "
			          + "subSerieMapaTomador ='" +  subserie + "', "
			          + "numeroDocMapaTomador ='" +  numeroDoc + "', "
			          + "valorDocMapaTomador ='" 
			          +  SEPConverter.convertFrmtCurrencyToMySQL(valorDocumento) + "', "
					  +  "seqMapaTomador='" + controlSeqLanc + "'" 						   
				      +  " WHERE osMapaTomador ='" + currOS + "'"
			 		  +  " AND seqMapaTomador ='" + searchKey + "'" ; 
				       
//					  +  " AND cmcPrestadorMapaTomador ='" + currCMC + "'" 
//					  +  " AND cmcTomadorMapaTomador ='" + cmcTomador + "'"  ;
			
			
	 		myStatement.executeUpdate( cmd );		
	 		
			con.commit();	
			System.out.println( " gravou " );
			         
			Integer rowID = (Integer) mapaTomadorRows.get(  searchKey  );
			// -- position in the buffer of choicen record
			int index = rowID.intValue();
			// -- remove first and insert again. Doing this, is not 
			// -- necessary to worry with refreshing cacher buffer
			mapaTomador.remove( index );
			mapaTomador.add( index, tomador );
			bufferRefresh();                // updating buffer				

			tomadoresTable.setModel( documentosTableModel );
			tomadoresTable.revalidate();
			tomadoresTable.repaint();  
			         
			         
			
		}		
		 
	}	


	/**
	 *  It will update textfield when a record is selected inthe TabEmpresasPanel 
	 */
    private void inputTabMapaTomadorPanelRefresh() {   	
    
    
    	String value;    	
		int selectedRow = tomadoresTable.getSelectedRow();
		
		
		// identificação do curso
		value = (String) tomadoresTable.getValueAt(selectedRow, 0 );
		cmcTomadorTxt.setText( insertMaskCMC(value) );			
		value = (String) tomadoresTable.getValueAt(selectedRow, 1 );
		nomeTomadorTxt.setText( value );
		value = (String) tomadoresTable.getValueAt(selectedRow, 2 );
		cnpjTxt.setText( insertMaskCNPJ(value) );
		value = (String) tomadoresTable.getValueAt(selectedRow, 3 );
		dataDocumentoTxt.setText( value );
		
		
		value = (String) tomadoresTable.getValueAt(selectedRow, 4 );
		int tipoDoc = Integer.parseInt( value ) - 1;
		tipoDocumentoComboBox.setSelectedIndex( tipoDoc ) ;		
		
		value = (String) tomadoresTable.getValueAt(selectedRow, 5 );
		serieTxt.setText( value );
		value = (String) tomadoresTable.getValueAt(selectedRow, 6 );
		subserieTxt.setText( value );
		value = (String) tomadoresTable.getValueAt(selectedRow, 7 );
		numeroDocumentoTxt.setText( value );
		value = (String) tomadoresTable.getValueAt(selectedRow, 8 );
		valorDocumentoTxt.setText( value );		
		
		revalidate();				
		
    }
    
    
	private void loadMapaTomadoresTable( ResultSet rs ) {
		
	   int cols = columnNames.length;
	   mapaTomadorRows = new HashMap();
	   mapaTomador = new Vector();
	   bufferRecordPos = 0;
	   String key = null;
	   
	   try {	   	
	   
	   	   /**
	        * Inserts all the data as a vector of Object[] ROWS
	        * It was not used Object[][] because is not knew how many rows
	        * the ResultSet has
	        */
	        while ( rs.next() ) {
	        	
	    	    String[] theRow = new String[ cols ];
	    	    
	    	    
	    	    String cmcTomador = rs.getString( 1 );
	    	    String nomeTomador = rs.getString( 2 );
	    	    String cnpjTomador = rs.getString( 3 );
	    	    String dataEmissao = 
	    	        SEPConverter.converteFrmtDataFromMySQL(rs.getString( 4 ));
	    	    String tipoDoc = rs.getString( 5 );
	    	    String serieNota = rs.getString( 6 );
	    	    String subSerieNota = rs.getString( 7 );
	    	    String numDoc = rs.getString( 8 );
	    	    
				String value = SEPConverter.adapterCurrencyFrmt( rs.getString( 9 ) );
				double dummy = Double.parseDouble( value ) ;
				String fmtValueDoc = getFmtCurrency( dummy );	    	    
	    	    String valorDoc = fmtValueDoc;
	    	    
				String controlSeqLanc = rs.getString( 10 );
	    	    
	    	    theRow[ CMC_TOMADOR ] = cmcTomador;
	    	    theRow[ NOME_TOMADOR ] = nomeTomador;
	    	    theRow[ CNPJ_TOMADOR ] = cnpjTomador;
	    	    theRow[ DATA_DOC ] = dataEmissao;
	    	    theRow[ TIPO_DOC ] = tipoDoc;
	    	    theRow[ SERIE ] = serieNota;
	    	    theRow[ SUBSERIE ] = subSerieNota;
	    	    theRow[ NUMERO_DOC ] = numDoc;
	    	    theRow[ VALOR_DOC ] = valorDoc;
	    	    theRow[ CONTROL_SEQ ] = controlSeqLanc;   
	    	    
    	    
	    	    key = controlSeqLanc;
	    	    
	    	    mapaTomadorRows.put( key, new Integer( bufferRecordPos ) );
	    	    mapaTomador.add( theRow );
	    	    bufferRecordPos++;
	    	
	        }
	   }    
	   catch ( SQLException sqlException ) {
	   	   sqlException.printStackTrace();
	   }

	}
	
	/**
	 * It will update the rows hash (buffer ) if it delets a record
	 */
	private void bufferRefresh() {
		String[] tomador = new String[ FIELDS_QTY ];
		String searchKey;
		mapaTomadorRows.clear();
		for ( int i = 0; i < mapaTomador.size() ; i++ ) {
			tomador = (String[]) mapaTomador.get( i );
			String controlSeq = tomador[ CONTROL_SEQ ];
			searchKey = controlSeq;
			mapaTomadorRows.put( searchKey, new Integer( i ) );
		}
	}
	
	
	private class TabMapaTomadorModel extends AbstractTableModel { 
		
		public int getRowCount() {
			return mapaTomadorRows.size();
		}
		
		public Object getValueAt( int r , int c ) {
			String[] theRow = null;
			if ( r < mapaTomador.size() ) {
				Object obj =  mapaTomador.get( r ) ;
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
	
	
	private class CmcKeySearcher extends KeyAdapter {
		
		
		String[] cmcTomadores = sepLookupTomadores.getCmcTomadorList();
		/*Arrays.sort(cmcTomadores, new Comparator() {
			public int compare(Object s1, Object s2){
				return ((String)s1)).compareTo((String)s2);
			}
		});		      
		}*/
		String searchKey ="";
		public void keyTyped(KeyEvent e) {
			char ch = e.getKeyChar();
			if ( !Character.isLetterOrDigit( ch ))
			    return;
			if (  cmcTomadorTxt.getText() != "" ) 
			        searchKey += cmcTomadorTxt.getText() + ch ;

			if ( cmcTomadorTxt.getText() != "" ) {
				for ( int k = 0; k < cmcTomadores.length; k++ ) {
					String str = cmcTomadores[ k ];
					if ( str.startsWith( searchKey )) {
						cmcTomadorTxt.setText("");
						cmcTomadorTxt.repaint();
						cmcTomadorTxt.setText( cmcTomadores[ k ] );
						cmcTomadorTxt.repaint();
						String cmc = cmcTomadorTxt.getText();
						if ( cmc.length() == 7 ) {
							String first = cmc.substring( 0, 3 );
							String second = cmc.substring( 3, 6 );
							String digit = cmc.substring( 6, 7 );
							cmcTomadorTxt.setText("");
							String dummy = first + "." + second + "-" + digit;
							cmcTomadorTxt.setText( dummy );
							searchKey = new String("");
						}
						searchKey = new String("");
						break;
					}				   
				}    
				searchKey = new String("");
			} 
		}
	}
	
	private class TomadoresKeySearcher extends KeyAdapter {
		
		String[] tomadores = sepLookupTomadores.getNomeTomadorList();
		//Arrays.sort(cmcTomadores);		      
		String searchKey ="";
		public void keyTyped(KeyEvent e) {
			char ch = e.getKeyChar();
			if ( !Character.isLetterOrDigit( ch ))
				return;
				
			if ( nomeTomadorTxt.getText() != "" ) {
//				System.out.println( "nomeTomadorTxt= " +
//				   nomeTomadorTxt.getText());
				searchKey += nomeTomadorTxt.getText() + ch ;
				for ( int k = 0; k < tomadores.length; k++ ) {
					String str = tomadores[ k ];
					if ( str.startsWith( searchKey )) {
						nomeTomadorTxt.setText("");
						nomeTomadorTxt.repaint();
						nomeTomadorTxt.setText( tomadores[ k ] );
						nomeTomadorTxt.repaint();
						searchKey = new String("");
						break;
					}				   
				}    
				searchKey = new String("");
			} 
		}
	}
	
	private class CnpjKeySearcher extends KeyAdapter {
		
		String[] cnpj = sepLookupTomadores.getCnpjTomadorList();
		//Arrays.sort(cmcTomadores);		      
		String searchKey ="";
		public void keyTyped(KeyEvent e) {
			char ch = e.getKeyChar();
			if ( !Character.isLetterOrDigit( ch ))
				return;
			if ( cnpjTxt.getText() != "" ) {
				searchKey += cnpjTxt.getText() + ch ;
				for ( int k = 0; k < cnpj.length; k++ ) {
					String str = cnpj[ k ];
					if ( str.startsWith( searchKey )) {
						cnpjTxt.setText("");
						cnpjTxt.repaint();
						cnpjTxt.setText( cnpj[ k ] );
						cnpjTxt.repaint();
						String cnpj= cnpjTxt.getText();						
						if ( cnpj.length() == 14 ) {
							String first = cnpj.substring( 0, 2 );
							String second = cnpj.substring( 2, 5 );
							String third = cnpj.substring( 5, 8 );
							String four = cnpj.substring( 8, 12 );
							String digit = cnpj.substring( 12, 14 );
							cnpjTxt.setText("");
							String dummy = first + "." + second + "." +
							               third + "/" + four + "-" + digit;
							cnpjTxt.setText( dummy );
							searchKey = new String("");
						}
						searchKey = new String("");
						break;
					}				   
				}    
				searchKey = new String("");
			} 
		}	
		
	}
	
	/*
	
class InputTextDocument extends PlainDocument {
	public void insertingString( int offset, String str, AttributeSet a) 
	  throws BadLocationException {
		char[] insertChars = str.toCharArray();
		  	
		boolean valid = true;
		boolean fit = true;
		  	
		if ( insertChars.length + getLength() <= 8 ){
			 System.out.println("dentro InputTextDobument");
			for ( int i = 0; i < insertChars.length; i++ ){
				System.out.println( "insertChars[" + i + "]" +
				               insertChars[ i ]);
				if (!Character.isDigit( insertChars[ i ])) {
					valid = false;
					break;
				}
			}
		}
		else {
			fit = false;		  		
		}
		if ( fit && valid ){
			super.insertString( offset, str, a );
		}
		else if (!fit) {
			getToolkit().beep();
		}
			
	}
		
} */
	
	
	
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
				dataDocumentoTxt.setText("");
				f = false;
			}
			if ( searchKey.length() == 8 ) {
				String day = searchKey.substring( 0, 2 );
				String month = searchKey.substring( 2, 4 );
				String year = searchKey.substring( 4, 8);				
				dataDocumentoTxt.setText("");
				String dummy = day + "/" + month + "/" + year;

				dataDocumentoTxt.setText( dummy );
				f = true;
				return;				   
			}
		}
	}
	
	
	/**
	 * Put a mask in corresponding textfield , after a key <ENTER> to be pressed
	 */
	private class MaskCMCHandler implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			String cmc = cmcTomadorTxt.getText();
			if ( cmc.length() == 7 ) {
				String first = cmc.substring( 0, 3 );
				String second = cmc.substring( 3, 6 );
				String digit = cmc.substring( 6, 7 );
				cmcTomadorTxt.setText("");
				String dummy = first + "." + second + "-" + digit;
				cmcTomadorTxt.setText( dummy );
			}
		}
	}
	
	/**
	 * Put a mask in corresponding textfield , after a key <ENTER> to be pressed
	 */
	private class MaskCNPJHandler implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			String cnpj= cnpjTxt.getText();						
			if ( cnpj.length() == 14 ) {
				String first = cnpj.substring( 0, 2 );
				String second = cnpj.substring( 2, 5 );
				String third = cnpj.substring( 5, 8 );
				String four = cnpj.substring( 8, 12 );
				String digit = cnpj.substring( 12, 14 );
				cnpjTxt.setText("");
				String dummy = first + "." + second + "." +
							   third + "/" + four + "-" + digit;
				cnpjTxt.setText( dummy );
			}
		}
	}
	
    private class MaskCurrencyHandler implements ActionListener {
    	  public void actionPerformed( ActionEvent e ) {
    	  	try {
    	  		fmtCurrencyValues();    	  	
    	  	}
    	  	catch( Exception ex ) {
    	  		JOptionPane.showMessageDialog( null, "Valor incorreto.\n" +
                      "Formato válido deve ser: 99999,99 ou 99.999,99." ,
                        "Entrada inválida", JOptionPane.ERROR_MESSAGE);                           
    	  	}
    	  }    	
    }
    
    
	
	private String insertMaskCMC( String cmc ) {
		String retVal = cmc;
		if ( cmc.length() == 7 ) {
			String first = cmc.substring( 0, 3 );
			String second = cmc.substring( 3, 6 );
			String digit = cmc.substring( 6, 7 );
			String dummy = first + "." + second + "-" + digit;
			retVal = dummy;
		}
		return retVal;
	}
	
	private String insertMaskCNPJ( String cnpj ) {
		String retVal = cnpj;
		if ( cnpj.length() == 14 ) {
			String first = cnpj.substring( 0, 2 );
			String second = cnpj.substring( 2, 5 );
			String third = cnpj.substring( 5, 8 );
			String four = cnpj.substring( 8, 12 );
			String digit = cnpj.substring( 12, 14 );
			String dummy = first + "." + second + "." +
						   third + "/" + four + "-" + digit;
			retVal = dummy;			   
		}
		return retVal;
	}
	
	
	
	/**
	 * Cut the mask off previously inserted in textfield
	 * This occurs because the database doesnt contain masked field
	 * @param <code>cmc</code> indicates the field to have the mask removed
	 * @return A <code>String</code> has not mask  
	 */
	private String removeMaskCMC( String cmc ) {
		String retVal = new String("") ;
		for ( int i = 0; i < cmc.length(); i++ ){
			if ( ( cmc.charAt( i ) == '.' ) || ( cmc.charAt( i ) ==  '-' ) )
			   continue;
			retVal += cmc.charAt( i );   
		}
		return retVal;
	}
	
	/**
	 * Cut the mask off previously inserted in textfield
	 * This occurs because the database doesnt contain masked field
	 * @param <code>cnpj</code> indicates the field to have the mask removed
	 * @returnps A <code>String</code> has not mask  
	 */
	private String removeMaskCNPJ( String cnpj ) {
		String retVal = new String("") ;
		for ( int i = 0; i < cnpj.length(); i++ ){
			if ( ( cnpj.charAt( i ) == '.' ) 
			     || ( cnpj.charAt( i ) ==  '-' ) || ( cnpj.charAt( i ) == '/') )
			   continue;
			retVal += cnpj.charAt( i );   
		}
		return retVal;
	}
	
	private void fmtCurrencyValues() {
		  // Computes the values from taxes due   
		  String vlrPago = valorDocumentoTxt.getText();
		  String value = SEPConverter.adapterCurrencyFrmt( vlrPago );
		  double vlrPagoNominal = Double.parseDouble( value ) ;
		  String fmtVlrPago = getFmtCurrency( vlrPagoNominal );
		  valorDocumentoTxt.setText("");
		  valorDocumentoTxt.setText( fmtVlrPago );
	}

	private String getFmtCurrency( double value ){	
		NumberFormat nf = NumberFormat.getInstance( Locale.GERMANY );
		nf.setMaximumFractionDigits( 2 );
		nf.setMinimumFractionDigits( 2 );
		String formattedNumber = nf.format( value );		
		return formattedNumber;
	} 	
		
	
	
}

class FixedNumericDocument extends PlainDocument {

   private int maxLength = 9999;
   private boolean numericOnly;

   public FixedNumericDocument(int maxLength, boolean numericOnly) {
	  super();
	  this.maxLength = maxLength;
	  this.numericOnly = numericOnly;
   }

   //this is where we'll control all input to our document.  
   //If the text that is being entered passes our criteria, then we'll just call
   //super.insertString(...)
   public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
	  if (getLength() + str.length() > maxLength) {
		 Toolkit.getDefaultToolkit().beep();
		 return;
	  }
	  else {
		 try {
			if (numericOnly) {
			   //check if str is numeric only
			   Integer.parseInt(str);
			   //if we get here then str contains only numbers
			   //so it's ok to insert
			}
			super.insertString(offset, str, attr);
		 }
		 catch(NumberFormatException exp) {
			Toolkit.getDefaultToolkit().beep();
			return;
		 }
	  }
	  return;
   }
}



