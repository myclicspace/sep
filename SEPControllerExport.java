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
//import java.sql.*;
//import java.io.*;
import java.util.*;

public class SEPControllerExport extends JPanel {
	
	static SEPModelExport model = new SEPModelExport();
	static SEPViewExport view = new SEPViewExport();	
	//
	
	public SEPControllerExport() {
		model.addObserver( view );
		createWidgets();
	}
	
	private JPanel createWidgets()  {
		
		JPanel result = new JPanel( new BorderLayout() );
		
		JPanel controlPanel = getControlPanel();
		JPanel outputPanel = new JPanel( new BorderLayout() ) ;
		outputPanel.add( view );
		
        result.add( view,  BorderLayout.CENTER );
        result.add( view,  BorderLayout.SOUTH );
        
        return result;
	}
	
	private JPanel getControlPanel() {
		JPanel result = new JPanel( new FlowLayout( FlowLayout.CENTER ) ); 
		JButton filterButton = new JButton( "Filtrar");
		filterButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				//viewModel.filter();
			}
		});
		JButton exportButton = new JButton( "Salvar");
		result.add( filterButton );
		result.add( exportButton );
		
		return result;
	
	}
		

}

class SEPViewExport extends JPanel implements Observer {
	
	
	private JTree osTree = null;
	private DefaultTreeModel osModel = null;
	
	
	public SEPViewExport() {
		super();
		setLayout( new BorderLayout() );
		createViewPanel();
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
        osTree = new JTree();
        osTree.setEditable( false );
        JScrollPane scrollPane = new JScrollPane( osTree );
        add( scrollPane );
	}
	
	public void update( Observable observable, Object arg ) {
		DefaultTreeModel osModel = (((SEPModelExport)observable).getModel() );
	    osTree.setModel( osModel );	
	    osTree.revalidate();
		repaint();
	}
	
	
	
}

class SEPModelExport extends Observable {
	
	DefaultTreeModel model = null;
	
	DefaultMutableTreeNode root, osNode, companyNode;
	
	Map os = null;
	
	
	public SEPModelExport() {
		root = new DefaultMutableTreeNode( "Ordens disponíveis");
		loadRecords();
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
		setChanged();
		notifyObservers();
	}
	
	public DefaultTreeModel getModel() {
		return model;
	}
	
	
	public void filter() {	
	//::PENDENT	
	//::1. Executar consultar
	//:;2: Atualizar modelo
		setChanged();
		notifyObservers();
	}
	
	/**
	 * Load all services order
	 */
	private void loadRecords() {
		
		os = new HashMap();
		
		String[] companiesListOne = { new String("EMPRESA_A1"), 
		                              new String("EMPRESA_A2"),
		                              new String("EMPRESA_A3") };
		os.put( new String("OS2000"), companiesListOne );
		
		String[] companiesListTwo = {  new String("EMPRESA_B1"), 
		                               new String("EMPRESA_B2"),
		                               new String("EMPRESA_B3") };		
		os.put( new String("OS2001"), companiesListTwo );
		
		String[] companiesListThree = {  new String("EMPRESA_C1"), 
		                                 new String("EMPRESA_C2"),
		                                 new String("EMPRESA_C3"),
		                                 new String("EMPRESA_C4") };		
		os.put( new String("OS2002"), companiesListThree ); 
	}
	
}
