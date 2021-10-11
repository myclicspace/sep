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
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
//import java.util.*;

public class SEPFormFilterDATMGui extends JFrame {

	
	/**
	 * Holds information about the choicen company
	 */
	private static String currOS = null;   // ordem de serviço
	private static String currCMC = null;  // insc. municipal
	
	private JTextField startPaymentDateTxt = null;
    private JTextField endPaymentDateTxt   = null;
	private JTextField refMonthTxt         = null;			
	private JTextField refYearTxt          = null;	
	
	private Connection myConnection        = null;
	private Statement  myStatement         = null;
	
	public SEPFormFilterDATMGui( Connection con ) {
		
		setTitle("Fitrar DATM pagos");
		setSize( 300, 200 );
		setResizable( false );
		Container contentPane = getContentPane();
		JPanel filterDatmsPanel = createFilterDatmsPanel();
		contentPane.add( filterDatmsPanel );
		
		// center screen
  		// Centraliza tela
  		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
  		int x = ( int ) ( ( d.getWidth() - this.getWidth() ) / 2 );
  		int y = ( int ) ( ( d.getHeight() - this.getHeight() ) / 2 );
  		this.setLocation( x, y );
  		
  		setVisible( true );
		
	}

	public JPanel createFilterDatmsPanel(){
		
		
        JPanel filterDatmPanel = new JPanel( new BorderLayout() );		
		JPanel inputFilterDatmPanel = new JPanel( new GridLayout( 2, 4, 5, 5 ) );
		
		JLabel startPaymentDateLabel = new JLabel("Data inicial pagamento");
		startPaymentDateTxt = new JTextField( 5 );
		JLabel endPaymentDateLabel = new JLabel("Data final pagamento");
		endPaymentDateTxt = new JTextField( 5 );

		
		JLabel refMonthLabel = new JLabel("Mês Referência");
		refMonthTxt = new JTextField( 4 );
		JLabel refYearLabel = new JLabel("Ano Referência");
		refYearTxt = new JTextField( 4 );
		
		
		Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Filtrar Datms");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		filterDatmPanel.setBorder( titleBorder );					
		
		inputFilterDatmPanel.add( startPaymentDateLabel ); 
		inputFilterDatmPanel.add( startPaymentDateTxt );
		inputFilterDatmPanel.add( endPaymentDateLabel );
		inputFilterDatmPanel.add( endPaymentDateTxt ); 
		inputFilterDatmPanel.add( refMonthLabel );
		inputFilterDatmPanel.add( refMonthTxt ); 
		inputFilterDatmPanel.add( refYearLabel );
		inputFilterDatmPanel.add( refYearTxt ); 
		
		JButton searchButton = new JButton("Filtar");		
		
		searchButton.setMnemonic(KeyEvent.VK_L);
		searchButton.setToolTipText( "Filtrar datms pagos" );
		
	
		
		searchButton.setMnemonic('F');
		searchButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
			}	
		});
		
		
		filterDatmPanel.add( inputFilterDatmPanel, BorderLayout.CENTER );
		filterDatmPanel.add( searchButton, BorderLayout.SOUTH );		
		
		return filterDatmPanel;	
		
	}
	
	
	
	
}
