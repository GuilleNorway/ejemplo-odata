package com.cairone.odataexample.dtos;

import com.cairone.odataexample.edm.resources.SectorEdm;

public class SectorFrmDto {
	
	private Integer id = null;
	private String nombre;

	public SectorFrmDto() {
	}

	public SectorFrmDto(Integer id, String nombre, Integer prefijo) {
		super();
		this.id = id;
		this.nombre = nombre;
	}

	public SectorFrmDto(SectorEdm sectorEdm) {
		this.id = sectorEdm.getId();
		this.nombre = sectorEdm.getNombre() == null || sectorEdm.getNombre().trim().isEmpty() ? null : sectorEdm.getNombre().trim().toUpperCase();
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
