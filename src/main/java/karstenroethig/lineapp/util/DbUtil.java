package karstenroethig.lineapp.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * DOCUMENT ME!
 *
 * @author   $Author$
 * @version  $Revision$, $Date$
 */
public class DbUtil {

    /**
     * Schlieﬂt die Connection und f‰ngt eventuell auftretende {@link SQLException} ab.
     *
     * @param  conn  Verbindung, die geschlossen werden soll.
     */
    public static void closeQuietly( Connection conn ) {
        closeQuietly( conn, null, null );
    }

    /**
     * Schlieﬂt nacheinander Connection, Statement und ResultSet und f‰ngt eventuell auftretende
     * {@link SQLException} ab.
     *
     * @param  conn  Verbindung, die geschlossen werden soll.
     * @param  stm   Statement, das geschlossen werden soll.
     * @param  rs    ResultSet, das geschlossen werden soll.
     */
    public static void closeQuietly( Connection conn, Statement stm, ResultSet rs ) {

        try {

            if( rs != null ) {
                rs.close();
            }
        } catch( SQLException ex ) {
            // Nothing to do
        }

        try {

            if( stm != null ) {
                stm.close();
            }
        } catch( SQLException ex ) {
            // Nothing to do
        }

        try {

            if( conn != null ) {
                conn.close();
            }
        } catch( SQLException ex ) {
            // Nothing to do
        }
    }

}
