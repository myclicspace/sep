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

//import sep.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
//import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;
//import java.text.*;

//-> PENDENCIA: 
// #1. Finalizar painel de controle para empressas e fiscais
// #2. Verificar BUG na montagem da subquery
// #3; Finalizar painel de controle de OS ( inclusão, exclusão, etc )
// #4. nÃO Exibir registros para empresas e fisais enquanto não tiver
//     sido selecionado um registro da grid de OS 

//-> BUG: Inclusão de dados não está validando se a empresa existe
//        Anteriormente,        

public class SEPFormTabOS extends JPanel {
		
		
     /**
      * Tracker the position of record in the buffer
      */		
     private static int cmcEmpresaPosTracker = 0;
      
	
	/**
	 * Represents the column names of each table
	 */
	private String[] empresasColumnNames = { "CMC", "Razao Social", "Período inicial",
						"Período final", "Codigo do projeto", "Número do processo" };
									 	
	private String[] fiscaisColumnNames = { "Matricula do fiscal", "Nome do fiscal"};
	
	private String[] osColumnNames = { "Número da ordem de serviço" } ; 
	                                      

	/**
	 * Indicates member variable for tables and its model, respectively
	 */
	private TabOSModel osModel = null;
	private JTable osTable = null;	
	private TabEmpresasModel empresasModel = null;
	private JTable empresasTable = null;	
	private TabFiscaisModel fiscaisModel = null;
	private JTable fiscaisTable = null;
	
	/**
	 * Availables a search engine for searching company´s name known its ID ( cmc ) 
	 **/
	private Map cmcEmpresasTracker = new HashMap();
	
	/**
	 *  Avaiblables a search engine for finding code of revenue officer known
	 *  him name. This tracker is loaded only once.
	 */
	private Map codeFiscalTracker = null;
	 
	 /**
	  *  Holds the name and code of a specified revenue officer
	  *  Its functionality is used into checking for dupplicated records
	  *	 This tracker, certanly, will changes dynamically
	  **/
	private Map nameCodeFiscalTracker = null;
	
	/**
	 *  Holding the code of os, project and process number. 
	 */
	private Map osTracker = null;
	
	
	/** Indicates values for editing of tabEmpresasPanel input panel */
	JTextField cmcTextField = null;
	JTextField inicioPeriodoTxt = null;
	JTextField finalPeriodoTxt = null;
	
	/** Indicates values for editing of tabOSPanel input panel */
	JTextField codigoOSTxt = null;
	JTextField codigoProjetoTxt = null;
	JTextField numeroProcessoTxt = null;	
	
	/** Indicates the list of revenue officer ( = fiscais de tributos ) */
	JComboBox fiscalCombo	 = null;
	
	/**
	 * Holds the names of fiscais for displaying in the JComboBox
	 */
	private Vector fiscaisNames = null;
	
	/**
	 * Holds rows of grid tableOSPanel
	 */
	private Vector os = null;
	
	/**
	 * Holds rows of grid for the panel called tableEmpresasPanel 
	 */
	private Vector empresas = null ; 
	
	/**
	 * Holds rows of grid for the panel called tableFiscaisPanel 
	 */
	private Vector fiscais = null ; 
	
	

	/**
	 * Holds rows of table tabOS and tabOSFiscal for displaying in the grid way
	 */
	private Map rows = null;
	
	
	/**
	 * bufferRecordPos represents the raw-order was stored in the buffer for 
	 * the record read of table when of starting load of that records
	 */
	private int bufferRecordPos = 0;
	
	/**
	 * Indicates member variable of connection
	 */	 
	private Connection myConnection = null;
	
	/**
	 * Indicates member variable for executing the queries
	 */	 
	private Statement myStatement = null;	
	
	
	public SEPFormTabOS ( Connection con ) {
		
		try {
			
			String loggedUser = EmpresaAtual.getUsuario();
			
			myConnection = con;
			myStatement = con.createStatement();
			Statement stmt = con.createStatement();
			              
			              
/*			String query = "SELECT DISTINCT tabOS.codigoOS, tabOS.codigoProjetoOS,  " +
			        " tabOS.numeroProcessoOS " + 
			        " FROM tabOS, tabOSFiscal, tabEmpresa " +
			           " WHERE tabOS.codigoOS = tabOSFiscal.codigoOSFiscal " +
			           " AND tabOS.codigoProjetoOS = tabOSFiscal.codProjOSFiscal " +
			              " AND tabOS.cmcEmpresaOS = tabEmpresa.cmcEmpresa" +
			              " AND tabOSFiscal.matrOSFiscal = " + loggedUser +
			              " ORDER BY tabOS.codigoOS " ; */


			String query = "SELECT DISTINCT tabOSFiscal.codigoOSFiscal " +
//			        " tabOSFiscal.codProjOSFiscal,  " +
//			        " tabOSFiscal.numProcOSFiscal " + 
			        " FROM tabOSFiscal " +
			           " WHERE tabOSFiscal.matrOSFiscal = " + loggedUser +
			              " ORDER BY tabOSFiscal.codigoOSFiscal " ;
			              
			ResultSet rs = stmt.executeQuery( query );
			loadOSTable( rs );	
			loadFiscaisCombo( con );			
			
			
		} catch ( SQLException ignore ) {
			System.out.println( ignore.getMessage() );
			ignore.printStackTrace();
		}	
		
	}

	public JPanel createTabOSPanel(){
		
		
		JPanel outerDisplayPanel =  new JPanel();
		outerDisplayPanel.setLayout( new BoxLayout( outerDisplayPanel,
										  BoxLayout.Y_AXIS) );		
		
		JPanel tabOSPanel = new JPanel( new BorderLayout() );		
		JPanel infoOSPanel = new JPanel( new GridLayout( 1, 4, 5, 5 ) );
		
		
		JPanel inputOSPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
		JLabel codigoOSLabel = new JLabel("Ordem de serviço");
		codigoOSTxt = new JTextField( 10 );		
		
		inputOSPanel.add( codigoOSLabel );
		inputOSPanel.add( codigoOSTxt );
		infoOSPanel.add( inputOSPanel );	
		
		
		// Creates area for input panel for company
		JPanel infoEmpresasPanel = new JPanel( new BorderLayout() );
        Border etchedEmpresasPanel = BorderFactory.createEtchedBorder();
        TitledBorder titleBorderEmpresasPanel;
        titleBorderEmpresasPanel = BorderFactory.createTitledBorder(
		       etchedEmpresasPanel, "Empresas");
        titleBorderEmpresasPanel.setTitleJustification(TitledBorder.LEFT);
		infoEmpresasPanel.setBorder( titleBorderEmpresasPanel );		
		JPanel inputEmpresasPanel = createEmpresasPanel();
		infoEmpresasPanel.add( inputEmpresasPanel );		
		outerDisplayPanel.add( infoEmpresasPanel );
		
		
		// Creates area for input panel for revenue officer 
		JPanel infoFiscaisPanel = new JPanel( new BorderLayout() );
        Border etchedFiscaisPanel = BorderFactory.createEtchedBorder();
        TitledBorder titleBorderFiscaisPanel;
        titleBorderFiscaisPanel = BorderFactory.createTitledBorder(
		       etchedFiscaisPanel, "Fiscais");
        titleBorderFiscaisPanel.setTitleJustification(TitledBorder.LEFT);
		infoFiscaisPanel.setBorder( titleBorderFiscaisPanel );		
		JPanel inputFiscaisPanel = createFiscaisPanel();
		infoFiscaisPanel.add( inputFiscaisPanel ); 		
		outerDisplayPanel.add( infoFiscaisPanel ); 
		
		// Creates grid group from the service order
		JPanel gridOSPanel = new JPanel( new BorderLayout() );
		Border etchedOSPanel = BorderFactory.createEtchedBorder();
        TitledBorder titleBorderOSPanel;
        titleBorderOSPanel = BorderFactory.createTitledBorder(
		       etchedOSPanel, "Ordems de serviços cadastradas");
        titleBorderOSPanel.setTitleJustification(TitledBorder.LEFT);
		gridOSPanel.setBorder( titleBorderOSPanel );		
		JPanel displayOSPanel = createOSPanel();
		gridOSPanel.add( displayOSPanel );
		outerDisplayPanel.add( gridOSPanel );
		

		JPanel controlOSPanel = createControlOSPanel();
		
		tabOSPanel.add( infoOSPanel, BorderLayout.NORTH );
		tabOSPanel.add( outerDisplayPanel );		
		
		tabOSPanel.add( controlOSPanel, BorderLayout.SOUTH );		
					
		return tabOSPanel;
		 
	}
	
