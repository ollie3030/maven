package org.apache.maven.xml.sax.filter;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.function.Function;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Resolves all ci-friendly properties occurrences
 * 
 * @author Robert Scholte
 * @since 3.7.0
 */
class CiFriendlyXMLFilter
    extends AbstractEventXMLFilter
{
    private Function<String, String> replaceChain = Function.identity();
    
    private String characters; 
    
    private boolean parsing = false;

    @Override
    protected boolean isParsing()
    {
        return parsing;
    }

    @Override
    protected String getState()
    {
        return null;
    }
    
    public CiFriendlyXMLFilter setChangelist( String changelist )
    {
        replaceChain = replaceChain.andThen( t -> t.replace( "${changelist}", changelist ) );
        return this;
    }
    
    public CiFriendlyXMLFilter setRevision( String revision )
    {
        replaceChain = replaceChain.andThen( t -> t.replace( "${revision}", revision ) );
        return this;
    }

    public CiFriendlyXMLFilter setSha1( String sha1 )
    {
        replaceChain = replaceChain.andThen( t -> t.replace( "${sha1}", sha1 ) );
        return this;
    }
    
    /**
     * @return {@code true} is any of the ci properties is set, otherwise {@code false}
     */
    public boolean isSet()
    {
        return !replaceChain.equals( Function.identity() );
    }
    
    @Override
    public void characters( char[] ch, int start, int length )
        throws SAXException
    {
        this.parsing = true;
        this.characters = nullSafeAppend( characters, new String( ch, start, length ) );
    }

    @Override
    public void comment( char[] ch, int start, int length )
        throws SAXException
    {
        parseCharacters();
        super.comment( ch, start, length );
    }

    @Override
    public void endElement( String uri, String localName, String qName )
        throws SAXException
    {
        parseCharacters();
        super.endElement( uri, localName, qName );
    }
    
    @Override
    public void endCDATA()
        throws SAXException
    {
        parseCharacters();
        super.endCDATA();
    }

    @Override
    public void endDocument()
        throws SAXException
    {
        parseCharacters();
        super.endDocument();
    }

    @Override
    public void endDTD()
        throws SAXException
    {
        parseCharacters();
        super.endDTD();
    }
    
    @Override
    public void endEntity( String name )
        throws SAXException
    {
        parseCharacters();
        super.endEntity( name );
    }
    
    @Override
    public void endPrefixMapping( String prefix )
        throws SAXException
    {
        parseCharacters();
        super.endPrefixMapping( prefix );
    }

    @Override
    public void skippedEntity( String name )
        throws SAXException
    {
        parseCharacters();
        super.skippedEntity( name );
    }
    
    @Override
    public void startCDATA()
        throws SAXException
    {
        parseCharacters();
        super.startCDATA();
    }
    
    @Override
    public void ignorableWhitespace( char[] ch, int start, int length )
        throws SAXException
    {
        parseCharacters();
        super.ignorableWhitespace( ch, start, length );
    }
    
    @Override
    public void processingInstruction( String target, String data )
        throws SAXException
    {
        parseCharacters();
        super.processingInstruction( target, data );
    }
    
    @Override
    public void startDocument()
        throws SAXException
    {
        parseCharacters();
        super.startDocument();
    }
    
    @Override
    public void startDTD( String name, String publicId, String systemId )
        throws SAXException
    {
        parseCharacters();
        super.startDTD( name, publicId, systemId );
    }
    
    @Override
    public void startElement( String uri, String localName, String qName, Attributes atts )
        throws SAXException
    {
        parseCharacters();
        super.startElement( uri, localName, qName, atts );
    }
    
    @Override
    public void startEntity( String name )
        throws SAXException
    {
        parseCharacters();
        super.startEntity( name );
    }
    
    @Override
    public void startPrefixMapping( String prefix, String uri )
        throws SAXException
    {
        parseCharacters();
        super.startPrefixMapping( prefix, uri );
    }
    
    private void parseCharacters() throws SAXException
    {
        this.parsing = false;
        if ( characters == null )
        {
            return;
        }
        // assuming this has the best performance
        if ( characters.contains( "${" ) )
        {
            char[] ch = replaceChain.apply( characters ).toCharArray();
            super.characters( ch, 0, ch.length );
        }
        else
        {
            char[] ch = characters.toCharArray();
            super.characters( ch, 0, ch.length );
        }
        characters = null;
    }
    
}