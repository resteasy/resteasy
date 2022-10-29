package org.jboss.resteasy.grpc.runtime.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class AsyncMockServletOutputStream extends MockServletOutputStream {

   private static final ByteArrayOutputStream CLOSE_MARKER = new ByteArrayOutputStream();
   private enum STATE {OPEN, CLOSING, CLOSED};

   private STATE state = STATE.OPEN;
   private volatile ArrayList<ByteArrayOutputStream> list = new ArrayList<ByteArrayOutputStream>();

   @Override
   public boolean isClosed() {
      return state == STATE.CLOSED;
   }

   public synchronized ByteArrayOutputStream await() throws InterruptedException {
      if (state == STATE.CLOSED) {
         return null;
      }
      if (state == STATE.CLOSING) {
         ByteArrayOutputStream baos = list.remove(0);
         if (CLOSE_MARKER == baos) {
            state = STATE.CLOSED;
            return null;
         }
         return baos;
      }
      while (true) {
         if (list.size() > 0) {
            ByteArrayOutputStream baos = list.remove(0);
            if (CLOSE_MARKER == baos) {
               state = STATE.CLOSED;
               return null;
            }
            return baos;
         }
         try {
            wait();
         } catch (InterruptedException e) {
            //
         }
      }
   }

   public synchronized void release() throws IOException {
      if (state != STATE.OPEN) {
         return;
      }
      list.add(getDelegate());
      notify();
   }

   public synchronized void release(ByteArrayOutputStream baos) throws IOException {
      if (state != STATE.OPEN) {
         return;
      }
      list.add(baos);
      notify();
   }

   @Override
   public synchronized void close() throws IOException {
      if (state != STATE.OPEN) {
         return;
      }
      if (list.isEmpty()) {
         state = STATE.CLOSED;
      } else {
         state = STATE.CLOSING;
      }
      list.add(CLOSE_MARKER);
      notifyAll();
//      System.out.println("AsyncMockServletOutputStream.close()");
   }
}
