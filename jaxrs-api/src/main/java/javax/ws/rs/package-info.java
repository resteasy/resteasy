/**
 * High-level interfaces and annotations used to create RESTful service 
 * resources. E.g.:
 <pre>
 &#064;Path("widgets/{widgetid}")
 &#064;Consumes("application/widgets+xml")
 &#064;Produces("application/widgets+xml")
 public class WidgetResource {

 &#064;GET
 public String getWidget(&#064;PathParam("widgetid") String id) {
 return getWidgetAsXml(id);
 }

 &#064;PUT
 public void updateWidget(&#064;PathParam("widgetid") String id,
 Source update) {
 updateWidgetFromXml(id, update);
 }

 ...
 }
 </pre>
 */
package javax.ws.rs;