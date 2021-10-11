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
import java.sql.*;
import java.util.*;
import java.io.*;
//import java.text.*;

//import java.util.logging.Logger.*;
import java.util.logging.*;


public class SEPFormTabEmpresas extends JPanel {

	private int[] arrCodigoEmpresas;
	private Map rows;
	private Vector empresas;
		
	private JTextField cmcEmpresaTxt;		
	private JTextField nomeEmpresaTxt;	
	private	JTextField cnpjEmpresaTxt;	
	private JTextField nomeLogradouroTxt;
	private JTextField numeroLogradouroTxt;
	private JTextField complementoTxt;	
	private JTextField bairroTxt;
	private JTextField cidadeTxt;		
	private JTextField cepTxt;	
	private JTextField ufTxt;	
	
	
	
	/**
	 * bufferRecordPos represents the raw-order was stored in the buffer for 
	 * the record read of table when of starting load of that records
	 */
	private int bufferRecordPos = 0;
	
	private Connection myConnection = null;
	private Statement myStatement = null;
//	private Statement batchStatement = null;
	
	TabEmpresasModel model	= null;
    JTable empresasTable = null; 	

	private String[] columnNames = { "CMC", "Empresa",
									 "CNPJ",  "Logradouro",
									 "Numero", "Complemento", "Bairro",
									 "Cidade", "CEP", "UF" };
			
	
	public SEPFormTabEmpresas( Connection con ) {
		try {
			myConnection = con;
			myStatement = con.createStatement();
			Statement stmt = con.createStatement();
			String query = "SELECT cmcEmpresa, nomeEmpresa, cnpjEmpresa, " +
			               "nomeLogradouroEmpresa, numeroLogradouroEmpresa, " +
			               "complementoEmpresa , bairroEmpresa," +
			               "cidadeEmpresa, cepEmpresa, ufEmpresa " +			               
			               " FROM tabEmpresa ORDER BY cmcEmpresa" ;
			ResultSet rs = stmt.executeQuery( query );
			loadEmpresasTable( rs );	
			
			// speeding the performance for access tabInfracao through
			// using batch mechanism of JDBC
/*-->			boolean autoCommit = myConnection.getAutoCommit();
			System.out.println( " valor do autocommit " + autoCommit );
			myConnection.setAutoCommit( false );
			System.out.println( " desabilitou autocomit " );
			batchStatement = myConnection.createStatement(); */
			
			
		} catch ( SQLException ignore ) {
			System.out.println( ignore.getMessage() );
			ignore.printStackTrace();
		}	
	}

