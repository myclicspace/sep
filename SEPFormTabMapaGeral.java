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
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.text.*;

public class SEPFormTabMapaGeral extends JPanel {
	
	/**
	 * holds the values of rates used for compute of taxes
	 */
	 private HashMap rates = null;
	
	/**
	 * qtd de campos existente em uma linha da tabela mapaGeral
	 */
	private static final int FIELDS_QTY = 13;
	
	/**
	 * Especifica os tipos de lançamentos possíveis obtidos pela fiscalização
	 */
	private static final int LANCAMENTO_ESCRIT = 1;
	private static final int LANCAMENTO_AJUST  = 2;
	
	
	/**
	 * Mapeia posição do registro selecionada da tabela no buffer
	 */
	private static int bufferRecordPos = 0;
	
	private static final int TOMADOR = 0;
	private static final int DATA_EMISSAO = 3;
	private static final int NUM_DOC = 6;
	private static final int CONTROL_SEQ = 12;
	
	
	private static String currOS = null; // ordem de serviço aberta
	private static String currCMC = null ; // insc. munic. aberta
	
	
	
	/**
	 * Representa um buffer para os lançamentos de uma determinada
	 * ordem de serviço. Também controla operações básicas de inserção
	 * exclusão e atualização de registros
	 */
	private Map mapaGeralRows = null;
	
	/**
	 * Representa a fonte de dados para uma tabela
	 */
	 private Vector mapaGeral = null;
	

	//private Object[][] documentos;
	
	
			
	private String[] columnNames = { "Tomador", "Item da lista", "Natureza do servico", 
									"Data emissao" ,"Data de competência" ,
									"Tipo documento", "N° documento", 
									 "Rec.própria", "ISS devido", 
									 "Rec.subst.tribt.", "ISS subst.tribt. ",
		                              "Tipo lançamento", "Data de registro"									 
	 								 };
	
	private String tipos[] = { "NFS", "Recibo", "Outro" };
	
	private Connection myConnection = null;
	private Statement myStatement = null;
	
	
	// Exibe registros para uma os e empresa específica
	TabMapaGeralModel documentosTableModel = null ;		
	private static JTable documentosTable = null; 
	
	
	
	// identificação do contribuinte e serviço						
	private static JTextField nomeTomadorTxt = null;
	private static JTextField itemListaTxt = null;
	private static JTextField naturezaServicoTxt = null;
	private static JComboBox tipoDocumentoComboBox = null;
		
	// identificação do documento
	private static JTextField dataDocumentoTxt = null;
	private static JTextField dataCompetenciaTxt = null;
	private static JTextField numeroDocumentoTxt = null;
		
		
	// valor das operações
	private static JTextField receitaEscritTxt = null;
	private static JTextField receitaSubstTribTxt = null;
		
	// apuração do imposto
	private static JTextField issEscritTxt = null;
	private static JTextField issSubstTribTxt = null;
	
	// tipo de lançamento	
	private static JRadioButton escrituradoRadioButton = null;
	private static JRadioButton ajustadoRadioButton = null;
	
	private SEPLookupMapaGeral sepLookupMapaGeral = null;
	private SEPViewerMapaGeral sepViewerMapaGeral = null;
			
	
	
    /**
     * Constructor setup inital configuration parameters and 
     * loading the corresponding records to the respective service order
     * 
     * @param con a <code>String</code> indicates the established connection
     *
     */
   
	public SEPFormTabMapaGeral( Connection con ) {
		
		try {		
   	    	myConnection = con;
			myStatement = con.createStatement();
   	    
   	    	currOS = EmpresaAtual.getNumeroOS();
   	    	currCMC = EmpresaAtual.getCMCEmpresa();
   	    
   	    	Statement stmt = con.createStatement();
   	    	
   	    	String query = "SELECT tomadorMapaGeral, itemListaMapaGeral, " +
   	    	           " descServicoMapaGeral, " + 
   	                   " dataEmissaoMapaGeral, dataCompetenciaMapaGeral, " +
   	                   " numeroDocMapaGeral, tipoDocumentoMapaGeral," +
   	                   " recEscritMapaGeral, issDevidoMapaGeral, " +
   	                   " recSubstTribMapaGeral, issSubstMapaGeral, " +
			           " tipoRecMapaGeral, seqMapaGeral " +   	                   
   	                   " FROM tabMapaGeral " + 
		               " WHERE osMapaGeral='" + currOS + "'" +
		               " AND cmcMapaGeral='" + currCMC + "'" +
					   " ORDER BY tomadorMapaGeral,  dataEmissaoMapaGeral, " +
					   " numeroDocMapaGeral"  ;
   	    	
   	             
   	    	ResultSet rs = stmt.executeQuery( query );   	    
			loadMapaGeralTable( rs );
			
			loadRates( con );
			
			sepLookupMapaGeral = new SEPLookupMapaGeral( myConnection );
							
		}
		catch( SQLException ignore ) {
			ignore.printStackTrace();
		}	
	}
	
	/**
	 * Sets the textfield named nomeTomadorTxt
	 * @param nomeTomador indicates the value for setting
	 */
	public static void setNomeTomadorTxt( String nomeTomador ) {
		nomeTomadorTxt.setText( nomeTomador );
	}
	
	/**
	 * Sets the textfield named itemListTxt
	 * @param itemLista indicates the value for setting
	 */
	public static void setItemListaTxt( String itemLista ) {
		itemListaTxt.setText( itemLista );
	}
	
	/**
	 * Sets the textfield named naturezaServicoTxt
	 * @param naturezaServico indicates the value for setting
	 */
	public static void setNaturezaServicoTxt( String naturezaServico ) {
		naturezaServicoTxt.setText( naturezaServico );
	}
	
	/**
	 * Sets the textfield named tipoDocumentoComboBox
	 * @param tipoDocumento indicates the value for setting
	 */
	public static void setTipoDocumentoCombo( String tipoDocumento ) {
		tipoDocumentoComboBox.setSelectedItem( tipoDocumento );
	}
	
