package com.celements.model.context;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.context.Execution;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.model.reference.SpaceReference;
import org.xwiki.model.reference.WikiReference;

import com.celements.configuration.CelementsFromWikiConfigurationSource;
import com.celements.model.access.IModelAccessFacade;
import com.celements.model.access.exception.DocumentNotExistsException;
import com.celements.model.util.ModelUtils;
import com.google.common.base.Optional;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.user.api.XWikiUser;
import com.xpn.xwiki.web.Utils;

@Component
public class DefaultModelContext implements ModelContext {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultModelContext.class);

  @Requirement(CelementsFromWikiConfigurationSource.NAME)
  ConfigurationSource wikiConfigSrc;

  @Requirement
  ConfigurationSource defaultConfigSrc;

  @Requirement
  private Execution execution;

  @Override
  public XWikiContext getXWikiContext() {
    return (XWikiContext) execution.getContext().getProperty(XWikiContext.EXECUTIONCONTEXT_KEY);
  }

  @Override
  public WikiReference getWikiRef() {
    return new WikiReference(getXWikiContext().getDatabase());
  }

  @Override
  public WikiReference setWikiRef(WikiReference wikiRef) {
    WikiReference oldWiki = getWikiRef();
    getXWikiContext().setDatabase(wikiRef.getName());
    return oldWiki;
  }

  @Override
  public WikiReference getMainWikiRef() {
    return new WikiReference(getXWikiContext().getMainXWiki());
  }

  @Override
  public XWikiDocument getDoc() {
    return getXWikiContext().getDoc();
  }

  @Override
  public XWikiDocument setDoc(XWikiDocument doc) {
    XWikiDocument oldDoc = getDoc();
    getXWikiContext().setDoc(doc);
    return oldDoc;
  }

  @Override
  public XWikiUser getUser() {
    return getXWikiContext().getXWikiUser();
  }

  @Override
  public XWikiUser setUser(@Nullable XWikiUser user) {
    XWikiUser oldUser = getUser();
    if (user != null) {
      getXWikiContext().setUser(user.getUser(), user.isMain());
    } else {
      getXWikiContext().setUser(null);
    }
    return oldUser;
  }

  @Override
  public String getUserName() {
    return getXWikiContext().getUser();
  }

  @Override
  public String getDefaultLanguage() {
    return getDefaultLanguage(getWikiRef());
  }

  @Override
  public String getDefaultLanguage(EntityReference ref) {
    String ret = getDefaultLangFromDoc(ref);
    if (ret.isEmpty()) {
      ret = getDefaultLangFromConfigSrc(ref);
    }
    LOGGER.trace("getDefaultLanguage: for '{}' got lang" + " '{}'", ref, ret);
    return ret;
  }

  private String getDefaultLangFromDoc(EntityReference ref) {
    String ret = "";
    Optional<DocumentReference> docRef = getModelUtils().extractRef(ref, DocumentReference.class);
    if (docRef.isPresent()) {
      try {
        ret = getModelAccess().getDocument(docRef.get()).getDefaultLanguage();
      } catch (DocumentNotExistsException exc) {
        LOGGER.info("trying to get language for inexistent document '{}'", docRef);
      }
    }
    return ret;
  }

  private String getDefaultLangFromConfigSrc(EntityReference ref) {
    WikiReference wikiBefore = getWikiRef();
    XWikiDocument docBefore = getDoc();
    try {
      ConfigurationSource configSrc;
      setWikiRef(getModelUtils().extractRef(ref, WikiReference.class).or(getWikiRef()));
      XWikiDocument spacePrefDoc = getSpacePrefDoc(ref);
      if (spacePrefDoc != null) {
        setDoc(spacePrefDoc);
        configSrc = defaultConfigSrc; // checks space preferences
      } else {
        configSrc = wikiConfigSrc; // skips space preferences
      }
      return configSrc.getProperty(CFG_KEY_DEFAULT_LANG, FALLBACK_DEFAULT_LANG);
    } finally {
      setWikiRef(wikiBefore);
      setDoc(docBefore);
    }
  }

  private XWikiDocument getSpacePrefDoc(EntityReference ref) {
    XWikiDocument ret = null;
    Optional<SpaceReference> spaceRef = getModelUtils().extractRef(ref, SpaceReference.class);
    if (spaceRef.isPresent()) {
      try {
        ret = getModelAccess().getDocument(new DocumentReference(WEB_PREF_DOC_NAME,
            spaceRef.get()));
      } catch (DocumentNotExistsException exc) {
        LOGGER.debug("no web preferences for space '{}'", spaceRef);
      }
    }
    return ret;
  }

  private ModelUtils getModelUtils() {
    return Utils.getComponent(ModelUtils.class);
  }

  private IModelAccessFacade getModelAccess() {
    return Utils.getComponent(IModelAccessFacade.class);
  }

}