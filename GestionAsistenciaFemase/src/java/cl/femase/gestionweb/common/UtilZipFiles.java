/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cl.femase.gestionweb.common;

/**
 *
 * @author Alexander
 */
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
* This utility compresses a list of files to standard ZIP format file.
* It is able to compresses all sub files and sub directories, recursively.
* @author Ha Minh Nam
*
*/
public class UtilZipFiles {
    public static String WEB_NAME = "[GestionFemaseWeb]";
    /**
     * A constants for buffer size used to read/write data
     */
    private static final int BUFFER_SIZE = 4096;

    /**
     * Compresses a collection of files to a destination zip file
     * @param listFiles A collection of files and directories
     * @param destZipFile The path of the destination zip file
     * @throws FileNotFoundException
     * @throws IOException
     */
   public static void compressFiles(List<File> listFiles, String destZipFile) throws FileNotFoundException, IOException {
       System.out.println(WEB_NAME+"[UtilZipFiles.compressFiles]"
               + "zip destino: "+destZipFile);

       ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFile));

       for (File file : listFiles) {
           if (file.isDirectory()) {
               addFolderToZip(file, file.getName(), zos);
           } else {
               addFileToZip(file, zos);
           }
       }

       if (zos!=null){
           zos.flush();
           zos.close();
       }
   }

   /**
    * Adds a directory to the current zip output stream
    * @param folder the directory to be  added
    * @param parentFolder the path of parent directory
    * @param zos the current zip output stream
    * @throws FileNotFoundException
    * @throws IOException
    */
    private static void addFolderToZip(File folder, String parentFolder,
            ZipOutputStream zos) throws FileNotFoundException, IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                addFolderToZip(file, parentFolder + "/" + file.getName(), zos);
                continue;
            }

            zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));

            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));

            long bytesRead = 0;
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read = 0;

            while ((read = bis.read(bytesIn)) != -1) {
                zos.write(bytesIn, 0, read);
                bytesRead += read;
            }

            zos.closeEntry();

        }
    }

    /**
     * Adds a file to the current zip output stream
     * @param file the file to be added
     * @param zos the current zip output stream
     * @throws FileNotFoundException
     * @throws IOException
     */
    private static void addFileToZip(File file, ZipOutputStream zos)
            throws FileNotFoundException, IOException {
         if (file.exists()){
            zos.putNextEntry(new ZipEntry(file.getName()));

            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                    file));

            long bytesRead = 0;
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read = 0;

            while ((read = bis.read(bytesIn)) != -1) {
                zos.write(bytesIn, 0, read);
                bytesRead += read;
            }

            zos.closeEntry();
         }
    }

//     public static void main(String[] args) throws IOException{
//        String zipfilename="D://Proyectos//BEC//ReportesPrestamos//store//readme4.zip";
//        List<File> lstFiles=new ArrayList<File>();
//        lstFiles.add(new File("D://Proyectos//BEC//ReportesPrestamos//store//readme1.txt"));
//        lstFiles.add(new File("D://Proyectos//BEC//ReportesPrestamos//store//readme2.txt"));
//
//        compressFiles(lstFiles, zipfilename);
//     }
}
