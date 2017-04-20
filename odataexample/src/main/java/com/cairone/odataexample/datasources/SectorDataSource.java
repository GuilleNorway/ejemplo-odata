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

import com.cairone.odataexample.dtos.SectorFrmDto;
import com.cairone.odataexample.dtos.validators.SectorFrmDtoValidator;
import com.cairone.odataexample.edm.resources.SectorEdm;
import com.cairone.odataexample.entities.SectorEntity;
import com.cairone.odataexample.repositories.SectorRepository;
import com.cairone.odataexample.strategyBuilders.SectoresStrategyBuilder;
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
public class SectorDataSource implements DataSourceProvider, DataSource {

	@Autowired
	private SectorRepository sectorRepository = null;
	@Autowired
	private SectorFrmDtoValidator sectorFrmDtoValidator = null;

	@Autowired
	private MessageSource messageSource = null;

	@Override
	public Object create(ODataUri uri, Object entity, EntityDataModel entityDataModel) throws ODataException {

		if (entity instanceof SectorEdm) {

			SectorEdm sectorEdm = (SectorEdm) entity;
			SectorFrmDto sectorFrmDto = new SectorFrmDto(sectorEdm);

			DataBinder binder = new DataBinder(sectorFrmDto);

			binder.setValidator(sectorFrmDtoValidator);
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
			
			SectorEntity sectorEntity = sectorRepository.findOne(sectorFrmDto.getId());

			if (sectorEntity != null) {
				throw new ODataDataSourceException(
						String.format("CLAVE DUPLICADA"));
			} else {

				sectorEntity = new SectorEntity();

				sectorEntity.setId(sectorFrmDto.getId());
				sectorEntity.setNombre(sectorFrmDto.getNombre());

				sectorRepository.save(sectorEntity);

				return new SectorEdm(sectorEntity);	
			}

		}

		throw new ODataDataSourceException("LOS DATOS NO CORRESPONDEN A LA ENTIDAD SECTOR");
	}

	@Override
	public Object update(ODataUri uri, Object entity, EntityDataModel entityDataModel) throws ODataException {

		if (entity instanceof SectorEdm) {
			
			Map<String, Object> oDataUriKeyValues = ODataUriUtil
					.asJavaMap(ODataUriUtil.getEntityKeyMap(uri, entityDataModel));

			SectorEdm sector = (SectorEdm) entity;

			oDataUriKeyValues.values().forEach(item -> {
				sector.setId(Integer.valueOf(item.toString()));
			});
			

			SectorFrmDto sectorFrmDto = new SectorFrmDto(sector);
			
			//String test_name = "";
			
			//test_name = sector.getNombre();
			
			DataBinder binder = new DataBinder(sectorFrmDto);

			binder.setValidator(sectorFrmDtoValidator);
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

			Integer sectorID = sector.getId();
			SectorEntity sectorEntity = sectorRepository.findOne(sectorID);

			if (sectorEntity == null) {
				throw new ODataDataSourceException(
						String.format("NO SE ENCUENTRA UN SECTOR CON ID %s", sector.getId()));
			}
			
			sectorEntity.setNombre(sectorFrmDto.getNombre());

			sectorRepository.save(sectorEntity);

			return new SectorEdm(sectorEntity);
		}

		throw new ODataDataSourceException("LOS DATOS NO CORRESPONDEN A LA ENTIDAD SECTOR");

	}

	@Override
	public void delete(ODataUri uri, EntityDataModel entityDataModel) throws ODataException {

		Option<Object> entity = ODataUriUtil.extractEntityWithKeys(uri, entityDataModel);

		if (entity.isDefined()) {

			SectorEdm sector = (SectorEdm) entity.get();
			SectorEntity sectorEntity = sectorRepository.findOne(sector.getId());

			if (sectorEntity == null) {
				throw new ODataDataSourceException(
						String.format("NO SE ENCUENTRA UN SECTOR CON ID %s", sector.getId()));
			}

			sectorRepository.delete(sectorEntity);

			return;
		}

		throw new ODataDataSourceException("LOS DATOS NO CORRESPONDEN A LA ENTIDAD SECTOR");
	}

	@Override
	public void createLink(ODataUri arg0, ODataLink arg1, EntityDataModel arg2) throws ODataException {
	}

	@Override
	public void deleteLink(ODataUri arg0, ODataLink arg1, EntityDataModel arg2) throws ODataException {
	}

	@Override
	public TransactionalDataSource startTransaction() {
		throw new ODataSystemException("No support for transactions");
	}

	@Override
	public boolean isSuitableFor(ODataRequestContext requestContext, String entityType)
			throws ODataDataSourceException {
		return requestContext.getEntityDataModel().getType(entityType).getJavaType().equals(SectorEdm.class);
	}

	@Override
	public DataSource getDataSource(ODataRequestContext requestContext) {
		return this;
	}

	@Override
	public QueryOperationStrategy getStrategy(ODataRequestContext requestContext, QueryOperation operation, TargetType expectedODataEntityType) throws ODataException {
		SectoresStrategyBuilder builder = new SectoresStrategyBuilder();
		BooleanExpression expression = builder.buildCriteria(operation, requestContext);
		List<Sort.Order> orderByList = builder.getOrderByList();

		int limit = builder.getLimit();
		int skip = builder.getSkip();
		List<String> propertyNames = builder.getPropertyNames();

		Page<SectorEntity> pageSectorEntity = orderByList == null || orderByList.size() == 0
				? sectorRepository.findAll(expression, new PageRequest(0, limit))
				: sectorRepository.findAll(expression, new PageRequest(0, limit, new Sort(orderByList)));

		List<SectorEntity> sectorEntities = pageSectorEntity.getContent();

		return () -> {

			List<SectorEdm> filtered = sectorEntities.stream().map(entity -> {
				return new SectorEdm(entity);
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
