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

public class SEPFormTabMC extends JPanel {

	private JTextField anoReferenciaTxt;
    JPanel tabMCPanel, inputTabMCPanel;
	
	private GridBagLayout gbLayout;
	private	GridBagConstraints gbConstraints;	
	private Connection c;
	
	private String[] numeroDoc, dataCompetencia, tomador, descServico, receitaBruta;
	private String[] receitaTributavel, issDevido, aliquota, percentualDeducao, valorDeducao;
	
	private int indice, ind, pagina;
	
	private Action actionImprimir;

	private String os  = EmpresaAtual.getNumeroOS();
	private String cmc = EmpresaAtual.getCMCEmpresa();	   
	private String cnpj = EmpresaAtual.getCNPJEmpresa();	   	
	private String razaoSocial = EmpresaAtual.getRazaoSocialEmpresa();	   

	public SEPFormTabMC( Connection con ) {
		c = con;
		initializeActionEvents();
	}

	public JPanel createTabMCPanel() {
		
        tabMCPanel = new JPanel( new BorderLayout() );
		gbLayout = new GridBagLayout();
		inputTabMCPanel = new JPanel( gbLayout );
		gbConstraints = new GridBagConstraints();
		
		JLabel anoReferenciaLabel = new JLabel("Ano Referência");
		anoReferenciaTxt = new JTextField( 05 );
	
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Mapa Demonstrativo de Apuraçao da Receita");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabMCPanel.setBorder( titleBorder );					
		
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.anchor = GridBagConstraints.EAST;
		
		addComponent( anoReferenciaLabel, 1, 0, 1, 1 ); 
		addComponent( anoReferenciaTxt, 1, 1, 1, 1  ); 		
		
		JButton imprimirButton = new JButton( "Imprimir" );		
		
		imprimirButton.addActionListener( actionImprimir );
		
		imprimirButton.setMnemonic('p');		

		JPanel controlMCPanel = new JPanel( new FlowLayout() );
		
		controlMCPanel.add( imprimirButton );		

		tabMCPanel.add( inputTabMCPanel );

		tabMCPanel.add( inputTabMCPanel, BorderLayout.NORTH );
		tabMCPanel.add( controlMCPanel, BorderLayout.SOUTH );		
		
		return tabMCPanel;	
		 
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
		
		numeroDoc       = new String[20];
		dataCompetencia = new String[20];
		tomador         = new String[20];
		descServico     = new String[20];
		receitaBruta    = new String[20];
		aliquota        = new String[20];
		percentualDeducao = new String[20];
		valorDeducao    = new String[20];
		receitaTributavel = new String[20];
		issDevido       = new String[20];
		
		
	   String query = "SELECT * FROM tabmapaconstrucao WHERE OSmapaconstrucao = '";
	   query += os + "' AND cmcmapaconstrucao = '" + cmc;
	   query += "' AND datacompetenciamapaconstrucao >= '" + ano + "/01";
	   query += "' AND datacompetenciamapaconstrucao <= '" + ano + "/12";
	   query += "' ORDER BY datacompetenciamapaconstrucao, dataemissaomapaconstrucao";

		String texto  = "Digite quais vias para impressão :\n";
		texto += "     1=1a        |        2=2a        |       3=3a\n";
		texto += "12=1a e 2a  |  13=1a e 3a  |  23=2a e 3a\n";
		texto += "123=1a, 2a e 3a"; 
	   
		String vias = JOptionPane.showInputDialog ( texto );
	   
	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
			ind = 0;
			pagina = 0;
	      	while (rs.next()) {

    			String item = rs.getString( 4 );
    			
   				String cmd = "SELECT * FROM tabservicos WHERE codigoServico = '" + item + "'"; 
    			try {
    				PreparedStatement stmt1 = c.prepareStatement( cmd );
					ResultSet rs1 = (ResultSet)stmt1.executeQuery();
					if ( rs1.next() ) {
						aliquota[ind] = rs1.getString( 3 );
					} // if
				} // try
	   			catch(SQLException sqle)
	   			{
	      			sqle.printStackTrace();
	   			} // catch

				numeroDoc[ind] = rs.getString( 12 );
				dataCompetencia[ind] = rs.getString( 9 );
				tomador[ind] = rs.getString( 3 );
				descServico[ind] = rs.getString( 5 );
				receitaBruta[ind] = SEPConverter.adapterCurrencyFrmt( rs.getString( 13 ) );
				receitaTributavel[ind] = SEPConverter.adapterCurrencyFrmt( rs.getString( 17 ) );
				percentualDeducao[ind] = rs.getString( 11 );
				valorDeducao[ind] = SEPConverter.adapterCurrencyFrmt( rs.getString( 14 ) );
				double iss = Double.parseDouble( receitaTributavel[ind] ) * Double.parseDouble( aliquota[ind] ) / 100;
				issDevido[ind] = SEPConverter.adapterCurrencyFrmt( SEPConverter.getFmtCurrency( iss ) );

				ind = ind + 1;

				if ( ind % 20 == 0 && ind != 0 ) {
					pagina = pagina + 1;
					montaPagina( vias );
					ind = 0;
	   			}
					
			} // while
				
		    rs.close();
	        stmt.close();
	        
			if ( ind > 0 ) {
				pagina = pagina + 1;
				montaPagina( vias );
			}
	        
	   } // try
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

