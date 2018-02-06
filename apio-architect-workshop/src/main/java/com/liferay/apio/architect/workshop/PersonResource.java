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

import com.liferay.apio.architect.functional.Try;
import com.liferay.apio.architect.pagination.PageItems;
import com.liferay.apio.architect.pagination.Pagination;
import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.resource.CollectionResource;
import com.liferay.apio.architect.routes.CollectionRoutes;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserService;

import java.util.List;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.ServerErrorException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class PersonResource
	implements CollectionResource<User, Long, PersonId> {

	@Override
	public CollectionRoutes<User> collectionRoutes(
		CollectionRoutes.Builder<User> builder) {

		return builder.addGetter(
			this::_getPageItems, Company.class
		).build();
	}

	@Override
	public String getName() {
		return "people";
	}

	@Override
	public ItemRoutes<User> itemRoutes(ItemRoutes.Builder<User, Long> builder) {
		return builder.addGetter(
			this::_getUser
		).build();
	}

	@Override
	public Representor<User, Long> representor(
		Representor.Builder<User, Long> representorBuilder) {

		return representorBuilder.types(
			"Person"
		).identifier(
			User::getUserId
		).addDate(
			"birthDate",
			user -> Try.fromFallible(
				user::getBirthday
			).orElse(
				null
			)
		).addString(
			"additionalName", User::getMiddleName
		).addString(
			"alternateName", User::getScreenName
		).addString(
			"email", User::getEmailAddress
		).addString(
			"familyName", User::getLastName
		).addString(
			"gender",
			user -> Try.fromFallible(
				user::isMale
			).map(
				male -> male ? "male" : "female"
			).orElse(
				null
			)
		).addString(
			"givenName", User::getFirstName
		).addString(
			"jobTitle", User::getJobTitle
		).addString(
			"name", User::getFullName
		).build();
	}

	private PageItems<User> _getPageItems(
		Pagination pagination, Company company) {

		List<User> users;

		try {
			users = _userService.getCompanyUsers(
				company.getCompanyId(), pagination.getStartPosition(),
				pagination.getEndPosition());
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}

		int count = _userLocalService.getCompanyUsersCount(
			company.getCompanyId());

		return new PageItems<>(users, count);
	}

	private User _getUser(Long userId) {
		try {
			return _userLocalService.getUserById(userId);
		}
		catch (NoSuchUserException | PrincipalException e) {
			throw new NotFoundException("Unable to get user " + userId, e);
		}
		catch (PortalException pe) {
			throw new ServerErrorException(500, pe);
		}
	}

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private UserService _userService;

}