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
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
//import java.io.*;
import java.util.*;

// PENDENCIA:
//  1. Retornar um numero especifico de linhas 
//  2. Implementar uma função para retornar as linhas selecionadas
//
//

public class SEPFormExportController extends JPanel {
	
	static SEPFormExportModel model;   

	static SEPFormExportView view;	
	
	private static final int SHORTEST_SEARCH = 1;
	private static final int LONGEST_SEARCH = 2;	
	
	
	JFrame owner = null;
	//
	
	public SEPFormExportController( JFrame owner, Connection conexao ) {
		super();
		this.owner = owner;
		model = new SEPFormExportModel( conexao );	
		view = new SEPFormExportView();
		
		setLayout( new BorderLayout() );
		model.addObserver( view );
		model.loadRecords( conexao );
		//view.setData( model.getRecords() );		
        registerListeners();		
        
		JPanel p = createWidgets();
		add( p );
	}
	
	private JPanel createWidgets()  {
		JPanel result = new JPanel( new BorderLayout() );
		JPanel outputPanel = new JPanel( new BorderLayout() ) ;
		outputPanel.add( view );
        result.add( outputPanel );
        
        return result;
	}
	
	private class FilterDlg extends JDialog {
		int chosenSearchCriteria ;
		
		FilterDlg( JFrame owner ) {
			super( owner, "Seleção de ordens de serviços", true ) ;
			createWidgets();
		}
		
		public int getChosenSearchCriteria() {
			return chosenSearchCriteria;
		}
		
		private void createWidgets() {
			Container contentPane = null;
			contentPane = getContentPane();
			JPanel choosePanel = new JPanel( new GridLayout(2,1, 5, 5 ) );
		    JRadioButton shorterSearchRadioButton 
		      = new JRadioButton( "Selecione as 12 últimas ordens de serviço" );
		      
		      
  		    JRadioButton longSearchRadioButton 
  		       = new JRadioButton( "Selecione todas as ordens de serviço" );		
  		       
  		    choosePanel.add( shorterSearchRadioButton );
  		    choosePanel.add( longSearchRadioButton );
  		       
  		       
  		       
		    longSearchRadioButton.setSelected( true );
		    
		    ButtonGroup chooseSearchButtonGroup = new ButtonGroup();
		    chooseSearchButtonGroup.add( shorterSearchRadioButton );
		    chooseSearchButtonGroup.add( longSearchRadioButton );
		    
		    shorterSearchRadioButton.addActionListener( new ActionListener() {
			    public void actionPerformed( ActionEvent evt ) {
			    	chosenSearchCriteria = SHORTEST_SEARCH;
				}
		    });
		    
		    longSearchRadioButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
			    	chosenSearchCriteria = LONGEST_SEARCH;
				}
		    });
		    
			JPanel controlPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
			JButton okButton = new JButton( "OK");
			
			okButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent evt ) {
					model.filterOrders( chosenSearchCriteria );
					dispose();
				}
			});
			
			JButton cancelButton = new JButton( "Cancela" );
			cancelButton.addActionListener( new ActionListener() {
				public void actionPerformed( ActionEvent evt ) {
				    dispose();
				}
			});
			controlPanel.add( okButton );
			controlPanel.add( cancelButton );
			
			contentPane.add( choosePanel, BorderLayout.CENTER );
			contentPane.add( controlPanel, BorderLayout.SOUTH );
			
		
		}
		
		
	}
	
	private void registerListeners() {
		view.searchOrdersAction( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				FilterDlg filterDlg = new FilterDlg( owner );
				filterDlg.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
				filterDlg.setResizable( false );
				filterDlg.pack();
				filterDlg.setSize( 400, 150 );
				Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
				int x = (int ) ( ( d.getWidth() - filterDlg.getWidth() ) / 2 );
				int y = (int ) ( ( d.getHeight() - filterDlg.getHeight() ) / 2);
				filterDlg.setLocation( x, y );
				filterDlg.setVisible( true );
				model.filterOrders( filterDlg.getChosenSearchCriteria() );
			}
		});
		
		// Initially, getting the chosen orders and after that export ones
		view.exportOrdersAction( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				view.getChosenOrders();
				
			}
		});
	}
		

}

class SEPFormExportView extends JPanel implements Observer {
	
	private JTree osTree = null;	
	private HashMap os = new HashMap() ;
	JButton filterSearchButton = new JButton( "Filtrar");
	JButton exportButton = new JButton( "Salvar");	
	
	
	public SEPFormExportView() {
		super();
		setLayout( new BorderLayout() );
		createViewPanel();
	}
	
	public void setData( HashMap records ) {
		if ( os == null ) {
			os = new HashMap();
		}
		os = records;
	}
	
	public void createViewPanel() {	
		// Create panel display services order
		JPanel displayTreeOSPanel = new JPanel( new BorderLayout() );
        Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( 
                        etched, "Selecione empresa(s)");
        titleBorder.setTitleJustification( TitledBorder.RIGHT );
        displayTreeOSPanel.setBorder( titleBorder );
        
        // building tree
        DefaultTreeModel model = getTreeModel();
        osTree = new JTree( model );
        osTree.setEditable( false );
        JScrollPane scrollPane = new JScrollPane( osTree );
        displayTreeOSPanel.add( scrollPane );
        add( displayTreeOSPanel );
        
        JPanel controlPanel = new JPanel ( new FlowLayout( FlowLayout.CENTER ) );
        controlPanel.add( filterSearchButton );
        controlPanel.add( exportButton );
        
                
        add ( controlPanel, BorderLayout.SOUTH );
	}
	
	
	public void update( Observable observable, Object arg ) {
		os = new HashMap();
		HashMap map = (HashMap)(((SEPFormExportModel)observable).getRecords() );
		os = map;
        // building tree
        DefaultTreeModel model = getTreeModel();
        osTree.setModel( model );
	}
	
