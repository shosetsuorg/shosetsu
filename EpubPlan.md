# Plan for epubs
This is the shosetsu plan to handle ePubs

## Getting

### Extension

### Importing

#### User steps
The following are the steps that the user will have to take to import an ePub

##### Importing from files
- User goes to "Browse"
- User then goes to the top right, and selects import
- File manager will open for selecting the file
- After complete, will take the filepath and send it a ImporterWorker that will move/copy the file into the applications local directory
- Prompt user to where the new file is located (the local directory)
- EPub/PDFReaderWorker will being reading the file, will notify user when done

##### Local directory
- User goes to "Browse"
- User clicks "Local source"
- User will be sent to an view showing the local files
- User can select the listing, and import the file
- EPub/PDFReaderWorker will being reading the file, will notify user when done

After the above, the user can go to their library and read the file just like any other novel

## Parsing
An ePub has to be parsed properly before it can be used by Shosetsu

### From a glance

= D/extracted epub
== F/mimetype
== D/META-INF
=== F/container.xml

F/mimetype seems to contain what the extracted content is
META-INF/container.xml


 
## Viewing