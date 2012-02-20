package karstenroethig.lineapp.model.enums;

import org.apache.commons.lang.StringUtils;


/**
 * DOCUMENT ME!
 *
 * @author   $author$
 * @version  $Revision$, $Date$
 */
public enum FederalLand {

    EMPTY( StringUtils.EMPTY ),

    BW( "Baden-Württemberg" ),

    BY( "Bayern" ),

    BE( "Berlin" ),

    BB( "Brandenburg" ),

    HB( "Bremen" ),

    HH( "Hamburg" ),

    HE( "Hessen" ),

    MV( "Mecklenburg-Vorpommern" ),

    NI( "Niedersachsen" ),

    NW( "Nordrhein-Westfalen" ),

    RP( "Rheinland-Pfalz" ),

    SL( "Saarland" ),

    SN( "Sachsen" ),

    ST( "Sachsen-Anhalt" ),

    SH( "Schleswig-Holstein" ),

    TH( "Thüringen" );

    /** DOCUMENT ME! */
    private String fullname;

    /**
     * Creates a new FederalLand object.
     *
     * @param  fullname  DOCUMENT ME!
     */
    private FederalLand( String fullname ) {
        this.fullname = fullname;
    }

    /**
     * DOCUMENT ME!
     *
     * @param   fullname  DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public static FederalLand findByFullname( String fullname ) {

        for( FederalLand federalLand : values() ) {

            if( StringUtils.equalsIgnoreCase( federalLand.getFullname(),
                        StringUtils.trim( fullname ) ) ) {
                return federalLand;
            }
        }

        return EMPTY;
    }

    /**
     * DOCUMENT ME!
     *
     * @return  DOCUMENT ME!
     */
    public String getFullname() {
        return fullname;
    }
}
