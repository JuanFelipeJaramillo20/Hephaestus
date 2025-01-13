- name: Upload SDK to Azure Blob Storage
  run: |
    az storage blob upload --container-name my-container --file ./sdk.zip --name sdk.zip
