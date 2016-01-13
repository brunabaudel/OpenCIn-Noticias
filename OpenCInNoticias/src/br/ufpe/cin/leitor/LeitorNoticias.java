package br.ufpe.cin.leitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import br.ufpe.cin.entidades.Noticia;

public class LeitorNoticias {
	
	public static List<Noticia> lerXMLNoticia(String pathIn) {
		
		File arquivo = new File(pathIn);
		
		SAXBuilder builder = new SAXBuilder();
		List<Noticia> listaNoticias = new ArrayList<Noticia>();
		  
		  try {
	 
			Document document = (Document) builder.build(arquivo);
			Element rootNode = document.getRootElement();
			List<Element> list = rootNode.getChildren("Noticia");
	 
			for (int i = 0; i < list.size(); i++) {
	 
			   Element node = (Element) list.get(i);
			   Noticia grad = new Noticia();
			   grad.setIdNoticia(node.getChildText("IDNoticia"));
			   grad.setTitulo(node.getChildText("Titulo"));
			   grad.setTexto(node.getChildText("Texto"));
			   grad.setData(node.getChildText("Data"));
			   grad.setSubtitulo(node.getChildText("Resumo"));
			   grad.setCita(node.getChildText("Cita"));
			   
			   listaNoticias.add(grad);
	 
			}
	 
		  } catch (IOException io) {
			System.out.println(io.getMessage());
		  } catch (JDOMException jdomex) {
			System.out.println(jdomex.getMessage());
		  }
		  
		  return listaNoticias;
	}
}
