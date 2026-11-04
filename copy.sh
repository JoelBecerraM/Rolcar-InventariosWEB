#!/bin/bash

for dir in index.jsp view css js img
do
	cp -R src/main/webapp/${dir} /opt/apache-tomcat-9.0.65/webapps/inventarios/
done
