package com.celements.model.classes.fields;

import static org.junit.Assert.*;
import static org.mutabilitydetector.unittesting.MutabilityAssert.*;

import org.junit.Before;
import org.junit.Test;
import org.xwiki.model.reference.DocumentReference;

import com.celements.common.test.AbstractComponentTest;
import com.xpn.xwiki.objects.classes.BooleanClass;

public class BooleanFieldTest extends AbstractComponentTest {

  private BooleanField field;

  String displayType = "displayType";
  Integer defaultValue = 5;

  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    field = new BooleanField.Builder(new DocumentReference("wiki", "class", "any"),
        "name").displayType(displayType).defaultValue(defaultValue).build();
  }

  @Test
  public void test_immutability() {
    assertImmutable(BooleanField.class);
  }

  @Test
  public void test_getters() throws Exception {
    assertEquals(displayType, field.getDisplayType());
    assertEquals(defaultValue, field.getDefaultValue());
  }

  @Test
  public void test_getXField() throws Exception {
    assertTrue(field.getXField() instanceof BooleanClass);
    BooleanClass xField = (BooleanClass) field.getXField();
    assertEquals(field.getName(), xField.getName());
    assertEquals(displayType, xField.getDisplayType());
    assertEquals(defaultValue, (Integer) xField.getDefaultValue());
  }
}
