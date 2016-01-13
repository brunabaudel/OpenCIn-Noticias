package br.ufpe.cin.escritor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import br.ufpe.cin.entidades.Noticia;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

public class RDFNoticia {

	public static void transformarRDFNoticia(List<Noticia> noticias, String pathOut) throws FileNotFoundException{

		String propriedade = "http://www.cin.ufpe.br/opencin/";

		Model model = ModelFactory.createDefaultModel();

		Property titulo = model.createProperty(propriedade, "title");
		Property subtitulo = model.createProperty(propriedade, "subtitle");
		Property data = model.createProperty(propriedade, "date");
		Property texto = model.createProperty(propriedade, "text");

		Property cita = model.createProperty(propriedade, "cite");

		Resource rc = model.createResource("http://www.cin.ufpe.br/opencin/newsitem");
		
		String citap = "";
		
		for (int i = 0; i < noticias.size(); i++) {
			
			citap = noticias.get(i).getCita();
			
			if(!citap.isEmpty()) {
				Resource rcCita = model.createResource(citap);
	
				model.createResource(propriedade +"newsitem/" + noticias.get(i).getIdNoticia())
						.addProperty(cita, rcCita)
						.addProperty(titulo, noticias.get(i).getTitulo())
						.addProperty(subtitulo, noticias.get(i).getSubtitulo())
						.addProperty(data, ResourceFactory.createTypedLiteral(noticias.get(i).getDataFormatada()/*noticias.get(i).getData()*/, XSDDatatype.XSDdate))
						.addProperty(texto, noticias.get(i).getTexto())
						.addProperty(RDF.type, rc);
			}
		}
		
		model.setNsPrefix("cin", propriedade);
		OutputStream output = new FileOutputStream(pathOut);
		model.write(output);
	}

}