	/**
	 * Sets the combox named tipoDocumentoCombo
	 * @param index indicates the value for setting
	 */
	public static void setTipoDocCombo( int index ) {
		tipoDocumentoComboBox.setSelectedIndex( index - 1  );
	}
	
	
	/**
	 * Sets the textfield named numeroDocumentoTxt
	 * @param numeroDocumento indicates the value for setting
	 */
	public static void setNumeroDocumentoTxt( String numeroDocumento ) {
		numeroDocumentoTxt.setText( numeroDocumento );
	}
	
	/**
	 * Sets the textfield named dataDocumentoTxt
	 * @param dataEmissao indicates the value for setting
	 */
	public static void setDataEmissaoTxt( String dataEmissao ) {
		dataDocumentoTxt.setText( dataEmissao );
	}
	
	/**
	 * Sets the textfield named dataCompetenciaTxt
	 * @param dataCompetencia indicates the value for setting
	 */
	public static void setDataCompetenciaTxt( String dataCompetencia ) {
		dataCompetenciaTxt.setText( dataCompetencia );
	}
	
	/**
	 * Sets the textfield named receitaEscritTxt
	 * @param recPropria indicates the value for setting
	 */
	public static void setReceitaPropria( String recPropria ) {
		receitaEscritTxt.setText( recPropria );
	}
	
	/**
	 * Sets the textfield named issEscritTxt
	 * @param issRecPropria indicates the value for setting
	 */
	public static void setIssRecPropria( String issRecPropria ) {
		issEscritTxt.setText( issRecPropria );
	}
	
	/**
	 * Sets the textfield named naturezaServicoTxt
	 * @param naturezaServico indicates the value for setting
	 */
	public static void setReceitaTerceiros( String recTerceiros ) {
		receitaSubstTribTxt.setText( recTerceiros );
	}
	
	/**
	 * Sets the textfield named issSubstTribTxt
	 * @param issRecTerceiros indicates the value for setting
	 */
	public static void setIssRecTerceiros( String issRecTerceiros ) {
		issSubstTribTxt.setText( issRecTerceiros );
	}
	
	/**
	 * Sets the radio button named escrituradoRadioButton
	 */
	public static void setLancamentoEscriturado() {
		escrituradoRadioButton.setSelected( true );		
	}
	
	/**
	 * Sets the radio button named ajustadoRadioButton
	 */
	public static void setLancamentoAjustado() {
		ajustadoRadioButton.setSelected( true );		
	} 
	
	/**
	 * Sets the textfield named numeroDocumentoTxt
	 * @param numeroDocumento indicates the value for setting
	 */
/*	public static void setDataDocumentoTxt( String numeroDocumento ) {
		dataDocumentoTxt.setText( numeroDocumento );
	} */
	

	public static void setSelectedRow( String timestampSelected ) {
		int numRows = documentosTable.getRowCount();
		for ( int row = 0; row < numRows ; row++ ) {
			String timestamp = (String) documentosTable.getValueAt( row, CONTROL_SEQ );
			if ( timestampSelected.equals( timestamp ))
				documentosTable.setRowSelectionInterval( row, row );
			}       
	}


