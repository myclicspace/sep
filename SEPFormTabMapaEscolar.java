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

import sep.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.text.*;

public class SEPFormTabMapaEscolar extends JPanel {
	
	/**
	 * Number of fields for the  mapaEscolar form
	 */
	private static final int FIELDS_QTY = 9;
	
	/**
	 * Especifica os tipos de lançamentos possíveis obtidos pela fiscalização
	 */
	private static final int LANCAMENTO_ESCRIT = 1;
	private static final int LANCAMENTO_AJUST  = 2;
	 
	 
	 /**
	  * Mapeia posição do registro selecionada da tabela para um buffer
	  */
	private static int bufferRecordPos = 0;
	
	private static String currOS = null; // currently service order openned
	private static String currCMC = null; // currently company choicen
	
	/**
	 * Indicates a buffer manages records into a corresponding service order
	 * Also, is responsible for basic operation of insertion, deleting and
	 * updating of the records in the database
	 */
	private Map mapaEscolarRows = null;
	
	/**
	 * Indicates the source of the table model
	 */
	private Vector mapaEscolar = null;
	
	

	//private Object[][] documentos;
			
	private String[] columnNames = { "Curso", "Exercício", "Mes",
									"Qtd. alunos","Mensalidade",
									 "(+)Receita", "(-)Deduções",
									 "(=)Receita Tributável", 
									 "Tipo lançamento"
									 };
	
	private String meses[] = { "JAN", "FEV", "MAR", "ABR", "MAI", "JUN", "JUL",
	                           "AGO", "SET", "OUT", "NOV", "DEZ"  };
	                           
	                           
	// identificação do curso
	private JTextField nomeCursoTxt = null;
	private	JTextField exercicioCursoTxt = null;
	private	JComboBox mesCursoCombo = null;
	private	JTextField numeroAlunosTxt = null;
	private	JTextField mensalidadeCursoTxt = null;
		
		
	// movimento econômico
	private	JTextField receitaTotalTxt = null;
	private	JTextField deducoesTxt = null;
	private	JTextField receitaTributavelTxt = null;
	

	// tipo de lançamento
	private	JRadioButton escrituradoRadioButton = null;
	private	JRadioButton ajustadoRadioButton = null;
	
    private ComputeIncomingsAction	computeIncomingsAction = null;
	
	private Connection myConnection = null;
	private Statement myStatement = null;
	
	
	private SEPLookupMapaEscolar sepLookupMapaEscolar = null;
	
	TabMapaEscolarModel documentosTableModel = null;		
	JTable documentosTable = null; 
	
   
	public SEPFormTabMapaEscolar( Connection con ) {		
		try {
			myConnection = con;
			myStatement = con.createStatement();
			
			currOS = EmpresaAtual.getNumeroOS();
			currCMC = EmpresaAtual.getCMCEmpresa();
			
			String query = " SELECT cursoMapaEnsino, exercicioMapaEnsino, " +
			               " mesMapaEnsino, qtdAlunosMapaEnsino, " +
			               " mensalidadeMapaEnsino, receitaTotalMapaEnsino, " +
			               " deducoesMapaEnsino, receitaLiquidaMapaEnsino, " +
			               " tipoLancamentoMapaEnsino " +
			               " FROM tabMapaEnsino " +
			               " WHERE osMapaEnsino ='" + currOS + "'" +
			               "  AND cmcMapaEnsino ='" + currCMC + "'" +
			               " ORDER BY cursoMapaEnsino, exercicioMapaEnsino, " +
			               " mesMapaEnsino ";
			              
			ResultSet rs =  myStatement.executeQuery( query );
			// Getting the records of the database
			loadMapaEscolarTable( rs );	
			
			sepLookupMapaEscolar = new SEPLookupMapaEscolar( con );
			
			
		}
		catch ( SQLException ignore ) {
		}
	
	}

