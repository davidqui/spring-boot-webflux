package com.springboot.webflux.app.models.documents;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
//import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Document(collection="productos")
public class Producto {
	
	@Id
	private String id;

	@NotEmpty
	private String nombre;

	@NotNull
	private Double precio;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate createAt;

	public Producto() {}

	public Producto(String nombre, Double precio) {
		this.nombre = nombre;
		this.precio = precio;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Double getPrecio() {
		return precio;
	}
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	public LocalDate getCreateAt() {
		return createAt;
	}
	public void setCreateAt(LocalDate  createAt) {
		this.createAt = createAt;
	}
	
	

}
