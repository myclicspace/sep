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
import java.util.*;
import java.io.*;

public class SEPFormTabNOAI extends JPanel {

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
    JPanel tabNOAIPanel, inputTabNOAIPanel;
	
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
	
	private String listaNOAI;
	private JLabel listaNOAILabel = new JLabel(" ");
		
	public SEPFormTabNOAI( Connection con ) {
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
		listNOAI();
	}

	public JPanel createTabNOAIPanel() {
		
        tabNOAIPanel = new JPanel( new BorderLayout() );
		gbLayout = new GridBagLayout();
		inputTabNOAIPanel = new JPanel( gbLayout );
		gbConstraints = new GridBagConstraints();
		
		JLabel listaLabel = new JLabel("Autos de Infração");				
		JLabel codigoInfracaoLabel = new JLabel("Código Infração");
		codigoInfracaoTxt = new JTextField( 05 );
		JLabel descricaoInfracaoLabel = new JLabel("Descrição Infração");
		descricaoInfracaoArea = new JTextArea( "", 05, 73 ); //, TextArea.SCROLLBARS_VERTICAL_ONLY );
		descricaoInfracaoArea.setLineWrap( true );		
		
//		Font fonteCourier = new Font( "Monospaced", Font.PLAIN, 12 );
//		descricaoInfracaoArea.setFont( fonteCourier );
//		descricaoInfracaoArea.setAutoscrolls( true );
//		descricaoInfracaoArea.setLineWrap( true );
//		descricaoInfracaoArea.setWrapStyleWord( false );
		
		scrollPaneDescricao = new JScrollPane( descricaoInfracaoArea );
		scrollPaneDescricao.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); 
		scrollPaneDescricao.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		//scrollPaneDescricao.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
		
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Notificação / Auto de Infração");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabNOAIPanel.setBorder( titleBorder );					
		
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.anchor = GridBagConstraints.EAST;
		
		addComponent( listaLabel, 0, 0, 1, 1 );
		addComponent( listaNOAILabel, 0, 1, 1, 1 );		
		addComponent( codigoInfracaoLabel, 1, 0, 1, 1 );
		addComponent( codigoInfracaoTxt, 1, 1, 1, 1  ); 
		addComponent( descricaoInfracaoLabel, 2, 0, 1, 1 ); 
		addComponent( scrollPaneDescricao, 3, 0, 5, 5 );
		
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
		
		JPanel controlNOAIPanel = new JPanel( new FlowLayout() );
		
		controlNOAIPanel.add( incluirButton );
		controlNOAIPanel.add( excluirButton );
		controlNOAIPanel.add( alterarButton );
		controlNOAIPanel.add( consultarButton );		
		controlNOAIPanel.add( imprimirButton );		
		controlNOAIPanel.add( limparButton );		
		
		tabNOAIPanel.add( inputTabNOAIPanel );

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

		tabNOAIPanel.add( inputTabNOAIPanel, BorderLayout.NORTH );
		tabNOAIPanel.add( infracoesDisplayPanel, BorderLayout.CENTER );
		tabNOAIPanel.add( controlNOAIPanel, BorderLayout.SOUTH );		
		
		return tabNOAIPanel;	
		 
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

