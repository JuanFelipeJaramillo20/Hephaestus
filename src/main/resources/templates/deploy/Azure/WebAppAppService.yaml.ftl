- name: Deploy Web App to Azure App Service
  uses: azure/webapps-deploy@v2
  with:
    app-name: my-web-app
    package: path/to/deployment/package.zip
