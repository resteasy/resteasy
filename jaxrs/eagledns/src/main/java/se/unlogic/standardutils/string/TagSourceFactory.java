package se.unlogic.standardutils.string;

import java.util.Set;

public interface TagSourceFactory<T> {

	public <X extends T> TagSource getTagSource(X bean);

	public Set<String> getTagsSet();

	/**
	 * @return a comma separated {@link String} of all available tags
	 */
	public String getAvailableTags();
}
