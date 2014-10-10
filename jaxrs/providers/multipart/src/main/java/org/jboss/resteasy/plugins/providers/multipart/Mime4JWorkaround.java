package org.jboss.resteasy.plugins.providers.multipart;

import java.io.IOException;
import java.io.InputStream;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.MimeIOException;
import org.apache.james.mime4j.codec.DecodeMonitor;
import org.apache.james.mime4j.dom.Message;
import org.apache.james.mime4j.field.DefaultFieldParser;
import org.apache.james.mime4j.field.LenientFieldParser;
import org.apache.james.mime4j.message.BasicBodyFactory;
import org.apache.james.mime4j.message.BodyFactory;
import org.apache.james.mime4j.message.DefaultBodyDescriptorBuilder;
import org.apache.james.mime4j.message.MessageImpl;
import org.apache.james.mime4j.parser.MimeStreamParser;
import org.apache.james.mime4j.stream.BodyDescriptorBuilder;
import org.apache.james.mime4j.stream.MimeConfig;

/**
 * Copy code from org.apache.james.mime4j.message.DefaultMessageBuilder.parseMessage().
 * Alter said code to use Mime4JWorkaroundBinaryEntityBuilder instead of EntityBuilder.
 */
public class Mime4JWorkaround
{
    /**
     * This is a rough copy of DefaultMessageBuilder.parseMessage() modified to use a Mime4JWorkaround as the contentHandler instead
     * of an EntityBuilder.
     * <p>
     * @see org.apache.james.mime4j.message.DefaultMessageBuilder#parseMessage(java.io.InputStream)
     * @param is
     * @return
     * @throws IOException
     * @throws MimeIOException
     */
    public static Message parseMessage(InputStream is) throws IOException, MimeIOException
    {
        try
        {
            MessageImpl message = new MessageImpl();
            MimeConfig cfg = new MimeConfig();
            boolean strict = cfg.isStrictParsing();
            DecodeMonitor mon = strict ? DecodeMonitor.STRICT : DecodeMonitor.SILENT;
            BodyDescriptorBuilder bdb = new DefaultBodyDescriptorBuilder(null, strict ? DefaultFieldParser.getParser() : LenientFieldParser.getParser(), mon);
            BodyFactory bf = new BasicBodyFactory();
            MimeStreamParser parser = new MimeStreamParser(cfg, mon, bdb);
            // EntityBuilder expect the parser will send ParserFields for the well known fields
            // It will throw exceptions, otherwise.
            parser.setContentHandler(new Mime4jWorkaroundBinaryEntityBuilder(message, bf));
            parser.setContentDecoding(false);
            parser.setRecurse();

            parser.parse(is);
            return message;
        }
        catch (MimeException e)
        {
            throw new MimeIOException(e);
        }
    }
}
