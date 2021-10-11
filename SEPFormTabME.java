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
//import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
//import java.util.*;
import java.io.*;

public class SEPFormTabME extends JPanel {

	private JTextField anoReferenciaTxt;
    JPanel tabMEPanel, inputTabMEPanel;
	
	private GridBagLayout gbLayout;
	private	GridBagConstraints gbConstraints;	
	private Connection c;
	
	private String[] qtdAlunos, mensalidade, deducoes, cursos;
	private int indice, ind, ind2, pagina;
	private double totalGeral = 0, qal, men, ded;
	
	private Action actionImprimir;

	private String os  = EmpresaAtual.getNumeroOS();
	private String cmc = EmpresaAtual.getCMCEmpresa();	   
	private String razaoSocial = EmpresaAtual.getRazaoSocialEmpresa();	   

	public SEPFormTabME( Connection con ) {
		c = con;
		initializeActionEvents();
	}

	public JPanel createTabMEPanel() {
		
        tabMEPanel = new JPanel( new BorderLayout() );
		gbLayout = new GridBagLayout();
		inputTabMEPanel = new JPanel( gbLayout );
		gbConstraints = new GridBagConstraints();
		
		JLabel anoReferenciaLabel = new JLabel("Ano Referência");
		anoReferenciaTxt = new JTextField( 05 );
	
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Mapa Escolar I");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabMEPanel.setBorder( titleBorder );					
		
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.anchor = GridBagConstraints.EAST;
		
		addComponent( anoReferenciaLabel, 1, 0, 1, 1 ); 
		addComponent( anoReferenciaTxt, 1, 1, 1, 1  ); 		
		
		JButton imprimirButton = new JButton( "Imprimir" );		
		
		imprimirButton.addActionListener( actionImprimir );
		
		imprimirButton.setMnemonic('p');		

		JPanel controlMEPanel = new JPanel( new FlowLayout() );
		
		controlMEPanel.add( imprimirButton );		

		tabMEPanel.add( inputTabMEPanel );

		tabMEPanel.add( inputTabMEPanel, BorderLayout.NORTH );
		tabMEPanel.add( controlMEPanel, BorderLayout.SOUTH );		
		
		return tabMEPanel;	
		 
	}
	

