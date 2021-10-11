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
import java.io.*;

public class SEPFormTabRF extends JPanel {

	private JTextField periodoTxt, receitaTxt, exerciciosTxt, tiTxt, tfTxt, deTxt, noTxt, aiTxt, noaiTxt, taTxt, mrTxt, mpTxt;
   	private JTextArea observacoesArea;
	private JPanel tabRFPanel, inputTabRFPanel;
	
	private GridBagLayout gbLayout;
	private	GridBagConstraints gbConstraints;	
	private Connection c;
	
	private Action actionIncluir;
	private Action actionAlterar;
	private Action actionConsultar;	
	private Action actionImprimir;

	private String os  = EmpresaAtual.getNumeroOS();
	private String cmc = EmpresaAtual.getCMCEmpresa();	   
	private String razaoSocial = EmpresaAtual.getRazaoSocialEmpresa();
	private String cnpj = EmpresaAtual.getCNPJEmpresa();	   		

	private String projeto, periodoFiscalizacao, fiscal;
	
	public SEPFormTabRF( Connection con ) {
		c = con;
		
	try {
			c.setAutoCommit( true );
		}
	   	catch( SQLException sqle ) {
		  	System.out.println( sqle.toString() );
	   	}		
		initializeActionEvents();	
	}

	public JPanel createTabRFPanel() {
		
        tabRFPanel = new JPanel( new BorderLayout() );
		gbLayout = new GridBagLayout();
		inputTabRFPanel = new JPanel( gbLayout );
		gbConstraints = new GridBagConstraints();
		
		JLabel periodoLabel = new JLabel("Período da Fiscalização");
		periodoTxt = new JTextField( 20 );
		JLabel receitaLabel = new JLabel("Receita do Último Exercício Fiscalizado");
		receitaTxt = new JTextField( 20 );
		JLabel exerciciosLabel = new JLabel("Exercícios com Irregularidades");
		exerciciosTxt = new JTextField( 20 );
		JLabel tiLabel = new JLabel("Termo Inicial");
		tiTxt = new JTextField( 05 );
		JLabel tfLabel = new JLabel("Termo Final");
		tfTxt = new JTextField( 05 );
		JLabel deLabel = new JLabel("Demonstrativo Econômico");
		deTxt = new JTextField( 05 );
		JLabel noLabel = new JLabel("Notificação");
		noTxt = new JTextField( 05 );
		JLabel aiLabel = new JLabel("Auto de Infração");
		aiTxt = new JTextField( 05 );
		JLabel noaiLabel = new JLabel("Notificação / Auto de Infração");
		noaiTxt = new JTextField( 05 );
		JLabel taLabel = new JLabel("Termo de Apreensão");
		taTxt = new JTextField( 05 );
		JLabel mrLabel = new JLabel("Mapa de Apuração da Receita Tributável");
		mrTxt = new JTextField( 05 );
		JLabel mpLabel = new JLabel("Mapa de Prestadores");
		mpTxt = new JTextField( 05 );
		JLabel observacoesLabel = new JLabel("Observacoes");
		
		observacoesArea = new JTextArea( "", 06, 73 ); //, TextArea.SCROLLBARS_VERTICAL_ONLY );
		observacoesArea.setLineWrap( true );
		
		JScrollPane scrollPaneObservacao = new JScrollPane( observacoesArea );
		scrollPaneObservacao.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		scrollPaneObservacao.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
		
		
//		Font fonteCourier = new Font( "Monospaced", Font.PLAIN, 12 );
//		observacoesArea.setFont( fonteCourier );
		
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Relatório de Fiscalização");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabRFPanel.setBorder( titleBorder );					
		
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.anchor = GridBagConstraints.EAST;
		
		addComponent( periodoLabel, 0, 0, 1, 1 );
		addComponent( periodoTxt, 0, 1, 1, 1  ); 
		addComponent( receitaLabel, 1, 0, 1, 1 );
		addComponent( receitaTxt, 1, 1, 1, 1  ); 
		addComponent( exerciciosLabel, 2, 0, 1, 1 );
		addComponent( exerciciosTxt, 2, 1, 1, 1  ); 
		addComponent( tiLabel, 3, 0, 1, 1 );		
		addComponent( tiTxt, 3, 1, 1, 1  ); 		
		addComponent( tfLabel, 4, 0, 1, 1 );		
		addComponent( tfTxt, 4, 1, 1, 1  ); 
		addComponent( deLabel, 5, 0, 1, 1 );		
		addComponent( deTxt, 5, 1, 1, 1  ); 
		addComponent( noLabel, 6, 0, 1, 1 );
		addComponent( noTxt, 6, 1, 1, 1  );
		addComponent( aiLabel, 7, 0, 1, 1 );
		addComponent( aiTxt, 7, 1, 1, 1  );
		addComponent( noaiLabel, 8, 0, 1, 1 );
		addComponent( noaiTxt, 8, 1, 1, 1  );
		addComponent( taLabel, 9, 0, 1, 1 );
		addComponent( taTxt, 9, 1, 1, 1  ); 		
		addComponent( mrLabel, 10, 0, 1, 1 );
		addComponent( mrTxt, 10, 1, 1, 1  );
		addComponent( mpLabel, 11, 0, 1, 1 );		
		addComponent( mpTxt, 11, 1, 1, 1  ); 
		addComponent( observacoesLabel, 12, 0, 1, 1 ); 
		//addComponent( observacoesArea, 13, 0, 2, 1 );
		addComponent( scrollPaneObservacao, 13, 0, 2, 1 );
		
		JButton incluirButton = new JButton( "Incluir" );		
		JButton alterarButton = new JButton( "Alterar" );
		JButton consultarButton = new JButton( "Consultar" );		
		JButton imprimirButton = new JButton( "Imprimir" );		
	
		incluirButton.addActionListener( actionIncluir );		
		alterarButton.addActionListener( actionAlterar );
		consultarButton.addActionListener( actionConsultar );				
		imprimirButton.addActionListener( actionImprimir );
		
		incluirButton.setMnemonic('I');
		alterarButton.setMnemonic('A');
		consultarButton.setMnemonic('C');		
		imprimirButton.setMnemonic('p');		

		JPanel controlRFPanel = new JPanel( new FlowLayout() );
		
		controlRFPanel.add( incluirButton );
		controlRFPanel.add( alterarButton );
		controlRFPanel.add( consultarButton );		
		controlRFPanel.add( imprimirButton );		

		tabRFPanel.add( inputTabRFPanel );
				
		tabRFPanel.add( inputTabRFPanel, BorderLayout.NORTH );
		tabRFPanel.add( controlRFPanel, BorderLayout.SOUTH );		
		
		return tabRFPanel;	
		 
	}
	

