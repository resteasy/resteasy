package org.jboss.resteasy.core.registry;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClassExpression extends Expression
{
   protected RootNode root = new RootNode();
   protected ClassNode parent;

   public ClassExpression(String segment)
   {
      super(segment, "".equals(segment) ? "(.*)" : "(/.+)?");
   }

   public RootNode getRoot()
   {
      return root;
   }

   public ClassNode getParent()
   {
      return parent;
   }
}
