package org.jboss.resteasy.star.bpm.test;

import org.jbpm.api.listener.EventListener;
import org.jbpm.api.listener.EventListenerExecution;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class LogListener implements EventListener
{

  private static final long serialVersionUID = 1L;

  // value gets injected from process definition
  String msg;

  public void notify(EventListenerExecution execution) {
     System.out.println("----> " + msg);
  }
}
