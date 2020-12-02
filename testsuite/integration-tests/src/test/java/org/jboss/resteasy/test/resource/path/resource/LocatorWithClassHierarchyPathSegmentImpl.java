package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.PathSegment;

public class LocatorWithClassHierarchyPathSegmentImpl implements PathSegment {

   public LocatorWithClassHierarchyPathSegmentImpl(final String id) {
      super();
      this.id = id;
   }

   private String id;

   @Override
   public MultivaluedMap<String, String> getMatrixParameters() {
      return null;
   }

   @Override
   public String getPath() {
      return id;
   }

}
