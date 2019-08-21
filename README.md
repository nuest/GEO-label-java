GEO-label-java
==============

A Java implementation of the GEO label server API.

[![Build Status](https://travis-ci.org/52North/GEO-label-java.png?branch=master)](https://travis-ci.org/52North/GEO-label-java)

## The GEO label


The GEO label is an interactive visual summary of geospatial metadata to facilitate search and discovery use cases. Ii is a graphical representation which comprises 8 informational facets:
producer profile, lineage information, producer comments, compliance with standards, quality information, user feedback, expert reviews, and citations information. Each informational facet can have an availability states: available, not available, available only at a higher level. 
This information is encoded with symbols and colours for easy comprehensibility and repeated recognition.

Currently, the GEO label supports several metadata standards such as ISO19115 and FGDC (producer metadata documents), SensorML, as well as GeoViQua Quality Information Model (producer and user quality information).

More information: http://geolabel.info/ and http://geolabel.net/

### The GEO label API

The GEO label server is basically an API to generate a SVG document based on standardised metadata documents (e.g. XML) that can (a) be POSTed to the server, or (b) a GET Url can be build to let the server request the document and provide a permanent link to the label.

The Java implementation currently only supports the endpoint ``geolabel`` to generate labels.

The API further defines endpoints to retrieve seperate facets as well as "drilldown" informations where the service provides a visual and textual perspective (HTML) into specific aspects of a given metadata document.

Full API documentation: http://www.geolabel.net/api.html

### Transformation Rules/Mappings

The GEO label basically uses a set of XPath expressions to determine if the required information is available so that a facet can be marked as available. For interoperability reasons (using the same configuration file as the PHP implementation, and PHP only supports XPath 1.0) this Java implementation uses ``XPath 1.0``.

The transformation rules are be downloaded on startup from http://geoviqua.github.io/geolabel/ and the service has a local fallback if the online file cannot be found.

For future work, using XPath 2.0 using Saxon would be an advantage because wildcards can be used for namespaces. A commit that still contains a Saxon-based implementation is https://github.com/52North/GEO-label-java/commit/def538a4966201970a397963328664c70b2de788

   
## Modules

This project consists of a service implementation generating GEO label 
SVG representations from supplied metadata, a client API to access such a service as well as a client JSF 
component to directly render GEO labels into JSF 1/2 and JSP based webpages.


### Client

Client API to access a GEO label service. Uses a builder pattern to allow various combinations of metadata 
and feedback document inputs supporting streams, URL references and XML Documents.

```java
InputStream geoLabel = GeoLabelClientV1.createGeoLabelRequest()
							.setMetadataDocument(metadataStream)
							.setFeedbackDocument(feedbackStream)
							.getSVG();
```

### JSF

Simple JSF component rendering GEO labels from actual feedback/metadata documents and/or URL references. Supports asynchronous 
loading using Partial Page Rendering.


```html
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:h="http://java.sun.com/jsf/html"
	xmlns:glb="http://www.geolabel.net">
	<h:head/>
	<h:body>
		<glb:geolabel metadataUrl="#{metadataUrl}" feedbackUrl="#{feedbackUrl}"	async="true" size="100" />
	</h:body>
</html>		
```

For more details see `jsf/Readme.md`

### Server

An demo instance is deployed at http://geoviqua.dev.52north.org/glbservice

For more details see `server/Readme.md`

### Commons

Resources required by server and client modules.

For more details see `commons/Readme.md`

## Contact

Daniel Nüst (d.nuest@52north.org)

Support: Metadatada management community mailing list, see http://metadata.forum.52north.org/

## License

The GEO label Java project is licensed under The Apache Software License, Version 2.0.

For licenses of used libraries see NOTICE file.
