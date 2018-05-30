package org.jboss.resteasy.core;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.MultivaluedMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Abstract implementation of {@link ValueInjector} that can inject collections.
 *
 * @param <T> The type of collection that will be created.
 */
public abstract class AbstractCollectionFormInjector<T> extends PrefixedFormInjector
{

   private final Class collectionType;

   private final Pattern pattern;

   /**
    * Creates an injector to inject a collection.
    *
    * @param collectionType The type of collection to return.
    * @param genericType    The type of elements in the collection.
    * @param prefix prefix
    * @param pattern        The pattern that a field name should follow to be a part of this collection. The first group in the pattern must be the index.
    * @param factory provider factory
    */
   protected AbstractCollectionFormInjector(Class collectionType, Class genericType, String prefix, Pattern pattern, ResteasyProviderFactory factory)
   {
      super(genericType, prefix, factory);
      this.collectionType = collectionType;
      this.pattern = pattern;
   }

   /**
    * {@inheritDoc} Creates a collection instance and fills it with content by using the super implementation.
    */
   @Override
   public CompletionStage<Object> inject(HttpRequest request, HttpResponse response, boolean unwrapAsync)
   {
      T result = createInstance(collectionType);
      CompletionStage<Void> ret = CompletableFuture.completedFuture(null);
      for (String collectionPrefix : findMatchingPrefixesWithNoneEmptyValues(request.getDecodedFormParameters()))
      {
         Matcher matcher = pattern.matcher(collectionPrefix);
         matcher.matches();
         String key = matcher.group(1);
         ret = ret.thenCompose(v -> super.doInject(collectionPrefix, request, response, unwrapAsync)
               .thenAccept(value -> {
                  addTo(result, key, value);
               }));
      }
      return ret.thenApply(v -> result);
   }

   /**
    * Finds all field names that follow the pattern.
    * @param parameters parameters map
    * @return set of parameter names
    */
   private Set<String> findMatchingPrefixesWithNoneEmptyValues(MultivaluedMap<String, String> parameters)
   {
      final HashSet<String> result = new HashSet<String>();
      for (String parameterName : parameters.keySet())
      {
         final Matcher matcher = pattern.matcher(parameterName);
         if (matcher.lookingAt() && hasValue(parameters.get(parameterName)))
         {
            result.add(matcher.group(0));
         }
      }
      return result;
   }

   /**
    * Creates an instance of the collection type.
    * @param collectionType collection type
    * @return object instance of type T 
    */
   protected abstract T createInstance(Class collectionType);

   /**
    * Adds the item to the collection.
    * @param collection collection
    * @param key key
    * @param value value
    */
   protected abstract void addTo(T collection, String key, Object value);
}