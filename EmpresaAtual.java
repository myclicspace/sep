
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

import java.sql.*;

public class EmpresaAtual {   

	private static String numeroOS = null;
	private static String cmcEmpresa = null;
	private static String razaoSocialEmpresa = null;
	private static String cnpjEmpresa = null;	
	private static String enderecoEmpresa = null;
	private static String usuarioSistema = null; 

	public static void setNumeroOS( String os ) {
		numeroOS = os;
	}

	public static void setCMCEmpresa( String cmc ) {
		cmcEmpresa = cmc;
	}
	
	public static void setUsuario( String usuario ) {
		usuarioSistema = usuario;
	}

	public static void setCNPJRazaoSocialEndereco( String cmc, Connection c) {
	   
	   String query = "SELECT * FROM tabempresa WHERE cmcEmpresa = '" + cmc + "'";

	   try
	   {
		    PreparedStatement stmt = c.prepareStatement(query);
			ResultSet rs = (ResultSet)stmt.executeQuery();   
	   
	      	if (rs.next()) {

				razaoSocialEmpresa = rs.getString( 2 );
	      		cnpjEmpresa = rs.getString( 3 );
	      		enderecoEmpresa = rs.getString( 4 ) + " - " + rs.getString( 5 )
	      		+ " - " + rs.getString( 6 ) + " - " + rs.getString( 7 );

		    }
		    rs.close();
	        stmt.close();
	   }
	   catch(SQLException sqle)
	   {
		  System.out.println(sqle.toString());		  
	   }
	}

	public static String getNumeroOS() {
		return numeroOS;
	}
 
	public static String getCMCEmpresa() {
		return cmcEmpresa; 
	}
	
	public static String getRazaoSocialEmpresa() {
		return razaoSocialEmpresa;
	}

	public static String getCNPJEmpresa() {
		return cnpjEmpresa;
	}

	public static String getEnderecoEmpresa() {
		return enderecoEmpresa;
	}

	public static String getUsuario() {
		return usuarioSistema;
	}
}
