package com.celements.model.util;

import static com.google.common.base.Preconditions.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.xwiki.model.EntityType;
import org.xwiki.model.reference.AttachmentReference;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.ObjectPropertyReference;
import org.xwiki.model.reference.ObjectReference;
import org.xwiki.model.reference.SpaceReference;
import org.xwiki.model.reference.WikiReference;

import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

public class EntityTypeUtil {

  private static final BiMap<Class<? extends EntityReference>, EntityType> ENTITY_TYPE_MAP;
  private static final Map<EntityType, String> REGEX_MAP;

  public static final String REGEX_WORD = "[a-zA-Z0-9_-]+";
  public static final String REGEX_WIKINAME = "[a-zA-Z0-9]+";
  public static final String REGEX_SPACE = "(" + REGEX_WIKINAME + "\\:)?" + REGEX_WORD;
  public static final String REGEX_DOC = REGEX_SPACE + "\\." + REGEX_WORD;
  public static final String REGEX_ATT = REGEX_DOC + "\\@" + ".+";

  static {
    Map<Class<? extends EntityReference>, EntityType> map = new HashMap<>();
    map.put(WikiReference.class, EntityType.WIKI);
    map.put(SpaceReference.class, EntityType.SPACE);
    map.put(DocumentReference.class, EntityType.DOCUMENT);
    map.put(AttachmentReference.class, EntityType.ATTACHMENT);
    map.put(ObjectReference.class, EntityType.OBJECT);
    map.put(ObjectPropertyReference.class, EntityType.OBJECT_PROPERTY);
    ENTITY_TYPE_MAP = ImmutableBiMap.copyOf(map);
    Map<EntityType, String> regexMap = new LinkedHashMap<>(); // keeps insertion order
    regexMap.put(EntityType.WIKI, REGEX_WIKINAME);
    regexMap.put(EntityType.SPACE, REGEX_SPACE);
    regexMap.put(EntityType.DOCUMENT, REGEX_DOC);
    regexMap.put(EntityType.ATTACHMENT, REGEX_ATT);
    REGEX_MAP = Collections.unmodifiableMap(regexMap);
  }

  @NotNull
  public static Optional<EntityType> getEntityTypeForClass(
      @NotNull Class<? extends EntityReference> token) {
    return Optional.fromNullable(ENTITY_TYPE_MAP.get(checkNotNull(token)));
  }

  @NotNull
  public static EntityType getEntityTypeForClassOrThrow(
      @NotNull Class<? extends EntityReference> token) {
    Optional<EntityType> type = getEntityTypeForClass(token);
    if (!type.isPresent()) {
      throw new IllegalArgumentException("No entity type for class: " + token);
    }
    return type.get();
  }

  @NotNull
  public static Class<? extends EntityReference> getClassForEntityType(@NotNull EntityType type) {
    Class<? extends EntityReference> token = ENTITY_TYPE_MAP.inverse().get(checkNotNull(type));
    if (token != null) {
      return token;
    } else {
      throw new IllegalStateException("No class for entity type: " + type);
    }
  }

  @NotNull
  public static EntityType getRootEntityType() {
    return EntityType.values()[0];
  }

  @NotNull
  public static EntityType getLastEntityType() {
    return EntityType.values()[EntityType.values().length - 1];
  }

  /**
   * @return the class for the root entity type
   */
  @NotNull
  public static Class<? extends EntityReference> getRootClass() {
    return checkNotNull(ENTITY_TYPE_MAP.inverse().get(getRootEntityType()));
  }

  /**
   * identifies the reference class for the given absolute name (root type may be missing).<br>
   * <br>
   * simple names default to the root entity type.
   *
   * @param name
   *          the string representation
   * @return the identified reference class
   * @throws IllegalArgumentException
   *           for illegal strings
   */
  @NotNull
  public static Optional<EntityType> identifyEntityTypeFromName(@NotNull String name) {
    if (!checkNotNull(name).isEmpty()) {
      for (EntityType type : REGEX_MAP.keySet()) {
        if (name.matches(REGEX_MAP.get(type))) {
          return Optional.of(type);
        }
      }
    }
    return Optional.absent();
  }

  @NotNull
  public static Iterator<EntityType> createIterator() {
    return createIteratorAt(null);
  }

  @NotNull
  public static Iterator<EntityType> createIteratorFrom(@Nullable final EntityType startType) {
    Iterator<EntityType> ret = createIteratorAt(checkNotNull(startType));
    if ((startType != null) && ret.hasNext()) {
      ret.next();
    }
    return ret;
  }

  @NotNull
  public static Iterator<EntityType> createIteratorAt(@Nullable final EntityType startType) {
    return new Iterator<EntityType>() {

      private int ordinal = MoreObjects.firstNonNull(startType, getLastEntityType()).ordinal();

      @Override
      public boolean hasNext() {
        return ordinal >= 0;
      }

      @Override
      public EntityType next() {
        EntityType ret = EntityType.values()[ordinal];
        decrease();
        return ret;
      }

      private int decrease() {
        // skip type attachment for type OBJECT or OBJECT_PROPERTY
        return ordinal -= ((ordinal == EntityType.OBJECT.ordinal()) ? 2 : 1);
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

}