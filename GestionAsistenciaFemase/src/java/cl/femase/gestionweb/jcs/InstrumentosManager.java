package cl.femase.gestionweb.jcs;

import cl.femase.gestionweb.vo.ModuloSistemaVO;
import cl.femase.gestionweb.vo.PropertiesVO;

import org.apache.log4j.Logger;
import org.apache.jcs.JCS;

/**
 * Esta clase permite obtener el formato precio de cada instrumento, usando para ello
 * el Repositorio de instrumentos.
 * Para hacer mas rapida la respuesta al usuario, se usara
 * un cache con el formato precio de c/instrumento.
 */
public class InstrumentosManager {

    protected static Logger m_logger = Logger.getLogger("gestionweb");

    private static InstrumentosManager instance;
    private static int counter = 0;
    private static JCS instrumentoCache;
    private static PropertiesVO m_props;

    public InstrumentosManager(PropertiesVO _props) {
        System.out.println("Constructor InstrumentosManager");
        try {
            instrumentoCache = JCS.getInstance("instrumentoCache");
        } catch (Exception e) {
        	//Handle cache region initialization failure
        	System.err.println("Exception is: "+e.toString());
        }
        m_props = _props;
        m_logger.debug("Obteniendo instancia de RAPI...");
       
    }

    /**
     * Singleton access point to the manager.
     * @return 
     */
    public static InstrumentosManager getInstance() {
        System.out.println("InstrumentosManager.getInstance");
        synchronized (InstrumentosManager.class) {
            if (instance == null) {
                instance = new InstrumentosManager(m_props);
            }
        }

        synchronized (instance) {
            instance.counter++;
        }

        return instance;
    }


    /**
     * Retrieves a int indicando si existe el instrumento. Second argument decides whether to look
     * in the cache. Returns a new value object if one can't be
     * loaded from the database. Database cache synchronization is
     * handled by removing cache elements upon modification.
     * @param _nombreInstrumento
     * @param fromCache
     * @return 
     */
    public int existeInstrumento(String _nombreInstrumento,boolean fromCache) {
        ModuloSistemaVO vObj = null;
        int existe			= 0;
        try {
            // First, if requested, attempt to load from cache
            if (fromCache) {
//                m_logger.debug("InstrumentosManager.getInstrumento " +
//                		"from cache id:"+_nombreInstrumento);
                vObj = (ModuloSistemaVO) instrumentoCache.get(_nombreInstrumento);
            }

            // Either fromCache was false or the object was not found, so
            // call loadCorredoraBEC to create it

            if (vObj == null) {
                vObj = loadInstrumento(_nombreInstrumento);//desde el repositorio de instrumentos
            }
//            }else{
//            	System.out.println("InstrumentosManager.getInstrumento " +
//            			"[_nombreInstrumento]=[" + _nombreInstrumento + "] encontrado en cache!!");
//            }
            if (vObj != null){
            	existe = 1;
            }
        } catch (Exception ex) {
            System.err.println("Error : getInstrumento" + ex.toString());
            ex.printStackTrace();

        }
        return existe;
    }

    /**
     * Obtiene el formato precio de cada instrumento, usando para ello
     * el Repositorio de instrumentos.
     * @param _nombreInstrumento
     * @param fromCache
     * @return 
     */
    public String getFormatoPrecio(String _nombreInstrumento,boolean fromCache) {
        ModuloSistemaVO vObj = null;
        String formatoPrecio= "";
        try {
            // First, if requested, attempt to load from cache
            if (fromCache) {
//                System.out.println("InstrumentosManager.getFormatoPrecio " +
//                		"from cache id:"+_nombreInstrumento);
                vObj = (ModuloSistemaVO) instrumentoCache.get(_nombreInstrumento);
            }

            // Either fromCache was false or the object was not found, so
            // call loadCorredoraBEC to create it

            if (vObj == null) {
                System.out.println("InstrumentosManager.getFormatoPrecio. "
                        + "Rescatar formatoPrecio de "+_nombreInstrumento+" "
                        + "desde el repositorio de instrumentos");
                vObj = loadInstrumento(_nombreInstrumento);//desde el repositorio de instrumentos
            }
//            else{
//            	System.out.println("InstrumentosManager.getFormatoPrecio " +
//            			"[_nombreInstrumento]=[" + _nombreInstrumento + "] encontrado en cache!!");
//            }
            if (vObj != null){
            	formatoPrecio = "888-0";
            }
        } catch (Exception ex) {
            System.err.println("Error : getFormatoPrecio" + ex.toString());
            ex.printStackTrace();

        }
        return formatoPrecio;
    }

