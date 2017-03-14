package com.cairone.odataexample.datasources;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;

import com.cairone.odataexample.dtos.ProvinciaFrmDto;
import com.cairone.odataexample.dtos.validators.ProvinciaFrmDtoValidator;
import com.cairone.odataexample.edm.resources.ProvinciaEdm;
import com.cairone.odataexample.entities.PaisEntity;
import com.cairone.odataexample.entities.ProvinciaEntity;
import com.cairone.odataexample.repositories.PaisRepository;
import com.cairone.odataexample.repositories.ProvinciaRepository;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.ODataSystemException;
import com.sdl.odata.api.edm.model.EntityDataModel;
import com.sdl.odata.api.parser.ODataUri;
import com.sdl.odata.api.parser.ODataUriUtil;
import com.sdl.odata.api.parser.TargetType;
import com.sdl.odata.api.processor.datasource.DataSource;
import com.sdl.odata.api.processor.datasource.DataSourceProvider;
import com.sdl.odata.api.processor.datasource.ODataDataSourceException;
import com.sdl.odata.api.processor.datasource.TransactionalDataSource;
import com.sdl.odata.api.processor.link.ODataLink;
import com.sdl.odata.api.processor.query.QueryOperation;
import com.sdl.odata.api.processor.query.strategy.QueryOperationStrategy;
import com.sdl.odata.api.service.ODataRequestContext;

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

			/** Que hace esto? **/
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

		}

		return null;
	}

	@Override
	public void delete(ODataUri uri, EntityDataModel entityDataModel) throws ODataException {
		// TODO Auto-generated method stub

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
		// TODO Auto-generated method stub
		return null;
	}

}
