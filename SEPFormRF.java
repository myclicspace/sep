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

public class SEPFormRF extends JPanel { 
	
   	private String currOS = null;
	private String currCMC = null;
	private static final String ROOT_TXT = "Rotinas de fiscalização efetuadas";
	private Connection myConnection = null;
	private Map searchEngineCompanies = null;
	private JTree rfTree = null;
	private Vector rotinasNames = null;
	private Vector quesitosNames = null;
	private JComboBox rotinasCombo;	
	private JComboBox quesitosCombo = null;
	private QuizzComboModel quizzComboModel = null;
	private	JButton addRotinaButton = null;
	private JButton addQuesitoButton = null;
	private JButton deleteQuesitoButton	 = null;
	private JButton printReportButton = null;
	private	TreeNode root = null ;
	private	DefaultTreeModel model = null;
	
	
	
	public SEPFormRF( Connection con ) { // SEPMain owner, Connection con ) {
			myConnection = con;
			loadRotinas( con );
	}


	public JPanel createTreeRFPanel(){
		
		// cria panel para escolha da rotina
		JPanel chosenPanel = new JPanel( new GridLayout( 2, 4 ) ) ;
		JLabel rotinaLabel = new JLabel("Descrição da rotina" );
		rotinasCombo = new JComboBox( rotinasNames );
		JLabel quesitoLabel = new JLabel("Descrição do quesito");
		
		String str = (String)rotinasCombo.getItemAt( 0 );
		StringTokenizer st = new StringTokenizer( str,"-");
		String chosenRotina = st.nextToken();		
		
		updateQuesitos( chosenRotina );
		
		rotinasCombo.addItemListener( new ItemListener() {
			public void itemStateChanged( ItemEvent e ) {
				String str = (String)rotinasCombo.getSelectedItem();
 				StringTokenizer st = new StringTokenizer( str,"-");
				String chosenRotina = st.nextToken();
				updateQuesitos( chosenRotina );
			}
		});
		
		chosenPanel.add( rotinaLabel );
		chosenPanel.add( rotinasCombo );
		chosenPanel.add( quesitoLabel );
		chosenPanel.add( quesitosCombo );				

	    // cria panel para exibir ordens de servicos disponiveis 	
		JPanel displayTreeRFPanel = new JPanel( new BorderLayout() );
		Border etchedDisplayRFPanel = BorderFactory.createEtchedBorder();
        TitledBorder titleBorderDisplayRFPanel;
        titleBorderDisplayRFPanel = BorderFactory.createTitledBorder(
		       etchedDisplayRFPanel, "Rotinas de fiscalização realizadas");
        titleBorderDisplayRFPanel.setTitleJustification(TitledBorder.RIGHT);
        displayTreeRFPanel.setBorder( titleBorderDisplayRFPanel );
		
		// constroi arvore
		root = makeRFTree( myConnection ) ;
		model = new DefaultTreeModel( root );
		rfTree = new JTree() ; 
		rfTree.setModel( model );
		rfTree.setEditable( false );
		JScrollPane scrollPane = new JScrollPane( rfTree );
		
		displayTreeRFPanel.add( scrollPane );
		
		JPanel controlRFPanel = new JPanel();
		addRotinaButton = new JButton( "Incluir rotina");
		addRotinaButton.setToolTipText("Incluir rotina de fiscalização " );
		addRotinaButton.setMnemonic('R');	
		
		addRotinaButton.addActionListener( new ActionListener() {
		 public void actionPerformed( ActionEvent event ) {
		 	// add a routine
			String chosenRoutine = (String) rotinasCombo.getSelectedItem();
			int selectedIndex = root.getIndex( root );
			DefaultMutableTreeNode theRoot 
		   		= (DefaultMutableTreeNode)model.getRoot();
			DefaultMutableTreeNode newRoutine 
		    	= new DefaultMutableTreeNode( chosenRoutine ) ;
		    	
		    // check for dupplicated routines...
			DefaultMutableTreeNode foundRoutine 
					= findUserObject( chosenRoutine );				
			if ( foundRoutine != null ) {
				String err = "Tentativa de incluir rotina já existente";
				JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
			}
			else {
				model.insertNodeInto( newRoutine, theRoot, selectedIndex + 1 ); 			
				
			}		
		 }	
		});
		              
		addQuesitoButton = new JButton( "Incluir quesito");
		addQuesitoButton.setToolTipText("Incluir quesito de fiscalização " );
		addQuesitoButton.setMnemonic('Q');	
		addQuesitoButton.addActionListener( new ActionListener() {
		 public void actionPerformed( ActionEvent event ) {
		
		   	// add a quizz
			String chosenRoutine = (String) rotinasCombo.getSelectedItem();
			String chosenQuizz = (String) quesitosCombo.getSelectedItem();
			
			DefaultMutableTreeNode foundQuizz 
					= findUserObject( chosenQuizz );				
			if ( foundQuizz != null ) {
				String err = "Tentativa de incluir quesito já existente";
				JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
			     return; 
			}
			
			
			if ( chosenQuizz != null ) {
				DefaultMutableTreeNode newQuizz 
			    	= new DefaultMutableTreeNode( chosenQuizz ) ;
				DefaultMutableTreeNode quizzParent 
					= findUserObject( chosenRoutine );				
				    
				model.insertNodeInto( newQuizz, quizzParent, 
			   		quizzParent.getChildCount() );
				   
				TreeNode[] nodes = model.getPathToRoot( newQuizz );
				TreePath path = new TreePath( nodes );
				rfTree.scrollPathToVisible( path );  
				rfTree.setModel( model );
				rfTree.revalidate();
				repaint(); 
				
				addDatabaseQuizz( chosenRoutine, chosenQuizz );
				
			}
		 }	
		});
		

		deleteQuesitoButton = new JButton( "Exluir");
		deleteQuesitoButton.setToolTipText("Excluir quesito ou rotina vazia" );
		deleteQuesitoButton.setMnemonic('E');
		deleteQuesitoButton.addActionListener( new ActionListener() {
		 public void actionPerformed( ActionEvent event ) {
		 	DefaultMutableTreeNode selectedNode 
		        = ( DefaultMutableTreeNode)
		           rfTree.getLastSelectedPathComponent();
		           
		    if ( selectedNode == null ) return ;
		
			// Ensures removes only quizzes
		    if ( selectedNode.getChildCount() != 0 ) {
				String err = "Remova primeiro todos os quesitos";
				JOptionPane.showMessageDialog( null, err,
			      "Exclusão inválida", JOptionPane.ERROR_MESSAGE   );
		    	return ;
		    }		    
	    

		    String quizz = selectedNode.toString();
		    TreeNode parent = selectedNode.getParent();		    
		    String routine = parent.toString();
		    
		    // deletes a quizz
		    model.removeNodeFromParent( selectedNode ) ;
	    
		    
		    if ( routine != ROOT_TXT ) {
				deleteDatabaseQuizz( routine, quizz );		    
		    }		    
		    
			return;
		 }	
		});
		
		printReportButton = new JButton( "Relatório");
		printReportButton.setToolTipText("Imprimir relatório de fiscalização" );
		printReportButton.setMnemonic('R');
		
		
		
		
		controlRFPanel.add( addRotinaButton );
		controlRFPanel.add( addQuesitoButton );
		controlRFPanel.add( deleteQuesitoButton );
		controlRFPanel.add( printReportButton );
		
		
		Border etchedControlRF = BorderFactory.createEtchedBorder();
 		controlRFPanel.setBorder( etchedControlRF );
		
		
		JPanel treeRFPanel = new JPanel( new BorderLayout() );
		treeRFPanel.add( chosenPanel, BorderLayout.NORTH );
		treeRFPanel.add( displayTreeRFPanel );
		treeRFPanel.add( controlRFPanel, BorderLayout.SOUTH );
		return treeRFPanel;
		 
	}
	
	
	
