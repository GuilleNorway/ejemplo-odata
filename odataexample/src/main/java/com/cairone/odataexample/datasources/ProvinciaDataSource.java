package com.cairone.odataexample.datasources;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;

import com.cairone.odataexample.dtos.ProvinciaFrmDto;
import com.cairone.odataexample.dtos.validators.ProvinciaFrmDtoValidator;
import com.cairone.odataexample.edm.resources.ProvinciaEdm;
import com.cairone.odataexample.entities.PaisEntity;
import com.cairone.odataexample.entities.ProvinciaEntity;
import com.cairone.odataexample.entities.ProvinciaPKEntity;
import com.cairone.odataexample.repositories.PaisRepository;
import com.cairone.odataexample.repositories.ProvinciaRepository;
import com.cairone.odataexample.strategyBuilders.ProvinciasStrategyBuilder;
import com.mysema.query.types.expr.BooleanExpression;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.edm.util.EdmUtil;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.parser.TargetType;
import com.sdl.odata.api.processor.datasource.DataSource;
import com.sdl.odata.api.processor.datasource.DataSourceProvider;
import com.sdl.odata.api.processor.datasource.ODataDataSourceException;
import com.sdl.odata.api.processor.datasource.TransactionalDataSource;
import com.sdl.odata.api.processor.link.ODataLink;
import com.sdl.odata.api.processor.query.QueryOperation;
import com.sdl.odata.api.processor.query.QueryResult;
import com.sdl.odata.api.processor.query.strategy.QueryOperationStrategy;
import com.sdl.odata.api.service.ODataRequestContext;

import scala.Option;

@Component
public class ProvinciaDataSource implements DataSourceProvider, DataSource {

	@Autowired
	private ProvinciaRepository provinciaRepository = null;
	@Autowired
	private PaisRepository paisRepository = null;
	@Autowired
	private ProvinciaFrmDtoValidator provinciaFrmDtoValidator = null;

	/** Help: transforma info en texto facil de leer **/
	@Autowired
	private MessageSource messageSource = null;

	@Override
	public Object create(ODataUri uri, Object entity, EntityDataModel entityDataModel) throws ODataException {

		if (entity instanceof ProvinciaEdm) {

			ProvinciaEdm provinciaEdm = (ProvinciaEdm) entity;
			ProvinciaFrmDto provinciaFrmDto = new ProvinciaFrmDto(provinciaEdm);

			/** Help: DataBinder funciona como un filtro para los datos **/
			// Valido formato de los datos de provincia
			DataBinder binder = new DataBinder(provinciaFrmDto);

			binder.setValidator(provinciaFrmDtoValidator);
			binder.validate();

			BindingResult bindingResult = binder.getBindingResult();

			// Verifico si hubo errores en esos datos
			if (bindingResult.hasFieldErrors()) {
				for (Object object : bindingResult.getAllErrors()) {
					if (object instanceof FieldError) {
						FieldError fieldError = (FieldError) object;
						String message = messageSource.getMessage(fieldError, null);
						throw new ODataDataSourceException(
								String.format("HAY DATOS INVALIDOS EN LA SOLICITUB ENVIADA. %s", message));
					}
				}
			}

			// Obtengo el PaisEntity mediante el idPais
			PaisEntity paisEntity = paisRepository.findOne(provinciaFrmDto.getId());

			if (paisEntity == null) {
				throw new ODataDataSourceException(String.format("NO SE ENCONTRO. %s", provinciaFrmDto.getId()));
			}

			ProvinciaEntity provinciaEntity = new ProvinciaEntity();
			provinciaEntity.setId(provinciaFrmDto.getId());
			provinciaEntity.setNombre(provinciaFrmDto.getNombre());
			provinciaEntity.setPais(paisEntity);

			provinciaRepository.save(provinciaEntity);

			return new ProvinciaEdm(provinciaEntity);
		}

		throw new ODataDataSourceException("LOS DATOS NO CORRESPONDEN A LA ENTIDAD PROVINCIA");
	}