	private void initializeActionEvents() {

		actionIncluir = new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionIncluir );
			}
		};

		actionAlterar =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionAlterar );
			}
		};	
		
		actionConsultar =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionConsultar );
			}
		};	

		actionImprimir =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionImprimir );
			}
		};	
	}

	private void actionChanged( Action actionChanged )  {
		
		if ( actionChanged == actionIncluir ) {
			insertRF();
		}
		else if ( actionChanged == actionAlterar ) {
			updateRF();
		}
		else if ( actionChanged == actionConsultar ) {
			findRF();
		}
		else if ( actionChanged == actionImprimir ) {
			assembleReport();
		}
	} 
	
	private void findRF() {

	   String query = "SELECT * FROM tabRF2 WHERE numeroOSRF2 = '" + os + "'";
	   query += " AND cmcRF2 = '" + cmc + "'";
	   
	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	      	if (rs.next()) {
				periodoTxt.setText ( rs.getString(3) );
				receitaTxt.setText ( rs.getString(4) );
				exerciciosTxt.setText ( rs.getString(5));
				tiTxt.setText ( rs.getString(6));
				tfTxt.setText ( rs.getString(7));
				deTxt.setText ( rs.getString(8));
				noTxt.setText ( rs.getString(9 ));
				aiTxt.setText ( rs.getString(10));
				noaiTxt.setText ( rs.getString(11));
				taTxt.setText ( rs.getString(12));
				mrTxt.setText ( rs.getString(13));
				mpTxt.setText ( rs.getString(14));
   	   		    observacoesArea.setText( rs.getString(15));
   	   		}
		    rs.close();
	        stmt.close();
	   }
	   catch(SQLException sqle)
	   {
		  System.out.println( sqle.toString());
	   }
	   catch(Exception e)
	   {
		  System.out.println( e.toString());
	   }
	   return;
	}
	
	private void assembleReport() {
	   String fileName;
	   String resultado;
       String periodo, receita, exercicios, ti, tf, de, no, ai, noai, ta, mr, mp, observacoes;	   
	   String query = "SELECT * FROM tabRF2 WHERE numeroOSRF2 = '" + os + "' AND cmcRF2 = '" + cmc +"'";

		String texto  = "Digite quais vias para impressão :\n";
		texto += "     1=1a        |        2=2a        |       3=3a\n";
		texto += "12=1a e 2a  |  13=1a e 3a  |  23=2a e 3a\n";
		texto += "123=1a, 2a e 3a"; 
	   
		String vias = JOptionPane.showInputDialog ( texto );
		
	   	periodo = "";
		receita = "";
		exercicios = "";
		ti = "";
		tf = "";
		de = "";
		no = "";
		ai = "";
		noai = "";
		ta = "";
		mr = "";
		mp = "";
   	   	observacoes = "";

	   try
	   {
		    
		    buscaDados();
		    
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	   
	      	if (rs.next()) {

				periodo = rs.getString(3);
				receita = rs.getString(4);
				exercicios = rs.getString(5);
				ti = rs.getString(6);
				tf = rs.getString(7);
				de = rs.getString(8);
				no = rs.getString(9);
				ai = rs.getString(10);
				noai = rs.getString(11);
				ta = rs.getString(12);
				mr = rs.getString(13);
				mp = rs.getString(14);
   	   		    observacoes = rs.getString(15);

		    }
		    rs.close();
	        stmt.close();

			for ( int i = 0; i < vias.length(); i++ ) {
				char via = vias.charAt( i );

			fileName = "c:\\sep\\html\\" + cmc + "-" + razaoSocial + "\\RF";
			fileName += "-Via-" + via + ".htm";

			resultado ="<html><head><title>Relatório de Fiscalização</title></head><body>";
			resultado+="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado+="<tr valign=top align=center>";
        	resultado+="<td width=25%><img src=../brasao.gif align=middle>PMT<br>Secretaria Municipal<br>de Finanças</td>";
        	resultado+="<td width=50%>Relatório de<br>Fiscalização</td>";
        	resultado+="<td width=25%>Protocolo</td></tr></table>";
			resultado+="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
    		resultado+="<tr><td width=50%><font size=3 face=Times New Roman>Razão Social<br>" + razaoSocial + "</td>";			
        	resultado+="<td width=25%><font size=3 face=Times New Roman>CNPJ/CGC No.<br>" + SEPConverter.insertMaskCNPJ( cnpj ) + "</td>";
        	resultado+="<td width=18%><font size=3 face=Times New Roman>CMC No.<br>" + SEPConverter.insertMaskCMC( cmc ) + "</td>";			
        	resultado+="<td width=7% align=center><font size=3 face=Times New Roman>Via<br>" + via + "a.</td>";
        	resultado+="</td></tr></table>";
			resultado+="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
        	resultado+="<td width=40%><font size=3 face=Times New Roman>Fiscal de Tributos<br>" + fiscal + "</td>";
        	resultado+="<td width=25%><font size=3 face=Times New Roman>Período Fiscalizado<br>" + periodo + "</td>";
        	resultado+="<td width=25%><font size=3 face=Times New Roman>Período da Fiscalização<br>" + periodoFiscalizacao + "</td>";
        	resultado+="<td width=10% align=center><font size=3 face=Times New Roman>Projeto<br>" + projeto + "</td></tr></table>";
        	
			resultado+=montaRotinasQuesitos();
			
			resultado+="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
        	resultado+="<td align=center width=50%><font size=3 face=Times New Roman>Receita Último Exercício Fiscalizado</td>";
			resultado+="<td align=center width=50%><font size=3 face=Times New Roman>Exercícios com Irregularidades</td>";
    		resultado+="</tr><tr><td width=50%><font size=3 face=Times New Roman><p align=center>" + receita + "</td>";
        	resultado+="<td width=50%><p align=center><font size=3 face=Times New Roman>" + exercicios + "</td></tr></table>";
			resultado+="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
        	resultado+="<td align=center colspan=9 width=100%><font size=3 face=Times New Roman>Anexos</td></tr><tr>";
        	resultado+="<td align=center width=11%><font size=3 face=Times New Roman>TI</td>";
        	resultado+="<td align=center width=11%><font size=3 face=Times New Roman>TF</td>";
        	resultado+="<td align=center width=11%><font size=3 face=Times New Roman>DE</td>";
			resultado+="<td align=center width=11%><font size=3 face=Times New Roman>NO</td>";
			resultado+="<td align=center width=11%><font size=3 face=Times New Roman>AI</td>";
			resultado+="<td align=center width=11%><font size=3 face=Times New Roman>NO/AI</td>";
			resultado+="<td align=center width=11%><font size=3 face=Times New Roman>TA</td>";
			resultado+="<td align=center width=11%><font size=3 face=Times New Roman>MR</td>";
        	resultado+="<td align=center width=12%><font size=3 face=Times New Roman>MP</td></tr>";
        	resultado+="<td width=11%><p align=center><font size=3 face=Times New Roman>" + ti + "</td>";
			resultado+="<td width=11%><p align=center><font size=3 face=Times New Roman>" + tf + "</td>";
			resultado+="<td width=11%><p align=center><font size=3 face=Times New Roman>" + de + "</td>";
			resultado+="<td width=11%><p align=center><font size=3 face=Times New Roman>" + no + "</td>";
			resultado+="<td width=11%><p align=center><font size=3 face=Times New Roman>" + ai + "</td>";
			resultado+="<td width=11%><p align=center><font size=3 face=Times New Roman>" + noai + "</td>";
			resultado+="<td width=11%><p align=center><font size=3 face=Times New Roman>" + ta + "</td>";
			resultado+="<td width=11%><p align=center><font size=3 face=Times New Roman>" + mr + "</td>";
			resultado+="<td width=12%><p align=center><font size=3 face=Times New Roman>" + mp + "</td><tr>";
        	resultado+="<td colspan=3 width=33%><p><font size=3 face=Times New Roman>";
			resultado+="TI - Termo de Início<br>TF - Termo Final<br>DE - Demonstrativo Econômico</td>";
        	resultado+="<td colspan=3 width=33%><font size=3 face=Times New Roman><p>";
			resultado+="NO - Notificação<br>AI - Auto de Infração<br>NO/AI - Notificação/Auto de Infração</td>";
        	resultado+="<td colspan=3 width=34%><font size=3 face=Times New Roman><p>";
			resultado+="TA - Termo de Apreensão<br>MR - Mapa de Apuração de Receita Tributável<br>MP - Mapa de Prestadores</td></tr></table>";
			resultado+="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
        	resultado+="<td align=center colspan=2><font size=3 face=Times New Roman>Observações</td></tr><tr>";
        	resultado+="<td><p><font size=3 face=Times New Roman>" + PrintReport.insereBR( observacoes ) + "</td></tr></table>";
			resultado+="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado+="<tr valign=bottom align=left>";
        	resultado+="<td width=40%><font size=3 face=Times New Roman><p>&nbsp<p>&nbsp<p>&nbsp<p>&nbsp</td>";
        	resultado+="<td width=30% align=center><font size=3 face=Times New Roman>Fiscal de Tributos</td>";
        	resultado+="<td width=30% align=center><font size=3 face=Times New Roman>Coordenador</td></tr></table></body></html>";
			
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
		} // for
	        
	   }
	   catch(SQLException sqle)
	   {
	      sqle.printStackTrace();
	   }
	   catch ( IOException ioe ) {
	   	  ioe.printStackTrace();
	   }
}


	private void insertRF() {

	   String query = "INSERT INTO tabRF2 VALUES ('" + os + "','" + cmc + "','";
			query += periodoTxt.getText() + "','";
			query += receitaTxt.getText() + "','";
			query += exerciciosTxt.getText() + "',";
			query += Integer.parseInt( tiTxt.getText() ) + ",";
			query += Integer.parseInt( tfTxt.getText() ) + ",";
			query += Integer.parseInt( deTxt.getText() ) + ",";
			query += Integer.parseInt( noTxt.getText() ) + ",";
			query += Integer.parseInt( aiTxt.getText() ) + ",";
			query += Integer.parseInt( noaiTxt.getText() ) + ",";
			query += Integer.parseInt( taTxt.getText() ) + ",";
			query += Integer.parseInt( mrTxt.getText() ) + ",";
			query += Integer.parseInt( mpTxt.getText() ) + ",'";
   	   		query += observacoesArea.getText() + "')";

	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			stmt.executeUpdate();
	        stmt.close();
	   }
	   catch(SQLException sqle)
	   {
		  System.out.println(sqle.toString());		  
	   }
	   return;
	}
	
	private void updateRF() {

	   String query = "UPDATE tabRF2";
			query += " SET periodoRF2 = '" + periodoTxt.getText() + "',";
			query += " receitaRF2 = '" + receitaTxt.getText() + "',";
			query += " exerciciosRF2 = '" + exerciciosTxt.getText()  + "',";
			query += " tiRF2 = " + Integer.parseInt( tiTxt.getText() ) + ",";
			query += " tfRF2 = " + Integer.parseInt( tfTxt.getText() ) + ",";
			query += " deRF2 = " + Integer.parseInt( deTxt.getText() ) + ",";
			query += " noRF2 = " + Integer.parseInt( noTxt.getText() ) + ",";
			query += " aiRF2 = " + Integer.parseInt( aiTxt.getText() ) + ",";
			query += " noaiRF2 = " + Integer.parseInt( noaiTxt.getText() ) + ",";
			query += " taRF2 = " + Integer.parseInt( taTxt.getText() ) + ",";
			query += " mrRF2 = " + Integer.parseInt( mrTxt.getText() ) + ",";
			query += " mpRF2 = " + Integer.parseInt( mpTxt.getText() ) + ",";
   	   		query += " observacoesRF2 = '" + observacoesArea.getText() + "'";
			query += " WHERE numeroOSRF2 = '" + os + "'";
	   		query += " AND cmcRF2 = '" + cmc + "'";   	   		
	   
	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			stmt.executeUpdate();
	        stmt.close();
	   }
	   catch(SQLException sqle)
	   {
		  System.out.println(sqle.toString());		  
	   }
	   return;
	}

	private String montaRotinasQuesitos() {

	   String resultado = "";
	   
	   String rotina, quesito, rotinaRef, listaQuesitos;	   
	   String query = "SELECT * FROM tabRF WHERE osRF = '" + os + "' AND cmcempresaRF = '" + cmc;
	    	  query += "' ORDER BY rotinaRF, quesitoRF";

		resultado+="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
        resultado+="<td align=center colspan=2><font size=3 face=Times New Roman>Identificação das Rotinas Desenvolvidas</td></tr>";
        resultado+="<tr><td width=15%><font size=3 face=Times New Roman>Código Rotina</td>";
        resultado+="<td width=85%><font size=3 face=Times New Roman>Quesitos Executados</td></tr><tr>";
        
//        resultado+="<td width=15%><br>&nbsp</td>
//        resultado+="<td width=85%><br>&nbsp</td></tr></table>";
//        resultado+="</tr></table>";
	   
	   rotinaRef = "01";
	   listaQuesitos = "";
	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	   
	      	while (rs.next()) {

				rotina = rs.getString(3);
				quesito = rs.getString(4);

				if ( rotinaRef.equals( rotina ) ) {
					listaQuesitos += quesito + "  ";
				}
				else {
        			resultado+="<td width=15%><font size=3 face=Times New Roman>" + rotinaRef + "<br></td>";
        			resultado+="<td width=85%><font size=3 face=Times New Roman>" + listaQuesitos + "<br></td></tr><tr>";
        			rotinaRef = rotina;
	   				listaQuesitos = quesito + "  ";        			
				}
		    }

        	resultado+="<td width=15%><font size=3 face=Times New Roman>" + rotinaRef + "<br></td>";
        	resultado+="<td width=85%><font size=3 face=Times New Roman>" + listaQuesitos + "<br></td></tr><tr>";
		    
		    rs.close();
	        stmt.close();
	   }
	   catch(SQLException sqle)
	   {
		  System.out.println(sqle.toString());		  
	   }
	   
       resultado+="</tr></table>";	   
	   return resultado;
	}

	private void buscaDados() {
	
	   String query = "SELECT DISTINCT * FROM tabos o, tabosfiscal l, tabfiscal f";
	   	      query +=" WHERE o.codigoOS = '" + os + "'";
	   	      query +=" AND o.cmcEmpresaOS = '" + cmc + "'";
	   	      query +=" AND l.matrOSFiscal = f.codigoFiscal";
	   	      query +=" AND o.codigoOS = l.codigoOSfiscal";

	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	   
	   		fiscal = "";
	   		
	      	while (rs.next()) {
	      		
	      		projeto = rs.getString( 2 );
				periodoFiscalizacao = SEPConverter.converteFrmtDataFromMySQL ( rs.getString(5) );
				periodoFiscalizacao += " a " + SEPConverter.converteFrmtDataFromMySQL ( rs.getString(6) );
				fiscal += rs.getString( 12 ) + "<br>";
		    }

		    rs.close();
	        stmt.close();
	   }
	   catch(SQLException sqle)
	   {
		  System.out.println(sqle.toString());		  
	   }
	}

	private void addComponent( Component c, int row, int column, int width, int height) {
		gbConstraints.gridx = column;
		gbConstraints.gridy = row;
		gbConstraints.gridwidth = width;
		gbConstraints.gridheight = height;
		gbLayout.setConstraints( c, gbConstraints );
		inputTabRFPanel.add( c );	
	}
}
