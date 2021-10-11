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
//import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.io.*;
//import java.math.BigInteger;
import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.text.DecimalFormat;


public class SEPFormTabAI extends JPanel {

	private int[] arrCodigoInfracoes;
	private Map rows;
	private Vector infracoes;
	private int bufferRecordPos = 0;

	TabInfracoesModel model	= null;
    JTable infracoesTable = null; 	
			
	private String[] columnNames =
	{ "Código", "Descrição" };

	private JTextField codigoInfracaoTxt;
   	private JTextArea descricaoInfracaoArea;
	private JScrollPane scrollPaneDescricao;
    private JPanel tabAIPanel, inputTabAIPanel;
        	
	private GridBagLayout gbLayout;
	private	GridBagConstraints gbConstraints;	
	private Connection c;
	
	private Action actionIncluir;	
	private Action actionExcluir;
	private Action actionAlterar;
	private Action actionConsultar;	
	private Action actionImprimir;
	private Action actionLimpar;

	private String os  = EmpresaAtual.getNumeroOS();
	private String cmc = EmpresaAtual.getCMCEmpresa();
	private String razaoSocial = EmpresaAtual.getRazaoSocialEmpresa();		


	private String listaAI;
	private JLabel listaAILabel = new JLabel(" ");
		
	public SEPFormTabAI( Connection con ) {
		c = con;
		String query = "SELECT * FROM tabInfracao ORDER BY codigoInfracao";	
		initializeActionEvents();
		try {
		    
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery( query );
			loadInfracoesTable( rs );	
		}
		catch ( SQLException ignore ) {
			System.out.println( ignore.getMessage() );
			ignore.printStackTrace();
		}	
		listAI();
	}

