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
 * Created on Oct 29, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package sep;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;

/**
 * @author FCARLOS 
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SEPAboutDlg extends JDialog {
	private JLabel aboutSEPLabel = null;
	public SEPAboutDlg( JFrame owner ) {
		
		super( owner, 
       "Sobre o SEP - Sistema de Emissão de Papéis de trabalho :)", true );
		createWidgets();             
		
	}
	
	private void createWidgets() {
		
		JPanel aboutPanel = null;
		Container contentPane;
		contentPane = getContentPane();
		aboutPanel = setupPanel();
		contentPane.setLayout( new BorderLayout() );
		contentPane.add( aboutPanel );
		
	}
	
	private JPanel setupPanel() {
		ImageIcon brasaoIcon = new ImageIcon("c:\\sep\\img\\brasao.gif");
		
		JPanel mainPanel = new JPanel( new BorderLayout());
		aboutSEPLabel =	new JLabel ();
		aboutSEPLabel.setText("Copyright (c) SECRETARIA DE FINANÇAS-DIVISÃO DE FISCALIZAÇÃO" );	
		aboutSEPLabel.setIcon( brasaoIcon );
		aboutSEPLabel.setHorizontalAlignment( SwingConstants.CENTER );
		JPanel titleSEPPanel = new JPanel( new FlowLayout() );
		JLabel title = new JLabel("SISTEMA DE EMISSÃO DE PAPÉIS DE TRABALHO   versão 1.00");
		titleSEPPanel.add( title );
		JPanel infoSEPPanel = new JPanel( new BorderLayout());
		infoSEPPanel.add( aboutSEPLabel );
		JPanel infoAuthorsPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT ));
		JButton authorsButton = new JButton("Autores");
		authorsButton.setMnemonic('A');
		authorsButton.addActionListener( new ActionListener() {
			String output = " Módulos desenvolvidos:\n\n" +
                            "   Interface gráfica by Francisco Emidio \n " +
	                        "   Relatórios oficiais by Fábio de Jesus";		
			public void actionPerformed( ActionEvent evt ) {
				JOptionPane.showMessageDialog( null, output,
				"Informação", JOptionPane.INFORMATION_MESSAGE );
			} 
		}); 		
		JButton okButton = new JButton("OK");
		okButton.setMnemonic('O');
		okButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				dispose();
			}
		});
		
		infoAuthorsPanel.add( authorsButton );
		infoAuthorsPanel.add( okButton );
		
		JPanel titlePanel = new JPanel(new BorderLayout());
		titlePanel.add( titleSEPPanel, BorderLayout.NORTH );
		titlePanel.add( infoSEPPanel );		
		
		Border etched = BorderFactory.createEtchedBorder();
		TitledBorder titleBorder;
		titleBorder = BorderFactory.createTitledBorder( etched, "Informações de contato");
		titleBorder.setTitleJustification( TitledBorder.LEFT );		
		JPanel contactAuthors = new JPanel();
		contactAuthors.setLayout( 
			new  BoxLayout( contactAuthors, BoxLayout.Y_AXIS ) );
		JLabel emidioLabel = new JLabel("Francisco Emidio Freitas Carlos  goias789@ig.com.br");
		JLabel fabioLabel =  new JLabel("Fábio de Jesus Lima Gomes        fabiojlgomes@bol.com.br ");
		contactAuthors.add( emidioLabel );
		contactAuthors.add( fabioLabel );	  							
		contactAuthors.setBorder( titleBorder );
		
		mainPanel.add( titlePanel, BorderLayout.NORTH );
		mainPanel.add( contactAuthors, BorderLayout.CENTER );
		mainPanel.add( infoAuthorsPanel, BorderLayout.SOUTH );
		
		return mainPanel;
	}
}