	public JPanel createTabMapaGeralPanel(){
		
		JPanel tabDocumentosPanel = new JPanel( new BorderLayout() );
		
		JPanel inputTabMapaGeralPanel = new JPanel(); 
		
		inputTabMapaGeralPanel.setLayout( 
		    new  BoxLayout( inputTabMapaGeralPanel, BoxLayout.Y_AXIS ) );  							
		
		
		// identificação do contribuinte e serviço						
		JLabel nomeTomadorLabel = new JLabel("Tomador");
//		nomeTomadorTxt = new JTextField( 45 ); 
		
		nomeTomadorTxt = new JTextField( 
							new FixedNumericDocument( 45, false ), "", 35 );
		nomeTomadorTxt.addKeyListener( new TomadorKeySearcher());							 
		
		
		JLabel itemListaLabel = new JLabel("Item da lista");
		itemListaTxt = new JTextField( 
							new FixedNumericDocument( 3, true ), "", 3 ); 
		itemListaTxt.addKeyListener( new ItemListaKeySearcher() );					
				
		
		JLabel naturezaServicoLabel = new JLabel("Natureza do serviço");
		naturezaServicoTxt = new JTextField( 
							new FixedNumericDocument( 30, false ), "", 25 );
		naturezaServicoTxt.addKeyListener( new NaturezaKeySearcher() );					 
			
		
		
		// identificação do documento
		JLabel dataDocumentoLabel = new JLabel("Data da emissão");
		dataDocumentoTxt = new JTextField( 
						 new FixedNumericDocument( 10, false ), "", 6 );
		dataDocumentoTxt.setToolTipText("Formato válido: DD/MM/AAAA");                 
		dataDocumentoTxt.addKeyListener( new FmtPaymentDataListener() );		
		
		
		JLabel dataCompetenciaLabel = new JLabel("Data de competência");
		dataCompetenciaTxt = new JTextField( 
						 new FixedNumericDocument( 7, false ), "", 5 );
		dataCompetenciaTxt.setToolTipText("Formato válido: MM/AAAA");
		dataCompetenciaTxt.addKeyListener( new FmtDataCompetenciaListener() );
		
		
/*		dataDocumentoTxt.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				dataCompetenciaTxt.setText( dataDocumentoTxt.getText() );
			}
		}); */		
		
		
		JLabel numeroDocumentoLabel = new JLabel("Nro. doc.");
		numeroDocumentoTxt = new JTextField( 6 );
		
		JLabel tipoDocumentoLabel = new JLabel("Tipo doc.");
		tipoDocumentoComboBox = new JComboBox( tipos );
		
		
		// valor das operações
		JLabel receitaEscritLabel = new JLabel("Receita própria");
		receitaEscritTxt = new JTextField(
			new FixedNumericDocument( 14, false ), "0,00", 14 );
		receitaEscritTxt.setToolTipText("Formato válido: 99.999,99 ou 99999,99");
			
		
		JLabel receitaSubstTribLabel = new JLabel("Rec.subt.trib." );
		receitaSubstTribTxt = new JTextField(
			new FixedNumericDocument( 14, false ), "0,00", 14 );
		receitaSubstTribTxt.setToolTipText("Formato válido: 99.999,99 ou 99999,99");
		
		
		// apuração do imposto
		JLabel issEscritLabel = new JLabel("ISS devido");
		issEscritTxt = new JTextField( 10 );
		issEscritTxt.setText("0,00");
		issEscritTxt.setEditable( false );
		JLabel issSubstTribLabel = new JLabel("ISS subt.trib." );
		issSubstTribTxt = new JTextField( 10 );		
		issSubstTribTxt.setText("0,00");
		issSubstTribTxt.setEditable( false );
		
		// bounds taxes compute listener to text field
		ComputeTaxes computeTaxes = new ComputeTaxes();
		receitaEscritTxt.addActionListener( computeTaxes );	
		receitaSubstTribTxt.addActionListener( computeTaxes );	
		
	
		JPanel tomadorTipoDocPanel  = new JPanel( new FlowLayout( FlowLayout.LEFT )); 
		JPanel servicoPanel = //new JPanel( new GridLayout( 1, 4, 5 , 5 ) );		
		                     new JPanel( new FlowLayout( FlowLayout.LEFT )); 
		JPanel dataPanel =  new JPanel ( new GridLayout( 1, 4, 1, 1 ) ) ;		
		                   // new JPanel( new FlowLayout( FlowLayout.LEFT )); 
		JPanel documentoPanel =  new JPanel( new GridLayout( 1, 8, 5, 5 ) );
//		                    new JPanel( new FlowLayout( FlowLayout.LEFT )); 
		
		JPanel issNormalPanel = new JPanel( new GridLayout( 1, 8, 5, 5 ) );
		
		
		tomadorTipoDocPanel.add( nomeTomadorLabel );
		tomadorTipoDocPanel.add( nomeTomadorTxt );		
		
		
		servicoPanel.add( itemListaLabel );
		servicoPanel.add( itemListaTxt );
		servicoPanel.add( naturezaServicoLabel );
		servicoPanel.add( naturezaServicoTxt );	
		
		
		documentoPanel.add( tipoDocumentoLabel );
		documentoPanel.add( tipoDocumentoComboBox );
		documentoPanel.add( numeroDocumentoLabel );
		documentoPanel.add( numeroDocumentoTxt );
		documentoPanel.add( dataDocumentoLabel );
		documentoPanel.add( dataDocumentoTxt );
		documentoPanel.add( dataCompetenciaLabel );
		documentoPanel.add( dataCompetenciaTxt );	
		
	
		issNormalPanel.add( receitaEscritLabel );		
		issNormalPanel.add( receitaEscritTxt );
		issNormalPanel.add( issEscritLabel );
		issNormalPanel.add( issEscritTxt );
		issNormalPanel.add( receitaSubstTribLabel );
		issNormalPanel.add( receitaSubstTribTxt );		
		issNormalPanel.add( issSubstTribLabel );
		issNormalPanel.add( issSubstTribTxt );		
		
		
        Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Documentos");
        titleBorder.setTitleJustification( TitledBorder.RIGHT );

		inputTabMapaGeralPanel.setBorder( titleBorder );
		
		
		escrituradoRadioButton = new JRadioButton( "Receita Escriturada" );
		ajustadoRadioButton = new JRadioButton( "Receita Ajustada" );		
		escrituradoRadioButton.setSelected( true );
		ButtonGroup tipoReceitaButtonGroup = new ButtonGroup();
		tipoReceitaButtonGroup.add( escrituradoRadioButton );
		tipoReceitaButtonGroup.add( ajustadoRadioButton );
		
		JPanel tipoReceitaPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
		JLabel tipoLancamentoLabel = new JLabel("Lançamento ");

		tipoReceitaPanel.add( tipoLancamentoLabel );
		tipoReceitaPanel.add( escrituradoRadioButton );
		tipoReceitaPanel.add( ajustadoRadioButton );
		
		
		inputTabMapaGeralPanel.add( tomadorTipoDocPanel );
		inputTabMapaGeralPanel.add( servicoPanel );
		inputTabMapaGeralPanel.add( documentoPanel );
		inputTabMapaGeralPanel.add( issNormalPanel );
		inputTabMapaGeralPanel.add( tipoReceitaPanel );
		
		
		JButton incluirButton = new JButton("Incluir");
		incluirButton.setToolTipText("Cadastra serviço prestado");
		incluirButton.setMnemonic('I');
		
		
		incluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
					fmtCurrencyValues();
				    addRecord( myConnection );
					// reloads database containing newer updates
					sepLookupMapaGeral.updateSearching();
				    					
				} catch( SQLException ignore ) {
					ignore.printStackTrace();					
				}    
			}
		});
		
				
		
		JButton excluirButton = new JButton("Excluir");
		excluirButton.setMnemonic('E');
		excluirButton.setToolTipText("Excluir registro do serviço prestado");
		excluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
				    deleteRecord( myConnection );
					sepLookupMapaGeral.updateSearching();
				} catch( SQLException ignore ) {
					ignore.printStackTrace();					
				}    
			}
		});
		
		
		JButton gravarButton = new JButton("Gravar");
		gravarButton.setMnemonic('G');
		gravarButton.setToolTipText("Atualiza serviço prestado");		
		gravarButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
					fmtCurrencyValues();
					updateRecord( myConnection );
					sepLookupMapaGeral.updateSearching();
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
   	    	
					String query = "SELECT tomadorMapaGeral, itemListaMapaGeral, " +
						   " descServicoMapaGeral, " + 
						   " dataEmissaoMapaGeral, dataCompetenciaMapaGeral, " +
						   " numeroDocMapaGeral, tipoDocumentoMapaGeral," +
						   " recEscritMapaGeral, issDevidoMapaGeral, " +
						   " recSubstTribMapaGeral, issSubstMapaGeral, " +
						   " tipoRecMapaGeral, seqMapaGeral " +   	                   
						   " FROM tabMapaGeral " + 
						   " WHERE osMapaGeral='" + currOS + "'" +
						   " AND cmcMapaGeral='" + currCMC + "'" +
						   " ORDER BY dataCompetenciaMapaGeral";
