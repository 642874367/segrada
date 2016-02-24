package org.segrada.service.repository.orientdb;

import com.google.inject.Inject;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.impl.ODocument;
import org.segrada.model.UserGroup;
import org.segrada.model.prototype.IUserGroup;
import org.segrada.service.repository.UserGroupRepository;
import org.segrada.service.repository.orientdb.base.AbstractSegradaOrientDbRepository;
import org.segrada.service.repository.orientdb.factory.OrientDbRepositoryFactory;
import org.segrada.util.Sluggify;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Copyright 2016 Maximilian Kalus [segrada@auxnet.de]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * User Group Repository implementation
 */
public class OrientDbUserGroupRepository extends AbstractSegradaOrientDbRepository<IUserGroup> implements UserGroupRepository {
	private static final Logger logger = LoggerFactory.getLogger(OrientDbUserGroupRepository.class);

	/**
	 * Constructor
	 */
	@Inject
	public OrientDbUserGroupRepository(OrientDbRepositoryFactory repositoryFactory) {
		super(repositoryFactory);
	}

	@Override
	public IUserGroup convertToEntity(ODocument document) {
		return convertToUserGroup(document);
	}

	@Override
	public ODocument convertToDocument(IUserGroup entity) {
		ODocument document = createOrLoadDocument(entity);

		// populate with data
		document.field("title", entity.getTitle())
				.field("titleasc", Sluggify.sluggify(entity.getTitle()))
				.field("active", entity.getActive())
				.field("roles", entity.getRoles());

		// populate with data
		populateODocumentWithCreatedModified(document, entity);

		return document;
	}

	@Override
	public String getModelClassName() {
		return "UserGroup";
	}

	@Override
	protected String getDefaultOrder(boolean addOrderBy) {
		return (addOrderBy?" ORDER BY":"").concat(" titleasc");
	}
}