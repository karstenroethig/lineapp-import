package karstenroethig.lineapp.model;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.util.Properties;


/**
 * DOCUMENT ME!
 *
 * @author   $Author$
 * @version  $Revision$, $Date$
 */
public class DatabaseConfiguration {

    /** DOCUMENT ME! */
    private String server;

    /** DOCUMENT ME! */
    private String port;

    /** DOCUMENT ME! */
    private String databaseName;

    /** DOCUMENT ME! */
    private String username;

    /** DOCUMENT ME! */
    private String password;

    /**
     * DOCUMENT ME!
     *
     * @param   pathToFile  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static DatabaseConfiguration load( String pathToFile ) throws Exception {

        if( StringUtils.isBlank( pathToFile ) ) {
            throw new Exception( "Kein Pfad zur Datenbank-Konfiguration angegeben." );
        }

        File file = new File( pathToFile );

        if( !file.exists() ) {
            throw new Exception( "Datei f¸r Datenbank-Konfiguration existiert nicht (Pfad: "
                + pathToFile + ")." );
        }

        if( !file.canRead() ) {
            throw new Exception(
                "Datei f¸r Datenbank-Konfiguration kann nicht gelesen werden (Pfad: " + pathToFile
                + ")." );
        }

        InputStream in = null;

        try {
            in = new FileInputStream( file );

            Properties props = new Properties();
            props.load( in );

            DatabaseConfiguration config = new DatabaseConfiguration();

            config.setServer( StringUtils.trim( props.getProperty( "server" ) ) );
            config.setPort( StringUtils.trim( props.getProperty( "port" ) ) );
            config.setDatabaseName( StringUtils.trim( props.getProperty( "databaseName" ) ) );
            config.setUsername( StringUtils.trim( props.getProperty( "username" ) ) );
            config.setPassword( StringUtils.trim( props.getProperty( "password" ) ) );

            String errorMsg = config.validate();

            if( errorMsg != null ) {
                throw new Exception( "Datenbank-Konfiguration unvollst‰ndig: " + errorMsg );
            }

            return config;

        } finally {

            if( in != null ) {

                try {
                    in.close();
                } catch( Exception ex ) {
                    // Nothing to do
                }
            }
        }

    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String validate() {

        if( StringUtils.isBlank( getServer() ) ) {
            return "Es wurde kein Datenbank-Server angegeben.";
        }

        if( StringUtils.isNotBlank( getPort() ) ) {

            if( !StringUtils.isNumeric( getPort() ) ) {
                return "Der angegebene Port ist nicht numerisch.";
            }

            if( getPort().length() > 5 ) {
                return "Der angegebene Port ist zu groﬂ.";
            }
        }

        if( StringUtils.isBlank( getDatabaseName() ) ) {
            return "Es wurde keine Datenbank angegeben.";
        }

        if( StringUtils.isBlank( getUsername() ) ) {
            return "Es wurde kein Benutzername angegeben.";
        }

        if( StringUtils.isBlank( getPassword() ) ) {
            return "Es wurde kein Kennwort angegeben.";
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getUrl() {
        StringBuffer url = new StringBuffer();

        url.append( "jdbc:mysql://" );
        url.append( getServer() );

        if( StringUtils.isNotBlank( getPort() ) ) {
            url.append( ":" );
            url.append( getPort() );
        }

        url.append( "/" );
        url.append( getDatabaseName() );
        url.append( "?user=" );
        url.append( getUsername() );
        url.append( "&password=" );
        url.append( getPassword() );
        url.append( "&useUnicode=yes&characterEncoding=UTF-8" );

        return url.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  databaseName  DOCUMENT ME!
     */
    public void setDatabaseName( String databaseName ) {
        this.databaseName = databaseName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getPassword() {
        return password;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  password  DOCUMENT ME!
     */
    public void setPassword( String password ) {
        this.password = password;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getPort() {
        return port;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  port  DOCUMENT ME!
     */
    public void setPort( String port ) {
        this.port = port;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getServer() {
        return server;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  server  DOCUMENT ME!
     */
    public void setServer( String server ) {
        this.server = server;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getUsername() {
        return username;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  username  DOCUMENT ME!
     */
    public void setUsername( String username ) {
        this.username = username;
    }

}
