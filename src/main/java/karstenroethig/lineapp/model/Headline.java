package karstenroethig.lineapp.model;

import karstenroethig.lineapp.model.enums.FederalLand;

import karstenroethig.lineapp.util.DbUtil;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.RandomAccessFile;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * DOCUMENT ME!
 *
 * @author   $Author$
 * @version  $Revision$, $Date$
 */
public class Headline {

    /** DOCUMENT ME! */
    private String subject;

    /** DOCUMENT ME! */
    private String subHeadline;

    /** DOCUMENT ME! */
    private String body;

    /** DOCUMENT ME! */
    private FederalLand federalLand;

    /** DOCUMENT ME! */
    private String location;

    /** DOCUMENT ME! */
    private Date recordingDate;

    /** DOCUMENT ME! */
    private String comment;

    /** DOCUMENT ME! */
    private Long offerNumber;

    /** DOCUMENT ME! */
    private Date dateCreated;

    /** DOCUMENT ME! */
    private Date lastUpdated;

    /** DOCUMENT ME! */
    private List<Scene> scenes;

    /**
     * DOCUMENT ME!
     *
     * @param   file  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static Headline load( File file ) throws Exception {

        RandomAccessFile raf = new RandomAccessFile( file, "r" );

        List<String> lines = new ArrayList<String>();
        boolean startRead = false;
        String part = StringUtils.EMPTY;

        String angebot = StringUtils.EMPTY;

        for( String line; ( line = raf.readLine() ) != null; ) {
            line = StringUtils.trim( line );

            if( !startRead ) {

                if( StringUtils.startsWith( line, "<body" ) ) {
                    startRead = true;
                }
            } else {

                if( StringUtils.equals( line, "<br>" ) ) {

                    if( StringUtils.isNotBlank( part ) ) {
                        lines.add( StringUtils.trim( StringUtils.replace( part, "<br>", "\n" ) ) );
                        part = StringUtils.EMPTY;
                    }
                } else {

                    if( StringUtils.isNotBlank( part ) ) {
                        part += " ";
                    }

                    part += line;
                }

                if( StringUtils.startsWith( line, "Angebot" )
                        && StringUtils.endsWith( line, "<br>" ) && ( line.length() == 17 ) ) {
                    angebot = StringUtils.remove( StringUtils.remove( part, "Angebot " ), "<br>" );

                    break;
                }
            }
        }

        raf.close();

        if( StringUtils.isBlank( angebot ) ) {
            throw new Exception( "Keine Angebotsnummer gefunden." );
        }

        Headline headline = new Headline();

        try {
            Long key = Long.parseLong( angebot.trim() );
            headline.setOfferNumber( key );
        } catch( Exception ex ) {
            throw new Exception( "Die Angebotsnummer " + angebot
                + " konnte nicht umgewandelt werden." );
        }

        boolean scenesOnly = false;
        boolean hasHeadline = false;

        for( String line : lines ) {

            // Szenen
            if( scenesOnly ) {
                headline.addScene( line );

                continue;
            } else if( StringUtils.equals( line.trim(), "Bilder:" ) ) {
                scenesOnly = true;

                continue;
            }

            // Alles andere vor den Szenen
            if( StringUtils.startsWith( line, "<b>" ) ) {

                // Location und Bundesland
                String[] split = StringUtils.split( line, '/' );

                // Location
                headline.setLocation( StringUtils.trim( StringUtils.remove( split[0], "<b>" ) ) );

                // Bundesland
                if( split.length > 1 ) {
                    FederalLand federalLand = FederalLand.findByFullname( split[1] );

                    if( StringUtils.isNotBlank( split[1] )
                            && ( federalLand == FederalLand.EMPTY ) ) {
                        throw new Exception( "Das angegebene Bundesland \""
                            + StringUtils.trim( split[1] ) + "\" konnte nicht zugeordnet werden." );
                    }

                    headline.setFederalLand( federalLand );
                } else {
                    headline.setFederalLand( FederalLand.EMPTY );
                }

            } else if( StringUtils.endsWith( line, "</b>" ) ) {

                // Headline oder Sub-Headline
                if( hasHeadline ) {

                    // Sub-Headline
                    headline.setSubHeadline( StringUtils.removeEnd( line, "</b>" ) );

                } else {

                    // Headline
                    headline.setSubject( StringUtils.removeEnd( line, "</b>" ) );
                    hasHeadline = true;
                }

            } else if( hasHeadline ) {

                // Body
                headline.setBody( line );

            } else {

                // Headline
                headline.setSubject( line );
                hasHeadline = true;
            }

        }

        return headline;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   dbconfig   DOCUMENT ME!
     * @param   headlines  DOCUMENT ME!
     *
     * @throws  Exception  DOCUMENT ME!
     */
    public static void saveHeadlines( DatabaseConfiguration dbconfig,
        Collection<Headline> headlines ) throws Exception {

        if( ( dbconfig == null ) || ( headlines == null ) || headlines.isEmpty() ) {
            return;
        }

        try {

            Class.forName( "com.mysql.jdbc.Driver" ).newInstance();

        } catch( Exception ex ) {
            throw new Exception( "Der JDBC-Treiber konnte nicht gefunden werden." );
        }

        Connection conn = null;

        try {

            conn = DriverManager.getConnection( dbconfig.getUrl() );

            // ID für Administrator ermitteln
            Long adminId = findAdminId( conn );

            if( adminId == null ) {
                throw new Exception(
                    "Der Benutzer 'admin' konnte nicht in der Datenbank gefunden werden." );
            }

            for( Headline headline : headlines ) {
                headline.save( conn, adminId );
            }

        } finally {
            DbUtil.closeQuietly( conn );
        }

    }