    /**
     * Creates a ModuloSistemaVO based on the id of the Instrumento.
     * Se conecta al repositorio de instrumentos para ello.
     * @param _nombreInstrumento
     * @return 
     */
    public ModuloSistemaVO loadInstrumento(String _nombreInstrumento) {
//        System.out.println("InstrumentosManager.loadInstrumento " +
//        		"[nomInstrumento]: ["+_nombreInstrumento+"]");

        ModuloSistemaVO dataNemo = new ModuloSistemaVO();
        
        try {
            boolean found = false;

            // load the data and set the rest of the fields
            // set found to true if it was found
//            try {
//               
//
//            } catch (Exception ee) {
//                System.out.println(new java.util.Date() + ": "
//                        + "InstrumentosManager.loadInstrumento:  " + ee.toString());
//            } finally {
//               
//            }

            // cache the value object if found
            if (found) {
                // could use the defaults like this
                // calendarioCache.put( "InstrumentoDTO" + id, vObj );
                // or specify special characteristics
                // put to cache
                storeInstrumento(dataNemo);
            }
        } catch (Exception e) {
            // Handle failure putting object to cache
            System.out.println(new java.util.Date() + ": InstrumentosManager.loadInstrumento: " +
                    "Error al agregar al cache de instrumento:" + e.toString());
        }

        return dataNemo;
    }

    /**
     * Create all DTOs from the instrument table and stores them in the cache.
     */
//    public void populateInstrumentos() {
//        System.out.println("InstrumentosManager.populateInstrumentos <START>");
//
//        DBConnectionManager connMgr;
//        connMgr = DBConnectionManager.getInstance();
//
//        InstrumentoDTO vObj = new InstrumentoDTO();
//        PreparedStatement callStm = null;
//        ResultSet rset = null;
//        Connection conn = null;
//
//        try {
//
//            // load the data and set the rest of the fields
//            // set found to true if it was found
//            try {
//
//                 String consulta = " SELECT NOM_INS,NOM_INS_VALIDO " +
//                 		"from OIB_TA_INSTRUMENTO ";
//
//                conn = connMgr.getConnection("ibbec");
//
//                callStm = conn.prepareStatement(consulta);
//                rset = callStm.executeQuery();
//                while (rset.next()) {
//                    vObj = new InstrumentoDTO();
//                    vObj.setNomIns(rset.getString("NOM_INS"));
//                    vObj.setNomInsValido(rset.getString("NOM_INS_VALIDO"));
//
//                    storeInstrumento(vObj);
//                }
//
//                rset.close();
//                callStm.close();
//
//            } catch (SQLException|DatabaseException e) {
//                System.out.println(new java.util.Date() + ": InstrumentosManager.populateInstrumentos: " +
//                		"Error al cerrar Statement o ResultSet: " + e.toString());
//                System.out.println(new java.util.Date() + ": InstrumentosManager.populateInstrumentos: " +
//                		"SQLException: " + e.toString());
//            } catch (Exception ee) {
//                System.out.println(new java.util.Date() + ": InstrumentosManager.populateInstrumentos: "
//                		+ ee.toString());
//            } finally {
//               connMgr.freeConnection("ibbec",conn);
//            }
//
//        } catch (Exception e) {
//            // Handle failure putting object to cache
//            System.out.println(new java.util.Date() + ": InstrumentosManager.populateInstrumentos: " +
//                    "Error al agregar al cache:" + e.toString());
//        }
//
//        System.out.println("InstrumentosManager.populateInstrumentos <STOP>");
//
//    }

    /**
     * Stores InstrumentoDTO's in database.  Clears old items and caches
     * new.
     * @param vObj
     * @return 
     */
    public int storeInstrumento(ModuloSistemaVO vObj) {
        try {
            // since any cached data is no longer valid, we should
            // remove the item from the cache if it an update.

            if (vObj.getModulo_nombre() != null) {
                instrumentoCache.remove(vObj.getModulo_id());
            }

            // put the new object in the cache
            instrumentoCache.put(vObj.getModulo_id(), vObj);
        } catch (Exception e) {
        	// Handle failure removing object or putting object to cache.
        	System.err.println("Exception en store: "+e.toString());
        	e.printStackTrace();
        }
        return vObj.getModulo_id();
    }
}



