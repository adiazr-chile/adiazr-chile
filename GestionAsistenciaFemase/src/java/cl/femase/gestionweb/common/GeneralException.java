package cl.femase.gestionweb.common;

/**
 * DOCUMENT ME!
 *
 */
public abstract class GeneralException extends Exception {
    public static final int   SIN_ERROR  = 0;
    protected static String[] errorDescs;
    protected static String   errorSigla = "";
    protected int             errorNum   = 0;

    /**
     * DOCUMENT ME!
     *
     * @param error
     * @param s
     */
    public GeneralException(int error, String s) {
        super(getErrorDesc(error) + " \n\t " + s);
        this.errorNum = error;
        this.fillInStackTrace();
    }

    /**
    	 *
    	 *
    	 */
    public GeneralException() {
        super();
    }

    /**
     * DOCUMENT ME!
     *
     * @param error
     *
     * @return
     */
    protected static String getErrorDesc(int error) {
        String descr = "No definido.";

        if ((0 < error) && (error <= errorDescs.length)) {
            descr = errorDescs[error];
        }

        return "[" + errorSigla + "_" + error + "] " + descr;
    }

    /**
     * Returns the errorNum.
     *
     * @return int
     */
    public int getErrorNum() {
        return errorNum;
    }

    /**
     * DOCUMENT ME!
     *
     * @return
     */
    public String getErrorDesc() {
        return getErrorDesc(errorNum);
    }
}
