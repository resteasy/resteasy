package org.jboss.resteasy.test.resource.param.resource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class MultiValuedParamResource {

	@Path("queryParam")
	public static class QueryParamResource {

		// http://xxx/queryParam/customConversion_multiValuedParam?date=20161217,20161218,20161219
		@GET
		@Path("customConversion_multiValuedParam")
		public Response customConversion_multiValuedParam(@QueryParam("date") MultiValuedParam<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}
		
		// http://xxx/queryParam/customConversion_multiValuedParam_array?date=20161217,20161218,20161219
		@GET
		@Path("customConversion_multiValuedParam_array")
		public Response customConversion_multiValuedParam_array(@QueryParam("date") ParamWrapper<Date>[] dates) {
			Collection<Date> dateCollection=new ArrayList<>();
			for (ParamWrapper<Date> paramWrapper : dates) {
				dateCollection.add(paramWrapper.getElement());
			}
			return Response.ok(formatDates(dateCollection)).build();
		}

		// http://xxx/queryParam/defaultConversion_list?date=20161217&date=20161218&date=20161219
		@GET
		@Path("defaultConversion_list")
		public Response defaultConversion_list(@QueryParam("date") List<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// http://xxx/queryParam/defaultConversion_arrayList?date=20161217&date=20161218&date=20161219
		@GET
		@Path("defaultConversion_arrayList")
		public Response defaultConversion_arrayList(@QueryParam("date") ArrayList<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// http://xxx/queryParam/defaultConversion_set?date=20161217&date=20161218&date=20161219
		@GET
		@Path("defaultConversion_set")
		public Response defaultConversion_set(@QueryParam("date") Set<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// http://xxx/queryParam/defaultConversion_hashSet?date=20161217&date=20161218&date=20161219
		@GET
		@Path("defaultConversion_hashSet")
		public Response defaultConversion_hashSet(@QueryParam("date") HashSet<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// http://xxx/queryParam/defaultConversion_sortedSet?date=20161217&date=20161218&date=20161219
		@GET
		@Path("defaultConversion_sortedSet")
		public Response defaultConversion_sortedSet(@QueryParam("date") SortedSet<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// http://xxx/queryParam/defaultConversion_treeSet?date=20161217&date=20161218&date=20161219
		@GET
		@Path("defaultConversion_treeSet")
		public Response defaultConversion_treeSet(@QueryParam("date") TreeSet<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}
		
		// http://xxx/queryParam/defaultConversion_array?date=20161217&date=20161218&date=20161219
		@GET
		@Path("defaultConversion_array")
		public Response defaultConversion_array(@QueryParam("date") Date[] dates) {
			return Response.ok(formatDates(Arrays.asList(dates))).build();
		}

	}

	@Path("headerParam")
	public static class HeaderParamResource {

		// date:20161217,20161218,20161219
		@GET
		@Path("customConversion_multiValuedParam")
		public Response customConversion_multiValuedParam(@HeaderParam("date") MultiValuedParam<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}
		
		// date:20161217,20161218,20161219
		@GET
		@Path("customConversion_multiValuedParam_array")
		public Response customConversion_multiValuedParam_array(@HeaderParam("date") ParamWrapper<Date>[] dates) {
			Collection<Date> dateCollection=new ArrayList<>();
			for (ParamWrapper<Date> paramWrapper : dates) {
				dateCollection.add(paramWrapper.getElement());
			}
			return Response.ok(formatDates(dateCollection)).build();
		}

		// date:20161217,20161218,20161219
		@GET
		@Path("defaultConversion_list")
		public Response defaultConversion_list(@HeaderParam("date") List<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// date:20161217,20161218,20161219
		@GET
		@Path("defaultConversion_arrayList")
		public Response defaultConversion_arrayList(@HeaderParam("date") ArrayList<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// date:20161217,20161218,20161219
		@GET
		@Path("defaultConversion_set")
		public Response defaultConversion_set(@HeaderParam("date") Set<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// date:20161217,20161218,20161219
		@GET
		@Path("defaultConversion_hashSet")
		public Response defaultConversion_hashSet(@HeaderParam("date") HashSet<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// date:20161217,20161218,20161219
		@GET
		@Path("defaultConversion_sortedSet")
		public Response defaultConversion_sortedSet(@HeaderParam("date") SortedSet<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// date:20161217,20161218,20161219
		@GET
		@Path("defaultConversion_treeSet")
		public Response defaultConversion_treeSet(@HeaderParam("date") TreeSet<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}
		
		// date:20161217,20161218,20161219
		@GET
		@Path("defaultConversion_array")
		public Response defaultConversion_array(@HeaderParam("date") Date[] dates) {
			return Response.ok(formatDates(Arrays.asList(dates))).build();
		}

	}
	
	@Path("matrixParam")
	public static class MatrixParamResource {

		// http://xxx/matrixParam/customConversion_multiValuedParam;date=20161217,20161218,20161219
		@GET
		@Path("customConversion_multiValuedParam")
		public Response customConversion_multiValuedParam(@MatrixParam("date") MultiValuedParam<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}
		
		// http://xxx/matrixParam/customConversion_multiValuedParam_array;date=20161217,20161218,20161219
		@GET
		@Path("customConversion_multiValuedParam_array")
		public Response customConversion_multiValuedParam_array(@MatrixParam("date") ParamWrapper<Date>[] dates) {
			Collection<Date> dateCollection=new ArrayList<>();
			for (ParamWrapper<Date> paramWrapper : dates) {
				dateCollection.add(paramWrapper.getElement());
			}
			return Response.ok(formatDates(dateCollection)).build();
		}

		// http://xxx/matrixParam/defaultConversion_list;date=20161217;date=20161218;date=20161219
		@GET
		@Path("defaultConversion_list")
		public Response defaultConversion_list(@MatrixParam("date") List<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// http://xxx/matrixParam/defaultConversion_arrayList;date=20161217;date=20161218;date=20161219
		@GET
		@Path("defaultConversion_arrayList")
		public Response defaultConversion_arrayList(@MatrixParam("date") ArrayList<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// http://xxx/matrixParam/defaultConversion_set;date=20161217;date=20161218;date=20161219
		@GET
		@Path("defaultConversion_set")
		public Response defaultConversion_set(@MatrixParam("date") Set<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// http://xxx/matrixParam/defaultConversion_hashSet;date=20161217;date=20161218;date=20161219
		@GET
		@Path("defaultConversion_hashSet")
		public Response defaultConversion_hashSet(@MatrixParam("date") HashSet<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// http://xxx/matrixParam/defaultConversion_sortedSet;date=20161217;date=20161218;date=20161219
		@GET
		@Path("defaultConversion_sortedSet")
		public Response defaultConversion_sortedSet(@MatrixParam("date") SortedSet<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// http://xxx/matrixParam/defaultConversion_treeSet;date=20161217;date=20161218;date=20161219
		@GET
		@Path("defaultConversion_treeSet")
		public Response defaultConversion_treeSet(@MatrixParam("date") TreeSet<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}
		
		// http://xxx/matrixParam/defaultConversion_array;date=20161217;date=20161218;date=20161219
		@GET
		@Path("defaultConversion_array")
		public Response defaultConversion_array(@MatrixParam("date") Date[] dates) {
			return Response.ok(formatDates(Arrays.asList(dates))).build();
		}

	}
	
	@Path("cookieParam")
	public static class CookieParamResource {

		// cookie:date=20161217-20161218-20161219
		@GET
		@Path("customConversion_multiValuedCookieParam")
		public Response customConversion_multiValuedCookieParam(@CookieParam("date") MultiValuedCookieParam<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}
		
		// cookie:date=20161217-20161218-20161219
		@GET
		@Path("customConversion_multiValuedCookieParam_array")
		public Response customConversion_multiValuedCookieParam_array(@CookieParam("date") CookieParamWrapper<Date>[] dates) {
			Collection<Date> dateCollection=new ArrayList<>();
			for (CookieParamWrapper<Date> paramWrapper : dates) {
				dateCollection.add(paramWrapper.getElement());
			}
			return Response.ok(formatDates(dateCollection)).build();
		}

		// cookie:date=20161217
		@GET
		@Path("defaultConversion_list")
		public Response defaultConversion_list(@CookieParam("date") List<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// cookie:date=20161217
		@GET
		@Path("defaultConversion_arrayList")
		public Response defaultConversion_arrayList(@CookieParam("date") ArrayList<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// cookie:date=20161217
		@GET
		@Path("defaultConversion_set")
		public Response defaultConversion_set(@CookieParam("date") Set<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// cookie:date=20161217
		@GET
		@Path("defaultConversion_hashSet")
		public Response defaultConversion_hashSet(@CookieParam("date") HashSet<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// cookie:date=20161217
		@GET
		@Path("defaultConversion_sortedSet")
		public Response defaultConversion_sortedSet(@CookieParam("date") SortedSet<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// cookie:date=20161217
		@GET
		@Path("defaultConversion_treeSet")
		public Response defaultConversion_treeSet(@CookieParam("date") TreeSet<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}
		
		// cookie:date=20161217
		@GET
		@Path("defaultConversion_array")
		public Response defaultConversion_array(@CookieParam("date") Date[] dates) {
			return Response.ok(formatDates(Arrays.asList(dates))).build();
		}

	}
	
	@Path("formParam")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public static class FormParamResource {

		// date=20161217,20161218,20161219
		@POST
		@Path("customConversion_multiValuedParam")
		public Response customConversion_multiValuedParam(@FormParam("date") MultiValuedParam<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}
		
		// date=20161217,20161218,20161219
		@POST
		@Path("customConversion_multiValuedParam_array")
		public Response customConversion_multiValuedParam_array(@FormParam("date") ParamWrapper<Date>[] dates) {
			Collection<Date> dateCollection=new ArrayList<>();
			for (ParamWrapper<Date> paramWrapper : dates) {
				dateCollection.add(paramWrapper.getElement());
			}
			return Response.ok(formatDates(dateCollection)).build();
		}

		// date=20161217&date=20161218&date=20161219
		@POST
		@Path("defaultConversion_list")
		public Response defaultConversion_list(@FormParam("date") List<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// date=20161217&date=20161218&date=20161219
		@POST
		@Path("defaultConversion_arrayList")
		public Response defaultConversion_arrayList(@FormParam("date") ArrayList<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// date=20161217&date=20161218&date=20161219
		@POST
		@Path("defaultConversion_set")
		public Response defaultConversion_set(@FormParam("date") Set<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// date=20161217&date=20161218&date=20161219
		@POST
		@Path("defaultConversion_hashSet")
		public Response defaultConversion_hashSet(@FormParam("date") HashSet<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// date=20161217&date=20161218&date=20161219
		@POST
		@Path("defaultConversion_sortedSet")
		public Response defaultConversion_sortedSet(@FormParam("date") SortedSet<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}

		// date=20161217&date=20161218&date=20161219
		@POST
		@Path("defaultConversion_treeSet")
		public Response defaultConversion_treeSet(@FormParam("date") TreeSet<Date> dates) {
			return Response.ok(formatDates(dates)).build();
		}
		
		// date=20161217&date=20161218&date=20161219
		@POST
		@Path("defaultConversion_array")
		public Response defaultConversion_array(@FormParam("date") Date[] dates) {
			return Response.ok(formatDates(Arrays.asList(dates))).build();
		}

	}
	
	@Path("pathParam")
	public static class PathParamResource {

		// http://xxx/pathParam/customConversion_multiValuedPathParam/20161217/20161218/20161219
		@GET
		@Path("customConversion_multiValuedPathParam/{path: .+}")
		public Response customConversion_multiValuedPathParam(@PathParam("path") MultiValuedPathParam<Date> paths) {
			return Response.ok(formatDates(paths)).build();
		}
		
		// http://xxx/pathParam/customConversion_multiValuedPathParam_array/20161217/20161218/20161219
		@GET
		@Path("customConversion_multiValuedPathParam_array/{path: .+}")
		public Response customConversion_multiValuedPathParam_array(@PathParam("path") PathParamWrapper<Date>[] paths) {
			Collection<Date> pathCollection=new ArrayList<>();
			for (PathParamWrapper<Date> paramWrapper : paths) {
				pathCollection.add(paramWrapper.getElement());
			}
			return Response.ok(formatDates(pathCollection)).build();
		}

		// http://xxx/pathParam/defaultConversion_list/20161217
		@GET
		@Path("defaultConversion_list/{path: .+}")
		public Response defaultConversion_list(@PathParam("path") List<Date> paths) {
			return Response.ok(formatDates(paths)).build();
		}

		// http://xxx/pathParam/defaultConversion_arrayList/20161217
		@GET
		@Path("defaultConversion_arrayList/{path: .+}")
		public Response defaultConversion_arrayList(@PathParam("path") ArrayList<Date> paths) {
			return Response.ok(formatDates(paths)).build();
		}

		// http://xxx/pathParam/defaultConversion_set/20161217
		@GET
		@Path("defaultConversion_set/{path: .+}")
		public Response defaultConversion_set(@PathParam("path") Set<Date> paths) {
			return Response.ok(formatDates(paths)).build();
		}

		// http://xxx/pathParam/defaultConversion_hashSet/20161217
		@GET
		@Path("defaultConversion_hashSet/{path: .+}")
		public Response defaultConversion_hashSet(@PathParam("path") HashSet<Date> paths) {
			return Response.ok(formatDates(paths)).build();
		}

		// http://xxx/pathParam/defaultConversion_sortedSet/20161217
		@GET
		@Path("defaultConversion_sortedSet/{path: .+}")
		public Response defaultConversion_sortedSet(@PathParam("path") SortedSet<Date> paths) {
			return Response.ok(formatDates(paths)).build();
		}

		// http://xxx/pathParam/defaultConversion_treeSet/20161217
		@GET
		@Path("defaultConversion_treeSet/{path: .+}")
		public Response defaultConversion_treeSet(@PathParam("path") TreeSet<Date> paths) {
			return Response.ok(formatDates(paths)).build();
		}
		
		// http://xxx/pathParam/defaultConversion_array/20161217
		@GET
		@Path("defaultConversion_array/{path: .+}")
		public Response defaultConversion_array(@PathParam("path") Date[] paths) {
			return Response.ok(formatDates(Arrays.asList(paths))).build();
		}

	}
	
	private static String formatDates(Collection<Date> dates) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		StringBuilder stringBuilder = new StringBuilder();
		int datesCount = dates.size();
		int i = 0;
		for (Date date : new TreeSet<>(dates)) {
			stringBuilder.append(simpleDateFormat.format(date));
			++i;
			if (i < datesCount) {
				stringBuilder.append(",");
			}
		}
		return stringBuilder.toString();
	}
	
}