	public JPanel createTabMapaEscolarPanel(){
		
		JPanel tabDocumentosPanel = new JPanel( new BorderLayout() );
		
		JPanel inputTabMapaEscolarPanel = new JPanel();
		
		inputTabMapaEscolarPanel.setLayout( 
		    new  BoxLayout( inputTabMapaEscolarPanel, BoxLayout.Y_AXIS ) );      


		// listener to computing incomings
		computeIncomingsAction = new ComputeIncomingsAction();
		
		 
		// identificação do curso
		JLabel nomeCursoLabel = new JLabel("Curso");
		nomeCursoTxt = new JTextField( 
							new FixedNumericDocument( 40, false ), "", 40 ); 

		nomeCursoTxt.addKeyListener( new CursosKeySearcher() );

		
		JLabel exercicioCursoLabel = new JLabel("Exercício");
		exercicioCursoTxt = new JTextField( 
							new FixedNumericDocument( 4, true ), "", 4 );
		exercicioCursoTxt.setToolTipText("Formato válido: 99.999,99 ou 99999,99");
		exercicioCursoTxt.addKeyListener( new ExerciciosKeySearcher() );

		
		JLabel mesCursoLabel = new JLabel("Mês");
		mesCursoCombo = new JComboBox( meses );
		
		JLabel numeroAlunosLabel = new JLabel("Qtd. alunos" );
		numeroAlunosTxt = new JTextField( 
		                    new FixedNumericDocument( 5, true ), "0", 5 ); 
		numeroAlunosTxt.setText("0");
		numeroAlunosTxt.addActionListener( computeIncomingsAction );
		
		
		JLabel mensalidadeCursoLabel = new JLabel("Mensalidade");
		mensalidadeCursoTxt = new JTextField(
			new FixedNumericDocument( 9, false ), "0,00", 9 );
		mensalidadeCursoTxt.setToolTipText("Formato válido: 99.999,99 ou 99999,99");
		mensalidadeCursoTxt.addActionListener( computeIncomingsAction );
		
		
		
		// movimento econômico
		JLabel receitaTotalLabel = new JLabel("(+)Receita");
		receitaTotalTxt = new JTextField( 10 );
		receitaTotalTxt.setText("0,00");
		receitaTotalTxt.setEditable( false );
		JLabel deducoesLabel = new JLabel("(-)Deduções" );
		
		deducoesTxt = new JTextField(
			new FixedNumericDocument( 14, false ), "0,00", 14 );
		deducoesTxt.setToolTipText("Formato válido: 99.999,99 ou 99999,99");
		deducoesTxt.addActionListener( computeIncomingsAction );

		
		JLabel receitaTributavelLabel = new JLabel("(=)Receita tributável" );
		receitaTributavelTxt = new JTextField( 10 );
		receitaTributavelTxt.setText("0,00");
		receitaTributavelTxt.setEditable( false ) ;
		
		
		// tipo lançamento
		JLabel tipoLancamentoLabel = new JLabel("Lançamento ");
		escrituradoRadioButton =  new JRadioButton( "Receita Escriturada" );
		ajustadoRadioButton =   new JRadioButton( "Receita Ajustada" );		
		escrituradoRadioButton.setSelected( true );
		ButtonGroup tipoReceitaButtonGroup = new ButtonGroup();
		tipoReceitaButtonGroup.add( escrituradoRadioButton );
		tipoReceitaButtonGroup.add( ajustadoRadioButton );
		
		
	
		JPanel descricaoCursoPanel  =
		    new JPanel( new FlowLayout( FlowLayout.LEFT )); 
		JPanel exercicioMesCursoPanel =
		    new JPanel ( new GridLayout( 1, 8, 5, 5 ) ) ;
//		JPanel alunosMensalidadeCursoPanel = 
//		    new JPanel ( new GridLayout( 1, 4, 5, 5 ) ) ;
		JPanel movimentoEconomicoPanel = 
		    new JPanel( new GridLayout( 2, 6, 5, 5 ) );		    
		JPanel tipoReceitaPanel =
		    new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
		    
		
		
		descricaoCursoPanel.add( nomeCursoLabel );
		descricaoCursoPanel.add( nomeCursoTxt );
		
		exercicioMesCursoPanel.add( exercicioCursoLabel );
		exercicioMesCursoPanel.add( exercicioCursoTxt );
		exercicioMesCursoPanel.add( mesCursoLabel );
		exercicioMesCursoPanel.add( mesCursoCombo );
		exercicioMesCursoPanel.add( numeroAlunosLabel );
		exercicioMesCursoPanel.add( numeroAlunosTxt );
		exercicioMesCursoPanel.add( mensalidadeCursoLabel );
		exercicioMesCursoPanel.add( mensalidadeCursoTxt );
		
		
        Border etchedMovEconomicoPanel =
           BorderFactory.createBevelBorder( BevelBorder.LOWERED ) ;
        TitledBorder titleBorderMovEconomico;
        titleBorderMovEconomico = BorderFactory.createTitledBorder( 
                      etchedMovEconomicoPanel, "Movimentação Econômica");
        titleBorderMovEconomico.setTitleJustification( TitledBorder.LEFT );
		movimentoEconomicoPanel.setBorder( titleBorderMovEconomico );
		
		movimentoEconomicoPanel.add( receitaTotalLabel );
		movimentoEconomicoPanel.add( deducoesLabel );
		movimentoEconomicoPanel.add( receitaTributavelLabel );
		movimentoEconomicoPanel.add( receitaTotalTxt );
		movimentoEconomicoPanel.add( deducoesTxt );
		movimentoEconomicoPanel.add( receitaTributavelTxt );


		tipoReceitaPanel.add( tipoLancamentoLabel );
		tipoReceitaPanel.add( escrituradoRadioButton );
		tipoReceitaPanel.add( ajustadoRadioButton );	

		
        Border etched = BorderFactory.createEtchedBorder();
        TitledBorder titleBorder;
        titleBorder = BorderFactory.createTitledBorder( etched, "Documentos");
        titleBorder.setTitleJustification( TitledBorder.RIGHT );

		inputTabMapaEscolarPanel.setBorder( titleBorder );
		
		inputTabMapaEscolarPanel.add( descricaoCursoPanel );
		inputTabMapaEscolarPanel.add( exercicioMesCursoPanel );
		inputTabMapaEscolarPanel.add( movimentoEconomicoPanel );
		inputTabMapaEscolarPanel.add( tipoReceitaPanel );
		
		
		JButton incluirButton = new JButton("Incluir");
		incluirButton.setMnemonic('I');
		incluirButton.setToolTipText("Cadastra serviço prestado");
		incluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
					fmtCurrencyValues();
				    addRecord( myConnection );
					sepLookupMapaEscolar.updateSearching();										
				} catch( SQLException ignore ) {
					ignore.printStackTrace();					
				}    
			}
		});
		
		JButton excluirButton = new JButton("Excluir");
		excluirButton.setMnemonic('E');
		excluirButton.setToolTipText("Excluir registro do serviço prestado");
		excluirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
				    deleteRecord( myConnection );
					// reloads database containing newer updates
					sepLookupMapaEscolar.updateSearching();										
				    					
				} catch( SQLException ignore ) {
					ignore.printStackTrace();					
				}    
			}
		});

		
		
		JButton gravarButton = new JButton("Gravar");
		gravarButton.setMnemonic('G');
		gravarButton.setToolTipText("Atualiza serviço prestado");
		gravarButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
				try {
					fmtCurrencyValues();
					updateRecord( myConnection );
					sepLookupMapaEscolar.updateSearching();										
				} catch ( SQLException ignore ) {
					ignore.printStackTrace();
				}    
			}
		}); 		
		
		JButton imprimirButton = new JButton("Imprimir");
		imprimirButton.setToolTipText("Imprimir mapa de apuração");
		imprimirButton.setMnemonic('R');
		imprimirButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent evt ) {
			} 
		}); 		
		
		
		JPanel controlMapaEscolarPanel = new JPanel( new FlowLayout() );
		
		controlMapaEscolarPanel.add( incluirButton );
		controlMapaEscolarPanel.add( excluirButton );
		controlMapaEscolarPanel.add( gravarButton );
		//controlMapaEscolarPanel.add( imprimirButton );
				
		documentosTableModel = new TabMapaEscolarModel();		
		documentosTable = new JTable();
		JTableHeader headers = documentosTable.getTableHeader();
		headers.setReorderingAllowed( false );
		documentosTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
		documentosTable.addMouseListener( new MouseAdapter()  {
			public void mouseClicked( MouseEvent e ) {
				int count = documentosTable.getSelectedRowCount();
				if ( count > 0 ) {
                    inputTabMapaEscolarPanelRefresh();					
				}
			}
		});
		
		
		documentosTable.setModel( documentosTableModel );
		JScrollPane scrollpane = new JScrollPane( documentosTable );
		
		JPanel mapaEscolarDisplayPanel = new JPanel( new BorderLayout() ) ;
		
		mapaEscolarDisplayPanel.add( scrollpane );	
		
		
		tabDocumentosPanel.add( inputTabMapaEscolarPanel, BorderLayout.NORTH );
		tabDocumentosPanel.add( mapaEscolarDisplayPanel, BorderLayout.CENTER );
		tabDocumentosPanel.add( controlMapaEscolarPanel, BorderLayout.SOUTH );		
		
		return tabDocumentosPanel;	
		  
	}
	
	
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
	

		// identificação do curso
		String nomeCurso = nomeCursoTxt.getText();		
		String exercicioCurso = exercicioCursoTxt.getText();
		int  mes = mesCursoCombo.getSelectedIndex() + 1 ;
		String mesCurso = Integer.toString( mes ) ;
		if ( mes < 10 )
			mesCurso = "0" + mesCurso;
		
		// identificacao da turma
		String numeroAlunos = numeroAlunosTxt.getText();
		String mensalidadeCurso = mensalidadeCursoTxt.getText();
		
		// movimentação econômica
		String receitaTotal = receitaTotalTxt.getText();
		String deducoes = deducoesTxt.getText();
		String receitaTributavel = receitaTributavelTxt.getText();
		

		// tipo de lançamento 1 - Normal 2 -AJustado		
		String tipoLancamento = null;
		if ( escrituradoRadioButton.isSelected() ) {
			tipoLancamento = Integer.toString( LANCAMENTO_ESCRIT );
		}
		else {
			tipoLancamento = Integer.toString( LANCAMENTO_AJUST );
		}
		

	    String key = nomeCurso + exercicioCurso + mesCurso ;
	    
		boolean bufferHas = mapaEscolarRows.containsKey( key );
		if ( bufferHas ) {
			String err = "Tentativa de inserir registro duplicado";
			JOptionPane.showMessageDialog( null, err,
			      "Inclusão inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			// -- Includes data in table model, and so does refreshes gui 
			String lancamento[] = new String[ FIELDS_QTY ];					
			lancamento[   0  ] = nomeCurso;
			lancamento[   1  ] = exercicioCurso;
			lancamento[   2  ] = mesCurso;
			lancamento[   3  ] = numeroAlunos;
			lancamento[   4  ] = mensalidadeCurso;
			lancamento[   5  ] = receitaTotal;
			lancamento[   6  ] = deducoes;
			lancamento[   7  ] = receitaTributavel;
			lancamento[   8  ] = tipoLancamento;
			
			String ignore = "0";
						
			// The chunck of data is right,then proceed to addding them to database
			String cmd = "INSERT INTO tabMapaEnsino " +
			" VALUES  ('"  + currOS + "','" + currCMC + "','" 
			            + nomeCurso + "','" + exercicioCurso + "','"
			            + mesCurso + "','" + numeroAlunos + "','"
			            + SEPConverter.convertFrmtCurrencyToMySQL(mensalidadeCurso)  + "','" 
			            + SEPConverter.convertFrmtCurrencyToMySQL(receitaTotal) + "','"
			            + SEPConverter.convertFrmtCurrencyToMySQL(deducoes) + "','"
			            + SEPConverter.convertFrmtCurrencyToMySQL(receitaTributavel) + "','" 
			            + tipoLancamento + " ')" ;
			               
			myStatement.executeUpdate( cmd );
			con.commit();		
			
			
			mapaEscolar.add( lancamento );
			
			bufferRefresh();                // updating buffer
			documentosTable.setModel( documentosTableModel );
			documentosTable.revalidate();
			documentosTable.repaint();
			revalidate();
			repaint();
			
			System.out.println( " incluiu " );		
			
			
		}
			
	}
	
	/**
	 * Delete records from database
	 *
	 * precondition: Must be garanted that there is just a record 
	 *               with the key to be deleted	 
	 * @param <code>con</code> represents a Connection object
	 *         necessary for creating the statement  
	 * @throws SQLException if ocurrs some error with Connection
	 */	
	private void deleteRecord( Connection con ) throws SQLException {
		
		
		int selectedRow = documentosTable.getSelectedRow();
		
		
		// -- primary key into to choicen record 
		String nomeCurso = (String) documentosTable.getValueAt(selectedRow, 0 );
		String exercicioCurso = (String) documentosTable.getValueAt(selectedRow, 1 );
		String mesCurso = (String) documentosTable.getValueAt(selectedRow, 2 );
		
	    String searchKey = nomeCurso + exercicioCurso + mesCurso ;
	    
		Integer rowID = (Integer) mapaEscolarRows.get( searchKey );
		
	
	 	System.out.println( " rowID = " + rowID );
		// -- position in the buffer of choicen record
		int index = rowID.intValue(); 
		
		System.out.println( "index = " + index );		
                   
		mapaEscolar.remove( index );
		bufferRefresh();                // updating buffer
		documentosTable.setModel( documentosTableModel );
		documentosTable.revalidate();
		documentosTable.repaint();
		revalidate();
		repaint();

		String cmd = "DELETE FROM  tabMapaEnsino" + 
		             " WHERE osMapaEnsino = '" + currOS + "'" +
		             " AND cmcMapaEnsino ='" + currCMC + "'" +
		             " AND cursoMapaEnsino ='" + nomeCurso + "'" +
		             " AND exercicioMapaEnsino ='" + exercicioCurso + "'" +
  		             " AND mesMapaEnsino ='" + mesCurso + "'"  ; 
  		             
 		myStatement.executeUpdate( cmd );

		con.commit();					
		
		System.out.println( " removeu " );		
		
		
	}
	
	
	/*	
	 * Updates the selected record currently of fiscaisTable table 
	 *
	 * precondition: Must check that there is a subjacent key in database
	 *               if it will update the record
	 * @param <code>con</code> represents a Connection object
	 *         necessary for creating the statement  
	 * @throws SQLException if ocurrs some error with Connection
	 */	
	private void updateRecord( Connection con ) throws SQLException {
		
		int selectedRow = documentosTable.getSelectedRow();		
		
		// -- primary key into to choicen record 
		String nomeCurso = (String) documentosTable.getValueAt(selectedRow, 0 );
		String exercicioCurso = (String) documentosTable.getValueAt(selectedRow, 1 );
		String mesCurso = (String) documentosTable.getValueAt(selectedRow, 2 );
		
	    String searchKey = nomeCurso + exercicioCurso + mesCurso ;
		
		boolean bufferHas = mapaEscolarRows.containsKey( searchKey );
		if ( !bufferHas ) {
			String err = "Tentativa de atualizar registro inexistente";
			JOptionPane.showMessageDialog( null, err,
			      "Atualizacao inválida", JOptionPane.ERROR_MESSAGE   );
		}
		else {
			
			// identificação da turma
			String numeroAlunos = numeroAlunosTxt.getText();
			String mensalidadeCurso = mensalidadeCursoTxt.getText();
			
			// movimentação econômica
			String receitaTotal = receitaTotalTxt.getText();
			String deducoes = deducoesTxt.getText();
			String receitaTributavel = receitaTributavelTxt.getText();
			
	
		
			String tipoLancamento = null;
			if ( escrituradoRadioButton.isSelected() ) {
				tipoLancamento = Integer.toString( LANCAMENTO_ESCRIT );
			}
			else {
				tipoLancamento = Integer.toString( LANCAMENTO_AJUST );
			}

			
			String lancamento[] = new String[ FIELDS_QTY ];					
			lancamento[   0  ] = nomeCurso;
			lancamento[   1  ] = exercicioCurso;
			lancamento[   2  ] = mesCurso;
			lancamento[   3  ] = numeroAlunos;
			lancamento[   4  ] = mensalidadeCurso;
			lancamento[   5  ] = receitaTotal;
			lancamento[   6  ] = deducoes;
			lancamento[   7  ] = receitaTributavel;
			lancamento[   8  ] = tipoLancamento;

			Integer rowID = (Integer) mapaEscolarRows.get(  searchKey  );
			// -- position in the buffer of choicen record
			int index = rowID.intValue();
			// -- remove first and insert again. Doing this, is not 
			// -- necessary to worry with refreshing cacher buffer
			mapaEscolar.remove( index );
			mapaEscolar.add( index, lancamento );		

			documentosTable.setModel( documentosTableModel );
			documentosTable.revalidate();
			documentosTable.repaint();  
			
			// -- The calling for updating records is done
			// Here, is upadted just the field valor because
			// the other fields behaviour as unique keys			
			
			String cmd = "UPDATE tabMapaEnsino "
			          + "SET qtdAlunosMapaEnsino ='" + numeroAlunos + "', "
			          + "mensalidadeMapaEnsino ='"
			          +  SEPConverter.convertFrmtCurrencyToMySQL(mensalidadeCurso) + "', "
			          + "receitaTotalMapaEnsino ='"
			          + SEPConverter.convertFrmtCurrencyToMySQL(receitaTotal) + "', "
			          + "deducoesMapaEnsino ='"
			          + SEPConverter.convertFrmtCurrencyToMySQL(deducoes) + "', " 
			          + "receitaLiquidaMapaEnsino ='"
			          + SEPConverter.convertFrmtCurrencyToMySQL(receitaTributavel) + "', "
			          + "tipoLancamentoMapaEnsino ='" +  tipoLancamento + "' "
				      +  " WHERE osMapaEnsino ='" + currOS + "'" 
					  +  " AND cmcMapaEnsino ='" + currCMC + "'" 
					  +  " AND cursoMapaEnsino ='" + nomeCurso + "'" 
					  +  " AND exercicioMapaEnsino ='" + exercicioCurso + "'"
					  +  " AND mesMapaEnsino ='" + mesCurso + "'"  ;
			
			
	 		myStatement.executeUpdate( cmd );		
	 		
			con.commit();	
			System.out.println( " gravou " );		
			
		}		
		 
	}	
	
	
	/**
	 *  It will update textfield when a record is selected inthe TabEmpresasPanel 
	 */
    private void inputTabMapaEscolarPanelRefresh() {   	
    
    
    	String value;    	
		int selectedRow = documentosTable.getSelectedRow();
		
		// identificação do curso
		value = (String) documentosTable.getValueAt(selectedRow, 0 );
		nomeCursoTxt.setText( value );
		value = (String) documentosTable.getValueAt(selectedRow, 1 );
		exercicioCursoTxt.setText( value );
		value = (String) documentosTable.getValueAt(selectedRow, 2 );
		int mes = Integer.parseInt( value ) - 1 ;
		mesCursoCombo.setSelectedIndex( mes ) ;		
		
		// identificação da turma
		value = (String) documentosTable.getValueAt(selectedRow, 3 );
		numeroAlunosTxt.setText( value );
		value = (String) documentosTable.getValueAt(selectedRow, 4 );
		mensalidadeCursoTxt.setText( value );
		
		// identificao movimento econômico
		value = (String) documentosTable.getValueAt(selectedRow, 5 );
		receitaTotalTxt.setText( value );
		value = (String) documentosTable.getValueAt(selectedRow, 6 );
		deducoesTxt.setText( value );
		value = (String) documentosTable.getValueAt(selectedRow, 7 );
		receitaTributavelTxt.setText( value );		
		

		value = (String) documentosTable.getValueAt( selectedRow, 8 );
		
		int choice = Integer.parseInt( value ) ;
		
		if ( choice == LANCAMENTO_ESCRIT  ) {
			escrituradoRadioButton.setSelected( true );			
		}
		else {
			ajustadoRadioButton.setSelected( true );			
		}
		
		revalidate();		
		
		
    }
    
    
	private void loadMapaEscolarTable( ResultSet rs ) {
		
	   int cols = columnNames.length;
	   mapaEscolarRows = new HashMap();
	   mapaEscolar = new Vector();
	   bufferRecordPos = 0;
	   String key = null;
	   
	   try {	   	
	   
	   	   /**
	        * Inserts all the data as a vector of Object[] ROWS
	        * It was not used Object[][] because is not knew how many rows
	        * the ResultSet has
	        */
	        while ( rs.next() ) {
	        	
	    	    String[] theRow = new String[ cols ];
	    	    
	    	    String nomeCurso = (String)rs.getString( 1 );
	    	    String exercicioCurso = (String)rs.getString( 2 );
	    	    String mesCurso = (String) rs.getString( 3 );
	    	    String qtdAlunos = (String) rs.getString( 4 );
	    	    
				String vlrMensal = SEPConverter.adapterCurrencyFrmt( rs.getString( 5 ) );
				double dummy = Double.parseDouble( vlrMensal ) ;
				String fmtVlrMensal = getFmtCurrency( dummy );	    	    
				String mensalidade = fmtVlrMensal;

				String vlrReceita = SEPConverter.adapterCurrencyFrmt( rs.getString( 6 ) );
				dummy = Double.parseDouble( vlrReceita ) ;
				String fmtVlrReceita = getFmtCurrency( dummy );	    	    
				String receita = fmtVlrReceita;

				String vlrDeducoes = SEPConverter.adapterCurrencyFrmt( rs.getString( 7 ) );
				dummy = Double.parseDouble( vlrDeducoes ) ;
				String fmtVlrDeducoes = getFmtCurrency( dummy );	    	    
				String deducoes = fmtVlrDeducoes;

				String vlrRecTrib = SEPConverter.adapterCurrencyFrmt( rs.getString( 8 ) );
				dummy = Double.parseDouble( vlrRecTrib ) ;
				String fmtVlrRecTrib = getFmtCurrency( dummy );	    	    
				String receitaTributavel = fmtVlrRecTrib;

	    	    String tipoLancamento =  rs.getString( 9 ) ;       
	    	      
	    	    theRow[ 0 ] = nomeCurso;
	    	    theRow[ 1 ] = exercicioCurso;
	    	    theRow[ 2 ] = mesCurso;
	    	    theRow[ 3 ] = qtdAlunos;
	    	    theRow[ 4 ] = mensalidade ;
	    	    theRow[ 5 ] = receita;
	    	    theRow[ 6 ] = deducoes;
	    	    theRow[ 7 ] = receitaTributavel;
	    	    theRow[ 8 ] = tipoLancamento;  

	    	    key = nomeCurso + exercicioCurso + mesCurso;
	    	    mapaEscolarRows.put( key, new Integer( bufferRecordPos ) );
	    	    mapaEscolar.add( theRow );
	    	    bufferRecordPos++;
	    	
	        }
	   }    
	   catch ( SQLException sqlException ) {
	   	   sqlException.printStackTrace();
	   }

	}
	
	/**
	 * It will update the rows hash (buffer ) if it delets a record
	 */
	private void bufferRefresh() {
		String[] lancamento = new String[ FIELDS_QTY ];
		String searchKey;
		mapaEscolarRows.clear();
		for ( int i = 0; i < mapaEscolar.size() ; i++ ) {
			lancamento = (String[]) mapaEscolar.get( i );
			String nomeCurso = lancamento[ 0 ];
			String exercicioCurso = lancamento[ 1 ];
			String mesCurso = lancamento[ 2 ];
			searchKey = nomeCurso + exercicioCurso + mesCurso;
			mapaEscolarRows.put( searchKey, new Integer( i ) );
		}
	}
	
	
	private class TabMapaEscolarModel extends AbstractTableModel { 
		
		public int getRowCount() {
			return mapaEscolarRows.size();
		}
		
		public Object getValueAt( int r , int c ) {
			String[] theRow = null;
			if ( r < mapaEscolar.size() ) {
				Object obj =  mapaEscolar.get( r ) ;
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
	
	private class ComputeIncomingsAction extends AbstractAction {
		int qtdAlunos ;
		float mensalidade;
		float receitaTotal;
		float deducoes;
		float receitaLiquida;
		
		public void actionPerformed(ActionEvent e ) {
			try {
				
				String vlrMensal = mensalidadeCursoTxt.getText();
				String value = SEPConverter.adapterCurrencyFrmt( vlrMensal );
				double dummy = Double.parseDouble( value ) ;
				String fmtValueDoc = getFmtCurrency( dummy );
				mensalidadeCursoTxt.setText("");
				mensalidadeCursoTxt.setText( fmtValueDoc );
				
				String vlrDeducao = deducoesTxt.getText();
				String value2 = SEPConverter.adapterCurrencyFrmt( vlrDeducao );
				double dummy2 = Double.parseDouble( value2 ) ;
				String fmtValueDed = getFmtCurrency( dummy2 );
				deducoesTxt.setText("");
				deducoesTxt.setText( fmtValueDed );
				
				
			    qtdAlunos = Integer.parseInt( numeroAlunosTxt.getText() );
			    
			    String mensalidadeCurso =
			       SEPConverter.adapterCurrencyFrmt(
			       	  mensalidadeCursoTxt.getText());
			    
			    mensalidade = Float.parseFloat( mensalidadeCurso );
			    
			    receitaTotal = qtdAlunos * mensalidade;
			    
				String fmtValueRecBruta = getFmtCurrency( receitaTotal );
				receitaTotalTxt.setText("");
				receitaTotalTxt.setText( fmtValueRecBruta );

/*			    String receitaBruta =
			      SEPConverter.convertFrmtCurrencyFromMySQL(
			       SEPConverter.adapterDecimalCurrencyFrmt(
			       	   Float.toString( receitaTotal )));
			    
			    receitaTotalTxt.setText( receitaBruta ) ; */

			    String deducoesReceita = 
			       SEPConverter.adapterCurrencyFrmt(
			       	   deducoesTxt.getText() );			       	   
			       	
			    deducoes = Float.parseFloat( deducoesReceita );
			    
			    receitaLiquida = receitaTotal - deducoes;
			    
				String fmtValueRecTrib = getFmtCurrency( receitaLiquida );
				receitaTributavelTxt.setText("");
				receitaTributavelTxt.setText( fmtValueRecTrib );

			    /*String receitaTributavel =
			      SEPConverter.convertFrmtCurrencyFromMySQL(
			      	  SEPConverter.adapterDecimalCurrencyFrmt(
			      	  	  Float.toString( receitaLiquida ) ) );
			    receitaTributavelTxt.setText( receitaTributavel ); */
			    
			    revalidate();
			 }
		     catch (	NumberFormatException nfe ) {
				String err = "Campo de registro inválido. Corrija.";
		 			JOptionPane.showMessageDialog( null, err,
			    		  "Formato de campo inválido", 
			    		     JOptionPane.ERROR_MESSAGE   );
		}		
			    
						
		} 
	}
	
	private void fmtCurrencyValues() {
		int qtdAlunos ;
		float mensalidade;
		float receitaTotal;
		float deducoes;
		float receitaLiquida;
		try {
				
			String vlrMensal = mensalidadeCursoTxt.getText();
			String value = SEPConverter.adapterCurrencyFrmt( vlrMensal );
			double dummy = Double.parseDouble( value ) ;
			String fmtValueDoc = getFmtCurrency( dummy );
			mensalidadeCursoTxt.setText("");
			mensalidadeCursoTxt.setText( fmtValueDoc );
				
			String vlrDeducao = deducoesTxt.getText();
			String value2 = SEPConverter.adapterCurrencyFrmt( vlrDeducao );
			double dummy2 = Double.parseDouble( value2 ) ;
			String fmtValueDed = getFmtCurrency( dummy2 );
			deducoesTxt.setText("");
			deducoesTxt.setText( fmtValueDed );
				
				
			qtdAlunos = Integer.parseInt( numeroAlunosTxt.getText() );
			    
			String mensalidadeCurso =
				   SEPConverter.adapterCurrencyFrmt(
					  mensalidadeCursoTxt.getText());
			    
			mensalidade = Float.parseFloat( mensalidadeCurso );
			    
			receitaTotal = qtdAlunos * mensalidade;
			    
			String fmtValueRecBruta = getFmtCurrency( receitaTotal );
			receitaTotalTxt.setText("");
			receitaTotalTxt.setText( fmtValueRecBruta );

			String deducoesReceita = 
			   SEPConverter.adapterCurrencyFrmt(
				   deducoesTxt.getText() );			       	   
			       	
			deducoes = Float.parseFloat( deducoesReceita );
			    
			receitaLiquida = receitaTotal - deducoes;
			    
			String fmtValueRecTrib = getFmtCurrency( receitaLiquida );
			receitaTributavelTxt.setText("");
			receitaTributavelTxt.setText( fmtValueRecTrib );
			revalidate();
		}
		catch (	NumberFormatException nfe ) {
				String err = "Campo de registro inválido. Corrija.";
					JOptionPane.showMessageDialog( null, err,
						  "Formato de campo inválido", 
							 JOptionPane.ERROR_MESSAGE   );
		}		
	}
	
	private String getFmtCurrency( double value ){	
		NumberFormat nf = NumberFormat.getInstance( Locale.GERMANY );
		nf.setMaximumFractionDigits( 2 );
		nf.setMinimumFractionDigits( 2 );
		String formattedNumber = nf.format( value );		
		return formattedNumber;
	}
	
	
/*	private class FmtVlrMensalidade implements ActionListener {
		  public void actionPerformed( ActionEvent e ) {
			try {    	  
				String mensalidade = mensalidadeCursoTxt.getText();
				String value = SEPConverter.adapterCurrencyFrmt( mensalidade );
				double dummy = Double.parseDouble( value ) ;
				String fmtValueDoc = getFmtCurrency( dummy );
				mensalidadeCursoTxt.setText("");
				mensalidadeCursoTxt.setText( fmtValueDoc );
			}
			catch( Exception ex ) {
				JOptionPane.showMessageDialog( null, "Valor incorreto.\n" +
					  "Formato válido deve ser: 99999,99 ou 99.999,99." ,
						"Entrada inválida", JOptionPane.ERROR_MESSAGE);                           
			}
		  }    	
	} */
	
	private class FmtVlrDeducoes implements ActionListener {
		  public void actionPerformed( ActionEvent e ) {
			try {    	  
				String deducoes = deducoesTxt.getText();
				String value = SEPConverter.adapterCurrencyFrmt( deducoes );
				double dummy = Double.parseDouble( value ) ;
				String fmtValueDoc = getFmtCurrency( dummy );
				deducoesTxt.setText("");
				deducoesTxt.setText( fmtValueDoc );
			}
			catch( Exception ex ) {
				JOptionPane.showMessageDialog( null, "Valor incorreto.\n" +
					  "Formato válido deve ser: 99999,99 ou 99.999,99." ,
						"Entrada inválida", JOptionPane.ERROR_MESSAGE);                           
			}
		  }    	
	}	
	
	private class CursosKeySearcher extends KeyAdapter {
		
		String[] cursos = sepLookupMapaEscolar.getNomeCursosList();
		String searchKey ="";
		public void keyTyped(KeyEvent e) {
			char ch = e.getKeyChar();
			if ( !Character.isLetterOrDigit( ch ))
				return;
				
			if ( nomeCursoTxt.getText() != "" ) {
				searchKey += nomeCursoTxt.getText() + ch ;
				for ( int k = 0; k < cursos.length; k++ ) {
					String str = cursos[ k ];
					if ( str.startsWith( searchKey )) {
						nomeCursoTxt.setText("");
						nomeCursoTxt.repaint();
						nomeCursoTxt.setText( cursos[ k ] );
						nomeCursoTxt.repaint();
						searchKey = new String("");
						break;
					}				   
				}    
				searchKey = new String("");
			} 
		}
	}
	
	private class ExerciciosKeySearcher extends KeyAdapter {
		
		String[] anosRef = sepLookupMapaEscolar.getAnosRef();
		String searchKey ="";
		public void keyTyped(KeyEvent e) {
			char ch = e.getKeyChar();
			if ( !Character.isLetterOrDigit( ch ))
				return;
				
			if ( exercicioCursoTxt.getText() != "" ) {
				searchKey += exercicioCursoTxt.getText() + ch ;
				for ( int k = 0; k < anosRef.length; k++ ) {
					String str = anosRef[ k ];
					if ( str.startsWith( searchKey )) {
						exercicioCursoTxt.setText("");
						exercicioCursoTxt.repaint();
						exercicioCursoTxt.setText( anosRef[ k ] );
						exercicioCursoTxt.repaint();
						searchKey = new String("");
						break;
					}				   
				}    
				searchKey = new String("");
			} 
		}
	}
		
	
	
}
