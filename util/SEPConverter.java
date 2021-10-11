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
import java.util.*;
import java.text.*;

public class SEPConverter {

	public static String converteFrmtDataFromMySQL( String currData ) {
		if ( currData != "N/A" ) {
		    StringTokenizer st = new StringTokenizer( currData,"-");
		    String year = st.nextToken();
		    String month = st.nextToken();
		    String day = st.nextToken();
		
		    String date = day + "/" + month + "/" + year;
		
		    return date;
		}
		return "N/A";    
	}
	
	public static  String converteFrmtDataToMySQL( String currData ) {
		if ( currData.length() != 0 ) {
		    StringTokenizer st = new StringTokenizer( currData,"/");
		    String day = st.nextToken();
		    String month = st.nextToken();
		    String year = st.nextToken();
		
		    String date = year + "-" + month + "-" + day;
		    return date;
		}
		return  "N/A";
	}
	
	
	
	/**
	 * Convertes a string representing a based-currency 99999,99 read
	 * from database for a specific input-screen format 99.99900
	 * a specific format 99999,99
	 * 
	 * @param <code>currCurrency</code> indicates a string that realizes
	 *        the onversion of data 
	 */
	public static String convertFrmtCurrencyFromMySQL( String currCurrency ) {
		
		try {
			
		    
		    NumberFormat nf = NumberFormat.getInstance(); 
		    
		    // PENDENCIA: BUG
		    // It seems that numbers of type '89,90' doesnt possible
		    // to convert using Float. It is necessary, before uses
		    // transforming it for the '89.90' format
		    int len = currCurrency.length();
		    StringBuffer sb = new StringBuffer();
		    for ( int i = 0; i < len; i++ ) {
		    	if ( currCurrency.charAt( i ) == ',' ) {
		    		sb.append( '.' );
		    	}
		    	else {
		    		sb.append( currCurrency.charAt( i ) );
		    	}		    	
		    }
		    
		    String adaptedCurrencyValue = sb.toString();
		    
		    float valueRead  = Float.parseFloat( adaptedCurrencyValue ) ;

		    return (nf.format( valueRead )).toString();
		    
		}
		catch ( ClassCastException e ) {
			System.err.println( e );
		}      		
		return null; 
	}
	
	/**
	 * Convertes a string representing a based-currency 99.999,99 read
	 * from  specific input-screen to the format 99999,00 of the database
	 * 
	 * @param <code>currCurrency</code> indicates a string where will be
	 *        realized the conversion of the data
	 */	
	public static String convertFrmtCurrencyToMySQL( String currCurrency ) {
		if ( currCurrency != null ) {
		    StringBuffer sb = new StringBuffer();
		    int len = currCurrency.length();
		
		    for ( int i = 0; i < len; i++ ) {
			    if ( currCurrency.charAt( i ) != '.' ) {
				    sb.append( currCurrency.charAt( i ) ) ;
			    }
		    }
		
		    return sb.toString();
		}
		else {
			return "N/A";   
		}
		
	}
	
	/**
	 * Convertes a string represeting a cnpj containint a mask 78.976.789/0987-34
	 * to a format non-mask: 78976789098734
	 * @param <code>cnpj</code> indicasts a string where will be done the conversion 
	 * @return A <code>string</code> has no mask 
	 */
	public static String convertFrmtCNPJToMySql( String cnpj ) {
		StringBuffer retVal = new StringBuffer();
		int len = cnpj.length();
		
		for ( int i = 0; i < len; i++ ) {
			if ( cnpj.charAt( i ) != '.' || cnpj.charAt( i ) != '/' 
			        || cnpj.charAt( i ) != '-') {
			        	retVal.append( cnpj.charAt( i ));
			        }
		}
		return retVal.toString();
	}
	
