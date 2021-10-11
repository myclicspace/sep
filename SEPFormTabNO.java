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

public class SEPFormTabNO extends JPanel {

	private int[] arrCodigoInfracoes;
	private Map rows;
	private Vector infracoes;
	private int bufferRecordPos = 0;

	TabInfracoesModel model	= null;
    JTable infracoesTable = null; 	
			
	private String[] columnNames =
	{ "Código", "Descrição" };

	private JTextField codigoInfracaoTxt;
   	private JTextArea descricaoInfracaoArea, exigenciasArea;
	private JScrollPane scrollPaneDescricao, scrollPaneExigencias;
    JPanel tabNOPanel, inputTabNOPanel;
	
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
	
	private String listaNO;
	private JLabel listaNOLabel = new JLabel(" ");
		
	public SEPFormTabNO( Connection con ) {
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
		listNO();
	}

	public JPanel createTabNOPanel() {
		
        tabNOPanel = new JPanel( new BorderLayout() );
		gbLayout = new GridBagLayout();
		inputTabNOPanel = new JPanel( gbLayout );
		gbConstraints = new GridBagConstraints();
		
		JLabel listaLabel = new JLabel("Notificações");				
		JLabel codigoInfracaoLabel = new JLabel("Código Infração");
		codigoInfracaoTxt = new JTextField( 05 );
		JLabel descricaoInfracaoLabel = new JLabel("Descrição Infração");
		descricaoInfracaoArea = new JTextArea( "", 05, 73);
		descricaoInfracaoArea.setLineWrap( true );		
		JLabel exigenciasLabel = new JLabel("Exigências");
		exigenciasArea = new JTextArea( "", 05, 73 );
		exigenciasArea.setLineWrap( true );		
		
//		Font fonteCourier = new Font( "Monospaced", Font.PLAIN, 12 );
//		descricaoInfracaoArea.setFont( fonteCourier );
//		descricaoInfracaoArea.setAutoscrolls( true );
//		exigenciasArea.setFont( fonteCourier );
//		exigenciasArea.setAutoscrolls( true );
		
		
		
		scrollPaneDescricao = new JScrollPane( descricaoInfracaoArea );
		scrollPaneDescricao.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); 
		scrollPaneDescricao.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		scrollPaneExigencias = new JScrollPane( exigenciasArea );
		
