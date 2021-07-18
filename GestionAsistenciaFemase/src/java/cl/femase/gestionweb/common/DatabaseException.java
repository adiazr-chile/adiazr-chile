package cl.femase.gestionweb.common;

/**
 * DOCUMENT ME!
 *
 * */
public class DatabaseException extends GeneralException {
    public static final int ERROR_SIN_CONTEXTO = 1;
    public static final int ERROR_DATASOURCE = 2;
    public static final int ERROR_CONEXION   = 3;

    static {
        errorSigla                         = "DB";
        errorDescs                         = new String[4];
        errorDescs[ERROR_SIN_CONTEXTO]     = "Contexto no disponible.";
        errorDescs[ERROR_DATASOURCE]       = "DataSource no disponible.";
        errorDescs[ERROR_CONEXION]         = "Problemas al obtener conexi�n.";
    }

    /**
     * Constructor for DataBaseException.
     *
     * @param error
     * @param s
     */
    public DatabaseException(int error, String s) {
        super(error, s);
    }
}
