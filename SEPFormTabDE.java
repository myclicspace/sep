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
import java.io.*;

public class SEPFormTabDE extends JPanel {


	private int[] arrCodigoDE;
	private Map rows;
	private Vector de;
	private int bufferRecordPos = 0;

	TabDEModel model	= null;
    JTable deTable = null; 	
			
	private String[] columnNames =
	{ "Ano Referência", "Mês Referência", "Movimento Escriturado" };

	private JTextField anoReferenciaTxt, mesReferenciaTxt, movimentoEscrituradoTxt;
    private JPanel tabDEPanel, inputTabDEPanel;
	
	private GridBagLayout gbLayout;
	private	GridBagConstraints gbConstraints;	
	private Connection c;
	
	private Action actionIncluir;	
	private Action actionAlterar;	
	private Action actionExcluir;	
	private Action actionImprimir;
	private Action actionLimpar;	

	private String os  = EmpresaAtual.getNumeroOS();
	private String cmc = EmpresaAtual.getCMCEmpresa();
	private String razaoSocial = EmpresaAtual.getRazaoSocialEmpresa();	
	private String cnpj = EmpresaAtual.getCNPJEmpresa();

	private double movimentoAjustado, receitaSubstituto, impostoAjustado, impostoSubstituto;
	private double valorParcelamento, impostoParcelamento;
	private double movimentoEscriturado, impostoRecolhido;
	private String processoParcelamento;
	

	public SEPFormTabDE( Connection con ) {
		c = con;
		initializeActionEvents();
		loadDETable();	
	}

	public JPanel createTabDEPanel() {
		
        tabDEPanel = new JPanel( new BorderLayout() );
		gbLayout = new GridBagLayout();
		inputTabDEPanel = new JPanel( gbLayout );
		gbConstraints = new GridBagConstraints();
		
		JLabel anoReferenciaLabel = new JLabel("Ano Referência");
		anoReferenciaTxt = new JTextField( 04 );
		JLabel mesReferenciaLabel = new JLabel("Mês Referência");
		mesReferenciaTxt = new JTextField( 04 );
		JLabel movimentoEscrituradoLabel = new JLabel("Movimento Escriturado");
		movimentoEscrituradoTxt = new JTextField( 15 );
	
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Demonstrativo Econômico");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabDEPanel.setBorder( titleBorder );					
		
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.anchor = GridBagConstraints.EAST;
		
		addComponent( anoReferenciaLabel, 1, 0, 1, 1 ); 
		addComponent( anoReferenciaTxt, 1, 1, 1, 1  ); 		
		addComponent( mesReferenciaLabel, 2, 0, 1, 1 ); 
		addComponent( mesReferenciaTxt, 2, 1, 1, 1 ); 
		addComponent( movimentoEscrituradoLabel, 3, 0, 1, 1 ); 
		addComponent( movimentoEscrituradoTxt, 3, 1, 1, 1 ); 
		
		JButton incluirButton = new JButton( "Incluir" );		
		JButton alterarButton = new JButton( "Alterar" );		
		JButton excluirButton = new JButton( "Excluir" );		
		JButton imprimirButton = new JButton( "Imprimir" );		
		JButton limparButton = new JButton( "Limpar" );				
		
		incluirButton.addActionListener( actionIncluir );
		alterarButton.addActionListener( actionAlterar );
		excluirButton.addActionListener( actionExcluir );
		imprimirButton.addActionListener( actionImprimir );
		limparButton.addActionListener( actionLimpar );				
		
		incluirButton.setMnemonic('I');		
		alterarButton.setMnemonic('A');		
		excluirButton.setMnemonic('E');		
		imprimirButton.setMnemonic('p');		
		limparButton.setMnemonic('L');				

		JPanel controlDEPanel = new JPanel( new FlowLayout() );
		
		controlDEPanel.add( incluirButton );				
		controlDEPanel.add( alterarButton );				
		controlDEPanel.add( excluirButton );				
		controlDEPanel.add( imprimirButton );		
		controlDEPanel.add( limparButton );						
		
		model = new TabDEModel();				
		
		deTable = new JTable(); 
		deTable.setModel( model );
		deTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		deTable.addMouseListener( new MouseAdapter()  {
			public void mouseClicked( MouseEvent e ) {
				int count = deTable.getSelectedRowCount();
				if ( count > 0 ) {
                    inputTabDEPanelRefresh();
                }					
			}
		});
		
		JScrollPane scrollpane = new JScrollPane( deTable );
		JPanel deDisplayPanel = new JPanel( new BorderLayout() ) ;
		deDisplayPanel.add( scrollpane );

		tabDEPanel.add( inputTabDEPanel, BorderLayout.NORTH );
		tabDEPanel.add( deDisplayPanel, BorderLayout.CENTER );		
		tabDEPanel.add( controlDEPanel, BorderLayout.SOUTH );		
		
		return tabDEPanel;	
		 
	}

