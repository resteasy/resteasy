package org.jboss.resteasy.core;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

class ContextServletOutputStream extends ServletOutputStream {
    private final ContextParameterInjector injector;
    private final ServletOutputStream delegate;

    ContextServletOutputStream(final ContextParameterInjector injector, final OutputStream delegate) {
        this.injector = injector;
        this.delegate = (ServletOutputStream) delegate;
    }

    ////////////////////////////////////////////////////////////////
    /// ServletOutputStream methods
    ////////////////////////////////////////////////////////////////
    @Override
    public void print(String s) throws IOException {
        delegate.print(s);
        injector.setOutputStreamWasWritten(true);
    }

    @Override
    public void print(boolean b) throws IOException {
        delegate.print(b);
        injector.setOutputStreamWasWritten(true);
    }

    @Override
    public void print(char c) throws IOException {
        delegate.print(c);
        injector.setOutputStreamWasWritten(true);
    }

    @Override
    public void print(int i) throws IOException {
        delegate.print(i);
        injector.setOutputStreamWasWritten(true);
    }

    @Override
    public void print(long l) throws IOException {
        delegate.print(l);
        injector.setOutputStreamWasWritten(true);
    }

    @Override
    public void print(float f) throws IOException {
        delegate.print(f);
        injector.setOutputStreamWasWritten(true);
    }

    @Override
    public void print(double d) throws IOException {
        delegate.print(d);
        injector.setOutputStreamWasWritten(true);
    }

    @Override
    public void println() throws IOException {
        delegate.println();
        injector.setOutputStreamWasWritten(true);
    }

    @Override
    public void println(String s) throws IOException {
        delegate.println(s);
        injector.setOutputStreamWasWritten(true);
    }

    @Override
    public void println(boolean b) throws IOException {
        delegate.println(b);
        injector.setOutputStreamWasWritten(true);
    }

    @Override
    public void println(char c) throws IOException {
        delegate.print(c);
        injector.setOutputStreamWasWritten(true);
    }

    @Override
    public void println(int i) throws IOException {
        delegate.println(i);
        injector.setOutputStreamWasWritten(true);
    }

    @Override
    public void println(long l) throws IOException {
        delegate.println(l);
        injector.setOutputStreamWasWritten(true);
    }

    @Override
    public void println(float f) throws IOException {
        delegate.println(f);
        injector.setOutputStreamWasWritten(true);
    }

    @Override
    public void println(double d) throws IOException {
        delegate.println(d);
        injector.setOutputStreamWasWritten(true);
    }

    @Override
    public boolean isReady() {
        return delegate.isReady();
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        delegate.setWriteListener(writeListener);
    }

    ////////////////////////////////////////////////////////////////
    /// OutputStream methods
    ////////////////////////////////////////////////////////////////
    @Override
    public void write(byte[] b) throws IOException {
        delegate.write(b);
        injector.setOutputStreamWasWritten(true);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        delegate.write(b, off, len);
        injector.setOutputStreamWasWritten(true);
    }

    @Override
    public void write(int b) throws IOException {
        delegate.write(b);
        injector.setOutputStreamWasWritten(true);
    }

    @Override
    public void flush() throws IOException {
        delegate.flush();
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    public ServletOutputStream getDelegate() {
        return delegate;
    }

    public ContextParameterInjector getInjector() {
        return injector;
    }
}
