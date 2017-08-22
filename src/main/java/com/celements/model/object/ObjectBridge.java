package com.celements.model.object;

import java.util.List;

import javax.annotation.concurrent.Immutable;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;

import org.xwiki.component.annotation.ComponentRole;
import org.xwiki.model.reference.DocumentReference;

import com.celements.model.classes.ClassIdentity;
import com.celements.model.field.FieldAccessor;

/**
 * Bridge for effective access on document and objects, primarily used by {@link ObjectHandler}s to
 * allow generic implementations
 *
 * @param <D>
 *          document type
 * @param <O>
 *          object type
 */
@Immutable
@Singleton
@ComponentRole
public interface ObjectBridge<D, O> {

  @NotNull
  Class<D> getDocumentType();

  @NotNull
  Class<O> getObjectType();

  void checkDoc(@NotNull D doc) throws IllegalArgumentException;

  @NotNull
  DocumentReference getDocRef(@NotNull D doc);

  @NotNull
  List<? extends ClassIdentity> getDocClasses(@NotNull D doc);

  @NotNull
  List<O> getObjects(@NotNull D doc, @NotNull ClassIdentity classId);

  int getObjectNumber(@NotNull O obj);

  @NotNull
  ClassIdentity getObjectClass(@NotNull O obj);

  @NotNull
  O cloneObject(@NotNull O obj);

  @NotNull
  O createObject(@NotNull D doc, @NotNull ClassIdentity classId);

  boolean deleteObject(@NotNull D doc, @NotNull O obj);

  @NotNull
  FieldAccessor<O> getFieldAccessor();

}