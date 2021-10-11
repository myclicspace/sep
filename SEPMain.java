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
import java.awt.*;
import java.awt.event.*;
import java.io.*;


//import java.util.*;
import java.sql.*;


//:: PENDENCIA
// 1. simular entrada de dados para alguns formularios
// 2. fazer relatorios
// 3. definir classes de entidade e controle
// 4. Melhorar a navegação da GUI de exibição de linhas da tabela


public class SEPMain extends JFrame {
	private JMenu arquivoMenu;
	private JMenu tabelaMenu;
	private JMenu osMenu;
	private JMenu empresasMenu;
	private JMenu fiscalizacaoMenu;
	private JMenu documentosMenu;
	private JMenu mapasMenu;
	private JMenu helpMenu;

 	private JMenu relatorioMenu;
	private JMenu dossieMenu;
	private SEPAboutDlg sepAboutDlg ;
	private SEPFormTabServicos sepFormTabServicos;
	private SEPFormTabFiscais sepFormTabFiscais;
	private SEPFormTabUsuarios sepFormTabUsuarios;
	private SEPFormTabInfracoes sepFormTabInfracoes;
	private SEPFormTabRotinas sepFormTabRotinas;
	private SEPFormTabEmpresas sepFormTabEmpresas;
	private SEPFormTabOS sepFormTabOS;
	private SEPFormTabParcelamento sepFormTabParcelamento;
	private SEPFormTabExtrato sepFormTabExtrato;
	private SEPFormTabDatm sepFormTabDatm;
	private SEPFormTabMapaGeral sepFormTabMapaGeral;
	private SEPFormTabMapaConstrucao sepFormTabMapaConstrucao;
	private SEPFormTabMapaEscolar sepFormTabMapaEscolar;
	private SEPFormTabTomadores sepFormTabTomadores;
	private SEPFormTabQuesitos sepFormTabQuesitos;
	private SEPFormTreeOS sepFormTreeOS;

	private SEPFormExportController sepFormExportController;
	private SEPFormTabAI sepFormTabAI;
	private SEPFormTabDE sepFormTabDE;
	private SEPFormTabME sepFormTabME;	
	private SEPFormTabNO sepFormTabNO;
	private SEPFormTabNOAI sepFormTabNOAI;
	private SEPFormTabRecibo sepFormTabRecibo;
	private SEPFormTabRF sepFormTabRF;
	private SEPFormTabTA sepFormTabTA;
	private SEPFormTabTF sepFormTabTF;
	private SEPFormTabTI sepFormTabTI;
	private SEPFormTabFC sepFormTabFC;
	private SEPFormTabMP sepFormTabMP;
	private SEPFormRF sepFormRF;
	private SEPFormTabMDATM sepFormTabMDATM;
	private SEPFormTabMParcelamento sepFormTabMParcelamento;
	private SEPFormTabMC sepFormTabMC;
	private SEPFormTabMR sepFormTabMR;


	private Connection conexao;

	private FileOutputStream log;

	private JLabel msgLabel = null;

// -- Objetos de eventos para toolbar e itens de menus
   private FocusAdapter focusMenu;

    private Action actionBackupExport;

	private Action actionCriarOS;
	private Action actionAbrirOS;
	private Action actionExtratosOS;
	private Action actionExportaOS;

	private Action actionServicos;
	private Action actionInfracoes;
	private Action actionEmpresas;
	private Action actionQuesitos;
	private Action actionRotinas;
	private Action actionFiscais;
	private Action actionUsuarios;

	private Action actionSair;
	private Action actionExtratos;
	private Action actionDatms;

	private Action actionParcelamento;
    // mapas de apuracao
	private Action actionServicosPrestadosGeral;
	private Action actionServicosPrestadosConstrucao;
	private Action actionServicosPrestadosEscolas;
	private Action actionServicosTomados;
	// reports
	private Action actionAutoInfracao;
	private Action actionDemonstrativoEconomico;
	private Action actionNotificacao;
	private Action actionNotificacaoAutoInfracao;
	private Action actionRecibo;
	private Action actionRelatorioFiscalizacao;
	private Action actionTermoApreensao;
	private Action actionTermoInicial;
	private Action actionTermoFinal;
	private Action actionMapaEscolar;
	private Action actionDatmsPagos;
	private Action actionRelatorioParcelamento;
	private Action actionMapaApuracao;
	private Action actionMapaConstrucao;
	private Action actionRotinaFiscalizacao;
	private Action actionFolhaComplementar;
	private Action actionMapaPrestadores;
	private Action actionAbout;
	

// Menu Arquivo
	private JMenuItem exportaDadosItem;
	private JMenuItem importaDadosItem;
	private JMenuItem sairItem;

// Menu Tabela
    private JMenuItem servicosItem;
	private JMenuItem infracoesItem;
	private JMenuItem fiscaisItem;
	private JMenuItem usuariosItem;
	private JMenuItem rotinasItem;
    private JMenuItem quesitosItem;
	private JMenuItem empresasItem;

// Menu Ordem de serviço
    private JMenuItem addOSItem;
    private JMenuItem choosenOSItem;

//  Menu mapa de empresas
    private JMenuItem cadastroPrestadoresItem;


//  Menu Mapas de apuracao de receita
    private JMenuItem geralItem;
    private JMenuItem construcaoCivilItem;
    private JMenuItem escolasItem;

// Menu Dossie
	private JMenuItem extratoItem;
	private JMenuItem datmItem;
	private JMenuItem parcelamentoItem;

// Menu Relatorios
	private JMenuItem autoInfracaoItem;
	private JMenuItem demonstrativoEconomicoItem;
	private JMenuItem notificacaoItem;
	private JMenuItem notificacaoAutoInfracaoItem;
	private JMenuItem reciboItem;
	private JMenuItem relatorioFiscalizacaoItem;
	private JMenuItem termoApreensaoItem;
	private JMenuItem termoFinalItem;
	private JMenuItem termoInicialItem;
	private JMenuItem mapaEscolarItem;
	private JMenuItem folhaComplementarItem;
	private JMenuItem mapaPrestadoresItem;
	private JMenuItem rotinaFiscalizacaoItem;
	private JMenuItem mapaDatmItem;
	private JMenuItem relatorioParcelamentoItem;
	private JMenuItem mapaApuracaoItem;
	private JMenuItem mapaConstrucaoItem;
	
// Menu Help
	private JMenuItem contentHelpItem;
	private JMenuItem sobreItem;	


