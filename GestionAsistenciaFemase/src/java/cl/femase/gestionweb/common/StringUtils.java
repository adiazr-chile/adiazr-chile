package cl.femase.gestionweb.common;

/**
This class add functionality to String tools.
Permits add white spaces to some String or replace word
inside some String, and so.
 */
public class StringUtils {

    /**
    This method fill the String passed as parameter with
    white spaces depending of quantity indicated in the numSpaces
    parameter.
    @param aString String to fill with spaces.
    @param numSpaces quantity of spaces to fill.
    @return String fixed.
    */
    public final String fillSpaces(String aString, int numSpaces) {
        StringBuffer padedString = new StringBuffer(aString);
        // If the String is too long this will be cut.
        if (padedString.length() > numSpaces) {
            padedString.setLength(numSpaces);
        } else {  // Is filled with white spaces.
            int n = numSpaces - padedString.length();
            while (n > 0) {
                padedString.append(' ');
                n--;
            }
        }
        return padedString.toString();
    }

    /**
    This method replace a word by another word inside of String.
    This method replace all ocurrencies of specified String.
    @param aString String to replace internally the specified word.
    @param oldWord String to be replaced.
    @param newWord new String to put where the oldString is located.
    @return String fixed.
     */
    public final String replaceString(String aString, String oldWord, String newWord) {
        if ((aString != null) && (oldWord != null) && (newWord != null)) {
            int position = aString.indexOf(oldWord);
            if (position >= 0) {
                StringBuffer result = new StringBuffer(aString.substring(0, position));
                result.append(newWord);
                result.append(aString.substring(position + oldWord.length()));
                return (new StringUtils()).replaceString(result.toString(), oldWord, newWord);
            } else {
                return aString;
            }
        } else {
            throw new NullPointerException("Null argument.");
        }
    }

    /**
    This method remove all none numeric characters from the string.
    @param aString String
    @return String fixed.
     */
    public final String removeNoneNumeric(String aString) {
        if (aString == null) {
            return aString;
        } else {
            return aString.replaceAll("\\D", "");
        }
    }

    /**
    Default constructor.
     */
    public StringUtils() {
    }
    
    public String reemplazarString(String cadena, String busqueda, String reemplazo) {
        return cadena.replaceAll(busqueda, reemplazo);
    }
    
    public String firstLetterToUpperCase(String _text){
        String mayuscula=_text.charAt(0)+""; 
        //Obtenemos el primer caracteres y concatenamos "" para que se transforme el char en String 
        mayuscula=mayuscula.toUpperCase();//Lue... lo transformamos en may�scula 
        _text=_text.replaceFirst 
        (_text.charAt(0)+"", mayuscula); 
        //Reemplazamos a la primera coicidencia con la may�scula 
        
        return _text;//Imprimimos para ver si resulto, que por su puesto 
    }
}
