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
//import teste.GeneratorReport;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.text.*;

/**
 * 
 * @author FCARLOS
 * 
 * PENDENCIAS:
 *   1. Ao selecionar um registro na grid atualizar automaticamente area de 
 *      entrada de dados com os valores do registro selecionado
 *   2. Ao clicar no formulario de pesquisa , habilitar o registro correspondente
 *      na grid
 *   3. Permitir a inclusao de aliquotas no proprio formulario
 */


public class SEPFormTabMapaConstrucao extends JPanel {
	
	
	/**
	 * qtd, de campos existente em uma linha da tabela mapaConstrucao
	 */
	private static final int FIELDS_QTY = 17;
	
	/**
	 * Especifica os tipos de lançamentos possíveis obtidos pela fiscalização
	 */
	private static final int LANCAMENTO_ESCRIT = 1;
	private static final int LANCAMENTO_AJUST  = 2;
	
	
	/**
	 * Especifica os tipos de deducoes possiveis
	 */
	 private static final int DEDUCAO_MANUAL = 1;
	 private static final int DEDUCAO_AUTOMATICA = 2;
	 
	 private static final int CONTRATANTE = 0;
	 private static final int NUM_DOC = 5;
	 private static final int DATA_EMISSAO = 6;
	 private static final int CONTROL_SEQ = 16;
	 
	 
	 /**
	  * Mapeia posição do registro selecionada da tabela para um buffer
	  */
	private static int bufferRecordPos = 0;
	
	private static String currOS = null; // currently service order openned
	private static String currCMC = null; // currently company choicen
	
	/**
	 * holds the values of rates used for compute of taxes
	 */
	 private HashMap rates = null;
	
	
	/**
	 * Indicates a buffer manages records into a corresponding service order
	 * Also, is responsible for basic operation of insertion, deleting and
	 * updating of the records in the database
	 */
	private Map mapaConstrucaoRows = null;
	
	/**
	 * Indicates the source of the table model
	 */
	private Vector mapaConstrucao = null;
	 
			
	private String[] columnNames = { "Contratante", "Item", "Natureza", 
	    "Local","Tipo Doc.", "N° Doc.", "Dt.Emissão", 
	    "Data de Competência", "Tipo Deducao","Perc.Dedução", "(+)Receita Bruta",
	    "(-)Ded.subempr.", "(-)Ded.Mat.Aplic.", "(-)Obra fora munic." ,
	    "(=)Rec.Tribut", "Tipo lançamento", "Data de registro"
	};
	
	
	private String tipos[] = { "NFS", "Recibo", "Diversos"  };
	private String aliquotas[] = { "40", "20", "10"  };
	
	
	
    private ComputeIncomesAction computeIncomesAction = null;
	
	private Connection myConnection = null;
	private Statement myStatement = null;
	
	// Displays records for a specified company
	TabMapaConstrucaoModel documentosTableModel = null;
	private static JTable documentosTable = null;
	
	
	// identificação do contribuinte e serviço						
	private static JTextField nomeTomadorTxt = null;
	private static JTextField itemListaTxt = null;
	private static JTextField naturezaServicoTxt = null;
	private static JTextField localExecucaoServicoTxt = null;
	
		
	// identificação do documento
	private static JComboBox tipoDocumentoComboBox = null;
	private static JTextField dataDocumentoTxt = null;
	private static JTextField dataCompetenciaTxt = null;	
	private static JTextField numeroDocumentoTxt = null;
	
		
	// movimento econômico
	private static JTextField receitaEscritTxt = null;
	private static JTextField subEmpreitadaTxt = null;
	private static JTextField materialAplicadoTxt = null;
	private static JTextField obraForaMunicTxt = null;
	private static JTextField receitaTributavelTxt = null;
		
	// tipo de lançamento	
	private static JRadioButton escrituradoRadioButton = null;
	private static JRadioButton ajustadoRadioButton = null;		
	private static JRadioButton manualRadioButton = null;
	private static JRadioButton automaticaRadioButton = null;		
	private static JComboBox percentualCombo = null;
	
	private SEPLookupMapaConstrucao sepLookupMapaConstrucao = null;
	private SEPViewerMapaConstrucao sepViewerMapaConstrucao = null;
	
		
		
    /**
     * Constructor setup initial configuration parameters and loading the 
     * corresponding records to the respective service order
     *
     * @param con a <code>String</code> indicates the established connection
     */
	public SEPFormTabMapaConstrucao( Connection con ) {
		try {
			myConnection = con;
			myStatement = con.createStatement(); 
			
			currOS = EmpresaAtual.getNumeroOS();
			currCMC = EmpresaAtual.getCMCEmpresa();
			
			String query = " SELECT  contratanteMapaConstrucao, itemMapaConstrucao, " + 
			              " servicoMapaConstrucao, localMapaConstrucao, " +
			              " tipoDocMapaConstrucao, dataEmissaoMapaConstrucao, " +
			              " numDocMapaConstrucao, " +
			              " dataCompetenciaMapaConstrucao, tipoDeducaoMapaConstrucao, " +
			              " percentualMapaConstrucao, " +
			              " recBrutaMapaConstrucao, subEmpMapaConstrucao, " +
			              " obrasMapaConstrucao, materialMapaConstrucao, " +
			              " recTribMapaConstrucao, tipoRecMapaConstrucao, seqMapaConstrucao " +
			              " FROM tabMapaConstrucao " +
			              " WHERE osMapaConstrucao='" + currOS + "'" +
			              "  AND cmcMapaConstrucao ='" + currCMC + "'" +
			              " ORDER BY contratanteMapaConstrucao, " +
			              "  dataEmissaoMapaConstrucao, numDocMapaConstrucao ";
			
			              
			ResultSet rs =  myStatement.executeQuery( query );
			// Getting the records of the database
			loadMapaConstrucaoTable( rs );
			
			loadRates( con );
			
			sepLookupMapaConstrucao = new SEPLookupMapaConstrucao( myConnection );
			
		}
		catch ( SQLException ignore ) {
			   ignore.printStackTrace();
	}
	
	}