	public JPanel createTabAIPanel() {
		
        tabAIPanel = new JPanel( new BorderLayout() );
		gbLayout = new GridBagLayout();
		inputTabAIPanel = new JPanel( gbLayout );
		gbConstraints = new GridBagConstraints();
		
		JLabel listaLabel = new JLabel("Autos de Infração");				
		JLabel codigoInfracaoLabel = new JLabel("Código Infração");
		codigoInfracaoTxt = new JTextField( 05 );
		JLabel descricaoInfracaoLabel = new JLabel("Descrição Infração");
		//--
		// Codigo antigo -
		//descricaoInfracaoArea = new JTextArea( "", 05, 73, TextArea.SCROLLBARS_VERTICAL_ONLY );
		descricaoInfracaoArea = new JTextArea( "", 05, 73 );
		descricaoInfracaoArea.setLineWrap( true );
		
		
//		Font fonteCourier = new Font( "Monospaced", Font.PLAIN, 12 );
//		descricaoInfracaoArea.setFont( fonteCourier );
		
		scrollPaneDescricao = new JScrollPane( descricaoInfracaoArea );
		
		scrollPaneDescricao.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); 
		scrollPaneDescricao.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS); 
		
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Auto de Infração");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabAIPanel.setBorder( titleBorder );					
		
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.anchor = GridBagConstraints.EAST;
		
		addComponent( listaLabel, 0, 0, 1, 1 );
		addComponent( listaAILabel, 0, 1, 1, 1 );		
		addComponent( codigoInfracaoLabel, 1, 0, 1, 1 );
		addComponent( codigoInfracaoTxt, 1, 1, 1, 1  ); 
		addComponent( descricaoInfracaoLabel, 2, 0, 1, 1 ); 
		addComponent( scrollPaneDescricao, 3, 0, 2, 1 );
		
		JButton incluirButton = new JButton( "Incluir" );		
		JButton excluirButton = new JButton( "Excluir" );
		JButton alterarButton = new JButton( "Alterar" );		
		JButton consultarButton = new JButton( "Consultar" );				
		JButton imprimirButton = new JButton( "Imprimir" );		
		JButton limparButton = new JButton( "Limpar" );		
		
		incluirButton.addActionListener( actionIncluir );		
		excluirButton.addActionListener( actionExcluir );
		alterarButton.addActionListener( actionAlterar );				
		consultarButton.addActionListener( actionConsultar );		
		imprimirButton.addActionListener( actionImprimir );				
		limparButton.addActionListener( actionLimpar );				
		
		incluirButton.setMnemonic('I');
		excluirButton.setMnemonic('E');
		alterarButton.setMnemonic('A');
		consultarButton.setMnemonic('C');		
		imprimirButton.setMnemonic('p');		
		limparButton.setMnemonic('L');				
		
		JPanel controlAIPanel = new JPanel( new FlowLayout() );
		
		controlAIPanel.add( incluirButton );
		controlAIPanel.add( excluirButton );
		controlAIPanel.add( alterarButton );
		controlAIPanel.add( consultarButton );		
		controlAIPanel.add( imprimirButton );		
		controlAIPanel.add( limparButton );		
		
		tabAIPanel.add( inputTabAIPanel );

		model = new TabInfracoesModel();				
		
		infracoesTable = new JTable(); 
		infracoesTable.setModel( model );
		infracoesTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		infracoesTable.addMouseListener( new MouseAdapter()  {
			public void mouseClicked( MouseEvent e ) {
				int count = infracoesTable.getSelectedRowCount();
				if ( count > 0 ) {
                    inputTabInfracoesPanelRefresh();
                }					
			}
		});
		
		JScrollPane scrollpane = new JScrollPane( infracoesTable );
		JPanel infracoesDisplayPanel = new JPanel( new BorderLayout() ) ;
		infracoesDisplayPanel.add( scrollpane );

		tabAIPanel.add( inputTabAIPanel, BorderLayout.NORTH );
		tabAIPanel.add( infracoesDisplayPanel, BorderLayout.CENTER );
		tabAIPanel.add( controlAIPanel, BorderLayout.SOUTH );		
		
		return tabAIPanel;	
		 
	}
	

	private void initializeActionEvents() {

		actionIncluir =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionIncluir );
			}
		};	

		actionExcluir =  new AbstractAction() {
			public  void actionPerformed( ActionEvent e ) {
				actionChanged( actionExcluir );
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
			insertInfracao();
		}

		else if ( actionChanged == actionExcluir ) {
			deleteInfracao();			
		}
		
		else if ( actionChanged == actionAlterar ) {
			updateInfracao();
		}
		
		else if ( actionChanged == actionConsultar ) {
			findInfracao();
		}
		
		else if ( actionChanged == actionImprimir ) {
			assembleReport();
		}

		else if ( actionChanged == actionLimpar ) {
			limparCampos();
		}
	} 

	private void insertInfracao() {

	   String query = "INSERT INTO tabAI VALUES ('" + os + "','" + cmc + "','" + codigoInfracaoTxt.getText() + "','" + descricaoInfracaoArea.getText() +"')";
	   try
	   {
	   		if ( existeAI() ) {
				JOptionPane.showMessageDialog (this, "Auto de Infração existente !", "Inclusão de Auto de Infração", JOptionPane.ERROR_MESSAGE);	   			
	   		}
	   		else if ( existeInfracao() ) {	   		
		    		PreparedStatement stmt = c.prepareStatement(query);
					stmt.executeUpdate();
	        		stmt.close();
	        		listAI();
					listaAILabel.repaint();
					limparCampos();
			}
			else {
					JOptionPane.showMessageDialog (this, "Infração inexistente !", "Inclusão de Auto de Infração", JOptionPane.ERROR_MESSAGE);	   							
			}
   	   }
	   catch(SQLException sqle)
	   {
		  System.out.println( sqle.toString() );		  
	   }
	   return;
	}
	

	private void deleteInfracao() {
	   
	   String query = "DELETE FROM tabAI WHERE numeroOSAI = '" + os + "' AND cmcEmpresaAI = '" + cmc + "' AND codigoInfracaoAI = '" + codigoInfracaoTxt.getText() + "'";
	   try
	   {
	   		if ( existeAI() ) {
		    	PreparedStatement stmt = c.prepareStatement(query);
				stmt.executeUpdate();
	        	stmt.close();
	        	listAI();
				listaAILabel.repaint();
	        	limparCampos();
	     	}
	     	else {
				JOptionPane.showMessageDialog (this, "Auto de Infração inexistente !", "Exclusão de Auto de Infração", JOptionPane.ERROR_MESSAGE);	     		
	     	}
	   }
	   catch(SQLException sqle)
	   {
		  System.out.println( sqle.toString() );		  
	   }
	   return;
	}

	private void updateInfracao() {

	   String query = "UPDATE tabAI SET descricaoInfracaoAI = '" + descricaoInfracaoArea.getText() +"'";
	   query+=" WHERE numeroOSAI = '" + os + "' AND cmcEmpresaAI = '" + cmc + "' AND codigoInfracaoAI = '" + codigoInfracaoTxt.getText() + "'";
	   try
	   {
	   		if ( existeAI() ) {
		    	PreparedStatement stmt = c.prepareStatement(query);
				stmt.executeUpdate();
	        	stmt.close();
	        	limparCampos();
	     	}
	     	else {
				JOptionPane.showMessageDialog (this, "Auto de Infração inexistente !", "Alteração de Auto de Infração", JOptionPane.ERROR_MESSAGE);	     		
	     	}
   	   }
	   catch(SQLException sqle)
	   {
		  System.out.println( sqle.toString() );		  
	   }
	   return;
	}


	private void findInfracao() {

		String query = "SELECT * FROM tabAI WHERE numeroOSAI = '" + os + "' AND cmcEmpresaAI = '" + cmc + "' AND codigoInfracaoAI = '" + codigoInfracaoTxt.getText() + "'";
	
	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	      	if (rs.next()) {
	      		
   	   		    descricaoInfracaoArea.setText( rs.getString(4) );
  	   		    
   	   		}
	     	else {
				JOptionPane.showMessageDialog (this, "Auto de Infração inexistente !", "Consulta de Auto de Infração", JOptionPane.ERROR_MESSAGE);	     		
	     	}
		    rs.close();
	        stmt.close();
	   }
	   catch(SQLException sqle)
	   {
		  System.out.println( sqle.toString() );
	   }
	   catch(Exception e)
	   {
		  System.out.println( e.toString() );
	   }
	   return;
	}

	private void assembleReport() {
	   String fileName;
	   String resultado;
  	   String descricaoInfracao = "";
	   String enquadramento = "";
	   String penalidade = "";
	   String valor = "";
	   String codigoInfracao = codigoInfracaoTxt.getText();
	   int qtdeLinhas = 0;
	   
		String texto  = "Digite quais vias para impressão :\n";
	    texto += "     1=1a        |        2=2a        |       3=3a\n";
	    texto += "12=1a e 2a  |  13=1a e 3a  |  23=2a e 3a\n";
	    texto += "123=1a, 2a e 3a"; 
	   
		String vias = JOptionPane.showInputDialog ( texto );

	   String query = "SELECT * FROM tabAI a, tabInfracao i WHERE a.numeroOSAI = '" + os + "' AND a.cmcEmpresaAI = '" + cmc +"' AND a.codigoInfracaoAI = '" + codigoInfracaoTxt.getText() + "' AND a.codigoInfracaoAI = i.codigoInfracao";
	   try
	   {
	   		if ( existeAI() ) {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	   
	      	while (rs.next()) {
   	   		    descricaoInfracao = rs.getString(4);
   	   		    enquadramento = rs.getString(7);
   	   		    penalidade = rs.getString(8);
   	   		    valor = SEPConverter.adapterCurrencyFrmt( rs.getString(9) );
	      		
	      		qtdeLinhas = 20 - ( rs.getString(4).length() / 80 );  // linhas para completar a pagina
   	   		    
		    }
		    rs.close();
	        stmt.close();
	        	        
		  for ( int i = 0; i < vias.length(); i++ ) {
				char via = vias.charAt( i );
				
				fileName = "c:\\sep\\html\\" + cmc + "-" + razaoSocial + "\\AI-";
				fileName += codigoInfracao + "-Via-" + via + ".htm";

			resultado = "<html><head><title>Auto de Infração</title></head><body>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr valign=top align=center>";
        	resultado +="<td width=25%><img src=../brasao.gif align=middle>PMT<br>Secretaria Municipal<br>de Finanças</td>";
        	resultado +="<td width=50%>Auto de Infração</td>";
        	resultado +="<td width=25%>Protocolo</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td width=60%><font size=3 face=Times New Roman>Razão Social<br>" + razaoSocial + "</td>";
        	resultado +="<td width=30%><font size=3 face=Times New Roman>CMC No.<br>" + SEPConverter.insertMaskCMC ( cmc ) + "</td>";
        	resultado +="<td width=10% align=center><font size=3 face=Times New Roman>Via<br>" + via + "a.</td>";
        	resultado +="</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td align=center><font size=3 face=Times New Roman>Infrações Cometidas</td></tr>";
    		resultado +="<tr><td><font size=3 face=Times New Roman><p align=justify>" + PrintReport.insereBR( descricaoInfracao );
		  	
		  	for ( int ind = 0; ind < qtdeLinhas; ind++ ) {
				resultado += ".<br>";
			}
    		
    		resultado +="&nbsp;<br></td></tr></table>";
			
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td align=center><font size=3 face=Times New Roman>Enquadramento Legal</td></tr>";
    		resultado +="<tr><td><font size=3 face=Times New Roman><p align=justify>" + PrintReport.paragrafo() + enquadramento + "<br>&nbsp</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td align=center><font size=3 face=Times New Roman>Penalidade</td></tr>";
    		resultado +="<tr><td><font size=3 face=Times New Roman><p align=justify>";
    		resultado +=PrintReport.paragrafo();
    		
    		if (!(valor.equals("N/A")) ) {
				double valorConvertido = Double.parseDouble( valor );
				String valorMascarado = SEPConverter.getFmtCurrency( valorConvertido );
				Extenso numero = new Extenso(new BigDecimal(valor));
    			resultado += "Multa de R$ " + valorMascarado + " ( " + numero.toString() + " ), conforme "; 
    		}
    			
    		resultado +=penalidade + "<br>&nbsp</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr><td><font size=3 face=Times New Roman><p align=justify>" + PrintReport.paragrafo();
    		resultado +="Fica o contribuinte acima identificado, INTIMADO a comparecer à Prefeitura Municipal de Teresina, no prazo ";
    		resultado +="de 20 (vinte) dias, a fim de apresentar defesa ou providenciar o pagamento total ou parcial do débito ";
    		resultado +="constante no presente AUTO DE INFRAÇÃO, com os acréscimos legais cabíveis, conforme Lei 1.761/83 Art. 200.</p>";
        	resultado +="<p align=right>Teresina, ____ de ____________ de _______ ___:___</p>";
        	resultado +="</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr valign=bottom align=left>";
        	resultado +="<td align=center><font size=3 face=Times New Roman>&nbsp;<p>&nbsp;<p>";
			resultado +="Contribuinte/Representante Legal</td>";
        	resultado +="<td align=center><font size=3 face=Times New Roman>Fiscal de Tributos</td>";
        	resultado +="<td align=center><font size=3 face=Times New Roman>Coordenador</td></tr></table></font></body></html>";
			
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
	       }  // for
	        
	   		} // if
	     	else {
				JOptionPane.showMessageDialog (this, "Auto de Infração inexistente !", "Impressão de Auto de Infração", JOptionPane.ERROR_MESSAGE);	     		
	     	}
		} // try
	   catch(SQLException sqle)
	   {
	      sqle.printStackTrace();
	   }
	   catch ( IOException ioe ) {
	   	  ioe.printStackTrace();
	   }
	   
    }

	private void listAI() {

	   String query = "SELECT * FROM tabAI WHERE numeroOSAI = '" + os + "' AND cmcEmpresaAI = '" + cmc + "' ORDER BY codigoInfracaoAI";
	   listaAI = "";	   
	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	      	while (rs.next()) {
   	   		    listaAI = listaAI + rs.getString(3) + "  ";
   	   		}
		    rs.close();
	        stmt.close();
			listaAILabel.setText( listaAI );
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
	
	private void loadInfracoesTable( ResultSet rs ) {	
		try {
			rows = new HashMap();
			infracoes = new Vector();
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
			 	for ( int j = 0; j < theRow.length; j++ )  {
			 	    theRow[ j ] = (String)rs.getString( j + 1 );
			 	}
		 	    key = (String) rs.getString( 1 );
			 	rows.put( new Integer(key) , new Integer(  bufferRecordPos ) );	 	
			 	infracoes.add( theRow );
			 	bufferRecordPos++;	 	
			 	
			 }
			 
		}
		catch ( SQLException sqlException ) {
			System.out.println( sqlException.getMessage() );
			sqlException.printStackTrace();
		}
	}
	
	private class TabInfracoesModel extends AbstractTableModel {
		
		public int getRowCount() {
			return rows.size();
		}
		
		public Object getValueAt( int r , int c ) {			
		String[] theRow = null ;
		    if ( r < infracoes.size() ) {
		    	Object obj = infracoes.get( r );
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
	
	private void inputTabInfracoesPanelRefresh() {   	
    
    	String value;

		int selectedRow = infracoesTable.getSelectedRow();
		value = (String) infracoesTable.getValueAt(selectedRow, 0 );
		codigoInfracaoTxt.setText( value );
		value = (String) infracoesTable.getValueAt(selectedRow, 1 );
		descricaoInfracaoArea.setText( value );
		
		revalidate();		
    }	

	private void limparCampos() {
		
		codigoInfracaoTxt.setText( "" );
		descricaoInfracaoArea.setText( "" );
		
		return;
			
	}
	
	private	boolean existeInfracao () {
		boolean resposta = false;
	   String query = "SELECT * FROM tabInfracao WHERE codigoInfracao = '" + codigoInfracaoTxt.getText() + "'";
	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	      	if (rs.next()) {
   	   		    resposta = true;
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
	   return resposta;
		
	}
	

	private boolean existeAI() {

		boolean resposta = false;
		String query = "SELECT * FROM tabAI WHERE numeroOSAI = '" + os + "' AND cmcEmpresaAI = '" + cmc + "' AND codigoInfracaoAI = '" + codigoInfracaoTxt.getText() + "'";
		
	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	      	if (rs.next()) {
   	   		    resposta = true;
   	   		}
		    rs.close();
	        stmt.close();
	   }
	   catch(SQLException sqle)
	   {
		  System.out.println( sqle.toString() );
	   }
	   catch(Exception e)
	   {
		  System.out.println( e.toString() );
	   }
	   return resposta;
	}

	private void addComponent( Component c, int row, int column, int width, int height) {
		gbConstraints.gridx = column;
		gbConstraints.gridy = row;
		gbConstraints.gridwidth = width;
		gbConstraints.gridheight = height;
		gbLayout.setConstraints( c, gbConstraints );
		inputTabAIPanel.add( c );	
	}
    
}
