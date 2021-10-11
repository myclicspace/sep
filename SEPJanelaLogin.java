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
//import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
//import java.io.*;
//import java.util.*;
import java.sql.*;


public class SEPJanelaLogin extends JFrame {

	private JTextField usuarioText;
	private JPasswordField senhaText;
	private static Connection c;
	private Container container;
	private static SEPJanelaLogin janela = null;
	private static final String IMAGE_DIRECTORY = 
            System.getProperty("user.dir") +
            System.getProperty("file.separator") +  "img" + 
            System.getProperty("file.separator") ;
		
	public SEPJanelaLogin () {

		super( "Informe código do usuário e senha" );
		
		String version = System.getProperty("java.version");
		char minor = version.charAt( 2 );
		char point = version.charAt( 4 ); 
		if ( minor < '4' )
		  throw new RuntimeException("JDK 1.4.0 ou superior é necessário");
//		  System.out.println("JDK versao " + version + " encontrada");
		c = abreConexao();
		container = getContentPane();
		container.setLayout( new FlowLayout() );

		JLabel usuarioLabel = new JLabel("Matricula");
		usuarioText = new JTextField(  6 ); 
		JLabel senhaLabel = new JLabel("Senha");
		senhaText = new JPasswordField( 8 );		
		
  		JButton botaoConfirmar = new JButton( "Confirmar" );
		
		container.add( usuarioLabel );
		container.add( usuarioText );
		container.add( senhaLabel );
		container.add( senhaText );
  		container.add( botaoConfirmar);		

  		ButtonHandler handler = new ButtonHandler(); 
  		botaoConfirmar.addActionListener( handler );
  		senhaText.addActionListener( handler );
  		setSize( 400, 75 );
		ImageIcon brasaoImage = new ImageIcon(IMAGE_DIRECTORY + "brasao.gif");
  		setIconImage(brasaoImage.getImage());
  		
  		
  		// Centraliza tela
  		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
  		int x = ( int ) ( ( d.getWidth() - this.getWidth() ) / 2 );
  		int y = ( int ) ( ( d.getHeight() - this.getHeight() ) / 2 );
  		this.setLocation( x, y );
  		
  		setVisible( true );
  		setResizable( false );
  		
	}

	public static void main (String args[]) {
  		
		janela = new SEPJanelaLogin();
  		janela.addWindowListener(new WindowAdapter() {
       	    public void windowClosing (WindowEvent e) { 
	     	    System.exit(0);
	   	    }
    	});
	}

	private void consultaFiscal() {
	   String query = "SELECT * FROM tabUsuarios WHERE usuario = '" +
	        usuarioText.getText() + "' AND senha = '" + 
	        senhaText.getText() + "' ORDER BY usuario";
	   boolean resposta = false;
	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	      	if ( rs.next() ) {
				resposta = true;
   	   		}
		    rs.close();
	        stmt.close();
	   }
	   catch(SQLException sqle)
	   {
			System.out.println( sqle.toString() );	   
		}
	   catch(Exception e)
	   {
			System.out.println( e.toString() );
	   }
	   if ( resposta ) {
			EmpresaAtual.setUsuario( usuarioText.getText() );
			this.dispose();	
  			SEPMain sepMain = new SEPMain( c );
	   }
	   else {
			JOptionPane.showMessageDialog (this, 
			"Matrícula inexistente ou senha incorreta !",
			 "Acesso inválido", JOptionPane.ERROR_MESSAGE);
			usuarioText.setText( "" );
			senhaText.setText( "" );			
	   }
	   return;
	}

  /* private Connection abreConexao() {
  	Connection con = null;
  	try {
        String driver = "sun.jdbc.odbc.JdbcOdbcDriver";
        String driverUrl = "jdbc:odbc:sep";
     	String user = "sep";
     	String senha = "sep";
     	Class.forName(driver);
     	con = DriverManager.getConnection(driverUrl, user, senha);
		System.out.println("Conexao realizada com sucesso");
    }
    catch ( SQLException sqle ) {
    	sqle.printStackTrace();
		System.out.println("SQL Exception = " + sqle.toString());		
    } 	
    catch( ClassNotFoundException cnfe ) {
    	cnfe.printStackTrace();
		System.out.println("Class Not Found Exception = " + cnfe.toString());					
    }
   	return con;
  } */
   
  
  private Connection abreConexao() {
      Connection myCon = null;
   	  Statement mySta = null;
  	  String driver = "org.gjt.mm.mysql.Driver";
  	  String driverUrl = "jdbc:mysql://localhost/sep";
  	  String user ="root";
  	  String pwd = "";
  	 try {  	
  	
		Class.forName(driver); // .newInstance();
		if (myCon==null) {
			myCon = DriverManager.getConnection( driverUrl, user, pwd );
		}  	
  	
     }
     catch ( Exception e ) {
    	e.printStackTrace();
     } 	
    
   	return myCon; 
  }
  


	private class ButtonHandler implements ActionListener {
  		public void actionPerformed (ActionEvent e) {
			consultaFiscal();
		}
	}
}	
