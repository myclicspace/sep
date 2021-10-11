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

public class SEPFormTabDocumentos extends JPanel {

	private Object[][] documentos;
			
	private String[] columnNames = { "CMC Tomador", "Tomador", "Tipo Documento",
									"Data Emissão",  "N° Documento", 
									"Valor Documento", "Escriturado", "Tipo da Empresa",
									"Natureza Serviço", "Valor ISSQN Fonte"
									 };
	
	private String tipos[] = { "NFS", "Recibo", "Outro" };
   
	public SEPFormTabDocumentos () {
		loadDocumentosTable();	
	}

	public JPanel createTabDocumentosPanel(){
		
		JPanel tabDocumentosPanel = new JPanel( new BorderLayout() );
		
		JPanel inputTabDocumentosPanel = new JPanel( new GridLayout( 3, 2, 5, 5 ) );
								
		JLabel cmcTomadorLabel = new JLabel("CMC Tomador");
		JTextField cmcTomadorTxt = new JTextField( 10 );
		JLabel nomeTomadorLabel = new JLabel("Tomador");
		JTextField nomeTomadorTxt = new JTextField( 20 );
		JLabel tipoDocumentoLabel = new JLabel("Tipo Documento");
		JComboBox tipoDocumentoComboBox = new JComboBox( tipos );
		JLabel dataDocumentoLabel = new JLabel("Data da Emissão");
		JTextField dataDocumentoTxt = new JTextField( 20 );
		JLabel numeroDocumentoLabel = new JLabel("Número Documento");
		JTextField numeroDocumentoTxt = new JTextField( 6 );
		JLabel valorDocumentoLabel = new JLabel("Valor do Documento");
		JTextField valorDocumentoTxt = new JTextField( 10 );
		JCheckBox escrituradoCheckBox = new JCheckBox( "Escriturado" );
		JCheckBox substitutoCheckBox = new JCheckBox( "Substituto" );		
		JLabel naturezaServicoLabel = new JLabel("Natureza do Serviço");
		JTextField naturezaServicoTxt = new JTextField( 10 );
		JLabel valorISSFonteLabel = new JLabel("Valor ISSQN Fonte");
		JTextField valorISSFonteTxt = new JTextField( 10 );
		
        Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Documentos");
        titleBorder.setTitleJustification( TitledBorder.RIGHT );

		inputTabDocumentosPanel.setBorder( titleBorder );

		inputTabDocumentosPanel.add( cmcTomadorLabel );
		inputTabDocumentosPanel.add( cmcTomadorTxt ); 
		inputTabDocumentosPanel.add( nomeTomadorLabel ); 
		inputTabDocumentosPanel.add( nomeTomadorTxt );
		inputTabDocumentosPanel.add( tipoDocumentoLabel ); 
		inputTabDocumentosPanel.add( tipoDocumentoComboBox );
		inputTabDocumentosPanel.add( dataDocumentoLabel ); 
		inputTabDocumentosPanel.add( dataDocumentoTxt );
		inputTabDocumentosPanel.add( numeroDocumentoLabel ); 
		inputTabDocumentosPanel.add( numeroDocumentoTxt );
		inputTabDocumentosPanel.add( valorDocumentoLabel ); 
		inputTabDocumentosPanel.add( valorDocumentoTxt );
		inputTabDocumentosPanel.add( escrituradoCheckBox ); 
		inputTabDocumentosPanel.add( substitutoCheckBox );
		inputTabDocumentosPanel.add( naturezaServicoLabel ); 
		inputTabDocumentosPanel.add( naturezaServicoTxt );
		inputTabDocumentosPanel.add( valorISSFonteLabel ); 
		inputTabDocumentosPanel.add( valorISSFonteTxt );
					
		JButton incluirButton = new JButton("Incluir");		
		JButton excluirButton = new JButton("Excluir");
		JButton gravarButton = new JButton("Gravar");
		JButton sairButton = new JButton("Sair");
		
		incluirButton.setMnemonic('I');
		excluirButton.setMnemonic('E');
		gravarButton.setMnemonic('G');
		sairButton.setMnemonic('S');
		JPanel controlDocumentosPanel = new JPanel( new FlowLayout() );
		
		controlDocumentosPanel.add( incluirButton );
		controlDocumentosPanel.add( excluirButton );
		controlDocumentosPanel.add( gravarButton );
		controlDocumentosPanel.add( sairButton );
				
		TabDocumentosModel model = new TabDocumentosModel();		
		JTable documentosTable = new JTable(); 
		documentosTable.setModel( model );
		JScrollPane scrollpane = new JScrollPane( documentosTable );
		
		JPanel documentosDisplayPanel = new JPanel( new BorderLayout() ) ;
		
		documentosDisplayPanel.add( scrollpane );	
		
		
		tabDocumentosPanel.add( inputTabDocumentosPanel, BorderLayout.NORTH );
		tabDocumentosPanel.add( documentosDisplayPanel, BorderLayout.CENTER );
		tabDocumentosPanel.add( controlDocumentosPanel, BorderLayout.SOUTH );		
		
		return tabDocumentosPanel;	
		 
	}
	
	private void loadDocumentosTable() {
		documentos = new Object[30][ 10 ]; 
		for ( int i = 0; i < 30; i++ ) {
			documentos[ i ][ 0 ] = new Integer( i );
			documentos[ i ][ 1 ] = new String("adddff");		
			documentos[ i ][ 2 ] = new String("Filial " + i );		
			documentos[ i ][ 3 ] = new String("adddff");		
			documentos[ i ][ 4 ] = new String("Logradouro" + i);		
			documentos[ i ][ 5 ] = new String("adddff");		
			documentos[ i ][ 6 ] = new String("adddff");		
			documentos[ i ][ 7 ] = new String("adddff");		
			documentos[ i ][ 8 ] = new String("adddff");		
			documentos[ i ][ 9 ] = new String("adddff");					
		}			
	}
	
	private class TabDocumentosModel extends AbstractTableModel {
		
		public int getRowCount() {
			return documentos.length;
		}
		
		public Object getValueAt( int r , int c ) {
			Object value = documentos[ r ][ c ];
			return value;
		}
		
		public String getColumnName( int c ) {
			return columnNames[ c ];
		}
		
		public int getColumnCount() {
			return columnNames.length; 
		}
		
		
		
	}
	
	
}
