package com.celements.model.classes;

import java.util.List;

import org.xwiki.component.annotation.ComponentRole;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.WikiReference;

import com.celements.model.classes.fields.ClassField;

@ComponentRole
public interface ClassDefinition {

  public static final String CFG_SRC_KEY = "celements.classdefinition.blacklist";

  /**
   * @return the name of the component and class definition, used for blacklisting
   */
  public String getName();

  /**
   * @return the document reference on which the class is defined, using current wiki
   */
  public DocumentReference getClassRef();

  /**
   * @param wikiRef
   * @return the document reference on which the class is defined, using given wiki
   */
  public DocumentReference getClassRef(WikiReference wikiRef);

  /**
   * @return true if the class definition is blacklisted
   */
  public boolean isBlacklisted();

  /**
   * @return true if the class is mapped internally (hibernate mapping)
   */
  public boolean isInternalMapping();

  /**
   * @return a list of all fields defining this class
   */
  public List<ClassField<?>> getFields();

}