	private void initializeActionEvents() {
		
		actionImprimir =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionImprimir );
			}
		};	
	}

	private void actionChanged( Action actionChanged )  {
		
		if ( actionChanged == actionImprimir ) {
			assembleReport();
		}
	} 
	
	private void assembleReport() {

		String ano = anoReferenciaTxt.getText();
		qtdAlunos = new String[48];
		mensalidade = new String[48];
		deducoes = new String[48];
		cursos = new String[4];
		
	   String query = "SELECT * FROM tabmapaensino WHERE OSmapaensino = '";
	   query += os + "' AND cmcmapaensino = '" + cmc;
	   query += "' AND exerciciomapaensino = '" + ano;
	   query += "' ORDER BY cursomapaensino, exerciciomapaensino, mesmapaensino";

		String texto  = "Digite quais vias para impressão :\n";
		texto += "     1=1a        |        2=2a        |       3=3a\n";
		texto += "12=1a e 2a  |  13=1a e 3a  |  23=2a e 3a\n";
		texto += "123=1a, 2a e 3a"; 
	   
		String vias = JOptionPane.showInputDialog ( texto );

	   for ( indice = 0; indice < 4; indice = indice + 1 ) {
				cursos[indice] = "&nbsp;";
	   }
	   
	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
			ind = 0;
			indice = 0;
			pagina = 0;
	      	while (rs.next()) {
				qtdAlunos [ind] = SEPConverter.adapterCurrencyFrmt( rs.getString( 6 ) );
				mensalidade [ind] = SEPConverter.adapterCurrencyFrmt( rs.getString( 7 ) );					
				deducoes [ind] = SEPConverter.adapterCurrencyFrmt( rs.getString( 9 ) );

      			qal = Double.parseDouble( qtdAlunos [ind] );
      			men = Double.parseDouble( mensalidade [ind] ) ;
      			ded = Double.parseDouble( deducoes [ind] );
				totalGeral += ( qal * men ) - ded;      				
      				
				ind = ind + 1;

				if ( ind % 12 == 0 ) {
					cursos[indice] = rs.getString(3);
					indice = indice + 1;
				}
				
				if ( indice % 4 == 0 && indice != 0 ) {
					pagina = pagina + 1;
					montaPagina( vias );
	   				for ( indice = 0; indice < 4; indice = indice + 1 ) {
						cursos[indice] = "&nbsp;";
					}
					ind = 0;
					indice = 0;
	   			}
					
			}
				
		    rs.close();
	        stmt.close();
	        
			if ( indice > 0 ) {
				pagina = pagina + 1;
				montaPagina( vias );
			}
	        
	   }
	   catch(SQLException sqle)
	   {
	      sqle.printStackTrace();
	   }
}
       
	private void montaPagina( String vias ) {	        

	   String fileName;
	   String resultado;
	   String mes = "";	   
	   String ano = anoReferenciaTxt.getText(), valorMascarado;

		double receitaTributavel, receitaTotal, valorConvertido, issDevido;
		
		for ( int i = 0; i < vias.length(); i++ ) {
			char via = vias.charAt( i );
			
			fileName = "c:\\sep\\html\\" + cmc + "-" + razaoSocial + "\\ME-";
			fileName += ano + "-" + pagina + "-Via-" + via + ".htm";
			
			resultado ="<html><head><title>Mapa de Apuração da Receita Escolar</title>";
			resultado +="</head><body>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr valign=top align=center>";
        	resultado +="<td width=25%><img src=../brasao.gif align=middle>PMT<br>Secretaria Municipal<br>de Finanças</td>";
        	resultado +="<td width=50%>Mapa de Apuração<br>da Receita Escolar</h1></td>";
        	resultado +="<td width=25%>Protocolo</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td colspan=4 width=100% align=center><font size=2 face=Times New Roman>Identificação do Contribuinte</td>";
    		resultado +="</tr><tr><td width=15%><font size=2 face=Times New Roman>CMC<br>" + SEPConverter.insertMaskCMC( cmc ) + "</td>";
			resultado +="<td width=60%><font size=2 face=Times New Roman>Razão Social<br>" + razaoSocial + "</td>";
			resultado +="<td width=15%><font size=2 face=Times New Roman>Exercício<br>" + ano + "</td>";
			resultado +="<td width=10%><font size=2 face=Times New Roman>Via<br>" + via + "a.</td></tr></table>";

			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
    		resultado +="<td align=center><font size=2 face=Times New Roman>Curso</td>";
    		for ( ind2 = 0; ind2 < 4; ind2 = ind2 + 1 ) {
    			resultado +="<td align=center colspan=4><font size=2 face=Times New Roman>" +cursos[ind2]+ "</td>";
      		}
      		
      		resultado +="<td align=center><font size=2 face=Times New Roman>Receita.Total</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>ISS Devido</td>";
    		resultado +="</tr><tr><td align=center><font size=2 face=Times New Roman>Mês</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>No.Alunos</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>Mensalidades</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>Deduções</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>Receita Tributável(1)</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>No.Alunos</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>Mensalidades</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>Deduções</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>Receita Tributável(2)</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>No.Alunos</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>Mensalidades</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>Deduções</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>Receita Tributável(3)</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>No.Alunos</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>Mensalidades</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>Deduções</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>Receita Tributável(4)</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>1+2+3+4</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>&nbsp;<br></td></tr>";
    			        
			for ( indice = 0; indice < 12; indice = indice + 1 ) {
				mes = String.valueOf( indice + 1 );
				if ( indice < 9 ) {
					mes = "0" + mes;
				}

				receitaTotal = 0;
								
				if ( cursos[0].equals("&nbsp;") ) {
    				resultado +="<tr><td width=5% align=center><font size=2 face=Times New Roman>" + mes + "</td>";
      				resultado +="<td align=right><font size=2 face=Times New Roman>&nbsp;</td>";
      				resultado +="<td align=right><font size=2 face=Times New Roman>&nbsp;</td>";
      				resultado +="<td align=right><font size=2 face=Times New Roman>&nbsp;</td>";
      				resultado +="<td align=right><font size=2 face=Times New Roman>&nbsp;</td>";
      			}
      			else {
      				qal = Double.parseDouble( qtdAlunos [indice] );
      				men = Double.parseDouble( mensalidade [indice] ) ;
      				ded = Double.parseDouble( deducoes [indice] );
					receitaTributavel =  qal * men - ded;      				
					receitaTotal += receitaTributavel;
											
    				resultado +="<tr><td width=5% align=center><font size=2 face=Times New Roman>" + mes + "</td>";
      				resultado +="<td align=right><font size=2 face=Times New Roman>" + qtdAlunos [indice] + "</td>";
      				
					valorConvertido = Double.parseDouble( mensalidade[indice] ) ;
					valorMascarado = SEPConverter.getFmtCurrency( valorConvertido );
      				
      				resultado +="<td align=right><font size=2 face=Times New Roman>" + valorMascarado + "</td>"; // mensalidades
      				
					valorConvertido = Double.parseDouble( deducoes[indice] ) ;
					valorMascarado = SEPConverter.getFmtCurrency( valorConvertido );
      				
      				resultado +="<td align=right><font size=2 face=Times New Roman>" + valorMascarado + "</td>"; // deducoes

					valorMascarado = SEPConverter.getFmtCurrency( receitaTributavel );
      				
      				resultado +="<td align=right><font size=2 face=Times New Roman>" + valorMascarado + "</td>"; // receita tributavel
      			}

				if ( cursos[1].equals("&nbsp;") ) {
      				resultado +="<td align=right><font size=2 face=Times New Roman>&nbsp;</td>";
      				resultado +="<td align=right><font size=2 face=Times New Roman>&nbsp;</td>";
      				resultado +="<td align=right><font size=2 face=Times New Roman>&nbsp;</td>";
      				resultado +="<td align=right><font size=2 face=Times New Roman>&nbsp;</td>";
      			}
      			else {
      				qal = Double.parseDouble( qtdAlunos [indice + 12] );
      				men = Double.parseDouble( mensalidade [indice + 12] ) ;
      				ded = Double.parseDouble( deducoes [indice + 12] );
					receitaTributavel =  qal * men - ded;      				      				
					receitaTotal += receitaTributavel;
    				resultado +="<td align=right><font size=2 face=Times New Roman>" + qtdAlunos [indice + 12] + "</td>";
    				
					valorConvertido = Double.parseDouble( mensalidade[indice + 12] ) ;
					valorMascarado = SEPConverter.getFmtCurrency( valorConvertido );
      				
      				resultado +="<td align=right><font size=2 face=Times New Roman>" + valorMascarado + "</td>"; // mensalidades
      				
					valorConvertido = Double.parseDouble( deducoes[indice  + 12] ) ;
					valorMascarado = SEPConverter.getFmtCurrency( valorConvertido );
      				
      				resultado +="<td align=right><font size=2 face=Times New Roman>" + valorMascarado + "</td>"; // deducoes

					valorMascarado = SEPConverter.getFmtCurrency( receitaTributavel );
      				
      				resultado +="<td align=right><font size=2 face=Times New Roman>" + valorMascarado + "</td>"; // receita tributavel
				}
				
				if ( cursos[2].equals("&nbsp;") ) {
      				resultado +="<td align=right><font size=2 face=Times New Roman>&nbsp;</td>";
      				resultado +="<td align=right><font size=2 face=Times New Roman>&nbsp;</td>";
      				resultado +="<td align=right><font size=2 face=Times New Roman>&nbsp;</td>";
      				resultado +="<td align=right><font size=2 face=Times New Roman>&nbsp;</td>";
      			}
      			else {
      				qal = Double.parseDouble( qtdAlunos [indice + 24] );
      				men = Double.parseDouble( mensalidade [indice + 24] ) ;
      				ded = Double.parseDouble( deducoes [indice + 24] );
					receitaTributavel =  qal * men - ded;      				      				
					receitaTotal += receitaTributavel;
      				resultado +="<td align=right><font size=2 face=Times New Roman>" + qtdAlunos [indice + 24] + "</td>";
      				
 					valorConvertido = Double.parseDouble( mensalidade[indice + 24] ) ;
					valorMascarado = SEPConverter.getFmtCurrency( valorConvertido );
      				
      				resultado +="<td align=right><font size=2 face=Times New Roman>" + valorMascarado + "</td>"; // mensalidades
      				
					valorConvertido = Double.parseDouble( deducoes[indice  + 24] ) ;
					valorMascarado = SEPConverter.getFmtCurrency( valorConvertido );
      				
      				resultado +="<td align=right><font size=2 face=Times New Roman>" + valorMascarado + "</td>"; // deducoes

					valorMascarado = SEPConverter.getFmtCurrency( receitaTributavel );
      				
      				resultado +="<td align=right><font size=2 face=Times New Roman>" + valorMascarado + "</td>"; // receita tributavel
      			}

				if ( cursos[3].equals("&nbsp;") ) {
      				resultado +="<td align=right><font size=2 face=Times New Roman>&nbsp;</td>";
      				resultado +="<td align=right><font size=2 face=Times New Roman>&nbsp;</td>";
      				resultado +="<td align=right><font size=2 face=Times New Roman>&nbsp;</td>";
      				resultado +="<td align=right><font size=2 face=Times New Roman>&nbsp;</td>";
      			}
      			else {
      				qal = Double.parseDouble( qtdAlunos [indice + 36] );
      				men = Double.parseDouble( mensalidade [indice + 36] ) ;
      				ded = Double.parseDouble( deducoes [indice + 36] );
					receitaTributavel =  qal * men - ded;      				      				
					receitaTotal += receitaTributavel;
      				resultado +="<td align=right><font size=2 face=Times New Roman>" + qtdAlunos [indice + 36] + "</td>";
      				
					valorConvertido = Double.parseDouble( mensalidade[indice + 36] ) ;
					valorMascarado = SEPConverter.getFmtCurrency( valorConvertido );
      				
      				resultado +="<td align=right><font size=2 face=Times New Roman>" + valorMascarado + "</td>"; // mensalidades
      				
					valorConvertido = Double.parseDouble( deducoes[indice  + 36] ) ;
					valorMascarado = SEPConverter.getFmtCurrency( valorConvertido );
      				
      				resultado +="<td align=right><font size=2 face=Times New Roman>" + valorMascarado + "</td>"; // deducoes

					valorMascarado = SEPConverter.getFmtCurrency( receitaTributavel );
      				
      				resultado +="<td align=right><font size=2 face=Times New Roman>" + valorMascarado + "</td>"; // receita tributavel
      				
      			}
				
				valorMascarado = SEPConverter.getFmtCurrency( receitaTotal );
      			
      			resultado +="<td align=right><font size=2 face=Times New Roman>" + valorMascarado + "</td>";
      			
      			issDevido = receitaTotal * 0.03;
      			
				valorMascarado = SEPConverter.getFmtCurrency( issDevido );
      			
      			resultado +="<td align=right><font size=2 face=Times New Roman>" + valorMascarado + "</td>";
   			
			}

    		resultado +="<tr><td colspan=17 align=center><font size=2 face=Times New Roman>Total Geral</td>";
			
			valorMascarado = SEPConverter.getFmtCurrency( totalGeral );
      		resultado +="<td align=right><font size=2 face=Times New Roman>" + valorMascarado + "</td>";  // total geral
      		
			valorMascarado = SEPConverter.getFmtCurrency( totalGeral * 0.03);
      		resultado +="<td align=right><font size=2 face=Times New Roman>" + valorMascarado + "</td>";  // iss devido total
      		
   			resultado +="</tr></table>";
  
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td width=20%><font size=2 face=Times New Roman>Data Emissão<br>____/____/______</td>";
      		resultado +="<td width=20%><font size=2 face=Times New Roman>Fiscal 1<br>&nbsp;</td>";
      		resultado +="<td width=20%><font size=2 face=Times New Roman>Fiscal 2<br>&nbsp;</td>";
      		resultado +="<td width=20%><font size=2 face=Times New Roman>Data Visto<br>____/____/______</td>";
      		resultado +="<td width=20%><font size=2 face=Times New Roman>Coordenador<br>&nbsp;</td>";
      		resultado +="</tr></table></body></html>";
	               	    
       	    try {
       	    
       	    	File f = new File( fileName );
	        	FileWriter fw = new FileWriter( f );
	        	BufferedWriter bw = new BufferedWriter( fw );
	        	bw.write( resultado );	        
	        	bw.flush();
	        	bw.close();
	        
				PrintReport.printReport( fileName );
			}
			catch(IOException ioe) {
				System.out.println( ioe.toString() );
			}
		}
	}

	private void addComponent( Component c, int row, int column, int width, int height) {
		gbConstraints.gridx = column;
		gbConstraints.gridy = row;
		gbConstraints.gridwidth = width;
		gbConstraints.gridheight = height;
		gbLayout.setConstraints( c, gbConstraints );
		inputTabMEPanel.add( c );	
	}
}
