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
package sep.util;
import sep.SEPFormTabTomadores;
import sep.SEPConverter;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;
import java.text.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*; 

/**
 * @author Francisco Carlos
 * @created on Sept 05, 2003
 * 
 * Listing the companies that bought services
 */
public class SEPViewerTomadores extends JDialog {
	
	private JList tomadoresList;
	private String[] tomadores = null ;
	private SEPLookupTomadores sepLookupTomadores = null;
	
	private String[] cmcTomadores = null ;
	private String[] nomeTomadores = null;
	private String[] cnpjTomadores = null;
	
	public SEPViewerTomadores( Connection con, boolean mode) {
		setModal( mode );
		setTitle("Lista de servicos prestados ordenados pelo inscricao municipal ");
		setSize( 500, 240 );
		sepLookupTomadores = new SEPLookupTomadores( con );
		loadTomadoresList();
		tomadoresList = new JList(tomadores);
		
		TabListCellRenderer renderer = new TabListCellRenderer();
		renderer.setTabs( new int[] { 50, 200, 300 });
		tomadoresList.setCellRenderer( renderer );
/*		tomadoresList.addListSelectionListener( new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e ){				
				int index = tomadoresList.getSelectedIndex();				
				SEPFormTabTomadores.setCmcTomadorTxt( 
				                         getMaskCMC(cmcTomadores[ index ] ));
				SEPFormTabTomadores.setNomeTomadorTxt( nomeTomadores[ index ]);
				SEPFormTabTomadores.setCnpjTomadorTxt(
				                         getMaskCNPJ( cnpjTomadores[ index ] ));
			}		
			
		}); */
		
		tomadoresList.addListSelectionListener( new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e ){
				
				initializeValuesForm();
								
				int index = tomadoresList.getSelectedIndex();		
				
				SEPFormTabTomadores.setCmcTomadorTxt( 
				                   getMaskCMC( cmcTomadores[ index ]) );

				SEPFormTabTomadores.setNomeTomadorTxt( nomeTomadores[ index ]);
				
				SEPFormTabTomadores.setCnpjTomadorTxt( 
				                  getMaskCNPJ( cnpjTomadores[ index ] ));

				String test = getFmtData(
				sepLookupTomadores.getDataEmissaoList( index ));

				SEPFormTabTomadores.setDataEmissaoTxt(
					 getFmtData(
					   sepLookupTomadores.getDataEmissaoList( index )));      

				int tipoDocumento = Integer.parseInt(
						   sepLookupTomadores.getTipoDocList( index ));
				SEPFormTabTomadores.setTipoDocCombo( tipoDocumento );          


				SEPFormTabTomadores.setSerieTxt( 
				   sepLookupTomadores.getSerieList( index ));
				   
				SEPFormTabTomadores.setSubSerieTxt(
				   sepLookupTomadores.getSubSerieList( index ));
				   
				SEPFormTabTomadores.setNumDocTxt(
				   sepLookupTomadores.getNumDocList( index ));
				   
			
				String valueFromDB = SEPConverter.adapterCurrencyFrmt(
						  sepLookupTomadores.getValorDocList( index ));
				double dummy = Double.parseDouble( valueFromDB );
				SEPFormTabTomadores.setValorDocTxt( getFmtCurrency(dummy));
				
				
				String currTimestamp = 
					(String)sepLookupTomadores.getTimestamp( index );				
				SEPFormTabTomadores.setSelectedRow( currTimestamp  );
					                            
				                       
			}		
			
		});	
		
		
		JScrollPane ps = new JScrollPane();
		ps.getViewport().add(tomadoresList);
		getContentPane().add( ps, BorderLayout.CENTER);
		
		
		JButton exitButton = new JButton("Finalizar pesquisa");
		exitButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				dispose();
			}
		});
		exitButton.setMnemonic(KeyEvent.VK_F);
		exitButton.setToolTipText("Fechar janela de pesquisa");
		
		JPanel basePanel = new JPanel( new FlowLayout( FlowLayout.CENTER));	
		basePanel.add( exitButton );
		
		//getContentPane().add( topoPanel, BorderLayout.NORTH );		 
		getContentPane().add( basePanel, BorderLayout.SOUTH );		
		
		// Centraliza tela 
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x = ( int ) ( ( d.getWidth() - this.getWidth() ) / 2 );
		int y = ( int ) ( ( d.getHeight() - this.getHeight() ) / 2 );
		this.setLocation( x, y );
  		
 		this.setVisible( true ); 
		
		
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );		
	}

	private void initializeValuesForm() {
		SEPFormTabTomadores.setCmcTomadorTxt("");
		SEPFormTabTomadores.setNomeTomadorTxt("");
		SEPFormTabTomadores.setCnpjTomadorTxt("");
		SEPFormTabTomadores.setDataEmissaoTxt("");
		SEPFormTabTomadores.setTipoDocCombo( 1 ); 
		SEPFormTabTomadores.setSerieTxt("");
		SEPFormTabTomadores.setSubSerieTxt("");
		SEPFormTabTomadores.setNumDocTxt("");
		SEPFormTabTomadores.setValorDocTxt("0,00");
	}
	
	
	private void loadTomadoresList() {		
		cmcTomadores = sepLookupTomadores.getCmcTomadorList();
		nomeTomadores = sepLookupTomadores.getNomeTomadorList();
		cnpjTomadores = sepLookupTomadores.getCnpjTomadorList();
		
		tomadores = new String[ cmcTomadores.length ] ;
		for ( int i = 0; i < cnpjTomadores.length; i++ ) {
			tomadores[ i ] = cmcTomadores[ i ] + "\t" + nomeTomadores[ i ] + "\t" 
			       + cnpjTomadores[ i ];			       
		}
	}
	
	private String getMaskCMC( String cmc ) {
		String retVal = cmc;
		if ( cmc.length() == 7 ) {
			String first = cmc.substring( 0, 3 );
			String second = cmc.substring( 3, 6 );
			String digit = cmc.substring( 6, 7 );
			retVal = first + "." + second + "-" + digit;
		}
		return retVal;		
	}
	
	private String getMaskCNPJ( String cnpj ) {
		String retVal = cnpj;
		if ( cnpj.length() == 14 ) {
			String first = cnpj.substring( 0, 2 );
			String second = cnpj.substring( 2, 5 );
			String third = cnpj.substring( 5, 8 );
			String four = cnpj.substring( 8, 12 );
			String digit = cnpj.substring( 12, 14 );
			retVal = first + "." + second + "." +
						   third + "/" + four + "-" + digit;
		}
		return retVal;
	}
	
	private String getFmtCurrency( double value ){	
		NumberFormat nf = NumberFormat.getInstance( Locale.GERMANY );
		nf.setMaximumFractionDigits( 2 );
		nf.setMinimumFractionDigits( 2 );
		String formattedNumber = nf.format( value );		
		return formattedNumber;
	}
	
	private String getFmtData( String data  ) {
		String retVal = "";
		
		String day = data.substring( 8, 10 );
		String month = data.substring( 5, 7 );
		String year = data.substring( 0, 4);
		retVal = day + "/" + month + "/" + year;
		return retVal;	
	}
	

}

