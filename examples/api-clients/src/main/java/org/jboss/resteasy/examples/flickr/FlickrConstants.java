package org.jboss.resteasy.examples.flickr;

public class FlickrConstants
{
    public static final String photoSearchUrl = "http://www.flickr.com/services/rest?method=flickr.photos.search&per_page=8&sort=interestingness-desc&api+key={api-key}&{type}={searchTerm}";
    public static final String photoServer = "http://static.flickr.com";
    public static final String photoPath = "/{server}/{id}_{secret}_m.jpg";
    public static final String photoUrlTemplate = photoServer + photoPath;
}