//-->						   " ORDER BY tomadorMapaGeral,  dataEmissaoMapaGeral, " +
//-->						   " numeroDocMapaGeral"  ;
   	    	
					ResultSet rs = stmt.executeQuery( query );   	    
					printReportMapaGeral( rs );
				}
				catch ( SQLException sqlEx ) { 
					  sqlEx.printStackTrace() ;	
				}
			}
		}); 		
				
		
		JButton buscarLocalButton = new JButton("Pesquisa serviços");
		buscarLocalButton.setToolTipText("Lista serviços cadastrados apenas nesta OS");
		buscarLocalButton.setMnemonic('P');
		buscarLocalButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				sepViewerMapaGeral =
				   new SEPViewerMapaGeral( myConnection, true );
				sepViewerMapaGeral.setVisible( true );  
			}
		}); 		
		 		
		
		 
		
		JPanel controlMapaGeralPanel = new JPanel( new FlowLayout() );
		
		controlMapaGeralPanel.add( incluirButton );
		controlMapaGeralPanel.add( excluirButton );
		controlMapaGeralPanel.add( gravarButton );
		//controlMapaGeralPanel.add( imprimirButton );
		controlMapaGeralPanel.add( buscarLocalButton );
		
				
		documentosTableModel = new TabMapaGeralModel();		
		documentosTable = new JTable(); 
		JTableHeader headers = documentosTable.getTableHeader();
		headers.setReorderingAllowed( false );
		
		documentosTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		documentosTable.addMouseListener( new MouseAdapter()  {
			public void mouseClicked( MouseEvent e ) {
				int count = documentosTable.getSelectedRowCount();
				if ( count > 0 ) {
                    inputTabMapaGeralPanelRefresh();					
				}
			}
		});
		
		documentosTable.setModel( documentosTableModel );
		JScrollPane scrollpane = new JScrollPane( documentosTable );
		
		JPanel mapaGeralDisplayPanel = new JPanel( new BorderLayout() ) ;
		
		mapaGeralDisplayPanel.add( scrollpane );	
		
		
		tabDocumentosPanel.add( inputTabMapaGeralPanel, BorderLayout.NORTH );
		tabDocumentosPanel.add( mapaGeralDisplayPanel, BorderLayout.CENTER );
		tabDocumentosPanel.add( controlMapaGeralPanel, BorderLayout.SOUTH );		
		
		return tabDocumentosPanel;	
		  
	}
	
	
	private void printReportMapaGeral( ResultSet rs ) {
		String[] columnsNames = { "NRO. N.F", "MÊS/ANO", "TOMADOR DO SERVIÇO",
		                         "NATUREZA DO SERVICO", "ITEM ART.116,LEI 1.761/83",
		                         "RECEITA TRIBUTÁVEL", "ALIQ(%)",  "ISSQN" };  
		
		Vector rows = new Vector();
		
		try {	   	
	   
			 while ( rs.next() ) {
	        	
				 String[] theRow = new String[ columnsNames.length ];
	    	    
				 String tomador = rs.getString( 1 );
				 String itemLista = rs.getString( 2 );	    	    
				 String descServico = rs.getString( 3 );
				 String dataCompetencia =  getDataDirectOrder( rs.getString( 5 ) ) ;
				 String numDoc = rs.getString( 6 );
	    	    
				 String vlrRecPropria = 
					 SEPConverter.adapterCurrencyFrmt( rs.getString( 8 ) );
				 double vlrRecPropriaContrib = Double.parseDouble( vlrRecPropria ) ;
	    	    
				 String vlrRecSubst = 
					 SEPConverter.adapterCurrencyFrmt( rs.getString( 10 ) );
				 double vlrRecSubstContrib = Double.parseDouble( vlrRecSubst ) ;
				 
				 double vlrRecBruta = vlrRecPropriaContrib + vlrRecSubstContrib;
				 String fmtVlrRecBruta = getFmtCurrency( vlrRecBruta );
				 String recTributavel = fmtVlrRecBruta;	    	    

				String vlrIssDevido = 
					SEPConverter.adapterCurrencyFrmt( rs.getString( 9 ) );
				double vlrIssProprio = Double.parseDouble( vlrIssDevido ) ;

				 String vlrIssSubstTrib = 
					 SEPConverter.adapterCurrencyFrmt( rs.getString( 11 ) );
				 double vlrIssTerceiros = Double.parseDouble( vlrIssSubstTrib ) ;
				 
				double vlrISSQN	 = vlrIssProprio + vlrIssTerceiros;
				String fmtVlrISSQN = getFmtCurrency( vlrISSQN );
				String ISSQN = fmtVlrISSQN;

				String tmp = (String) rates.get( itemLista );
				double rate  = Float.parseFloat( 
					   SEPConverter.adapterCurrencyFrmt( tmp ) );
				String aliquota = getFmtCurrency( rate );	   
				 
				theRow[ 0 ] = numDoc;
				theRow[ 1 ] = dataCompetencia;
				theRow[ 2 ] = tomador;
				theRow[ 3 ] = descServico;
				theRow[ 4 ] = itemLista;
				theRow[ 5 ] = recTributavel;
				theRow[ 6 ] = aliquota + "%";
				theRow[ 7 ] = ISSQN;
				
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
//			    System.out.println( "values[" + i + "]= " + values[ i ][ j ]);
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
	 *         necessary for creating the statement  
	 * @throws SQLException if ocurrs some erro with Connection
	 */	
	private void addRecord(Connection con ) throws SQLException {	
	    
		String tomador = nomeTomadorTxt.getText();		
		String itemLista = itemListaTxt.getText();
		String naturezaServico = naturezaServicoTxt.getText();		
		
		String dataEmissao = dataDocumentoTxt.getText();
		String dataCompetencia = dataCompetenciaTxt.getText();
				
		String numeroDocumento = numeroDocumentoTxt.getText();		
		int itemChoicenCombo =	tipoDocumentoComboBox.getSelectedIndex() + 1 ;
		String tipoDocumento = Integer.toString( itemChoicenCombo );			
		
		
		String receitaEscrit =	receitaEscritTxt.getText();		
		String issDevido = issEscritTxt.getText();		
		String receitaSubstTrib = receitaSubstTribTxt.getText();		
		String issSubstTrib = issSubstTribTxt.getText();
		
		String tipoLancamento = null;
		if ( escrituradoRadioButton.isSelected() ) {
			tipoLancamento = Integer.toString( LANCAMENTO_ESCRIT );
		}
		else {
			tipoLancamento = Integer.toString( LANCAMENTO_AJUST );
		}		
		
		
		java.util.Date now = new java.util.Date();
		long timeStamp = now.getTime();
		String strTimeStamp = Long.toString( timeStamp );
		DateFormat longTimestamp
		   = DateFormat.getDateTimeInstance( DateFormat.FULL, DateFormat.FULL );			          
		 java.util.Date dummy =  new java.util.Date( timeStamp );
		 String controlSeqLanc = longTimestamp.format( dummy );	
		
		String key = controlSeqLanc ;
	    
	    
		boolean bufferHas = mapaGeralRows.containsKey( key );
		if ( bufferHas ) {
			String err = "Tentativa de inserir registro duplicado";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			// -- Includes data in table model, and so does refreshes gui 
			String lancamento[] = new String[ FIELDS_QTY ];	
			lancamento[  0  ] = tomador;
			lancamento[  1  ] = itemLista;			
			lancamento[  2  ] = naturezaServico;			
			lancamento[  3  ] = dataEmissao;
			lancamento[  4  ] = dataCompetencia;
			lancamento[  5  ] = tipoDocumento; 
			lancamento[  6  ] = numeroDocumento;
			lancamento[  7  ] = receitaEscrit;
			lancamento[  8  ] = issDevido;
			lancamento[  9  ] = receitaSubstTrib;
			lancamento[ 10  ] = issSubstTrib;
			lancamento[ 11  ] = tipoLancamento;
			lancamento[ 12  ] = controlSeqLanc;				
			
			
			// The chunck of data is right,then proceed to addding them to database
			String cmd = "INSERT INTO tabMapaGeral " +
			" VALUES  ('"  + currOS + "','" + currCMC + "','" 
			     + tomador + "','" + itemLista + "','" + naturezaServico + "','"
			     + tipoDocumento + "','" + numeroDocumento + "','" 
			     + SEPConverter.convertFrmtCurrencyToMySQL(issDevido) + "','"
			     + SEPConverter.convertFrmtCurrencyToMySQL(issSubstTrib) + "','"
			     + SEPConverter.convertFrmtCurrencyToMySQL(receitaEscrit) + "','" 
			     + SEPConverter.convertFrmtCurrencyToMySQL(receitaSubstTrib) + "','" 
			     + SEPConverter.converteFrmtDataToMySQL(dataEmissao)  + "','"
 			     + getDataReverseOrder(dataCompetencia) + "','"
				 + tipoLancamento + "','"
			     + controlSeqLanc +  " ')" ;
 			     
			myStatement.executeUpdate( cmd );
			con.commit();
			
			mapaGeral.add( lancamento );
			bufferRefresh();                // updating buffer
			documentosTable.setModel( documentosTableModel );
			documentosTable.revalidate();
			documentosTable.repaint();
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
		
		
		int selectedRow = documentosTable.getSelectedRow();
		
		// -- primary key into to choicen record 
		String controlSeq 
		   = (String) documentosTable.getValueAt(selectedRow, CONTROL_SEQ );   
		
	    String searchKey = controlSeq;
	    
		Integer rowID = (Integer) mapaGeralRows.get( searchKey );
	
		// -- position in the buffer of choicen record
		int index = rowID.intValue();		
		
		mapaGeral.remove( index );
		bufferRefresh();                // updating buffer
		documentosTable.setModel( documentosTableModel );
		documentosTable.revalidate();
		documentosTable.repaint();
		revalidate();
		repaint();

        Statement stm = con.createStatement();     
		String cmd = "DELETE FROM tabMapaGeral" + 
		             " WHERE osMapaGeral = '" + currOS + "'" +
		             " AND cmcMapaGeral ='" + currCMC + "'" +
					 " AND seqMapaGeral ='" + searchKey + "'" ; 
  		             
         stm = con.createStatement();     
 		 stm.executeUpdate( cmd );

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
		
		int selectedRow = documentosTable.getSelectedRow();		
		
	   // -- primary key into to choicen record 
		String controlSeq 
		   = (String) documentosTable.getValueAt( selectedRow, CONTROL_SEQ );
		
	    String searchKey = controlSeq;	    
		
		boolean bufferHas = mapaGeralRows.containsKey( searchKey );
		if ( !bufferHas ) {
			String err = "Tentativa de atualizar registro inexistente";
			JOptionPane.showMessageDialog( null, err,
			      "Atualizacao inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			
			String tomador = nomeTomadorTxt.getText(); 
			String numDoc = numeroDocumentoTxt.getText();
			String dataEmissao = dataDocumentoTxt.getText();				
			
			String itemLista = itemListaTxt.getText();
			String naturezaServico = naturezaServicoTxt.getText();		
		
			String dataCompetencia = dataCompetenciaTxt.getText();
				
			int itemChoicenCombo =	tipoDocumentoComboBox.getSelectedIndex() + 1 ;
			String tipoDocumento = Integer.toString( itemChoicenCombo );		
		
		
			String receitaEscrit =	receitaEscritTxt.getText();		
			String issDevido = issEscritTxt.getText();		
			String receitaSubstTrib = receitaSubstTribTxt.getText();		
			String issSubstTrib = issSubstTribTxt.getText();
			
			String tipoLancamento = null;
			if ( escrituradoRadioButton.isSelected() ) {
				tipoLancamento = Integer.toString( LANCAMENTO_ESCRIT );
			}
			else {
				tipoLancamento = Integer.toString( LANCAMENTO_AJUST );
			} 		
					
			
			String lancamento[] = new String[ FIELDS_QTY ];					
			lancamento[  0  ] = tomador;
			lancamento[  1  ] = itemLista;			
			lancamento[  2  ] = naturezaServico;			
			lancamento[  3  ] = dataEmissao;
			lancamento[  4  ] = dataCompetencia;
			lancamento[  5  ] = tipoDocumento;
			lancamento[  6  ] = numDoc;
			lancamento[  7  ] = receitaEscrit;
			lancamento[  8  ] = issDevido;
			lancamento[  9  ] = receitaSubstTrib;
			lancamento[ 10  ] = issSubstTrib;
			lancamento[ 11  ] = tipoLancamento;
			
			java.util.Date now = new java.util.Date();
			long timeStamp = now.getTime();
			String strTimeStamp = Long.toString( timeStamp );
			DateFormat longTimestamp
			   = DateFormat.getDateTimeInstance( DateFormat.FULL, DateFormat.FULL );			          
			 java.util.Date dummy =  new java.util.Date( timeStamp );
			 String controlSeqLanc = longTimestamp.format( dummy );
			   
			lancamento[ 12 ] = controlSeqLanc;		
			
			
			// -- The calling for updating records is done
			// Here, is upadted just the field valor because
			// the other fields behaviour as unique keys
			
			
			String cmd = "UPDATE tabMapaGeral "
			          + "SET tipoDocumentoMapaGeral ='" + tipoDocumento + "', "
					  + "tomadorMapaGeral ='" +  tomador + "', "
					  + "numeroDocMapaGeral ='" +  numDoc + "', "
					  + "dataEmissaoMapaGeral ='"
				      +  SEPConverter.converteFrmtDataToMySQL(dataEmissao) + "', "
			          
			          + "itemListaMapaGeral ='" +  itemLista + "', "
			          + "descServicoMapaGeral ='" +  naturezaServico + "', "
			          + "tipoRecMapaGeral = '" +  tipoLancamento + "', "
 			          + "dataCompetenciaMapaGeral = '" 
  			          + getDataReverseOrder( dataCompetencia ) + "', "
			          + "issDevidoMapaGeral ='" +
			              SEPConverter.convertFrmtCurrencyToMySQL(issDevido) + "', "
			          + "issSubstMapaGeral ='" + 
			            SEPConverter.convertFrmtCurrencyToMySQL( issSubstTrib ) + "', "
			          + "recEscritMapaGeral ='" +  
			            SEPConverter.convertFrmtCurrencyToMySQL( receitaEscrit ) + "', "
			          + "recSubstTribMapaGeral = '" +  
			            SEPConverter.convertFrmtCurrencyToMySQL( receitaSubstTrib ) + "', "
					  + "seqMapaGeral = '" + controlSeqLanc + "'"
				      +  " WHERE osMapaGeral ='" + currOS + "'" 
					  +  " AND cmcMapaGeral='" + currCMC + "'" 
					  +  " AND seqMapaGeral ='" + searchKey + "'" ; 
			
	 		myStatement.executeUpdate( cmd );		
	 		
			con.commit();	
			
			Integer rowID = (Integer) mapaGeralRows.get(  searchKey  );
			// -- position in the buffer of choicen record
			int index = rowID.intValue();
			// -- remove first and insert again. Doing this, is not 
			// -- necessary to worry with refreshing cacher buffer
			mapaGeral.remove( index );
			mapaGeral.add( index, lancamento );	
				
			bufferRefresh();      // updating buffer
			documentosTable.setModel( documentosTableModel );
			documentosTable.revalidate();
			documentosTable.repaint();	
			revalidate();
			repaint();
					
			
			System.out.println( " gravou " );		
			
		}		
		 
	}	
	
	
	
	/**
	 *  It will update textfield when a record is selected inthe TabEmpresasPanel 
	 */
    private void inputTabMapaGeralPanelRefresh() {   	
    	String value;
    	
		int selectedRow = documentosTable.getSelectedRow();
		
		value = (String) documentosTable.getValueAt(selectedRow, 0 );
		nomeTomadorTxt.setText( value );
		
		value = (String) documentosTable.getValueAt(selectedRow, 1 );
		itemListaTxt.setText( value );		
				
		value = (String) documentosTable.getValueAt(selectedRow, 2 );
		naturezaServicoTxt.setText( value );		
		
		value = (String) documentosTable.getValueAt(selectedRow, 3 );
		dataDocumentoTxt.setText( value );		
		
		value = (String) documentosTable.getValueAt(selectedRow, 4 );
		dataCompetenciaTxt.setText( value );		
		
		value = (String) documentosTable.getValueAt(selectedRow, 5 ) ;
		tipoDocumentoComboBox.setSelectedIndex( Integer.parseInt( value ) - 1 );		
		
		value = (String) documentosTable.getValueAt(selectedRow, 6 );
		numeroDocumentoTxt.setText( value );				
		
		value = (String) documentosTable.getValueAt(selectedRow, 7 );
		receitaEscritTxt.setText( value );				
		
		value = (String) documentosTable.getValueAt(selectedRow, 8 );
		issEscritTxt.setText( value );		
		
		value = (String) documentosTable.getValueAt(selectedRow, 9 );
		receitaSubstTribTxt.setText( value );			
			
		value = (String) documentosTable.getValueAt(selectedRow, 10 );
		issSubstTribTxt.setText( value );
		
		value = (String) documentosTable.getValueAt( selectedRow, 11 );		
		int choice = Integer.parseInt( value ) ;
		
		if ( choice == LANCAMENTO_ESCRIT  ) {
			escrituradoRadioButton.setSelected( true );			
		}
		else {
			ajustadoRadioButton.setSelected( true );			
		}
				
		
		revalidate();		
    }						
	
	
	private void loadMapaGeralTable( ResultSet rs ) {
		
	   int cols = columnNames.length;
	   mapaGeralRows = new HashMap();
	   mapaGeral = new Vector();
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
	    	    
	    	    String tomador = rs.getString( 1 );
	    	    
	    	    String itemLista = rs.getString( 2 );
	    	    
	    	    String descServico = rs.getString( 3 );
	    	    String dataEmissao = rs.getString( 4 );
				String dataCompetencia =  getDataDirectOrder( rs.getString( 5 ) ) ;
	    	    String numDoc = rs.getString( 6 );
	    	    String tipoDoc = rs.getString( 7 );
	    	    
				String vlrRecPropria = 
					SEPConverter.adapterCurrencyFrmt( rs.getString( 8 ) );
				double dummy = Double.parseDouble( vlrRecPropria ) ;
				String fmtVlrRecPropria = getFmtCurrency( dummy );	    	    
				String recPropria = fmtVlrRecPropria;
	    	    
				String vlrIssDevido = 
					SEPConverter.adapterCurrencyFrmt( rs.getString( 9 ) );
				dummy = Double.parseDouble( vlrIssDevido ) ;
				String fmtVlrIssDevido = getFmtCurrency( dummy );	    	    
				String issDevido = fmtVlrIssDevido;

	    	    
				String vlrRecSubst = 
					SEPConverter.adapterCurrencyFrmt( rs.getString( 10 ) );
				dummy = Double.parseDouble( vlrRecSubst ) ;
				String fmtVlrRecSubst = getFmtCurrency( dummy );	    	    
				String recSubst = fmtVlrRecSubst;

				String vlrIssSubstTrib = 
					SEPConverter.adapterCurrencyFrmt( rs.getString( 11 ) );
				dummy = Double.parseDouble( vlrIssSubstTrib ) ;
				String fmtVlrIssSubstTrib = getFmtCurrency( dummy );	    	    
				String issSubstTrib = fmtVlrIssSubstTrib;
				
				
				// ::PENDENCIA -> Verificar a existencia de BUG!!
				String tipoLanc = rs.getString( 12 );

				String controlSeqLanc = rs.getString( 13 );
				

	    	    theRow[ 0 ] = tomador;
	    	    theRow[ 1 ] = itemLista;
	    	    theRow[ 2 ] = descServico;
	    	    theRow[ 3 ] = SEPConverter.converteFrmtDataFromMySQL( dataEmissao );
	    	    theRow[ 4 ] = dataCompetencia ;
	    	    theRow[ 5 ] = tipoDoc;
	    	    theRow[ 6 ] = numDoc;
	    	    theRow[ 7 ] = recPropria; 
	    	    theRow[ 8 ] = issDevido; 
   	    	    theRow[ 9 ] = recSubst; 
	    	    theRow[ 10 ] = issSubstTrib;
	    	    theRow[ 11 ] = tipoLanc;
	    	    theRow[ 12 ] = controlSeqLanc;
	    	    
				key = controlSeqLanc ;
	    	
/*	    	    key = tomador  
	    	          + SEPConverter.converteFrmtDataFromMySQL(dataEmissao) 
	    	          + numDoc; */
	    	    mapaGeralRows.put( key, new Integer( bufferRecordPos ) );
	    	    mapaGeral.add( theRow );
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
		String[] lancamento = new String[ FIELDS_QTY ];
		String searchKey;
		mapaGeralRows.clear();
		for ( int i = 0; i < mapaGeral.size() ; i++ ) {
			lancamento = (String[]) mapaGeral.get( i );
			String controlSeq = lancamento[ CONTROL_SEQ ];
			searchKey = controlSeq;
			mapaGeralRows.put( searchKey, new Integer( i ) );
		}
	}
	
	private class TabMapaGeralModel extends AbstractTableModel {
		
		public int getRowCount() {
			return mapaGeralRows.size(); 
		}
		
		public Object getValueAt( int r , int c ) {
			
			String[] theRow = null;
			if ( r < mapaGeral.size() ) {
				Object obj =  mapaGeral.get( r ) ;
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
	

	/**
	 * Precalculate the rates for services list items
	 *
	 * @param con A <code>Connection</code> represeting the current connection
	 *            into the database
	 * @exception Throwns SQLException if unnespected error happened
	 *            for accessing the databae
	 */
	private void loadRates( Connection con ) {
		
		rates = new HashMap();  
		
		try {
		
   	    	Statement stmt = con.createStatement();
   	    	
   	    	String query = "SELECT codigoServico, aliquotaServico " +
   	                   " FROM tabServicos " + 
					   " ORDER BY codigoServico "  ;
   	             
   	    	ResultSet rs = stmt.executeQuery( query );   	    
   	    
   	    	while ( rs.next() ) {
   	    		String item = rs.getString( 1 ); // item da lista
   	    		String rate = rs.getString( 2 ); // aliquota
   	    		rates.put( item, rate );
   	    	}
   	    
		} catch( SQLException ignore ) {
			ignore.printStackTrace();
		}	
   	    
		
	}
	
	private class ComputeTaxes extends AbstractAction {
		public void actionPerformed(ActionEvent e ) {
			fmtCurrencyValues();			
		} 
	}
	
	private class TomadorKeySearcher extends KeyAdapter {		
		   
		String searchKey ="";
		public void keyTyped(KeyEvent e) {
			
			String[] tomadores = sepLookupMapaGeral.getNomeTomadorList();			
			 
			char ch = e.getKeyChar();
			if ( !Character.isLetterOrDigit( ch ))
				return;
				 
			if ( nomeTomadorTxt.getText() != "" ) {
				searchKey += nomeTomadorTxt.getText() + ch ;
				for ( int k = 0; k < tomadores.length; k++ ) {
					String str = tomadores[ k ];
					if ( str.startsWith( searchKey )) {
						nomeTomadorTxt.setText("");
						nomeTomadorTxt.repaint();
						nomeTomadorTxt.setText( tomadores[ k ] );
						nomeTomadorTxt.repaint();
						searchKey = new String("");
						e.setKeyChar(' ');
						break;
					}				   
				}    
				searchKey = new String("");
			} 
		}
	}
	
	private class NaturezaKeySearcher extends KeyAdapter {
		
		String searchKey ="";
		public void keyTyped(KeyEvent e) {
			
			String[] descServico = sepLookupMapaGeral.getDescServicoList();
			
			char ch = e.getKeyChar();
			if ( !Character.isLetterOrDigit( ch ))
				return;
				 
			if ( naturezaServicoTxt.getText() != "" ) {
				searchKey += naturezaServicoTxt.getText() + ch ;
				for ( int k = 0; k < descServico.length; k++ ) {
					String str = descServico[ k ];
					if ( str.startsWith( searchKey )) {
						naturezaServicoTxt.setText("");
						naturezaServicoTxt.repaint();
						naturezaServicoTxt.setText( descServico[ k ] );
						naturezaServicoTxt.repaint();
						searchKey = new String("");
						e.setKeyChar(' ');
						break;
					}				   
				}    
				searchKey = new String("");
			} 
		}
	}
	
	
	
	private class ItemListaKeySearcher extends KeyAdapter {
		
		String searchKey ="";
		public void keyTyped(KeyEvent e) {
			String[] itemLista = sepLookupMapaGeral.getNumItemList();
			
			char ch = e.getKeyChar();
			if ( !Character.isLetterOrDigit( ch ))
				return;
				 
			if ( itemListaTxt.getText() != "" ) {
				searchKey += itemListaTxt.getText() + ch ;
				for ( int k = 0; k < itemLista.length; k++ ) {
					String str = itemLista[ k ];
					if ( str.startsWith( searchKey )) {
						itemListaTxt.setText("");
						itemListaTxt.repaint();
						itemListaTxt.setText( itemLista[ k ] );
						itemListaTxt.repaint();
						searchKey = new String("");
						e.setKeyChar(' ');
						break;
					}				   
				}    
				searchKey = new String("");
			} 
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
				
				StringTokenizer st = 
					new StringTokenizer( dataDocumentoTxt.getText(),"/");
				String dd = st.nextToken();
				String mm = st.nextToken();
				String yy = st.nextToken();		
				String date =  mm + "/" + yy;				
				dataCompetenciaTxt.setText( date );				
				
				f = true;
				return;				   
			}
		}
	}
	
	private class FmtDataCompetenciaListener extends KeyAdapter {
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
				dataCompetenciaTxt.setText("");
				f = false;
			}
			if ( searchKey.length() == 6 ) {
				String month = searchKey.substring( 0, 2 );
				String year = searchKey.substring( 2, 6 );
				dataCompetenciaTxt.setText("");
				String dummy = month + "/" + year;

				dataCompetenciaTxt.setText( dummy );
				
				f = true;
				return;				   
			}
		}
	}
	
	
	private void fmtCurrencyValues() {
		
		  String itemDaLista = itemListaTxt.getText();
		  if ( itemDaLista == null || itemDaLista == "" ) {
			String err = "Campo de item da lista deve ter um valor válido";
			JOptionPane.showMessageDialog( null, err,
			  "Campo inválido", JOptionPane.ERROR_MESSAGE   );
		  }
		  String str = (String) rates.get( itemDaLista );
			
		  if ( str != null ) {	
		  
		      double rate  = Float.parseFloat( 
		         SEPConverter.adapterCurrencyFrmt(str) );
		         
		      // Computes the values from taxes due   
			  String vlrRecPropria = receitaEscritTxt.getText();
			  String value = SEPConverter.adapterCurrencyFrmt( vlrRecPropria );
			  double incomeRecPropria = Double.parseDouble( value ) ;
			  String fmtVlrRecPropria = getFmtCurrency( incomeRecPropria );
			  receitaEscritTxt.setText("");
			  receitaEscritTxt.setText( fmtVlrRecPropria );
			        
		      double taxRecPropria = ( rate * incomeRecPropria ) / 100 ;
		      
			  String fmtVlrIssProprio = getFmtCurrency( taxRecPropria );
			  issEscritTxt.setText("");
			  issEscritTxt.setText( fmtVlrIssProprio );
			  
			  
			  String vlrRecTerceiros = receitaSubstTribTxt.getText();
			  value = SEPConverter.adapterCurrencyFrmt( vlrRecTerceiros );
			  double incomeRecTerceiros = Double.parseDouble( value ) ;
			  String fmtVlrRecTerceiros = getFmtCurrency( incomeRecTerceiros );
			  receitaSubstTribTxt.setText("");
			  receitaSubstTribTxt.setText( fmtVlrRecTerceiros );
			        
			  double taxRecTerceiros = ( rate * incomeRecTerceiros ) / 100 ;
		      
			  String fmtVlrIssTerceiros = getFmtCurrency( taxRecTerceiros );
			  issSubstTribTxt.setText("");
			  issSubstTribTxt.setText( fmtVlrIssTerceiros );
			  
		
		}
		else {
			String err = "Campo de item da lista deve ter um valor válido";
			JOptionPane.showMessageDialog( null, err,
			  "Campo inválido", JOptionPane.ERROR_MESSAGE   );
		}
		
	}
	
	private String getFmtCurrency( double value ){	
		NumberFormat nf = NumberFormat.getInstance( Locale.GERMANY );
		nf.setMaximumFractionDigits( 2 );
		nf.setMinimumFractionDigits( 2 );
		String formattedNumber = nf.format( value );		
		return formattedNumber;
	} 	
	
	private String getDataReverseOrder( String data  ){
		String retVal = "";		
		String month = data.substring( 0, 2 );
		String year = data.substring( 3, 7 );
		retVal =  year + "/" + month;
		return retVal;
	}
	
	private String getDataDirectOrder( String data  ){
		String retVal = "";		
		String year = data.substring( 0, 4 );
		String month = data.substring( 5, 7 );
		retVal =  month + "/" + year;
		return retVal;
	}
	
	
}
