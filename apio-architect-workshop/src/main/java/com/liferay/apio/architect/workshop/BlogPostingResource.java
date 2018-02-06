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

import com.liferay.apio.architect.representor.Representor;
import com.liferay.apio.architect.resource.CollectionResource;
import com.liferay.apio.architect.routes.CollectionRoutes;
import com.liferay.apio.architect.routes.ItemRoutes;
import com.liferay.blogs.kernel.model.BlogsEntry;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Hernández
 */
@Component(immediate = true)
public class BlogPostingResource
	implements CollectionResource<BlogsEntry, Long, BlogPostingId> {

	@Override
	public CollectionRoutes<BlogsEntry> collectionRoutes(
		CollectionRoutes.Builder<BlogsEntry> builder) {

		return null;
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
		).build();
	}

}