package com.bng.core.utils;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;
/**
 * <p>Description: This class parse a string and also encode it if <i>quoted-printable</i>.</p>
 * @author $Author: Agrim $
 * @version $Revision: 1.5 $ $Date: {@link SimpleParser}$
 *
 * $Log: SimpleParser.java,v $
 * Revision 1.0  2  Agrim
 * *** empty log message ***
 *
 * 
 */

public class SimpleParser {
    private static String []asLogicalOperators = {  "ex",
                                                    "EX",
                                                    "!ex",
                                                    "!EX",
                                                    "==",
                                                    "=i",
                                                    "!==",
                                                    "!=i",
                                                    ">>",
                                                    "gt",
                                                    "GT",
                                                    ">=",
                                                    "ge",
                                                    "GE",
                                                    "<<",
                                                    "lt",
                                                    "LT",
                                                    "<=",
                                                    "le",
                                                    "LE",
                                                    "cc",
                                                    "!cc",
                                                    "CC",
                                                    "!CC",
                                                    "sw",
                                                    "!sw",
                                                    "SW",
                                                    "!SW",
                                                    "ew",
                                                    "!ew",
                                                    "EW",
                                                    "!EW",
                                                    "in",
                                                    "!in",
                                                    "IN",
                                                    "!IN",
                                                    "mod"
                                                    };
    private final static int EXIST                   =  0;
    private final static int EXIST_I                 =  1;
    private final static int NOT_EXIST               =  2;
    private final static int NOT_EXIST_I             =  3;
    private final static int EQUAL                   =  4;
    private final static int EQUAL_I                 =  5;
    private final static int DIFFERENT               =  6;
    private final static int DIFFERENT_I             =  7;
    private final static int GREATER_NUMBERS         =  8;
    private final static int GREATER_STRINGS         =  9;
    private final static int GREATER_STRINGS_I       = 10;
    private final static int GREATER_EQUAL_NUMBERS   = 11;
    private final static int GREATER_EQUAL_STRINGS   = 12;
    private final static int GREATER_EQUAL_STRINGS_I = 13;
    private final static int LESS_NUMBERS            = 14;
    private final static int LESS_STRINGS            = 15;
    private final static int LESS_STRINGS_I          = 16;
    private final static int LESS_EQUAL_NUMBERS      = 17;
    private final static int LESS_EQUAL_STRINGS      = 18;
    private final static int LESS_EQUAL_STRINGS_I    = 19;
    private final static int CONTAINS                = 20;
    private final static int NOT_CONTAINS            = 21;
    private final static int CONTAINS_I              = 22;
    private final static int NOT_CONTAINS_I          = 23;
    private final static int STARTS_WITH             = 24;
    private final static int NOT_STARTS_WITH         = 25;
    private final static int STARTS_WITH_I           = 26;
    private final static int NOT_STARTS_WITH_I       = 27;
    private final static int ENDS_WITH               = 28;
    private final static int NOT_ENDS_WITH           = 29;
    private final static int ENDS_WITH_I             = 30;
    private final static int NOT_ENDS_WITH_I         = 31;
    private final static int IN                      = 32;
    private final static int NOT_IN                  = 33;
    private final static int IN_I                    = 34;
    private final static int NOT_IN_I                = 35;
    private final static int MOD                     = 36;

    private final static String tofind = "\r\nContent-Transfer-Encoding: quoted-printable";