class TabListCellRenderer extends JLabel implements ListCellRenderer {
	protected static Border noFocusBorder ;
	protected FontMetrics fm = null;
	protected Insets insets = new Insets( 0, 0, 0, 0);
	protected int defaultTab = 50;
	protected int[] tabs = null;
		
	public TabListCellRenderer(){
		super();
		noFocusBorder = new EmptyBorder( 1, 1, 1, 1);
		setOpaque( true );
		setBorder( noFocusBorder );
	}
	public Component getListCellRendererComponent( JList list,
					  Object value, int index, boolean isSelected,
					  boolean cellHasFocus) {
		                  	
		setText( value.toString());
		setBackground( isSelected ? list.getSelectionBackground() :
									list.getBackground());
		setForeground( isSelected ? list.getSelectionForeground() :
									 list.getForeground()) ;
		setFont( list.getFont() );
		setBorder((cellHasFocus) ? UIManager.getBorder(
					"List.focusCellHighlightBorder") : noFocusBorder);         
		return this;
		                  	
	}
		
	public void setDefaultTab( int defaultTab ){
		this.defaultTab = defaultTab;
	} 
		
	public int getDefaultTab() { return defaultTab; }
		 
	public void setTabs( int[] tabs ) { this.tabs = tabs; }
		
	public int[] getTabs() { return tabs; }
		
	public int getTab( int index ) {
		if ( tabs == null )
		   return defaultTab * index;
			   
		int len = tabs.length;
		if ( index >= 0 && index < len )
		   return tabs[index];
			   
		return tabs[ len - 1] + defaultTab * ( index - len + 1 );   
	}
		
	public void paint(Graphics g){
		fm = g.getFontMetrics();
			
		g.setColor( getBackground());
		g.fillRect( 0, 0, getWidth(), getHeight());
		getBorder().paintBorder( this, g, 0, 0, getWidth(), getHeight() );
		g.setColor( getForeground());
		g.setFont( getFont());
		g.setColor( getForeground());
		g.setFont( getFont());
		insets = getInsets();
		int x = insets.left;
		int y = insets.top + fm.getAscent();
			
		StringTokenizer st = new StringTokenizer(getText(), "\t");
		while ( st.hasMoreTokens()) {
			String sNext = st.nextToken();
			g.drawString( sNext, x, y);
			x += fm.stringWidth( sNext );
			if ( !st.hasMoreTokens())
				break;
			int index = 0;
			while ( x >= getTab( index ))
				index++;
			x = getTab( index );        
		}
	}
		
}