	   String query = "INSERT INTO tabNOAI VALUES ('" + os + "','" + cmc + "','" + codigoInfracaoTxt.getText() + "','" + descricaoInfracaoArea.getText() +"')";
	   try
	   {
	   		if ( existeNOAI() ) {
				JOptionPane.showMessageDialog (this, "Notificação / Auto de Infração existente !", "Inclusão de Notificação / Auto de Infração", JOptionPane.ERROR_MESSAGE);	   			
	   		}
	   		else if ( existeInfracao() ) {	   		
		    		PreparedStatement stmt = c.prepareStatement(query);
					stmt.executeUpdate();
	        		stmt.close();
	        		listNOAI();
					listaNOAILabel.repaint();
					limparCampos();
			}
			else {
					JOptionPane.showMessageDialog (this, "Infração inexistente !", "Inclusão de Notificação / Auto de Infração", JOptionPane.ERROR_MESSAGE);	   							
			}
   	   }
	   catch(SQLException sqle)
	   {
		  System.out.println(sqle.toString());		  
	   }
	   return;
	}
	

	private void deleteInfracao() {
	   
	   String query = "DELETE FROM tabNOAI WHERE numeroOSNOAI = '" + os + "' AND cmcEmpresaNOAI = '" + cmc + "' AND codigoInfracaoNOAI = '" + codigoInfracaoTxt.getText() + "'";
	   try
	   {
	   		if ( existeNOAI() ) {
		    	PreparedStatement stmt = c.prepareStatement(query);
				stmt.executeUpdate();
	        	stmt.close();
	        	listNOAI();
				listaNOAILabel.repaint();
	        	limparCampos();
	     	}
	     	else {
				JOptionPane.showMessageDialog (this, "Notificação / Auto de Infração inexistente !", "Exclusão de Notificação / Auto de Infração", JOptionPane.ERROR_MESSAGE);	     		
	     	}
	   }
	   catch(SQLException sqle)
	   {
		  System.out.println( sqle.toString() );		  
	   }
	   return;
	}

	private void updateInfracao() {

	   String query = "UPDATE tabNOAI SET descricaoInfracaoNOAI = '" + descricaoInfracaoArea.getText() +"'";
	   query+=" WHERE numeroOSNOAI = '" + os + "' AND cmcEmpresaNOAI = '" + cmc + "' AND codigoInfracaoNOAI = '" + codigoInfracaoTxt.getText() + "'";
	   try
	   {
	   		if ( existeNOAI() ) {
		    	PreparedStatement stmt = c.prepareStatement(query);
				stmt.executeUpdate();
	        	stmt.close();
	        	limparCampos();
	     	}
	     	else {
				JOptionPane.showMessageDialog (this, "Notificação / Auto de Infração inexistente !", "Alteração de Notificação / Auto de Infração", JOptionPane.ERROR_MESSAGE);	     		
	     	}
   	   }
	   catch(SQLException sqle)
	   {
		  System.out.println(sqle.toString());		  
	   }
	   return;
	}


	private void findInfracao() {

		String query = "SELECT * FROM tabNOAI WHERE numeroOSNOAI = '" + os + "' AND cmcEmpresaNOAI = '" + cmc + "' AND codigoInfracaoNOAI = '" + codigoInfracaoTxt.getText() + "'";
	
	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	      	if (rs.next()) {
   	   		    descricaoInfracaoArea.setText( rs.getString(4) );
   	   		}
	     	else {
				JOptionPane.showMessageDialog (this, "Notificação / Auto de Infração inexistente !", "Consulta de Notificação / Auto de Infração", JOptionPane.ERROR_MESSAGE);	     		
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
	   String descricaoInfracao = "";
	   String enquadramento = "";
	   String codigoInfracao = codigoInfracaoTxt.getText();
	   int qtdeLinhas = 0;
	   
	   String query = "SELECT * FROM tabNOAI a, tabInfracao i WHERE a.numeroOSNOAI = '" + os + "' AND a.cmcEmpresaNOAI = '" + cmc +"' AND a.codigoInfracaoNOAI = '" + codigoInfracaoTxt.getText() + "' AND a.codigoInfracaoNOAI = i.codigoInfracao";
	   
		String texto  = "Digite quais vias para impressão :\n";
		texto += "     1=1a        |        2=2a        |       3=3a\n";
		texto += "12=1a e 2a  |  13=1a e 3a  |  23=2a e 3a\n";
		texto += "123=1a, 2a e 3a"; 
	   
		String vias = JOptionPane.showInputDialog ( texto );
	   
	   try
	   {
	   		if ( existeNOAI() ) {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	   
	      	while (rs.next()) {
   	   		    descricaoInfracao = rs.getString(4);
   	   		    enquadramento = rs.getString(7);
				qtdeLinhas = 20 - ( rs.getString(4).length() / 80 );  // linhas para completar a pagina   	   		    
		    }
		    rs.close();
	        stmt.close();
	        
	      for ( int i = 0; i < vias.length(); i++ ) {
				char via = vias.charAt( i );
				
			resultado = "";
			
			fileName = "c:\\sep\\html\\" + cmc + "-" + razaoSocial + "\\NOAI-";
			fileName += codigoInfracao + "-Via-" + via + ".htm";

			resultado = "<html><head><title>Notificação / Auto de Infração</title></head>";
			resultado +="<body><table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr valign=top align=center>";
        	resultado +="<td width=25%><img src=../brasao.gif align=middle>PMT<br>Secretaria Municipal<br>de Finanças</td>";
        	resultado +="<td width=50%>Notificação/<br>Auto de Infração</td>";
        	resultado +="<td width=25%>Protocolo</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
    		resultado +="<tr><td width=60%><font size=3 face=Times New Roman>Razão Social<br>" + razaoSocial + "</td>";
        	resultado +="<td width=30%><font size=3 face=Times New Roman>CMC No.<br>" + SEPConverter.insertMaskCMC( cmc ) + "</td>";			
        	resultado +="<td width=10% align=center><font size=3 face=Times New Roman>Via<br>" + via + "a.</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
        	resultado +="<td align=center><font size=3 face=Times New Roman>Infrações Cometidas</td></tr><tr>";
			resultado +="<tr><td><p align=justify><font size=3 face=Times New Roman>";
			resultado +=PrintReport.insereBR( descricaoInfracao );
			
			for ( int ind = 0; ind < qtdeLinhas; ind++ ) {
				resultado += ".<br>";
			}
    		
			resultado +="&nbsp;<br></td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
        	resultado +="<td align=center><font size=3 face=Times New Roman>Dispositivos da Legislação Tributária Infrigidos</td></tr><tr><td>";
			resultado +="<p align=justify><font size=3 face=Times New Roman>" + PrintReport.paragrafo() + enquadramento + "</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
        	resultado +="<td><p align=justify><font size=3 face=Times New Roman>";
        	resultado +=PrintReport.paragrafo() + "Fica o contribuinte acima identificado, notificado a comparecer à Prefeitura Municipal de Teresina, no prazo de 08 ( oito ) dias, a fim de efetuar o recolhimento do débito, com os acréscimos administrativos legais, ou apresentar a reclamação contra o lançamento, conforme art. 188 da lei 1.761/83. O não atendimento à presente notificação no prazo acima mencionado, ou ainda, a decisão administrativa que julgue improcedente reclamação apresentada, converterá a presente Notificação em Auto de Infração, conforme disposto no artigo 188, parágrafo 1º da Lei 1.761/83 obrigando ao pagamento de multa por infração de 150%( cento e cinqüenta  por cento ) do imposto devido previsto no(s) Art. 82, IV, 'c' da Lei 1.761/83, modificado pela Lei 2.748/98.</p>";
			resultado +="<p align=right><font size=3 face=Times New Roman>Teresina, ____ de ____________ de _______   ___:___</p>";
        	resultado +="</td></tr></table>";
			resultado +="<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado +="<tr valign=bottom align=left>";
        	resultado +="<td align=center><font size=3 face=Times New Roman>&nbsp<p>&nbsp;<p>&nbsp;Contribuinte / Representante Legal</td>";
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
	     	else {
				JOptionPane.showMessageDialog (this, "Notificação / Auto de Infração inexistente !", "Impressão de Notificação / Auto de Infração", JOptionPane.ERROR_MESSAGE);	     		
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

	private void listNOAI() {

	   String query = "SELECT * FROM tabNOAI WHERE numeroOSNOAI = '" + os + "' AND cmcEmpresaNOAI = '" + cmc + "' ORDER BY codigoInfracaoNOAI";
	   listaNOAI = "";	   
	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	      	while (rs.next()) {
   	   		    listaNOAI = listaNOAI + rs.getString(3) + "  ";
   	   		}
		    rs.close();
	        stmt.close();
			listaNOAILabel.setText( listaNOAI );
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
			System.out.println( sqlException.getMessage() + " aqui");
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
	

	private boolean existeNOAI() {

		boolean resposta = false;
		String query = "SELECT * FROM tabNOAI WHERE numeroOSNOAI = '" + os + "' AND cmcEmpresaNOAI = '" + cmc + "' AND codigoInfracaoNOAI = '" + codigoInfracaoTxt.getText() + "'";
		
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
		inputTabNOAIPanel.add( c );	
	}
    
}