	public JPanel createTabEmpresasPanel(){
		
		JPanel tabEmpresasPanel = new JPanel( new BorderLayout() );
		
		JPanel inputTabEmpresasPanel = new JPanel();
		inputTabEmpresasPanel.setLayout( 
		    new  BoxLayout( inputTabEmpresasPanel, BoxLayout.Y_AXIS ) );  							
		JLabel cmcEmpresaLabel = new JLabel("Insc. Municipal");
		cmcEmpresaTxt = new JTextField( new FixedNumericDocument( 9, false ), "", 9 );
		cmcEmpresaTxt.setToolTipText("Formato válido: 999999-9 ou 9999999");
		cmcEmpresaTxt.addActionListener( new MaskCMCHandler() );	
		
				
		JLabel nomeEmpresaLabel = new JLabel("Nome da empresa");
		nomeEmpresaTxt = new JTextField( 20 );
		JLabel cnpjEmpresaLabel = new JLabel("CNPJ");
		cnpjEmpresaTxt =
		      new JTextField( new FixedNumericDocument( 18, false ), "", 18 );
		cnpjEmpresaTxt.setToolTipText(
                     "Formato válido: 12.345.678/1234-12 ou 12345678901234");
		cnpjEmpresaTxt.addActionListener( new MaskCNPJHandler() );	
                                      
		
		JLabel nomeLogradouroLabel = new JLabel("Logradouro");
		nomeLogradouroTxt = new JTextField( new FixedNumericDocument( 30, false ), "", 30 );		
		JLabel numeroLogradouroLabel = new JLabel("Numero");
		numeroLogradouroTxt = 
		   new JTextField( new FixedNumericDocument( 6, false ), "", 6 );		
		JLabel complementoLabel = new JLabel("Complemento");
		complementoTxt = 
		     new JTextField( new FixedNumericDocument( 35, false ), "", 35 );		
		JLabel bairroLabel = new JLabel("Bairro");
		bairroTxt = 
		     new JTextField( new FixedNumericDocument( 15, false ), "", 15 );		
		JLabel cidadeLabel = new JLabel("Cidade");
		cidadeTxt = new JTextField( new FixedNumericDocument( 15, false ), "", 15 );		
		JLabel cepLabel = new JLabel("CEP");
		cepTxt = new JTextField( new FixedNumericDocument( 8, true ), "", 8 );
		JLabel ufLabel = new JLabel("UF");
		ufTxt = new JTextField( new FixedNumericDocument( 2, false ), "PI", 2 );		
				
		JPanel inscNomePanel  = new JPanel( new FlowLayout( FlowLayout.LEFT )); //dLayout( 1, 4, 5, 5 ) );
		JPanel cnpjLogradouroPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) ); // GridLayout( 1, 4, 5, 5 ) );
		JPanel numComplementoPanel = new JPanel( new FlowLayout( FlowLayout.LEFT ) );
		
		inscNomePanel.add( cmcEmpresaLabel );
		inscNomePanel.add( cmcEmpresaTxt ); 
		inscNomePanel.add( nomeEmpresaLabel );
		inscNomePanel.add( nomeEmpresaTxt );		
		inscNomePanel.add( cnpjEmpresaLabel );
		inscNomePanel.add( cnpjEmpresaTxt );
		
		
		cnpjLogradouroPanel.add( nomeLogradouroLabel );
		cnpjLogradouroPanel.add( nomeLogradouroTxt );
		
		cnpjLogradouroPanel.add( numeroLogradouroLabel );
		cnpjLogradouroPanel.add( numeroLogradouroTxt );
		cnpjLogradouroPanel.add( complementoLabel );
		cnpjLogradouroPanel.add( complementoTxt );
		numComplementoPanel.add( bairroLabel );
		numComplementoPanel.add( bairroTxt );
		numComplementoPanel.add( cidadeLabel );
		numComplementoPanel.add( cidadeTxt );
		numComplementoPanel.add( cepLabel );
		numComplementoPanel.add( cepTxt );
		numComplementoPanel.add( ufLabel );
		numComplementoPanel.add( ufTxt );		
		
		
        Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder(
		       etched, "Entrada de dados");
        titleBorder.setTitleJustification(TitledBorder.RIGHT);

		inputTabEmpresasPanel.setBorder( titleBorder );

		inputTabEmpresasPanel.add( inscNomePanel );
		inputTabEmpresasPanel.add( cnpjLogradouroPanel ); 
		inputTabEmpresasPanel.add( numComplementoPanel );		
				
		JButton incluirButton = new JButton("Incluir");		
		JButton excluirButton = new JButton("Excluir");
		JButton gravarButton = new JButton("Gravar");
		//JButton sairButton = new JButton("Sair");
		
		incluirButton.setMnemonic('I');
		incluirButton.setToolTipText("Cadastra empresa");
		
		incluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
				    addRecord( myConnection );					
				} catch( SQLException ignore ) {
					ignore.printStackTrace();					
				}    
			}
		});
		
		excluirButton.setMnemonic('E');
		excluirButton.setToolTipText("Excluir empresa registrada");
		
		excluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
				    deleteRecord( myConnection );					
				} catch( SQLException ignore ) {
					ignore.printStackTrace();					
				}    
			}
		});
		
		
		gravarButton.setMnemonic('G');
		gravarButton.setToolTipText("Atualiza dados da empresa");
		gravarButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
					updateRecord( myConnection );
				} catch ( SQLException ignore ) {
					ignore.printStackTrace();
				}    
			}
		}); 
		
		
//		sairButton.setMnemonic('S');
		JPanel controlEmpresasPanel = new JPanel( new FlowLayout() );
		
		controlEmpresasPanel.add( incluirButton );
		controlEmpresasPanel.add( excluirButton );
		controlEmpresasPanel.add( gravarButton );
