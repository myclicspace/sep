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

public class SEPFormTabFC extends JPanel {

   	private JTextArea descricaoFolhaComplementarArea;
   	private JTextField numeroFolhaComplementarTxt;
	private JScrollPane scrollPaneDescricao;
    JPanel tabFCPanel, inputTabFCPanel;
	
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
	
	private String listaFC;
	private JLabel listaFCLabel = new JLabel(" ");
	int quantidadeFC;
	
	public SEPFormTabFC( Connection con ) {
		c = con;
		initializeActionEvents();	
		listFC();
	}

	public JPanel createTabFCPanel() {
		
        tabFCPanel = new JPanel( new BorderLayout() );
		gbLayout = new GridBagLayout();
		inputTabFCPanel = new JPanel( gbLayout );
		gbConstraints = new GridBagConstraints();
		
		JLabel listaLabel = new JLabel("Folhas Complementares");
		JLabel folhaComplementarLabel = new JLabel("Folha Complementar");
		numeroFolhaComplementarTxt = new JTextField( 05 );
		descricaoFolhaComplementarArea = new JTextArea( "", 21, 73 );
		descricaoFolhaComplementarArea.setLineWrap( true );
				
		
//		Font fonteCourier = new Font( "Monospaced", Font.PLAIN, 12 );
//		descricaoFolhaComplementarArea.setFont( fonteCourier );
//		descricaoFolhaComplementarArea.setAutoscrolls( true );		
		
		scrollPaneDescricao = new JScrollPane( descricaoFolhaComplementarArea );
		scrollPaneDescricao.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); 
		scrollPaneDescricao.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); 
		
