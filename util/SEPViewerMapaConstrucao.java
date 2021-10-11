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
import sep.SEPFormTabMapaConstrucao;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;
import java.text.*;

import javax.swing.*;
//import javax.swing.border.*;
import javax.swing.event.*; 

/**
 * @author Francisco Carlos
 * @created on Oct 06, 2003
 * 
 * Listing the companies that bought services
 */
public class SEPViewerMapaConstrucao extends JDialog {
	
	private static final int LANCAMENTO_ESCRITURADO = 1;
	private static final int DEDUCAO_AUTOMATICA = 2;
	
	private JList servicosList;
	private String[] servicos = null ;
	private SEPLookupMapaConstrucao sepLookupMapaConstrucao = null;
	
	private String[] dataCompetencia = null ;
	private String[] nomeContratante = null;
//	private String[] itemLista = null;
//	private String[] dataEmissao = null;
//	private String[] localExecucao = null;
//	private String[] numDoc = null;
	
	public SEPViewerMapaConstrucao( Connection con, boolean mode ) {
		setModal( mode );
		setTitle("Lista de servicos prestados ordenados por data de competência ");
		setSize( 500, 240 );
		sepLookupMapaConstrucao = new SEPLookupMapaConstrucao( con );
		loadServicosList();
		servicosList = new JList(servicos);
		
		TabListCellRenderer renderer = new TabListCellRenderer();
		renderer.setTabs( new int[] { 50, 200 });
		servicosList.setCellRenderer( renderer );
		servicosList.addListSelectionListener( new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e ){
				
				initializeValuesForm();
								
				int index = servicosList.getSelectedIndex();				
				SEPFormTabMapaConstrucao.setDataCompetenciaTxt( 
				    getDataDirectOrder( dataCompetencia[ index ]   ) );
				SEPFormTabMapaConstrucao.setNomeTomadorTxt( nomeContratante[ index ]);
				SEPFormTabMapaConstrucao.setItemServicoTxt(  
				           sepLookupMapaConstrucao.getItemServicoList( index ));
				SEPFormTabMapaConstrucao.setDataDocumentoTxt(
				           getFmtData(
				             sepLookupMapaConstrucao.getDataEmissaoList( index )) );
				SEPFormTabMapaConstrucao.setLocalExecucaoServicoTxt(
				            sepLookupMapaConstrucao.getLocalExecucaoList( index ));
				SEPFormTabMapaConstrucao.setNumDocTxt( 
				            sepLookupMapaConstrucao.getNumDocList( index ));
				SEPFormTabMapaConstrucao.setNaturezaServicoTxt(
				           sepLookupMapaConstrucao.getDescServicoList( index ));
				           
				           
				int tipoDocumento = Integer.parseInt(
				           sepLookupMapaConstrucao.getTipoDocList( index ));
				SEPFormTabMapaConstrucao.setTipoDocCombo( tipoDocumento	);           
				                      
				String valueFromDB = SEPConverter.adapterCurrencyFrmt(
				          sepLookupMapaConstrucao.getRecBrutaList( index ));
				double dummy = Double.parseDouble( valueFromDB );
				SEPFormTabMapaConstrucao.setReceitaEscritTxt( getFmtCurrency(dummy));
				
				int tipoDeducao = Integer.parseInt( 
				           sepLookupMapaConstrucao.getTipoDedList( index ) );
				           
				if ( tipoDeducao == DEDUCAO_AUTOMATICA ) {
					SEPFormTabMapaConstrucao.setDeducaoAutomatica();					
					SEPFormTabMapaConstrucao.setAliquotaCombo(					
					      sepLookupMapaConstrucao.getPercentualList( index) );             
				}
				else {
					SEPFormTabMapaConstrucao.setDeducaoManual();
					valueFromDB = SEPConverter.adapterCurrencyFrmt(
					     sepLookupMapaConstrucao.getObrasForaMunicList( index ));					
					dummy = Double.parseDouble( valueFromDB );
					SEPFormTabMapaConstrucao.setObraForaMunicTxt( getFmtCurrency( dummy ) );
					valueFromDB = SEPConverter.adapterCurrencyFrmt(
					sepLookupMapaConstrucao.getMaterialAplicadoList( index ));
					dummy = Double.parseDouble( valueFromDB );
					SEPFormTabMapaConstrucao.setMaterialAplicadoTxt( getFmtCurrency( dummy ));
					valueFromDB = SEPConverter.adapterCurrencyFrmt(
					       sepLookupMapaConstrucao.getSubEmpreitadaList( index) );					
					dummy = Double.parseDouble( valueFromDB  );
					SEPFormTabMapaConstrucao.setSubEmpreitadaTxt( getFmtCurrency( dummy ) );           
				}
				
				valueFromDB = SEPConverter.adapterCurrencyFrmt(
				sepLookupMapaConstrucao.getRecTributavelList( index ) );
				dummy = Double.parseDouble( valueFromDB );
				SEPFormTabMapaConstrucao.setReceitaTributavelTxt( getFmtCurrency( dummy ) );
				
				int tipoLancamento = Integer.parseInt(
				             sepLookupMapaConstrucao.getTipoLancamento( index )) ;
				             				             
				if ( tipoLancamento == LANCAMENTO_ESCRITURADO ) {
				    SEPFormTabMapaConstrucao.setLancamentoEscriturado();
				}
				else {
					SEPFormTabMapaConstrucao.setLancamentoAjustado();					   
				}
				
				String currTimestamp = 
					(String)sepLookupMapaConstrucao.getTimestamp( index );				
				SEPFormTabMapaConstrucao.setSelectedRow( currTimestamp  );
					                            
				                       
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
				    (String)sepLookupMapaConstrucao.getTimestamp( index );				
				SEPFormTabMapaConstrucao.setSelectedRow( currTimestamp  );
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
		SEPFormTabMapaConstrucao.setDataCompetenciaTxt("");
		SEPFormTabMapaConstrucao.setNomeTomadorTxt("");
		SEPFormTabMapaConstrucao.setItemServicoTxt("");
		SEPFormTabMapaConstrucao.setDataDocumentoTxt("");
		SEPFormTabMapaConstrucao.setLocalExecucaoServicoTxt("TERESINA");
		SEPFormTabMapaConstrucao.setNumDocTxt("");
		SEPFormTabMapaConstrucao.setNaturezaServicoTxt("");
		SEPFormTabMapaConstrucao.setTipoDocCombo( 1 );           
				                      
				
		SEPFormTabMapaConstrucao.setReceitaEscritTxt("0,00");
				
		SEPFormTabMapaConstrucao.setDeducaoAutomatica();					
		SEPFormTabMapaConstrucao.setAliquotaCombo( "40" );					
		SEPFormTabMapaConstrucao.setObraForaMunicTxt("0,00");
		SEPFormTabMapaConstrucao.setMaterialAplicadoTxt("0,00");
		SEPFormTabMapaConstrucao.setSubEmpreitadaTxt("0,00");           
				
		SEPFormTabMapaConstrucao.setReceitaTributavelTxt("0,00");
				
				             				             
		SEPFormTabMapaConstrucao.setLancamentoEscriturado();
		
		
	}
	
	private void loadServicosList() {		
		dataCompetencia = sepLookupMapaConstrucao.getDataCompetenciaList();
		nomeContratante = sepLookupMapaConstrucao.getNomeContratanteList();

		servicos = new String[ dataCompetencia.length ] ;
		for ( int i = 0; i < dataCompetencia.length; i++ ) {
			servicos[ i ] = dataCompetencia[ i ] + "\t" + nomeContratante[ i ];			       
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
	
	
	
/*	private String getMaskCMC( String cmc ) {
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
	} */

}