	private JPanel createEmpresasPanel() {		
		
		JPanel empresasPanel = new JPanel( new BorderLayout());
		Border etched = BorderFactory.createEtchedBorder();		
		JPanel inputEmpresasPanel = new JPanel( new GridLayout( 1, 6, 5, 5 ) );
		inputEmpresasPanel.setBorder( etched );
		JLabel cmcLabel = new JLabel( "Insc. Municipal");
//		cmcTextField = new JTextField( 5 );		
		cmcTextField = new JTextField( new FixedNumericDocument( 9, false ), "", 9 );
		cmcTextField.setToolTipText("Formato válido: 999999-9 ou 9999999");
		cmcTextField.addActionListener( new MaskCMCHandler());

		
		JLabel inicioPeriodoLabel = new JLabel("Periodo inicial");
//		inicioPeriodoTxt = new JTextField( 8 );
		
		inicioPeriodoTxt = new JTextField( 
						 new FixedNumericDocument( 10, false ), "99/99/9999", 6 );
		inicioPeriodoTxt.setToolTipText("Formato válido: DD/MM/AAAA");                 
		inicioPeriodoTxt.addKeyListener( new FmtInitDataAuditListener() );		
		
		JLabel codigoProjetoLabel = new JLabel( "Projeto");
		codigoProjetoTxt = new JTextField( 10 );
		JLabel numProcessoLabel = new JLabel("Processo" );
		numeroProcessoTxt = new JTextField( 10 );
		numeroProcessoTxt.setText("N/A");
		
		
		
		/* Date now = new Date();
		DateFormat fmt = null;
		String s = fmt.format( now );
		inicioPeriodoTxt.setfor */
		
		JLabel finalPeriodoLabel = new JLabel("Periodo final");
//		finalPeriodoTxt = new JTextField( 8 );
		finalPeriodoTxt = new JTextField( 
						 new FixedNumericDocument( 10, false ), "99/99/9999", 6 );
		finalPeriodoTxt.setToolTipText("Formato válido: DD/MM/AAAA");                 
		finalPeriodoTxt.addKeyListener( new FmtEndDataAuditListener() );		
		
		inputEmpresasPanel.add( cmcLabel );
		inputEmpresasPanel.add( cmcTextField );
		inputEmpresasPanel.add( inicioPeriodoLabel );
		inputEmpresasPanel.add( inicioPeriodoTxt );
		inputEmpresasPanel.add( finalPeriodoLabel );
		inputEmpresasPanel.add( finalPeriodoTxt );
		inputEmpresasPanel.add( codigoProjetoLabel );
		inputEmpresasPanel.add( codigoProjetoTxt );
		inputEmpresasPanel.add( numProcessoLabel ) ;
		inputEmpresasPanel.add( numeroProcessoTxt );
		
		
		empresasModel = new TabEmpresasModel();		
		empresasTable = new JTable(); 
		empresasTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		empresasTable.setModel( empresasModel );
		
		empresasTable.addMouseListener( new MouseAdapter()  {
			public void mouseClicked( MouseEvent e ) {
				int count = empresasTable.getSelectedRowCount();
				if ( count > 0 ) {
                    inputTabEmpresasPanelRefresh();					
				}
			}
		});
		
		
		JScrollPane scrollpane = new JScrollPane( empresasTable );
		
		JPanel empresasDisplayPanel = new JPanel( new BorderLayout() ) ;		
		empresasDisplayPanel.add( scrollpane );	
		
		
		JButton incluirButton = new JButton("Incluir empresa");	
		incluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				System.out.println( " inclusão de empresas.. " );
				addEmpresaTableModel();	
				empresasTable.revalidate();
				empresasTable.repaint();
				repaint();	
				System.out.println( " .. finalizou inclusão");	
			}
		});
		incluirButton.setToolTipText("inclui empresa na ordem de serviço");
		
		
		JButton removerButton = new JButton("Excluir empresa" );
		removerButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				deleteEmpresaTableModel();
				empresasTable.revalidate();
				repaint();
			}
		});
		removerButton.setToolTipText("exclui empresa da ordem de serviço");
		
		JButton salvarButton  = new JButton("Gravar empresa" );
		salvarButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				updateEmpresaTableModel();
				empresasTable.revalidate();
				empresasTable.repaint();
				repaint();
			}
		});
		salvarButton.setToolTipText("atualiza campos ref. periodo de" +
		 " fiscalização na ordem de serviço");
			
		JPanel controlEmpresasPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT) );		
		controlEmpresasPanel.add( incluirButton );				
		controlEmpresasPanel.add( removerButton );
		controlEmpresasPanel.add( salvarButton );
		
		empresasPanel.add( inputEmpresasPanel, BorderLayout.NORTH );
		empresasPanel.add( empresasDisplayPanel, BorderLayout.CENTER );
		empresasPanel.add( controlEmpresasPanel, BorderLayout.SOUTH );
		
		return empresasPanel;			
		
	}
	
	private JPanel createFiscaisPanel() {
		
		JPanel fiscaisPanel = new JPanel( new BorderLayout());
		Border etched = BorderFactory.createEtchedBorder();				
		JPanel inputFiscaisPanel = new JPanel( new GridLayout( 1, 2, 5, 5 ) );
		inputFiscaisPanel.setBorder( etched );
		JLabel fiscalLabel = new JLabel( "Fiscal");
		// fill combo in
		fiscalCombo = new JComboBox( fiscaisNames );	
		
		inputFiscaisPanel.add( fiscalLabel );
		inputFiscaisPanel.add( fiscalCombo );
		
		fiscaisModel = new TabFiscaisModel();		
		fiscaisTable = new JTable(); 
		fiscaisTable.setModel( fiscaisModel );
		JScrollPane scrollpane = new JScrollPane( fiscaisTable );
		
		JPanel fiscaisDisplayPanel = new JPanel( new BorderLayout() ) ;		
		fiscaisDisplayPanel.add( scrollpane );	
		
		JButton incluirButton = new JButton("Incluir fiscal");
		incluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				addFiscalTableModel();	
				fiscaisTable.revalidate();
				fiscaisTable.repaint();
				repaint();
			}
		});
		incluirButton.setToolTipText("incluir fiscal na ordem de serviço" );
				
		JButton removerButton = new JButton( "Excluir fiscal" );
		removerButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				deleteFiscalTableModel();
				fiscaisTable.revalidate();
				repaint(); 
			}
		});
		removerButton.setToolTipText("excluir fiscal na ordem de serviço" );
		
		JPanel controlFiscaisPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT) );		
		controlFiscaisPanel.add( incluirButton );	
		controlFiscaisPanel.add( removerButton );
		
		fiscaisPanel.add( inputFiscaisPanel, BorderLayout.NORTH );
		fiscaisPanel.add( fiscaisDisplayPanel, BorderLayout.CENTER );
		fiscaisPanel.add( controlFiscaisPanel, BorderLayout.SOUTH );
		
		return fiscaisPanel;			
		
	}
	
	private JPanel createOSPanel() {		
		 
		osModel = new TabOSModel();		
		osTable = new JTable(); 
		osTable.setModel( osModel );
		osTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		osTable.addMouseListener( new MouseAdapter()  {
			public void mouseClicked( MouseEvent e ) {
				int count = osTable.getSelectedRowCount();
				if ( count > 0 ) {
					
					// diplays content into the textarea for dysplaing company
					// associated with a specified service order
					loadEmpresasTable();
					empresasTable.revalidate();
					empresasTable.repaint();
					
					loadFiscaisTable();
					fiscaisTable.revalidate();
					fiscaisTable.repaint();
					inputTabOSPanelRefresh();
					 
				}
			}	
		});
		
		
		JScrollPane scrollpane = new JScrollPane( osTable );
		
		JPanel osDisplayPanel = new JPanel( new BorderLayout() ) ;
		
		osDisplayPanel.add( scrollpane );
		
		return osDisplayPanel;
		
	}
	
	

	private JPanel createControlOSPanel() {	
		
		JButton incluirButton = new JButton("Incluir OS");				
		incluirButton.addActionListener( new ActionListener() {
		 public void actionPerformed( ActionEvent evt ) {
			addOSTableModel();	
			osTable.revalidate();
			osTable.repaint();
			repaint();					
			addRecTabOS();
		 }
		});
		incluirButton.setToolTipText("incluir ordem de serviço");
		
		JButton excluirButton = new JButton("Excluir OS");
		
		excluirButton.setToolTipText("excluir ordem de serviço");
		excluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt )  {
				deleteOSTableModel();
				deleteRecTabOS();
			}
			
		});
		
		
		JButton gravarButton = new JButton("Gravar OS");
		
		gravarButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				updateRecTabOS();
				osTable.revalidate();
				osTable.repaint();
				repaint();
			}
		});	
		
		gravarButton.setToolTipText("atualizar dados da ordem de serviço");
		JButton searchButton = new JButton("Filtrar OS");
		searchButton.setToolTipText("exibe ordens de serviços específicas");
		
		// JButton sairButton = new JButton("Sair");
		
		incluirButton.setMnemonic('I');
		excluirButton.setMnemonic('E');
		gravarButton.setMnemonic('G');
