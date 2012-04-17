package javax.ws.rs;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Response;

public class ViolationException extends WebApplicationException
{
   private static final long serialVersionUID = -1936853734650912417L;
   private List<String> exceptions;

   public ViolationException()
   {
      super(null, Response.Status.BAD_REQUEST);
      exceptions = new ArrayList<String>();
   }

   public ViolationException(List<String> exceptions)
   {
      super(null, Response.Status.BAD_REQUEST);
      this.exceptions = exceptions;
   }
   
   public void setExceptions(List<String> exceptions)
   {
      this.exceptions = exceptions;
   }
   
   public List<String> getExceptions()
   {
      return exceptions;
   }
}
