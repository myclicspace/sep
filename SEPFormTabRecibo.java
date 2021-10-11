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

public class SEPFormTabRecibo extends JPanel {

   	private JTextArea descricaoReciboArea;
	private JScrollPane scrollPaneDescricao;
    JPanel tabReciboPanel, inputTabReciboPanel;
	
	private GridBagLayout gbLayout;
	private	GridBagConstraints gbConstraints;	
	private Connection c;
	
	private Action actionImprimir;
	private Action actionLimpar;

	private String os  = EmpresaAtual.getNumeroOS();
	private String cmc = EmpresaAtual.getCMCEmpresa();
	private String razaoSocial = EmpresaAtual.getRazaoSocialEmpresa();	

	
	public SEPFormTabRecibo( Connection con ) {
		c = con;
		initializeActionEvents();
	}

	public JPanel createTabReciboPanel() {
		
        tabReciboPanel = new JPanel( new BorderLayout() );
		gbLayout = new GridBagLayout();
		inputTabReciboPanel = new JPanel( gbLayout );
		gbConstraints = new GridBagConstraints(); 
		descricaoReciboArea = new JTextArea( "", 23, 73 );//, TextArea.SCROLLBARS_VERTICAL_ONLY );
		descricaoReciboArea.setLineWrap( true );		
		
//		Font fonteCourier = new Font( "Monospaced", Font.PLAIN, 12 );
//		descricaoReciboArea.setFont( fonteCourier );
//		descricaoReciboArea.setAutoscrolls( true );
			
		scrollPaneDescricao = new JScrollPane( descricaoReciboArea );
		scrollPaneDescricao.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		scrollPaneDescricao.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS );
		
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Recibo de Documentos");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabReciboPanel.setBorder( titleBorder );					
		
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.anchor = GridBagConstraints.EAST;
		
		addComponent( scrollPaneDescricao, 0, 0, 1, 1 );
				
		JButton imprimirButton = new JButton( "Imprimir" );		
		JButton limparButton = new JButton( "Limpar" );		
		
		imprimirButton.addActionListener( actionImprimir );				
		limparButton.addActionListener( actionLimpar );				
		
		imprimirButton.setMnemonic('p');		
		limparButton.setMnemonic('L');				
		
		JPanel controlReciboPanel = new JPanel( new FlowLayout() );
		
		controlReciboPanel.add( imprimirButton );		
		controlReciboPanel.add( limparButton );		
		
		tabReciboPanel.add( inputTabReciboPanel, BorderLayout.NORTH );
		tabReciboPanel.add( controlReciboPanel, BorderLayout.SOUTH );		
		
		return tabReciboPanel;	
		 
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
  	   String descricaoRecibo = descricaoReciboArea.getText();
	        
		String texto  = "Digite quais vias para impressão :\n";
		texto += "     1=1a        |        2=2a        |       3=3a\n";
		texto += "12=1a e 2a  |  13=1a e 3a  |  23=2a e 3a\n";
		texto += "123=1a, 2a e 3a"; 
	   
		String vias = JOptionPane.showInputDialog ( texto );

		if ( descricaoRecibo.equals( "" ) ) {
			for ( int i = 1; i < 16; i++ ) { 
				descricaoRecibo = descricaoRecibo + i + ". ______________________________________________<br>";
			}
		}
		
		for ( int i = 0; i < vias.length(); i++ ) {
			char via = vias.charAt( i );

			fileName = "c:\\sep\\html\\" + cmc + "-" + razaoSocial + "\\Recibo";
			fileName += "-Via-" + via + ".htm";
				        
			resultado = "<html><head><title>Recibo de Documentos</title></head><body>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr valign=top align=center>";
        	resultado +="<td width=25%><img src=../brasao.gif align=middle>PMT<br>Secretaria Municipal<br>de Finanças</td>";
        	resultado +="<td width=50%>Recibo de<br>Documentos</td>";
        	resultado +="<td width=25%>Protocolo</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
    		resultado +="<tr><td width=50%><font size=3 face=Times New Roman>Razão Social<br>" + razaoSocial + "</td>";
        	resultado +="<td width=30%><font size=3 face=Times New Roman>CMC No.<br>" + SEPConverter.insertMaskCMC ( cmc ) + "</td>";
        	resultado +="<td width=10% align=center><font size=3 face=Times New Roman>Via<br>" + via + "a.</td>";
			resultado +="</tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
        	resultado +="<td align=center><font size=3 face=Times New Roman>Documentos</td></tr><tr>";
    		resultado +="<tr><td><p align=justify><font size=3 face=Times New Roman>" + PrintReport.insereBR( descricaoRecibo ) + "</td></tr></table>";
    		resultado +="</tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
        	resultado +="<td><font size=3 face=Times New Roman>&nbsp;<p>Agente Fiscal</td><td>&nbsp;<p>Contribuinte / Responsável</td></tr>";
			resultado +="<tr><td><p align=justify><font size=3 face=Times New Roman>";
			resultado +=PrintReport.paragrafo() + "Recebi documentação acima listada para efeito de Levantamento Fiscal.</p><p align=right>Teresina, ___ de _________ de ____ </p></td>";
        	resultado +="<td><p align=justify><font size=3 face=Times New Roman>";
        	resultado +=PrintReport.paragrafo() + "Recebi do AFTM aqui identificado devolução da documentação acima listada.</p><p align=right>Teresina, ___ de _________ de ____ </p></td></tr></table></body></html>";
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
		} // for
	}

	
	private void limparCampos() {
		
		descricaoReciboArea.setText( "" );
		
		return;
			
	}

	private void addComponent( Component c, int row, int column, int width, int height) {
		gbConstraints.gridx = column;
		gbConstraints.gridy = row;
		gbConstraints.gridwidth = width;
		gbConstraints.gridheight = height;
		gbLayout.setConstraints( c, gbConstraints );
		inputTabReciboPanel.add( c );	
	}
    
}
