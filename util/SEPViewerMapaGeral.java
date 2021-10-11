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
import sep.SEPConverter;
import sep.SEPFormTabMapaGeral;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;
import java.text.*;

import javax.swing.*;
import javax.swing.event.*; 

/**
 * @author Francisco Carlos
 * @created on Oct 14, 2003
 * 
 * Listing the companies that bought services
 */


public class SEPViewerMapaGeral extends JDialog {
	
	private static final int LANCAMENTO_ESCRITURADO = 1;

	private JList servicosList;
	private String[] servicos = null ;
	private SEPLookupMapaGeral sepLookupMapaGeral = null;
	
	private String[] dataCompetencia = null ;
	private String[] nomeTomador = null;
	
	public SEPViewerMapaGeral( Connection con, boolean mode ) {
		setModal( mode );
		setTitle("Lista de servicos prestados ordenados por data de competência ");
		setSize( 500, 240 );
		sepLookupMapaGeral = new SEPLookupMapaGeral( con );
		loadServicosList();
		servicosList = new JList(servicos);
		
		TabListCellRenderer renderer = new TabListCellRenderer();
		renderer.setTabs( new int[] { 50, 200 });
		servicosList.setCellRenderer( renderer );
		servicosList.addListSelectionListener( new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e ){
				
				initializeValuesForm();
								
				int index = servicosList.getSelectedIndex();			
				
								
				SEPFormTabMapaGeral.setDataCompetenciaTxt( 
					getDataDirectOrder( dataCompetencia[ index ]  ) );
					
				SEPFormTabMapaGeral.setNomeTomadorTxt( nomeTomador[ index ]);
				SEPFormTabMapaGeral.setItemListaTxt(
				      sepLookupMapaGeral.getNumItemList( index ));
				      
				SEPFormTabMapaGeral.setDataEmissaoTxt(
				     getFmtData(
				       sepLookupMapaGeral.getDataEmissaoList( index )));      
					
					
				SEPFormTabMapaGeral.setNumeroDocumentoTxt( 
							sepLookupMapaGeral.getNumDocListList( index ));
							
				SEPFormTabMapaGeral.setNaturezaServicoTxt(
						   sepLookupMapaGeral.getDescServicoList( index ));
				           
				           
				int tipoDocumento = Integer.parseInt(
						   sepLookupMapaGeral.getTipoDocList( index ));
				SEPFormTabMapaGeral.setTipoDocCombo( tipoDocumento );          
				              
				                      
				String valueFromDB = SEPConverter.adapterCurrencyFrmt(
						  sepLookupMapaGeral.getRecBrutaList( index ));
				double dummy = Double.parseDouble( valueFromDB );
				SEPFormTabMapaGeral.setReceitaPropria( getFmtCurrency(dummy));
				
				valueFromDB = SEPConverter.adapterCurrencyFrmt(
				          sepLookupMapaGeral.getIssDevidoList( index ));
				dummy = Double.parseDouble( valueFromDB );
				SEPFormTabMapaGeral.setIssRecPropria( getFmtCurrency(dummy));
				
				valueFromDB = SEPConverter.adapterCurrencyFrmt(
				sepLookupMapaGeral.getRecSubstTribList( index ) );
				dummy = Double.parseDouble( valueFromDB );
				SEPFormTabMapaGeral.setReceitaTerceiros( getFmtCurrency( dummy ) );
				
				valueFromDB = SEPConverter.adapterCurrencyFrmt(
						  sepLookupMapaGeral.getIssSubstTribList( index ));
				dummy = Double.parseDouble( valueFromDB );
				SEPFormTabMapaGeral.setIssRecTerceiros( getFmtCurrency(dummy));
				
				
				int tipoLancamento = Integer.parseInt(
							 sepLookupMapaGeral.getTipoLancamentoList( index )) ;
				             				             
				if ( tipoLancamento == LANCAMENTO_ESCRITURADO ) {
					SEPFormTabMapaGeral.setLancamentoEscriturado();
				}
				else {
					SEPFormTabMapaGeral.setLancamentoAjustado();					   
				}
				
				String currTimestamp = 
					(String)sepLookupMapaGeral.getTimestamp( index );				
				SEPFormTabMapaGeral.setSelectedRow( currTimestamp  );
					                            
				                       
			}		
			
		});
		
		
		JScrollPane ps = new JScrollPane();
		ps.getViewport().add(servicosList);
		getContentPane().add( ps, BorderLayout.CENTER);
		
		JButton exitButton = new JButton("Finalizar pesquisa");
		exitButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				dispose();
				int index = servicosList.getSelectedIndex();
				String currTimestamp = 
					(String)sepLookupMapaGeral.getTimestamp( index );				
				SEPFormTabMapaGeral.setSelectedRow( currTimestamp  );
			}
		});
		
		exitButton.setMnemonic(KeyEvent.VK_F);
		exitButton.setToolTipText("Fechar janela de pesquisa");
		
		JPanel basePanel = new JPanel( new FlowLayout( FlowLayout.CENTER));	
		basePanel.add( exitButton );
		
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
		SEPFormTabMapaGeral.setDataCompetenciaTxt("");
		SEPFormTabMapaGeral.setNomeTomadorTxt("");
		SEPFormTabMapaGeral.setItemListaTxt("");
		SEPFormTabMapaGeral.setDataEmissaoTxt("");
		SEPFormTabMapaGeral.setNumeroDocumentoTxt("");
		SEPFormTabMapaGeral.setNaturezaServicoTxt("");
		SEPFormTabMapaGeral.setTipoDocCombo( 1 );           
				                      
				
		SEPFormTabMapaGeral.setReceitaPropria("0,00");
				
		SEPFormTabMapaGeral.setIssRecPropria("0,00");					
		SEPFormTabMapaGeral.setReceitaTerceiros( "0,00" );					
		SEPFormTabMapaGeral.setIssRecTerceiros("0,00");
				             				             
		SEPFormTabMapaGeral.setLancamentoEscriturado();
		
		
	}
	
	
	private void loadServicosList() {		
		dataCompetencia = sepLookupMapaGeral.getDataCompetenciaList();
		nomeTomador = sepLookupMapaGeral.getNomeTomadorList();

		servicos = new String[ dataCompetencia.length ] ;
		for ( int i = 0; i < dataCompetencia.length; i++ ) {
			servicos[ i ] = dataCompetencia[ i ] + "\t" + nomeTomador[ i ];			       
		}
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
	
	private String getDataDirectOrder( String data  ){
		String retVal = "";		
		String year = data.substring( 0, 4 );
		String month = data.substring( 5, 7 );
		retVal =  month + "/" + year;
		return retVal;
	}
	
	

}
