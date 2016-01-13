# -*- coding: utf-8 -*-

import urllib2
from bs4 import BeautifulSoup
import re
import cgi
from xml.etree.ElementTree import Element, SubElement, tostring, parse

listaNoticias = []
DATA_FILENAMEXML = 'XML/Noticias.xml'

class Noticia(object):
	def __init__(self, titulo, resumo, data, texto, idNoticia):
		self.titulo = titulo
		self.resumo = resumo
		self.data = data
		self.texto = texto
		self.idNoticia = idNoticia

def multEnters(linha):
	boo = False
	linhar = linha

	while not boo:
		if " --- --- " in linhar:
			linhar = linhar.replace(" --- --- ", " --- ")
		else:
			boo = True

	return linhar.replace("---", "\n")

def removeMultSpace(dado):

	textToSearch = "\xc2\xa0"
	
	dado = dado.replace(textToSearch, " ")

	if len(dado) > 5:
		dado = ' '.join(dado.split())
		dado = multEnters(dado)

	return dado

def extractTags(div):

	divh2 = div.find('h2',{'class':'tituloPaginaInterna'})
	divh2.extract()

	tituloNoticia = div.find('h3',{'class':'tituloNoticia'})
	tituloNoticia.extract()

	resumo = div.find('em',{'class':'resumo'})
	resumo.extract()

	dataNoticia = div.find('em',{'class':'dataNoticia'})
	dataNoticia.extract()

def crawlerNews(idInicial, idFinal):

	while idInicial <= idFinal:
		dados = ["", "", "", "", ""]

		urlEvento = "http://www2.cin.ufpe.br/site/lerNoticia.php?s=1&c=21&id=" + str(idInicial)
		page = urllib2.urlopen(urlEvento)
		pageString = page.read()

		soup = BeautifulSoup(pageString)

		div = soup.find('div',{'class':'conteudo'})

		tituloNoticia = div.find('h3',{'class':'tituloNoticia'})
		resumo = div.find('em',{'class':'resumo'})
		dataNoticia = div.find('em',{'class':'dataNoticia'})

		extractTags(soup)

		tituloNoticia = tituloNoticia.string
		resumo = resumo.string
		dataNoticia = dataNoticia.string
		texto = div.text.replace("\n", " --- ")
		texto = re.sub('<!--.*-->', '', texto)

		if tituloNoticia:
			tituloNoticia = cgi.escape(tituloNoticia)
			dados[0] = tituloNoticia.encode('utf-8')
			dados[0] = removeMultSpace(dados[0])

			if resumo:
				resumo = cgi.escape(resumo)
				dados[1] = resumo.encode('utf-8')
				dados[1] = removeMultSpace(dados[1])

			dataNoticia = cgi.escape(dataNoticia)
			dados[2] = dataNoticia.encode('utf-8')
			dados[2] = removeMultSpace(dados[2])
			
			texto = cgi.escape(texto)
			dados[3] += texto.encode('utf-8')
			dados[3] = removeMultSpace(dados[3])

			dados[4] = str(idInicial)

			noticia = Noticia(dados[0], dados[1], dados[2], dados[3], dados[4])
			listaNoticias.append(noticia)

		print idInicial
		idInicial += 1

def createXMLNoticias():

	noticias = Element('Noticias')

	for itemNoticia in listaNoticias:

		noticia = SubElement(noticias, 'Noticia')

		idNoticia = SubElement(noticia, 'IDNoticia')
		idNoticia.text = itemNoticia.idNoticia

		titulo = SubElement(noticia, 'Titulo')
		titulo.text = itemNoticia.titulo.decode('UTF-8')

		resumo = SubElement(noticia, 'Resumo')
		resumo.text = itemNoticia.resumo.decode('UTF-8')

		data = SubElement(noticia, 'Data')
		data.text = itemNoticia.data.decode('UTF-8')

		texto = SubElement(noticia, 'Texto')
		texto.text = itemNoticia.texto.decode('UTF-8')

	outputXML = tostring(noticias, encoding='utf8', method='xml')

	with open(DATA_FILENAMEXML, "w") as arquivo:
		arquivo.write(outputXML)

def main():
	print("Rodando")

	idInicial = 1
	idFinal = 1270

	crawlerNews(idInicial, idFinal)
	print len(listaNoticias)
	createXMLNoticias()

if __name__ == '__main__':
    main()