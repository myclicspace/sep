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

/*
 * Created on Oct 19, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package sep.util;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.print.*;
import java.awt.*;
import java.awt.event.*;
import sep.EmpresaAtual;
import sep.SEPConverter;
import java.sql.*;


/**
 * @author FCARLOS
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class GeneratorReport  implements Printable {
	private JFrame frame;
	private JTable tableView;
	private static double LENGTH_TITLE = 100.0;
	private String cmcEmpresa = null;
	private String cnpjEmpresa = null;
	private String enderecoEmpresa = null;
	private String razaoSocialEmpresa = null;	
	private Connection myConnection = null;
	
  public GeneratorReport( String[] columnsNames, String[][] values, 
                          int countRec, Connection con ) {
                          	
        myConnection = con;                 	
  	
		final String[] headers = new String[ columnsNames.length];
		final String[][] data = new String[ countRec ][ columnsNames.length ] ;
  	
		frame = new JFrame("Gerador de relatórios");
		for ( int i = 0; i < columnsNames.length; i++ )
		    headers[ i ] = columnsNames[ i ];
		    
		for ( int j = 0; j < data.length; j++ ) {
		 	for ( int k = 0; k < values[ j ].length; k++ ) {
		 	    data[ j ][ k ] = values[ j ][ k ];
//			    System.out.println( values[ j ][ k ]);
		 	}    
		}
		       
		
		
	
	TableModel dataModel = new AbstractTableModel() {
		public int getColumnCount() {
			return headers.length;
		}
		public int getRowCount() { return data.length; }
		public Object getValueAt( int row, int col ) {
			return data[row][col];
		}	 
		public String getColumnName( int column ) {
			return headers[ column ];
		}
		public Class getColumnClass(int col ){
			return getValueAt( 0, col).getClass();
		}	
		public boolean isCellEditable( int row, int col ){
			return ( col == 1);
		}
		public void setValueAt(Object aValue, int row, int column ){
			data[row][column] = (String)aValue;
		}
	};
	 
	 
	tableView = new JTable(dataModel);
	JScrollPane scrollpane = new JScrollPane( tableView );
	scrollpane.setPreferredSize( new Dimension( 500, 80 ));
	frame.getContentPane().setLayout(
	     new BorderLayout());
	frame.getContentPane().add( BorderLayout.CENTER, scrollpane );
	frame.pack();
	JButton printButton = new JButton();
	printButton.setText("Imprimr");
	frame.getContentPane().add(
	     BorderLayout.SOUTH, printButton );
	RepaintManager.currentManager(frame).setDoubleBufferingEnabled( false );          
	printButton.addActionListener( new ActionListener() {
		public void actionPerformed(ActionEvent evt ) {
			PrinterJob pj = PrinterJob.getPrinterJob();
			pj.setPrintable( GeneratorReport.this);
			pj.printDialog();
			try {
				pj.print();
			}catch ( Exception PrintExceptin ) {}
		}	
	});
	
	frame.setVisible( true );
 }	
 public int print( Graphics g, PageFormat pageFormat,
                    int pageIndex ) throws PrinterException  {
    System.out.println(" imprimindo pagina " + pageIndex );                    	
    Graphics2D g2 = (Graphics2D) g;
    g2.setColor( Color.BLACK );
    int fontHeight = g2.getFontMetrics().getHeight();
    int fontDesent = g2.getFontMetrics().getDescent();
    
    pageFormat.setOrientation( pageFormat.LANDSCAPE );
    
    double pageHeight = 
        pageFormat.getImageableHeight() - fontHeight - LENGTH_TITLE ;
    double pageWidth =
        pageFormat.getImageableWidth();
    
    // increases width by half for landscape    
    pageWidth += pageWidth/2;   
    
        
    double tableWidth = 
       (double) tableView.getColumnModel().getTotalColumnWidth();
    double scale = 1;   
    
    if ( tableWidth >= pageWidth) {
    	scale = 0.6 ; // pageWidth / tableWidth;
    }
    
    double headerHeightOnPage =
         tableView.getTableHeader().getHeight() * scale;
    double tableWidthOnPage = tableWidth * scale;
    
    double oneRowHeight = (tableView.getRowHeight() +
                            tableView.getRowMargin()) * scale;
    int numRowsOnAPage = 26;
//        (int)( ( pageHeight - headerHeightOnPage) / oneRowHeight);
                                     
    double pageHeightForTable = oneRowHeight * numRowsOnAPage;
    
    int totalNumPages = (int)Math.ceil(( 
              (double) tableView.getRowCount()) / numRowsOnAPage );
    
    if ( pageIndex >= totalNumPages ) {
    	return NO_SUCH_PAGE;          
    }
    
   
	// title
	header( g2, pageFormat, pageIndex, scale, 
		  headerHeightOnPage, pageHeightForTable );


	if ( pageIndex + 1 == totalNumPages ) {
		System.out.println("imprimindo RODAPE na ultimada pagina");
		footer( g2, pageFormat, pageIndex, scale, 
			  headerHeightOnPage, pageHeightForTable );
	} 
 
 
    g2.translate( pageFormat.getImageableX(),
                  pageFormat.getImageableY());
    // bottom center
    g2.drawString("Página: " + ( pageIndex + 1 )  + "/" + totalNumPages,
					(int) pageWidth/ 2  - 100, 
//                    (int) pageWidth/ 2  - 35, 
                    (int) ( pageHeight + fontHeight - fontDesent) + 10 );                   
                   
    g2.translate( 0f, 0f );              
	g2.translate( 0f, LENGTH_TITLE/2 );
    g2.translate( 0f, headerHeightOnPage );
    g2.translate( 0f, -pageIndex * pageHeightForTable );    
    
    if ( pageIndex + 1 == totalNumPages ) {
    	int lastRowPrinted = numRowsOnAPage * pageIndex;
    	int numRowsLeft = tableView.getRowCount() - lastRowPrinted;
    	g2.setClip( 0,
    	    (int) (pageHeightForTable * pageIndex),
    	    (int) Math.ceil( tableWidthOnPage),
    	    (int) Math.ceil( oneRowHeight * numRowsLeft ));              
    }
    else {
    	g2.setClip( 0,
    	(int)(pageHeightForTable * pageIndex),
    	(int)Math.ceil( tableWidthOnPage),
    	(int)Math.ceil( pageHeightForTable)); 
    }
    
    g2.scale( scale, scale); // --?
    tableView.paint( g2 );
    g2.translate( 0f, 0f );
    g2.scale( 1/scale, 1/scale);
    g2.translate( 0f, pageIndex * pageHeightForTable) ;
    g2.translate( 0f, -headerHeightOnPage);
    g2.setClip( 0, 0,
                (int)Math.ceil( tableWidthOnPage),
                (int)Math.ceil( headerHeightOnPage));
    g2.scale( scale, scale );
    tableView.getTableHeader().paint( g2 );    
    
    return Printable.PAGE_EXISTS;                    
 	
 }
 
 private void header( Graphics2D g, PageFormat pageFormat, 
                          int pageIndex, double scale, 
                          double headerHeightOnPage,
                          double pageHeightForTable ) throws PrinterException  {
	String HEADER_1 = "ESTADO DO PIAUI";
	String HEADER_2 = "PREFEITURA MUNICIPAL DE TERESINA";
	String HEADER_3 = "SECRETARIA MUNICIPAL DE FINANÇAS";
	String HEADER_4 = "DIVISÃO DE FISCALIZAÇÃO";
	
	String TITULO_MAPA = "MAPA DEMONSTRATIVO DE APURAÇÃO DA RECEITA DO ISSQN";
	String NOME_EMPRESA = "RAZÃO SOCIAL:";
	String ENDERECO_EMPRESA = "ENDEREÇO:";
	String CMC_EMPRESA = "CMC NRO";
	String CNPJ_EMPRESA ="CNPJ NRO";
	// 
	float x = 20; 
	float y = 8;
	 
	g.setColor( Color.BLACK );  
	g.setFont( new Font("TimesRoman", Font.PLAIN, 12 ));
	FontMetrics fm = g.getFontMetrics();
	y += fm.getAscent();
	
//	g.scale( scale, scale );
	g.translate( 0f,  LENGTH_TITLE/2 );
	g.translate( 0f, headerHeightOnPage );
	 
	g.drawString( HEADER_1, x + 100, y );
	g.drawString( HEADER_2, x + 100, y + 15 );
	g.drawString( HEADER_3, x + 100, y + 25 ); 
	g.drawString( HEADER_4, x + 100, y + 35 );
		
	g.setFont( new Font("TimesRoman", Font.PLAIN, 14 ));	
	g.drawString( TITULO_MAPA, x + 100, y + 50 );
	
	getInfoCompany();	
	
	g.setFont( new Font("TimesRoman", Font.PLAIN, 10 ));
	g.drawString( NOME_EMPRESA, x + 75, y + 70 );
	g.drawString( razaoSocialEmpresa.toUpperCase(), x + 155, y + 70 );	
	g.drawString( ENDERECO_EMPRESA, x + 75, y + 80 );
	g.drawString( enderecoEmpresa.toUpperCase(), x + 140, y + 80 );	
	g.drawString( CMC_EMPRESA, x + 75, y + 90);
	g.drawString( cmcEmpresa, x + 140, y + 90);	
	g.drawString( CNPJ_EMPRESA, x + 200, y + 90 );
	g.drawString( cnpjEmpresa, x + 270, y + 90 ); 	
	 	
 }
 
 private void footer( Graphics2D g, PageFormat pageFormat, 
 					  int pageIndex, double scale, 
 					  double headerHeightOnPage,
 					  double pageHeightForTable ) throws PrinterException  {
    
    float x = 20;
    if ( pageIndex == 0 ) pageIndex = 1;    
    float y = (float) ( (pageIndex * pageHeightForTable )+ 25) ;					  	
    String FISCAL1   = "FISCAL 1: ___________________________________________";
	String FISCAL2   = "FISCAL 2: ___________________________________________";
	String TRACO_COORD = " ___________________________________________";
	String COORD     =   "                COORDENADOR                 ";
	String TRACO_CONTRIB = " ___________________________________________";
	String CONTRIB       = "             ASS. DO CONTRIB.               ";
	String DATA_VISTO    = "DATA DO VISTO: _____/_____/_______";
	
	g.setColor( Color.BLACK );  
	g.setFont( new Font("TimesRoman", Font.PLAIN, 12 ));
	FontMetrics fm = g.getFontMetrics();
	y += fm.getAscent();

	//g.scale( scale, scale );

	g.setFont( new Font("TimesRoman", Font.PLAIN, 10 ));
	g.drawString( FISCAL1, x + 75, y + 70 );
	g.drawString( FISCAL2, x + 75, y + 90 );
	g.drawString( TRACO_CONTRIB, x + 75, y + 115 );
	g.drawString( CONTRIB, x + 75, y + 125 );
	g.drawString( TRACO_COORD, x + 350, y + 70 );
	g.drawString( COORD, x + 350, y + 80 );
	g.drawString( DATA_VISTO , x + 350, y + 120);	
	
	
 }
 
 /**
  * Sets some variables for helping in specifing of current company in the header
  * @param <code>con</code> indicates a connection done previously  
  */
 private void getInfoCompany() { 	
 	try {
 		String cmc = EmpresaAtual.getCMCEmpresa();
 	
		String query = "SELECT cnpjEmpresa, " +
				   "nomeLogradouroEmpresa, numeroLogradouroEmpresa, " +
				   "complementoEmpresa , bairroEmpresa," +
				   "cidadeEmpresa, cepEmpresa, ufEmpresa, nomeEmpresa " +			               
				   " FROM tabEmpresa " +
				   " WHERE cmcEmpresa = '" + cmc + "'" ;
				   
		Statement stmt =  myConnection.createStatement();
		ResultSet rs = stmt.executeQuery( query );		
		
		while ( rs.next() ) {
			System.out.println("ok");
			String cnpj = (String) rs.getString( 1 );
			String logradouro = (String) rs.getString( 2 );
			String numeroLogradouro = (String) rs.getString( 3 );
			String complemento = (String) rs.getString( 4 );
			String bairroEmpresa = (String) rs.getString( 5 );
			String cidadeEmpresa = (String) rs.getString( 6 );
			String cepEmpresa = rs.getString( 7 );
			String ufEmpresa = rs.getString( 8 );
			String nomeEmpresa = rs.getString( 9 );
	
			enderecoEmpresa = logradouro + " ," + numeroLogradouro + " " + complemento + " " 
									   + bairroEmpresa + " CEP " + cepEmpresa + " "
									   + cidadeEmpresa + "-" + ufEmpresa;
			cnpjEmpresa = SEPConverter.insertMaskCNPJ( cnpj );
			cmcEmpresa = SEPConverter.insertMaskCMC( cmc );
			razaoSocialEmpresa = nomeEmpresa;
		}
			
		
 	}
 	catch ( SQLException sqlEx ) {
 		sqlEx.printStackTrace();
 	}
 	
 }
 

}