	public JPanel createTabMapaConstrucaoPanel(){
		
		JPanel tabDocumentosPanel = new JPanel( new BorderLayout() );
		
		JPanel inputTabMapaConstrucaoPanel = new JPanel(); 
		
		inputTabMapaConstrucaoPanel.setLayout( 
		    new  BoxLayout( inputTabMapaConstrucaoPanel, BoxLayout.Y_AXIS ) );  							
		
				
		// identificação do contribuinte e serviço						
		JLabel nomeTomadorLabel = new JLabel("Contratante");
		nomeTomadorTxt = new JTextField( 
							new FixedNumericDocument( 45, false ), "", 35 );
		nomeTomadorTxt.addKeyListener( new ContratanteKeySearcher());							 
		
		
		JLabel itemListaLabel = new JLabel("Item da Lista");
		itemListaTxt = new JTextField( 
							new FixedNumericDocument( 3, true ), "", 3 ); 
		itemListaTxt.addKeyListener( new ItemListaKeySearcher() );					

		JLabel naturezaServicoLabel = new JLabel("Natureza do serviço");
		naturezaServicoTxt = new JTextField( 
							new FixedNumericDocument( 30, false ), "", 25 );
		naturezaServicoTxt.addKeyListener( new NaturezaKeySearcher() );					 
		
		JLabel localExecucaoServicoLabel = new JLabel("Local de execução");
		localExecucaoServicoTxt = new JTextField( 
							new FixedNumericDocument( 14, false ), "TERESINA", 8 ); 

		// identificação do documento
		JLabel tipoDocumentoLabel = new JLabel("Tipo documento");
		tipoDocumentoComboBox = new JComboBox( tipos );
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
		                 
		
		JLabel numeroDocumentoLabel = new JLabel("Número documento");
		numeroDocumentoTxt = new JTextField( 
		                new FixedNumericDocument( 6, true ), "", 6 );
		
		computeIncomesAction = new ComputeIncomesAction();
		
		
		// movimento econômico
		JLabel receitaEscritLabel = new JLabel("(+)Receita Bruta");
		receitaEscritTxt = new JTextField( 10 );
		receitaEscritTxt.setText( "0,00" );
		receitaEscritTxt.setToolTipText("Formato válido: 99.999,99 ou 99999,99");
		receitaEscritTxt.addActionListener( computeIncomesAction );
		JLabel subEmpreitadaLabel = new JLabel("(-)Sub-empreitada" );
		subEmpreitadaTxt = new JTextField( 10 );
		subEmpreitadaTxt.setText( "0,00" );
		subEmpreitadaTxt.setToolTipText("Formato válido: 99.999,99 ou 99999,99");
		subEmpreitadaTxt.setEditable( false );
		subEmpreitadaTxt.addActionListener( computeIncomesAction );
		JLabel materialAplicadoLabel = new JLabel("(-)Mat.aplicado" );
		materialAplicadoTxt = new JTextField( 10 );
		materialAplicadoTxt.setText("0,00" );
		materialAplicadoTxt.setToolTipText("Formato válido: 99.999,99 ou 99999,99");
		materialAplicadoTxt.setEditable( false );
		materialAplicadoTxt.addActionListener( computeIncomesAction );
		JLabel obraForaMunicLabel = new JLabel("(-)Obra fora município" );
		obraForaMunicTxt = new JTextField( 10 );
		obraForaMunicTxt.setText("0,00");
		obraForaMunicTxt.setEditable( false );
		obraForaMunicTxt.addActionListener( computeIncomesAction );
		obraForaMunicTxt.setToolTipText("Formato válido: 99.999,99 ou 99999,99");
		JLabel receitaTributavelLabel = new JLabel("(=)Receita tributável");
		receitaTributavelTxt = new JTextField( 10 );
		receitaTributavelTxt.setText("0,00");
		receitaTributavelTxt.setToolTipText("Formato válido: 99.999,99 ou 99999,99");

		receitaTributavelTxt.setEditable( false );
		
		manualRadioButton = new JRadioButton( "Manual" );
		automaticaRadioButton = new JRadioButton( "Automática" );		
		automaticaRadioButton.setSelected( true );
		ButtonGroup tipoDeducaoButtonGroup = new ButtonGroup();
		tipoDeducaoButtonGroup.add( manualRadioButton );
		tipoDeducaoButtonGroup.add( automaticaRadioButton );
		JLabel percentualLabel = new JLabel("Percentual da dedução (%) " );
		percentualCombo = new JComboBox( aliquotas );
		percentualCombo.addActionListener( computeIncomesAction );
		

		// 	
		escrituradoRadioButton = new JRadioButton( "Receita Escriturada" );
		ajustadoRadioButton = new JRadioButton( "Receita Ajustada" );		
		escrituradoRadioButton.setSelected( true );
		ButtonGroup tipoReceitaButtonGroup = new ButtonGroup();
		tipoReceitaButtonGroup.add( escrituradoRadioButton );
		tipoReceitaButtonGroup.add( ajustadoRadioButton );
		
		manualRadioButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				subEmpreitadaTxt.setEditable( true );
				subEmpreitadaTxt.setText( "0,00" );
				materialAplicadoTxt.setEditable( true );
				materialAplicadoTxt.setText("0,00" );
				obraForaMunicTxt.setEditable( true );
				obraForaMunicTxt.setText("0,00");
				receitaTributavelTxt.setText( receitaEscritTxt.getText() );
				percentualCombo.setEnabled( false );
			}
		});
		
		automaticaRadioButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				subEmpreitadaTxt.setEditable( false );
				subEmpreitadaTxt.setText( "0,00" );
				materialAplicadoTxt.setEditable( false );
				materialAplicadoTxt.setText("0,00" );
				obraForaMunicTxt.setEditable( false );
				obraForaMunicTxt.setText("0,00");
				receitaTributavelTxt.setText( receitaEscritTxt.getText() );
				percentualCombo.setEnabled( true );
			}
		});
		
		
		
		
	
		JPanel tomadorTipoDocPanel  = new JPanel( new FlowLayout( FlowLayout.LEFT )); 
		JPanel servicoPanel  = new JPanel( new FlowLayout( FlowLayout.LEFT )); 
		JPanel dataTipoNumDocPanel // =  new JPanel ( new GridLayout( 1, 6, 5, 5 ) ) ;		
		                  = new JPanel( new FlowLayout( FlowLayout.LEFT )); 
		JPanel movimentoEconomicoPanel = new JPanel( new GridLayout( 2, 10, 5, 5 ) );
		JPanel tipoDeducaoPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
		JPanel tipoReceitaPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
		
		
        Border etchedMovEconomicoPanel =
           BorderFactory.createBevelBorder( BevelBorder.LOWERED ) ;
        TitledBorder titleBorderMovEconomico;
        titleBorderMovEconomico = BorderFactory.createTitledBorder( 
                      etchedMovEconomicoPanel, "Movimentação Econômica");
        titleBorderMovEconomico.setTitleJustification( TitledBorder.LEFT );
		movimentoEconomicoPanel.setBorder( titleBorderMovEconomico );
        
		
		
		tomadorTipoDocPanel.add( nomeTomadorLabel );
		tomadorTipoDocPanel.add( nomeTomadorTxt );
		
		
		servicoPanel.add( itemListaLabel );
		servicoPanel.add( itemListaTxt );		
		servicoPanel.add( naturezaServicoLabel );
		servicoPanel.add( naturezaServicoTxt );
		servicoPanel.add( localExecucaoServicoLabel );
		servicoPanel.add( localExecucaoServicoTxt );		
		
		
		dataTipoNumDocPanel.add( tipoDocumentoLabel );
		dataTipoNumDocPanel.add( tipoDocumentoComboBox );
		dataTipoNumDocPanel.add( numeroDocumentoLabel );
		dataTipoNumDocPanel.add( numeroDocumentoTxt );
		dataTipoNumDocPanel.add( dataDocumentoLabel );
		dataTipoNumDocPanel.add( dataDocumentoTxt );
		dataTipoNumDocPanel.add( dataCompetenciaLabel );
		dataTipoNumDocPanel.add( dataCompetenciaTxt );
		
		

		movimentoEconomicoPanel.add( receitaEscritLabel );
		movimentoEconomicoPanel.add( subEmpreitadaLabel );
		movimentoEconomicoPanel.add( materialAplicadoLabel );
		movimentoEconomicoPanel.add( obraForaMunicLabel );
		movimentoEconomicoPanel.add( receitaTributavelLabel );
		
		movimentoEconomicoPanel.add( receitaEscritTxt );
		movimentoEconomicoPanel.add( subEmpreitadaTxt );
		movimentoEconomicoPanel.add( materialAplicadoTxt );
		movimentoEconomicoPanel.add( obraForaMunicTxt );
		movimentoEconomicoPanel.add( receitaTributavelTxt );
		
		JLabel tipoDeducaoLabel = new JLabel("Dedução ");

        tipoDeducaoPanel.add( tipoDeducaoLabel );
   		tipoDeducaoPanel.add( manualRadioButton );
		tipoDeducaoPanel.add( automaticaRadioButton );
		tipoDeducaoPanel.add( percentualLabel );
		tipoDeducaoPanel.add( percentualCombo );		

		JLabel tipoLancamentoLabel = new JLabel("Lançamento ");

        tipoReceitaPanel.add( tipoLancamentoLabel );
		tipoReceitaPanel.add( escrituradoRadioButton );
		tipoReceitaPanel.add( ajustadoRadioButton );
		
		
        Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Documentos");
        titleBorder.setTitleJustification( TitledBorder.RIGHT );

		inputTabMapaConstrucaoPanel.setBorder( titleBorder );
		
		inputTabMapaConstrucaoPanel.add( tomadorTipoDocPanel );
		inputTabMapaConstrucaoPanel.add( servicoPanel );
		inputTabMapaConstrucaoPanel.add( dataTipoNumDocPanel );
		inputTabMapaConstrucaoPanel.add( tipoDeducaoPanel );		
		inputTabMapaConstrucaoPanel.add( movimentoEconomicoPanel );
		
		inputTabMapaConstrucaoPanel.add( tipoReceitaPanel  );
		
		JButton incluirButton = new JButton("Incluir");
		incluirButton.setMnemonic('I');
		incluirButton.setToolTipText("Cadastra serviço prestado");
		incluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
					fmtCurrencyValues();					
				    addRecord( myConnection );
				    // reloads database containing newer updates
				    sepLookupMapaConstrucao.updateSearching();
				    // updates the listener
					nomeTomadorTxt.addKeyListener( new ContratanteKeySearcher() );					 
					naturezaServicoTxt.addKeyListener( new NaturezaKeySearcher() );					 
					itemListaTxt.addKeyListener( new ItemListaKeySearcher() );

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
				    // After delete a record it is necessary reloads database
				    // for reflecting the newest condition of records from database				
					sepLookupMapaConstrucao.updateSearching();					
					nomeTomadorTxt.addKeyListener( new ContratanteKeySearcher() );					 
					naturezaServicoTxt.addKeyListener( new NaturezaKeySearcher() );					 
					itemListaTxt.addKeyListener( new ItemListaKeySearcher() );
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
					// reloads database containing newer updates
					sepLookupMapaConstrucao.updateSearching();										
					// updates the listener
					nomeTomadorTxt.addKeyListener( new ContratanteKeySearcher() );					 
					naturezaServicoTxt.addKeyListener( new NaturezaKeySearcher() );
					itemListaTxt.addKeyListener( new ItemListaKeySearcher() );
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

					String query = " SELECT  contratanteMapaConstrucao, itemMapaConstrucao, " + 
								  " servicoMapaConstrucao, localMapaConstrucao, " +
								  " tipoDocMapaConstrucao, dataEmissaoMapaConstrucao, " +
								  " numDocMapaConstrucao, " +
								  " dataCompetenciaMapaConstrucao, tipoDeducaoMapaConstrucao, " +
								  " percentualMapaConstrucao, " +
								  " recBrutaMapaConstrucao, subEmpMapaConstrucao, " +
								  " obrasMapaConstrucao, materialMapaConstrucao " +
								  " FROM tabMapaConstrucao " +
								  " WHERE osMapaConstrucao='" + currOS + "'" +
								  "  AND cmcMapaConstrucao ='" + currCMC + "'" +
								  " ORDER BY contratanteMapaConstrucao, " +
								  "  dataEmissaoMapaConstrucao ";
   	    	
					ResultSet rs = stmt.executeQuery( query );   	    
					printReportMapaConstrucao( rs );
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
				sepViewerMapaConstrucao =
				   new SEPViewerMapaConstrucao( myConnection, true );
				sepViewerMapaConstrucao.setVisible( true ); 
			}
		}); 		
		
		
		
		
		JPanel controlMapaConstrucaoPanel = new JPanel( new FlowLayout() );
		
		controlMapaConstrucaoPanel.add( incluirButton );
		controlMapaConstrucaoPanel.add( excluirButton );
		controlMapaConstrucaoPanel.add( gravarButton );
		//controlMapaConstrucaoPanel.add( imprimirButton );
		controlMapaConstrucaoPanel.add( buscarLocalButton );
				
		documentosTableModel = new TabMapaConstrucaoModel();		
		documentosTable = new JTable();
		JTableHeader headers = documentosTable.getTableHeader();
		headers.setReorderingAllowed( false );
		
		documentosTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		documentosTable.addMouseListener( new MouseAdapter()  {
			public void mouseClicked( MouseEvent e ) {
				int count = documentosTable.getSelectedRowCount();
				if ( count > 0 ) {
                    inputTabMapaConstrucaoPanelRefresh();					
				}
			}
		});
		
		documentosTable.setModel( documentosTableModel );
		JScrollPane scrollpane = new JScrollPane( documentosTable );
		
		JPanel mapaConstrucaoDisplayPanel = new JPanel( new BorderLayout() ) ;
		
		mapaConstrucaoDisplayPanel.add( scrollpane );			
		
		tabDocumentosPanel.add( inputTabMapaConstrucaoPanel, BorderLayout.NORTH );
		tabDocumentosPanel.add( mapaConstrucaoDisplayPanel, BorderLayout.CENTER );
		tabDocumentosPanel.add( controlMapaConstrucaoPanel, BorderLayout.SOUTH );		
		
		return tabDocumentosPanel;	
		  
	}
	
	/**
	 * Sets the textfield named nomeTomadorTxt
	 * @param nomeTomador indicates the value for setting
	 */
	public static void setNomeTomadorTxt( String nomeTomador ) {
		nomeTomadorTxt.setText( nomeTomador );
	}
	
	/**
	 * Sets the textfield named nomeTomadorTxt
	 * @param dataCompetencia indicates the value for setting
	 */
	public static void setDataCompetenciaTxt( String dataCompetencia ) {
		dataCompetenciaTxt.setText( dataCompetencia );
	}
	
	/**
	 * Sets the textfield named itemServicoTxt
	 * @param itemServico indicates the value for setting
	 */
	public static void setItemServicoTxt( String itemServico ) {
		itemListaTxt.setText( itemServico );
	}
	
	/**
	 * Sets the textfield named naturezaServicoTxt
	 * @param itemServico indicates the value for setting
	 */
	public static void setNaturezaServicoTxt( String naturezaServico ) {
		naturezaServicoTxt.setText( naturezaServico );
	}
	
	/**
	 * Sets the textfield named dataDocumentoTxt
	 * @param dataDocumento indicates the value for setting
	 */
	public static void setDataDocumentoTxt( String dataDocumento ) {
		dataDocumentoTxt.setText( dataDocumento );
	}
	
	/**
	 * Sets the textfield named numeroDocumentoTxt
	 * @param numDoc indicates the value for setting
	 */
	public static void setNumDocTxt( String numDoc ) {
		numeroDocumentoTxt.setText( numDoc );
	}
	
	/**
	 * Sets the textfield named localExecucaoServicoTxt
	 * @param local indicates the value for setting
	 */
	public static void setLocalExecucaoServicoTxt( String local ) {
		localExecucaoServicoTxt.setText( local );
	}
	
	/**
	 * Sets the textfield named receitaEscritTxt
	 * @param receita indicates the value for setting
	 */
	public static void setReceitaEscritTxt( String receita ) {
		receitaEscritTxt.setText( receita );
	}
	
	/**
	 * Sets the combox named percentualCombo
	 * @param receita indicates the value for setting
	 */
	public static void setAliquotaCombo( String aliquota ) {
		percentualCombo.setSelectedItem( aliquota );
	}

	/**
	 * Sets the combox named tipoDocumentoCombo
	 * @param index indicates the value for setting
	 */
	public static void setTipoDocCombo( int index ) {
		//tipoDocumentoComboBox.setSelectedIndex( Integer.parseInt( value ) - 1  );      
		tipoDocumentoComboBox.setSelectedIndex( index - 1  );
	}

	/**
	 * Sets the textfield named subEmpreitdasTxt
	 * @param subEmpreitada indicates the value for setting
	 */
	public static void setSubEmpreitadaTxt( String subEmpreitada ) {
		subEmpreitadaTxt.setText( subEmpreitada );
	}
	
	
	/**
	 * Sets the radio button named manualRadioButton
	 */
	public static void setDeducaoManual() {
		manualRadioButton.setSelected( true );		
	}
	
	/**
	 * Sets the radio button named automaticaRadioButton
	 */
	public static void setDeducaoAutomatica() {
		automaticaRadioButton.setSelected( true );		
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
	 * Sets the textfield named materialAplicadoTxt
	 * @param materialAplic indicates the value for setting
	 */
	public static void setMaterialAplicadoTxt( String materialAplic ) {
		materialAplicadoTxt.setText( materialAplic );
	} 

	/**
	 * Sets the textfield named obraForaMunicTxt
	 * @param obraForaMunic indicates the value for setting
	 */
	public static void setObraForaMunicTxt( String obraForaMunic ) {
		obraForaMunicTxt.setText( obraForaMunic );
	}
	
	/**
	 * Sets the textfield named receitaTributavelTxt
	 * @param receitaTributavel indicates the value for setting
	 */
	public static void setReceitaTributavelTxt( String receitaTributavel ) {
		receitaTributavelTxt.setText( receitaTributavel );
	}
	
	public static boolean getLancamentoEscriturado() {
		if ( escrituradoRadioButton.isSelected())
		    return true;
		return false;    
	}
	
	public static boolean getLancamentoAjustado() {
		if ( ajustadoRadioButton.isSelected())
			return true;
		return false;    
	}
	
	public static boolean getDeducaoManual() {
		if ( manualRadioButton.isSelected())
			return true;
		return false;    
	}
	
	public static boolean getDeducaoAutomatica() {
		if ( automaticaRadioButton.isSelected())
			return true;
		return false;    
	}
	
	public static String getAliquotaAplic() {
		String retVal = (String) percentualCombo.getSelectedItem();		
		return retVal;
	} 
	
	public static String getTipoDoc() {
		String retVal = (String) tipoDocumentoComboBox.getSelectedItem();		
		return retVal;
	} 
	
	public static void setSelectedRow( String timestampSelected ) {
		int numRows = documentosTable.getRowCount();
		for ( int row = 0; row < numRows ; row++ ) {
		    String timestamp = (String) documentosTable.getValueAt( row, CONTROL_SEQ );
			if ( timestampSelected.equals( timestamp ))
				documentosTable.setRowSelectionInterval( row, row );
			}       
	}
	
	private void printReportMapaConstrucao( ResultSet rs ) {
		String[] columnsNames = { "NRO. N.F", "MÊS/ANO", "TOMADOR DO SERVIÇO",
								 "NATUREZA DO SERVICO", "ITEM ART.116\nLEI 1.761/83", 
								 "RECEITA\nBRUTA", "DEDUCOES", 
								 "RECEITA\nTRIBUTÁVEL", "ALIQ\n  (%)",  "ISSQN" };  
		
		Vector rows = new Vector();
		
		try {	   	
	   
			 while ( rs.next() ) {
	        	
				 String[] theRow = new String[ columnsNames.length ];
	    	    
				 String tomador = rs.getString( 1 );
				 String itemLista = rs.getString( 2 );	    	    
				 String descServico = rs.getString( 3 );
 				 String numDoc = rs.getString( 7 );
				 String dataCompetencia =  getDataDirectOrder( rs.getString( 8 ) ) ;
	    	    
				 String vlrRecBrutaContrib = 
					SEPConverter.adapterCurrencyFrmt( rs.getString( 11 ) );
				 double vlrRecBruta = Double.parseDouble( vlrRecBrutaContrib ) ;
				 String fmtVlrRecBruta = SEPConverter.getFmtCurrency( vlrRecBruta );				 
				 String recBruta = fmtVlrRecBruta;
				 
				 int tipoDeducao =  Integer.parseInt( rs.getString( 9 ) );
				 String deducoes = null;
				 double vlrDeducoes = 0;
				 if ( tipoDeducao == DEDUCAO_AUTOMATICA ) {
				 	String percentualDeducao = rs.getString( 10 ); 
					double rate  = Float.parseFloat( percentualDeducao );
					double deducaoAutomatica = (rate/100) * vlrRecBruta;
					vlrDeducoes = deducaoAutomatica;
					deducoes = SEPConverter.getFmtCurrency( deducaoAutomatica ); 				 	
//					String dummy = Double.toString( deducao );
//					String deducaoAutomatica = 
//					          SEPConverter.adapterCurrencyFrmt( dummy );
//					deducoes = SEPConverter.getFmtCurrency( deducaoAutomatica );				 		    	    
				 }
				 else {
					String subEmpr =
					      SEPConverter.adapterCurrencyFrmt( rs.getString( 12 ) );
				    double vlrDedSubEmpr = Double.parseDouble( subEmpr ) ;
					String obras =
						  SEPConverter.adapterCurrencyFrmt( rs.getString( 13 ) );
					double vlrDedObras = Double.parseDouble( obras ) ;
					String material =
						  SEPConverter.adapterCurrencyFrmt( rs.getString( 14 ) );
					double vlrDedMaterial = Double.parseDouble( material ) ;
					double deducaoManual = vlrDedSubEmpr + vlrDedObras + vlrDedMaterial ;
//					String dummy = Double.toString( deducao );
//					String deducaoManual = 
//					          SEPConverter.adapterCurrencyFrmt( dummy );
					vlrDeducoes = deducaoManual;
					deducoes = SEPConverter.getFmtCurrency( deducaoManual ); 				 	
				 }
				 
				 double vlrRecTributavel =  vlrRecBruta - vlrDeducoes;
				 String fmtVlrRecTributavel = 
				             SEPConverter.getFmtCurrency( vlrRecTributavel );
				 String recTributavel = fmtVlrRecTributavel;	    	    
				 
				 String tmp = (String) rates.get( itemLista );
				 double rate  = Float.parseFloat( 
					   SEPConverter.adapterCurrencyFrmt( tmp) );
				 String aliquota = SEPConverter.getFmtCurrency( rate );   
					   
				 double vlrISSQN = (rate/100) * vlrRecTributavel;
				 String fmtVlrISSQN = SEPConverter.getFmtCurrency( vlrISSQN );
				 String ISSQN = fmtVlrISSQN;	   
				 
				 theRow[ 0 ] = numDoc;
				 theRow[ 1 ] = dataCompetencia;
				 theRow[ 2 ] = tomador;
				 theRow[ 3 ] = descServico;
				 theRow[ 4 ] = itemLista;
				 theRow[ 5 ] = recBruta;
				 theRow[ 6 ] = deducoes;
				 theRow[ 7 ] = recTributavel;
				 theRow[ 8 ] = aliquota + "%";
				 theRow[ 9 ] = ISSQN;
				
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

		// identificação do contribuinte e serviço						
		String contratante = nomeTomadorTxt.getText();	
		String itemLista = itemListaTxt.getText();	
		String naturezaServico = naturezaServicoTxt.getText();
		String localExecucao = localExecucaoServicoTxt.getText();
		
		// identificação do documento
		int itemChoicenCombo =	tipoDocumentoComboBox.getSelectedIndex() + 1 ;
		String tipoDocumento = Integer.toString( itemChoicenCombo );
					
		String numDoc = numeroDocumentoTxt.getText() ;				
		if ( numDoc.length() == 0 ) numDoc = "N/A";
		
		String dataEmissao = dataDocumentoTxt.getText();	
		String dataCompetencia = dataCompetenciaTxt.getText();
		
		String tipoDeducao = null;
		if ( manualRadioButton.isSelected() ) {
			tipoDeducao = Integer.toString( DEDUCAO_MANUAL );
		}
		else {
			tipoDeducao = Integer.toString( DEDUCAO_AUTOMATICA );
		}
		
		String percentual = (String) percentualCombo.getSelectedItem();
			
		
		// movimento econômico
		String receitaEscrit =	receitaEscritTxt.getText();		
		String subEmpreitada = subEmpreitadaTxt.getText();
		String materialAplicado =  materialAplicadoTxt.getText();		
		String obraForaMunic = obraForaMunicTxt.getText();		
		String receitaTributavel = receitaTributavelTxt.getText();
		
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
	    
		boolean bufferHas = mapaConstrucaoRows.containsKey( key );
		if ( bufferHas ) {
			String err = "Tentativa de inserir registro duplicado";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {

			/*java.util.Date now = new java.util.Date();
			long timeStamp = now.getTime();
			//String strTimeStamp = Long.toString( timeStamp );
			DateFormat longTimestamp
			   = DateFormat.getDateTimeInstance( DateFormat.FULL, DateFormat.FULL );			          
			 java.util.Date dummy =  new java.util.Date( timeStamp );
			 String controlSeqLanc = longTimestamp.format( dummy );  */

			// -- Includes data in table model, and so does refreshes gui 
			String lancamento[] = new String[ FIELDS_QTY ];
			
	    	lancamento[ 0 ] = contratante;	    	    
	    	lancamento[ 1 ] = itemLista;
	    	lancamento[ 2 ] = naturezaServico;	    	    
	    	lancamento[ 3 ] = localExecucao;
	    	lancamento[ 4 ] = tipoDocumento;
	    	lancamento[ 5 ] = numDoc;
	    	lancamento[ 6 ] = dataEmissao;
	    	lancamento[ 7 ] = dataCompetencia;
	    	lancamento[ 8 ] = tipoDeducao;
	    	lancamento[ 9 ] = percentual;
	    	lancamento[ 10 ] = receitaEscrit;
	    	lancamento[ 11 ] = subEmpreitada;
	    	lancamento[ 12 ] = materialAplicado;
	    	lancamento[ 13 ] = obraForaMunic;
	    	lancamento[ 14 ] = receitaTributavel;
	    	lancamento[ 15 ] = tipoLancamento;
	    	lancamento[ 16 ] = controlSeqLanc;				
						
						
			// The chunck of data is right,then proceed to addding them to database
			String cmd = "INSERT INTO tabMapaConstrucao " +
			" VALUES  ('"  + currOS + "','" + currCMC + "','" 
			            + contratante + "','" + itemLista + "','" + naturezaServico + "','"
			            + localExecucao + "','" + tipoDocumento + "','"
			            + SEPConverter.converteFrmtDataToMySQL(dataEmissao) + "','"
//    		    + SEPConverter.converteFrmtDataToMySQL(dataCompetencia) + "','"
			            + getDataReverseOrder( dataCompetencia ) + "','"
			            + tipoDeducao + "','" + percentual + "','"
			            + numDoc + "','" 
			            + SEPConverter.convertFrmtCurrencyToMySQL(receitaEscrit) + "','"
			            + SEPConverter.convertFrmtCurrencyToMySQL(subEmpreitada) + "','" 
			            + SEPConverter.convertFrmtCurrencyToMySQL(obraForaMunic) + "','" 
			            + SEPConverter.convertFrmtCurrencyToMySQL(materialAplicado) + "','" 
			            + SEPConverter.convertFrmtCurrencyToMySQL(receitaTributavel) + "','"
			            + tipoLancamento + "','"
			            + controlSeqLanc +  " ')" ;
			               
			myStatement.executeUpdate( cmd ); 
			con.commit();		 
				 
			
			mapaConstrucao.add( lancamento );
			
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
/*		String contratante
		   = (String) documentosTable.getValueAt(selectedRow, CONTRATANTE );
		String dataEmissao 
		   = (String) documentosTable.getValueAt(selectedRow, DATA_EMISSAO );
		String numDoc 
		   = (String) documentosTable.getValueAt(selectedRow, NUM_DOC ); */
		   
		String controlSeq 
		   = (String) documentosTable.getValueAt(selectedRow, CONTROL_SEQ );   
		   
		
		String searchKey = controlSeq ;
	    
		Integer rowID = (Integer) mapaConstrucaoRows.get( searchKey );
		
	
		// -- position in the buffer of choicen record
		int index = rowID.intValue(); 
                   
		mapaConstrucao.remove( index );
		bufferRefresh();                // updating buffer
		documentosTable.setModel( documentosTableModel );
		documentosTable.revalidate();
		documentosTable.repaint();
		revalidate();
		repaint();

		String cmd = "DELETE FROM  tabMapaConstrucao" + 
		             " WHERE osMapaConstrucao = '" + currOS + "'" +
		             " AND cmcMapaConstrucao ='" + currCMC + "'" +
		             " AND seqMapaConstrucao ='" + searchKey + "'" ; 
  		             
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
		
		int selectedRow = documentosTable.getSelectedRow();
		
		
		// -- primary key into to choicen record 
		String controlSeq 
		   = (String) documentosTable.getValueAt( selectedRow, CONTROL_SEQ );
		//String controlSeqLancPrev = controlSeq;
		
		String searchKey = controlSeq ;
		
		boolean bufferHas = mapaConstrucaoRows.containsKey( searchKey );
		if ( !bufferHas ) {
			String err = "Tentativa de atualizar registro inexistente";
			JOptionPane.showMessageDialog( null, err,
			      "Atualizacao inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			
			
			// identificação do contribuinte e serviço
			String contratante = nomeTomadorTxt.getText(); 
			String numDoc = numeroDocumentoTxt.getText();
			String dataEmissao = dataDocumentoTxt.getText();				
			String itemLista = itemListaTxt.getText();	
			String naturezaServico = naturezaServicoTxt.getText();
			String localExecucao = localExecucaoServicoTxt.getText();
		
			// identificação do documento
			int itemChoicenCombo =	tipoDocumentoComboBox.getSelectedIndex() + 1 ;
			String tipoDocumento = Integer.toString( itemChoicenCombo );			
			String dataCompetencia = dataCompetenciaTxt.getText();
		

			// movimento econômico
			String receitaEscrit =	receitaEscritTxt.getText();		
			String subEmpreitada = subEmpreitadaTxt.getText();
			String materialAplicado =  materialAplicadoTxt.getText();		
			String obraForaMunic = obraForaMunicTxt.getText();		
			String receitaTributavel = receitaTributavelTxt.getText();
			
			String tipoLancamento = null;
			if ( escrituradoRadioButton.isSelected() ) {
				tipoLancamento = Integer.toString( LANCAMENTO_ESCRIT );
			}
			else {
				tipoLancamento = Integer.toString( LANCAMENTO_AJUST );
			} 		
			
			String tipoDeducao = null;
			if ( manualRadioButton.isSelected() ) {
				tipoDeducao = Integer.toString( DEDUCAO_MANUAL );
			}
			else {
				tipoDeducao = Integer.toString( DEDUCAO_AUTOMATICA );
			}
		
			String percentual = (String) percentualCombo.getSelectedItem();
			
			
			String lancamento[] = new String[ FIELDS_QTY ];					
			
	    	lancamento[ 0 ] = contratante;	    	    
	    	lancamento[ 1 ] = itemLista;
	    	lancamento[ 2 ] = naturezaServico;	    	    
	    	lancamento[ 3 ] = localExecucao;
	    	lancamento[ 4 ] = tipoDocumento;
	    	lancamento[ 5 ] = numDoc;
	    	lancamento[ 6 ] = dataEmissao;
	    	lancamento[ 7 ] = dataCompetencia;
	    	lancamento[ 8 ] = tipoDeducao;
	    	lancamento[ 9 ] = percentual;
	    	lancamento[ 10 ] = receitaEscrit;
	    	lancamento[ 11 ] = subEmpreitada;
	    	lancamento[ 12 ] = materialAplicado;
	    	lancamento[ 13 ] = obraForaMunic;
	    	lancamento[ 14 ] = receitaTributavel;
	    	lancamento[ 15 ] = tipoLancamento;
	    	
			java.util.Date now = new java.util.Date();
			long timeStamp = now.getTime();
			String strTimeStamp = Long.toString( timeStamp );
			DateFormat longTimestamp
			   = DateFormat.getDateTimeInstance( DateFormat.FULL, DateFormat.FULL );			          
			 java.util.Date dummy =  new java.util.Date( timeStamp );
			 String controlSeqLanc = longTimestamp.format( dummy );
			   
			lancamento[ 16 ] = controlSeqLanc; 

			
			// -- The calling for updating records is done
			// Here, is upadted just the field valor because
			// the other fields behaviour as unique keys			
			
			
			String cmd = "UPDATE tabMapaConstrucao "
			          + "SET servicoMapaConstrucao ='" + naturezaServico + "', "		          
					  + "contratanteMapaConstrucao ='" +  contratante + "', "
					  + "numDocMapaConstrucao ='" +  numDoc + "', "
					  + "dataEmissaoMapaConstrucao ='"
					      +  SEPConverter.converteFrmtDataToMySQL(dataEmissao) + "', "
			          + "itemMapaConstrucao ='" +  itemLista + "', "
			          + "dataCompetenciaMapaConstrucao = '" 
			          + getDataReverseOrder(dataCompetencia) + "',"
			          + "localMapaConstrucao ='" +  localExecucao + "', "
			          + "tipoDocMapaConstrucao ='"   + tipoDocumento + "', "
			          + "percentualMapaConstrucao ='" + percentual + "', "
			          + "recBrutaMapaConstrucao ='"
			          + SEPConverter.convertFrmtCurrencyToMySQL(receitaEscrit) + "', " 
			          + "subEmpMapaConstrucao ='" 
			          + SEPConverter.convertFrmtCurrencyToMySQL(subEmpreitada) + "', "
			          + "obrasMapaConstrucao ='" 
			          +  SEPConverter.convertFrmtCurrencyToMySQL(obraForaMunic) + "', "
			          + "materialMapaConstrucao = '" 
			          + SEPConverter.convertFrmtCurrencyToMySQL(materialAplicado) + "', " 
			          + "recTribMapaConstrucao = '" 
			          + SEPConverter.convertFrmtCurrencyToMySQL(receitaTributavel) + "', "			          
			          + "tipoDeducaoMapaConstrucao = '" +  tipoDeducao + "', " 
			          + "tipoRecMapaConstrucao = '" +  tipoLancamento + "', "
			          + "seqMapaConstrucao = '" + controlSeqLanc + "' " 
				      +  " WHERE osMapaConstrucao ='" + currOS + "'" 
					  +  " AND cmcMapaConstrucao='" + currCMC + "'" 
  			          +  " AND seqMapaConstrucao ='" + searchKey + "'" ; 

			
	 		myStatement.executeUpdate( cmd );		
	 		
			con.commit();				
			
			Integer rowID = (Integer) mapaConstrucaoRows.get(  searchKey  );
			// -- position in the buffer of choicen record
			int index = rowID.intValue();
			// -- remove first and insert again. Doing this, is not 
			// -- necessary to worry with refreshing cacher buffer
			mapaConstrucao.remove( index );
			mapaConstrucao.add( index, lancamento );		
			
			bufferRefresh();                // updating buffer
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
    private void inputTabMapaConstrucaoPanelRefresh() {   	
    
    	String value;    	
		int selectedRow = documentosTable.getSelectedRow();
		
		// identificação do contribuinte e serviço						
		value = (String) documentosTable.getValueAt(selectedRow, 0 );
		nomeTomadorTxt.setText( value );
		value = (String) documentosTable.getValueAt(selectedRow, 1 );
		itemListaTxt.setText( value );	
		value = (String) documentosTable.getValueAt(selectedRow, 2 );
		naturezaServicoTxt.setText( value );
		value = (String) documentosTable.getValueAt(selectedRow, 3 );
		localExecucaoServicoTxt.setText( value );		
		

		// identificação do documento
		value = (String) documentosTable.getValueAt(selectedRow, 4 );
        tipoDocumentoComboBox.setSelectedIndex( Integer.parseInt( value ) - 1  );      
		value = (String) documentosTable.getValueAt(selectedRow, 5 );
		numeroDocumentoTxt.setText( value );		        
		value = (String) documentosTable.getValueAt(selectedRow, 6 );
		dataDocumentoTxt.setText( value );				
		value = (String) documentosTable.getValueAt(selectedRow, 7 );
		dataCompetenciaTxt.setText( value );				
		
		value = (String) documentosTable.getValueAt( selectedRow, 8 );		
		int choice = Integer.parseInt( value ) ;		
		if ( choice == DEDUCAO_MANUAL ) {
			manualRadioButton.setSelected( true );						
			subEmpreitadaTxt.setEditable( true );
			materialAplicadoTxt.setEditable( true );
			obraForaMunicTxt.setEditable( true );
			percentualCombo.setEnabled( false );
			percentualCombo.setSelectedIndex( 1 );
		}
		else {
			automaticaRadioButton.setSelected( true );			
			subEmpreitadaTxt.setEditable( false );
			materialAplicadoTxt.setEditable( false );
			obraForaMunicTxt.setEditable( false );
			percentualCombo.setEnabled( true );
		}
		
		value = (String) documentosTable.getValueAt(selectedRow, 9 );
		percentualCombo.setSelectedItem( value );
		
		// movimento econômico		
		value = (String) documentosTable.getValueAt(selectedRow, 10 );
		receitaEscritTxt.setText( value );		
		value = (String) documentosTable.getValueAt(selectedRow, 11 );
		subEmpreitadaTxt.setText( value );		
		value = (String) documentosTable.getValueAt(selectedRow, 12 );
		materialAplicadoTxt.setText( value );		
		value = (String) documentosTable.getValueAt(selectedRow, 13 );
		obraForaMunicTxt.setText( value );		
		
		value = (String) documentosTable.getValueAt(selectedRow, 14 );
		receitaTributavelTxt.setText( value );
		
		value = (String) documentosTable.getValueAt( selectedRow, 15 );		
		choice = Integer.parseInt( value ) ;
		
		if ( choice == LANCAMENTO_ESCRIT  ) {
			escrituradoRadioButton.setSelected( true );			
		}
		else {
			ajustadoRadioButton.setSelected( true );			
		}
		
		revalidate();		
		
		
    }	
    
	
	private void loadMapaConstrucaoTable( ResultSet rs ) {
		
	   int cols = columnNames.length;
	   mapaConstrucaoRows = new HashMap();
	   mapaConstrucao = new Vector();
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
	    	    String item = rs.getString( 2 );
	    	    String naturezaServico = rs.getString( 3 );
	    	    String localExecucao = rs.getString( 4 );
	    	    String tipoDocumento = rs.getString( 5 );
	    	    String dataEmissao =
	    	        SEPConverter.converteFrmtDataFromMySQL(rs.getString( 6 ) );
	    	    String numDoc = rs.getString( 7 );
	    	    String dataCompetencia =  getDataDirectOrder( rs.getString( 8 ) ) ;
	    	    String tipoDeducao = rs.getString( 9 );    
	    	    String percentualDeducao = rs.getString( 10 );
	    	    
				String vlrReceita = 
				    SEPConverter.adapterCurrencyFrmt( rs.getString( 11 ) );
				double dummy = Double.parseDouble( vlrReceita ) ;
				String fmtVlrReceita = getFmtCurrency( dummy );	    	    
				String receita = fmtVlrReceita;
   	    
				String vlrDeducao = 
				    SEPConverter.adapterCurrencyFrmt( rs.getString( 12 ) );
				dummy = Double.parseDouble( vlrDeducao ) ;
				String fmtVlrDeducao = getFmtCurrency( dummy );	    	    
				String dedSubEmpr = fmtVlrDeducao;

				String vlrMatAplic = 
				    SEPConverter.adapterCurrencyFrmt( rs.getString( 13 ) );
				dummy = Double.parseDouble( vlrMatAplic ) ;
				String fmtVlrMatAplic = getFmtCurrency( dummy );	    	    
				String dedMatAplic = fmtVlrMatAplic;
   	        
				String vlrObraForaMunic = 
				   SEPConverter.adapterCurrencyFrmt( rs.getString( 14 ) );
				dummy = Double.parseDouble( vlrObraForaMunic ) ;
				String fmtVlrObraForaMunic = getFmtCurrency( dummy );	    	    
				String obraForaMunic = fmtVlrObraForaMunic;

				String vlrRecTrib = 
				   SEPConverter.adapterCurrencyFrmt( rs.getString( 15 ) );
				dummy = Double.parseDouble( vlrRecTrib ) ;
				String fmtVlrRecTrib = getFmtCurrency( dummy );	    	    
				String recTribut = fmtVlrRecTrib;

	    	    String tipoLanc = rs.getString( 16 );

				String controlSeqLanc = rs.getString( 17 );
	    	    
/*	    	    long timestamp = Long.parseLong( rs.getString( 17 ));
				DateFormat longTimestamp
				   = DateFormat.getDateTimeInstance( DateFormat.FULL, DateFormat.FULL );			          
				 java.util.Date date =  new java.util.Date( timestamp );
				String controlSeqLanc = (String)longTimestamp.format( date );
	    	    */
	    	    theRow[ 0 ] = tomador;	    	    
	    	    theRow[ 1 ] = item;
	    	    theRow[ 2 ] = naturezaServico;	    	    
	    	    theRow[ 3 ] = localExecucao;
	    	    theRow[ 4 ] = tipoDocumento;
	    	    theRow[ 5 ] = numDoc;
	    	    theRow[ 6 ] = dataEmissao;
	    	    theRow[ 7 ] = dataCompetencia;
	    	    theRow[ 8 ] = tipoDeducao;
	    	    theRow[ 9 ] = percentualDeducao;	    	    
	    	    theRow[ 10 ] = receita;
	    	    theRow[ 11 ] = dedSubEmpr;
	    	    theRow[ 12 ] = dedMatAplic;
	    	    theRow[ 13 ] = obraForaMunic;
	    	    theRow[ 14 ] = recTribut;
	    	    theRow[ 15 ] = tipoLanc;
	    	    theRow[ 16 ] = controlSeqLanc;
	    	    
	    	    key = controlSeqLanc ;

	    	    mapaConstrucaoRows.put( key, new Integer( bufferRecordPos ) );
	    	    mapaConstrucao.add( theRow );
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
		mapaConstrucaoRows.clear();
		for ( int i = 0; i < mapaConstrucao.size() ; i++ ) {
			lancamento = (String[]) mapaConstrucao.get( i );
			String controlSeq = lancamento[ CONTROL_SEQ ];
            searchKey = controlSeq ;
			mapaConstrucaoRows.put( searchKey, new Integer( i ) );
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
	
	
	private class TabMapaConstrucaoModel extends AbstractTableModel {
		
		public int getRowCount() {
			return mapaConstrucaoRows.size();
		}
		
		public Object getValueAt( int r , int c ) {
			String[] theRow = null;
			if ( r < mapaConstrucao.size() ) {
				Object obj =  mapaConstrucao.get( r ) ;
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
	
	private class ComputeIncomesAction extends AbstractAction {
		public void actionPerformed(ActionEvent e ) {			
			fmtCurrencyValues();	
		}
	}
	
	private void fmtCurrencyValues() {
		float receita;
		float empreitada;
		float material;
		float obras;
		float receitaTributavel;
		if ( manualRadioButton.isSelected() ) {
						
			 String vlrRecEscrit = receitaEscritTxt.getText();
			 String value = SEPConverter.adapterCurrencyFrmt( vlrRecEscrit );
			 double dummy = Double.parseDouble( value ) ;
			 String fmtVlrRecEscrit = getFmtCurrency( dummy );
			 receitaEscritTxt.setText("");
			 receitaEscritTxt.setText( fmtVlrRecEscrit );
			 String receitaEscrit = 
			 SEPConverter.adapterCurrencyFrmt(receitaEscritTxt.getText());
			
			 String vlrSubEmpreit = subEmpreitadaTxt.getText();
			 value = SEPConverter.adapterCurrencyFrmt( vlrSubEmpreit );
			 dummy = Double.parseDouble( value ) ;
			 String fmtVlrSubEmpreit = getFmtCurrency( dummy );
			 subEmpreitadaTxt.setText("");
			 subEmpreitadaTxt.setText( fmtVlrSubEmpreit );
			 String subEmpreitada = 
			  SEPConverter.adapterCurrencyFrmt( subEmpreitadaTxt.getText() );
			   
			 String vlrMatAplic = materialAplicadoTxt.getText();
			 value = SEPConverter.adapterCurrencyFrmt( vlrMatAplic );
			 dummy = Double.parseDouble( value ) ;
			 String fmtVlrMatAplic = getFmtCurrency( dummy );
			 materialAplicadoTxt.setText("");
			 materialAplicadoTxt.setText( fmtVlrMatAplic );
			 String materialAplicado = 
			   SEPConverter.adapterCurrencyFrmt( materialAplicadoTxt.getText() );
			   
			 String vlrObraForaMunic = obraForaMunicTxt.getText();
			 value = SEPConverter.adapterCurrencyFrmt( vlrObraForaMunic );
			 dummy = Double.parseDouble( value ) ;
			 String fmtVlrObraForaMunic = getFmtCurrency( dummy );
			 obraForaMunicTxt.setText("");
			 obraForaMunicTxt.setText( fmtVlrObraForaMunic );
			 String obraForaMunic = 
				SEPConverter.adapterCurrencyFrmt( obraForaMunicTxt.getText() );       
						
			 receita = Float.parseFloat( receitaEscrit );
			 empreitada = Float.parseFloat( subEmpreitada );
			 material = Float.parseFloat( materialAplicado );
			 obras = Float.parseFloat(obraForaMunic );
	        
			 receitaTributavel = receita - ( empreitada + material + obras );
	        
			 String fmtValueRecTrib = getFmtCurrency( receitaTributavel );
			 receitaTributavelTxt.setText("");
			 receitaTributavelTxt.setText( fmtValueRecTrib );

		}
		else {			
			
			String vlrRecEscrit = receitaEscritTxt.getText();
			String value = SEPConverter.adapterCurrencyFrmt( vlrRecEscrit );
			double dummy = Double.parseDouble( value ) ;
			String fmtVlrRecEscrit = getFmtCurrency( dummy );
			receitaEscritTxt.setText("");
			receitaEscritTxt.setText( fmtVlrRecEscrit );
			String receitaEscrit = 
			SEPConverter.adapterCurrencyFrmt(receitaEscritTxt.getText());
			receita = Float.parseFloat( receitaEscrit );

			String vlrAliquota = (String)percentualCombo.getSelectedItem();
			String percentual = SEPConverter.adapterCurrencyFrmt( vlrAliquota );
			float aliquota = Float.parseFloat( percentual ) ;


			 receitaTributavel = ( receita * (100-aliquota) ) / 100;
			 
			String fmtValueRecTrib = getFmtCurrency( receitaTributavel );
			receitaTributavelTxt.setText("");
			receitaTributavelTxt.setText( fmtValueRecTrib );
			 
		}


		
	}
	
	private class ContratanteKeySearcher extends KeyAdapter {
		
		   
		String searchKey ="";
		public void keyTyped(KeyEvent e) {
			
			String[] tomadores = sepLookupMapaConstrucao.getNomeContratanteList();
			
//			for ( int i = 0; i < tomadores.length; i++ )
//			  System.out.println("tomadores[" + i + "]=" + tomadores[ i ] );

			 
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
			
			String[] descServico = sepLookupMapaConstrucao.getDescServicoList();
			
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
			String[] itemLista = sepLookupMapaConstrucao.getItemServicoList();
			
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
