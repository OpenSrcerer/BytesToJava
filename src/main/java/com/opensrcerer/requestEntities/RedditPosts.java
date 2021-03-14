package com.opensrcerer.requestEntities;

import java.util.ArrayList;
import java.util.Collection;

/**
 * BTJReturnable ArrayList to encapsulate RedditPost-s. Immutable.
 */
public class RedditPosts extends ArrayList<RedditPost> implements BTJReturnable {
    public RedditPosts(Collection<? extends RedditPost> c) {
        super(c);
    }

    @Override
    public boolean add(RedditPost e) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This list is immutable.");
    }

    @Override
    public void add(int index, RedditPost e) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This list is immutable.");
    }

    @Override
    public boolean addAll(Collection<? extends RedditPost> c)  {
        throw new UnsupportedOperationException("This list is immutable.");
    }

    @Override
    public boolean addAll(int index, Collection<? extends RedditPost> c) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This list is immutable.");
    }

    @Override
    public RedditPost remove(int index) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This list is immutable.");
    }
}
