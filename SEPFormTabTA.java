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

public class SEPFormTabTA extends JPanel {

   	private JTextArea descricaoTermoApreensaoArea;
	private JScrollPane scrollPaneDescricao;
    JPanel tabTAPanel, inputTabTAPanel;
	
	private GridBagLayout gbLayout;
	private	GridBagConstraints gbConstraints;	
	private Connection c;
	
	private Action actionImprimir;
	private Action actionLimpar;

	private String os  = EmpresaAtual.getNumeroOS();
	private String cmc = EmpresaAtual.getCMCEmpresa();
	private String razaoSocial = EmpresaAtual.getRazaoSocialEmpresa();	
	private String endereco = EmpresaAtual.getEnderecoEmpresa();

	
	public SEPFormTabTA( Connection con ) {
		c = con;
		initializeActionEvents();
	}

	public JPanel createTabTAPanel() {
		
        tabTAPanel = new JPanel( new BorderLayout() );
		gbLayout = new GridBagLayout();
		inputTabTAPanel = new JPanel( gbLayout );
		gbConstraints = new GridBagConstraints();
		
		descricaoTermoApreensaoArea = new JTextArea( "", 23, 73 ); //, TextArea.SCROLLBARS_VERTICAL_ONLY );
		descricaoTermoApreensaoArea.setLineWrap( true );		

//		Font fonteCourier = new Font( "Monospaced", Font.PLAIN, 12 );
//		descricaoTermoApreensaoArea.setFont( fonteCourier );
//		descricaoTermoApreensaoArea.setAutoscrolls( true );
			
		scrollPaneDescricao = new JScrollPane( descricaoTermoApreensaoArea );
		scrollPaneDescricao.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		scrollPaneDescricao.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );//.VERTICAL_SCROLLBAR_AS_NEEDED );
		
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Termo de Apreensão");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabTAPanel.setBorder( titleBorder );					
		
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.anchor = GridBagConstraints.EAST;
		
		addComponent( scrollPaneDescricao, 0, 0, 1, 1 );
				
		JButton imprimirButton = new JButton( "Imprimir" );		
		JButton limparButton = new JButton( "Limpar" );		
		
		imprimirButton.addActionListener( actionImprimir );				
		limparButton.addActionListener( actionLimpar );				
		
		imprimirButton.setMnemonic('p');		
		limparButton.setMnemonic('L');				
		
		JPanel controlTAPanel = new JPanel( new FlowLayout() );
		
		controlTAPanel.add( imprimirButton );		
		controlTAPanel.add( limparButton );		
		
		tabTAPanel.add( inputTabTAPanel, BorderLayout.NORTH );
		tabTAPanel.add( controlTAPanel, BorderLayout.SOUTH );		
		
