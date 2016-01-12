package com.celements.rights.access;

import org.apache.commons.lang.RandomStringUtils;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.SpaceReference;

import com.celements.model.access.IModelAccessFacade;

@Component
public class EntityReferenceRandomCompleter
  implements IEntityReferenceRandomCompleterRole {

  @Requirement
  private IModelAccessFacade modelAccess;

  public EntityReference randomCompleteSpaceRef(EntityReference entityRef) {
    if (entityRef.getType() == EntityType.SPACE) {
      SpaceReference spaceRef = new SpaceReference(entityRef);
      DocumentReference randomDocRef;
      do {
        String randomDocName = RandomStringUtils.randomAlphanumeric(50);
        randomDocRef = new DocumentReference(randomDocName, spaceRef);
      } while (modelAccess.exists(randomDocRef));
      entityRef = randomDocRef;
    }
    return entityRef;
  }

}