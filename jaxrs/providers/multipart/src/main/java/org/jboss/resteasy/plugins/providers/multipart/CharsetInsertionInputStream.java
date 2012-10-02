package org.jboss.resteasy.plugins.providers.multipart;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Sep 7, 2012
 */
public class CharsetInsertionInputStream extends InputStream
{
   enum State
   {
      EXPECT_BOUNDARY_DECL, B1, O1, U1, N1, D1, A1, R1, EXPECT_EQUALS, READ_BOUNDARY,
      EXPECT_END_OF_MAIN_HEADERS1, EXPECT_END_OF_MAIN_HEADERS2, EXPECT_END_OF_MAIN_HEADERS3, EXPECT_END_OF_MAIN_HEADERS4,
      EXPECT_PART, EXPECT_DASH, EXPECT_BOUNDARY, EXPECT_END_OF_BOUNDARY, EXPECT_END_OF_BOUNDARY2,
      EXPECT_END_OF_HEADERS1, EXPECT_END_OF_HEADERS2,
      EXPECT_CR, EXPECT_LF, EXPECT_CONTENT_TYPE,
      C1, O2, N2, T1, E1, N3, T2, DASH, T3, Y1, P1, EXPECT_COLON,
      EXPECT_SEMICOLON, EXPECT_CHARSET,
      C2, H1, A2, R2, S1, E3, T4, VERIFY_CHARSET,
      INSERT_CONTENT_TYPE, INSERT_CHARSET, RETURN_SAVED_CHAR,
      EOF
   };
 
   InputStream is;
   byte[] boundary;
   byte[] contentType;
   byte[] charset;
   int bpos = 0;
   int cspos = 0;
   int ctpos = 0;
   int savedChar;
   ByteArrayOutputStream boundaryStream = new ByteArrayOutputStream();
   State state = State.EXPECT_BOUNDARY_DECL;
   
