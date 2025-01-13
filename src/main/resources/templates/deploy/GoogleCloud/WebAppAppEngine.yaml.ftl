- name: Deploy Web App to Google App Engine
  uses: google-github-actions/deploy-appengine@v1
  with:
    deliverables: ./build
