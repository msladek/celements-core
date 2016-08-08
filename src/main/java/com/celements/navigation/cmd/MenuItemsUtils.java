package com.celements.navigation.cmd;

import javax.validation.constraints.NotNull;

import org.xwiki.model.reference.EntityReference;

import com.celements.model.util.IModelUtils;
import com.xpn.xwiki.web.Utils;

public class MenuItemsUtils {

  private MenuItemsUtils() {
  }

  public static EntityReference resolveParentRef(@NotNull String parentFN) {
    return (parentFN.isEmpty()) ? null : Utils.getComponent(IModelUtils.class).resolveRef(parentFN);
  }

}
