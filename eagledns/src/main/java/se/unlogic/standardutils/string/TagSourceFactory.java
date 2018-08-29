package se.unlogic.standardutils.string;

import java.util.Set;

public interface TagSourceFactory<T> {

	<X extends T> TagSource getTagSource(X bean);

	Set<String> getTagsSet();

	/**
	 * @return a comma separated {@link String} of all available tags
	 */
	String getAvailableTags();
}