//		searchButton.setMnemonic('F');
		
		JPanel controlPanel = new JPanel( new FlowLayout() );
		
		controlPanel.add( incluirButton );
		controlPanel.add( excluirButton );
		controlPanel.add( gravarButton );
//		controlPanel.add( searchButton );
		
		return controlPanel;
	}
	
	
	/**
	 * Adds rows of empresas and fiscal grids in the tabOS and tabOSFiscal tables,
	 * before checking for referential integraty of database
	 **/
	private void addRecTabOS() {
		
		try {
		
			String codOS = null;
			String codProjOS = null ;
			String numProcOS = null;
			String cmcEmpresaOS = null;
			String inicioFisc = null;
			String finalFisc = null;
			String matrFiscal = null;
		
			boolean isValidTransaction = true;
		
        	String[] empresa = new String[ empresasColumnNames.length ];		
        	String[] fiscal  = new String[ fiscaisColumnNames.length ];
        
			Statement stmt = myConnection.createStatement();
			
		
			codOS = codigoOSTxt.getText();
			codProjOS = codigoProjetoTxt.getText();
			numProcOS = numeroProcessoTxt.getText();
		
			// Starting adding of companies
			// Getting the companies list of the grid and 
			// includes it on the tabos table
			for ( int i = 0; i < empresas.size(); i++ ) {
				
				empresa = (String[]) empresas.get( i );
				cmcEmpresaOS  = empresa[ 0 ];
			
					
				// Test if the transaction ensures the integrity 
				// of the database,  codOS + codProjOS + cmcEmpresaOS 
				// must be unique

				String query = "SELECT * FROM tabos WHERE " +
			         "codigoOS='" + codOS + "'  AND " +
			         "codigoProjetoOS='" + codProjOS + "' AND " +
			         "numeroProcessoOS='" + numProcOS + "' AND " + 
			         "cmcEmpresaOS='" + cmcEmpresaOS + "'" ;
				ResultSet rs = stmt.executeQuery( query );
				
				while ( rs.next() ) {
					isValidTransaction = false ;
					break;
				}
				
				// It is necessary provides records of office revenue
				if ( fiscais.size() == 0 ) {
					isValidTransaction = false;
				}
				
			}
				
				
 			//::TO CHECK OUT:: 
 			// Realizes batch processing of following transaction and this way
 			// to ensure the atomicity of one , and therefore
 			// the consistence of the database
 			
			// Relational integraty is it garanteed?
			if ( isValidTransaction ) {
				
				// Starting inclusion of audit´s paper to the companies 
				for ( int j = 0 ; j < empresas.size(); j++ ) {
					
					empresa = (String[]) empresas.get( j );
					
					cmcEmpresaOS = empresa[ 0 ];
					inicioFisc = SEPConverter.converteFrmtDataToMySQL( empresa[ 2 ] );
					finalFisc = SEPConverter.converteFrmtDataToMySQL( empresa[ 3 ] );
					codProjOS = empresa[ 4 ];
					numProcOS = empresa[ 5 ];
					
					
					String cmd = "INSERT INTO tabOS VALUES  ('" 
			              + codOS + "','"
			              + codProjOS + "','" 
			              + numProcOS + "','"
			              + cmcEmpresaOS + "','" 
			              + inicioFisc + "','"
			              + finalFisc +  "')"; 
					myStatement.executeUpdate( cmd );
					myConnection.commit();													
				}

				

				// Starting inclusion of the revenue officer allocated
				// for the auditing of that choice companies
				
				String ignore = "IGNORE";
				for ( int j = 0 ; j < fiscais.size(); j++ ) {

					fiscal = (String[]) fiscais.get( j );
					matrFiscal = fiscal[ 0 ];
					
					String cmd = "INSERT INTO tabOSFiscal VALUES  ('" 
			              + codOS + "','"
// BY Franciso Emidio , Oct 26, 2003			              
// --- Agora, o numero do processo e o codigo do projeto, estao amarrados
// -- a empresa. Entretanto, NAO vou mexer na estrutura da tabela
// -- porque estao sendo gerados relatorios.
// -- PENDENCIAS: Futuras versoes poderao reestruturar esta tabela			              
//					+ codProjOS + "','"    
//					+ numProcOS + "','"
			              + ignore + "','" 
			              + ignore + "','"
			              + matrFiscal +  "')"; 
					myStatement.executeUpdate( cmd );
					myConnection.commit();								
					
				} // < --- Finalizar transação em lote ::PENDENCIA
				
			}
		}
		catch ( SQLException sqlex ) {
			sqlex.printStackTrace();
		}
		
			

	}
	
	/**
	 * Updates contents of companies and auditors in the tabOS and tabOSFiscal
	 * tables before checking for referential integraty of databae
	 **/
	private void updateRecTabOS() {
		
		try {
			
			
			String codOS = null;
			String codProjOS = null ;
			String numProcOS = null;
			String cmcEmpresaOS = null;
			String inicioFisc = null;
			String finalFisc = null;
			String matrFiscal = null;
		
        	String[] empresa = new String[ empresasColumnNames.length ];		
        	String[] fiscal  = new String[ fiscaisColumnNames.length ];			
			
			// 1. Check if service order is in the database?
		
			boolean isValidTransaction = false;
	
			Statement stmt = myConnection.createStatement();
			
		
			codOS = codigoOSTxt.getText();
		
			// It is only possible update a record when that one was stored
			// in the database previously.
			String query = "SELECT * FROM tabos WHERE " +
	        	 "codigoOS='" + codOS + "' " ; //  AND " +
//	         	"codigoProjetoOS='" + codProjOS + "' AND " +
//        	"numeroProcessoOS='" + numProcOS + "'" ;
			ResultSet rs = stmt.executeQuery( query );
			while ( rs.next() ) {
				isValidTransaction = true ;
				break;
			}
		
			// 2. Starting, updates the records..
			if ( isValidTransaction ) {
			
				// 2.1 Firstly, removes the corresponding service order 
				// and its respectives records
				
				String cmd = null;
			
			
				cmd = "DELETE  FROM tabos WHERE " +
	         	"codigoOS='" + codOS + "'" ; // +  " AND " +
//	         	"codigoProjetoOS='" + codProjOS + "'" + " AND " +
//	         	"numeroProcessoOS='" + numProcOS + "'" ;
            	stmt.executeUpdate( cmd );
 	    		myConnection.commit();		
 	    		
 	    	
 	    		cmd = "DELETE  FROM tabosfiscal WHERE " +
	         	"codigoOSFiscal='" + codOS + "'" ; //  + " AND " +
//	         	"codProjOSFiscal='" + codProjOS + "'" + " AND " +
//	         	"numProcOSFiscal='" + numProcOS + "'" ;
            	stmt.executeUpdate( cmd );
 	    		myConnection.commit();
 	    		
				// 2.2. Includes,the records again updated.
			
			
				// Starting inclusion of audit´s paper to the companies 
				for ( int i = 0 ; i < empresas.size(); i++ ) {
					
					empresa = (String[]) empresas.get( i );					
					
					cmcEmpresaOS = empresa[ 0 ];
	  				
	 			    inicioFisc = SEPConverter.converteFrmtDataToMySQL( empresa[ 2 ]  );
	 			    finalFisc = SEPConverter.converteFrmtDataToMySQL( empresa[ 3 ] );  			    
					codProjOS = empresa[ 4 ];
					numProcOS = empresa[ 5 ];
					
					cmd = "INSERT INTO tabOS VALUES  ('" 
			              + codOS + "','"
			              + codProjOS + "','" 
			              + numProcOS + "','"
			              + cmcEmpresaOS + "','" 
			              + inicioFisc + "','"
			              + finalFisc +  "')"; 
					myStatement.executeUpdate( cmd );
					myConnection.commit();													
				}
				
				// Starting inclusion of the revenue officer allocated
				// for the auditing of that choice companies
				for ( int j = 0 ; j < fiscais.size(); j++ ) {

					fiscal = (String[]) fiscais.get( j );
					matrFiscal = fiscal[ 0 ];

					String ignore = "IGNORE";
					
					cmd = "INSERT INTO tabOSFiscal VALUES  ('" 
			              + codOS + "','"
//					+ codProjOS + "','" 
//					+ numProcOS + "','" 
//					BY Franciso Emidio , Oct 26, 2003			              
//					--- At the moment, the process number and the project code, are
//                 -- binded to the company. However, I wont touch in the layout of
//                  correspoding table because it is being used for making reports.
//					-- PENDENCIAS: In the next future, releases refactor that table		              

			              + ignore + "','" 
			              + ignore + "','"
			              + matrFiscal +  "')"; 
					myStatement.executeUpdate( cmd );
					myConnection.commit();								
					
				} // < --- Finalizar transação em lote ::PENDENCIA
				
			}
			else {
				String err = "Tentativa de atualizar registro inexistente.";
		 			JOptionPane.showMessageDialog( null, err,
			    		  "Exclusão inválida", 
			    		     JOptionPane.ERROR_MESSAGE   );
			}
		} catch ( SQLException sqlex ) {
			sqlex.printStackTrace();
		}
		
	}
	
	/**
	 * Deletes rows of empresas and fiscal grids in the tabOS and tabOSFiscal tables,
	 * before checking for referential integraty of database
	 **/
	 private void deleteRecTabOS() {
	 	
	 	try {
	 		
			String codOS = null;
//			String codProjOS = null ;
//			String numProcOS = null;
		
			boolean isValidTransaction = false;
		
			Statement stmt = myConnection.createStatement();
			
		
			codOS = codigoOSTxt.getText();
//			codProjOS = codigoProjetoTxt.getText();
//			numProcOS = numeroProcessoTxt.getText();
		
			String query = "SELECT * FROM tabos WHERE " +
	        	 "codigoOS='" + codOS + "'";
	        	  
//	        	 + "'  AND " +  	"codigoProjetoOS='" + codProjOS + "' AND " +
//	         	"numeroProcessoOS='" + numProcOS + "'" ;
			ResultSet rs = stmt.executeQuery( query );
			while ( rs.next() ) {
				isValidTransaction = true ;
				break;
			}
			
			//::PENDENCIA:-> Efetuar transacao em lote
		
			if ( isValidTransaction ) {
			
				String cmd = null ;
			
				cmd = "DELETE  FROM tabos WHERE " +
	         	"codigoOS='" + codOS + "'" ; //+  " AND " +
//	         	"codigoProjetoOS='" + codProjOS + "'" + " AND " +
//	         	"numeroProcessoOS='" + numProcOS + "'" ;
            	stmt.executeUpdate( cmd );
 	    		myConnection.commit();		
 	    		
 	    	
 	    		cmd = "DELETE  FROM tabosfiscal WHERE " +
	         	"codigoOSFiscal='" + codOS + "'"; // + " AND " +
//	         	"codProjOSFiscal='" + codProjOS + "'" + " AND " +
//	         	"numProcOSFiscal='" + numProcOS + "'" ;
            	stmt.executeUpdate( cmd );
 	    		myConnection.commit();
			  
			}
			else {
				String err = "Tentativa de excluir registro inexistente.";
		 			JOptionPane.showMessageDialog( null, err,
			    		  "Exclusão inválida", 
			    		     JOptionPane.ERROR_MESSAGE   );
			}
			
		} catch ( SQLException sqlex ) {
			sqlex.printStackTrace();
		}


	 	
	 }
		
	

    /**
     * Adds the selected item of service order grid
     */
     private void addOSTableModel() {		
     
	    String numOS = codigoOSTxt.getText();
//	    String numProj = codigoProjetoTxt.getText();
//		String numProc = numeroProcessoTxt.getText();	

     	
        String[] recOS = new String[ osColumnNames.length ];		
        
		recOS[ 0 ] = numOS;
//		recOS[ 1 ] = numProj;
//		recOS[ 2 ] = numProc;
		
		String searchKey = numOS;		
//		String searchKey = numOS + numProj + numProc;		
		boolean hasRecord =
		    osTracker.containsKey( searchKey  );
		    
		
		if ( hasRecord ) {
			String err = "Tentativa de inserir registro duplicado";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			// updates the source of the FiscalTableModel
			os.add( recOS );
			// updates tracker of dupplicated records
			 osTracker.put( searchKey, "N/A" ) ;
			// forces refreshing of the table
			osBufferRefresh(); 
			osTable.setModel( osModel);			
		}	
		
	}
	
	
	
	
	/**
	 * Deletes the selected item of service order grid
	 */
	private void deleteOSTableModel() {
		
		int selectedRow = osTable.getSelectedRow();
		
		// -- primary key into to choicen record 
		String numOS = (String) osTable.getValueAt(selectedRow, 0 );
//		String numProj = (String) osTable.getValueAt( selectedRow, 1 );
//		String numProc = (String) osTable.getValueAt( selectedRow, 2 );

//		String searchKey = numOS + numProj + numProc;
		String searchKey = numOS ;
		
		
		//::PENDENCIA:->Verificar se a exclusão esta sendo 
		//  atualizada corretamente		
		
		Integer rowID = (Integer) osTracker.get( searchKey );
		
	
		// -- position in the buffer of choicen record
		int index = rowID.intValue(); 
		
                   
		os.remove( index );
		osBufferRefresh();		
		osTable.setModel( osModel );
		osTable.revalidate();
		osTable.repaint();
		revalidate();
		repaint();		
		
	}
	
	
	
	
	/**
	 *  Adds a company to TableModel of EmpresasTable
	 *
	 */
	private void addEmpresaTableModel() {
		
		
        String[] empresa = new String[ empresasColumnNames.length ];
		String cmc = SEPConverter.removeMaskCMC(cmcTextField.getText());
//		String cmc = cmcTextField.getText();
		String inicioPeriodo = inicioPeriodoTxt.getText();
		String finalPeriodo = finalPeriodoTxt.getText();
		String numProj = codigoProjetoTxt.getText();
		String numProc = numeroProcessoTxt.getText();	
		
		boolean hasCompanyRecord = false;		
		String razaoSocial = null;
		
		empresa[ 0 ] = cmc;
		empresa[ 2 ] = inicioPeriodo;
		empresa[ 3 ] = finalPeriodo;
		empresa[ 4 ] = numProj;
		empresa[ 5 ] = numProc;				
		
		// Check if the company already is in the database, getting following its name
		try {
			String query = "SELECT nomeEmpresa FROM tabEmpresa " + 
		       " WHERE cmcEmpresa='" + cmc + "'" ;
			ResultSet rs = myStatement.executeQuery( query );
				
			while ( rs.next() ) {
				hasCompanyRecord = true ;
				razaoSocial = rs.getString( 1 );
				empresa[ 1 ] = razaoSocial;
				break;
			}
		}
		catch (	SQLException ignore ) {
			ignore.printStackTrace();
		}
		
		
		if ( hasCompanyRecord ) {		
		
			try {
						
				// Check for repeatded rows
				boolean hasRecord;
				if ( cmcEmpresasTracker == null ) {
					hasRecord = false;
				}   
				else {
					hasRecord =	 cmcEmpresasTracker.containsKey( new Integer( cmc) );
				}           
			
				if ( hasRecord ) {
					String err = "Tentativa de incluir registro já existente";
		 				JOptionPane.showMessageDialog( null, err,
			    		  "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
				}
				else {
			    
			    	// updates the source of the EmpresaTableModel
					empresas.add( empresa );
				
					// updates the tracker of dupplicated records
					cmcEmpresasTracker.put( new Integer( cmc ), "N/A" );
			                         
					cmcTextField.setText("");
					inicioPeriodoTxt.setText("");
					finalPeriodoTxt.setText("");
					codigoProjetoTxt.setText("");
					numeroProcessoTxt.setText("");		
			
			    	// forces refreshing of the table
			    	empresasBufferRefresh(); 
					empresasTable.setModel( empresasModel );

				
				}
			
			}
			catch (	NumberFormatException nfe ) {
					String err = "Campo de registro inválido";
		 				JOptionPane.showMessageDialog( null, err,
			    			  "Inclusão inválida", 
			    			     JOptionPane.ERROR_MESSAGE   );
			}		
		}
		else {
				String err = "Empresa não cadastrada";
		 			JOptionPane.showMessageDialog( null, err,
			    		  "Inclusão inválida", 
			    		     JOptionPane.ERROR_MESSAGE   );
		}
			
			
	}
	
	/**
	 *  Removes a specific company supplied by end-user
	 *
	 */
	private void deleteEmpresaTableModel() {
        String[] empresa = new String[ empresasColumnNames.length ];
		String cmc = SEPConverter.removeMaskCMC(cmcTextField.getText());
//		String cmc = cmcTextField.getText();
//		String inicioPeriodo = inicioPeriodoTxt.getText();
//		String finalPeriodo = finalPeriodoTxt.getText();

		try {
			boolean hasRecord =
				 cmcEmpresasTracker.containsKey( new Integer( cmc) );
			
			if ( !hasRecord ) {
				String err = "Tentativa de excluir registro inexistente";
		 			JOptionPane.showMessageDialog( null, err,
			    		  "Excluão inválida", JOptionPane.ERROR_MESSAGE   );
			}
			else {
				// Find the matching record in the buffer for company
				Integer rowID = (Integer) cmcEmpresasTracker.get( new Integer( cmc ) );
				int index = rowID.intValue();
				// Removes the selected record of source of empresasModel model
				empresas.remove( index );
				empresasBufferRefresh(); 
				empresasTable.setModel( empresasModel);
			}
		}
		catch (	NumberFormatException nfe ) {
				String err = "Campo de registro inválido";
		 			JOptionPane.showMessageDialog( null, err,
			    		  "Exclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}

	}
	
	/**
	 * Updates the grid of company panel for displaying the newest information
	 * updated
	 */
	private void updateEmpresaTableModel() {
        String[] empresa = new String[ empresasColumnNames.length ];		
//		String cmc = cmcTextField.getText();
		String cmc = SEPConverter.removeMaskCMC(cmcTextField.getText());
		String inicioPeriodo = inicioPeriodoTxt.getText();
		String finalPeriodo = finalPeriodoTxt.getText();
		String numProj = codigoProjetoTxt.getText();
		String numProc = numeroProcessoTxt.getText();	

	
		try {
			boolean hasRecord =
				 cmcEmpresasTracker.containsKey( new Integer( cmc) );
			
			if ( !hasRecord ) {
				String err = "Tentativa de atualizar registro inexistente";
		 			JOptionPane.showMessageDialog( null, err,
			    		  "Atualização inválida",
			    		     JOptionPane.ERROR_MESSAGE   ); 
			}
			else {			
			 
				empresa[ 0 ] = cmc;
				
				String query = "SELECT nomeEmpresa FROM tabEmpresa " + 
	       		" WHERE cmcEmpresa='" + cmc + "'" ;
				ResultSet rs = myStatement.executeQuery( query );
				
				while ( rs.next() ) {
					String razaoSocial = rs.getString( 1 );
					empresa[ 1 ] = razaoSocial;
					break;
				}
				
				empresa[ 2 ] = inicioPeriodo;
				empresa[ 3 ] = finalPeriodo;
				empresa[ 4 ] = numProj;
				empresa[ 5 ] = numProc;					
			
				Integer rowID = (Integer) cmcEmpresasTracker.get( 
				     							new Integer( cmc ) );
				// -- position in the buffer of choicen record
				int index = rowID.intValue();
				// -- remove first and insert again. Doing this, is not 
				// -- necessary to worry with refreshing cacher buffer
				empresas.remove( index );
				empresas.add( index, empresa );
				
				cmcEmpresasTracker.remove( cmc ); 
				cmcEmpresasTracker.put( new Integer( cmc ), rowID ); 
						
				empresasBufferRefresh(); 
				empresasTable.setModel( empresasModel);
			}	

		}
		catch (	NumberFormatException nfe ) {
				String err = "Campo de registro inválido";
		 			JOptionPane.showMessageDialog( null, err,
			    		  "Atualização inválida", 
			    		     JOptionPane.ERROR_MESSAGE   );
		}		
		catch ( SQLException ignore ) {
			ignore.printStackTrace();
		}

		
	}
	

    /**
     * Adds the selected item of Fiscais combo into the TableModel fiscaisModel
     */
     private void addFiscalTableModel() {		
     
     	String ignore = "blah" ; 
     	
		String nomeFiscal = (String) fiscalCombo.getSelectedItem();
		
		String matrFiscal = (String) codeFiscalTracker.get( nomeFiscal );		
	
		
        String[] fiscal = new String[ fiscaisColumnNames.length ];		
        
		fiscal[ 0 ] = matrFiscal;
		fiscal[ 1 ] = nomeFiscal;
		
		String searchKey = matrFiscal + nomeFiscal;		
		boolean hasRecord =
		    nameCodeFiscalTracker.containsKey( searchKey  );
		    
		
		if ( hasRecord ) {
			String err = "Tentativa de inserir registro duplicado";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			// updates the source of the FiscalTableModel
			fiscais.add( fiscal );
			// updates tracker of dupplicated records
			 nameCodeFiscalTracker.put( searchKey, "N/A" ) ;
			// forces refreshing of the table
			fiscaisBufferRefresh(); 
			fiscaisTable.setModel( fiscaisModel);			
		}	
		
	}
	
	
    /**
     * Deletes the selected item of Fiscais combo into the TableModel fiscaisModel
     */
	private void deleteFiscalTableModel()  {
		
		// Identifies the selected row 
		int index = fiscaisTable.getSelectedRow();
		
		// Search previous on the vector
		String[] fiscal = (String[]) fiscais.get( index );
		
		String searchKey = fiscal[ 0 ] + fiscal[ 1 ] ;
		
		boolean hasRecord =
		    nameCodeFiscalTracker.containsKey( searchKey  );
		    

		// Deletes record 
		if ( hasRecord ) {
			// deletes the selected record
			fiscais.remove( index );
			// updates tracker of dupplicated records
			 nameCodeFiscalTracker.remove( searchKey ) ;
			// forces refreshing of the table
			fiscaisBufferRefresh(); 
			fiscaisTable.setModel( fiscaisModel);
		}		     
		
	}
	
	
	/**
	 * This function is used for helping the manegement of dupplicated rows
	 * in the grid of companies
	 */
	private void osBufferRefresh() {
		String[] recOS;
		String numOS;
		String numProj;
		String numProc;
		osTracker.clear();
		for ( int i = 0; i < os.size(); i++ ) {
			recOS = (String[])  os.get( i );
			numOS = recOS[ 0 ];
//			numProj = recOS[ 1 ];
//			numProc = recOS[ 2 ];		
			String key = numOS;
//			String key = numOS + numProj + numProc;
			osTracker.put( key , new Integer( i ) ); 
		}
	}
	
	
	/**
	 * This function is used for helping the manegement of dupplicated rows
	 * in the grid of companies
	 */
	private void empresasBufferRefresh() {
		String[] empresa;
		String cmc;
		String inicioPeriodo;
		String finalPeriodo;
		cmcEmpresasTracker.clear();
		for ( int i = 0; i < empresas.size(); i++ ) {
			empresa = (String[])  empresas.get( i );
			cmc = empresa[ 0 ];
			//inicioPeriodo = empresa[ 1 ];//
			//finalPeriodo = empresa[ 2 ];	//	
			cmcEmpresasTracker.put( new Integer ( cmc ) , new Integer( i ) );		
		}
	}
	
	/**
	 * This function is used for helping the management of dupplicated rows
	 * in the grid of revenue officer
	 */
	 private void fiscaisBufferRefresh() {
	 	String[] fiscal;
	 	String matr;
	 	String name;
	 	nameCodeFiscalTracker.clear();
	 	for ( int i = 0 ; i < fiscais.size(); i++ ) {
	 		 fiscal = (String[]) fiscais.get( i );
	 		 matr = fiscal[ 0 ];
	 		 name = fiscal[ 1 ];
	 		 String key = matr + name;
	 		 nameCodeFiscalTracker.put( key, matr );
	 	}
	 }
	  
	 
	/**
	 * Loads a combo component with the field fiscalName of the  table tabfiscais
	 * Also, traces filling of a tracker throught
	 * of the names and code of a specified revenue officer
	 * 
	 * @param <code>con</code> representing the active connection
	 * @exception Thrown if ocurrs some exception for the access of table tabrotinas
	 *
	 */	
	private void loadFiscaisCombo( Connection con ) {
		int cols = fiscaisColumnNames.length;
		String dummy = "trash" ; 
		
		// helpes into check for dupplicates records
	    nameCodeFiscalTracker = new HashMap();	

 		// helpes find the name of revenue office known its code
	    codeFiscalTracker = new HashMap();
	    
	    // data source into the table model
		fiscaisNames = new Vector();
		String fiscalMatr;
		String fiscalName;
		try {
			
			Connection theConn = con;
			Statement stm = theConn.createStatement();
			Statement stmt = theConn.createStatement();
			String query = "SELECT codigoFiscal, nomeFiscal " +
		               " FROM tabFiscal ORDER BY codigoFiscal" ;
			ResultSet rs = stmt.executeQuery( query ); 		
		
			/**
		 	* Assembles the fiscalCombo component and the fiscal hash with
		 	* its matching fiscal records of the table tabfiscal
		 	*/
		 	while ( rs.next() ) {
		 		String[] theRow = new String[ cols ] ;
		 		fiscalMatr = (String)rs.getString( 1 );
		 		fiscalName = (String)rs.getString( 2 );
		 		
		 		String key = fiscalMatr + fiscalName ;
		 		
		 		fiscaisNames.add( fiscalName );
		 		
		 		//  helpes find the code of the revenue office knows its name
		 		codeFiscalTracker.put( fiscalName, fiscalMatr );	 		
		 		
		 	}		 
		}
		catch ( SQLException sqlException ) {
			System.out.println( sqlException.getMessage() );
			sqlException.printStackTrace();
		}
	}	

	
	private void loadEmpresasTable() {

		// reset the tracker		
		cmcEmpresasTracker = new HashMap();
		
		// Check if an specified service order was selected. If so, getting
		// all the records associated with that one, containing information
		// about the Insc.Municipal, Perido Inicial, Periodo Final and 
		// Razao social of every company
		if ( osTable.getSelectedRowCount() > 0  ) {
			// Look for the most recently selected row in the grid of TabOSPanel panel		
    		String choicenOS;
    		//String choicenProj;
    		//String choicenProc;
    		
			int selectedRow = osTable.getSelectedRow();			
			choicenOS = (String) osTable.getValueAt( selectedRow, 0 );
//			choicenProj = (String) osTable.getValueAt( selectedRow, 1 );
//			choicenProc = (String) osTable.getValueAt( selectedRow, 2 );

			try {
				
				Statement stmt = myConnection.createStatement();
				String query = "SELECT  tabOS.cmcEmpresaOS, tabEmpresa.nomeEmpresa, " +
			        " tabOS.inicioFiscOS, tabOS.fimFiscOS, " + 
			        " tabOS.codigoProjetoOS, tabOS.numeroProcessoOS " +
			        " FROM tabOS, tabEmpresa " +
			           " WHERE tabOS.codigoOS = '" + choicenOS + "'" +
//			           " AND tabOS.codigoProjetoOS = '" + choicenProj + "'" +
//			           " AND tabOS.numeroProcessoOS = '" + choicenProc + "'" +
			           " AND tabOS.cmcEmpresaOS = tabEmpresa.cmcEmpresa" +
			           " ORDER BY tabOS.cmcEmpresaOS ";

				ResultSet rs = stmt.executeQuery( query );
				
				empresas.clear();
				
				while ( rs.next() ) {
					String[] theRow = new String[ empresasColumnNames.length ];
		 			String cmc  = (String)rs.getString( 1 );
		 			String razaoSocial = (String)rs.getString( 2 );
		 			String periodoInic = (String)rs.getString( 3 );
		 			String periodoFinal = (String)rs.getString( 4 );
		 			String codigoProjeto = (String)rs.getString( 5 );
		 			String numProcesso = (String)rs.getString( 6 );
		 			
		 			theRow[ 0 ] = cmc;
		 			theRow[ 1 ] = razaoSocial;
		 			
		 			/**
		 			 * Convertes the current data from format yyyy-mm-dd
		 			 * stored in database mySQL for the format dd/mm/yyyy		 			 *
		 			 */ 
		 			 
		 			theRow[ 2 ] = SEPConverter.converteFrmtDataFromMySQL( periodoInic );
		 			theRow[ 3 ] = SEPConverter.converteFrmtDataFromMySQL( periodoFinal );
		 			theRow[ 4 ] = codigoProjeto;
		 			theRow[ 5 ] = numProcesso; 
		 			
					empresas.add( theRow );		
					
				}
				
				// Updates the model
				empresasTable.setModel( empresasModel );
				empresasBufferRefresh();
			
			}
			catch( SQLException ignore ) {
				ignore.printStackTrace();
			}	
		
		}
	}
	
	/**
	 * Updates the tabFiscaisPanel panel for reflecting the selected row
	 * in the tabOSPanel panel. Initially, the tabFiscaisPanel is displayed
	 * empty because is not selected row in the tabFiscaisPanel anymore.
	 *
	 */
	private void loadFiscaisTable() {
		
		String[] row = new String[ fiscaisColumnNames.length ];
		
		if ( osTable.getSelectedRowCount() > 0 ) {
			
			// Cleanup the source of data into the TableModel
			fiscais.clear();
			// Cleanup the tracker of dupplicated records
			nameCodeFiscalTracker.clear();			
			
			
			// Look for the most recently selected row in the 
			// grid of TabOSPanel panel		
			String[] theRow = new String[ empresasColumnNames.length ];
			int selectedRow = osTable.getSelectedRow();
			String codOS = (String) osTable.getValueAt(selectedRow, 0 );
//			String codProj = (String) osTable.getValueAt( selectedRow, 1 );			
//			String codProc = (String) osTable.getValueAt( selectedRow, 2 );			
			
			 
			try { 
			
				// Search for all the records attendes to search criteria
				Statement stmt = myConnection.createStatement();

				String query =
				   " SELECT codigoFiscal, nomeFiscal "
				   + " FROM tabfiscal, tabosfiscal "
				   + " WHERE codigoosfiscal = '" + codOS + "'" 
//				   +  " AND codprojosfiscal= '" + codProj + "'" 
//				   +   " AND numprocosfiscal= '" + codProc + "'" 
				   +      " AND matrosfiscal = codigofiscal" ;				    



				ResultSet rs = stmt.executeQuery( query );
			
				while ( rs.next() ) {
			    	String[] rowFiscal = new String[ fiscaisColumnNames.length ];
					for ( int j = 0; j < rowFiscal.length; j++ )  {
						rowFiscal[ j ] = (String)rs.getString( j + 1 );
					}		
					
					// updates the source of the FiscalTableModel
					fiscais.add( rowFiscal );
					// updates tracker of dupplicated records					
					String searchKey = rowFiscal[ 0 ] + rowFiscal[ 1 ] ;
					
					
			 		nameCodeFiscalTracker.put( searchKey, "N/A" ) ;
			 		
					// forces refreshing of the table
					fiscaisTable.setModel( fiscaisModel);			
					fiscaisBufferRefresh();
					
					// cleanup textfieds
					cmcTextField.setText( "" );
					inicioPeriodoTxt.setText("" );
					finalPeriodoTxt.setText("");
					
				}

			} catch ( SQLException ignore ) {
				System.out.println( ignore.getMessage() );
				ignore.printStackTrace();
			}	
			
		}	// end of if
			
	}
	
	
	
	private void loadOSTable( ResultSet rs ) {	
	
		try {
			//rows = new HashMap();
			// helpes into check for dupplicates records
	    	osTracker = new HashMap();				
			os = new Vector();
			empresas = new Vector();
			fiscais = new Vector();
			
			String strCodOS = null;
			String strCodProj = null;
			String strCodProc = null;
			
			String key = null;
			int cols = osColumnNames.length;
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
		 	    strCodOS =  rs.getString( 1 );
//		 	    strCodProj = rs.getString( 2 );
//		 	    strCodProc = rs.getString( 3 );

				key = strCodOS ;
//		 	    key = strCodOS + strCodProj + strCodProc ;
			 	osTracker.put( new String(key) , new Integer(  bufferRecordPos ) );	 	
			 	os.add( theRow );
			 	bufferRecordPos++;	 	
			 }			
			 
		}
		catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		}
	}
	
	
	/**
	 *  It will update textfield when a record is selected inthe TabEmpresasPanel 
	 */
    private void inputTabEmpresasPanelRefresh() {   	
    	String value;
    	
		int selectedRow = empresasTable.getSelectedRow();
		value = (String) empresasTable.getValueAt(selectedRow, 0 );
//		cmcTextField.setText( value );
		cmcTextField.setText( SEPConverter.insertMaskCMC(value) );
		
		value = (String) empresasTable.getValueAt(selectedRow, 2 );
		inicioPeriodoTxt.setText( value );
		value = (String) empresasTable.getValueAt(selectedRow, 3 );
		finalPeriodoTxt.setText( value );
		
		revalidate();		
    }						
    
	/**
	 *  It will update textfield when a record is selected inthe TabEmpresasPanel 
	 */
    private void inputTabOSPanelRefresh() {   	
    	String value;
    	
		int selectedRow = osTable.getSelectedRow();
		value = (String) osTable.getValueAt(selectedRow, 0 );
		codigoOSTxt.setText( value );
//?		value = (String) osTable.getValueAt(selectedRow, 1 );
//?		codigoProjetoTxt.setText( value );
//?		value = (String) osTable.getValueAt(selectedRow, 2 );
//?		numeroProcessoTxt.setText( value );
		
		revalidate();		
    }						
    
	 
	
	private class TabEmpresasModel extends AbstractTableModel {
		
		public int getRowCount() {
			
			if ( empresas == null ) return 0;
			
			return empresas.size();
		}
		
		public Object getValueAt( int r , int c ) {
			
			if ( empresas == null ) return null;
			
			String value;
			String[] empresa =(String[]) empresas.get( r );
			value = empresa[ c ];
			return value;
		}
		
		public String getColumnName( int c ) {
			return empresasColumnNames[ c ];
		}
		
		public int getColumnCount() {
			return empresasColumnNames.length; 
		}	
		
		
	}
	
	private class TabFiscaisModel extends AbstractTableModel {
		
		public int getRowCount() {
			if ( fiscais == null ) return 0;
			return fiscais.size();
		}
		
		public Object getValueAt( int r , int c ) {
			
			if ( fiscais == null ) return null;
			
			String value;
			String[] fiscal =(String[]) fiscais.get( r );
			value = fiscal[ c ];
			return value;				
		}
		
		public String getColumnName( int c ) {
			return fiscaisColumnNames[ c ];
		}
		
		public int getColumnCount() {
			return fiscaisColumnNames.length; 
		}	
		
		
	}
	
	
	private class TabOSModel extends AbstractTableModel {
		
		public int getRowCount() {
			return os.size();
		}
		
		public Object getValueAt( int r , int c ) {			
		String[] theRow = null ;
		    if ( r < os.size() ) {
		    	Object obj = os.get( r );
		    	theRow = (String[]) obj;
		    	return ( theRow[ c ] );
		    }
		    else {
		    	return null;
		    }		    
		}
		
		public String getColumnName( int c ) {
			return osColumnNames[ c ];
		}
		
		public int getColumnCount() {
			return osColumnNames.length; 
		}	
		
		
	}
	
	/**
	 * Put a mask in corresponding textfield , after a key <ENTER> to be pressed
	 */
	private class MaskCMCHandler implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			String cmc = cmcTextField.getText();
			if ( cmc.length() == 7 ) {
				String first = cmc.substring( 0, 3 );
				String second = cmc.substring( 3, 6 );
				String digit = cmc.substring( 6, 7 );
				cmcTextField.setText("");
				String dummy = first + "." + second + "-" + digit;
				cmcTextField.setText( dummy );
			}
		}
	}
	
	private class FmtInitDataAuditListener extends KeyAdapter {
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
				inicioPeriodoTxt.setText("");
				f = false;
			}
			if ( searchKey.length() == 8 ) {
				String day = searchKey.substring( 0, 2 );
				String month = searchKey.substring( 2, 4 );
				String year = searchKey.substring( 4, 8);				
				inicioPeriodoTxt.setText("");
				String dummy = day + "/" + month + "/" + year;

				inicioPeriodoTxt.setText( dummy );
				
				f = true;
				return;				   
			}
		}
	}
	
	private class FmtEndDataAuditListener extends KeyAdapter {
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
				finalPeriodoTxt.setText("");
				f = false;
			}
			if ( searchKey.length() == 8 ) {
				String day = searchKey.substring( 0, 2 );
				String month = searchKey.substring( 2, 4 );
				String year = searchKey.substring( 4, 8);				
				finalPeriodoTxt.setText("");
				String dummy = day + "/" + month + "/" + year;

				finalPeriodoTxt.setText( dummy );
				
				f = true;
				return;				   
			}
		}
	}
	
	
/*	private String converteFrmtDataFromMySQL( String currData ) {

		StringTokenizer st = new StringTokenizer( currData,"-");
		String year = st.nextToken();
		String month = st.nextToken();
		String day = st.nextToken();
		
		String date = day + "/" + month + "/" + year;
		
		return date;
		
	}
	
	private String converteFrmtDataToMySQL( String currData ) {

		StringTokenizer st = new StringTokenizer( currData,"/");
		String day = st.nextToken();
		String month = st.nextToken();
		String year = st.nextToken();
		
		String date = year + "-" + month + "-" + day;
		
		return date;
		
	} */
	
	
}
