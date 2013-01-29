package org.jboss.resteasy.specimpl;

import org.jboss.resteasy.spi.NotImplementedYetException;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;
import java.lang.reflect.Method;
import java.net.URI;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LinkBuilderImpl implements Link.Builder
{
   protected LinkImpl link = new LinkImpl();
   protected UriBuilder uriBuilder;

   @Override
   public Link.Builder link(Link link)
   {
      uriBuilder = UriBuilder.fromUri(link.getUri());
      this.link.map.clear();
      this.link.map.putAll(link.getParams());
      return this;
   }

   @Override
   public Link.Builder link(String link)
   {
      Link l = LinkImpl.valueOf(link);
      return link(l);
   }

   @Override
   public Link.Builder uriBuilder(UriBuilder uriBuilder)
   {
      this.uriBuilder = uriBuilder;
      return this;
   }

   @Override
   public Link.Builder uri(URI uri) {
      uriBuilder = UriBuilder.fromUri(uri);
      return this;
   }

   @Override
   public Link.Builder uri(String uri) throws IllegalArgumentException {
      uriBuilder = UriBuilder.fromUri(uri);
      return this;
   }

   @Override
   public Link.Builder rel(String rel) {
      final String rels = link.map.get(Link.REL);
      param(Link.REL, rels == null ? rel : rels + " " + rel);
      return this;
   }

   @Override
   public Link.Builder title(String title) {
      param(Link.TITLE, title);
      return this;

   }

   @Override
   public Link.Builder type(String type) {
      param(Link.TYPE, type);
      return this;
   }

   @Override
   public Link.Builder param(String name, String value) throws IllegalArgumentException {
      if (name == null || value == null) {
         throw new IllegalArgumentException("Link parameter name or value is null");
      }
      link.map.put(name, value);
      return this;
   }

   @Override
   public Link build(Object... values) throws UriBuilderException
   {
      link.uri = uriBuilder.build(values);
      return link;
   }

   @Override
   public Link buildRelativized(UriInfo uriInfo, Object... values)
   {
      URI uri = uriBuilder.build(values);
      link.uri = uriInfo.relativize(uri);
      return link;
   }

   @Override
   public Link buildResolved(UriInfo uriInfo, Object... values)
   {
      URI uri = uriBuilder.build(values);
      link.uri = uriInfo.resolve(uri);
      return link;
   }
}
