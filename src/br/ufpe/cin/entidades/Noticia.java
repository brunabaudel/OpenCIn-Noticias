package br.ufpe.cin.entidades;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Noticia {
	
	private String idNoticia;
	private String titulo;
	private String subtitulo;
	private String data;
	private String texto;
	private String cita;
	
	public Noticia() {}
	
	public Noticia(String titulo, String subtitulo, String data, String texto, String idNoticia) {
		this.titulo = titulo;
		this.subtitulo = subtitulo;
		this.data = data;
		this.texto = texto;
		this.idNoticia = idNoticia;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public String getSubtitulo() {
		return subtitulo;
	}
	public void setSubtitulo(String subtitulo) {
		this.subtitulo = subtitulo;
	}
	public String getData() {
		return data;
	}
	public String getDataFormatada() {
		
		data = data.replace("de ", "");
		data = data.replace("às ", "");
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM yyyy");
		
		SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = formatter.parse(data);
			System.out.println(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		String date2 = formatter2.format(date);
		
		return date2;
	}
	
	public void setData(String data) {
		this.data = data;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}

	public String getIdNoticia() {
		return idNoticia;
	}

	public void setIdNoticia(String idNoticia) {
		this.idNoticia = idNoticia;
	}

	public String getCita() {
		return cita;
	}

	public void setCita(String cita) {
		this.cita = cita;
	}
	
	
}