	public void searchOrdersAction( ActionListener action ) {
		filterSearchButton.addActionListener( action );
		
	}
	
	public void exportOrdersAction( ActionListener action ) {
		exportButton.addActionListener( action );
	}
	
	private DefaultTreeModel getTreeModel() {
				
		DefaultTreeModel model = null;
	
		DefaultMutableTreeNode root, osNode, companyNode;
		
		root = new DefaultMutableTreeNode( "Ordens disponíveis");		
		Set keys = os.keySet();
		Iterator it = keys.iterator();
		while ( it.hasNext() ) {
			String osDesc = (String) it.next();
			osNode = new DefaultMutableTreeNode( osDesc );
			String[] companies = (String[]) os.get( osDesc );
			for ( int i = 0; i < companies.length; i++ ) {
				String aCompany = companies[ i ] ;
				companyNode = new DefaultMutableTreeNode( aCompany );
				osNode.add( companyNode );
			}
			root.add( osNode );
		}
		model = new DefaultTreeModel( root );
		
		return model;
	}
	
	public Vector getChosenOrders() {
		
		TreePath[] chosenNodes = ( TreePath[] ) osTree.getSelectionPaths();
		Vector retVal = new Vector();
		
		for ( int i = 0; i < chosenNodes.length; i++ ) {
			String cmc = chosenNodes[ i ].getLastPathComponent().toString();
			String order = chosenNodes[ i ].getPathComponent( 1 ).toString();
			System.out.println( " cmc = " + cmc +
			                   " order = " + order );
			                   
			if ( !cmc.equals( order ) ) {                   
			    retVal.add( new ChosenNode( order, cmc ) );
			 }    
			
		}
		
		return retVal;
		
	}
	
	
	
}

class SEPFormExportModel extends Observable {
	
	DefaultTreeModel model = null;
	HashMap os = null;
	Connection conexao = null;
	
	private static final int SHORTEST_SEARCH = 1;
	private static final int LONGEST_SEARCH = 2;
	private static final int READ_RECORDS = 1;	

	
	public SEPFormExportModel( Connection conexao ) {
		this.conexao = conexao;
		loadRecords( conexao );
		setChanged();
		notifyObservers();		
	}
	
	public HashMap getRecords() {
		return os;
	}
	
	public void filterOrders( int criteria ) {	
	  	doSearch( conexao, criteria );
		setChanged();
		notifyObservers();
	}
	
	
	public void loadRecords( Connection conexao ) {
		doSearch( conexao, LONGEST_SEARCH) ;
	}
	
	
	private void doSearch( Connection conexao, int criteria ) {
		os = new HashMap();
		Vector result = new Vector();
		
		try {
			
			String loggedUser = EmpresaAtual.getUsuario();
			Statement stmt = conexao.createStatement();	
			String queryOS = null;
		    
	        queryOS = "SELECT DISTINCT tabOSFiscal.codigoOSFiscal " +
	           " FROM tabOSFiscal " +
	             " WHERE tabOSFiscal.matrOSFiscal = " + loggedUser +
	              " ORDER BY tabOSFiscal.codigoOSFiscal DESC" ;
	              
	              

			ResultSet rsOS = stmt.executeQuery( queryOS );
			String recOS = null;
			int countRec = 0;
			while ( rsOS.next() ) {	
			 
			    countRec++;
			
			    if ( ( criteria == SHORTEST_SEARCH )
			            && ( countRec > READ_RECORDS ) ) {
			            	break;
			    }        	
			
			
				Vector tmp = new Vector();				
				recOS = rsOS.getString( 1 ) ;

				String queryCompanies =
		  		" SELECT tabEmpresa.cmcEmpresa, tabEmpresa.nomeEmpresa " +
			  	      " FROM tabEmpresa, tabOS " +
			          " WHERE tabOS.codigoOS ='" +  recOS + "'" +
			          " AND tabEmpresa.cmcEmpresa = tabOS.cmcEmpresaOS " ; 			          			                          
			          
				

				Statement stmt2 = conexao.createStatement();
				ResultSet rsCompanies = stmt2.executeQuery( queryCompanies );
				while ( rsCompanies.next() ) {
					String recCompanyName = rsCompanies.getString( 2 );
					
					tmp.add( recCompanyName );
					
				} // inner while	
				
        		String[] companies = new String[ tmp.size() ];
        		companies = (String[]) tmp.toArray( companies );

				os.put( recOS, companies );
				
			} // outer while	
		
		} catch ( SQLException sqlerr ) {
			  sqlerr.printStackTrace();
		}	

	}
	
	
	
	
}

class ChosenNode {
	private String order;
	private String cmc;
	ChosenNode( String order, String cmc ) {
		this.order = order;
		this.cmc = cmc;
	}		
		
	public String getOrder() {
		return order;
	}
	public String getCMC() {
		return cmc;
	}
}