    private Vector vTokens = null;
    private String szMySource = null;
    private Properties pVariables = new Properties();
    private boolean bStatic;
    private boolean bQuoted = false;
    private boolean autoQuote = true;

/**
 * Init message structure.
 * @param source the source string
 * @throws Exception
 */
    public SimpleParser(String source) throws Exception {
        this(source,false,true);
    }

/**
 * Init message structure.
 * @param source the source string
 * @param isStatic <i>true</i> if static message, <i>false</i> otherwise
 * @throws Exception
 */
    public SimpleParser(String source, boolean isStatic) throws Exception {
        this(source,isStatic,true);
    }
/**
 * Init message structure.
 * @param source the source string
 * @param isStatic <i>true</i> if static message, <i>false</i> otherwise
 * @param autoQuote <i>true</i> if automatic quote message (default) <i>false</i> otherwise
 * @throws Exception
 */
    public SimpleParser(String source, boolean isStatic, boolean autoQuote) throws Exception
    {
        String szToken = null;
        String szTag = null;
        int iStatus = 0; // [0]reset; [1]last token was a $; [2]last token was a tag

        // check if text has to be encoded
        this.autoQuote = autoQuote;
        int posQuoted = source.indexOf(tofind);
        bQuoted = (posQuoted > -1);

        // counstruct a new StringTokenizer from the szSource text
        StringTokenizer stText = new StringTokenizer(source, "$", true); // use '$' as separator, and return it as a token
        if (isStatic || stText.countTokens() < 2)
        {
            bStatic = true;
            int pos1;

            // no quoted-printable
            if (!bQuoted || !autoQuote)
            {
                szMySource = source;
            }
            else if ((pos1=source.substring(0,posQuoted).lastIndexOf("\r\n\r\n")) == -1)
            {
                // encode static message once: not multipart
                pos1 = source.indexOf("\r\n\r\n",posQuoted);
                szMySource = source.substring(0,pos1+4) + getQuotedPrintableText(source.substring(pos1+4));
            }
            else
            {
                // encode static message once: multipart then get boundary name
                final String boundaryName = source.substring(pos1,source.indexOf("\r\n",pos1+4));
                do {
                    pos1 = source.indexOf("\r\n\r\n",posQuoted);
                    final int pos2 = source.indexOf(boundaryName,pos1);
                     if (pos2 == -1)
                        source = source.substring(0,pos1+4) + getQuotedPrintableText(source.substring(pos1+4));
                    else if (pos2 > pos1+4)
                        source = source.substring(0,pos1+4) + getQuotedPrintableText(source.substring(pos1+4,pos2)) + source.substring(pos2);
                } while ((posQuoted=source.indexOf(tofind,posQuoted+2)) > -1);
                szMySource = source;
            }
        }
        else
        {
            bStatic = false;
            // construct the vTokens vector
            vTokens = new Vector();
            while (stText.hasMoreTokens())
            {
                szToken = stText.nextToken();
                if (szToken.charAt(0) == '$')
                {   // this is a separator
                    switch (iStatus)
                    {
                        case 0: // the last token was a normal string or default/start
                            iStatus = 1;
                            break;

                        case 1: // the last token was a $ separator; add a "$" string to the vector
                            vTokens.addElement("$");
                            iStatus = 0;
                            break;

                        case 2: // the last token was a tag content; add an empty element and the tag content to the vector
                            vTokens.addElement(null);
                            vTokens.addElement(szTag);
                            iStatus = 0;
                            break;
                    } // switch
                }
                else
                {   // this isn't a separator
                    switch (iStatus)
                    {
                        case 0: // the last token was a normal string or default/start
                            vTokens.addElement(szToken);
                            break;

                        case 1: // the last token was a $ separator; this is a tag content
                            szTag = szToken;
                            iStatus = 2;
                            if (szTag.length() > 0)
                                addVariableName(szTag);
                            break;

                        case 2: // the last token was a tag content (this should never happen)
                            iStatus = 0;
                            break;
                    } // switch
                }
            } // while (stText.hasMoreTokens())

            // tag $ not closed: odd tag ???
            if (iStatus != 0)
                throw new Exception("Bad source: odd '$' tags ???");
        }
    } // SimpleParser()

/**
 * Check for static message i.e. without <b>$</b> char.
 * @return <i>true</i> if static message, <i>false</i> otherwise
 */
    public boolean isStatic() {
            return (bStatic);
    }

/**
 * Get all dynamic variables in the messages.
 * @return Properties containing all dynamic variables
 */
    public Properties getAllVariables() {
            if (!pVariables.isEmpty())
                pVariables.setProperty("jfmt","true");
            return pVariables;
    }

/**
 * Parse message giving a property table.
 * @param p the property table
 * @return parsed message
 */
    public String parse(Properties p) {
        String szToken;
        String szName;
        String szValue;
        String szModifier;
        StringBuffer sbText;
        Stack stackShowComment = new Stack();
        int iCommentFlag = 0;

        if (bStatic)
        {   // static text
            return(szMySource);
        }
        else
        {   // dynamic text
            sbText = new StringBuffer();
            for (int iCount = 0; iCount < vTokens.size(); iCount++)
            {
                try
                {
                szToken = (String)vTokens.elementAt(iCount);
                if (szToken == null)
                {   // next token is a tag content
                    szToken = (String)vTokens.elementAt(++iCount);
                    // translate tag
                    if (szToken.charAt(0) == '(')
                    {   // there is a tag modifier
                        int iPointer = szToken.indexOf(')');
                        szModifier = szToken.substring(1, iPointer);
                        if (szModifier.equalsIgnoreCase("!comment"))
                        {   // look for the following name; if it is found in the properties, set the comment flag to true
                                szValue = p.getProperty(szToken.substring(iPointer + 1));
                                if( ConditionMatched( szToken.substring(iPointer + 1), p ) )
                                {
                                    stackShowComment.push( new Boolean( true ) );
                                    iCommentFlag += 1;
                                }
                                else
                                    stackShowComment.push( new Boolean( false ) );
                        }
                        else if (szModifier.equalsIgnoreCase("!comment-end"))
                        {
                            if( ((Boolean)stackShowComment.pop()).booleanValue() )
                                iCommentFlag = iCommentFlag > 0 ? iCommentFlag - 1 : 0;
                        }
                        else if (szModifier.equalsIgnoreCase("!show"))
                        {   // look for the following name; if it is found in the properties, set the comment flag to false
                                if( !ConditionMatched( szToken.substring(iPointer + 1), p ) )
                                {
                                    stackShowComment.push( new Boolean( true ) );
                                    iCommentFlag += 1;
                                }
                                else
                                    stackShowComment.push( new Boolean( false ) );
                        }
                        else if (szModifier.equalsIgnoreCase("!show-end"))
                        {
                            if( ((Boolean)stackShowComment.pop()).booleanValue() )
                                iCommentFlag = iCommentFlag > 0 ? iCommentFlag - 1 : 0;
                        }
                    }
                    else
                    {   // no modifiers
                        if (iCommentFlag == 0)
                        {
                            szValue = p.getProperty(szToken);
                            if (szValue != null)
                            {
                                sbText.append(szValue);
                            }
                        }
                    }
                }
                else
                {   // this is a text token
                        if (iCommentFlag == 0)
                        {
                            sbText.append(szToken);
                        }
                } // if ... else
            }
            catch (Exception e) {}
            } // for

            // no quoted-printable
            if (!bQuoted || !autoQuote)
                return(sbText.toString());

            String szSource = sbText.toString();
            int posQuoted = szSource.indexOf(tofind);
            int pos1;

            if ((pos1=szSource.substring(0,posQuoted).lastIndexOf("\r\n\r\n")) == -1)
            {
                // encode dynamic message: not multipart
                pos1 = szSource.indexOf("\r\n\r\n",posQuoted);
                return (szSource.substring(0,pos1+4) + getQuotedPrintableText(szSource.substring(pos1+4)));
            }
            else
            {
                // encode dynamic message: multipart then get boundary name
                final String boundaryName = szSource.substring(pos1,szSource.indexOf("\r\n",pos1+4));
                do {
                    pos1 = szSource.indexOf("\r\n\r\n",posQuoted);
                    final int pos2 = szSource.indexOf(boundaryName,pos1);
                    if (pos2 == -1)
                        szSource = szSource.substring(0,pos1+4) + getQuotedPrintableText(szSource.substring(pos1+4));
                    else if (pos2 > pos1+4)
                        szSource = szSource.substring(0,pos1+4) + getQuotedPrintableText(szSource.substring(pos1+4,pos2)) + szSource.substring(pos2);
                } while ((posQuoted=szSource.indexOf(tofind,posQuoted+2)) > -1);
                return szSource;
            }
        }
    }//parse



