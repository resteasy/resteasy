package org.jboss.resteasy.plugins.providers.multipart;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.RuntimeDelegate;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HeaderFlushedOutputStream extends OutputStream {
	private MultivaluedMap<String, Object> headers;
	private OutputStream stream;
	private boolean headersFlushed = false;

	public HeaderFlushedOutputStream(MultivaluedMap<String, Object> headers,
			OutputStream delegate) {
		this.headers = headers;
		this.stream = delegate;
	}

	@SuppressWarnings(value = "unchecked")
	protected void flushHeaders() throws IOException {
		if (headersFlushed)
			return;

		headersFlushed = true;
		RuntimeDelegate delegate = RuntimeDelegate.getInstance();

		for (String key : headers.keySet()) {
			List<Object> objs = headers.get(key);
			for (Object obj : objs) {
				String value;
				RuntimeDelegate.HeaderDelegate headerDelegate = delegate
						.createHeaderDelegate(obj.getClass());
				if (headerDelegate != null) {
					value = headerDelegate.toString(obj);
				} else {
					value = obj.toString();
				}
				stream.write(key.getBytes());
				stream.write(": ".getBytes());
				stream.write(value.getBytes());
				stream.write("\r\n".getBytes());
			}
		}
		stream.write("\r\n".getBytes());

	}

	@Override
	public void write(int i) throws IOException {
		flushHeaders();
		stream.write(i);
	}

	@Override
	public void write(byte[] bytes) throws IOException {
		flushHeaders();
		stream.write(bytes);
	}

	@Override
	public void write(byte[] bytes, int i, int i1) throws IOException {
		flushHeaders();
		stream.write(bytes, i, i1);
	}

	@Override
	public void flush() throws IOException {
		stream.flush();
	}

	@Override
	public void close() throws IOException {
		stream.close();
	}
}
