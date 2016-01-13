package br.ufpe.cin.matcher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import br.ufpe.cin.entidades.Docente;

public class ComplementarNoticias {
	
	public static HashMap<String, Docente> hashDocentes;

	public ComplementarNoticias(String path){
		ComplementarNoticias.hashDocentes = ComplementarNoticias.lerXMLListaProfessores(new File(path));
	}
	
	public static HashMap<String, Docente> lerXMLListaProfessores(File arquivo) {
		
		SAXBuilder builder = new SAXBuilder();
		HashMap<String, Docente> hashDocentes = new HashMap<String, Docente>();
		  
		  try {
	 
			Document document = (Document) builder.build(arquivo);
			Element rootNode = document.getRootElement();
			List<Element> list = rootNode.getChildren("Description", rootNode.getNamespace("rdf"));
			
			for (int i = 0; i < list.size(); i++) {
	 
			   Element node = (Element) list.get(i);
			   Docente grad = new Docente();
			   grad.setIdDocente(node.getAttributeValue("about", node.getNamespace("rdf")).replace("http://www.cin.ufpe.br/opencin/academic/", ""));
			   grad.setNome(node.getChildText("name", rootNode.getNamespace("cin")));

			   String nome = grad.getNome();
			   nome = Normalizer.normalize(nome, Normalizer.Form.NFD);
			   nome = nome.replaceAll("[^\\p{ASCII}]", "");
			   nome = nome.replaceAll("([pP]rof[aª]?[.:]?|\\(.*\\))", "").toUpperCase();
			   
			   hashDocentes.put(nome, grad);
			   
			}
	 
		  } catch (IOException io) {
			System.out.println(io.getMessage());
		  } catch (JDOMException jdomex) {
			System.out.println(jdomex.getMessage());
		  }
		  
		  return hashDocentes;
	}
	
	public static String getIdDocente(String texto) {
		
		String idDocente = "";
		
		texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
		texto = texto.replaceAll("[^\\p{ASCII}]", "");
		texto = texto.toUpperCase();
		
		String[] nomes = null;
		String parteTexto = texto;
		String nomeAchado = "";
		int ind = -1;
		
		if(texto.contains("PROFESSOR")) {
			HashMap<String, List<Integer>> hashIndices = getIndices(texto);
		
			for (String nomeDocente : hashIndices.keySet()) {
				
				nomes = nomeDocente.split(" ");
				
				for (Integer indice : hashIndices.get(nomeDocente)) {
					//System.out.println("parte 1 " +parteTexto);
					System.out.println("indice 1 " +indice);
					parteTexto = parteTexto.substring(indice).trim();
					//System.out.println("parte 2 " +parteTexto);
					
					ind = parteTexto.indexOf(" ");
					
					if(ind > -1){
						nomeAchado = parteTexto.substring(0, ind).replace(",", "");
						nomeAchado = nomeAchado.replace(".", "");
					}
					System.out.println("nome acha " +nomeAchado);
					
					if(nomeAchado.equals("DE") || nomeAchado.equals("DA") || nomeAchado.equals("DO")) {
						parteTexto = parteTexto.substring(nomeAchado.length()).trim();
						nomeAchado = parteTexto.substring(0, parteTexto.indexOf(" ")).replace(",", "");
						nomeAchado = nomeAchado.replace(".", "");
						
						System.out.println("nome acha de " +nomeAchado);
					}
					
					for (int i = 1; i < nomes.length; i++) {
						
						if(nomeAchado.equals(nomes[i])){
							System.out.println("Encontrou " + nomeDocente);
							idDocente = "http://www.cin.ufpe.br/opencin/academic/" + hashDocentes.get(nomeDocente).getIdDocente();
							//break;
						}
					}
					
					parteTexto = texto;
					
				}
				
			}
		}
		
		return idDocente;
		
	}
	
	public static HashMap<String, List<Integer>> getIndices(String texto) {
		HashMap<String, List<Integer>> hashIndices = new HashMap<String, List<Integer>>();
		List<Integer> listaIndices = new ArrayList<Integer>();
		
		texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
		texto = texto.replaceAll("[^\\p{ASCII}]", "");
		texto = texto.toUpperCase();
		
		String[] nomes = null;
		String primeiroNome = "";
		String parteTexto = texto;
		Integer indice = -1;
		Integer indiceAnterior = 0;
		
		for (String nomeDocente : hashDocentes.keySet()) {
			
			nomes = nomeDocente.split(" ");
			primeiroNome = " " + nomes[0]+ " ";
			
			while(parteTexto.contains(primeiroNome)){
				
				indice = parteTexto.indexOf(primeiroNome)+primeiroNome.length();
				
				indiceAnterior += indice;
				
				if(indice > -1) {
					listaIndices.add(indiceAnterior);
				}
				
				System.out.println(primeiroNome + "   "+ indiceAnterior);
				parteTexto = parteTexto.substring(indice).trim();
			}
			hashIndices.put(nomeDocente, listaIndices);
			listaIndices = new ArrayList<Integer>();
			parteTexto = texto;
			indiceAnterior = 0;
		}
		
		
		return hashIndices;
	}
	
	//---------- Criar XML ----------------
	
	public static void obterXML(Document document){
		  
		Element rootNode = document.getRootElement();
		List<Element> list = rootNode.getChildren("Noticia");
		
		for (int i = 0; i < list.size(); i++) {
 
		   Element node = (Element) list.get(i);
		   
		   String texto = node.getChildText("Texto");
		   node.addContent(new Element("Cita").setText(ComplementarNoticias.getIdDocente(texto)));
		   
		}
	}
	
	public void escreverXML(String pathIn, String pathOut) throws JDOMException, IOException {
		  
		File diretorio = new File(pathIn);
		
		SAXBuilder builder = new SAXBuilder();
		Document document = (Document) builder.build(diretorio);
		
		ComplementarNoticias.obterXML(document);
		
		XMLOutputter xmlOutput = new XMLOutputter();
 
		xmlOutput.setFormat(Format.getPrettyFormat());
		xmlOutput.output(document, new FileWriter(pathOut));
		
	}

}