	private Container container;

	private static JMenuBar bar;
	private static SEPMain sepMain;
	private static final String IMAGE_DIRECTORY = 
			System.getProperty("user.dir") +
			System.getProperty("file.separator") +  "img" + 
			System.getProperty("file.separator") ;


	public SEPMain( Connection c ) {
		super("Papéis de Trabalho - Secretaria Municipal de Finanças");
		container = getContentPane();
		container.setLayout( new BorderLayout() );
		conexao = c ; // abreConexao();

/*		try {
		    log = new FileOutputStream("log.err");
		    PrintStream ps = new PrintStream( log );
			System.setErr( ps );
		}
		catch ( IOException ioe ) {
			ioe.printStackTrace();
		} */

		initializeActionEvents();

		JToolBar toolBar = createJToolBar();

		JPanel msgPanel = new JPanel( new FlowLayout( FlowLayout.LEFT) );
		msgLabel = new JLabel("Nenhuma ordem de serviço aberta" );
		Border etched = BorderFactory.createEtchedBorder();
	 	msgPanel.setBorder( etched );

		msgPanel.add( msgLabel );

		container.add( toolBar, BorderLayout.NORTH );
		container.add( msgPanel, BorderLayout.SOUTH );


		bar = createJMenuBar();
		setJMenuBar( this.bar );
  		this.setSize( 770, 550 );


  		// Centraliza tela
  		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
  		int x = ( int ) ( ( d.getWidth() - this.getWidth() ) / 2 );
  		int y = ( int ) ( ( d.getHeight() - this.getHeight() ) / 2 );
  		this.setLocation( x, y );

		ImageIcon brasaoImage = new ImageIcon(IMAGE_DIRECTORY + "brasao.gif");
		setIconImage(brasaoImage.getImage());
        setVisible( true );
	}

	/**
	 * Sets a message in the status bar
	 *
	 * @param msg A <code>String</code> indicates the message to the status bar
	 */
	public void setStatusBar( String msg ) {
		msgLabel.setText( msg );
	}

	private void initializeActionEvents() {

		ImageIcon iconExportaOS = new ImageIcon("c:\\sep\\img\\exportaOS.gif");
		ImageIcon iconCriarOS = new ImageIcon("c:\\sep\\img\\criarOS.gif");
		ImageIcon iconAbrirOS = new ImageIcon("c:\\sep\\img\\abrirOS.gif");
   	    ImageIcon iconExtratosOS = new ImageIcon("c:\\sep\\img\\extratosOS.gif");

//		Menu
			 focusMenu =  new FocusAdapter() {
				 public  void focusPerformed( FocusEvent f ) {
					 focusChanged( focusMenu );
				 }
			 };


// Botoes de toolbar
		actionExportaOS = new AbstractAction( "Exporta", iconExportaOS ) {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionExportaOS );
			}
		};

	    actionExtratosOS = new AbstractAction( "Extratos", iconExtratosOS ) {
			public  void actionPerformed( ActionEvent e ) {
				 actionChanged( actionExtratosOS );
			}
		};

// Menu Arquivos

		actionBackupExport =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionBackupExport );
			}
		};



// Menu Tabelas
		actionServicos =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionServicos );
			}
		};


		actionInfracoes =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionInfracoes );
			}
		};

		actionEmpresas =  new AbstractAction( "Empresas", iconExtratosOS) {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionEmpresas );
			}
		};

		actionQuesitos =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionQuesitos );
			}
		};

		actionRotinas =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionRotinas );
			}
		};

		actionFiscais =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionFiscais );
			}
		};

		actionUsuarios =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionUsuarios );
			}
		};


