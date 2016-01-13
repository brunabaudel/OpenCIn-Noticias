package br.ufpe.cin.main;

import java.io.IOException;

import org.jdom2.JDOMException;

import br.ufpe.cin.escritor.RDFNoticia;
import br.ufpe.cin.leitor.LeitorNoticias;
import br.ufpe.cin.matcher.ComplementarNoticias;

public class Main {

	public static void main(String[] args) {
		
		try {
			
			//Inclui os ids dos professores nas notícias
			ComplementarNoticias matcher = new ComplementarNoticias("Inputs/professoresRDF.xml");
			matcher.escreverXML("Inputs/Noticias.xml", "Inputs/noticiasProfessores.xml");
			
			//Cria o RDF de Notícias
			RDFNoticia.transformarRDFNoticia(LeitorNoticias.lerXMLNoticia("Inputs/noticiasProfessores.xml"), "Outputs/noticiasRDF.xml");
		
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
