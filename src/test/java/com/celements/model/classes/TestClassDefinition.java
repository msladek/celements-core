package com.celements.model.classes;

import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;

import com.celements.model.classes.fields.BooleanField;
import com.celements.model.classes.fields.ClassField;
import com.celements.model.classes.fields.StringField;
import com.celements.model.classes.fields.number.IntField;
import com.celements.model.classes.fields.ref.DocumentReferenceField;

@Singleton
@Component(TestClassDefinition.NAME)
public class TestClassDefinition extends AbstractClassDefinition implements
    TestClassDefinitionRole {

  public static final String NAME = "Test.TestClass";

  public static final DocumentReference CLASS_REF = new DocumentReference("db", "classes", "test");

  public static final ClassField<String> FIELD_MY_STRING = getFieldMyString();
  public static final ClassField<Integer> FIELD_MY_INT = getFieldMyInt();
  public static final ClassField<Boolean> FIELD_MY_BOOL = getFieldMyBool();
  public static final ClassField<DocumentReference> FIELD_MY_DOCREF = getFieldMyDocRef();

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public DocumentReference getClassRef() {
    return CLASS_REF;
  }

  @Override
  public boolean isInternalMapping() {
    return true;
  }

  private static ClassField<String> getFieldMyString() {
    return new StringField.Builder(CLASS_REF, "myString").size(30).build();
  }

  private static ClassField<Integer> getFieldMyInt() {
    return new IntField.Builder(CLASS_REF, "myInt").size(30).build();
  }

  private static ClassField<Boolean> getFieldMyBool() {
    return new BooleanField.Builder(CLASS_REF, "myBool").displayType("asdf").build();
  }

  private static ClassField<DocumentReference> getFieldMyDocRef() {
    return new DocumentReferenceField.Builder(CLASS_REF, "myDocRef").size(30).build();
  }

}