// Ordem de servico
		actionCriarOS = new AbstractAction("Criar", iconCriarOS ) {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionCriarOS );
			}
		};


		actionAbrirOS = new AbstractAction( "Abrir", iconAbrirOS ) {
			public  void actionPerformed( ActionEvent e ) {
				 actionChanged( actionAbrirOS );
			}
		};



// 	Dossie pregresso da empresa
		actionExtratos =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionExtratos );
			}
		};

		actionDatms =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionDatms );
			}
		};


		actionParcelamento =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionParcelamento );
			}
		};

//	Mapas de apuracao de receita
		actionServicosPrestadosGeral =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionServicosPrestadosGeral );
			}
		};

		actionServicosPrestadosConstrucao =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionServicosPrestadosConstrucao );
			}
		};

		actionServicosPrestadosEscolas =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionServicosPrestadosEscolas );
			}
		};


		actionServicosTomados =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionServicosTomados );
			}
		};



		actionAutoInfracao =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionAutoInfracao );
			}
		};


// Relatórios
		actionDemonstrativoEconomico =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionDemonstrativoEconomico );
			}
		};

		actionNotificacao =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionNotificacao );
			}
		};

		actionNotificacaoAutoInfracao =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionNotificacaoAutoInfracao );
			}
		};

		actionRecibo =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionRecibo );
			}
		};

		actionRelatorioFiscalizacao =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionRelatorioFiscalizacao );
			}
		};

		actionTermoApreensao =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionTermoApreensao );
			}
		};

		actionTermoFinal =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionTermoFinal );
			}
		};
		
		actionTermoInicial =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionTermoInicial );
			}
		};

		actionMapaEscolar =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionMapaEscolar );
			}
		};
		
		actionFolhaComplementar = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				actionChanged( actionFolhaComplementar );
			}
		};
		
		actionMapaPrestadores = new AbstractAction(){
			public void actionPerformed( ActionEvent e ) {
				actionChanged( actionMapaPrestadores );
			}
		};
		
		actionRotinaFiscalizacao = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				actionChanged( actionRotinaFiscalizacao );
			}
		};
		
		actionDatmsPagos = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				actionChanged( actionDatmsPagos );
			}
		}; 
		
		actionRelatorioParcelamento = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				actionChanged( actionRelatorioParcelamento );
			}
		};
		
		actionMapaApuracao = new AbstractAction() {
		    public void actionPerformed( ActionEvent e ) {
			    actionChanged( actionMapaApuracao );
		    }
	    }; 

		actionMapaConstrucao = new AbstractAction() {
			public void actionPerformed( ActionEvent e ) {
				actionChanged( actionMapaConstrucao );
			}
		}; 

		actionSair =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionSair );
			}
		};
		