	private TreeNode makeRFTree( Connection con ) {			
	
        searchEngineCompanies = new HashMap();		
	
	    DefaultMutableTreeNode root,rfNum, fullQuizz  ;
		
		root =  new DefaultMutableTreeNode( ROOT_TXT ) ; 
		
		try {		
			
		
			Statement stmt = con.createStatement();
//			String loggedUser = EmpresaAtual.getUsuario();

			String queryRF = "SELECT DISTINCT tabRF.rotinaRF, " +
			 " tabRotinas.descricaoRotina  FROM tabRF, tabRotinas " +
			 " WHERE tabRotinas.codigoRotina = tabRF.rotinaRF "  + 
//             " AND tabRF.osrf = '" + currOS + "'" +  
//             " AND tabRF.cmcempresarf = '" + currCMC + "'" +
			 " ORDER BY tabRF.rotinaRF " ;			 
			
			
			ResultSet rsRF = stmt.executeQuery( queryRF );
			String codeRF = null;
			String descRF = null;
			while ( rsRF.next() ) {
				
				codeRF = rsRF.getString( 1 ) ;
				descRF = rsRF.getString( 2 );
				
				String fullRF = codeRF + "-" + descRF ;
				
				
				rfNum =  new DefaultMutableTreeNode( fullRF );	
				
		   	   	currOS = EmpresaAtual.getNumeroOS();
  	    		currCMC = EmpresaAtual.getCMCEmpresa();
				String queryQuizzes =
			  	" SELECT tabRF.quesitoRF  FROM tabRF " +
			          " WHERE tabRF.rotinaRF ='" +  codeRF + "'" +
			          " AND tabRF.osrf = '" + currOS + "'" +  
			          " AND tabRF.cmcempresarf = '" + currCMC + "'" +
              		  " ORDER BY tabRF.quesitoRF " ;			 
			
				Statement stmt2 = con.createStatement();
				ResultSet rsQuizzes = stmt2.executeQuery( queryQuizzes );
				while ( rsQuizzes.next() ) {
					String quizzCode = rsQuizzes.getString( 1 );				
					
					
 					String queryFullQuizzes =
			  		" SELECT tabQuesito.descQuesito  FROM tabQuesito " +
			          " WHERE tabQuesito.codigoRotina ='" +  codeRF + "'" +
			          " AND tabQuesito.codigoQuesito = '" + quizzCode + "'" ;
			          
					Statement stmt3 = con.createStatement();				
					ResultSet rsFullQuizzes = stmt3.executeQuery( queryFullQuizzes );
					rsFullQuizzes.next();	
					
  				    String quizzName = rsFullQuizzes.getString( 1 );
				
					String descFullQuizz = quizzCode + "-" + quizzName;
					
					fullQuizz = new DefaultMutableTreeNode( descFullQuizz );
					
					rfNum.add( fullQuizz );    				
					
					
				} // end of inner while  
				 
				root.add( rfNum );
				
				
			
			} // end of outer while
		}
		catch ( SQLException ignore ) {
			ignore.printStackTrace();
		}	
		
		return root;
		
	}
	
	
	private void loadRotinas( Connection con ) {
		rotinasNames = new Vector();
		String rotinaCode;
		String rotinaName;
		try {
			
			Connection theConn = con;
			Statement stmt = theConn.createStatement();
			String query = "SELECT codigoRotina, descricaoRotina " +
		               " FROM tabRotinas " +
//		               " AND tabRF.osrf = '" + currOS + "'" +  
//        		       " AND tabRF.cmcempresarf = '" + currCMC + "'" +
		               " ORDER BY codigoRotina" ;
		               
			ResultSet rs = stmt.executeQuery( query );		
		
			/**
		 	* Assembles the rotinasCombo component and the rotinas hash with
		 	* your matching records of tabrotinas table
		 	*/
		 	while ( rs.next() ) {
		 		rotinaCode = (String)rs.getString( 1 );
		 		rotinaName = (String)rs.getString( 2 );
		 		String fullRotina = rotinaCode + "-" + rotinaName;
		 		rotinasNames.add( fullRotina );
		 	}		 
		}
		catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		}
	}	
	
	private void updateQuesitos( String chosenRotina ) {
		Vector quesitosNames = new Vector() ;
		String quesitoCode;
		String quesitoName;
		try {
			
			Statement stmt = myConnection.createStatement();
			String query = " SELECT tabQuesito.codigoQuesito, tabQuesito.descQuesito " +
		                   " FROM tabQuesito " + 
		                   " WHERE tabQuesito.codigoRotina =" + chosenRotina  +
		                   " ORDER BY codigoQuesito " ;
			ResultSet rs = stmt.executeQuery( query );		
		
			/**
		 	* Assembles the rotinasCombo component and the rotinas hash with
		 	* your matching records of tabrotinas table
		 	*/
		 	while ( rs.next() ) {
		 		quesitoCode = (String)rs.getString( 1 );
		 		quesitoName = (String)rs.getString( 2 );
		 		String fullQuesito = quesitoCode + "-" + quesitoName;
		 		quesitosNames.add( fullQuesito );
		 	}		 
		}
		catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		}
		
        quizzComboModel = new QuizzComboModel( quesitosNames );
        if ( quesitosCombo == null ) {
        	quesitosCombo = new JComboBox( quizzComboModel );
        }
        else {
        	quesitosCombo.setModel( quizzComboModel );
        }
       	quesitosCombo.revalidate();
	}
	
	
	private class QuizzComboModel extends DefaultComboBoxModel {
		public QuizzComboModel( Vector quesitos ) {
			for ( int i = 0; i < quesitos.size(); i++ ) {
				addElement( quesitos.get( i ) );
			}
		}
	}
	
	private DefaultMutableTreeNode findUserObject( Object obj ) {
		DefaultMutableTreeNode root 
			   = (DefaultMutableTreeNode)model.getRoot();		
		Enumeration e = root.breadthFirstEnumeration();
		while ( e.hasMoreElements() ) {
			DefaultMutableTreeNode node 
			    = ( DefaultMutableTreeNode ) e.nextElement();
			if ( node.getUserObject().equals( obj ) ) {
				return node;
			}    
		}
		return null;
	}
	
	
	private void addDatabaseQuizz( String routine, String quizz ) {
		
		StringTokenizer st = new StringTokenizer( routine,"-");
		String routineCode = st.nextToken();

		StringTokenizer st2 = new StringTokenizer( quizz,"-");
		String quizzCode = st2.nextToken();
		
		
		try {	
			// -- The calling for including records is done using batch process			
			String cmd = "INSERT INTO tabRF VALUES  ('" 
			              + currOS + "','" + currCMC + "','" 
			              + routineCode  + "','" +  quizzCode + "')"; 
		             
			Statement stmt = myConnection.createStatement();             
			stmt.executeUpdate( cmd );
			myConnection.commit();			
		}
		catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		}	
		
	}
	
	private void deleteDatabaseQuizz( String routine, String quizz ) {
		StringTokenizer st = new StringTokenizer( routine,"-");
		String routineCode = st.nextToken();

		StringTokenizer st2 = new StringTokenizer( quizz,"-");
		String quizzCode = st2.nextToken();

		try {	

			// -- The calling for deleting records is done
			String cmd = "DELETE FROM  tabRF" + 
		             " WHERE rotinaRF = '" + routineCode + "'" +
		             " AND quesitoRF = '" + quizzCode + "'"  +  
			          " AND osRF = '" + currOS + "'" +  
			          " AND cmcempresaRF = '" + currCMC + "'" ;
		             
		             
		             
			Statement stmt = myConnection.createStatement();             
			stmt.executeUpdate( cmd );
			myConnection.commit();			

		}
		catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		}	

	
	}
	
	
	
	
}