//		scrollPaneDescricao.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
//		scrollPaneDescricao.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
		
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Folha Complementar");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabFCPanel.setBorder( titleBorder );					
		
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.anchor = GridBagConstraints.EAST;
		
		addComponent( listaLabel, 0, 0, 1, 1 );
		addComponent( listaFCLabel, 0, 1, 1, 1 );		
		addComponent( folhaComplementarLabel, 1, 0, 1, 1);
		addComponent( numeroFolhaComplementarTxt, 1, 1, 1, 1);
		addComponent( scrollPaneDescricao, 2, 0, 2, 1 );
		
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

		JPanel controlFCPanel = new JPanel( new FlowLayout() );
		
		controlFCPanel.add( incluirButton );
		controlFCPanel.add( alterarButton );
		controlFCPanel.add( consultarButton );		
		controlFCPanel.add( imprimirButton );		
		controlFCPanel.add( limparButton );		

		tabFCPanel.add( inputTabFCPanel );
		
		tabFCPanel.add( inputTabFCPanel, BorderLayout.NORTH );
		tabFCPanel.add( controlFCPanel, BorderLayout.SOUTH );		
		
		return tabFCPanel;	
		 
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
			recordFolhaComplementar();
		}
		
		else if ( actionChanged == actionAlterar ) {
			updateFolhaComplementar();
		}
		
		else if ( actionChanged == actionConsultar ) {
			findFolhaComplementar();
		}

		else if ( actionChanged == actionImprimir ) {
			assembleReport();
		}
		else if ( actionChanged == actionLimpar ) {
			limparCampos();
		}
	} 
	
	private void findFolhaComplementar() {
	   String query = "SELECT * FROM tabFC WHERE numeroOSFC = '" + os;
	   query += "' AND cmcEmpresaFC = '" + cmc;
	   query += "' AND numeroFC = " + Integer.parseInt ( numeroFolhaComplementarTxt.getText() );
		
		descricaoFolhaComplementarArea.setText( "" );
		
	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	      	if (rs.next()) {
   	   		    descricaoFolhaComplementarArea.append( rs.getString(3) );
   	   		}
	     	else {
				JOptionPane.showMessageDialog (this, "Folha Complementar inexistente !", "Consulta de Folha Complementar", JOptionPane.ERROR_MESSAGE);	     		
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

	private void updateFolhaComplementar() {
	   String query = "UPDATE tabFC ";
	   query += "SET descricaoFC = '" + descricaoFolhaComplementarArea.getText() + "'";
	   query += " WHERE numeroOSFC = '" + os + "'";
	   query += " AND cmcEmpresaFC = '" + cmc + "'";
	   query += " AND numeroFC = " + Integer.parseInt ( numeroFolhaComplementarTxt.getText() );
	
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
	
	private void assembleReport() {
	   
	   String fileName;
	   String resultado;
	   String descricaoFolhaComplementar = "";
	   String numeroFolhaComplementar = numeroFolhaComplementarTxt.getText();

		String texto  = "Digite quais vias para impressão :\n";
		texto += "     1=1a        |        2=2a        |       3=3a\n";
		texto += "12=1a e 2a  |  13=1a e 3a  |  23=2a e 3a\n";
		texto += "123=1a, 2a e 3a"; 
	   
		String vias = JOptionPane.showInputDialog ( texto );
		
	   String query = "SELECT * FROM tabFC WHERE numeroOSFC = '" + os + "'";
	   query += " AND cmcEmpresaFC = '" + cmc + "'";
	   query += " AND numeroFC = " + Integer.parseInt ( numeroFolhaComplementar );

	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	   
	      	if (rs.next()) {
   	   		    descricaoFolhaComplementar = rs.getString(3);
		    }
		    
		    rs.close();
	        stmt.close();
			
		  for ( int i = 0; i < vias.length(); i++ ) {
			char via = vias.charAt( i );
			
			resultado = "";
				
			fileName = "c:\\sep\\html\\" + cmc + "-" + razaoSocial + "\\FC-";
			fileName += numeroFolhaComplementar + "-Via-" + via + ".htm";
			
			resultado = "<html><head><title>Folha Complementar</title></head><body>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr valign=top align=center>";
        	resultado +="<td width=25%><img src=../brasao.gif align=middle>PMT<br>Secretaria Municipal<br>de Finanças</td>";
        	resultado +="<td width=50%>Folha Complementar</td>";
        	resultado +="<td width=25%>Protocolo</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td width=50%><font size=3 face=Times New Roman>Razão Social<br>" + razaoSocial + "</td>";
        	resultado +="<td width=30%><font size=3 face=Times New Roman>CMC No.<br>" + SEPConverter.insertMaskCMC( cmc ) + "</td>";
        	resultado +="<td width=10% align=center><font size=3 face=Times New Roman>Folha<br>" + numeroFolhaComplementar + "</td>";
        	resultado +="<td width=10% align=center><font size=3 face=Times New Roman>Via<br>" + via + "a.</td></tr>";
			resultado +="</table><table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td><font size=3 face=Times New Roman>Endereço</td></tr><tr>";
        	resultado +="<td><font size=3 face=Times New Roman><p align=justify>" + endereco + "</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td><font size=3 face=Times New Roman>";
    		resultado +="<p align=justify>" + PrintReport.insereBR( descricaoFolhaComplementar ) + "</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
        	resultado +="<td><font size=3 face=Times New Roman><p align=justify>E para constar e produzir o efeito legal, lavramos o presente  termo em 03 ( três ) vias de igual teor e forma que vai assinado por nós e pelo Contribuinte ou seu Representante Legal.</p>";
        	resultado +="<p align=right>Teresina, ____ de ____________ de _______ </p>";
        	resultado +="</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr valign=bottom align=left>";
        	resultado +="<td align=center><font size=3 face=Times New Roman>&nbsp<p>&nbsp<p>";
			resultado +="Contribuinte/Representante Legal</td>";
			resultado +="<td align=center><font size=3 face=Times New Roman>Fiscal de Tributos</td>";
        	resultado +="<td align=center><font size=3 face=Times New Roman>Coordenador</td></tr></table></body></html>";
		
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


	private void recordFolhaComplementar() {
	   
		quantidadeFC = quantidadeFC + 1;
	   String query = "INSERT INTO tabFC VALUES ('" + os + "', '" + cmc;
	   query  += "', '" + descricaoFolhaComplementarArea.getText();
	   query  += "'," + quantidadeFC + ")";	   
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
	   listFC();
	   limparCampos();
	   return;
	}
	
	private void limparCampos() {
		
		numeroFolhaComplementarTxt.setText( "" );
		descricaoFolhaComplementarArea.setText( "" );
		
		return;
			
	}


	private void listFC() {

	   String query = "SELECT * FROM tabFC WHERE numeroOSFC = '" + os + "' AND cmcEmpresaFC = '" + cmc + "'";
	   listaFC = "";
	   quantidadeFC = 0;
	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	      	while (rs.next()) {
	   			quantidadeFC = quantidadeFC + 1; 	   	      		
   	   		    listaFC = listaFC + rs.getString(4) + "  ";
   	   		}
		    rs.close();
	        stmt.close();
			listaFCLabel.setText( listaFC );
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

	private void addComponent( Component c, int row, int column, int width, int height) {
		gbConstraints.gridx = column;
		gbConstraints.gridy = row;
		gbConstraints.gridwidth = width;
		gbConstraints.gridheight = height;
		gbLayout.setConstraints( c, gbConstraints );
		inputTabFCPanel.add( c );	
	}

}
