package org.jboss.resteasy.links.test;

import org.jboss.resteasy.links.AddLinks;
import org.jboss.resteasy.links.LinkResource;
import org.jboss.resteasy.links.LinkResources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
public class BookStoreMinimal {

	private Map<String,Book> books = new HashMap<String,Book>();

	{
		Book book = new Book("foo", "bar");
		book.addComment(0, "great book");
		book.addComment(1, "terrible book");
		books.put(book.getTitle(), book);
	}

	@Produces({"application/xml", "application/json"})
	@AddLinks
	@LinkResource(value = Book.class)
	@GET
	@Path("books")
	public Collection<Book> getBooks(){
		return books.values();
	}

	@Consumes({"application/xml", "application/json"})
	@LinkResource()
	@POST
	@Path("books")
	public void addBook(Book book){
		books.put(book.getTitle(), book);
	}

	@Produces({"application/xml", "application/json"})
	@AddLinks
	@LinkResource()
	@GET
	@Path("book/{id}")
	public Book getBook(@PathParam("id") String id){
		return books.get(id);
	}

	@Consumes({"application/xml", "application/json"})
	@LinkResource()
	@PUT
	@Path("book/{id}")
	public void updateBook(@PathParam("id") String id, Book book){
		books.put(id, book);
	}

	@LinkResource(value = Book.class)
	@DELETE
	@Path("book/{id}")
	public void deleteBook(@PathParam("id") String id){
		books.remove(id);
	}

	// comments

	@Produces({"application/xml", "application/json"})
	@AddLinks
	@LinkResources({
		@LinkResource(value = Book.class, rel = "comments"),
		@LinkResource(value = Comment.class)
	})
	@GET
	@Path("book/{id}/comments")
	public Collection<Comment> getComments(@PathParam("id") String bookId){
		return books.get(bookId).getComments();
	}

	@Produces({"application/xml", "application/json"})
	@AddLinks
	@LinkResources({
		@LinkResource(value = Book.class, rel="comment-collection"),
		@LinkResource(value = Comment.class, rel="collection")
	})
	@GET
	@Path("book/{id}/comment-collection")
	public ScrollableCollection getScrollableComments(@PathParam("id") String id){
		List<Comment> comments = books.get(id).getComments();
		return new ScrollableCollection(id, 0, comments.size(), comments);
	}

	@Produces({"application/xml", "application/json"})
	@AddLinks
	@LinkResource()
	@GET
	@Path("book/{id}/comment/{cid}")
	public Comment getComment(@PathParam("id") String bookId, @PathParam("cid") int commentId){
		return books.get(bookId).getComment(commentId);
	}

	@Consumes({"application/xml", "application/json"})
	@LinkResource()
	@POST
	@Path("book/{id}/comments")
	public void addComment(@PathParam("id") String bookId, Comment comment){
		books.get(bookId).getComments().add(comment);
	}

	@Consumes({"application/xml", "application/json"})
	@LinkResource()
	@PUT
	@Path("book/{id}/comment/{cid}")
	public void updateComment(@PathParam("id") String bookId, @PathParam("cid") int commentId, Comment comment){
		books.get(bookId).getComment(commentId).setText(comment.getText());
	}

	@LinkResource(Comment.class)
	@DELETE
	@Path("book/{id}/comment/{cid}")
	public void deleteComment(@PathParam("id") String bookId, @PathParam("cid") int commentId){
		Book book = books.get(bookId);
		Comment c = book.getComment(commentId);
		book.getComments().remove(c);
	}

}
