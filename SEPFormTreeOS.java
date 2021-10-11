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
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class SEPFormTreeOS extends JPanel {
	
	private Connection myConnection = null;
	private Map searchEngineCompanies = null;
	private JTree osTree = null;
	private SEPMain sepMain = null;
	
	public SEPFormTreeOS( SEPMain owner, Connection con ) {
			myConnection = con;
			sepMain = owner;
	}


	public JPanel createTreeOSPanel(){

	    // cria panel para exibir ordens de servicos disponiveis 	
		JPanel displayTreeOSPanel = new JPanel( new BorderLayout() );
		Border etchedDisplayOSPanel = BorderFactory.createEtchedBorder();
        TitledBorder titleBorderDisplayOSPanel;
        titleBorderDisplayOSPanel = BorderFactory.createTitledBorder(
		       etchedDisplayOSPanel, "Selecione uma empresa");
        titleBorderDisplayOSPanel.setTitleJustification(TitledBorder.RIGHT);
        displayTreeOSPanel.setBorder( titleBorderDisplayOSPanel );
		
		// constroi arvore
		TreeNode root = makeOSTree( myConnection ) ;
		DefaultTreeModel model = new DefaultTreeModel( root );
		osTree = new JTree() ; 
		osTree.setModel( model );
		osTree.setEditable( false );
		JScrollPane scrollPane = new JScrollPane( osTree );
		
		displayTreeOSPanel.add( scrollPane );
		
		JPanel controlOSPanel = new JPanel();
		JButton choicenOSButton = new JButton( "Abrir");
		choicenOSButton.setToolTipText("Seleciona empresa para " +
		              " iniciar processo de fiscalização " );
		choicenOSButton.setMnemonic('A');
		              
		choicenOSButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				DefaultMutableTreeNode selectedNode =
				   ( DefaultMutableTreeNode )
				     osTree.getLastSelectedPathComponent();
				     
				if ( selectedNode != null ) {
					String cmc =  (String)
					  searchEngineCompanies.get( selectedNode.toString() );    
					  
					TreeNode node = (TreeNode) selectedNode.getParent() ;	  
					String os = node.toString();
					
					sepMain.setStatusBar( " Ordem de serviço " + os +
					 " para a empresa " + selectedNode + 
					 "( Incr.Mun " + cmc + ") ABERTA ") ;
					 
					 repaint();
					 
					 // set current OS
					 EmpresaAtual.setNumeroOS( os );
					 EmpresaAtual.setCMCEmpresa( cmc );
//					 EmpresaAtual.setRazaoSocialEmpresa( selectedNode.toString() );
					 EmpresaAtual.setCNPJRazaoSocialEndereco( cmc, myConnection );
					
				}     
			}
		});
		
		controlOSPanel.add( choicenOSButton );
		
		Border etchedControlOS = BorderFactory.createEtchedBorder();
 		controlOSPanel.setBorder( etchedControlOS );
		
		
		JPanel treeOSPanel = new JPanel( new BorderLayout() );
		treeOSPanel.add( displayTreeOSPanel );
		treeOSPanel.add( controlOSPanel, BorderLayout.SOUTH );
		return treeOSPanel;
		 
	}
	
	
	
	private TreeNode makeOSTree( Connection con ) {			
	
        searchEngineCompanies = new HashMap();		
	
	    DefaultMutableTreeNode root,osNum, companyName  ;
		
		root =  new DefaultMutableTreeNode("Ordem de serviços disponíveis");
		
		try {		
			
		
			Statement stmt = con.createStatement();
			String loggedUser = EmpresaAtual.getUsuario();
			
			//String queryOS = " SELECT DISTINCT codigoOS FROM tabOS" ;

			String queryOS = "SELECT DISTINCT tabOSFiscal.codigoOSFiscal " +
			        " FROM tabOSFiscal " +
			           " WHERE tabOSFiscal.matrOSFiscal = " + loggedUser +
			              " ORDER BY tabOSFiscal.codigoOSFiscal " ;
			
			
			
			ResultSet rsOS = stmt.executeQuery( queryOS );
			String recOS = null;
			while ( rsOS.next() ) {
				
				recOS = rsOS.getString( 1 ) ;
				
				osNum =  new DefaultMutableTreeNode( recOS );				
				
				String queryCompanies =
			  	" SELECT tabEmpresa.cmcEmpresa, tabEmpresa.nomeEmpresa " +
			  	      " FROM tabEmpresa, tabOS " +
			          " WHERE tabOS.codigoOS ='" +  recOS + "'" +
			          " AND tabEmpresa.cmcEmpresa = tabOS.cmcEmpresaOS " ; 			          			                          
			
				Statement stmt2 = con.createStatement();
				ResultSet rsCompanies = stmt2.executeQuery( queryCompanies );
				while ( rsCompanies.next() ) {
					String recCompanyCode = rsCompanies.getString( 1 );
					String recCompanyName = rsCompanies.getString( 2 );
					companyName = new DefaultMutableTreeNode( recCompanyName );
					osNum.add( companyName );    				
					
					// Ensures there is not dupplicates records	
					Object dummy = searchEngineCompanies.get( recCompanyName );
					if ( !(searchEngineCompanies.containsKey( recCompanyName )) ) {						
						searchEngineCompanies.put( recCompanyName, recCompanyCode );
					}	
					
					
				} // end of inner while  
				 
				root.add( osNum );
				
				
			
			} // end of outer while
		}
		catch ( SQLException ignore ) {
			ignore.printStackTrace();
		}	
		
		return root;
		
	}
	
	
}