    private boolean ConditionMatched( String szCondition, Properties Pr ) throws Exception
    {
        int iLogicalOperatorPos = -1;
        int iLogicalOperatorFound = EXIST;
        boolean bUnaryOperator = false;
        String szVariable;
        String szVariableValue = null;
        String szValue = null;
        if( szCondition == null )
            return false;
        szCondition = szCondition.trim();
        szVariable = szCondition;
        for( int i = 0; i < asLogicalOperators.length; i++ )
        {
            switch( i )
            {
                case EXIST:
                case EXIST_I:
                case NOT_EXIST:
                case NOT_EXIST_I:
                     iLogicalOperatorPos = szCondition.indexOf( " "+asLogicalOperators[i] );
                     bUnaryOperator = true;
                     break;
                default:
                    iLogicalOperatorPos = szCondition.indexOf( " "+asLogicalOperators[i]+" " );
                    bUnaryOperator = false;
            }
            if( iLogicalOperatorPos != -1 )
            {
                iLogicalOperatorFound = i;
                szVariable = szCondition.substring( 0, iLogicalOperatorPos ).trim();
                if( !bUnaryOperator )
                {
                    if( szCondition.length() <= iLogicalOperatorPos+asLogicalOperators[iLogicalOperatorFound].length()+2 )
                    {
                        Util.sysLog( 3, this.getClass().getName()+".MatchCondition", "Binary Operator has not parameter" );
                        throw new Exception( "Error in SimpleParser.MatchCondition: Binary Operator has not parameter" );
                    }
                    szValue = szCondition.substring( iLogicalOperatorPos+asLogicalOperators[iLogicalOperatorFound].length()+2 ).trim();
                    if( ( szValue.length() > 0 ) && ( szValue.charAt( 0 ) == '"' ) )
                    {
                        szValue = szValue.substring( 1 );
                        if( ( szValue.length() > 0 ) && ( szValue.charAt( szValue.length()-1 ) == '"' ) )
                            szValue = szValue.substring( 0, szValue.length()-1 );
                    }
                }
                break;
            }// End if( iLogicalOperatorPos != -1 )
        }// End FOR
        switch( iLogicalOperatorFound )
        {
            case EXIST:
                 return Pr.getProperty( szVariable ) != null;
            case EXIST_I:
                 return Pr.getProperty( getKeyNameInProperties( szVariable, Pr ) ) != null;
            case NOT_EXIST:
                 return Pr.getProperty( szVariable ) == null;
            case NOT_EXIST_I:
                 return Pr.getProperty( getKeyNameInProperties( szVariable, Pr ) ) == null;
            default:
                 szVariableValue = Pr.getProperty( szVariable );
                 if( szVariableValue == null )
                     return false;
                switch( iLogicalOperatorFound )
                {
                    case EQUAL:
                         return szVariableValue.equals( szValue );
                    case EQUAL_I:
                         return szVariableValue.equalsIgnoreCase( szValue );
                    case DIFFERENT:
                         return !szVariableValue.equals( szValue );
                    case DIFFERENT_I:
                         return !szVariableValue.equalsIgnoreCase( szValue );
                    case GREATER_NUMBERS:
                         return Integer.parseInt( szVariableValue ) > Integer.parseInt( szValue );
                    case GREATER_STRINGS:
                         return szVariableValue.compareTo( szValue ) > 0;
                    case GREATER_STRINGS_I:
                         return szVariableValue.compareToIgnoreCase( szValue ) > 0;
                    case GREATER_EQUAL_NUMBERS:
                         return Integer.parseInt( szVariableValue ) >= Integer.parseInt( szValue );
                    case GREATER_EQUAL_STRINGS:
                         return szVariableValue.compareTo( szValue ) >= 0;
                    case GREATER_EQUAL_STRINGS_I:
                         return szVariableValue.compareToIgnoreCase( szValue ) >= 0;
                    case LESS_NUMBERS:
                         return Integer.parseInt( szVariableValue ) < Integer.parseInt( szValue );
                    case LESS_STRINGS:
                         return szVariableValue.compareTo( szValue ) < 0;
                    case LESS_STRINGS_I:
                         return szVariableValue.compareToIgnoreCase( szValue ) < 0;
                    case LESS_EQUAL_NUMBERS:
                         return Integer.parseInt( szVariableValue ) <= Integer.parseInt( szValue );
                    case LESS_EQUAL_STRINGS:
                         return szVariableValue.compareTo( szValue ) <= 0;
                    case LESS_EQUAL_STRINGS_I:
                         return szVariableValue.compareToIgnoreCase( szValue ) <= 0;
                    case CONTAINS:
                         return szVariableValue.indexOf( szValue ) != -1;
                    case NOT_CONTAINS:
                         return szVariableValue.indexOf( szValue ) == -1;
                    case CONTAINS_I:
                         return szVariableValue.toUpperCase().indexOf( szValue.toUpperCase() ) != -1;
                    case NOT_CONTAINS_I:
                         return szVariableValue.toUpperCase().indexOf( szValue.toUpperCase() ) == -1;
                    case STARTS_WITH:
                         return szVariableValue.startsWith( szValue );
                    case NOT_STARTS_WITH:
                         return !szVariableValue.startsWith( szValue );
                    case STARTS_WITH_I:
                         return szVariableValue.toUpperCase().startsWith( szValue.toUpperCase() );
                    case NOT_STARTS_WITH_I:
                         return !szVariableValue.toUpperCase().startsWith( szValue.toUpperCase() );
                    case ENDS_WITH:
                         return szVariableValue.endsWith( szValue );
                    case NOT_ENDS_WITH:
                         return !szVariableValue.endsWith( szValue );
                    case ENDS_WITH_I:
                         return szVariableValue.toUpperCase().endsWith( szValue.toUpperCase() );
                    case NOT_ENDS_WITH_I:
                         return !szVariableValue.toUpperCase().endsWith( szValue.toUpperCase() );
                    case IN:
                    case NOT_IN:
                    case IN_I:
                    case NOT_IN_I:
                         szValue = ";"+szValue.substring( 1, szValue.length()-1 ).trim()+";";
                         switch( iLogicalOperatorFound )
                         {
                            case IN:
                            return (szValue).indexOf( ";"+szVariableValue+";" ) != -1;
                            case NOT_IN:
                            return (szValue).indexOf( ";"+szVariableValue+";" ) == -1;
                            case IN_I:
                            return (szValue.toUpperCase()).indexOf( ";"+szVariableValue.toUpperCase()+";" ) != -1;
                            case NOT_IN_I:
                            return (szValue.toUpperCase()).indexOf( ";"+szVariableValue.toUpperCase()+";" ) == -1;
                         }
                    case MOD:
                         szValue = szValue.substring( 1, szValue.length()-1 );
                         int iCommaPos = szValue.indexOf( ',' );
                         int iDiv = 1;
                         int iMod = 0;
                         if( iCommaPos != -1 )
                         {
                            iDiv = Integer.parseInt( szValue.substring( 0, iCommaPos ) );
                            iMod = Integer.parseInt( szValue.substring( iCommaPos+1 ) );
                         }
                         else
                            iDiv = Integer.parseInt( szValue );

                        return Integer.parseInt( szVariableValue )%iDiv == iMod;
                }// End Inner Switch
        }// End Outer Switch
        return false;
    }