    /**
     * DOCUMENT ME!
     *
     * @param   conn  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     *
     * @throws  SQLException  DOCUMENT ME!
     */
    public static Long findAdminId( Connection conn ) throws SQLException {

        if( conn == null ) {
            return null;
        }

        Statement stm = null;
        ResultSet rs = null;

        try {

            stm = conn.createStatement();
            rs = stm.executeQuery( "SELECT id FROM user WHERE username = 'admin'" );

            if( rs != null ) {

                while( rs.next() ) {
                    return rs.getLong( "id" );
                }
            }

        } finally {
            DbUtil.closeQuietly( null, stm, rs );
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  conn     DOCUMENT ME!
     * @param  adminId  DOCUMENT ME!
     */
    public void save( Connection conn, Long adminId ) {

        PreparedStatement stm = null;
        ResultSet rs = null;

        try {

            /*
             * Einfügen der Headline
             */
            StringBuffer sql = new StringBuffer();
            sql.append( "INSERT INTO headline ( " );
            sql.append( "       subject, sub_headline, body, federal_land, " );
            sql.append( "       location, recording_date, comment, status, " );
            sql.append( "       offer_number, date_created, last_updated, " );
            sql.append( "       author_id, version ) " );
            sql.append( "       VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? );" );

            stm = conn.prepareStatement( sql.toString() );
            stm.setString( 1, getSubject() );
            stm.setString( 2, getSubHeadline() );
            stm.setString( 3, getBody() );
            stm.setString( 4, getFederalLand().name() );
            stm.setString( 5, getLocation() );
            stm.setDate( 6, getRecordingDate() );
            stm.setString( 7, "automatisiert hinzugefügt mit lineapp-import" );
            stm.setString( 8, "PUBLISHED" );
            stm.setLong( 9, getOfferNumber() );
            stm.setDate( 10, getDateCreated() );
            stm.setDate( 11, getLastUpdated() );
            stm.setLong( 12, adminId );
            stm.setInt( 13, 1 );

            stm.executeUpdate();

            if( getScenes().isEmpty() ) {
                return;
            }

            DbUtil.closeQuietly( null, stm, null );

            /*
             * ID der neuen Headline ermitteln
             */
            Long headlineId = null;

            sql = new StringBuffer();
            sql.append( "SELECT id FROM headline WHERE offer_number = ?;" );

            stm = conn.prepareStatement( sql.toString() );
            
            stm.setLong( 1, getOfferNumber() );
            
            rs = stm.executeQuery();

            if( rs != null ) {

                while( rs.next() ) {
                    headlineId = rs.getLong( "id" );
                }
            }

            if( headlineId == null ) {
                System.out.println(
                    "Die gespeicherte Headline konnte nicht in der Datenbank gefunden werden (Angebot: "
                    + getOfferNumber() + ")." );
            }

            DbUtil.closeQuietly( null, stm, rs );

            /*
             * Szenen hinzufügen
             */
            for( Scene scene : getScenes() ) {

                sql = new StringBuffer();
                sql.append( "INSERT INTO scene ( " );
                sql.append( "       body, sequence, headline_id, version ) " );
                sql.append( "       VALUES ( ?, ?, ?, ? );" );

                stm = conn.prepareStatement( sql.toString() );
                stm.setString( 1, scene.getBody() );
                stm.setInt( 2, 0 );
                stm.setLong( 3, headlineId );
                stm.setInt( 4, 1 );

                stm.executeUpdate();

                DbUtil.closeQuietly( null, stm, null );
            }

        } catch( SQLException ex ) {
            System.out.println( "Fehler beim Speichern der Headline " + getOfferNumber() + " ("
                + getSubject() + ") -> " + ex.getMessage() );
        } finally {
            DbUtil.closeQuietly( null, stm, rs );
        }

    }

    /**
     * DOCUMENT ME!
     *
     * @param  sceneStr  DOCUMENT ME!
     */
    public void addScene( String sceneStr ) {

        List<Scene> scenes = getScenes();

        Scene scene = new Scene();
        scene.setBody( sceneStr );
        scene.setSequence( scenes.size() + 1 );

        scenes.add( scene );
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getBody() {
        return body;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  body  DOCUMENT ME!
     */
    public void setBody( String body ) {
        this.body = body;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getComment() {
        return comment;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  comment  DOCUMENT ME!
     */
    public void setComment( String comment ) {
        this.comment = comment;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getDateCreated() {
        return dateCreated;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  dateCreated  DOCUMENT ME!
     */
    public void setDateCreated( Date dateCreated ) {
        this.dateCreated = dateCreated;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public FederalLand getFederalLand() {
        return federalLand;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  federalLand  DOCUMENT ME!
     */
    public void setFederalLand( FederalLand federalLand ) {
        this.federalLand = federalLand;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Long getOfferNumber() {
        return offerNumber;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  offerNumber  DOCUMENT ME!
     */
    public void setOfferNumber( Long offerNumber ) {
        this.offerNumber = offerNumber;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getLastUpdated() {
        return lastUpdated;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  lastUpdated  DOCUMENT ME!
     */
    public void setLastUpdated( Date lastUpdated ) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getLocation() {
        return location;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  location  DOCUMENT ME!
     */
    public void setLocation( String location ) {
        this.location = location;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public Date getRecordingDate() {
        return recordingDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  recordingDate  DOCUMENT ME!
     */
    public void setRecordingDate( Date recordingDate ) {
        this.recordingDate = recordingDate;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public List<Scene> getScenes() {

        if( scenes == null ) {
            scenes = new ArrayList<Scene>( 0 );
        }

        return scenes;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  scenes  DOCUMENT ME!
     */
    public void setScenes( List<Scene> scenes ) {
        this.scenes = scenes;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getSubHeadline() {
        return subHeadline;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  subHeadline  DOCUMENT ME!
     */
    public void setSubHeadline( String subHeadline ) {
        this.subHeadline = subHeadline;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getSubject() {
        return subject;
    }

    /**
     * DOCUMENT ME!
     *
     * @param  subject  DOCUMENT ME!
     */
    public void setSubject( String subject ) {
        this.subject = subject;
    }
}