		scrollPaneExigencias.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER); 
		scrollPaneExigencias.setVerticalScrollBarPolicy( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		 
		
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Notificação");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabNOPanel.setBorder( titleBorder );					
		
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.anchor = GridBagConstraints.EAST;
		
		addComponent( listaLabel, 0, 0, 1, 1 );
		addComponent( listaNOLabel, 0, 1, 1, 1 );		
		addComponent( codigoInfracaoLabel, 1, 0, 1, 1 );
		addComponent( codigoInfracaoTxt, 1, 1, 1, 1  ); 
		addComponent( descricaoInfracaoLabel, 2, 0, 1, 1 ); 
		addComponent( scrollPaneDescricao, 3, 0, 5, 5 );
		addComponent( exigenciasLabel, 8, 0, 1, 1 ); 
		addComponent( scrollPaneExigencias, 9, 0, 5, 5 );
		
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
		
		JPanel controlNOPanel = new JPanel( new FlowLayout() );
		
		controlNOPanel.add( incluirButton );
		controlNOPanel.add( excluirButton );
		controlNOPanel.add( alterarButton );
		controlNOPanel.add( consultarButton );		
		controlNOPanel.add( imprimirButton );		
		controlNOPanel.add( limparButton );		
		
		tabNOPanel.add( inputTabNOPanel );

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

		tabNOPanel.add( inputTabNOPanel, BorderLayout.NORTH );
		tabNOPanel.add( infracoesDisplayPanel, BorderLayout.CENTER );
		tabNOPanel.add( controlNOPanel, BorderLayout.SOUTH );		
		
		return tabNOPanel;	
		 
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
			insertNotificacao();
		}

		else if ( actionChanged == actionExcluir ) {
			deleteNotificacao();			
		}
		
		else if ( actionChanged == actionAlterar ) {
			updateNotificacao();
		}
		
		else if ( actionChanged == actionConsultar ) {
			findNotificacao();
		}
		
		else if ( actionChanged == actionImprimir ) {
			assembleReport();
		}

		else if ( actionChanged == actionLimpar ) {
			limparCampos();
		}
	} 

	private void insertNotificacao() {

	   String query = "INSERT INTO tabNO VALUES ('" + os + "','" + cmc + "','" + codigoInfracaoTxt.getText() + "','";
	   query += descricaoInfracaoArea.getText() + "','" + exigenciasArea.getText() + "')";
	   try
	   {
	   		if ( existeNO() ) {
				JOptionPane.showMessageDialog (this, "Notificação existente !", "Inclusão de Notificação", JOptionPane.ERROR_MESSAGE);	   			
	   		}
	   		else if ( existeInfracao() ) {	   		
		    		PreparedStatement stmt = c.prepareStatement(query);
					stmt.executeUpdate();
	        		stmt.close();
	        		listNO();
					listaNOLabel.repaint();
					limparCampos();
			}
			else {
					JOptionPane.showMessageDialog (this, "Infração inexistente !", "Inclusão de Notificação", JOptionPane.ERROR_MESSAGE);	   							
			}
   	   }
	   catch(SQLException sqle)
	   {
		  System.out.println(sqle.toString());		  
	   }
	   return;
	}
	

	private void deleteNotificacao() {
	   
	   String query = "DELETE FROM tabNO WHERE numeroOSNO = '" + os + "' AND cmcEmpresaNO = '" + cmc + "' AND codigoInfracaoNO = '" + codigoInfracaoTxt.getText() + "'";
	   try
	   {
	   		if ( existeNO() ) {
		    	PreparedStatement stmt = c.prepareStatement(query);
				stmt.executeUpdate();
	        	stmt.close();
	        	listNO();
				listaNOLabel.repaint();
	        	limparCampos();
	     	}
	     	else {
				JOptionPane.showMessageDialog (this, "Notificação inexistente !", "Exclusão de Notificação", JOptionPane.ERROR_MESSAGE);	     		
	     	}
	   }
	   catch(SQLException sqle)
	   {
		  System.out.println( sqle.toString() );		  
	   }
	   return;
	}

	private void updateNotificacao() {

	   String query = "UPDATE tabNO SET descricaoInfracaoNO = '" + descricaoInfracaoArea.getText();
	   query+="', exigenciasNO = '" + exigenciasArea.getText();
	   query+="' WHERE numeroOSNO = '" + os + "' AND cmcEmpresaNO = '" + cmc + "' AND codigoInfracaoNO = '" + codigoInfracaoTxt.getText() + "'";
	   try
	   {
	   		if ( existeNO() ) {
		    	PreparedStatement stmt = c.prepareStatement(query);
				stmt.executeUpdate();
	        	stmt.close();
	        	limparCampos();
	     	}
	     	else {
				JOptionPane.showMessageDialog (this, "Notificação inexistente !", "Alteração de Notificação", JOptionPane.ERROR_MESSAGE);	     		
	     	}
   	   }
	   catch(SQLException sqle)
	   {
		  System.out.println(sqle.toString());		  
	   }
	   return;
	}


	private void findNotificacao() {

		String query = "SELECT * FROM tabNO WHERE numeroOSNO = '" + os + "' AND cmcEmpresaNO = '" + cmc + "' AND codigoInfracaoNO = '" + codigoInfracaoTxt.getText() + "'";
	
	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	      	if (rs.next()) {
   	   		    descricaoInfracaoArea.setText( rs.getString(4) );
   	   		    exigenciasArea.setText( rs.getString(5) );
   	   		}
	     	else {
				JOptionPane.showMessageDialog (this, "Notificação inexistente !", "Consulta de Notificação", JOptionPane.ERROR_MESSAGE);	     		
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
	   String exigencias = "";
	   String codigoInfracao = codigoInfracaoTxt.getText();
		int qtdeLinhas = 0;
	   
	   String query = "SELECT * FROM tabNO a, tabInfracao i WHERE a.numeroOSNO = '" + os + "' AND a.cmcEmpresaNO = '" + cmc +"' AND a.codigoInfracaoNO = '" + codigoInfracaoTxt.getText() + "' AND a.codigoInfracaoNO = i.codigoInfracao";
	   
		String texto  = "Digite quais vias para impressão :\n";
		texto += "     1=1a        |        2=2a        |       3=3a\n";
		texto += "12=1a e 2a  |  13=1a e 3a  |  23=2a e 3a\n";
		texto += "123=1a, 2a e 3a"; 
	   
		String vias = JOptionPane.showInputDialog ( texto );
	   
	   try
	   {
	   		if ( existeNO() ) {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	   
	      	while (rs.next()) {
   	   		    descricaoInfracao = rs.getString(4);
				exigencias = rs.getString(5);
				qtdeLinhas = 20 - ( rs.getString(4).length() / 80 );  // linhas para completar a pagina				
   	   		}
		    rs.close();
	        stmt.close();

		  for ( int i = 0; i < vias.length(); i++ ) {
				char via = vias.charAt( i );
				
			fileName = "c:\\sep\\html\\" + cmc + "-" + razaoSocial + "\\NO-";
			fileName += codigoInfracao + "-Via-" + via + ".htm";

			resultado = "";
			resultado = "<html><head><title>Notificação</title></head><body>";
			resultado+= "<table border=1 cellspacing=0 cellpadding=0 width=100%>";
    		resultado+= "<tr valign=top align=center>";
        	resultado+= "<td width=25%><img src=../brasao.gif align=middle>PMT<br>Secretaria Municipal<br>de Finanças</td>";
        	resultado+= "<td width=50%>Notificação</td>";
        	resultado+= "<td width=25%>Protocolo</td></tr></table>";
			resultado+= "<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
			resultado +="<tr><td width=60%><font size=3 face=Times New Roman>Razão Social<br>" + razaoSocial + "</td>";
        	resultado +="<td width=30%><font size=3 face=Times New Roman>CMC No.<br>" + SEPConverter.insertMaskCMC( cmc ) + "</td>";			
        	resultado+= "<td width=10% align=center><font size=3 face=Times New Roman>Via<br>" + via + "a.</td></tr></table>";
//			resultado+= "<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
//        	resultado+= "<td>Denominação do Estabelecimento ou Atividade Profissional</td>";
//    		resultado+= "</tr><tr><td><p align=justify>Conteudo</td></tr></table>";
//			resultado+= "<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
//        	resultado+= "<td>Domicílio Tributário</td></tr><tr>";
//        	resultado+= "<td><p align=justify>Conteudo</td></tr></table>";
			resultado+= "<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
        	resultado+= "<td width=50%><font size=3 face=Times New Roman>Local<br>Teresina</td>";
        	resultado+= "<td width=30%><font size=3 face=Times New Roman>Data<br>____/____/________</td>";
        	resultado+= "<td width=20%><font size=3 face=Times New Roman>Hora<br>____:____</td></tr></table>";
			resultado+= "<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
        	resultado+= "<td align=center><font size=3 face=Times New Roman>Discriminação do Motivo da Lavratura</td>";
			resultado+= "<tr><td><p align=justify><font size=3 face=Times New Roman>" + PrintReport.insereBR( descricaoInfracao );
			for ( int ind = 0; ind < qtdeLinhas; ind++ ) {
				resultado += ".<br>";
			}
    		
			resultado +="&nbsp;<br></td></tr></table>";
			resultado+= "<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
        	resultado+= "<td align=center>Exigências a serem Cumpridas</td></tr>";
    		resultado+= "<tr><td><p align=justify><font size=3 face=Times New Roman>";
    		resultado+= PrintReport.insereBR( exigencias ) + "</td></tr></table>";
			resultado+= "<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
        	resultado+= "<td align=center><font size=3 face=Times New Roman>Notificação</td></tr><tr>";
        	resultado+= "<td><p align=justify><font size=3 face=Times New Roman>" + PrintReport.paragrafo();
        	resultado+= "Recebi a 2a. via e declaro estar notificado e ciente de que o não cumprimento das exigências formuladas, sujeitar-me-á às penalidades previstas na Legislação Tributária em vigor.<p>";

			resultado+= "<table border=0 cellspacing=0 cellpadding=0 width=100%>";
	   		resultado+= "<tr><td><font size=3 face=Times New Roman>Teresina, ____/____/________</td>";
			resultado+= "<td><font size=3 face=Times New Roman>___________________________________<br>";
		    resultado+= "Notificado ou Representante Legal</td>";
           	resultado+= "</tr><tr><td><font size=3 face=Times New Roman>&nbsp;<p>___________________________________<br>";
			resultado+= "1a. Testemunha</td><td><font size=3 face=Times New Roman>&nbsp;<p>___________________________________<br>";
			resultado+= "2a. Testemunha</td></tr></table>";
			resultado+= "<table border=1 cellspacing=0 cellpadding=0 width=100%><tr>";
        	resultado+= "<td align=center><font size=3 face=Times New Roman>Observações</td></tr><tr>";
        	resultado+= "<td><p align=justify><font size=3 face=Times New Roman>" + PrintReport.paragrafo();
        	resultado+= "No exercício da função de Agente(s) Fiscal(is) notificamos e intimamos, nos termos descritos acima.";
			resultado+= "<p align=center><font size=3 face=Times New Roman>Teresina, ____/____/________";
			resultado+= "<table border=0 cellspacing=0 cellpadding=0 width=100%>";
           	resultado+= "<tr><td><font size=3 face=Times New Roman>&nbsp;<p>___________________________________<br>";
			resultado+= "Agente Fiscal</td><td><font size=3 face=Times New Roman>&nbsp;<p>___________________________________<br>";
			resultado+= "Agente Fiscal</td></tr></table></tr></table></body></html>";

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
				JOptionPane.showMessageDialog (this, "Notificação inexistente !", "Impressão de Notificação", JOptionPane.ERROR_MESSAGE);	     		
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

	private void listNO() {

	   String query = "SELECT * FROM tabNO WHERE numeroOSNO = '" + os + "' AND cmcEmpresaNO = '" + cmc + "' ORDER BY codigoInfracaoNO";
	   listaNO = "";	   
	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	      	while (rs.next()) {
   	   		    listaNO = listaNO + rs.getString(3) + "  ";
   	   		}
		    rs.close();
	        stmt.close();
			listaNOLabel.setText( listaNO );
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
		exigenciasArea.setText( "" );
		
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
	

	private boolean existeNO() {

		boolean resposta = false;
		String query = "SELECT * FROM tabNO WHERE numeroOSNO = '" + os + "' AND cmcEmpresaNO = '" + cmc + "' AND codigoInfracaoNO = '" + codigoInfracaoTxt.getText() + "'";
		
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
		inputTabNOPanel.add( c );	
	}
    
}
