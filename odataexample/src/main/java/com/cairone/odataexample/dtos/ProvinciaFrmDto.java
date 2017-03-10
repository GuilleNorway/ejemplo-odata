package com.cairone.odataexample.dtos;

import com.cairone.odataexample.edm.resources.ProvinciaEdm;

public class ProvinciaFrmDto {

	private Integer paisID = null;
	private Integer id = null;
	private String nombre = null;
	
	public ProvinciaFrmDto() {
		super();
	}

	public ProvinciaFrmDto(Integer paisID, Integer id, String nombre) {
		super();
		this.paisID = paisID;
		this.id = id;
		this.nombre = nombre;
	}

	public ProvinciaFrmDto(ProvinciaEdm provinciaEdm) {
		super();
		this.paisID = provinciaEdm.getPaisId();
		this.id = provinciaEdm.getId();
		this.nombre = provinciaEdm.getNombre();
	}

	public Integer getPaisID() {
		return paisID;
	}

	public void setPaisID(Integer paisID) {
		this.paisID = paisID;
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
