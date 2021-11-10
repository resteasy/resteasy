package org.jboss.resteasy.links.test;

import org.jboss.resteasy.links.ELProvider;

import jakarta.el.ELContext;
import jakarta.el.ELResolver;
import jakarta.el.FunctionMapper;
import jakarta.el.VariableMapper;

public class SeamELProvider implements ELProvider {

   public ELContext getContext(final ELContext ctx) {
      return new ELContext() {

         private SeamFunctionMapper functionMapper;

         @Override
         public ELResolver getELResolver() {
            return ctx.getELResolver();
         }

         @Override
         public FunctionMapper getFunctionMapper() {
            if (functionMapper == null)
               functionMapper = new SeamFunctionMapper(ctx
                     .getFunctionMapper());
            return functionMapper;
         }

         @Override
         public VariableMapper getVariableMapper() {
            return ctx.getVariableMapper();
         }
      };
   }

}
