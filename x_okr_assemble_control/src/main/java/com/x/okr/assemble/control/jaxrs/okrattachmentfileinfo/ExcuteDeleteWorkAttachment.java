package com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo;

import javax.servlet.http.HttpServletRequest;

import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.entity.annotation.CheckRemoveType;
import com.x.base.core.http.ActionResult;
import com.x.base.core.http.EffectivePerson;
import com.x.base.core.http.WrapOutId;
import com.x.base.core.logger.Logger;
import com.x.base.core.logger.LoggerFactory;
import com.x.base.core.project.server.StorageMapping;
import com.x.okr.assemble.control.OkrUserCache;
import com.x.okr.assemble.control.ThisApplication;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.AttachmentDeleteException;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.AttachmentIdEmptyException;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.AttachmentNotExistsException;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.AttachmentQueryByIdException;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.GetOkrUserCacheException;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.InsufficientPermissionsException;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.UserNoLoginException;
import com.x.okr.assemble.control.jaxrs.okrattachmentfileinfo.exception.WorkQueryByIdException;
import com.x.okr.entity.OkrAttachmentFileInfo;
import com.x.okr.entity.OkrWorkBaseInfo;

public class ExcuteDeleteWorkAttachment extends ExcuteBase {

	private Logger logger = LoggerFactory.getLogger(ExcuteDeleteWorkAttachment.class);

	protected ActionResult<WrapOutId> execute(HttpServletRequest request, EffectivePerson effectivePerson, String id)
			throws Exception {
		ActionResult<WrapOutId> result = new ActionResult<>();
		OkrAttachmentFileInfo okrAttachmentFileInfo = null;
		OkrWorkBaseInfo okrWorkBaseInfo = null;
		StorageMapping mapping = null;
		boolean hasDeletePermission = false;
		Boolean check = true;
		OkrUserCache okrUserCache = null;
		try {
			okrUserCache = okrUserInfoService.getOkrUserCacheWithPersonName(effectivePerson.getName());
		} catch (Exception e1) {
			check = false;
			Exception exception = new GetOkrUserCacheException(e1, effectivePerson.getName());
			result.error(exception);
			logger.error(exception, effectivePerson, request, null);
		}

		if (check && (okrUserCache == null || okrUserCache.getLoginIdentityName() == null)) {
			check = false;
			Exception exception = new UserNoLoginException(effectivePerson.getName());
			result.error(exception);
			logger.error(exception, effectivePerson, request, null);
		}
		if (id == null || id.isEmpty()) {
			check = false;
			Exception exception = new AttachmentIdEmptyException();
			result.error(exception);
			logger.error(exception, effectivePerson, request, null);
		}
		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrAttachmentFileInfo = emc.find(id, OkrAttachmentFileInfo.class);
				if (null == okrAttachmentFileInfo) {
					check = false;
					Exception exception = new AttachmentNotExistsException(id);
					result.error(exception);
					logger.error(exception, effectivePerson, request, null);
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new AttachmentQueryByIdException(e, id);
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}
		if (check) {
			try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
				okrWorkBaseInfo = emc.find(okrAttachmentFileInfo.getKey(), OkrWorkBaseInfo.class);
				if (null == okrWorkBaseInfo) {
					hasDeletePermission = true;
					logger.warn("okrWorkBaseInfo{id:" + okrAttachmentFileInfo.getKey()
							+ "} is not exists, anyone can delete the attachments.");
				} else {
					// 根据工作信息查询工作信息的干系人信息，判断是否有权限删除附件信息。
					// 判断是否有权限删除附件
					if (!okrWorkBaseInfo.getDeployerName().equalsIgnoreCase(okrUserCache.getLoginUserName())) {
						hasDeletePermission = false;
					} else {
						hasDeletePermission = true;
					}
				}
			} catch (Exception e) {
				check = false;
				Exception exception = new WorkQueryByIdException(e, okrAttachmentFileInfo.getKey());
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}
		if (check) {
			if (hasDeletePermission) {
				if (okrAttachmentFileInfo != null) {
					try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
						mapping = ThisApplication.context().storageMappings().get(OkrAttachmentFileInfo.class,
								okrAttachmentFileInfo.getStorage());
						// 对文件进行删除
						okrAttachmentFileInfo.deleteContent(mapping);
						// 对数据库记录进行删除
						okrAttachmentFileInfo = emc.find(id, OkrAttachmentFileInfo.class);
						okrWorkBaseInfo = emc.find(okrAttachmentFileInfo.getWorkInfoId(), OkrWorkBaseInfo.class);
						emc.beginTransaction(OkrAttachmentFileInfo.class);
						emc.beginTransaction(OkrWorkBaseInfo.class);
						if (okrWorkBaseInfo != null && okrWorkBaseInfo.getAttachmentList() != null) {
							okrWorkBaseInfo.getAttachmentList().remove(okrAttachmentFileInfo.getId());
							emc.check(okrWorkBaseInfo, CheckPersistType.all);
						}
						emc.remove(okrAttachmentFileInfo, CheckRemoveType.all);
						emc.commit();
						result.setData(new WrapOutId(id));
					} catch (Exception e) {
						check = false;
						Exception exception = new AttachmentDeleteException(e, okrAttachmentFileInfo.getId());
						result.error(exception);
						logger.error(exception, effectivePerson, request, null);
					}
				}
			} else {
				Exception exception = new InsufficientPermissionsException(effectivePerson.getName(),
						okrAttachmentFileInfo.getId());
				result.error(exception);
				logger.error(exception, effectivePerson, request, null);
			}
		}
		return result;
	}

}