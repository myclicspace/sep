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
import java.util.*; 


public class SEPFormTabTF extends JPanel {

   	private JTextArea descricaoTermoFinalArea;
	private JScrollPane scrollPaneDescricao;
    JPanel tabTFPanel, inputTabTFPanel;
    
    int totalPaginas = 0;
	
	private GridBagLayout gbLayout;
	private	GridBagConstraints gbConstraints;	
	private Connection c;
	
	private Action actionIncluir;
	private Action actionAlterar;
	private Action actionConsultar;	
	private Action actionImprimir;
	private Action actionLimpar;
	
	private String os  = EmpresaAtual.getNumeroOS();
	private String cmc = EmpresaAtual.getCMCEmpresa();	   
	private String razaoSocial = EmpresaAtual.getRazaoSocialEmpresa();
	private String endereco = EmpresaAtual.getEnderecoEmpresa();
		
	
	public SEPFormTabTF( Connection con ) {
		c = con;
		initializeActionEvents();	
	}

	public JPanel createTabTFPanel() {
		
        tabTFPanel = new JPanel( new BorderLayout() );
		gbLayout = new GridBagLayout();
		inputTabTFPanel = new JPanel( gbLayout );
		gbConstraints = new GridBagConstraints();

		descricaoTermoFinalArea = new JTextArea( "", 23, 73 ); //, TextArea.SCROLLBARS_VERTICAL_ONLY );
		descricaoTermoFinalArea.setLineWrap( true );		
		
//		Font fonteCourier = new Font( "Monospaced", Font.PLAIN, 12 );
//		descricaoTermoFinalArea.setFont( fonteCourier );
//		descricaoTermoFinalArea.setAutoscrolls( true );
		
		scrollPaneDescricao = new JScrollPane( descricaoTermoFinalArea );
		scrollPaneDescricao.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
		scrollPaneDescricao.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);//.VERTICAL_SCROLLBAR_AS_NEEDED );
		
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Termo Final");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabTFPanel.setBorder( titleBorder );					
		
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.anchor = GridBagConstraints.EAST;
		
		addComponent( scrollPaneDescricao, 0, 0, 1, 1 );
		
		JButton incluirButton = new JButton( "Incluir" );		
		JButton alterarButton = new JButton( "Alterar" );		
		JButton consultarButton = new JButton( "Consultar" );				
		JButton imprimirButton = new JButton( "Imprimir" );		
		JButton limparButton = new JButton( "Limpar" );		
		
		incluirButton.addActionListener( actionIncluir );				
		alterarButton.addActionListener( actionAlterar );				
		consultarButton.addActionListener( actionConsultar );		
		imprimirButton.addActionListener( actionImprimir );				
		limparButton.addActionListener( actionLimpar );				
		
		incluirButton.setMnemonic('I');
		alterarButton.setMnemonic('A');
		consultarButton.setMnemonic('C');		
		imprimirButton.setMnemonic('p');		
		limparButton.setMnemonic('L');				

		JPanel controlTFPanel = new JPanel( new FlowLayout() );
		
		controlTFPanel.add( incluirButton );
		controlTFPanel.add( alterarButton );
		controlTFPanel.add( consultarButton );		
		controlTFPanel.add( imprimirButton );		
		controlTFPanel.add( limparButton );		

		tabTFPanel.add( inputTabTFPanel );
		
		tabTFPanel.add( inputTabTFPanel, BorderLayout.NORTH );
		tabTFPanel.add( controlTFPanel, BorderLayout.SOUTH );		
		
		return tabTFPanel;	
		 
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
		
