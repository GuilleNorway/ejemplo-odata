package com.cairone.odataexample.edm.resources;

import java.util.Date;

import com.cairone.odataexample.EntityServiceRegistar;
import com.cairone.odataexample.entities.UsuarioEntity;
import com.sdl.odata.api.edm.annotations.EdmEntity;
import com.sdl.odata.api.edm.annotations.EdmEntitySet;
import com.sdl.odata.api.edm.annotations.EdmProperty;

@EdmEntity(
		name = "Usuario", 
		key = { "idTipoDoc", "nroDocumento"},
		namespace = EntityServiceRegistar.NAME_SPACE,
		containerName = EntityServiceRegistar.CONTAINER_NAME)
@EdmEntitySet("Usuarios")
public class UsuarioEdm {
	
	@EdmProperty(nullable=false)
	private Integer idTipoDoc = null;

	@EdmProperty(nullable=false, maxLength = 100)
	private String nroDocumento = null;

	@EdmProperty(nullable=false, maxLength = 200)
	private String nombreUsuario = null;

	@EdmProperty(nullable=false, maxLength = 40)
	private String clave = null;

	@EdmProperty(nullable=false)
	private Date fechaAlta = null;

	@EdmProperty(nullable=false)
	private Boolean cuentaVencida = null;

	@EdmProperty(nullable=false)
	private Boolean claveVencida = null;

	@EdmProperty(nullable=false)
	private Boolean cuentaBloqueada = null;

	@EdmProperty(nullable=false)
	private Boolean usuarioHabilitado = null;

	public UsuarioEdm() {
		super();
	}

	public UsuarioEdm(
			Integer idTipoDoc, String nroDocumento, 
			String nombreUsuario, String clave, Date fechaAlta,
			Boolean cuentaVencida, Boolean claveVencida, 
			Boolean cuentaBloqueada, Boolean usuarioHabilitado) {
		super();
		this.idTipoDoc = idTipoDoc;
		this.nroDocumento = nroDocumento;
		this.nombreUsuario = nombreUsuario;
		this.clave = clave;
		this.fechaAlta = fechaAlta;
		this.cuentaVencida = cuentaVencida;
		this.claveVencida = claveVencida;
		this.cuentaBloqueada = cuentaBloqueada;
		this.usuarioHabilitado = usuarioHabilitado;
	}

	public UsuarioEdm(UsuarioEntity usuarioEntity) {
		this (
				usuarioEntity.getPersona().getTipoDocumento().getId(),
				usuarioEntity.getPersona().getNumeroDocumento(),
				usuarioEntity.getNombreUsuario(),
				usuarioEntity.getClave(),
				usuarioEntity.getFechaAlta(),
				usuarioEntity.getCuentaVencida(),
				usuarioEntity.getClaveVencida(),
				usuarioEntity.getCuentaBloqueada(),
				usuarioEntity.getUsuarioHabilitado()
				);
		
	}

	public Integer getIdTipoDoc() {
		return idTipoDoc;
	}

	public void setIdTipoDoc(Integer idTipoDoc) {
		this.idTipoDoc = idTipoDoc;
	}

	public String getNroDocumento() {
		return nroDocumento;
	}

	public void setNroDocumento(String nroDocumento) {
		this.nroDocumento = nroDocumento;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public Date getFechaAlta() {
		return fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}

	public Boolean getCuentaVencida() {
		return cuentaVencida;
	}

	public void setCuentaVencida(Boolean cuentaVencida) {
		this.cuentaVencida = cuentaVencida;
	}

	public Boolean getClaveVencida() {
		return claveVencida;
	}

	public void setClaveVencida(Boolean claveVencida) {
		this.claveVencida = claveVencida;
	}

	public Boolean getCuentaBloqueada() {
		return cuentaBloqueada;
	}

	public void setCuentaBloqueada(Boolean cuentaBloqueada) {
		this.cuentaBloqueada = cuentaBloqueada;
	}

	public Boolean getUsuarioHabilitado() {
		return usuarioHabilitado;
	}

	public void setUsuarioHabilitado(Boolean usuarioHabilitado) {
		this.usuarioHabilitado = usuarioHabilitado;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idTipoDoc == null) ? 0 : idTipoDoc.hashCode());
		result = prime * result + ((nroDocumento == null) ? 0 : nroDocumento.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UsuarioEdm other = (UsuarioEdm) obj;
		if (idTipoDoc == null) {
			if (other.idTipoDoc != null)
				return false;
		} else if (!idTipoDoc.equals(other.idTipoDoc))
			return false;
		if (nroDocumento == null) {
			if (other.nroDocumento != null)
				return false;
		} else if (!nroDocumento.equals(other.nroDocumento))
			return false;
		return true;
	}
	
}
