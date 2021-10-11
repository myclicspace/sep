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
import java.io.*;

public class SEPFormTabTI extends JPanel {

	private Connection c;
	private Action actionImprimir;
	private JTextField periodoFiscalizadoTxt, prazoTxt;
    private JPanel tabTIPanel, inputTabTIPanel;
	
	private GridBagLayout gbLayout;
	private	GridBagConstraints gbConstraints;	
	
	private String os  = EmpresaAtual.getNumeroOS();
	private String cmc = EmpresaAtual.getCMCEmpresa();	   
	private String razaoSocial = EmpresaAtual.getRazaoSocialEmpresa();
	private String endereco = EmpresaAtual.getEnderecoEmpresa();
	private String cnpj = EmpresaAtual.getCNPJEmpresa();
		
	
	public SEPFormTabTI( Connection con ) {
		c = con;
		initializeActionEvents();
	}

	public JPanel createTabTIPanel() {
		
        tabTIPanel = new JPanel( new BorderLayout() );
		gbLayout = new GridBagLayout();
		inputTabTIPanel = new JPanel( gbLayout );
		gbConstraints = new GridBagConstraints();
		
		JLabel periodoFiscalizadoLabel = new JLabel("Período a ser fiscalizado");
		periodoFiscalizadoTxt = new JTextField( 15 );
		JLabel prazoLabel = new JLabel("Prazo para entrega de documentos");
		prazoTxt = new JTextField( 15 );
		periodoFiscalizadoTxt.setText( "01/01/____  a  31/12/____" );
		prazoTxt.setText( "48 (quarenta e oito) horas" );
	
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Termo Inicial");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabTIPanel.setBorder( titleBorder );					
		
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.anchor = GridBagConstraints.EAST;
		
		addComponent( periodoFiscalizadoLabel, 1, 0, 1, 1 ); 
		addComponent( periodoFiscalizadoTxt, 1, 1, 1, 1  ); 		
		addComponent( prazoLabel, 2, 0, 1, 1 ); 
		addComponent( prazoTxt, 2, 1, 1, 1 ); 
		
		JButton imprimirButton = new JButton( "Imprimir" );		
		imprimirButton.addActionListener( actionImprimir );
		imprimirButton.setMnemonic('p');		

		JPanel controlTIPanel = new JPanel( new FlowLayout() );

		controlTIPanel.add( imprimirButton );		
		
		tabTIPanel.add( inputTabTIPanel );
		tabTIPanel.add( inputTabTIPanel, BorderLayout.NORTH );
		tabTIPanel.add( controlTIPanel, BorderLayout.SOUTH );		
		
		return tabTIPanel;	
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
	   
	   String fileName;
	   String resultado;
	   String periodo = periodoFiscalizadoTxt.getText();
	   String prazo = prazoTxt.getText();

		String texto  = "Digite quais vias para impressão :\n";
		texto += "     1=1a        |        2=2a        |       3=3a\n";
		texto += "12=1a e 2a  |  13=1a e 3a  |  23=2a e 3a\n";
		texto += "123=1a, 2a e 3a"; 
	   
		String vias = JOptionPane.showInputDialog ( texto );

		try {
		
		for ( int i = 0; i < vias.length(); i++ ) {
			char via = vias.charAt( i );
				
			resultado = "";
			
			fileName =  "c:\\sep\\html\\" + cmc + "-" + razaoSocial + "\\TI-";
			fileName += "Via-" + via + ".htm";
	   	
			resultado +="<html><head><title>Termo Inicial</title></head>";
			resultado +="<body><table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr valign=top align=center>";
        	resultado +="<td width=25%><img src=../brasao.gif align=middle>PMT<br>Secretaria Municipal<br>de Finanças</td>";
        	resultado +="<td width=50%>Termo<br>Inicial</td>";
        	resultado +="<td width=25%>Protocolo</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td width=50%><font size=3 face=Times New Roman>Razão Social<br>"+ razaoSocial +"</td>";
        	resultado +="<td width=30%><font size=3 face=Times New Roman>CNPJ<br>"+ SEPConverter.insertMaskCNPJ( cnpj ) +"</td>";
        	resultado +="<td width=20%><font size=3 face=Times New Roman>CMC<br>"+ SEPConverter.insertMaskCMC( cmc ) +"</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td width=70%><font size=3 face=Times New Roman>Endereço<br>"+ endereco +"</td>";
        	resultado +="<td width=20%><font size=3 face=Times New Roman>Ordem de Serviço<br>"+ os +"</td>";
        	resultado +="<td width=10% align=center><font size=3 face=Times New Roman>Via<br>"+ via +"a.</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td align=center><font size=3 face=Times New Roman>Intimação</td></tr>";
    		resultado +="<tr><td><font size=3 face=Times New Roman><p align=justify>";
    		resultado +=PrintReport.paragrafo();
         	resultado +="No exercício das funções de Agente Fiscal de Tributos Municipais,";
         	resultado +=" demos início, nesta data, a fiscalização do contribuinte acima";
         	resultado +=" identificado, intimando-o a apresentar os livros e documentos";
         	resultado +=" especificados a seguir, no prazo de " + prazo;
         	resultado +=" , observado o disposto no Art. 142 da Lei nº 1.761 de";
         	resultado +=" 26 de dezembro de 1983.";
		 	resultado +="</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
			resultado +="<tr><td align=center><font size=3 face=Times New Roman>Período a ser Fiscalizado: " + periodo + "</td>";
    		resultado +="</tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td align=center><font size=3 face=Times New Roman>Elementos Solicitados</td></tr>";
    		resultado +="<tr><td><font size=3 face=Times New Roman>";
			resultado +="(&nbsp;&nbsp;) DATM - Documentos de Arrecadação de Tributos Municipais;<br>";
			resultado +="(&nbsp;&nbsp;) NFS - Notas Fiscais de Serviços;<br>";
			resultado +="(&nbsp;&nbsp;) LRPS - Livro de Registro de Prestação de Serviços;<br>";
			resultado +="(&nbsp;&nbsp;) Livros Contábeis ( Diário / Razão / Caixa  );<br>";
			resultado +="(&nbsp;&nbsp;) Documentos de Caixa;<br>";
			resultado +="(&nbsp;&nbsp;) Avisos de Créditos e Extratos Bancários;<br>";
			resultado +="(&nbsp;&nbsp;) Declaração de Imposto de Renda;<br>";
			resultado +="(&nbsp;&nbsp;) Contratos de Prestação de Serviços;<br>";
			resultado +="(&nbsp;&nbsp;) Termo Final de Fiscalizações Anteriores;<br>";
			resultado +="(&nbsp;&nbsp;) Contrato Social e Aditivos;<br>";
			resultado +="(&nbsp;&nbsp;) Cartão CGC;<br>";
			resultado +="(&nbsp;&nbsp;) Outros:_______________________________________________________________<br>";
			resultado +="________________________________________________________________________<br>";
			resultado +="________________________________________________________________________<br>";
			resultado +="________________________________________________________________________<br>";
			resultado +="________________________________________________________________________<br>";
			resultado +="</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td><font size=3 face=Times New Roman><p align=justify>";
    		resultado +=PrintReport.paragrafo();
  			resultado +="O contribuinte que sistematicamente se recusar a exibir à fiscalização";
  			resultado +=" livros e documentos fiscais, embaraçar ou procurar iludir, por qualquer";
  			resultado +=" meio, a apuração dos tributos, terá a licença do seu estabelecimento";
  			resultado +=" suspensa ou cassada, sem prejuízo da cominação das penalidades cabíveis";
  			resultado +=" ( art. 107, parágrafo 3º, dec. 594/84 ).</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td><font size=3 face=Times New Roman><p align=justify>";
    		resultado +=PrintReport.paragrafo();
    		resultado +="E para constar e produzir o efeito legal, lavramos";
    		resultado +=" o presente termo em 03 ( três ) vias de igual teor e forma que vai assinado";
    		resultado +=" por nós e pelo Contribuinte ou seu Representante Legal.</p>";
    		resultado +="<p align=right>Teresina, ___ de ____________ de ______  ___:___";
        	resultado +="</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr valign=bottom align=left>";
        	resultado +="<td align=center><font size=3 face=Times New Roman>&nbsp<p>&nbsp<p>Contribuinte/Representante Legal</td>";
        	resultado +="<td align=center><font size=3 face=Times New Roman>Fiscal de Tributos</td>";
        	resultado +="<td align=center><font size=3 face=Times New Roman>Fiscal de Tributos</td>";
    		resultado +="</tr></table></body></html>";
		
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
    } // try
	   catch ( IOException ioe ) {
	   	  ioe.printStackTrace();
	   }
	}
	
	private void addComponent( Component c, int row, int column, int width, int height) {
		gbConstraints.gridx = column;
		gbConstraints.gridy = row;
		gbConstraints.gridwidth = width;
		gbConstraints.gridheight = height;
		gbLayout.setConstraints( c, gbConstraints );
		inputTabTIPanel.add( c );	
	}
}
