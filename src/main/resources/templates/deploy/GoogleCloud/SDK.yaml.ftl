- name: Upload SDK to Google Cloud Storage
  run: |
    gsutil cp ./sdk.zip gs://my-bucket-name/sdk.zip
