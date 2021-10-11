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

public class SEPFormTabMP extends JPanel {

    JPanel tabMPPanel, inputTabMPPanel;
	
	private GridBagLayout gbLayout;
	private	GridBagConstraints gbConstraints;	
	private Connection c;
	
	private Action actionImprimir;

	private String os  = EmpresaAtual.getNumeroOS();
	private String cmc = EmpresaAtual.getCMCEmpresa();
	private String razaoSocial = EmpresaAtual.getRazaoSocialEmpresa();	

	public SEPFormTabMP( Connection con ) {
		c = con;
		initializeActionEvents();
	}

	public JPanel createTabMPPanel() {
		
        tabMPPanel = new JPanel( new BorderLayout() );
		gbLayout = new GridBagLayout();
		inputTabMPPanel = new JPanel( gbLayout );
		gbConstraints = new GridBagConstraints();
	
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Mapa de Prestadores");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabMPPanel.setBorder( titleBorder );					
		
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.anchor = GridBagConstraints.EAST;
		
		JButton imprimirButton = new JButton( "Imprimir" );		
		
		imprimirButton.addActionListener( actionImprimir );
		
		imprimirButton.setMnemonic('p');		
		JPanel controlMPPanel = new JPanel( new FlowLayout() );
		
		controlMPPanel.add( imprimirButton );		
		tabMPPanel.add( inputTabMPPanel );

		tabMPPanel.add( inputTabMPPanel, BorderLayout.NORTH );
		tabMPPanel.add( controlMPPanel, BorderLayout.SOUTH );		
		
		return tabMPPanel;	
		 
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
			try {
				PrintReport.printReport( assembleReport() );
			}
	   		catch(IOException ioe) {
		  		System.out.println( ioe.toString() );
	   		}
		}
	} 
	
	private String assembleReport() {
	   String fileName = "c:\\sep\\html\\" + cmc + "-" + razaoSocial + "\\mp.htm";
	   String resultado = "";
	   String[] cmcPrestador, razaoSocialPrestador, cnpjPrestador, dataEmissao, tipoDocumento, serie, subserie, numeroDocumento, valorDocumento;
	   
	   int indice, qtdeRegistros, qtdeLinhas;
		
	   String query = "SELECT COUNT(*) FROM tabmapatomador WHERE osmapatomador = '" + os + "' AND cmcprestadormapatomador = '" + cmc + "'";
		qtdeRegistros = 0;
	
	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();
	      	if (rs.next()) {
	      	   qtdeRegistros = rs.getInt(1);
	      	}
	   }
	   catch(SQLException sqle)
	   {
	      System.out.println( sqle.toString() );
	   }
   		
		cmcPrestador = new String[qtdeRegistros];
		razaoSocialPrestador = new String[qtdeRegistros];
		cnpjPrestador = new String[qtdeRegistros];
		dataEmissao = new String[qtdeRegistros];
		tipoDocumento = new String[qtdeRegistros];
		serie = new String[qtdeRegistros];
		subserie = new String[qtdeRegistros];
		numeroDocumento = new String[qtdeRegistros];
		valorDocumento = new String[qtdeRegistros];
		
	   query = "SELECT * FROM tabmapatomador WHERE osmapatomador = '" + os + "' AND cmcprestadormapatomador = '" + cmc + "'";

	   try
	   {
		
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();
			
			indice = 0;
	      	while (rs.next()) {
				cmcPrestador [indice] = rs.getString(3);
				razaoSocialPrestador [indice] = rs.getString(4);
				cnpjPrestador [indice] = rs.getString(5);
				dataEmissao [indice] = rs.getString(6);
				tipoDocumento [indice] = rs.getString(7);
				serie [indice] = rs.getString(8);
				subserie [indice] = rs.getString(9);
				numeroDocumento [indice] = rs.getString(10);
				valorDocumento [indice] = rs.getString(11);
				indice = indice + 1;
		    }
		    rs.close();
	        stmt.close();
        
			resultado += "<html><head><title>Mapa de Prestadores</title></head><body>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr valign=top align=center>";
        	resultado +="<td width=25%><img src=../brasao.gif align=middle>PMT<br>Secretaria Municipal<br>de Finanças</td>";
        	resultado +="<td width=50%>Mapa de<br>Prestadores</td>";
        	resultado +="<td width=25%>Protocolo</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
			resultado +="<tr><td align=center colspan=4>Tomador</td></tr><tr>";
        	resultado +="<td width=70%><font size=1 face=Haettenschweiler>Razão Social<br>" + razaoSocial + "</td>";
        	resultado +="<td width=30%><font size=1 face=Haettenschweiler>CMC No.<br>"+ SEPConverter.insertMaskCMC( cmc ) + "</td>";
//        	resultado +="<td width=10% align=center>Via<br>" + via + "a.</td>";
        	resultado +="</tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
			resultado +="<td align=center colspan=6><font size=1 face=Haettenschweiler>Prestadores</td></tr></table>";
						
	        qtdeLinhas = 1;
	        
			for ( indice = 0; indice <= qtdeRegistros - 1; indice = indice + 1 ){
				if ( qtdeLinhas > 9 ) {
					resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    				resultado +="<tr valign=bottom align=left>";
        			resultado +="<td align=center><font size=1 face=Haettenschweiler>&nbsp;<p>&nbsp;<p>&nbsp;<p>Fiscal de Tributos</td>";
        			resultado +="<td align=center><font size=1 face=Haettenschweiler>&nbsp;<p>&nbsp;<p>&nbsp;<p>Fiscal de Tributos</td>";
        			resultado +="<td align=center><font size=1 face=Haettenschweiler>&nbsp;<p>&nbsp;<p>Coordenador</td>";
    				resultado +="</tr></table><p>&nbsp;";
// inicio de outra pagina					
					resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    				resultado +="<tr valign=top align=center>";
        			resultado +="<td width=25%><font size=1 face=Haettenschweiler>PMT<br>Secretaria Municipal<br>de Finanças</td>";
        			resultado +="<td width=50%><font size=1 face=Haettenschweiler>Mapa de<br>Prestadores</td>";
        			resultado +="<td width=25%><font size=1 face=Haettenschweiler>Protocolo</td></tr></table>";
					resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
					resultado +="<tr><td align=center colspan=4><font size=1 face=Haettenschweiler>Tomador</td></tr><tr>";
        			resultado +="<td width=70%><font size=1 face=Haettenschweiler>Razão Social<br>" + razaoSocial + "</td>";
        			resultado +="<td width=30%><font size=1 face=Haettenschweiler>CMC No.<br>"+ SEPConverter.insertMaskCMC( cmc ) + "</td>";
//        			resultado +="<td width=10% align=center>Via<br>" + via + "a.</td>";
        			resultado +="</tr></table>";
					resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
					resultado +="<td align=center colspan=6><font size=1 face=Haettenschweiler>Prestadores</td></tr></table>";
					qtdeLinhas = 1;
			}
				resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
        		resultado +="<td width=20%><font size=1 face=Haettenschweiler>CMC Prestador<br>" + SEPConverter.insertMaskCMC( cmcPrestador[indice] ) + "</td>";
        		resultado +="<td width=30% colspan=2><font size=1 face=Haettenschweiler>CNPJ/CPF Prestador<br>" + SEPConverter.insertMaskCNPJ( cnpjPrestador[indice] ) + "</td>";
				resultado +="<td width=50% colspan=3><font size=1 face=Haettenschweiler>Razão Social<br>" + razaoSocialPrestador[indice] + "</td></tr>";
	    		resultado +="<tr><td width=20%><font size=1 face=Haettenschweiler>Data Emissão<br>" + SEPConverter.converteFrmtDataFromMySQL( dataEmissao[indice] ) + "</td>";
				resultado +="<td width=20%><font size=1 face=Haettenschweiler>Tipo Documento<br>" + tipoDocumento[indice] + "</td>";
				resultado +="<td width=10%><font size=1 face=Haettenschweiler>Série<br>" + serie[indice] +"</td>";
				resultado +="<td width=15%><font size=1 face=Haettenschweiler>Sub-Série<br>" + subserie[indice] + "</td>";
				resultado +="<td width=20%><font size=1 face=Haettenschweiler>Num.Documento<br>" + numeroDocumento[indice] + "</td>";
				resultado +="<td width=15%><font size=1 face=Haettenschweiler>Valor<br>" + SEPConverter.convertFrmtCurrencyFromMySQL( valorDocumento[indice] ) + "</td>";
				resultado +="</tr></table>";
				qtdeLinhas = qtdeLinhas + 1;
			}

			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr valign=bottom align=left>";
        	resultado +="<td align=center><font size=1 face=Haettenschweiler>&nbsp;<p>&nbsp;<p>&nbsp;<p>Fiscal de Tributos</td>";
        	resultado +="<td align=center><font size=1 face=Haettenschweiler>&nbsp;<p>&nbsp;<p>&nbsp;<p>Fiscal de Tributos</td>";
        	resultado +="<td align=center><font size=1 face=Haettenschweiler>&nbsp;<p>&nbsp;<p>Coordenador</td>";
    		resultado +="</tr></table></body></html>";
				
       	    File f = new File( fileName );
	        FileWriter fw = new FileWriter( f );
	        BufferedWriter bw = new BufferedWriter( fw );
	        bw.write( resultado );	        
	        bw.flush();
	        bw.close();
	   }
	   catch(SQLException sqle)
	   {
	      System.out.println( sqle.toString() );
	   }
	   catch ( IOException ioe ) {
	   	  System.out.println( ioe.toString() );
	   }
	   return fileName;
	}

	private void addComponent( Component c, int row, int column, int width, int height) {
		gbConstraints.gridx = column;
		gbConstraints.gridy = row;
		gbConstraints.gridwidth = width;
		gbConstraints.gridheight = height;
		gbLayout.setConstraints( c, gbConstraints );
		inputTabMPPanel.add( c );	
	}
}