		double valorConvertido;
		
		for ( int i = 0; i < vias.length(); i++ ) {
			char via = vias.charAt( i );
			
			fileName = "c:\\sep\\html\\" + cmc + "-" + razaoSocial + "\\MC-";
			fileName += ano + "-" + pagina + "-Via-" + via + ".htm";
			
			resultado ="<html><head><title>Mapa Demonstrativo de Apuração da Receita</title>";
			resultado +="</head><body>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr valign=top align=center>";
        	resultado +="<td width=25%><img src=../brasao.gif align=middle>PMT<br>Secretaria Municipal<br>de Finanças</td>";
        	resultado +="<td width=50%>Mapa Demonstrativo<br>de Apuração da Receita</td>";
        	resultado +="<td width=25%>Protocolo</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td colspan=6 width=100% align=center><font size=2 face=Times New Roman>Identificação do Contribuinte</td>";
    		resultado +="</tr><tr><td width=10%><font size=2 face=Times New Roman>CMC<br>" + SEPConverter.insertMaskCMC( cmc ) + "</td>";
			resultado +="<td width=50%><font size=2 face=Times New Roman>Razão Social<br>" + razaoSocial + "</td>";
			resultado +="<td width=15%><font size=2 face=Times New Roman>CNPJ<br>" + SEPConverter.insertMaskCNPJ( cnpj ) + "</td>";			
			resultado +="<td width=10%><font size=2 face=Times New Roman>Exercício<br>" + ano + "</td>";
			resultado +="<td width=8%><font size=2 face=Times New Roman>Folha<br>" + pagina + "</td>";
			resultado +="<td width=7%><font size=2 face=Times New Roman>Via<br>" + via + "a.</td></tr></table>";

			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
    		resultado +="<td align=center><font size=2 face=Times New Roman>Documento</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>Ano/Mês</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>Tomador do Serviço</td>";
    		resultado +="<td align=center><font size=2 face=Times New Roman>Natureza do Serviço</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>Receita Bruta</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>Alíquota</td>";
      		resultado +="<td align=center colspan=2><font size=2 face=Times New Roman>Deduções</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>Receita Tributável</td>";
      		resultado +="<td align=center><font size=2 face=Times New Roman>ISSQN</td></tr>";
    			        
			for ( indice = 0; indice < ind; indice = indice + 1 ) {
								
    			resultado +="<tr><td width=5% align=center><font size=2 face=Times New Roman>" + numeroDoc[indice] + "</td>";
      			resultado +="<td align=center><font size=2 face=Times New Roman>" + dataCompetencia[indice] + "</td>";
      			resultado +="<td><font size=2 face=Times New Roman>" + tomador[indice] + "</td>";
				resultado +="<td><font size=2 face=Times New Roman>" + descServico[indice] + "</td>";

				valorConvertido = Double.parseDouble( receitaBruta[indice] ) ;
				valorMascarado = SEPConverter.getFmtCurrency( valorConvertido );
      			resultado +="<td align=right><font size=2 face=Times New Roman>" + valorMascarado + "</td>"; // receitaBruta

				resultado +="<td align=center><font size=2 face=Times New Roman>" + aliquota[indice] + "</td>";				
				resultado +="<td align=center><font size=2 face=Times New Roman>" + percentualDeducao[indice] + "</td>";
				
				valorConvertido = Double.parseDouble( valorDeducao[indice] ) ;
				valorMascarado = SEPConverter.getFmtCurrency( valorConvertido );
      			resultado +="<td align=right><font size=2 face=Times New Roman>" + valorMascarado + "</td>"; // valorDeducao
      			
				valorConvertido = Double.parseDouble( receitaTributavel[indice] ) ;
				valorMascarado = SEPConverter.getFmtCurrency( valorConvertido );
      			resultado +="<td align=right><font size=2 face=Times New Roman>" + valorMascarado + "</td>"; // receitaTributavel
				
				valorConvertido = Double.parseDouble( issDevido[indice] ) ;
				valorMascarado = SEPConverter.getFmtCurrency( valorConvertido );
      			resultado +="<td align=right><font size=2 face=Times New Roman>" + valorMascarado + "</td></tr>"; // issDevido
      			
   			}
  
			resultado +="</table><table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td width=10%><font size=2 face=Times New Roman>Data Emissão<br>____/____/______</td>";
			resultado +="<td width=20%><font size=2 face=Times New Roman>Contribuinte<br>&nbsp;</td>";    		
      		resultado +="<td width=20%><font size=2 face=Times New Roman>Fiscal 1<br>&nbsp;</td>";
      		resultado +="<td width=20%><font size=2 face=Times New Roman>Fiscal 2<br>&nbsp;</td>";
      		resultado +="<td width=10%><font size=2 face=Times New Roman>Data Visto<br>____/____/______</td>";
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
		inputTabMCPanel.add( c );	
	}
}



