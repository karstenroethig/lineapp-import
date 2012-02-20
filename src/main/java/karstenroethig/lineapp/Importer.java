package karstenroethig.lineapp;

import karstenroethig.lineapp.model.DatabaseConfiguration;
import karstenroethig.lineapp.model.Headline;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileFilter;

import java.sql.Date;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * DOCUMENT ME!
 *
 * @author   $Author$
 * @version  $Revision$, $Date$
 */
public class Importer {

    /**
     * @param  args
     */
    public static void main( String[] args ) {

        try {

            run( args );

        } catch( Exception ex ) {

            System.out.println(
                "Bei der Ausführung der Anwendung ist folgender Fehler aufgetreten:" );
            System.out.println( ex.getMessage() );

        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static void run( String[] args ) throws Exception {

        /*
         * Verzeichnis zum Suchen ermitteln
         */
        File searchDirectory = loadSearchDirectory( args );

        /*
         * Suche alle .eml-Dateien
         */
        Collection<File> headlineFiles = searchFiles( searchDirectory, ".eml" );

        System.out.println( "Gefundene Dateien: " + headlineFiles.size() );

        if( headlineFiles.isEmpty() ) {
            return;
        }

        /*
         * Daten aus gefundenen Dateien auslesen
         */
        List<Headline> headlines = new ArrayList<Headline>();
        Set<Long> keys = new HashSet<Long>();
        Date date = new Date( new java.util.Date().getTime() );

        for( File file : headlineFiles ) {

            try {
                Headline headline = Headline.load( file );

                if( headline != null ) {

                    if( keys.contains( headline.getOfferNumber() ) ) {
                        throw new Exception( "Die Headline mit dem Angebot " + headline.getOfferNumber()
                            + " existiert bereits." );
                    }

                    headline.setRecordingDate( date );
                    headline.setDateCreated( date );
                    headline.setLastUpdated( date );

                    headlines.add( headline );
                }

            } catch( Exception ex ) {
                System.out.println( "Fehler beim Auslesen der Datei " + file.getAbsolutePath() );
                System.out.println( "-> " + ex.getMessage() );
            }
        }

        System.out.println( "Erfolgreich konvertierte Headlines: " + headlines.size() );

        if( headlines.isEmpty() ) {
            return;
        }

        /*
         * Datenbank-Konfiguration laden
         */
        DatabaseConfiguration dbconfig = DatabaseConfiguration.load( "dbconfig.properties" );

        /*
         * Headlines in der Datenbank speichern
         */
        Headline.saveHeadlines( dbconfig, headlines );

        System.out.println( "Fertig." );
    }

    /**
     * DOCUMENT ME!
     *
     * @param   args  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    private static File loadSearchDirectory( String[] args ) throws Exception {

        if( ( args == null ) || ( args.length < 1 ) ) {
            System.out.println(
                "Es wurde kein Verzeichnis als Parameter angegeben, deshalb wird das aktuelle Verzeichnis durchsucht." );

            return new File( "." );
        }

        File dir = new File( args[0] );

        if( !dir.exists() ) {
            throw new Exception( "Das angegebene Verzeichnis existiert nicht (Pfad: " + args[0]
                + ")." );
        }

        if( !dir.isDirectory() ) {
            throw new Exception( "Beim angegebenen Pfad handelt es sich um kein Verzeichnis (Pfad: "
                + args[0] + ")." );
        }

        if( !dir.canRead() ) {
            throw new Exception( "In dem angegebenen Verzeichnis kann nicht gelesen werden (Pfad: "
                + args[0] + ")." );
        }

        return dir;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   searchDirectory  DOCUMENT ME!
     * @param   ending           DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    private static Collection<File> searchFiles( File searchDirectory, final String ending ) {

        List<File> files = new ArrayList<File>();

        File[] fileList = searchDirectory.listFiles( new FileFilter() {
                    public boolean accept( File file ) {

                        if( file.isDirectory() ) {
                            return true;
                        }

                        return StringUtils.endsWithIgnoreCase( file.getName(), ending );
                    }
                } );

        for( File file : fileList ) {

            if( file.isDirectory() ) {
                files.addAll( searchFiles( file, ending ) );
            } else {
                files.add( file );
            }

        }

        return files;
    }

}
