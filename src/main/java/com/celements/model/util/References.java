package com.celements.model.util;

import static com.google.common.base.Preconditions.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

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

import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

public class References {

  private static final BiMap<Class<? extends EntityReference>, EntityType> ENTITY_TYPE_MAP;
  private static final Map<Class<? extends EntityReference>, String> REGEX_MAP;

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
    Map<Class<? extends EntityReference>, String> regexMap = new LinkedHashMap<>();
    regexMap.put(WikiReference.class, REGEX_WIKINAME);
    regexMap.put(SpaceReference.class, REGEX_SPACE);
    regexMap.put(DocumentReference.class, REGEX_DOC);
    regexMap.put(AttachmentReference.class, REGEX_ATT);
    REGEX_MAP = Collections.unmodifiableMap(regexMap);
  }

  public static EntityType getEntityTypeForClass(Class<? extends EntityReference> token) {
    EntityType type = ENTITY_TYPE_MAP.get(checkNotNull(token));
    if (type != null) {
      return type;
    } else {
      throw new IllegalArgumentException("No entity type for class: " + token);
    }
  }

  public static Class<? extends EntityReference> getClassForEntityType(EntityType type) {
    Class<? extends EntityReference> token = ENTITY_TYPE_MAP.inverse().get(checkNotNull(type));
    if (token != null) {
      return token;
    } else {
      throw new IllegalArgumentException("No class for entity type: " + type);
    }
  }

  private static EntityType getRootEntityType() {
    return EntityType.values()[0]; // EntityType.WIKI
  }

  /**
   * @return the class for the root entity type
   */
  public static Class<? extends EntityReference> getRootClass() {
    return ENTITY_TYPE_MAP.inverse().get(getRootEntityType());
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
  public static Class<? extends EntityReference> identifyClassFromName(@NotNull String name) {
    if (!checkNotNull(name).isEmpty()) {
      Set<Class<? extends EntityReference>> tokens = new LinkedHashSet<>(); // keeps insertion order
      tokens.add(getRootClass());
      tokens.addAll(REGEX_MAP.keySet());
      for (Class<? extends EntityReference> token : tokens) {
        if (name.matches(REGEX_MAP.get(token))) {
          return token;
        }
      }
    }
    throw new IllegalArgumentException("No valid reference class found for '" + name + "'");
  }

  /**
   * @param ref
   * @return false if the given reference is relative
   */
  public static boolean isAbsoluteRef(@NotNull EntityReference ref) {
    checkNotNull(ref);
    return ref.extractReference(getRootEntityType()) != null;
  }

  /**
   * @param ref
   *          the reference to be cloned
   * @return a cloned instance of the reference
   */
  @NotNull
  public static EntityReference cloneRef(@NotNull EntityReference ref) {
    Class<? extends EntityReference> token = EntityReference.class;
    if (isAbsoluteRef(ref)) {
      token = ENTITY_TYPE_MAP.inverse().get(ref.getType());
    }
    return cloneRef(ref, token);
  }

  /**
   * @param ref
   *          the reference to be cloned
   * @param token
   *          type of the reference
   * @return a cloned instance of the reference of type T
   * @throws IllegalArgumentException
   *           when relative references are being cloned as subtypes of {@link EntityReference}
   */
  @NotNull
  public static <T extends EntityReference> T cloneRef(@NotNull EntityReference ref,
      @NotNull Class<T> token) {
    checkNotNull(ref);
    checkNotNull(token);
    try {
      ref = ref.clone();
      T ret;
      if (token == EntityReference.class) {
        ret = token.cast(ref);
      } else if (isAbsoluteRef(ref)) {
        ret = token.getConstructor(EntityReference.class).newInstance(ref);
      } else {
        throw new IllegalArgumentException("Relative references can only be returned as "
            + "EntityReference");
      }
      return ret;
    } catch (ReflectiveOperationException | SecurityException exc) {
      throw new IllegalArgumentException("Unsupported entity class: " + token, exc);
    }
  }

  /**
   * @param fromRef
   *          the reference to extract from
   * @param token
   *          reference class to extract
   * @return optional of the extracted reference
   */
  public static <T extends EntityReference> Optional<T> extractRef(
      @Nullable EntityReference fromRef, @NotNull Class<T> token) {
    EntityReference extractedRef = null;
    if (fromRef != null) {
      extractedRef = fromRef.extractReference(getEntityTypeForClass(token));
    }
    if (extractedRef != null) {
      return Optional.of(cloneRef(extractedRef, token));
    }
    return Optional.absent();
  }

  /**
   * adjust a reference to another one of higher order, e.g. a docRef to another wikiRef.
   *
   * @param ref
   *          to be adjusted
   * @param token
   *          for the reference type
   * @param toRef
   *          it is adjusted to
   * @return a new instance of the adjusted reference or ref if toRef was of lower order
   */
  @NotNull
  public static <T extends EntityReference> T adjustRef(@NotNull T ref, @NotNull Class<T> token,
      @Nullable EntityReference toRef) {
    checkNotNull(toRef);
    EntityReference adjustedRef = cloneRef(ref); // avoid modifying argument
    EntityReference current = adjustedRef;
    while (current != null) {
      if (current.getType() != toRef.getType()) {
        current = current.getParent();
      } else {
        if (current.getChild() != null) {
          current.getChild().setParent(toRef);
        } else {
          adjustedRef = toRef;
        }
        break;
      }
    }
    return cloneRef(adjustedRef, token); // effective immutability
  }

}