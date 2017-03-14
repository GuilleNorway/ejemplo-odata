package com.cairone.odataexample;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cairone.odataexample.edm.resources.PaisEdm;
import com.cairone.odataexample.edm.resources.ProvinciaEdm;
import com.sdl.odata.api.ODataException;
import com.sdl.odata.api.edm.registry.ODataEdmRegistry;

@Component
public class EntityServiceRegistar {

	public static final String NAME_SPACE = "com.cairone.odataexample";
	public static final String CONTAINER_NAME = "ODataExample";

	@Autowired
	private ODataEdmRegistry oDataEdmRegistry = null;

	@PostConstruct
	public void registerEntities() throws ODataException {
		oDataEdmRegistry.registerClasses(
				Arrays.asList(
						PaisEdm.class, 
						ProvinciaEdm.class));
	}
}
