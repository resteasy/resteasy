package org.jboss.resteasy.core.providerfactory;

public final class ExtSortedKey<T> extends SortedKey<T>
{
   public ExtSortedKey(final Class<?> intf, final T reader, final Class<?> readerClass, final int priority, final boolean isBuiltin)
   {
      super(intf, reader, readerClass, priority, isBuiltin);
   }

   public ExtSortedKey(final Class<?> intf, final T reader, final Class<?> readerClass, final boolean isBuiltin)
   {
      super(intf, reader, readerClass, isBuiltin);
   }

   public ExtSortedKey(final Class<?> intf, final T reader, final Class<?> readerClass)
   {
      super(intf, reader, readerClass);
   }

   @Override
   public int compareTo(SortedKey<T> tMessageBodyKey)
   {
      int c = super.compareTo(tMessageBodyKey);
      if (c != 0)
      {
         return c;
      }
      if (this.getObj() == tMessageBodyKey.getObj())
      {
         return 0;
      }
      return -1;
   }
}
