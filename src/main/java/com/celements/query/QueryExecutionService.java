package com.celements.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.Requirement;
import org.xwiki.context.Execution;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.WikiReference;
import org.xwiki.query.Query;
import org.xwiki.query.QueryException;

import com.celements.web.service.IWebUtilsService;
import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.store.XWikiHibernateBaseStore.HibernateCallback;
import com.xpn.xwiki.store.XWikiHibernateStore;

@Component
public class QueryExecutionService implements IQueryExecutionServiceRole {

  private static final Logger LOGGER = LoggerFactory.getLogger(
      QueryExecutionService.class);

  @Requirement
  private IWebUtilsService webUtilsService;

  @Requirement
  private Execution execution;

  private XWikiContext getContext() {
    return (XWikiContext) execution.getContext().getProperty("xwikicontext");
  }

  @Override
  public int executeWriteSQL(String sql) throws XWikiException {
    return executeWriteSQLs(Arrays.asList(sql)).get(0);
  }

  @Override
  public List<Integer> executeWriteSQLs(List<String> sqls) throws XWikiException {
    List<Integer> results = new ArrayList<Integer>();
    Session session = null;
    try {
      session = getNewHibSession();
      for (String sql : sqls) {
        results.add(executeWriteSQL(session, sql));
      }
    } finally {
      if (session != null) {
        session.close();
      }
    }
    return Collections.unmodifiableList(results);
  }

  private Session getNewHibSession() throws XWikiException {
    Session session = getHibStore().getSessionFactory().openSession();
    getHibStore().setDatabase(session, getContext());
    return session;
  }

  private int executeWriteSQL(Session session, String sql) {
    int result = -1;
    Transaction transaction = session.beginTransaction();
    try {
      result = session.createSQLQuery(sql).executeUpdate();
    } catch (HibernateException hibExc) {
      LOGGER.debug("error while executing sql '{}'", sql, hibExc);
    } finally {
      if (result > -1) {
        transaction.commit();
      } else {
        transaction.rollback();
      }
    }
    LOGGER.info("executing sql '{}' for db '{}' returned '{}'", sql, getContext(
        ).getDatabase(), result);
    return result;
  }

  @Override
  public int executeWriteHQL(String hql, Map<String, Object> binds) throws XWikiException {
    return executeWriteHQL(hql, binds, null);
  }

  @Override
  public int executeWriteHQL(String hql, Map<String, Object> binds, WikiReference wikiRef
      ) throws XWikiException {
    WikiReference curWikiRef = webUtilsService.getWikiRef();
    try {
      getContext().setDatabase(webUtilsService.getWikiRef(wikiRef).getName());
      HibernateCallback<Integer> callback = new ExecuteWriteCallback(hql, binds);
      return getHibStore().executeWrite(getContext(), true, callback);
    } finally {
      getContext().setDatabase(curWikiRef.getName());
    }
  }

  @Override
  public DocumentReference executeAndGetDocRef(Query query) throws QueryException {
    DocumentReference ret = null;
    List<DocumentReference> list = executeAndGetDocRefs(query);
    if (list.size() > 0) {
      ret = list.get(0);
    }
    return ret;
  }

  @Override
  public List<DocumentReference> executeAndGetDocRefs(Query query) throws QueryException {
    List<DocumentReference> ret = new ArrayList<DocumentReference>();
    WikiReference wikiRef = webUtilsService.getWikiRef();
    if (StringUtils.isNotBlank(query.getWiki())) {
      wikiRef = new WikiReference(query.getWiki());
    }
    for (Object fullName : query.execute()) {
      if ((fullName instanceof String) && StringUtils.isNotBlank((String) fullName)) {
        ret.add(webUtilsService.resolveDocumentReference((String) fullName, wikiRef));
      } else {
        LOGGER.debug("executeAndGetDocRefs: received invalid fullName '{}'", fullName);
      }
    }
    LOGGER.info("executeAndGetDocRefs: {} results for query '{}' and wiki '{}'", 
        ret.size(), query.getStatement(), wikiRef);
    return ret;
  }

  private XWikiHibernateStore getHibStore() {
    return getContext().getWiki().getHibernateStore();
  }

}
