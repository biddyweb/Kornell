#!/bin/bash

echo "Deploy API"
export KNL_API_WAR="../kornell-api/target/kornell-api.war" 
export EB_ENVIRONMENT="eduvem-test-prod-web"
node deploy-api.js

echo "Deploy GWT"
export KNL_BUCKET="test.eduvem.com"
export SRC_DIR="../kornell-gwt/target/kornell-gwt-1.0-SNAPSHOT/"
./deploy-gwt.sh