	/**
	 * Adaptes the number format 9.999,99 to 9999.99 and so manner, 
	 * it is possible realize conversion of data
	 *
	 * @param <code>currCurrency<code/> indicates a string to be adapted
	 *
	 */
	public static String adapterCurrencyFrmt( String currCurrency ) {
		    // PENDENCIA: BUG
		    // It seems that numbers of type '2.389,90' doesnt possible
		    // to convert using Float. It is necessary, before uses
		    // transforming it for the '2389.90' format
		    int len = currCurrency.length();
		    StringBuffer sb = new StringBuffer();
		    for ( int i = 0; i < len; i++ ) {
		    	if ( currCurrency.charAt( i ) == ',' ) {
		    		sb.append( '.' );
		    	}
		    	else if ( currCurrency.charAt( i ) != '.')  {
		    		sb.append( currCurrency.charAt( i ) );
		    	}		    	
		    }
		    
		    String adaptedCurrencyValue = sb.toString();
		    
		    return adaptedCurrencyValue;
	}
	
	/**
	 * Adaptes the number format 9999.99 to 9999,99 and so way, 
	 * it is possible realize conversion of currency format
	 *
	 * @param <code>currCurrency<code/> indicates a string to be adapted
	 *
	 */
	public static String adapterDecimalCurrencyFrmt( String currCurrency ) {
		    int len = currCurrency.length();
		    StringBuffer sb = new StringBuffer();
		    for ( int i = 0; i < len; i++ ) {
		    	if ( currCurrency.charAt( i ) == '.' ) {
		    		sb.append( ',' );
		    	}
		    	else  {
		    		sb.append( currCurrency.charAt( i ) );
		    	}		    	
		    }
		    
		    String adaptedCurrencyValue = sb.toString();
		    
		    return adaptedCurrencyValue;
	}
	
	public static String insertMaskCNPJ( String cnpj ) {
		String retVal = cnpj;
		if ( cnpj.length() == 14 ) {
			String first = cnpj.substring( 0, 2 );
			String second = cnpj.substring( 2, 5 );
			String third = cnpj.substring( 5, 8 );
			String four = cnpj.substring( 8, 12 );
			String digit = cnpj.substring( 12, 14 );
			String dummy = first + "." + second + "." +
						   third + "/" + four + "-" + digit;
			retVal = dummy;			   
		}
		return retVal;
	}
	
	public static String insertMaskCMC( String cmc ) {
		String retVal = cmc;
		if ( cmc.length() == 7 ) {
			String first = cmc.substring( 0, 3 );
			String second = cmc.substring( 3, 6 );
			String digit = cmc.substring( 6, 7 );
			String dummy = first + "." + second + "-" + digit;
			retVal = dummy;
		}
		return retVal;
	}
	
	/**
	 * Cut the mask off previously inserted in textfield
	 * This occurs because the database doesnt contain masked field
	 * @param <code>cnpj</code> indicates the field to have the mask removed
	 * @returnps A <code>String</code> has not mask  
	 */
	public static String removeMaskCNPJ( String cnpj ) {
		String retVal = new String("") ;
		for ( int i = 0; i < cnpj.length(); i++ ){
			if ( ( cnpj.charAt( i ) == '.' ) 
				 || ( cnpj.charAt( i ) ==  '-' ) || ( cnpj.charAt( i ) == '/') )
			   continue;
			retVal += cnpj.charAt( i );   
		}
		return retVal;
	}	
	
	
	/**
	 * Cut the mask off previously inserted in textfield
	 * This occurs because the database doesnt contain masked field
	 * @param <code>cmc</code> indicates the field to have the mask removed
	 * @return A <code>String</code> has not mask  
	 */
	public static String removeMaskCMC( String cmc ) {
		String retVal = new String("") ;
		for ( int i = 0; i < cmc.length(); i++ ){
			if ( ( cmc.charAt( i ) == '.' ) || ( cmc.charAt( i ) ==  '-' ) )
			   continue;
			retVal += cmc.charAt( i );   
		}
		return retVal;
	}
	
	
	public static String getFmtCurrency( double value ){	
		NumberFormat nf = NumberFormat.getInstance( Locale.GERMANY );
		nf.setMaximumFractionDigits( 2 );
		nf.setMinimumFractionDigits( 2 );
		String formattedNumber = nf.format( value );		
		return formattedNumber;
	} 	
	
	
	
	
	
}	
