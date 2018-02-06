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
import com.liferay.blogs.kernel.model.BlogsEntry;
import com.liferay.blogs.kernel.service.BlogsEntryService;

import java.util.List;

import javax.ws.rs.NotAuthorizedException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class BlogPostingResource
	implements CollectionResource<BlogsEntry, Long, BlogPostingId> {

	@Override
	public CollectionRoutes<BlogsEntry> collectionRoutes(
		CollectionRoutes.Builder<BlogsEntry> builder) {

		return builder.addGetter(
			this::_getPageItems
		).build();
	}

	@Override
	public String getName() {
		return "blog-postings";
	}

	@Override
	public ItemRoutes<BlogsEntry> itemRoutes(
		ItemRoutes.Builder<BlogsEntry, Long> builder) {

		return null;
	}

	@Override
	public Representor<BlogsEntry, Long> representor(
		Representor.Builder<BlogsEntry, Long> representorBuilder) {

		return representorBuilder.types(
			"BlogPosting"
		).identifier(
			BlogsEntry::getEntryId
		).addDate(
			"createDate", BlogsEntry::getCreateDate
		).addDate(
			"displayDate", BlogsEntry::getDisplayDate
		).addDate(
			"modifiedDate", BlogsEntry::getModifiedDate
		).addDate(
			"publishedDate", BlogsEntry::getLastPublishDate
		).addLink(
			"license", "https://creativecommons.org/licenses/by/4.0"
		).addString(
			"alternativeHeadline", BlogsEntry::getSubtitle
		).addString(
			"articleBody", BlogsEntry::getContent
		).addString(
			"description", BlogsEntry::getDescription
		).addString(
			"fileFormat", __ -> "text/html"
		).addString(
			"headline", BlogsEntry::getTitle
		).build();
	}

	private PageItems<BlogsEntry> _getPageItems(Pagination pagination) {
		List<BlogsEntry> blogsEntries;

		try {
			blogsEntries = _blogsService.getGroupEntries(
				20143, 0, pagination.getStartPosition(),
				pagination.getEndPosition());
		}
		catch (SecurityException se) {
			throw new NotAuthorizedException(se);
		}

		int count = _blogsService.getGroupEntriesCount(20143, 0);

		return new PageItems<>(blogsEntries, count);
	}

	@Reference
	private BlogsEntryService _blogsService;

}