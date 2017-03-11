package com.cairone.odataexample.dtos;

import com.cairone.odataexample.edm.resources.ProvinciaEdm;

public class ProvinciaFrmDto {

	private Integer paisId = null;
	private Integer id = null;
	private String nombre = null;
	
	public ProvinciaFrmDto() {
		super();
	}

	public ProvinciaFrmDto(Integer paisId, Integer id, String nombre) {
		super();
		this.paisId = paisId;
		this.id = id;
		this.nombre = nombre;
	}

	public ProvinciaFrmDto(ProvinciaEdm provinciaEdm) {
		super();
		this.paisId = provinciaEdm.getPaisId();
		this.id = provinciaEdm.getId();
		this.nombre = provinciaEdm.getNombre();
	}

	public Integer getPais() {
		return paisId;
	}

	public void setPaisID(Integer paisId) {
		this.paisId = paisId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
}