	private void initializeActionEvents() {
		
		actionIncluir =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionIncluir );
			}
		};	

		actionAlterar =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionAlterar );
			}
		};	

		actionExcluir =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionExcluir );
			}
		};	

		actionImprimir =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionImprimir );
			}
		};	

		actionLimpar = new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionLimpar );
			}
		};
	}

	private void actionChanged( Action actionChanged )  {
		
		if ( actionChanged == actionIncluir ) {

			int ano = Integer.parseInt( anoReferenciaTxt.getText() );
			int mes = Integer.parseInt( mesReferenciaTxt.getText() );
            String value = SEPConverter.adapterCurrencyFrmt( movimentoEscrituradoTxt.getText()  );			
			movimentoEscriturado = Double.parseDouble( value );
//			updateTabelaAuxiliar(mes, 3);
			String query = "SELECT * FROM tabAuxiliarDE ";
	    	query += " WHERE numeroOSDE = '" + os + "'";
	    	query += " AND cmcEmpresaDE = '" + cmc + "'";
	    	query += " AND anoReferenciaDE = " + ano;
	    	query += " AND mesReferenciaDE = " + mes;	    

			try {
		   		PreparedStatement stmt = c.prepareStatement(query);
				ResultSet rs = (ResultSet)stmt.executeQuery();
				boolean existe = rs.next();
				rs.close();
	    		stmt.close();

				if ( existe ) {
					updateRecord();
	    		}
	    		else {
	    			insertRecord();
	    		}
   	    	}
	    	catch(SQLException sqle)
	    	{
		  		System.out.println( sqle.toString() );		  
	    	}
			loadDETable();	    	
		}

		else if ( actionChanged == actionAlterar ) {
            String value = SEPConverter.adapterCurrencyFrmt( movimentoEscrituradoTxt.getText()  );			
			movimentoEscriturado = Double.parseDouble( value );
			updateRecord();
			loadDETable();
		}

		else if ( actionChanged == actionExcluir ) {
			deleteRecord();
			loadDETable();
		}
		
		else if ( actionChanged == actionImprimir ) {
			gerarTabelaAuxiliar();
			assembleReport();
		}
		
//		else if ( actionChanged == actionGerar ) {
//			gerarTabelaAuxiliar();
//		}
		
		else if ( actionChanged == actionLimpar ) {
			limparCampos();
		}
	} 
	
	private void assembleReport() {

	   String resultado, processo = "";
	   String mes = "";
	   String numeroProcessoParcelamento = "";
	   String ano = anoReferenciaTxt.getText();
	   String[] mesReferencia, anoReferencia, movimentoEscriturado, movimentoAjustado, receitaSubstituto, receitaParcelamento, impostoRecolhido, impostoAjustado, impostoSubstituto, impostoParcelamento;
	   int indice = 0;
		double diferenca;
		double[] totais;	   
		
		mesReferencia = new String[12];
		anoReferencia = new String[12];
		movimentoEscriturado = new String[12];
		movimentoAjustado = new String[12];
		receitaSubstituto = new String[12];
		receitaParcelamento = new String[12];
		impostoRecolhido = new String[12];
		impostoAjustado = new String[12];
		impostoSubstituto = new String[12];
		impostoParcelamento = new String[12];
		totais = new double[5];
		
	   	String query = "SELECT * FROM tabAuxiliarDE WHERE numeroOSDE = '" + os;
	   	query += "' AND cmcEmpresaDE = '" + cmc + "' AND anoReferenciaDE = '" + ano + "'";
	   	query += " ORDER BY anoreferenciaDE, mesreferenciaDE";

		String fileName;
	   		
		String texto  = "Digite quais vias para impressão :\n";
		texto += "     1=1a        |        2=2a        |       3=3a\n";
		texto += "12=1a e 2a  |  13=1a e 3a  |  23=2a e 3a\n";
		texto += "123=1a, 2a e 3a"; 
	   
		String vias = JOptionPane.showInputDialog ( texto );

	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   

	      	while (rs.next()) {
				mesReferencia [indice] = rs.getString(3);
				anoReferencia [indice] = rs.getString(4);
				movimentoEscriturado [indice] = rs.getString( 5 );
				movimentoAjustado [indice] = rs.getString( 6 );
				receitaSubstituto [indice] = rs.getString( 7 );
				receitaParcelamento [indice] = rs.getString( 8 );
				impostoRecolhido [indice] = rs.getString( 9 );
				impostoAjustado [indice] = rs.getString( 10 );
				impostoSubstituto [indice] = rs.getString( 11 );
				impostoParcelamento [indice] = rs.getString( 12 );

      			if (!(rs.getString(13).equals(""))) {
      				if (!(processo.equals( rs.getString(13)))) {
      					processo = rs.getString(13);
      					numeroProcessoParcelamento += processo + ", ";
      				}
      			}

				indice = indice + 1;
		    }
		    rs.close();
	        stmt.close();
	        
	        if ( numeroProcessoParcelamento.equals("") ) {
	        	numeroProcessoParcelamento = "__________";
	        }
	        
			for ( int i = 0; i < vias.length(); i++ ) {
				char via = vias.charAt( i );
				
				fileName = "c:\\sep\\html\\" + cmc + "-" + razaoSocial + "\\DE-";
				fileName += ano + "-Via-" + via + ".htm";
	        
				resultado = "";
				resultado += "<html><head><title>Demonstrativo Econômico</title></head>";
				resultado +="<body><table border=1 cellspacing=0 cellpadding=0 width=100%>";
    			resultado +="<tr valign=top align=center>";
        		resultado +="<td width=25%><img src=../brasao.gif align=middle>PMT<br>Secretaria Municipal<br>de Finanças</td>";
        		resultado +="<td width=50%>Demonstrativo<br>Econômico (" + ano + ")</td>";
        		resultado +="<td width=25%>Protocolo</td></tr></table>";
				resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
    			resultado +="<td colspan=6 width=100% align=center><font size=2 face=Times New Roman>Identificação do Contribuinte</td></tr>";
    			resultado +="<tr><td width=45%><font size=2 face=Times New Roman>Razão Social<br>" + razaoSocial + "</td>";
				resultado +="<td width=20%><font size=2 face=Times New Roman>CMC No.<br>" + SEPConverter.insertMaskCMC ( cmc ) + "</td>";
        		resultado +="<td width=25%><font size=2 face=Times New Roman>CNPJ/CPF<br>" + SEPConverter.insertMaskCNPJ ( cnpj ) + "</td>";
        		resultado +="<td width=5% align=center><font size=2 face=Times New Roman>Folha<br>" + qtdeFolhas() + "</td>";
        		resultado +="<td width=5% align=center><font size=2 face=Times New Roman>Via<br>" + via + "a.</td>";
        		resultado +="</tr></table>";
				resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
      			resultado +="<td colspan=7 width=100% align=center><font size=2 face=Times New Roman>Valores das Operações</td>";
    			resultado +="</tr><tr><td colspan=2 width=10% align=center><font size=2 face=Times New Roman>Período</td>";
      			resultado +="<td width=20% align=center><font size=2 face=Times New Roman>Movimento Econômico Escriturado (I)</td>";
      			resultado +="<td width=20% align=center><font size=2 face=Times New Roman>Movimento Econômico Ajustado pelo Fisco (II)</td>";
      			resultado +="<td rowspan=2 width=17% align=center><font size=2 face=Times New Roman>Receita Referente ao Substituto Tributário (III)</td>";
      			resultado +="<td rowspan=2 width=17% align=center><font size=2 face=Times New Roman>Receita Própria Confessada com Parcelamento (IV)</td>";
      			resultado +="<td width=16% align=center rowspan=2><font size=2 face=Times New Roman>Diferença<p>(II) - (I)</td>";
    			resultado +="</tr><tr><td width=5% align=center><font size=2 face=Times New Roman>Mês</td>";
      			resultado +="<td width=5% align=center><font size=2 face=Times New Roman>Ano</td>";
      			resultado +="<td width=10% align=center><font size=2 face=Times New Roman>Receita Própria</td>";
      			resultado +="<td width=10% align=center><font size=2 face=Times New Roman>Receita Própria</td></tr>";

				for ( indice = 0; indice <= 4; indice = indice + 1 ) {
					totais[indice] = 0;
				}
      		
				for ( indice = 0; indice <= 11; indice = indice + 1 ) {
					mes = String.valueOf( indice + 1 );
					if ( indice < 9 ) {
						mes = "0" + mes;
					}

					diferenca = Double.parseDouble( movimentoAjustado [indice] ) - Double.parseDouble( movimentoEscriturado [indice] );
    				resultado +="<tr><td width=5% align=center><font size=2 face=Times New Roman>" + mes + "</td>";
					resultado +="<td width=5% align=center><font size=2 face=Times New Roman>"  + ano + "</td>";
      				resultado +="<td width=20% align=right><font size=2 face=Times New Roman>" + SEPConverter.getFmtCurrency( Double.parseDouble( movimentoEscriturado [indice] ) ) + "</td>";
      				resultado +="<td width=20% align=right><font size=2 face=Times New Roman>" + SEPConverter.getFmtCurrency( Double.parseDouble( movimentoAjustado [indice] ) )    + "</td>";
      				resultado +="<td width=17% align=right><font size=2 face=Times New Roman>" + SEPConverter.getFmtCurrency( Double.parseDouble( receitaSubstituto [indice] ) )    + "</td>";
      				resultado +="<td width=17% align=right><font size=2 face=Times New Roman>" + SEPConverter.getFmtCurrency( Double.parseDouble( receitaParcelamento [indice] )  ) + "</td>";
     				resultado +="<td width=16% align=right><font size=2 face=Times New Roman>" + SEPConverter.getFmtCurrency(  diferenca ) + "</td></tr>";
      			      			
      				totais[0] = totais[0] + Double.parseDouble( movimentoEscriturado [indice] );
					totais[1] = totais[1] + Double.parseDouble( movimentoAjustado [indice] );      			
					totais[2] = totais[2] + Double.parseDouble( receitaSubstituto [indice] );
					totais[3] = totais[3] + Double.parseDouble( receitaParcelamento [indice] );
					totais[4] = totais[4] + diferenca;
				}

    			resultado +="<tr><td colspan=2 width=10% align=center><font size=2 face=Times New Roman>Totais</td>";
     			resultado +="<td width=20% align=right><font size=2 face=Times New Roman>" + SEPConverter.getFmtCurrency( totais[0] ) + "</td>";
    			resultado +="<td width=20% align=right><font size=2 face=Times New Roman>" + SEPConverter.getFmtCurrency( totais[1] ) + "</td>";
     			resultado +="<td width=17% align=right><font size=2 face=Times New Roman>" + SEPConverter.getFmtCurrency( totais[2] ) + "</td>";
     			resultado +="<td width=17% align=right><font size=2 face=Times New Roman>" + SEPConverter.getFmtCurrency( totais[3] ) + "</td>";
     			resultado +="<td width=16% align=right><font size=2 face=Times New Roman>" + SEPConverter.getFmtCurrency( totais[4] ) + "</td>";
    			resultado +="</tr></table>";
				resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    			resultado +="<tr><td colspan=7 width=100% align=center><font size=2 face=HaettenschweilerApuração do Imposto</td>";
    			resultado +="</tr><tr>";
      			resultado +="<td colspan=2 width=10% align=center><font size=2 face=Times New Roman>Período</td>";
      			resultado +="<td rowspan=2 width=20% align=center><font size=2 face=Times New Roman>Imposto Recolhido pelo Prestador de Serviços (I)</td>";
      			resultado +="<td rowspan=2 width=20% align=center><font size=2 face=Times New Roman>Imposto Ajustado pelo Fisco (II)</td>";
      			resultado +="<td rowspan=2 width=17% align=center><font size=2 face=Times New Roman>Imposto de Responsabilidade do Substituto Tributário (III)</td>";
      			resultado +="<td rowspan=2 width=17% align=center><font size=2 face=Times New Roman>Imposto Parcelamento ISS Confessado (IV)</td>";
      			resultado +="<td rowspan=2 width=16% align=center><font size=2 face=Times New Roman>Imposto Devido pelo Prestador de Serviços Diferença (II) - [(I) + (III) + (IV)]</td>";
    			resultado +="</tr><tr><td width=5% align=center><font size=2 face=Times New Roman>Mês</td>";
      			resultado +="<td width=5% align=center><font size=2 face=Times New Roman>Ano</td></tr><tr>";
			
				for ( indice = 0; indice <= 4; indice = indice + 1 ) {
					totais[indice] = 0;
				}

				for ( indice = 0; indice <= 11; indice = indice + 1 ) {
					mes = String.valueOf( indice + 1 );
					if ( indice < 9 ) {
						mes = "0" + mes;
					}
				
					diferenca = Double.parseDouble( impostoAjustado [indice] ) - ( Double.parseDouble( impostoRecolhido [indice] ) + Double.parseDouble( impostoSubstituto [indice] ) + Double.parseDouble( impostoParcelamento [indice] ) );
    				resultado +="<tr><td width=5% align=center><font size=2 face=Times New Roman>" + mes + "</td>";
					resultado +="<td width=5% align=center><font size=2 face=Times New Roman>"  + ano + "</td>";
      				resultado +="<td width=20% align=right><font size=2 face=Times New Roman>" + SEPConverter.getFmtCurrency( Double.parseDouble(impostoRecolhido [indice] ) ) + "</td>";
      				resultado +="<td width=20% align=right><font size=2 face=Times New Roman>" + SEPConverter.getFmtCurrency( Double.parseDouble(impostoAjustado [indice] ) )  + "</td>";
      				resultado +="<td width=17% align=right><font size=2 face=Times New Roman>" + SEPConverter.getFmtCurrency( Double.parseDouble(impostoSubstituto [indice] ) ) + "</td>";
      				resultado +="<td width=17% align=right><font size=2 face=Times New Roman>" + SEPConverter.getFmtCurrency( Double.parseDouble(impostoParcelamento [indice]) )+ "</td>";
     				resultado +="<td width=16% align=right><font size=2 face=Times New Roman>" + SEPConverter.getFmtCurrency( diferenca ) + "</td></tr>";
      			
      				totais[0] = totais[0] + Double.parseDouble( impostoRecolhido [indice] );
					totais[1] = totais[1] + Double.parseDouble( impostoAjustado [indice] );      			
					totais[2] = totais[2] + Double.parseDouble( impostoSubstituto [indice] );
					totais[3] = totais[3] + Double.parseDouble( impostoParcelamento [indice] );
					totais[4] = totais[4] + diferenca;
				}

    			resultado +="<tr><td colspan=2 width=10% align=center><font size=2 face=Times New Roman>Totais</td>";
    			resultado +="<td width=20% align=right><font size=2 face=Times New Roman>" + SEPConverter.getFmtCurrency( totais[0] ) + "</td>";
     			resultado +="<td width=20% align=right><font size=2 face=Times New Roman>" + SEPConverter.getFmtCurrency( totais[1] ) + "</td>";
     			resultado +="<td width=17% align=right><font size=2 face=Times New Roman>" + SEPConverter.getFmtCurrency( totais[2] ) + "</td>";
     			resultado +="<td width=17% align=right><font size=2 face=Times New Roman>" + SEPConverter.getFmtCurrency( totais[3] ) + "</td>";
     			resultado +="<td width=16% align=right><font size=2 face=Times New Roman>" + SEPConverter.getFmtCurrency( totais[4] ) + "</td>";
   				resultado +="</tr></table>";
				resultado +="<font size=2 face=Times New Roman>Observações";
				resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
      			resultado +="<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>";
      			resultado +="<td><p align=justify><font size=2 face=Times New Roman>1. O ISSQN de responsabilidade do substituto tributário discriminado na coluna III fica condicionado à fiscalização para posterior homologação.</td>";
    			resultado +="</tr><tr><td>&nbsp;</td>";
      			resultado +="<td><p align=justify><font size=2 face=Times New Roman>2. O ISSQN resultante do parcelamento de confissão de débito - Processo(s) nº " + numeroProcessoParcelamento;
      			resultado +=" discriminado na coluna IV, não foi considerado na composição do ISSQN devido pelo prestador do serviço, no presente levantamento fiscal.</td>";
    			resultado +="</tr></table>";
				resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    			resultado +="<tr><td width=20%><font size=2 face=Times New Roman>Data Emissão<br>&nbsp;</td>";
      			resultado +="<td width=40%><font size=2 face=Times New Roman>Fiscal 1<br>&nbsp;</td>";
      			resultado +="<td width=20%><font size=2 face=Times New Roman>Data Visto<br>&nbsp;</td>";
      			resultado +="<td width=20%><font size=2 face=Times New Roman>Coordenador<br>&nbsp;</td></tr><tr>";
      			resultado +="<td width=20%><font size=2 face=Times New Roman><br>____/____/______</td>";
      			resultado +="<td width=40%><font size=2 face=Times New Roman>Fiscal 2<br>&nbsp;</td>";
      			resultado +="<td width=20%><font size=2 face=Times New Roman><br>____/____/______</td>";
      			resultado +="<td width=20%><font size=2 face=Times New Roman><br>&nbsp;</td></tr></table></body></html>";

       	    	File f = new File( fileName );
	        	FileWriter fw = new FileWriter( f );
	        	BufferedWriter bw = new BufferedWriter( fw );
	        	bw.write( resultado );	        
	        	bw.flush();
	        	bw.close();

				try {
					PrintReport.printReport( fileName );
				}
				catch(IOException ioe) {
					System.out.println( ioe.toString() );
				}
			}
	   }
	   catch(SQLException sqle)
	   {
	      sqle.printStackTrace();
	   }
	   catch ( IOException ioe ) {
	   	  ioe.printStackTrace();
	   }
	}


	private void readTabelaParcelamento () {

		int mes ;
		
		String anoReferencia = anoReferenciaTxt.getText();
	   String query = "SELECT * FROM tabparcelamento";
	   query += " WHERE osparcelamento = '" + os + "'";
	   query += " AND cmcparcelamento = '" + cmc + "'";
	   query += " AND anoparcelamento = '" + anoReferencia + "'";
	   query += " ORDER BY mesparcelamento";
	
	   try
	   {

		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
			
	      	while ( rs.next() ) {
	      		
				processoParcelamento = rs.getString ( 3 );	      		
				mes = Integer.parseInt ( rs.getString( 4 ) );
				valorParcelamento = Double.parseDouble( SEPConverter.adapterCurrencyFrmt( rs.getString( 6 ) ) );
				impostoParcelamento = Double.parseDouble( SEPConverter.adapterCurrencyFrmt( rs.getString( 7 ) ) );

				updateTabelaAuxiliar( mes, 1 );
			}
		}
	   catch(SQLException sqle)
	   {
	      sqle.printStackTrace();
	   }
	}	
	
	private void readTabelaDATM () {

		int mes;
	
		String anoReferencia = anoReferenciaTxt.getText();
	   String query = "SELECT * FROM tabDATM";
	   query += " WHERE osdatm = '" + os + "'";
	   query += " AND cmcdatm = '" + cmc + "'";
	   query += " AND anopagdatm = '" + anoReferencia + "'";
	   query += " ORDER BY mespagdatm";
	   
	   try
	   {

		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
			
	      	while ( rs.next() ) {
	      		
				mes = Integer.parseInt( rs.getString( 3 ) );
				impostoRecolhido = Double.parseDouble( SEPConverter.adapterCurrencyFrmt( rs.getString( 6 ) ) );
				updateTabelaAuxiliar( mes, 2 );
			}
		}
	   catch(SQLException sqle)
	   {
	      sqle.printStackTrace();
	   }
	}	

	private void updateTabelaAuxiliar (int mes, int opcao) {
		
		int ano = Integer.parseInt( anoReferenciaTxt.getText() );
				
		String query = "UPDATE tabAuxiliarDE ";

		if ( opcao == 1 )  {  // parcelamento
			query += "SET receitaParcelamentoDE = " + valorParcelamento;
			query += ", impostoParcelamentoDE   = " + impostoParcelamento;
			query += ", numeroProcessoParcelamentoDE = '" + processoParcelamento + "'";
		}
		else if ( opcao == 2 ) {  // DATM
			query += "SET impostoRecolhidoDE = " + impostoRecolhido;
		}
		else if ( opcao == 3 ) {  // escriturado
			query += "SET movimentoEscrituradoDE = " + movimentoEscriturado;
		}
		else if ( opcao == 4 ) {  // mapas
			query += "SET movimentoAjustadoDE = " + movimentoAjustado;
			query += ", receitaSubstitutoDE = " + receitaSubstituto;			
			query += ", impostoAjustadoDE = " + impostoAjustado;			
			query += ", impostoSubstitutoDE = " + impostoSubstituto;			
		}
	    
	    query += " WHERE numeroOSDE = '" + os + "'";
	    query += " AND cmcEmpresaDE = '" + cmc + "'";
	    query += " AND anoReferenciaDE = " + ano;
	    query += " AND mesReferenciaDE = " + mes;	    
		
		try {
		   	PreparedStatement stmt = c.prepareStatement(query);
			stmt.executeUpdate();
//			c.commit();
	    	stmt.close();
   	    }
	    catch(SQLException sqle)
	    {
		  System.out.println( sqle.toString() );		  
	    }		
	}

	private void deleteTabelaAuxiliar () {
				
		int ano = Integer.parseInt( anoReferenciaTxt.getText() );				
		
		String query = "DELETE FROM tabAuxiliarDE";
	    query += " WHERE numeroOSDE = '" + os + "'";
    	query += " AND cmcEmpresaDE = '" + cmc + "'";
    	query += " AND anoReferenciaDE = " + ano;
		
		try {
		   	PreparedStatement stmt = c.prepareStatement(query);
			stmt.executeUpdate();
//			c.commit();
	    	stmt.close();
   	    }
	    catch(SQLException sqle)
	    {
		  System.out.println( sqle.toString() );		  
	    }		
	}
	
	private void limparCampos() {
		
		anoReferenciaTxt.setText( "" );
		mesReferenciaTxt.setText( "" );
		movimentoEscrituradoTxt.setText( "" );		
		
		return;
	}	


	private void addComponent( Component c, int row, int column, int width, int height) {
		gbConstraints.gridx = column;
		gbConstraints.gridy = row;
		gbConstraints.gridwidth = width;
		gbConstraints.gridheight = height;
		gbLayout.setConstraints( c, gbConstraints );
		inputTabDEPanel.add( c );	
	}

	private void gerarTabelaAuxiliar () {
		
		int m;
		int ano = Integer.parseInt( anoReferenciaTxt.getText() );
		double zero = 0;
		String nulo = "''";
		
//		deleteTabelaAuxiliar();
				
		for ( m = 1; m <= 12; m = m + 1 ) {
			
			String query = "SELECT * FROM tabAuxiliarDE ";
	    	query += " WHERE numeroOSDE = '" + os + "'";
	    	query += " AND cmcEmpresaDE = '" + cmc + "'";
	    	query += " AND anoReferenciaDE = " + ano;
	    	query += " AND mesReferenciaDE = " + m;	    

		
			try {
			
		    	PreparedStatement stmt = c.prepareStatement(query);
				ResultSet rs = (ResultSet)stmt.executeQuery();
				if (!( rs.next() )) {
					String cmd = "INSERT INTO tabAuxiliarDE VALUES ('" + cmc + "', '" + os + "',";
					cmd += m + "," + ano + ",0,0,0,0,0,0,0,0,'')";
					
					try {
		   				PreparedStatement stmt1 = c.prepareStatement(cmd);
						stmt1.executeUpdate();
//						c.commit();
	    				stmt.close();
   	    			}
	    			catch(SQLException sqle)
	    			{
		  				System.out.println( sqle.toString() );		  
	    			}		
	    		}
				rs.close();
	    		stmt.close();
   	    	}
	    	catch(SQLException sqle)
	    	{
		  		System.out.println( sqle.toString() );		  
	    	}		
			

		}	    
		
		readTabelaParcelamento();
		
		readTabelaDATM();
		
		gerarTabelaAgrupada("geral");
		
		gerarTabelaAgrupada("construcao");
		
		gerarTabelaAgrupada("ensino");
	}

	private void gerarTabelaAgrupada ( String tabela ) {

		deleteTabelaAgrupada();
		
		double valor1 = 0, valor2 = 0, valor3 = 0, valor4 = 0, aliquota = 0;
		String anoReferencia = anoReferenciaTxt.getText();
		String dataInicial = anoReferenciaTxt.getText() + "/01";
		String dataFinal   = anoReferenciaTxt.getText() + "/12";
		
		if (tabela.equals("geral")) {  // updateTabelaAuxiliar(m, 4);

	   		String query = "SELECT * FROM tabmapageral";
	   		query += " WHERE osmapageral = '" + os + "'";
	   		query += " AND cmcmapageral = '" + cmc + "'";
	   		query += " AND datacompetenciamapageral >= '" + dataInicial + "'";
	   		query += " AND datacompetenciamapageral <= '" + dataFinal + "'";
	   		query += " ORDER BY datacompetenciamapageral";
		
	   		try
	   		{
		
		    	PreparedStatement stmt = c.prepareStatement(query);
				ResultSet rs = (ResultSet)stmt.executeQuery();   
			
				while ( rs.next() ) {
				
					valor1 = Double.parseDouble( SEPConverter.adapterCurrencyFrmt( rs.getString(8) ) );
					valor2 = Double.parseDouble( SEPConverter.adapterCurrencyFrmt( rs.getString(9) ) );
					valor3 = Double.parseDouble( SEPConverter.adapterCurrencyFrmt( rs.getString(10)) );
					valor4 = Double.parseDouble( SEPConverter.adapterCurrencyFrmt( rs.getString(11)) );

					String data = rs.getString( 13 ) + "/01";
				
					String cmd = "INSERT INTO tabagrupada VALUES ('" + data + "',";
					cmd += valor1 + "," + valor2 + "," +  valor3 + "," + valor4 + ")";
				
		   			PreparedStatement stmt1 = c.prepareStatement( cmd );
					stmt1.executeUpdate();
//					c.commit();
	    			stmt1.close();
				}  // while
			} // try
	    	catch(SQLException sqle)
	    	{
		  		System.out.println( sqle.toString() );		  
	    	} // catch
	    } // if
	    else if (tabela.equals("construcao")) {  // updateTabelaAuxiliar(m, 5);

	   		String query = "SELECT * FROM tabmapaConstrucao";
	   		query += " WHERE osmapaConstrucao = '" + os + "'";
	   		query += " AND cmcmapaConstrucao = '" + cmc + "'";
	   		query += " AND datacompetenciamapaconstrucao >= '" + dataInicial + "'";
	   		query += " AND datacompetenciamapaconstrucao <= '" + dataFinal + "'";
	   		query += " ORDER BY datacompetenciamapaconstrucao";

	   		try
	   		{
		
		    	PreparedStatement stmt = c.prepareStatement(query);
				ResultSet rs = (ResultSet)stmt.executeQuery();   
			
				while ( rs.next() ) {
					
    				String item = rs.getString(4);
   					String cmd = "SELECT * FROM tabservicos WHERE codigoServico = '" + item + "'"; 
    				try {
    					PreparedStatement stmt1 = c.prepareStatement( cmd );
						ResultSet rs1 = (ResultSet)stmt1.executeQuery();
						if (rs1.next()) {
							aliquota = Double.parseDouble( rs1.getString(3) );
						} // if
					} // try
	   				catch(SQLException sqle)
	   				{
	      				sqle.printStackTrace();
	   				} // catch
					
				
					valor3 = Double.parseDouble( SEPConverter.adapterCurrencyFrmt( rs.getString(13) ) );
					valor1 = valor3 * ( aliquota/100 );
					String data = rs.getString( 9 ) + "/01";
				
					String cmd1 = "INSERT INTO tabagrupada VALUES ('" + data + "',";
					cmd1 += valor1 + "," + valor2 + "," +  valor3 + "," + valor4 + ")";
				
		   			PreparedStatement stmt1 = c.prepareStatement( cmd1 );
					stmt1.executeUpdate();
//					c.commit();
	    			stmt1.close();
				}  // while
			} // try
	    	catch( SQLException sqle )
	    	{
		  		System.out.println( sqle.toString() );		  
	        } // catch
	 	} // if
	 	
	 	else if (tabela.equals("ensino")) {  // updateTabelaAuxiliar(m, 5);
	 		
	   		String query = "SELECT * FROM tabmapaensino";
	   		query += " WHERE osmapaensino = '" + os + "'";
	   		query += " AND cmcmapaensino = '" + cmc + "'";
	   		query += " AND exerciciomapaensino = '" + anoReferencia + "'";
	   		query += " ORDER BY mesmapaensino";
	   		
	   		try
	   		{
		
		    	PreparedStatement stmt = c.prepareStatement(query);
				ResultSet rs = (ResultSet)stmt.executeQuery();   
			
				while ( rs.next() ) {
				
					valor3 = Double.parseDouble( SEPConverter.adapterCurrencyFrmt( rs.getString(8) ) );
					valor1 = valor3 * 0.03;
					String data = rs.getString( 4 ) + "/" + rs.getString( 5 ) + "/01";
				
					String cmd1 = "INSERT INTO tabagrupada VALUES ('" + data + "',";
					cmd1 += valor1 + "," + valor2 + "," +  valor3 + "," + valor4 + ")";
				
		   			PreparedStatement stmt1 = c.prepareStatement( cmd1 );
					stmt1.executeUpdate();
//					c.commit();
	    			stmt1.close();
				}  //while
			} // try
	    	catch( SQLException sqle )
	    	{
		  		System.out.println( sqle.toString() );		  
	    	} // catch
	 	} // if
	 	
// atualiza tabela auxiliar

	   		String query = "SELECT month( data ), year( data ), sum( valor1 ),";
	   		query += " sum( valor2 ), sum( valor3 ), sum( valor4 )";
	   		query += " FROM tabagrupada";
	   		query += " WHERE year( data ) = '" + anoReferencia + "'";
	   		query += " GROUP BY month( data ), year( data )";
	   		query += " ORDER BY month( data )";
		
	   		try
	   		{
		
		    	PreparedStatement stmt = c.prepareStatement(query);
				ResultSet rs = (ResultSet)stmt.executeQuery();   
			
				while ( rs.next() ) {
					
					impostoAjustado   = Double.parseDouble( rs.getString(3) );
					impostoSubstituto = Double.parseDouble( rs.getString(4) );
					movimentoAjustado = Double.parseDouble( rs.getString(5) );
					receitaSubstituto = Double.parseDouble( rs.getString(6) );
					int mes = Integer.parseInt( rs.getString( 1 ) );
					updateTabelaAuxiliar(mes, 4);
				}			
			}
	    	catch(SQLException sqle)
	    	{
		  		System.out.println( sqle.toString() );		  
	    	}

	}
		
	private void deleteTabelaAgrupada () {
				
		String query = "DELETE FROM tabAgrupada";
		
		try {
		   	PreparedStatement stmt = c.prepareStatement( query );
			stmt.executeUpdate();
//			c.commit();
	    	stmt.close();
   	    }
	    catch(SQLException sqle)
	    {
		  System.out.println( sqle.toString() );		  
	    }		
	}
	
	
	private void loadDETable() {	
	
//		String query = "SELECT anoReferenciaDE, mesReferenciaDE, movimentoEscrituradoDE FROM tabauxiliarDE";
		String query = "SELECT * FROM tabauxiliarDE";		
		query += " WHERE numeroOSDE = '" + os + "' AND cmcEmpresaDE = '" + cmc + "'";
		query += " ORDER BY anoReferenciaDE, mesReferenciaDE";	
		try {
		    
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery( query );
			rows = new HashMap();
			de = new Vector();
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
			 	
			 	String anoRef =  rs.getString( 4 );
			 	String mesRef = rs.getString( 3 );
				String movimentoEscriturado = rs.getString( 5 );
				double dummy = Double.parseDouble( movimentoEscriturado ) ;
				String movimento = SEPConverter.getFmtCurrency( dummy );	    	    
				movimentoEscriturado = movimento;
				
				theRow[ 0 ] = anoRef;
				theRow[ 1 ] = mesRef;
				theRow[ 2 ] = movimentoEscriturado; 			 	
			 	
		 	    key =  (String) rs.getString( 1 ) + (String) rs.getString( 2 );
		 	    key += (String) rs.getString( 4 ) + (String) rs.getString( 3 );
			 	rows.put( new Double(key) , new Integer( bufferRecordPos ) );	 	
			 	de.add( theRow );
			 	bufferRecordPos++;	 	
			 }
			 rs.close();
			 stmt.close();
		}
		catch ( SQLException sqlException ) {
			System.out.println( sqlException.toString());
		}
	}
	
	private class TabDEModel extends AbstractTableModel {
		
		public int getRowCount() {
			return rows.size();
		}
		
		public Object getValueAt( int r , int c ) {			
		String[] theRow = null ;
		    if ( r < de.size() ) {
		    	Object obj = de.get( r );
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
	
	private void inputTabDEPanelRefresh() {   	
    
    	String value;

		int selectedRow = deTable.getSelectedRow();
		value = (String) deTable.getValueAt(selectedRow, 0 );
		anoReferenciaTxt.setText( value );
		value = (String) deTable.getValueAt(selectedRow, 1 );
		mesReferenciaTxt.setText( value );
		value = (String) deTable.getValueAt(selectedRow, 2 );
		movimentoEscrituradoTxt.setText( value );
		revalidate();		
    }	

	private void insertRecord () {
		
		int ano = Integer.parseInt( anoReferenciaTxt.getText() );
		int mes = Integer.parseInt( mesReferenciaTxt.getText() );
				
		String query = "INSERT INTO tabAuxiliarDE";
	    query += " VALUES ('" + cmc + "','" + os + "'," + mes + "," + ano + "," + movimentoEscriturado;
	    query += " ,0, 0, 0, 0, 0, 0, 0, '')";
		
		try {
		   	PreparedStatement stmt = c.prepareStatement(query);
			stmt.executeUpdate();
//			c.commit();
	    	stmt.close();
   	    }
	    catch(SQLException sqle)
	    {
		  System.out.println( sqle.toString() );		  
	    }		
	}

	
	private void deleteRecord () {
		
		int ano = Integer.parseInt( anoReferenciaTxt.getText() );
		int mes = Integer.parseInt( mesReferenciaTxt.getText() );
				
		String query = "DELETE FROM tabAuxiliarDE ";
	    query += " WHERE numeroOSDE = '" + os + "'";
	    query += " AND cmcEmpresaDE = '" + cmc + "'";
	    query += " AND anoReferenciaDE = " + ano;
	    query += " AND mesReferenciaDE = " + mes;	    
		
		try {
		   	PreparedStatement stmt = c.prepareStatement(query);
			stmt.executeUpdate();
//			c.commit();
	    	stmt.close();
   	    }
	    catch(SQLException sqle)
	    {
		  System.out.println( sqle.toString() );		  
	    }		
	}

	private void updateRecord () {
		
		int ano = Integer.parseInt( anoReferenciaTxt.getText() );
		int mes = Integer.parseInt( mesReferenciaTxt.getText() );
				
		String query = "UPDATE tabAuxiliarDE ";
		query += " SET movimentoEscrituradoDE = " + movimentoEscriturado;		
	    query += " WHERE numeroOSDE = '" + os + "'";
	    query += " AND cmcEmpresaDE = '" + cmc + "'";
	    query += " AND anoReferenciaDE = " + ano;
	    query += " AND mesReferenciaDE = " + mes;	    
		
		try {
		   	PreparedStatement stmt = c.prepareStatement(query);
			stmt.executeUpdate();
//			c.commit();
	    	stmt.close();
   	    }
	    catch(SQLException sqle)
	    {
		  System.out.println( sqle.toString() );		  
	    }		
	}


	private String qtdeFolhas() {
		
		int folha = 0;
		String total = "", ano = anoReferenciaTxt.getText();
		
		String query = "SELECT DISTINCT(anoReferenciaDE) FROM tabAuxiliarDE ";
	    query += " WHERE numeroOSDE = '" + os + "'";
	    query += " AND cmcEmpresaDE = '" + cmc + "'";
	    query += " ORDER BY anoReferenciaDE";
		
		try {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();
			while ( rs.next() ) {
				if (ano.equals( rs.getString(1) ) ) {
					folha = rs.getRow() + 1;
				}
			}
			rs.close();
	    	stmt.close();
   	    }
	    catch(SQLException sqle)
	    {
		  System.out.println( sqle.toString() );		  
	    }		
		
		
		query = "SELECT COUNT(DISTINCT(anoReferenciaDE)) AS qtde FROM tabAuxiliarDE ";
	    query += " WHERE numeroOSDE = '" + os + "'";
	    query += " AND cmcEmpresaDE = '" + cmc + "'";
	    query += " ORDER BY anoReferenciaDE";
		
		try {
		    PreparedStatement stmt1 = c.prepareStatement(query);
			ResultSet rs1 = (ResultSet)stmt1.executeQuery();
			if ( rs1.next() ) {
				total = rs1.getString(1);
			}
			rs1.close();
	    	stmt1.close();
   	    }
	    catch(SQLException sqle)
	    {
		  System.out.println( sqle.toString() );		  
	    }		
	    
	    return folha + "/" + total;
	}

		
}

/*
 
SELECT month( dataemissaomapageral ), year( dataemissaomapageral ), sum( seqmapageral )
FROM tabmapageral
WHERE year( dataemissaomapageral ) = '2000'
GROUP BY month( dataemissaomapageral ), year( dataemissaomapageral )
ORDER BY month( dataemissaomapageral )

cmcEmpresaDE
numeroOSDE
mesReferenciaDE
anoReferenciaDE
movimentoEscrituradoDE
movimentoAjustadoDE
receitaSubstitutoDE
receitaParcelamentoDE
impostoRecolhidoDE
impostoAjustadoDE
impostoSubstitutoDE
impostoParcelamentoDE
numeroProcessoParcelamentoDE

Na tabela agrupada
valor1 = impostoAjustado
valor2 = impostoSubstituto
valor3 = movimentoAjustado
valor4 = receitaSubstitutoDE

*/