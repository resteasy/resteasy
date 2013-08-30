package com.restfully.shop.services;

import com.restfully.shop.domain.Product;
import com.restfully.shop.domain.Products;
import com.restfully.shop.persistence.ProductEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Transactional
public class ProductResourceBean implements ProductResource
{
   private EntityManager em;

   @PersistenceContext
   public void setEntityManager(EntityManager em)
   {
      this.em = em;
   }

   public Response createProduct(Product product, UriInfo uriInfo)
   {
      ProductEntity entity = new ProductEntity();
      domain2entity(entity, product);
      em.persist(entity);
      em.flush();

      System.out.println("Created product " + entity.getId());
      UriBuilder builder = uriInfo.getAbsolutePathBuilder();
      builder.path(Integer.toString(entity.getId()));
      return Response.created(builder.build()).build();

   }

   public static void domain2entity(ProductEntity entity, Product product)
   {
      entity.setId(product.getId());
      entity.setCost(product.getCost());
      entity.setName(product.getName());
   }

   public static Product entity2domain(ProductEntity entity)
   {
      Product product = new Product();
      product.setId(entity.getId());
      product.setCost(entity.getCost());
      product.setName(entity.getName());
      return product;
   }

   public Products getProducts(int start,
                               int size,
                               String name,
                               UriInfo uriInfo)
   {
      UriBuilder builder = uriInfo.getAbsolutePathBuilder();
      builder.queryParam("start", "{start}");
      builder.queryParam("size", "{size}");

      ArrayList<Product> list = new ArrayList<Product>();
      ArrayList<Link> links = new ArrayList<Link>();

      Query query = null;
      if (name != null)
      {
         query = em.createQuery("select p from Product p where p.name=:name");
         query.setParameter("name", name);

      }
      else
      {
         query = em.createQuery("select p from Product p");
      }


      List productEntities = query.setFirstResult(start)
              .setMaxResults(size)
              .getResultList();

      for (Object obj : productEntities)
      {
         ProductEntity entity = (ProductEntity) obj;
         list.add(entity2domain(entity));
      }
      // next link
      // If the size returned is equal then assume there is a next
      if (productEntities.size() == size)
      {
         int next = start + size;
         URI nextUri = builder.clone().build(next, size);
         Link nextLink = Link.fromUri(nextUri).rel("next").type("application/xml").build();
         links.add(nextLink);
      }
      // previous link
      if (start > 0)
      {
         int previous = start - size;
         if (previous < 0) previous = 0;
         URI previousUri = builder.clone().build(previous, size);
         Link previousLink = Link.fromUri(previousUri).rel("previous").type("application/xml").build();
         links.add(previousLink);
      }
      Products products = new Products();
      products.setProducts(list);
      products.setLinks(links);
      return products;
   }

   public Product getProduct(int id)
   {
      ProductEntity product = em.getReference(ProductEntity.class, id);
      return entity2domain(product);
   }

}