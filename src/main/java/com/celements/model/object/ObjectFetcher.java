package com.celements.model.object;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.xwiki.model.reference.DocumentReference;

import com.celements.model.classes.ClassIdentity;
import com.celements.model.object.restriction.ObjectQuery;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;

/**
 * Fetches objects O on a document D for the defined query. Returned objects are intended for
 * read-only operations. Use {@link ObjectEditor} instead for manipulations.
 *
 * @param <D>
 *          document type
 * @param <O>
 *          object type
 */
public interface ObjectFetcher<D, O> {

  @NotNull
  DocumentReference getDocRef();

  /**
   * @return clone of the current query
   */
  @NotNull
  ObjectQuery<O> getQuery();

  /**
   * @return amount of fetched objects
   */
  int count();

  /**
   * @return the first fetched object
   */
  @NotNull
  Optional<O> first();

  /**
   * @return a {@link List} of all fetched objects
   */
  @NotNull
  List<O> list();

  /**
   * @return an {@link Iterable} for all fetched objects
   */
  @NotNull
  FluentIterable<O> iter();

  /**
   * @return a {@link Map} of all fetched objects indexed by their {@link ClassIdentity}
   */
  @NotNull
  Map<ClassIdentity, List<O>> map();

}