		actionConsultar = new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionConsultar );
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
			recordTermoFinal();
		}

		if ( actionChanged == actionAlterar ) {
			updateTermoFinal();
		}
		
		else if ( actionChanged == actionConsultar ) {
			findTermoFinal();
		}

		else if ( actionChanged == actionImprimir ) {
			assembleReport();
		}
		else if ( actionChanged == actionLimpar ) {
			limparCampos();
		}
	} 
	
	private void findTermoFinal() {
	   String query = "SELECT * FROM tabTF WHERE numeroOSTF = '" + os + "' AND cmcEmpresaTF = '" + cmc + "'";

	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	      	if (rs.next()) {
   	   		    descricaoTermoFinalArea.setText( rs.getString(3) );
   	   		}
	     	else {
				JOptionPane.showMessageDialog (this, "Termo Final inexistente !", "Consulta de Termo Final", JOptionPane.ERROR_MESSAGE);	     		
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
	   
	   String descricaoTermoFinal = "";
	   	   
	   String query = "SELECT * FROM tabTF a, tabEmpresa e WHERE a.numeroOSTF = '" + os + "' AND a.cmcEmpresaTF = '" + cmc + "' AND a.cmcEmpresaTF = e.cmcEmpresa";

		String texto  = "Digite quais vias para impressão :\n";
		texto += "     1=1a        |        2=2a        |       3=3a\n";
		texto += "12=1a e 2a  |  13=1a e 3a  |  23=2a e 3a\n";
		texto += "123=1a, 2a e 3a"; 
		
		String vias = JOptionPane.showInputDialog ( texto );
		
	   try
	   {
		    PreparedStatement stmt = c.prepareStatement( query );
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	   
	      	while (rs.next()) {
   	   		    descricaoTermoFinal = rs.getString(3);
		    }
		    rs.close();
	        stmt.close();
			
			divideTexto( descricaoTermoFinal, vias );

	   } // try
	   catch(SQLException sqle)
	   {
	      sqle.printStackTrace();
	   }
	}


	private void recordTermoFinal() {
	   
	   String query = "INSERT INTO tabTF VALUES ('" + os + "', '" + cmc + "', '" + descricaoTermoFinalArea.getText() + "')";
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


	private void updateTermoFinal() {

	   String query = "UPDATE tabTF SET descricaoTF = '" + descricaoTermoFinalArea.getText() + "'";
	   query += " WHERE numeroOSTF = '" + os + "' AND cmcEmpresaTF = '" + cmc + "'";
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
	
	
	
	private void limparCampos() {
		
		descricaoTermoFinalArea.setText( "" );
		return;
			
	}

	private void divideTexto ( String texto, String vias ) {
		
		int caracteresPorLinha = 80, linhasPorPagina = 20, inicio = 0, fim;
		int caracteresPorPagina = caracteresPorLinha * linhasPorPagina;
		int tamanhoTexto = texto.length();
		int qtdePaginas = ( tamanhoTexto / caracteresPorPagina ) + 1;
		totalPaginas = qtdePaginas;
		int qtdeTokens, contador, pagina = 1;
		StringTokenizer st;
		String parteTexto, parte, p, token = ".";
		
		if ( qtdePaginas == 1 ) {
		   fim = tamanhoTexto;	
		}
		else {
			fim = caracteresPorPagina;	
		}
		
		while ( fim != tamanhoTexto ) {
			
//			System.out.println( "T= " + tamanhoTexto + " I= " + inicio + " F= " + fim + " P= " + qtdePaginas );

			parteTexto = texto.substring( inicio, fim );
			st = new StringTokenizer( parteTexto, token );
			qtdeTokens = st.countTokens();
			contador = 1;
			parte = "";
			while ( st.hasMoreTokens() ) {
				p = st.nextToken();
				if ( contador++ != qtdeTokens ) {
         			parte = parte + p + token;
         		}
     		}
     		
     		montaPagina( parte, vias, pagina++ );
     		
			inicio = inicio + parte.length();
			if ( --qtdePaginas == 1 ){
				fim = fim + ( tamanhoTexto % caracteresPorPagina );
			}
			else {
				fim = fim + caracteresPorPagina;
			}
		}
//		System.out.println( "T= " + tamanhoTexto + " I= " + inicio + " F= " + fim + " P= " + qtdePaginas );		
		parte = texto.substring( inicio, fim );
		int qtdeLinhas = 20 - ( parte.length() / 80 );  // linhas para completar a pagina
		for ( int ind = 0; ind < qtdeLinhas; ind++ ) {
			parte += ".\n";
		}
		montaPagina( parte, vias, pagina );
	}
	
	private void addComponent( Component c, int row, int column, int width, int height) {
		gbConstraints.gridx = column;
		gbConstraints.gridy = row;
		gbConstraints.gridwidth = width;
		gbConstraints.gridheight = height;
		gbLayout.setConstraints( c, gbConstraints );
		inputTabTFPanel.add( c );	
	}
	
	private void montaPagina( String texto, String vias, int pagina ) {	        

	   String fileName;
	   String resultado;
		
		for ( int i = 0; i < vias.length(); i++ ) {
				char via = vias.charAt( i );

			fileName = "c:\\sep\\html\\" + cmc + "-" + razaoSocial + "\\TF";
			fileName += "-" + pagina + "-Via-" + via + ".htm";
			
			resultado = "<html><head><title>Termo Final</title></head><body>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr valign=top align=center>";
        	resultado +="<td width=25%><img src=../brasao.gif align=middle>PMT<br>Secretaria Municipal<br>de Finanças</td>";
        	resultado +="<td width=50%>Termo Final</td>";
        	resultado +="<td width=25%>Protocolo</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td width=60%><font size=3 face=Times New Roman>Razão Social<br>" + razaoSocial + "</td>";
        	resultado +="<td width=20%><font size=3 face=Times New Roman>CMC No.<br>" + SEPConverter.insertMaskCMC ( cmc ) + "</td>";
        	resultado +="<td width=10% align=center><font size=3 face=Times New Roman>Pagina<br>" + pagina + "/" + totalPaginas + "</td>";
        	resultado +="<td width=10% align=center><font size=3 face=Times New Roman>Via<br>" + via + "a.</td></tr>";
			resultado +="</table><table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td><font size=3 face=Times New Roman>Endereço<br>" + endereco + "</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
  	  		resultado +="<tr><td><font size=3 face=Times New Roman><p align=justify>" + PrintReport.insereBR( texto ) + "</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
        	resultado +="<td><font size=3 face=Times New Roman><p align=justify>E para constar e produzir o efeito legal, lavramos o presente  termo em 03 ( três ) vias de igual teor e forma que vai assinado por nós e pelo Contribuinte ou seu Representante Legal.</p>";
        	resultado +="<p align=right><font size=3 face=Times New Roman>Teresina, ____ de ____________ de _______ </p>";
        	resultado +="</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr valign=bottom align=left>";
        	resultado +="<td align=center><font size=3 face=Times New Roman>&nbsp;<p>&nbsp;<p>";
			resultado +="Contribuinte/Representante Legal</td>";
			resultado +="<td align=center><font size=3 face=Times New Roman>Fiscal de Tributos</td>";
        	resultado +="<td align=center><font size=3 face=Times New Roman>Coordenador</td></tr></table></body></html>";
		
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
	     } // for
	}
}