   public CharsetInsertionInputStream(InputStream is, MediaType mediaType)
   {
      this.is = is;
      contentType = ("Content-Type: " + normalize(mediaType.toString()) + "\r\n\r\n").getBytes();
      charset = (";charset=" + mediaType.getParameters().get("charset") + "\r").getBytes();
   }

   
   @Override
   public int read() throws IOException
   {
      if (state == State.EOF)
      {
         return -1;
      }
      if (state == State.INSERT_CHARSET)
      {
         return nextCharsetChar(); 
      }
      if (state == State.INSERT_CONTENT_TYPE)
      {
         return nextContentTypeChar();
      }
      
      int c;
      if (state == State.RETURN_SAVED_CHAR)
      {
         c = savedChar;
         state = State.EXPECT_CONTENT_TYPE;
      }
      else
      {
         c = is.read();
      }
      if (c == -1)
      {
         state = State.EOF;
         return -1;
      }
      
      switch (state)
      {
         case EXPECT_BOUNDARY_DECL:
            if (c == 'B' || c == 'b')
            {
               state = State.B1;
            }
            break;
         case B1:
            if (c == 'O' || c == 'o')
            {
               state = State.O1;
            }
            else
            {
               state = State.EXPECT_BOUNDARY_DECL;
            }
            break;
         case O1:
            if (c == 'U' || c == 'u')
            {
               state = State.U1;
            }
            else
            {
               state = State.EXPECT_BOUNDARY_DECL;
            }
            break;
         case U1:
            if (c == 'N' || c == 'n')
            {
               state = State.N1;
            }
            else
            {
               state = State.EXPECT_BOUNDARY_DECL;
            }
            break;
         case N1:
            if (c == 'D' || c == 'd')
            {
               state = State.D1;
            }
            else
            {
               state = State.EXPECT_BOUNDARY_DECL;
            }
            break;
         case D1:
            if (c == 'A' || c == 'a')
            {
               state = State.A1;
            }
            else
            {
               state = State.EXPECT_BOUNDARY_DECL;
            }
            break;
         case A1:
            if (c == 'R' || c == 'r')
            {
               state = State.R1;
            }
            else
            {
               state = State.EXPECT_BOUNDARY_DECL;
            }
            break;
         case R1:
            if (c == 'Y' || c == 'y')
            {
               state = State.EXPECT_EQUALS;
            }
            else
            {
               state = State.EXPECT_BOUNDARY_DECL;
            }
            break;
         case EXPECT_EQUALS:
            if (c == '=')
            {
               state = State.READ_BOUNDARY;
            }
            else if (c != ' ' && c != '\t')
            {
               state = State.EXPECT_BOUNDARY_DECL;
            }
            break;
         case READ_BOUNDARY:
            if (c == '\r')
            {
               state = State.EXPECT_END_OF_MAIN_HEADERS2;
               boundary = boundaryStream.toByteArray();
            }
            else if (boundaryStream.size() > 0)
            {
               if (c == ' ' || c == '\t' || c == '"')
               {
                  state = State.EXPECT_END_OF_MAIN_HEADERS1;
                  boundary = boundaryStream.toByteArray();
               }
               else
               {
                  boundaryStream.write(c);
               }
            }
            else if (c != ' ' && c != '\t' && c != '"')
            {
               boundaryStream.write(c);
            }
            break;
         case EXPECT_END_OF_MAIN_HEADERS1:
            if (c == '\r')
            {
               state = State.EXPECT_END_OF_MAIN_HEADERS2;
            }
            break;
         case EXPECT_END_OF_MAIN_HEADERS2:
            if (c == '\n')
            {
               state = State.EXPECT_END_OF_MAIN_HEADERS3;
            }
            else
            {
               state = State.EXPECT_END_OF_MAIN_HEADERS1;
            }
            break;
         case EXPECT_END_OF_MAIN_HEADERS3:
            if (c == '\r')
            {
               state = State.EXPECT_END_OF_MAIN_HEADERS4;
            }
            else
            {
               state = State.EXPECT_END_OF_MAIN_HEADERS1;
            }
            break;
         case EXPECT_END_OF_MAIN_HEADERS4:
            if (c == '\n')
            {
               state = State.EXPECT_PART;
            }
            else
            {
               state = State.EXPECT_END_OF_MAIN_HEADERS1;
            }
            break;
         case EXPECT_PART:
            if (c == '-')
            {
               state = State.EXPECT_DASH;
            }
            break;
         case EXPECT_DASH:
            if (c == '-')
            {
               state = State.EXPECT_BOUNDARY;
            }
            else
            {
               state = State.EXPECT_PART;
            }
            break;
         case EXPECT_BOUNDARY:
            if (c == boundary[bpos])
            {
               if (bpos == boundary.length - 1)
               {
                  state = State.EXPECT_END_OF_BOUNDARY;
                  bpos = 0;
               }
               else
               {
                  bpos++;
               }
            }
            else
            {
               state = State.EXPECT_PART;
            }
            break;
         case EXPECT_END_OF_BOUNDARY:
            if (c == '\r')
            {
               state = State.EXPECT_END_OF_HEADERS1;
            }
            else if (c != ' ' && c != '\t')
            {
               state = State.EXPECT_BOUNDARY;
            }
            break;
         case EXPECT_CONTENT_TYPE:
            if (c == 'C' || c == 'c')
            {
               state = State.C1;
            }
            else if (c == '\r')
            {
               state = State.EXPECT_END_OF_HEADERS1;
            }
            else if (c != ' ' && c != '\t')
            {
               state = State.EXPECT_CR;
            }
            break;
         case EXPECT_END_OF_HEADERS1:
            if (c == '\n')
            {
               state = State.EXPECT_END_OF_HEADERS2;  
            }
            else
            {
               state = State.EXPECT_CONTENT_TYPE;
            }
            break;
         case EXPECT_END_OF_HEADERS2:
            if (c == '\r')
            {
               savedChar = is.read();
               if (savedChar == '\n')
               {
                  state = State.INSERT_CONTENT_TYPE;
                  return nextContentTypeChar();
               }
               else
               {
                  state = State.RETURN_SAVED_CHAR;
               }
            }
            if (c == 'C' || c == 'c')
            {
               state = State.C1;
            }
            else if (c == ' ' || c == '\t')
            {
               state = State.EXPECT_CONTENT_TYPE;
            }
            else
            {
               state = State.EXPECT_CR;
            }
            break;
         case EXPECT_CR:
            if (c == '\r')
            {
               state = State.EXPECT_END_OF_HEADERS1;
            }
            break;
         case EXPECT_LF:
            if (c == '\n')
            {
               state = State.EXPECT_CONTENT_TYPE;
            }
            else
            {
               state = State.EXPECT_CR;
            }
            break;
         case C1:
            if (c == 'O' || c == 'o')
            {
               state = State.O2;
            }
            else if (c == '\r')
            {
               state = State.EXPECT_LF;
            }
            else
            {
               state = State.EXPECT_CR;
            }
            break;
         case O2:
            if (c == 'N' || c == 'n')
            {
               state = State.N2;
            }
            else if (c == '\r')
            {
               state = State.EXPECT_LF;
            }
            else
            {
               state = State.EXPECT_CR;
            }
            break;
         case N2:
            if (c == 'T' || c == 't')
            {
               state = State.T1;
            }
            else if (c == '\r')
            {
               state = State.EXPECT_LF;
            }
            else
            {
               state = State.EXPECT_CR;
            }
            break;
         case T1:
            if (c == 'E' || c == 'e')
            {
               state = State.E1;
            }
            else if (c == '\r')
            {
               state = State.EXPECT_LF;
            }
            else
            {
               state = State.EXPECT_CR;
            }
            break;
         case E1:
            if (c == 'N' || c == 'n')
            {
               state = State.N3;
            }
            else if (c == '\r')
            {
               state = State.EXPECT_LF;
            }
            else
            {
               state = State.EXPECT_CR;
            }
            break;
         case N3:
            if (c == 'T' || c == 't')
            {
               state = State.T2;
            }
            else if (c == '\r')
            {
               state = State.EXPECT_LF;
            }
            else
            {
               state = State.EXPECT_CR;
            }
            break;
         case T2:
            if (c == '-')
            {
               state = State.DASH;
            }
            else if (c == '\r')
            {
               state = State.EXPECT_LF;
            }
            else
            {
               state = State.EXPECT_CR;
            }
            break;
         case DASH:
            if (c == 'T' || c == 't')
            {
               state = State.T3;
            }
            else if (c == '\r')
            {
               state = State.EXPECT_LF;
            }
            else
            {
               state = State.EXPECT_CR;
            }
            break;
         case T3:
            if (c == 'Y' || c == 'y')
            {
               state = State.Y1;
            }
            else if (c == '\r')
            {
               state = State.EXPECT_LF;
            }
            else
            {
               state = State.EXPECT_CR;
            }
            break;
         case Y1:
            if (c == 'P' || c == 'p')
            {
               state = State.P1;
            }
            else if (c == '\r')
            {
               state = State.EXPECT_LF;
            }
            else
            {
               state = State.EXPECT_CR;
            }
            break;
         case P1:
            if (c == 'E' || c == 'e')
            {
               state = State.EXPECT_COLON;
            }
            else if (c == '\r')
            {
               state = State.EXPECT_LF;
            }
            else
            {
               state = State.EXPECT_CR;
            }
            break;
         case EXPECT_COLON:
            if (c == ':')
            {
               state = State.EXPECT_CHARSET;
            }
            else if (c == '\r') // Ill-formed header.  Ignore.
            {
               state = State.EXPECT_LF;
            }
            else if (c != ' ' && c != '\t')
            {
               state = State.EXPECT_CR;
            }
            break;
         case EXPECT_SEMICOLON:
            if (c == ';')
            {
               state = State.EXPECT_CHARSET;
            }
            else if (c == '\r')
            {
               state = State.INSERT_CHARSET;
               return nextCharsetChar();
            }
            break;
         case EXPECT_CHARSET:
            if (c == 'C' || c == 'c')
            {
               state = State.C2;
            }
            else if (c == '\r')
            {
               state = State.INSERT_CHARSET;
               return nextCharsetChar();
            }
            else if (c != ' ' && c != '\t')
            {
               state = State.EXPECT_SEMICOLON;
            }
            break;
         case C2:
            if (c == 'H' || c == 'h')
            {
               state = State.H1;
            }
            else if (c == '\r')
            {
               state = State.INSERT_CHARSET;
               return nextCharsetChar();
            }
            else
            {
               state = State.EXPECT_SEMICOLON;
            }
            break;
         case H1:
            if (c == 'A' || c == 'a')
            {
               state = State.A2;
            }
            else if (c == '\r')
            {
               state = State.INSERT_CHARSET;
               return nextCharsetChar();
            }
            else
            {
               state = State.EXPECT_SEMICOLON;
            }
            break;
         case A2:
            if (c == 'R' || c == 'r')
            {
               state = State.R2;
            }
            else if (c == '\r')
            {
               state = State.INSERT_CHARSET;
               return nextCharsetChar();
            }
            else
            {
               state = State.EXPECT_SEMICOLON;
            }
            break;
         case R2:
            if (c == 'S' || c == 's')
            {
               state = State.S1;
            }
            else if (c == '\r')
            {
               state = State.INSERT_CHARSET;
               return nextCharsetChar();
            }
            else
            {
               state = State.EXPECT_SEMICOLON;
            }
            break;
         case S1:
            if (c == 'E' || c == 'e')
            {
               state = State.E3;
            }
            else if (c == '\r')
            {
               state = State.INSERT_CHARSET;
               return nextCharsetChar();
            }
            else
            {
               state = State.EXPECT_SEMICOLON;
            }
            break;
         case E3:
            if (c == 'T' || c == 't')
            {
               state = State.VERIFY_CHARSET;
            }
            else if (c == '\r')
            {
               state = State.INSERT_CHARSET;
               return nextCharsetChar();
            }
            else
            {
               state = State.EXPECT_SEMICOLON;
            }
            break;
         case VERIFY_CHARSET:
            if (c == '=' || c == '\r' || c == ' ' || c == '\t')
            {
               state = State.EXPECT_BOUNDARY;
            }
            else
            {
               state = State.EXPECT_SEMICOLON;
            }
            break;

         default:
            throw new RuntimeException("unexpected state in CharsetInsertionInputStream finite state machine");
      }
      return c;
   }
   
   private int nextCharsetChar()
   {
      if (cspos == charset.length - 1)
      {
         cspos = 0;
         state = State.EXPECT_BOUNDARY;
         return charset[charset.length - 1];
      }
      return charset[cspos++];  
   } 
   
   private int nextContentTypeChar()
   {
      if (ctpos == contentType.length - 1)
      {
         ctpos = 0;
         state = State.EXPECT_BOUNDARY;
         return contentType[contentType.length - 1];
      }
      return contentType[ctpos++];  
   } 
   
   private String normalize(String s)
   {
      String sl = s.toLowerCase();
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < s.length(); i++)
      {
         if (sl.charAt(i) != ' ' && sl.charAt(i) != '"')
         {
            sb.append(sl.charAt(i));
         }
      }
      return sb.toString();
   }
}

