package karstenroethig.lineapp;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class Importer {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		
		File file = new File( "C:/temp/b_5aWw2w.doc.part" );

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

        for( String line : lines ) {
            System.out.println( line );
        }

        String location = StringUtils.EMPTY;
        String bundesland = StringUtils.EMPTY;
        String headline = StringUtils.EMPTY;
        String subHeadline = StringUtils.EMPTY;
        String body = StringUtils.EMPTY;
        String scene = StringUtils.EMPTY;
        List<String> scenes = new ArrayList<String>();

        System.out.println( "Location: \"" + location + "\"" );
        System.out.println( "Bundesland: \"" + bundesland + "\"" );
        System.out.println( "Headline: \"" + headline + "\"" );
        System.out.println( "Sub-Headline: \"" + subHeadline + "\"" );
        System.out.println( "Body: \"" + body + "\"" );
        System.out.println( "Angebot: \"" + angebot + "\"" );

        for( int i = 0; i < scenes.size(); i++ ) {
            String sc = scenes.get( i );

            System.out.println( "Scene " + ( i + 1 ) + ": \"" + sc + "\"" );
        }
	}

}