//		controlEmpresasPanel.add( sairButton );
		tabEmpresasPanel.add( inputTabEmpresasPanel );
				
		model = new TabEmpresasModel();		
		empresasTable = new JTable(); 		
		JTableHeader headers = empresasTable.getTableHeader();
		headers.setReorderingAllowed( false );
		
		empresasTable.setModel( model );
		empresasTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		empresasTable.addMouseListener( new MouseAdapter()  {
			public void mouseClicked( MouseEvent e ) {
				int count = empresasTable.getSelectedRowCount();
				if ( count > 0 ) {
                    inputTabEmpresasPanelRefresh();					
				}
			}
		});
		
		JScrollPane scrollpane = new JScrollPane( empresasTable );
		
		JPanel empresasDisplayPanel = new JPanel( new BorderLayout() ) ;
		
		empresasDisplayPanel.add( scrollpane );	
		
		
		tabEmpresasPanel.add( inputTabEmpresasPanel, BorderLayout.NORTH );
		tabEmpresasPanel.add( empresasDisplayPanel, BorderLayout.CENTER );
		tabEmpresasPanel.add( controlEmpresasPanel, BorderLayout.SOUTH );		
		
		return tabEmpresasPanel;	
		
		
	}
	
	private void loadEmpresasTable( ResultSet rs ) {	
		try {
			rows = new HashMap();
			empresas = new Vector();
			String key = null;
			int cols = columnNames.length;
			bufferRecordPos = 0;
			/** 
			 *  Inserts all the data as a vector of Object[] rows
			/*  It was not used Object[][] because is not knew how many rows
			/*  the ResultSet has
			 **/
			 while ( rs.next() ) {
			 	String[] theRow = new String[ cols ];
			 	for ( int j = 0; j < theRow.length; j++ )  {
			 	    theRow[ j ] = (String)rs.getString( j + 1 );
			 	}
		 	    key = (String) rs.getString( 1 );
			 	rows.put( new Integer(key) , new Integer(  bufferRecordPos ) );	 	
			 	empresas.add( theRow );
			 	bufferRecordPos++;	 	
			 	
			 }
			 
		}
		catch ( SQLException sqlException ) {
			System.out.println( sqlException.getMessage() + " aqui");
			sqlException.printStackTrace();
		}
	}
	
	
	//:PENDENCIA: --> Erro de ponteiro NULL no campo CEP
	//
	/**
	 * Adds records to database
	 *
	 * precondition: Must be garanted that there is not 
	 *               duplicated keys in the database	 
	 * @param  <code>con</code> represents a Connection object
	 **         necessary for creating the statement  
	 * @throws SQLException if ocurrs some erro with Connection
	 */	
	private void addRecord(Connection con ) throws SQLException {	

		// Create a console handler
    	//ConsoleHandler handler = new ConsoleHandler();
    	
		// Create a new handler that uses the simple formatter
		FileHandler fh = null ;
        // Log entry
		Logger logger = Logger.getLogger("sep.SEPFormTabEmpresas");

    	// Set the level to a particular level
    	logger.setLevel(Level.FINEST);
    		
                    
        logger.entering(this.getClass().getName(), "addRecord");
		
            try {
                fh = new FileHandler("mylog.txt");
                fh.setFormatter(new SimpleFormatter());
          
                logger.addHandler(fh);
            } catch (IOException e) {
            }
        	
	
//	    logger.addHandler( fh ) ; // handler);
        
        /*if (logger.isLoggable(Level.FINER)) {
                logger.entering(this.getClass().getName(), "addRecord",
                                new Object[]{ con });
        }       */
        
        
        
        // body	
		int codigoInfracao = Integer.parseInt( 
		                        SEPConverter.removeMaskCMC( 
		                            cmcEmpresaTxt.getText() ) );
		boolean bufferHas = false;
		boolean isOkValue = true;
		try {
			bufferHas = rows.containsKey( new Integer( codigoInfracao ) );
			
		}
		catch (NumberFormatException formatException ) {
			String err = "Campo dever ser numérico.";
			JOptionPane.showMessageDialog( null, err,
			      "Valor inválido", JOptionPane.ERROR_MESSAGE   );
		    ;	      
		    isOkValue = false;
		}	
		
		if ( isOkValue ) {
			if ( bufferHas ) {
				String err = "Tentativa de inserir registro duplicado";
				JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
			}
			else {
				// -- Includes data in table model, and so does refreshes gui 
			
				String empresa[] = new String[ 10 ];		
//				String cmc =  cmcEmpresaTxt.getText();
				String cmc = SEPConverter.removeMaskCMC(cmcEmpresaTxt.getText());
				String nome = nomeEmpresaTxt.getText();
				String cnpj = SEPConverter.removeMaskCNPJ(cnpjEmpresaTxt.getText());
//				String cnpj = cnpjEmpresaTxt.getText();
				String nomeLogradouro = nomeLogradouroTxt.getText();
				String numeroLogradouro = numeroLogradouroTxt.getText();
				String complemento = complementoTxt.getText();
				String bairro = bairroTxt.getText();
				String cidade = cidadeTxt.getText();
				String cep =  cepTxt.getText();
				String uf =   ufTxt.getText(); 
							
				empresa[ 0 ] = cmc;
				empresa[ 1 ] = nome;
				empresa[ 2 ] = cnpj;
				empresa[ 3 ] = nomeLogradouro;
				empresa[ 4 ] = numeroLogradouro;
				empresa[ 5 ] = complemento;
				empresa[ 6 ] = bairro;
				empresa[ 7 ] = cidade;
				empresa[ 8 ] = cep;
				empresa[ 9 ] = uf;
			
			
				System.out.println( " iniciando atualização.. " );
			
				// -- The calling for including records is done using batch process			
				String cmd = "INSERT INTO tabEmpresa VALUES  ('" 
			              + cmc + "','" +  nome + "','" 
			              + cnpj + "','" + nomeLogradouro + "','"
			              + numeroLogradouro + "','" + complemento + "','"
			              + bairro + "','" +  cidade + "','" 
			              + cep + "','" + uf +  "')"; 
				myStatement.executeUpdate( cmd );
				con.commit();
				System.out.println( "... atualização ok");			
			
				empresas.add( empresa );
				rows.put( new Integer ( cmc ) , new Integer( bufferRecordPos ) );
				bufferRecordPos++;
			
				cmcEmpresaTxt.setText("");
				nomeEmpresaTxt.setText("");
				cnpjEmpresaTxt.setText("");
				nomeLogradouroTxt.setText("");
				numeroLogradouroTxt.setText("");
				complementoTxt.setText("");
				bairroTxt.setText("");
				cidadeTxt.setText("");
				cepTxt.setText("");
				ufTxt.setText("");
			
			
				bufferRefresh();                // updating buffer
				empresasTable.setModel( model );
				empresasTable.revalidate();
				repaint();
				
				createFolder( cmc, nome ); // Create folder for holding company audit-papers
			
//-->			batchStatement.addBatch( cmd );

			} // end of if bufferHas
		}// end of if isOkValue
		
        // Log exit
//        logger.info(this.getClass().getName(),  "isOkValue = " + isOkValue );
        logger.exiting(this.getClass().getName(), "addRecord");
                          
        
        /*boolean result = true;
        if (logger.isLoggable(Level.FINER)) {
            logger.exiting(this.getClass().getName(), "myMethod");
         }             */
	}

	/**
	 * Delete records from database
	 *
	 * precondition: Must be garanted that there is a record 
	 *               with the key to be deleted	 
	 * @param <code>con</code> represents a Connection object
	 **         necessary for creating the statement  
	 * @throws SQLException if ocurrs some erro with Connection
	 */	
	private void deleteRecord( Connection con ) throws SQLException {
		int selectedRow = empresasTable.getSelectedRow();
		// System.out.println(" linha selecionada " + selectedRow );
		// -- primary key of choicen record 
		String value = (String) empresasTable.getValueAt(selectedRow, 0 );
		//System.out.println(" coluna 0 da linha " + selectedRow + " é = " +
		//                     value );
		                     
		Integer rowID = (Integer) rows.get(  new Integer( value ) );
		// -- position in the buffer of choicen record
		int index = rowID.intValue();
		// System.out.println( " pos = " + rowID );				

		String[] record = new String[ 4 ];	
		record = (String[]) empresas.get( index  );		
		// System.out.println(" registro " + record[ 0 ] + " removido do vetor " +
        //             " empresas " );		
                   
		empresas.remove( index );
		bufferRefresh();                // updating buffer
		empresasTable.setModel( model );
		empresasTable.revalidate();

		// -- The calling for deleting records is done
		String cmd = "DELETE FROM  tabEmpresa" + 
		             " WHERE cmcEmpresa = '" + value + "'" ; 
 		myStatement.executeUpdate( cmd );

		con.commit();			
		
	}
	
	/*	
	 * Updates the selected record currently of empresasTable table 
	 *
	 * precondition: Must check that there is a subjacent key in database
	 *               if it will update the record
	 * @param <code>con</code> represents a Connection object
	 *         necessary for creating the statement  
	 * @throws SQLException if ocurrs some error with Connection
	 */	
	private void updateRecord( Connection con ) throws SQLException {		
		
		int cmcEmpresa = 
		   Integer.parseInt( SEPConverter.removeMaskCMC(cmcEmpresaTxt.getText()) ); 
		//Integer.parseInt( cmcEmpresaTxt.getText() );
		boolean bufferHas = rows.containsKey( new Integer( cmcEmpresa ) );
		if ( !bufferHas ) {
			String err = "Tentativa de atualizar registro inexistente";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			String empresa[] = new String[ 10 ];
			
			String cmc = SEPConverter.removeMaskCMC(cmcEmpresaTxt.getText());
			String cnpj = SEPConverter.removeMaskCNPJ(cnpjEmpresaTxt.getText());
			
			System.out.println("atualiazondo = " + cmc );
			System.out.println("cnpj = " + cnpj);
			
					
//			String cmc =  cmcEmpresaTxt.getText();
			String nome = nomeEmpresaTxt.getText();
//			String cnpj = cnpjEmpresaTxt.getText();
			String nomeLogradouro = nomeLogradouroTxt.getText();
			String numeroLogradouro = numeroLogradouroTxt.getText();
			String complemento = complementoTxt.getText();
			String bairro = bairroTxt.getText();
			String cidade = cidadeTxt.getText();
			String cep = cepTxt.getText();
			String uf = ufTxt.getText();
							
			empresa[ 0 ] = cmc;
			empresa[ 1 ] = nome;
			empresa[ 2 ] = cnpj;
			empresa[ 3 ] = nomeLogradouro;
			empresa[ 4 ] = numeroLogradouro;
			empresa[ 5 ] = complemento;
			empresa[ 6 ] = bairro;
			empresa[ 7 ] = cidade;
			empresa[ 8 ] = cep;
			empresa[ 9 ] = uf;

			
			Integer rowID = (Integer) rows.get(  new Integer( cmcEmpresa ) );
			// -- position in the buffer of choicen record
			int index = rowID.intValue();
			// -- remove first and insert again. Doing this, is not 
			// -- necessary to worry with refreshing cacher buffer
			empresas.remove( index );
			empresas.add( index, empresa );		


			empresasTable.setModel( model );
			empresasTable.revalidate();
			
//---->			
			//::PENDENCIA
			
			// FINALIZAR ESTE METODO
			// -- The calling for updating records is done
			String cmd = "UPDATE tabEmpresa "
						  +  "SET cmcEmpresa ='" + cmc + "'," 
						  +  "nomeEmpresa ='" + nome + "',"
						  +  "cnpjEmpresa ='" + cnpj + "',"
						  +  "nomeLogradouroEmpresa ='" + nomeLogradouro + "',"
						  +  "numeroLogradouroEmpresa ='" + numeroLogradouro + "',"
						  +  "complementoEmpresa='" + complemento + "',"
						  +  "bairroEmpresa='" + bairro + "'," 
						  +  "cidadeEmpresa='" + cidade + "'," 
						  +  "cepEmpresa='" + cep + "',"  
						  +  "ufEmpresa='" + uf +"' "					   						  
			              + " WHERE cmcEmpresa = '" + cmc + "'" ; 
	 		myStatement.executeUpdate( cmd );		
	 		
			con.commit();			
			
			/*String query = "SELECT codigoInfracao, descricaoInfracao, " +
			               "enquadramentoInfracao, penalidadeInfracao " +
			               "FROM tabInfracao ORDER BY codigoInfracao";
			ResultSet rs = myStatement.executeQuery( query );
			loadInfracoesTable( rs );	
			finfracoesTable.revalidate(); */		
			
			
			System.out.print(" gravou " );
			
			
		}		
		
	}
	
	/**
	 *  It will update the inputTabInfracoesPanel if the user selects
	 *  one row in displayTablInfracoesPanel
	 *
	 */
    private void inputTabEmpresasPanelRefresh() {   	
    	String value;
    	
    	// -- getting the currently selected row on the display table
		int selectedRow = empresasTable.getSelectedRow();
		
		value = (String) empresasTable.getValueAt(selectedRow, 0 );
//		cmcEmpresaTxt.setText( value );
		cmcEmpresaTxt.setText( SEPConverter.insertMaskCMC(value) );
		value = (String) empresasTable.getValueAt(selectedRow, 1 );
		nomeEmpresaTxt.setText( value );
		value = (String) empresasTable.getValueAt(selectedRow, 2 );
//		cnpjEmpresaTxt.setText( value );
		cnpjEmpresaTxt.setText( SEPConverter.insertMaskCNPJ(value) );
		value = (String) empresasTable.getValueAt(selectedRow, 3 );
		nomeLogradouroTxt.setText( value );
		value = (String) empresasTable.getValueAt(selectedRow, 4 );
		numeroLogradouroTxt.setText( value );
		value = (String) empresasTable.getValueAt(selectedRow, 5 );
		complementoTxt.setText( value );
		value = (String) empresasTable.getValueAt(selectedRow, 6 );
		bairroTxt.setText( value );
		value = (String) empresasTable.getValueAt(selectedRow, 7 );
		cidadeTxt.setText( value );
		value = (String) empresasTable.getValueAt(selectedRow, 8 );
		cepTxt.setText( value );
		value = (String) empresasTable.getValueAt(selectedRow, 9 );
		ufTxt.setText( value );		
		
		revalidate();		
    }						
	
	/**
	 * It will update the rows hash if it deletes a record
	 *
	 */
	private void bufferRefresh() {
		String[] empresa;
		String codigo;
		rows.clear();
		for ( int i = 0; i < empresas.size(); i++ ) {
			empresa = (String[])  empresas.get( i );
			codigo = empresa[ 0 ];
			rows.put( new Integer ( codigo ) , new Integer( i ) );		
		}
	}
	
	
	
	private class TabEmpresasModel extends AbstractTableModel {
		
		public int getRowCount() {
			return rows.size();
		}
		
		public Object getValueAt( int r , int c ) {			
		String[] theRow = null ;
		    if ( r < empresas.size() ) {
		    	Object obj = empresas.get( r );
		    	theRow = (String[]) obj;
		    	return ( theRow[ c ] );
		    }
		    else {
		    	return null;
		    }
		    
		}
		
		public String getColumnName( int c ) {
			
			return columnNames[ c ];
		}
		
		public int getColumnCount() {
			return columnNames.length; 
		} 
	}
	
	/**
	 * Put a mask in corresponding textfield , after a key <ENTER> to be pressed
	 */
	private class MaskCMCHandler implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			String cmc = cmcEmpresaTxt.getText();
			if ( cmc.length() == 7 ) {
				String first = cmc.substring( 0, 3 );
				String second = cmc.substring( 3, 6 );
				String digit = cmc.substring( 6, 7 );
				cmcEmpresaTxt.setText("");
				String dummy = first + "." + second + "-" + digit;
				cmcEmpresaTxt.setText( dummy );
			}
		}
	}
	
	/**
	 * Put a mask in corresponding textfield , after a key <ENTER> to be pressed
	 */
	private class MaskCNPJHandler implements ActionListener {
		public void actionPerformed( ActionEvent e ) {
			String cnpj= cnpjEmpresaTxt.getText();						
			if ( cnpj.length() == 14 ) {
				String first = cnpj.substring( 0, 2 );
				String second = cnpj.substring( 2, 5 );
				String third = cnpj.substring( 5, 8 );
				String four = cnpj.substring( 8, 12 );
				String digit = cnpj.substring( 12, 14 );
				cnpjEmpresaTxt.setText("");
				String dummy = first + "." + second + "." +
							   third + "/" + four + "-" + digit;
				cnpjEmpresaTxt.setText( dummy );
			}
		}
	}
	
	private void createFolder( String cmc, String razaoSocial ) {
		String FOLDER_HTML = "c:\\sep\\html\\";
		String folder = new String();
		folder = FOLDER_HTML + cmc + "-" + razaoSocial;
		File f = new File( folder );
		f.mkdir();
	}
	
	
	
}