//	Ajuda sobre o sistema
			 actionAbout =  new AbstractAction() {
				 public  void actionPerformed( ActionEvent e ) {
					 actionChanged( actionAbout );
				 }
			 };

	}
	


	private JToolBar createJToolBar() {

	    JToolBar toolBar = new JToolBar();

	 	JButton criarOSButton = toolBar.add( actionCriarOS );
	 	criarOSButton.setText("");
	 	criarOSButton.setToolTipText( "Criar ordens de serviço");
	 	criarOSButton.addActionListener( actionCriarOS );

	 	JButton abrirOSButton = toolBar.add( actionAbrirOS );
	 	abrirOSButton.setToolTipText( "Abrir ordens de serviço" );
	 	abrirOSButton.setText("");

	 	//JButton extratosOSButton = toolBar.add( actionExtratosOS );
	 	//extratosOSButton.setToolTipText( "Informar extratos de pagamentos");
	 	//extratosOSButton.setText("");

	 	JButton companyOSButton = toolBar.add( actionEmpresas );
	 	companyOSButton.setToolTipText( "Cadastrar empresas fiscalizadas");
	 	companyOSButton.setText("");

	 	//JButton relatoriosOSButton = toolBar.add( actionCriarOS );
	 	//relatoriosOSButton.setToolTipText( "Emissão de relatórios fiscais" );
	 	//relatoriosOSButton.setText("");


	 	toolBar.add( criarOSButton );
	 	toolBar.add( abrirOSButton );
//	 	toolBar.add( extratosOSButton );
	 	toolBar.addSeparator();
	 	toolBar.add( companyOSButton );
	 	//toolBar.add( relatoriosOSButton );

		Border etched = BorderFactory.createEtchedBorder();
	 	toolBar.setBorder( etched );

	 	toolBar.setFloatable( false );

	 	return toolBar;

	}

	private JMenuBar createJMenuBar(){
		JMenuBar bar = new JMenuBar();


// Menu Arquivo
		arquivoMenu = new JMenu("Arquivo");
		arquivoMenu.setMnemonic('A');
//		exportaDadosItem = new JMenuItem("Exporta dados");
//		exportaDadosItem.setMnemonic('E');
//		exportaDadosItem.addActionListener( actionBackupExport );

//		importaDadosItem = new JMenuItem("Importa dados");
//		importaDadosItem.setMnemonic('I');
		sairItem = new JMenuItem("Sair");
		sairItem.setMnemonic('S');
		sairItem.addActionListener( actionSair );
		//arquivoMenu.add( exportaDadosItem );
		//arquivoMenu.add( importaDadosItem );
		//arquivoMenu.addSeparator();
//		arquivoMenu.add( exportaDadosItem );
		arquivoMenu.add( sairItem );

// Menu Tabelas
		tabelaMenu = new JMenu("Tabelas");
		tabelaMenu.setMnemonic('T');

		infracoesItem = new JMenuItem("Infrações");
		infracoesItem.setMnemonic('I');
		infracoesItem.addActionListener( actionInfracoes );

		servicosItem = new JMenuItem("Lista de serviços");
		servicosItem.setMnemonic('L');
		servicosItem.addActionListener( actionServicos );


		fiscaisItem = new JMenuItem("Fiscais");
		fiscaisItem.setMnemonic('F');
		fiscaisItem.addActionListener( actionFiscais );

		usuariosItem = new JMenuItem("Usuarios");
		usuariosItem.setMnemonic('U');
		usuariosItem.addActionListener( actionUsuarios );

		rotinasItem = new JMenuItem("Rotinas");
		rotinasItem.setMnemonic('R');
		rotinasItem.addActionListener( actionRotinas );

		quesitosItem = new JMenuItem("Quesitos");
		quesitosItem.setMnemonic('Q');
		quesitosItem.addActionListener( actionQuesitos );

		empresasItem = new JMenuItem("Empresas");
		empresasItem.setMnemonic('E');
		empresasItem.addActionListener( actionEmpresas );

		tabelaMenu.add( servicosItem );
		tabelaMenu.add( infracoesItem );
		tabelaMenu.add( fiscaisItem );
		tabelaMenu.add( usuariosItem );
		tabelaMenu.add( rotinasItem );
		tabelaMenu.add( quesitosItem );
		tabelaMenu.add( empresasItem );

// Menu Ordem de servico
        osMenu = new JMenu("Ordem de serviço");
        osMenu.setMnemonic('O');
		addOSItem = new JMenuItem("Cadastrar");
		addOSItem.setMnemonic('C');
		addOSItem.addActionListener( actionCriarOS );

		choosenOSItem = new JMenuItem("Abrir");
		choosenOSItem.setMnemonic('A');
		choosenOSItem.addActionListener( actionAbrirOS );

		osMenu.add( addOSItem );
		osMenu.add( choosenOSItem );



// Menu Dossie
		//dossieMenu = new JMenu("Dossiê");
		//dossieMenu.setMnemonic('D');
		extratoItem = new JMenuItem("Extrato");
		extratoItem.setMnemonic('E');
		extratoItem.addActionListener( actionExtratos );

		datmItem = new JMenuItem("DATMs pagos");
		datmItem.setMnemonic('D');
		datmItem.addActionListener( actionDatms );


		parcelamentoItem = new JMenuItem("Parcelamento");
		parcelamentoItem.setMnemonic('O');
		parcelamentoItem.addActionListener( actionParcelamento );

		//dossieMenu.add( extratoItem );
		//dossieMenu.add( parcelamentoItem );



// Menu Mapa de Apuração de Receita

		mapasMenu = new JMenu("Mapas de apuração da receita");
		mapasMenu.setMnemonic('M');

		geralItem = new JMenuItem("Contribuinte geral");
		geralItem.setMnemonic(KeyEvent.VK_G);
		geralItem.addActionListener( actionServicosPrestadosGeral );
		construcaoCivilItem = new JMenuItem("Contribuinte da construção civil");
		construcaoCivilItem.setMnemonic(KeyEvent.VK_L);
		construcaoCivilItem.addActionListener( actionServicosPrestadosConstrucao );
		escolasItem = new JMenuItem("Contribuinte da rede de ensino");
		escolasItem.setMnemonic(KeyEvent.VK_L);
		escolasItem.addActionListener( actionServicosPrestadosEscolas );

		mapasMenu.add( geralItem );
		mapasMenu.add( construcaoCivilItem );
		mapasMenu.add( escolasItem );

//  Menu Relação de prestadores de serviço
        cadastroPrestadoresItem = new JMenuItem("Mapa de prestadores de serviço");
        cadastroPrestadoresItem.setMnemonic('P');
		cadastroPrestadoresItem.addActionListener( actionServicosTomados );

// Menu Empresas
        fiscalizacaoMenu = new JMenu("Levantamento fiscal");
        fiscalizacaoMenu.setMnemonic('L');
        fiscalizacaoMenu.add( datmItem );
        fiscalizacaoMenu.add( parcelamentoItem );
        fiscalizacaoMenu.add( mapasMenu );
        fiscalizacaoMenu.add( cadastroPrestadoresItem );



		/* fiscalizacaoMenu.add( osItem );
		fiscalizacaoMenu.add( dossieMenu );
		fiscalizacaoMenu.add( mapasMenu );
		fiscalizacaoMenu.add( cadastroPrestadoresItem );
		fiscalizacaoMenu.add( documentosMenu );
		fiscalizacaoMenu.add( choosenOSItem ); */

// Menu Relatorios
		relatorioMenu = new JMenu("Relatórios");
		relatorioMenu.setMnemonic('R');
		
		// Mapas auxiliares
		mapaEscolarItem = new JMenuItem("Mapa escolar");
		mapaEscolarItem.setMnemonic('P');
		mapaPrestadoresItem = new JMenuItem("Mapa prestadores");
		mapaPrestadoresItem.setMnemonic('S');
		mapaDatmItem = new JMenuItem("Datms pagos");
		relatorioParcelamentoItem = new JMenuItem("Parcelamentos efetuados");
		mapaApuracaoItem = new JMenuItem("Mapa geral");
		mapaConstrucaoItem = new JMenuItem("Mapa de construcao civil");	
		
		
		autoInfracaoItem = new JMenuItem("Auto de infração");
		autoInfracaoItem.setMnemonic('A');
		demonstrativoEconomicoItem = new JMenuItem("Demonstrativo econômico");
		demonstrativoEconomicoItem.setMnemonic('D');
		notificacaoItem = new JMenuItem("Notificação");
		notificacaoItem.setMnemonic('N');
		notificacaoAutoInfracaoItem = new JMenuItem("Notificação / Auto de infração");
		notificacaoAutoInfracaoItem.setMnemonic('I');
		reciboItem = new JMenuItem("Recibo de Documentos");
		reciboItem.setMnemonic('R');
		relatorioFiscalizacaoItem = new JMenuItem("Relatório de fiscalização");
		reciboItem.setMnemonic('F');
		termoFinalItem = new JMenuItem("Termo final");
		termoFinalItem.setMnemonic('T');
		termoApreensaoItem = new JMenuItem("Termo de apreensão");
		termoApreensaoItem.setMnemonic('E');
		
		termoInicialItem = new JMenuItem("Termo inicial");
		termoInicialItem.setMnemonic('L');
		
		folhaComplementarItem = new JMenuItem("Folha complementar");
		folhaComplementarItem.setMnemonic('M');
		rotinaFiscalizacaoItem = new JMenuItem("Rotina de fiscalizacao");
		rotinaFiscalizacaoItem.setMnemonic('O');
		
		// relatorios auxiliares
		mapaDatmItem.addActionListener( actionDatmsPagos );
		relatorioMenu.add( mapaDatmItem );		
		mapaEscolarItem.addActionListener( actionMapaEscolar );		
		relatorioMenu.add( mapaEscolarItem );
		mapaApuracaoItem.addActionListener( actionMapaApuracao );
		relatorioMenu.add( mapaApuracaoItem );
		mapaConstrucaoItem.addActionListener( actionMapaConstrucao );
		relatorioMenu.add( mapaConstrucaoItem );
		mapaPrestadoresItem.addActionListener( actionMapaPrestadores );
		relatorioMenu.add( mapaPrestadoresItem );
		relatorioParcelamentoItem.addActionListener( actionRelatorioParcelamento );
		relatorioMenu.add( relatorioParcelamentoItem );
		
		relatorioMenu.addSeparator();
		// relatorios principais
		autoInfracaoItem.addActionListener( actionAutoInfracao );
		relatorioMenu.add( autoInfracaoItem );		
		demonstrativoEconomicoItem.addActionListener( actionDemonstrativoEconomico );
		relatorioMenu.add( demonstrativoEconomicoItem );		
		notificacaoItem.addActionListener( actionNotificacao );
		relatorioMenu.add( notificacaoItem );
		notificacaoAutoInfracaoItem.addActionListener( actionNotificacaoAutoInfracao );
		relatorioMenu.add( notificacaoAutoInfracaoItem );
		reciboItem.addActionListener( actionRecibo );
		relatorioMenu.add( reciboItem );
		relatorioFiscalizacaoItem.addActionListener( actionRelatorioFiscalizacao );
		relatorioMenu.add( relatorioFiscalizacaoItem  );
		termoApreensaoItem.addActionListener( actionTermoApreensao );
		relatorioMenu.add( termoApreensaoItem );
		termoFinalItem.addActionListener( actionTermoFinal );
		relatorioMenu.add( termoFinalItem );
		
		termoInicialItem.addActionListener( actionTermoInicial );
		relatorioMenu.add( termoInicialItem );
		
		folhaComplementarItem.addActionListener( actionFolhaComplementar);
		relatorioMenu.add( folhaComplementarItem );
		
		
		rotinaFiscalizacaoItem.addActionListener( actionRotinaFiscalizacao );
		relatorioMenu.add( rotinaFiscalizacaoItem );
		
		
// Menu sobre
  		helpMenu = new JMenu("Ajuda");
   		helpMenu.setMnemonic('j');

   		contentHelpItem = new JMenuItem("Help on line sobre os papeis de trabalho");
   		contentHelpItem.setMnemonic('H');
   		 //contentHelpItem.addActionListener( actionHelp );

		sobreItem = new JMenuItem("Sobre os papeis de trabalho...");
		sobreItem.setMnemonic('S');
		sobreItem.addActionListener( actionAbout );
		 
		helpMenu.add( contentHelpItem );
		helpMenu.add( sobreItem );

		bar.add( arquivoMenu );
		bar.add( tabelaMenu );
		bar.add( osMenu );
//		bar.add( empresasMenu );
		bar.add( fiscalizacaoMenu );
		bar.add( relatorioMenu );
		bar.add( helpMenu );
		
		bar.addFocusListener( focusMenu );		

		return bar;

	}


	private void focusChanged( FocusAdapter focusChanged )  {		
//	   Menu Arquivo
			if ( focusChanged == focusMenu ) {
				System.out.println( "Focus" );
			}
		}