	@Override
	public Object update(ODataUri uri, Object entity, EntityDataModel entityDataModel) throws ODataException {

		if (entity instanceof ProvinciaEdm) {

			/** El uri trae los datos de las claves **/
			/** entity trae el body del JSON, no necesariamente las claves **/
			Map<String, Object> oDataUriKeyValues = ODataUriUtil
					.asJavaMap(ODataUriUtil.getEntityKeyMap(uri, entityDataModel));

			ProvinciaEdm provinciaEdm = (ProvinciaEdm) entity;
			ProvinciaFrmDto provinciaFrmDto = new ProvinciaFrmDto(provinciaEdm);

			// Valido formato de los datos de provincia
			DataBinder binder = new DataBinder(provinciaFrmDto);

			binder.setValidator(provinciaFrmDtoValidator);
			binder.validate();

			BindingResult bindingResult = binder.getBindingResult();

			if (bindingResult.hasFieldErrors()) {

				for (Object object : bindingResult.getAllErrors()) {
					if (object instanceof FieldError) {
						FieldError fieldError = (FieldError) object;
						String message = messageSource.getMessage(fieldError, null);
						throw new ODataDataSourceException(
								String.format("HAY DATOS INVALIDOS EN LA SOLICITUD ENVIADA. %s", message));
					}
				}
			}

			Integer paisId = Integer.valueOf(oDataUriKeyValues.get("paisId").toString());
			Integer provinciaId = Integer.valueOf(oDataUriKeyValues.get("id").toString());

			PaisEntity paisEntity = paisRepository.findOne(paisId);

			if (paisEntity == null) {
				throw new ODataDataSourceException(
						String.format("NO SE ENCUENTRA UN PAIS CON ID %s", paisEntity.getId()));
			}

			ProvinciaEntity provinciaEntity = provinciaRepository
					.findOne(new ProvinciaPKEntity(provinciaId, paisEntity));

			if (provinciaEntity == null) {
				throw new ODataDataSourceException(
						String.format("NO SE ENCUENTRA UNA PROVINCIA CON ID PROVINCIA %s E ID PAIS %s",
								provinciaEntity.getId(), paisEntity.getId()));
			}

			provinciaEntity.setNombre(provinciaFrmDto.getNombre());

			provinciaRepository.save(provinciaEntity);

			return new ProvinciaEdm(provinciaEntity);
		}

		throw new ODataDataSourceException("LOS DATOS NO CORRESPONDEN A LA ENTIDAD PROVINCIA");

	}

	@Override
	public void delete(ODataUri uri, EntityDataModel entityDataModel) throws ODataException {

		Option<Object> entity = ODataUriUtil.extractEntityWithKeys(uri, entityDataModel);

		if (entity.isDefined()) {

			ProvinciaEdm provinciaEdm = (ProvinciaEdm) entity.get();
			ProvinciaEntity provinciaEntity = provinciaRepository
					.findOne(new ProvinciaPKEntity(provinciaEdm.getId(), provinciaEdm.getPaisId()));

			if (provinciaEntity == null) {
				throw new ODataDataSourceException(
						String.format("NO SE ENCUENTRA UNA PROVINCIA CON ID PROVINCIA %s E ID PAIS %s",
								provinciaEntity.getId(), provinciaEntity.getPais().getId()));
			}

			provinciaRepository.delete(provinciaEntity);

			return;
		}

		throw new ODataDataSourceException("LOS DATOS NO CORRESPONDEN A LA ENTIDAD PROVINCIA");

	}

	@Override
	public void createLink(ODataUri uri, ODataLink link, EntityDataModel entityDataModel) throws ODataException {
		// NO HACER NADA
	}

	@Override
	public void deleteLink(ODataUri uri, ODataLink link, EntityDataModel entityDataModel) throws ODataException {
		// NO HACER NADA
	}

	@Override
	public TransactionalDataSource startTransaction() {
		throw new ODataSystemException("No support for transactions");
	}

	@Override
	public boolean isSuitableFor(ODataRequestContext requestContext, String entityType)
			throws ODataDataSourceException {
		return requestContext.getEntityDataModel().getType(entityType).getJavaType().equals(ProvinciaEdm.class);
	}

	@Override
	public DataSource getDataSource(ODataRequestContext requestContext) {
		return this;
	}

	@Override
	public QueryOperationStrategy getStrategy(ODataRequestContext requestContext, QueryOperation operation,
			TargetType expectedODataEntityType) throws ODataException {
		
		ProvinciasStrategyBuilder builder = new ProvinciasStrategyBuilder();
		BooleanExpression expression = builder.buildCriteria(operation, requestContext); //como se definen los parametros
		List<Sort.Order> orderByList = builder.getOrderByList();

		int limit = builder.getLimit();
		int skip = builder.getSkip();
		List<String> propertyNames = builder.getPropertyNames();

		Page<ProvinciaEntity> pageProvinciaEntity = orderByList == null || orderByList.size() == 0
				? provinciaRepository.findAll(expression, new PageRequest(0, limit))
				: provinciaRepository.findAll(expression, new PageRequest(0, limit, new Sort(orderByList)));

		List<ProvinciaEntity> provinciaEntities = pageProvinciaEntity.getContent();

		return () -> {

			List<ProvinciaEdm> filtered = provinciaEntities.stream().map(entity -> {
				return new ProvinciaEdm(entity);
			}).collect(Collectors.toList());

			long count = 0;

			if (builder.isCount() || builder.includeCount()) {
				count = filtered.size();

				if (builder.isCount()) {
					return QueryResult.from(count);
				}
			}

			if (skip != 0 || limit != Integer.MAX_VALUE) {
				filtered = filtered.stream().skip(skip).collect(Collectors.toList());
			}

			if (propertyNames != null && !propertyNames.isEmpty()) {
				try {
					return QueryResult.from(EdmUtil.getEdmPropertyValue(filtered.get(0), propertyNames.get(0)));
				} catch (IllegalAccessException e) {
					return QueryResult.from(Collections.emptyList());
				}
			}

			QueryResult result = QueryResult.from(filtered);
			if (builder.includeCount()) {
				result = result.withCount(count);
			}
			return result;
		};
	}

}
