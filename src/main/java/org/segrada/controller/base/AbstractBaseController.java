package org.segrada.controller.base;

import com.sun.jersey.api.view.Viewable;
import org.segrada.model.prototype.SegradaEntity;
import org.segrada.service.base.AbstractRepositoryService;
import org.segrada.service.repository.prototype.CRUDRepository;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright 2015 Maximilian Kalus [segrada@auxnet.de]
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
 * Abstract base controller
 */
abstract public class AbstractBaseController {
	/**
	 * handle whole update method of an entity
	 * @param backUrl either "add" or "edit"
	 * @param entity to save
	 * @param service saving entity
	 * @param <T> type of entity
	 * @param <E> type of repository
	 * @return response, either form view or redirect
	 */
	protected <T extends SegradaEntity, E extends CRUDRepository<T>> Response handleUpdate(String backUrl, T entity, AbstractRepositoryService<T, E> service) {
		// validate source
		Map<String, String> errors = validate(entity);

		// no validation errors: save entity
		if (errors.isEmpty()) {
			if (service.save(entity)) {
				//OK - redirect to show
				try {
					return Response.seeOther(new URI(getBasePath() + entity.getUid())).build();
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
			} else ;//TODO show error message?
		}

		// create model map
		Map<String, Object> model = new HashMap<>();

		model.put("entity", entity);
		model.put("errors", errors);

		// fallback
		if (backUrl == null) backUrl = "add";

		// return viewable
		return Response.ok(new Viewable(getBasePath() + backUrl, model)).build();
	}

	/**
	 * validate bean
	 * @param entity to validate
	 * @param <T> type of bean
	 * @return map containing property and one error message
	 */
	protected <T> Map<String, String> validate(T entity) {
		Map<String, String> errors = new HashMap<>();

		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		Validator validator = factory.getValidator();
		for (ConstraintViolation<T> error : validator.validate(entity)) {
			errors.put(error.getPropertyPath().toString(), error.getMessage());
		}

		return errors;
	}

	/**
	 * @return controller's base path, e.g. "/source/"
	 */
	abstract protected String getBasePath();
}
