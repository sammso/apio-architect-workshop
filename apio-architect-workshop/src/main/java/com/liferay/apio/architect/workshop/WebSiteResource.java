/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.apio.architect.workshop;

import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.resource.CollectionResource;
import com.liferay.apio.architect.routes.CollectionRoutes;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class WebSiteResource
	implements CollectionResource<Group, Long, WebSiteId> {

	@Override
	public CollectionRoutes<Group> collectionRoutes(
		CollectionRoutes.Builder<Group> builder) {

		return builder.addGetter(
			this::_getPageItems, Company.class
		).build();
	}

	@Override
	public String getName() {
		return "web-sites";
	}

	@Override
	public ItemRoutes<Group> itemRoutes(
		ItemRoutes.Builder<Group, Long> builder) {

		return builder.addGetter(
			this::_getGroup
		).build();
	}

	@Override
	public Representor<Group, Long> representor(
		Representor.Builder<Group, Long> representorBuilder) {

		return representorBuilder.types(
			"WebSite"
		).identifier(
			Group::getGroupId
		).addLocalizedString(
			"name",
			(group, language) -> group.getName(
				language.getPreferredLocale(), true)
		).addString(
			"description", Group::getDescription
		).build();
	}

	private Group _getGroup(Long groupId) {
		try {
			return _groupLocalService.getGroup(groupId);
		}
		catch (NoSuchGroupException nsge) {
			throw new NotFoundException(
				"Unable to get website " + groupId, nsge);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	private PageItems<Group> _getPageItems(
		Pagination pagination, Company company) {

		List<Group> groups = _groupLocalService.getGroups(
			company.getCompanyId(), 0, true);

		List<Group> paginatedGroups = ListUtil.subList(
			groups, pagination.getStartPosition(), pagination.getEndPosition());

		int count = _groupLocalService.getGroupsCount(
			company.getCompanyId(), 0, true);

		return new PageItems<>(paginatedGroups, count);
	}

	@Reference
	private GroupLocalService _groupLocalService;

}