    private String getKeyNameInProperties( String szPropertyName, Properties Pr )
    {
        if( Pr.getProperty( szPropertyName ) != null )
            return szPropertyName;
        Enumeration eKeys = Pr.keys();
        String szKey;
        while( eKeys.hasMoreElements() )
        {
            szKey = (String)eKeys.nextElement();
            if( szKey.equalsIgnoreCase( szPropertyName ) )
                return szKey;
        }
        return "";
    }

    private void addVariableName(String tok)
    {
        int pos1 = -1;
        if (tok.startsWith("(") && (pos1=tok.indexOf(")")) > 1)
        {
            int pos2 = tok.indexOf(" ",pos1);
            if (pos2 > -1)
                tok = tok.substring(pos1+1,pos2);
            else
                tok = tok.substring(pos1+1);
        }
        if (tok.length() > 0)
            pVariables.setProperty(tok,"true");
    }

    private String getQuotedPrintableText(String msgtext)
    {
        final int size = msgtext.length();
        StringBuffer sb = new StringBuffer();
        //boolean firstEncoding = false;
        for (int i=0; i<size; i++)
        {
            char tmp = msgtext.charAt(i);
            int iAsciiCode = (int)tmp;

            //if (!firstEncoding && iAsciiCode > 127) firstEncoding = true;
            if (iAsciiCode > 127 || iAsciiCode == 61)
                sb.append("="+Integer.toString(iAsciiCode, 16).toUpperCase());
            else
                sb.append(tmp);
        }
        return sb.toString();
/*
        if (firstEncoding)
            return sb.toString();
        else
            return msgtext;
*/
    }


    public static String parseQuotedPrintableText(String qsource)
    {
        int pos = -1;
        while ((pos=qsource.indexOf("=",pos+1)) != -1)
            qsource = qsource.substring( 0, pos ) + (char)Integer.parseInt(qsource.substring(pos+1,pos+3),16) + qsource.substring( pos+3 );
        return qsource;
    }
}//SimpleParser