/*
	 * Este metodo será responsável pelo gerenciamento dos
	 * eventos dos itens de um menu
	 *
	 **/

	private void actionChanged( Action actionChanged )  {

		JPanel p = null;
		JPanel inputDataPanel =  new JPanel( new BorderLayout() );
//		container.removeAll();
		JToolBar toolBar = createJToolBar();
		container.add( toolBar, BorderLayout.NORTH );
		JPanel msgPanel =  new JPanel( new FlowLayout( FlowLayout.LEFT) );
		Border etched =  BorderFactory.createEtchedBorder();
	 	msgPanel.setBorder( etched );
		msgLabel = new JLabel();


// itens de menu Arquivo
		if ( actionChanged == actionBackupExport ) {
			sepFormExportController = new SEPFormExportController( this, conexao );
			p = sepFormExportController;
			msgLabel.setText( "Exportação de ordens de serviços");
		}


// itens de menu Tabelas
		if ( actionChanged == actionFiscais ) {
			sepFormTabFiscais = new SEPFormTabFiscais( conexao );
			p = sepFormTabFiscais.createTabFiscaisPanel();
			msgLabel.setText( "Tabelas de fiscais");
		}
		if ( actionChanged == actionUsuarios ) {
			sepFormTabUsuarios = new SEPFormTabUsuarios( conexao );
			p = sepFormTabUsuarios.createTabUsuariosPanel();
			msgLabel.setText( "Tabelas de usuarios");
		}

		else if ( actionChanged == actionInfracoes ) {
			sepFormTabInfracoes = new SEPFormTabInfracoes( conexao );
			p = sepFormTabInfracoes.createTabInfracoesPanel();
			msgLabel.setText( "Tabelas de infrações");
		}
		else if ( actionChanged == actionServicos ) {
			sepFormTabServicos = new SEPFormTabServicos( conexao );
			p = sepFormTabServicos.createTabServicosPanel();
			msgLabel.setText( "Tabelas da lista de servicos");
		}

		else if ( actionChanged == actionRotinas ) {
			sepFormTabRotinas = new SEPFormTabRotinas( conexao );
			p = sepFormTabRotinas.createTabRotinasPanel();
			msgLabel.setText( "Tabelas de rotinas");
		}
		else if ( actionChanged == actionQuesitos ) {
			sepFormTabQuesitos = new SEPFormTabQuesitos( conexao );
			p = sepFormTabQuesitos.createTabQuesitosPanel();
			msgLabel.setText( "Tabelas de quesitos");
		}
		else if ( actionChanged == actionEmpresas ) {
			sepFormTabEmpresas = new SEPFormTabEmpresas( conexao ) ;
			p = sepFormTabEmpresas.createTabEmpresasPanel();
			msgLabel.setText( "Tabelas de empresas");
		}

// itens de menu Mapas

		// Exibe mapa de apuracão de receitas para contribuintes em geral
		else if ( actionChanged == actionServicosPrestadosGeral ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabMapaGeral = new SEPFormTabMapaGeral( conexao );
				p = sepFormTabMapaGeral.createTabMapaGeralPanel();
				setStatusBar( " Cadastro de Serviços prestados - " +
				     "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;

			}

		}

		// Display notes of taxes for building companies
		else if ( actionChanged == actionServicosPrestadosConstrucao ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabMapaConstrucao =
				   new SEPFormTabMapaConstrucao( conexao );
				p = sepFormTabMapaConstrucao.createTabMapaConstrucaoPanel();
				setStatusBar( " Cadastro de Serviços prestados - " +
				     "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;

			}

		} 

		// Display notes of taxes for learning companies
		else if ( actionChanged == actionServicosPrestadosEscolas ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabMapaEscolar = new SEPFormTabMapaEscolar( conexao );
				p = sepFormTabMapaEscolar.createTabMapaEscolarPanel();
				setStatusBar( " Cadastro de Serviços prestados - " +
				     "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;

			}

		}

		// Displaying companies that bought any kind of service
		else if ( actionChanged == actionServicosTomados ) {

			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabTomadores = new SEPFormTabTomadores( conexao ) ;
				p = sepFormTabTomadores.createTabTomadoresPanel();
				setStatusBar( " Cadastro de serviços tomados - " +
				     "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;

			}
		}



// itens de menu Fiscalização - Ordem de serviço
		else if ( actionChanged == actionCriarOS ) {
			sepFormTabOS = new SEPFormTabOS( conexao ) ;
			p = sepFormTabOS.createTabOSPanel();
			msgLabel.setText( "Cadastramento de ordem de serviço");
		}
		else if ( actionChanged == actionAbrirOS ) {
			sepFormTreeOS = new SEPFormTreeOS( this, conexao );
			p = sepFormTreeOS.createTreeOSPanel();
			msgLabel.setText( "Ativar ordem de serviço");
		}

// itens de menu Dossiê
		else if ( actionChanged == actionParcelamento ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabParcelamento = new SEPFormTabParcelamento( conexao ) ;
				p = sepFormTabParcelamento.createTabParcelamentosPanel();
				setStatusBar( " Cadastro de Parcelamento - " +
				     "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;

//				msgLabel.setText( "Cadastro de parcelamentos");
			}
		}
		else if ( actionChanged == actionExtratos ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabExtrato = new SEPFormTabExtrato( conexao ) ;
				p = sepFormTabExtrato.createTabExtratosPanel();
				setStatusBar( " Cadastro de Extratos - " +
				     "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;

			}
		}
		else if ( actionChanged == actionDatms ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabDatm = new SEPFormTabDatm( conexao ) ;
				p = sepFormTabDatm.createTabDatmsPanel();
				setStatusBar( " Cadastro de DATMs - " +
				     "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;

			}

		}

// itens de menu Relatorios


// itens de menu Relatorios
 		else if ( actionChanged == actionAutoInfracao ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabAI = new SEPFormTabAI( conexao );
				p = sepFormTabAI.createTabAIPanel();
				setStatusBar( " Auto de infração - " +
					 "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;
			}
		}

 		else if ( actionChanged == actionDemonstrativoEconomico ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabDE = new SEPFormTabDE( conexao );
				p = sepFormTabDE.createTabDEPanel();
				setStatusBar( " Demonstrativo econômico - " +
					 "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;
			}
		}

 		else if ( actionChanged == actionNotificacao ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabNO = new SEPFormTabNO( conexao );
				p = sepFormTabNO.createTabNOPanel();
				setStatusBar( " Notificação - " +
					 "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;
			}
		}

 		else if ( actionChanged == actionNotificacaoAutoInfracao ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabNOAI = new SEPFormTabNOAI( conexao );
				p = sepFormTabNOAI.createTabNOAIPanel();
				setStatusBar( " Notificação/Auto de infração - " +
					 "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;
			}
		}

 		else if ( actionChanged == actionRecibo ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabRecibo = new SEPFormTabRecibo( conexao );
				p = sepFormTabRecibo.createTabReciboPanel();
				setStatusBar( " Recibo de documentos - " +
					 "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;
			}
		}
		
		else if ( actionChanged == actionMapaEscolar ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabME = new SEPFormTabME( conexao );
				p = sepFormTabME.createTabMEPanel();
				setStatusBar( " Mapa escolar - " +
					 "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;
			}
		}
		
		else if ( actionChanged == actionTermoInicial ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabTI = new SEPFormTabTI( conexao ) ;
				p = sepFormTabTI.createTabTIPanel();
				setStatusBar( " Termo Inicial - " +
					 "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;
			}
		}

 		else if ( actionChanged == actionTermoApreensao ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabTA = new SEPFormTabTA( conexao ) ;
				p = sepFormTabTA.createTabTAPanel();
				setStatusBar( " Termo de apreensão - " +
					 "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;
			}
		}

 		else if ( actionChanged == actionTermoFinal ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabTF = new SEPFormTabTF( conexao ) ;
				p = sepFormTabTF.createTabTFPanel();
				setStatusBar( " Termo Final - " +
					 "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;
				
			}
 			
		}
		
		else if ( actionChanged == actionFolhaComplementar ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabFC = new SEPFormTabFC( conexao ) ;
				p = sepFormTabFC.createTabFCPanel();
				setStatusBar( " Folha complementar - " +
					 "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;
			}
		}

		else if ( actionChanged == actionMapaPrestadores ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabMP = new SEPFormTabMP( conexao ) ;
				p = sepFormTabMP.createTabMPPanel();
				setStatusBar( " Mapa de prestadores - " +
					 "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;
			}
			
		}
		
		else if ( actionChanged == actionRotinaFiscalizacao ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormRF = new SEPFormRF( conexao ) ;
				p = sepFormRF.createTreeRFPanel();
				setStatusBar( " Rotina de fiscalização - " +
					 "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;
			}
						
		}
		
		else if ( actionChanged == actionRelatorioFiscalizacao ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabRF = new SEPFormTabRF( conexao ) ;
				p = sepFormTabRF.createTabRFPanel();
				setStatusBar( " Relatório de fiscalização - " +
					 "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;
			}
		} 
		
		else if ( actionChanged == actionDatmsPagos ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabMDATM = new SEPFormTabMDATM( conexao ) ;
				p = sepFormTabMDATM.createTabMDATMPanel();
				setStatusBar( " Relatório de DATMs pagos - " +
					 "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;
			}
		}
		
		else if ( actionChanged == actionRelatorioParcelamento ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabMParcelamento = new SEPFormTabMParcelamento( conexao );
				p = sepFormTabMParcelamento.createTabMParcelamentoPanel();
				
				setStatusBar( " Relatório de parcelamento efetuados - " +
					 "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;
				
			}

		}
		else if ( actionChanged == actionMapaApuracao ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabMR = new SEPFormTabMR( conexao );
				p = sepFormTabMR.createTabMRPanel();
				setStatusBar( "Mapa de apuração da receita - "  +
					 "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;
			}
		}
		else if ( actionChanged == actionMapaConstrucao ) {
			String cmcEmpresa = EmpresaAtual.getCMCEmpresa();
			if ( cmcEmpresa == null ) {
				errorMessageAboutInvalidAction();				
			}
			else {
				sepFormTabMC = new SEPFormTabMC( conexao );
				p = sepFormTabMC.createTabMCPanel();
				setStatusBar( "Mapa de construcao civil - "  +
					 "Ordem de serviço " + EmpresaAtual.getNumeroOS() +
					 " para a empresa " + EmpresaAtual.getRazaoSocialEmpresa() +
					 "( Incr.Mun " + EmpresaAtual.getCMCEmpresa() + ") ABERTA ") ;
								
			}
		} 
		
// itens de menu Help
   		else if ( actionChanged == actionAbout ) {
			sepAboutDlg = new SEPAboutDlg( this );
			sepAboutDlg.setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
			sepAboutDlg.setResizable( false );
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			sepAboutDlg.setSize( 450, 300 );
			sepAboutDlg.setLocation( dim.width / 2 - sepAboutDlg.getWidth() / 2,
							  dim.height / 2 - sepAboutDlg.getHeight());

			sepAboutDlg.setVisible( true );
   			
   		}
		

		else if ( actionChanged == actionSair) {
			System.exit(0);
		}

        if ( p != null ) {
  		    container.removeAll();
			toolBar = createJToolBar();
			container.add( toolBar, BorderLayout.NORTH );
			msgPanel = new JPanel( new FlowLayout( FlowLayout.LEFT) );
			etched = BorderFactory.createEtchedBorder();
	 		msgPanel.setBorder( etched );
			//msgLabel = new JLabel();
        	inputDataPanel.add( p );
			inputDataPanel.setVisible( true );
			container.add( inputDataPanel );
			msgPanel.add( msgLabel );
			container.add( msgPanel, BorderLayout.SOUTH );
			show();
        }
	}
	
	private void errorMessageAboutInvalidAction() {
			String err = "Não existe nenhuma ordem de serviço aberta";
			JOptionPane.showMessageDialog( null, err,
			  "Operação inválida", JOptionPane.ERROR_MESSAGE   );
	}


	private String assembleReport() {
	   String fileName = "c:\\sep\\tabInfracoes.html";
	   String resultado = "<html><head><title>Tabela de cursos</title></head>"+
	       "<BODY>";
	   String query = "SELECT * FROM tabInfracoes ORDER BY codigoInfracao";
	   try
	   {
		    PreparedStatement stmt = conexao.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();

	      	while (rs.next()) {
  	   	        int codigoInfracao = rs.getInt(1);
   	   		    String descricaoInfracao = rs.getString(2);
   	   		    String enquadramento = rs.getString(3);
   	   		    String penalidade = rs.getString(4);

				resultado = resultado +
				            codigoInfracao + "<BR>" +
				            descricaoInfracao +  "<BR>" +
				            enquadramento + "<BR>" +
				            penalidade + "<P>" ;
		    }
		    rs.close();
	        stmt.close();

       	    File f = new File( fileName );
	        FileWriter fw = new FileWriter( f );
	        BufferedWriter bw = new BufferedWriter( fw );
	        bw.write( resultado );
	        bw.flush();
	        bw.close();
	   }
	   catch(SQLException sqle)
	   {
	      sqle.printStackTrace();
	   }
	   catch ( IOException ioe ) {
	   	  ioe.printStackTrace();
	   }
	   return fileName;
	}


  private Connection abreConexao() {
  	Connection myCon = null;
  	Statement mySta = null;
  	String driver = "org.gjt.mm.mysql.Driver";
  	String driverUrl = "jdbc:mysql://localhost/sep";
  	String user ="root";
  	String pwd = "";
  	try {

		Class.forName(driver); // .newInstance();
		if (myCon==null) {
			myCon = DriverManager.getConnection( driverUrl, user, pwd );
			//myCon.setAutoCommit( false );
			// mySta=myCon.createStatement();
		}

//      String driver = "com.mysql.jdbc.Driver"; // sun.jdbc.odbc.JdbcOdbcDriver";
//        String driverUrl = "jdbc:mysql://localhost/sep"; // jdbc:odbc:sep";
//     	String user = "root";
//     	String senha = "";
//     	Class.forName(driver);
//     	conexao = DriverManager.getConnection(driverUrl,user,senha);
    }
    catch ( Exception e ) {
    	e.printStackTrace();
    }

   	return myCon;
  }

}