		return tabTAPanel;	
		 
	}
	

	private void initializeActionEvents() {

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
		
	if ( actionChanged == actionImprimir ) {
			assembleReport();
		}

		else if ( actionChanged == actionLimpar ) {
			limparCampos();
		}
	} 

	private void assembleReport() {

	   String fileName;
	   String resultado;
  	   String descricaoTermoApreensao = descricaoTermoApreensaoArea.getText();

		String texto  = "Digite quais vias para impressão :\n";
		texto += "     1=1a        |        2=2a        |       3=3a\n";
		texto += "12=1a e 2a  |  13=1a e 3a  |  23=2a e 3a\n";
		texto += "123=1a, 2a e 3a"; 
	   
		String vias = JOptionPane.showInputDialog ( texto );
		
		if ( descricaoTermoApreensao.equals( "" ) ) {
			for ( int i = 1; i < 11; i++ ) { 
				descricaoTermoApreensao = descricaoTermoApreensao + i + ". ______________________________________________" + "\n";
			}
		}
		
		for ( int i = 0; i < vias.length(); i++ ) {
			char via = vias.charAt( i );

			fileName = "c:\\sep\\html\\" + cmc + "-" + razaoSocial + "\\TA";
			fileName += "-Via-" + via + ".htm";

			resultado = "<html><head><title>Termo de Apreensão</title></head><body>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr valign=top align=center>";
        	resultado +="<td width=25%><img src=../brasao.gif align=middle>PMT<br>Secretaria Municipal<br>de Finanças</td>";
        	resultado +="<td width=50%>Termo de Apreensão<br>e Fiel Depositário</td>";
        	resultado +="<td width=25%>Protocolo</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
    		resultado +="<tr><font size=3 face=Times New Roman><td width=60%>Razão Social<br>" + razaoSocial + "</td>";			
        	resultado +="<td width=30%><font size=3 face=Times New Roman>CMC No.<br>" + SEPConverter.insertMaskCMC ( cmc ) + "</td>";
        	resultado +="<td width=10% align=center><font size=3 face=Times New Roman>Via<br>" + via + "a.</td>";
        	resultado +="</tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
        	resultado +="<tr><td><font size=3 face=Times New Roman>Endereço<br>" + endereco + "</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
        	resultado +="<td align=center><font size=3 face=Times New Roman>Termo de Apreensão</td></tr>";
    		resultado +="<tr><td><p align=justify><font size=3 face=Times New Roman>";
    		resultado +=PrintReport.paragrafo() + "No exercício das funções de Agente Fiscal de Tributos Municipais e com base no Art. 183 da Lei 1.761/83 ( Código Tributário do Município de Teresina ), aprovado pelo Decreto nº 594/84, para defesa dos interesses da Fazenda Municipal, EFETUAMOS A APREENSÃO dos livros / documentos / equipamentos abaixo especificados :<p align=justify>" + PrintReport.insereBR( descricaoTermoApreensao ) + "</p></td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
        	resultado +="<td align=center><font size=3 face=Times New Roman>Constituição de Fiel Depositário</td></tr>";
    		resultado +="<tr><td><font size=3 face=Times New Roman>" + PrintReport.paragrafo();
    		resultado +="<p align=justify>Constituímos a Prefeitura Municipal de Teresina - Secretaria Municipal de Finanças - Divisão de Fiscalização, como fiel depositário dos bens acima discriminados ficando o mesmo desde já, ciente do disposto no Art. 186, do código de Processo Penal e Art. 902 ,  Parágrafo 1º do Código de Processo Civil.</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td><p align=justify><font size=3 face=Times New Roman>" + PrintReport.paragrafo();
    		resultado +="Para que se produza o efeito legal, lavramos o presente Termo em duas vias de igual teor e forma, que vai assinado por nós, Agentes Fiscais de Tributos Municipais, e pelo representante legal da empresa, com quem fica uma via desta.</p>";
        	resultado +="<p align=right><font size=3 face=Times New Roman>Teresina, ____ de ____________ de _______ </p></td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr valign=bottom align=left>";
        	resultado +="<td align=center><font size=3 face=Times New Roman>&nbsp;<p>&nbsp;<p>Contribuinte/Representante Legal<p>_____________________________<br>";
			resultado +="Nome Completo<p>_____________________________<br>Assinatura</td>";
        	resultado +="<td align=center><font size=3 face=Times New Roman>Fiscal de Tributos</td>";
        	resultado +="<td align=center><font size=3 face=Times New Roman>Coordenador</td></tr></table></body></html>";


			try {
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
			}  // try
	   		catch(IOException ioe) {
		  		System.out.println( ioe.toString() );
	   		}
	  }  // for
}
	
	private void limparCampos() {
		
		descricaoTermoApreensaoArea.setText( "" );
		
		return;
			
	}

	private void addComponent( Component c, int row, int column, int width, int height) {
		gbConstraints.gridx = column;
		gbConstraints.gridy = row;
		gbConstraints.gridwidth = width;
		gbConstraints.gridheight = height;
		gbLayout.setConstraints( c, gbConstraints );
		inputTabTAPanel.add( c );	
	}
    
}
